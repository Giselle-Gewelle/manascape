package com.runescape.gameserver.world.content.skills.impl;

import java.util.HashMap;
import java.util.Map;

import com.runescape.gameserver.pathfinder.DumbPathFinder;
import com.runescape.gameserver.pathfinder.Path;
import com.runescape.gameserver.pathfinder.PathFinder;
import com.runescape.gameserver.pathfinder.TileMap;
import com.runescape.gameserver.pathfinder.TileMapBuilder;
import com.runescape.gameserver.world.GroundItem;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.object.GameObject;

public class Firemaking extends Action {
	
	public enum Log {
		
		OAK(1521, 15, 60),
		NORMAL(1511, 1, 40);
		
		private static Map<Integer, Log> logs = new HashMap<Integer, Log>();
		
		public static Log forId(int id) {
			return logs.get(id);
		}
		
		static {
			for(Log log : Log.values()) {
				logs.put(log.id, log);
			}
		}
		
		private int id;
		private Item item;
		private int requiredLevel;
		private double expGained;
		
		private Log(int id, int requiredLevel, double expGained) {
			this.id = id;
			item = new Item(id);
			this.requiredLevel = requiredLevel;
			this.expGained = expGained;
		}
		
		public int getId() {
			return id;
		}
		
		public Item getItem() {
			return item;
		}
		
		public int getRequiredLevel() {
			return requiredLevel;
		}
		
		public double getExpGained() {
			return expGained;
		}
		
	}
	
	class FMSession {
		
		protected GroundItem gItem = null;
		protected int xOff = 0;
		protected int yOff = 0;
		
	}
	
	private static final int 
		STAGE_INITIAL_LIGHT = 0,
		STAGE_LIGHT = 1;
	
	public static final Item TINDERBOX = new Item(590, 1);
	public static final Animation LIGHT_FIRE = Animation.create(733);
	
	private Log log;
	private int stage = 0;
	private int usedItemId;
	private int usedWithItemId;
	private int usedSlotId;
	private int usedWithSlotId;
	private FMSession session;

	public Firemaking(Mob entity, Log log, int usedItemId, int usedWithItemId, int usedSlotId, int usedWithSlotId) {
		super(entity, 0);
		this.log = log;
		this.usedItemId = usedItemId;
		this.usedWithItemId = usedWithItemId;
		this.usedSlotId = usedSlotId;
		this.usedWithSlotId = usedWithSlotId;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}
	
	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	@Override
	public void execute() {
		final Mob entity = this.getMob();
		
		if(entity == null || entity.isDestroyed() || entity.isDead()) {
			this.stop();
		}
		
		if(stage == STAGE_INITIAL_LIGHT) {
			Player player = null;
			if(entity.isPlayer()) {
				player = (Player) entity;
				final int firemaking = player.getSkills().getLevel(Skill.FIREMAKING);
				if(firemaking < log.getRequiredLevel()) {
					player.getPacketSender().sendMessage("You need a firemaking level of " + log.getRequiredLevel() + " to light this log.");
					this.stop();
					return;
				}
			}
	
			session = new FMSession();
			boolean canMove = false;
			if(tryMove(-1, 0)) {
				// WEST
				canMove = true;
				session.xOff = -1;
			} else if(tryMove(1, 0)) {
				// EAST
				canMove = true;
				session.xOff = 1;
			} else if(tryMove(0, -1)) {
				// SOUTH
				canMove = true;
				session.yOff = -1;
			} else if(tryMove(0, 1)) {
				// NORTH
				canMove = true;
				session.yOff = 1;
			}
			if(canMove) {
				Location loc = entity.getLocation();
				
				if(entity instanceof Player) {
					player = (Player) entity;
					
					int logSlot = -1;
					if(usedItemId == log.getId()) {
						logSlot = usedSlotId;
					} else if(usedWithItemId == log.getId()) {
						logSlot = usedWithSlotId;
					}
					
					player.getInventory().remove(logSlot, log.getItem());
					session.gItem = new GroundItem(player.getName(), log.getItem(), loc);
					World.getInstance().createGroundItem(session.gItem, player);
				}
				
				long lastLight = entity.getSkills().getLastFireLight();
				if(lastLight == -1 || (System.currentTimeMillis() - lastLight) > 2500) {
					if(entity instanceof Player) {
						((Player) entity).getPacketSender().sendMessage("You attempt to light the logs.");
					}
					entity.playAnimation(Animation.LIGHT_FIRE);
					this.setDelay(1200);
				} else {
					this.setDelay(600);
				}
				stage++;
			} else {
				if(entity instanceof Player) {
					((Player) entity).getPacketSender().sendMessage("You cannot light a fire here.");
					
					this.stop();
				}
			}
		} else if(stage == STAGE_LIGHT) {
			Location loc = entity.getLocation();
			entity.playAnimation(Animation.RESET);
			GameObjectDefinition fire = GameObjectDefinition.forName("fire");
			World.getInstance().register(new GameObject(fire, loc, 10, 0, false));
			
			if(entity instanceof Player) {
				World.getInstance().unregister(session.gItem);
			}
			
			entity.getWalkingQueue().addStep(loc.getX() + session.xOff, loc.getY() + session.yOff);
			entity.getWalkingQueue().finish();
			
			if(entity instanceof Player) {
				((Player) entity).getPacketSender().sendMessage("The fire catches and the logs begin to burn.");
			}
			
			entity.face(loc);
			entity.getSkills().setLastFireLight(System.currentTimeMillis());
			
			this.stop();
		}
	}
	
	private boolean tryMove(int xOff, int yOff) {
		Location loc = getMob().getLocation();
		int px = loc.getX();
		int py = loc.getY();
		int xPos = px + xOff;
		int yPos = py + yOff;
		
		int radius = 4;
		int x = xPos - px + radius;
		int y = yPos - py + radius;
								
		TileMapBuilder bldr = new TileMapBuilder(loc, radius);
		TileMap map = bldr.build();
		
		PathFinder pf = new DumbPathFinder();
		Path p = pf.findPath(loc, radius, map, radius, radius, x, y);
		
		return (p != null);
	}

}
