package com.runescape.gameserver.world;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;

import com.runescape.cache.CacheDefinitionManager;
import com.runescape.gameserver.Constants;
import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.event.Event;
import com.runescape.gameserver.event.EventManager;
import com.runescape.gameserver.event.impl.CleanupEvent;
import com.runescape.gameserver.event.impl.UpdateEvent;
import com.runescape.gameserver.io.packet.PacketBuilder;
import com.runescape.gameserver.mysql.MySqlHandler;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.task.impl.SessionLoginTask;
import com.runescape.gameserver.util.EntityList;
import com.runescape.gameserver.util.NameUtils;
import com.runescape.gameserver.world.content.faction.GodFaction;
import com.runescape.gameserver.world.content.god.God;
import com.runescape.gameserver.world.content.god.battles.Battlefield;
import com.runescape.gameserver.world.content.god.battles.impl.*;
import com.runescape.gameserver.world.content.god.impl.*;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.EquipmentDefinition;
import com.runescape.gameserver.world.definitions.ItemDefinition;
import com.runescape.gameserver.world.definitions.WeaponDefinition;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.npc.NPCSpawn;
import com.runescape.gameserver.world.entity.mob.player.LoginResult;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.PlayerDetails;
import com.runescape.gameserver.world.entity.mob.player.mysql.PlayerLoader;
import com.runescape.gameserver.world.entity.mob.player.mysql.PlayerSaver;
import com.runescape.gameserver.world.entity.object.GameObject;
import com.runescape.gameserver.world.region.Region;
import com.runescape.gameserver.world.region.RegionManager;
import com.runescape.gameserver.world.region.Tile;
import com.runescape.util.BlockingExecutorService;

public class World {

	private static final Logger logger = Logger.getLogger(World.class.getName());

	private static final World instance = new World();

	public static World getInstance() {
		return instance;
	}

	private BlockingExecutorService backgroundLoader = new BlockingExecutorService(Executors.newSingleThreadExecutor());

	private GameEngine engine;
	private EventManager eventManager;

	private EntityList<Player> players = new EntityList<Player>(Constants.MAX_PLAYERS);
	private EntityList<NPC> npcs = new EntityList<NPC>(Constants.MAX_NPCS);

	private CacheDefinitionManager objectManager;

	private RegionManager regionManager = new RegionManager();

	private MySqlHandler mysqlHandler;

	private PlayerLoader playerLoader;
	private PlayerSaver playerSaver;
	
	private HashMap<GodFaction, God> gods;
	private Battlefield[] battles;

	public World() {
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				objectManager = new CacheDefinitionManager();
				objectManager.load();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ItemDefinition.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				WeaponDefinition.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				EquipmentDefinition.init();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				NPCSpawn.init();
				
				World.getInstance().initGods();
				World.getInstance().initBattles();
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				Skill.setXPForLevelTable();
				return null;
			}
		});
	}

	public void init(GameEngine engine) throws Throwable {
		if(this.engine != null) {
			throw new IllegalStateException("The world has already been initialised.");
		} else {
			this.engine = engine;
			this.eventManager = new EventManager(engine);
			this.registerGlobalEvents();
			this.mysqlHandler = new MySqlHandler();
			this.playerLoader = new PlayerLoader(this.mysqlHandler);
			this.playerSaver = new PlayerSaver(this.mysqlHandler);
		}
	}
	
	private void initBattles() {
		battles = new Battlefield[] {
			new LumbridgeBattlefield()
		};
		
		for(Battlefield battle : battles) {
			battle.spawnInitialObjects();
			battle.spawnInitialFighters();
		}
	}
	
	private void initGods() {
		this.gods = new HashMap<GodFaction, God>();
		
		God[] gods = new God[] {
			new Saradomin(),
			new Zamorak()
		};
		
		for(God god : gods) {
			this.gods.put(god.getFaction(), god);
		}
	}
	
	public God getGod(GodFaction faction) {
		return gods.get(faction);
	}

	private void registerGlobalEvents() {
		submit(new UpdateEvent());
		submit(new CleanupEvent());
	}

	public void submit(Event event) {
		this.eventManager.submit(event);
	}

	public void submit(Task task) {
		this.engine.pushTask(task);
	}

	public BlockingExecutorService getBackgroundLoader() {
		return backgroundLoader;
	}

	public RegionManager getRegionManager() {
		return regionManager;
	}

	public CacheDefinitionManager getObjectMap() {
		return objectManager;
	}

	public GameEngine getEngine() {
		return engine;
	}

	public void load(final PlayerDetails pd) {
		engine.submitWork(new Runnable() {
			public void run() {
				//LoginResult lr = playerLoader.checkLogin(pd);
				LoginResult lr = new LoginResult(2, new Player(pd));
				int code = lr.getReturnCode();
				if(!NameUtils.isValidName(pd.getName())) {
					code = 11;
				}
				if(code != 2) {
					PacketBuilder bldr = new PacketBuilder();
					bldr.put((byte) code);
					pd.getSession().write(bldr.toPacket()).addListener(new IoFutureListener<IoFuture>() {
						@Override
						public void operationComplete(IoFuture future) {
							future.getSession().close(false);
						}
					});
				} else {
					lr.getPlayer().getSession().setAttribute("player", lr.getPlayer());
					engine.pushTask(new SessionLoginTask(lr.getPlayer()));
				}
			}
		});
	}

	public void register(NPC npc) {
		npcs.add(npc);
	}

	public void unregister(NPC npc) {
		npcs.remove(npc);
		npc.destroy();
	}

	public void register(GameObject obj) {
		if(!obj.loadedInLandscape()) {
			Region[] regions = regionManager.getSurroundingRegions(obj.getLocation());
			for(Region r : regions) {
				for(Player p : r.getPlayers()) {
					p.getPacketSender().sendAddObject(obj.getLocation(), obj.getDefinition().getId(), obj.getType(), obj.getRotation());
				}
			}
			regionManager.getRegionByLocation(obj.getLocation()).addObject(obj);
		}
	}

	public void unregister(GameObject obj, boolean remove) {
		if(remove) {
			Region[] regions = regionManager.getSurroundingRegions(obj.getLocation());
			for(Region r : regions) {
				for(Player p : r.getPlayers()) {
					p.getPacketSender().sendRemoveObject(obj.getLocation(), obj.getType(), obj.getRotation());
				}
			}
		}
		World.getInstance().getRegionManager().getRegionByLocation(obj.getLocation()).removeObject(obj);
	}

	public void replaceObject(final GameObject original, final GameObject replacement) {
		replaceObject(original, replacement, -1);
	}

	public void replaceObject(final GameObject original, final GameObject replacement, int cycles) {
		original.setLoadedInLandscape(false);
		unregister(original, false);
		register(replacement);
		if(cycles > -1) {
			submit(new Event(cycles * 600) {
				@Override
				public void execute() {
					unregister(replacement, true);
					register(original);
					original.setHealth(-1);
					stop();
				}
			});
		}
	}

	public void register(final Player player) {
		// TODO final checks e.g. is player online? is world full?
		int returnCode = 2;
		if(isPlayerOnline(player.getName())) {
			returnCode = 5;
		} else {
			if(!players.add(player)) {
				returnCode = 7;
				logger.info("Could not register player : " + player + " [world full]");
			}
		}
		final int fReturnCode = returnCode;
		PacketBuilder bldr = new PacketBuilder();
		bldr.put((byte) returnCode);
		bldr.put((byte) player.getRights().toInteger());
		bldr.put((byte) 0);
		player.getSession().write(bldr.toPacket()).addListener(new IoFutureListener<IoFuture>() {
			@Override
			public void operationComplete(IoFuture future) {
				if(fReturnCode != 2) {
					player.getSession().close(false);
				} else {
					player.performLogin();
				}
			}
		});
		if(returnCode == 2) {
			logger.info("Registered player : " + player + " [online=" + players.size() + "]");
		}
	}

	public EntityList<Player> getPlayers() {
		return players;
	}

	public EntityList<NPC> getNPCs() {
		return npcs;
	}

	public boolean isPlayerOnline(String name) {
		name = NameUtils.formatName(name);
		for(Player player : players) {
			if(player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public void unregister(final Player player) {
		player.getActionQueue().cancelQueuedActions();
		player.destroy();
		player.getSession().close(false);
		players.remove(player);
		logger.info("Unregistered player : " + player + " [online=" + players.size() + "]");
		engine.submitWork(new Runnable() {
			public void run() {
				playerSaver.savePlayer(player);
			}
		});
	}

	public void createGroundItem(final GroundItem item, final Player player) {
		Tile tile = item.getRegion().getTile(item.getLocation());
		if(item.getItem().getDefinition().isStackable()) {
			if(tile.getGroundItems().size() > 0) {
				for(GroundItem g : tile.getGroundItems()) {
					if(item.isOwnedBy(player.getName()) && g.getItem().getId() == item.getItem().getId()) {
						long existingItemCount = g.getItem().getCount();
						long newItemCount = item.getItem().getCount();
						long total = existingItemCount + newItemCount;
						long remainder = 0;
						if(total > Integer.MAX_VALUE) {
							total = existingItemCount + (Integer.MAX_VALUE - existingItemCount);
							remainder = (existingItemCount + newItemCount) - Integer.MAX_VALUE;
						}
						g.setItem(new Item(g.getItem().getId(), (int) total));
						if(remainder > 0) {
							if(player != null) {
								player.getInventory().add(new Item(g.getItem().getId(), (int) remainder));
							}
						}
						if(g.isGlobal()) {
							for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
								for(Player p : r.getPlayers()) {
									p.getPacketSender().sendRemoveGroundItem(g);
									p.getPacketSender().sendGroundItem(g);
								}
							}
						} else {
							player.getPacketSender().sendRemoveGroundItem(g);
							player.getPacketSender().sendGroundItem(g);
						}
						return;
					}
				}
			}
		}
		register(item, player);
	}

	public void register(final GroundItem item, final Player player) {
		item.getRegion().getGroundItems().add(item);
		item.getRegion().getTile(item.getLocation()).getGroundItems().add(item);
		if(player != null) {
			player.getPacketSender().sendGroundItem(item);
			submit(new Event(100 * 600) {
				@Override
				public void execute() {
					if(item.isRegistered()) {
						for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
							for(Player p : r.getPlayers()) {
								if(!p.getName().equalsIgnoreCase(player.getName())) {
									p.getPacketSender().sendGroundItem(item);
								}
							}
						}
						item.setGlobal(true);
					}
					this.stop();
				}
			});
		} else if(player == null || item.getControllerName().length() < 1) {
			for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
				for(Player p : r.getPlayers()) {
					if(player == null || !p.getName().equalsIgnoreCase(player.getName())) {
						p.getPacketSender().sendGroundItem(item);
					}
				}
			}
			item.setGlobal(true);
		}
		submit(new Event(200 * 600) {

			@Override
			public void execute() {
				if(item.isRegistered()) {
					unregister(item);
				}
				this.stop();
			}
		});
	}

	public void unregister(GroundItem item) {
		item.setRegistered(false);
		item.getRegion().getGroundItems().remove(item);
		item.getRegion().getTile(item.getLocation()).getGroundItems().remove(item);
		if(item.isGlobal()) {
			for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
				for(Player p : r.getPlayers()) {
					p.getPacketSender().sendRemoveGroundItem(item);
				}
			}
		} else {
			for(Region r : getRegionManager().getSurroundingRegions(item.getLocation())) {
				for(Player p : r.getPlayers()) {
					if(item.isOwnedBy(p.getName())) {
						p.getPacketSender().sendRemoveGroundItem(item);
					}
				}
			}
		}
	}

	public Player getPlayerByName(String name) {
		for(Player player : players) {
			if(player != null)
				if(player.getName().equalsIgnoreCase(name))
					return player;
		}
		return null;
	}

	public void sendGlobalMessage(String message) {
		for(Player player : players) {
			if(player != null) {
				player.getPacketSender().sendMessage(message);
			}
		}
	}

	public void handleError(Throwable t) {
		logger.severe("An error occurred in an executor service! The server will be halted immediately.");
		t.printStackTrace();
		System.exit(1);
	}

}
