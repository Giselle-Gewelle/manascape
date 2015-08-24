package com.runescape.gameserver.world.entity.mob.player.packet.incoming;

import java.io.PrintWriter;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.event.Event;
import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.pathfinder.DumbPathFinder;
import com.runescape.gameserver.pathfinder.PFPoint;
import com.runescape.gameserver.pathfinder.Path;
import com.runescape.gameserver.pathfinder.PathFinder;
import com.runescape.gameserver.pathfinder.Tile;
import com.runescape.gameserver.pathfinder.TileMap;
import com.runescape.gameserver.pathfinder.TileMapBuilder;
import com.runescape.gameserver.world.Graphic;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.Location;
import com.runescape.gameserver.world.Palette;
import com.runescape.gameserver.world.Palette.PaletteTile;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.container.Bank;
import com.runescape.gameserver.world.content.combat.Combat.AttackType;
import com.runescape.gameserver.world.content.combat.magic.ProjectileHandler;
import com.runescape.gameserver.world.content.combat.magic.spells.Spells;
import com.runescape.gameserver.world.content.minigames.PestControl;
import com.runescape.gameserver.world.content.miscellaneous.Misc;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.GameObjectDefinition;
import com.runescape.gameserver.world.definitions.ItemDefinition;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.action.impl.AttackAction;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.npc.NPCSpawn;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.mob.player.Player.Rights;
import com.runescape.gameserver.world.entity.mob.player.Player.SpellBook;
import com.runescape.gameserver.world.entity.mob.player.packet.IncomingPacket;
import com.runescape.gameserver.world.entity.object.GameObject;
import com.runescape.gameserver.world.region.Region;

public class CommandPacket extends IncomingPacket {

	public CommandPacket(Player player) {
		super(player);
	}

	private void handlePlayerCommand(final Player player) {
		try {
			if(command.startsWith("rank")) {
				player.getPacketSender().sendMessage("You are a(n): " + player.getRights().toString());
				return;
			} else if(command.equals("pos")) {
				player.getPacketSender().sendMessage("You are at: " + player.getLocation() + ".");
				return;
			} else if(command.equals("design")) {
				player.getPacketSender().sendInterface(3559);
			} else if(command.equals("dumplook")) {
				PrintWriter writer = new PrintWriter("lookDump.txt", "UTF-8");

				writer.println("int[] look = getAppearance().getLook();");
				
				int[] look = player.getAppearance().getLook();
				for(int i = 0; i < look.length; i++) {
					writer.println("look[" + i + "] = " + look[i] + ";");
				}
				
				writer.println("getAppearance().setLook(look);");
				
				writer.flush();
				writer.close();
			} else if(command.equals("yell")) {
				if(!Constants.yellChatEnabled && !player.isStaff()) {
					player.getPacketSender().sendMessage("The yell chat is currently disabled.");
					return;
				}
				if(player.isYellBanned()) {
					player.getPacketSender().sendMessage("You are yell banned and cannot use the yellchat.");
					return;
				}
				String rights = player.getRights().toString().toLowerCase();
				rights = Misc.ucFirst(rights);
				//String yellTag = "[" + rights + "]";
				String name = player.getName();
				if(player.isAdmin()) {
					name = "<col=FFD342>" + name + ":</col>";
				} else if(player.isMod()) {
					name = "<col=F2F2F2>" + name + ":</col>";
				} else {
					name = "<col=333333>" + name + ":</col>";
				}
				
				String message = commandString.substring(5);
				message = message.toLowerCase();
				message = Character.toUpperCase(message.charAt(0)) + message.substring(1);
				
				message = "<col=4D0099>-></col> " + name + " <col=4D0099>" + message + "</col>";
				World.getInstance().sendGlobalMessage(message);
				return;
			}

			player.getPacketSender().sendMessage("The command: " + command + " doesn't exist.");
		} catch(Exception ex) {
			player.getPacketSender().sendMessage("Error while processing command.");
			ex.printStackTrace();
		}
	}

	void handleModeratorCommand(final Player player) {
		try {
			if(command.startsWith("teleto")) {
				Player other = World.getInstance().getPlayerByName(args[1]);
				if(command.equals("teletome")) {
					other.setTeleportTarget(player.getLocation());
				} else {
					player.setTeleportTarget(other.getLocation());
				}
				return;
			}
			handlePlayerCommand(player);
		} catch(Exception ex) {
			player.getPacketSender().sendMessage("Error while processing command.");
			ex.printStackTrace();
		}
	}

	void handleAdministratorCommand(final Player player) {
		try {
			if(command.equals("tele")) {
				if(args.length == 3 || args.length == 4) {
					int x = Integer.parseInt(args[1]);
					int y = Integer.parseInt(args[2]);
					int z = player.getLocation().getHeight();
					if(args.length == 4) {
						z = Integer.parseInt(args[3]);
					}
					player.setTeleportTarget(Location.create(x, y, z));
				} else {
					player.getPacketSender().sendMessage("Syntax is ::tele [x] [y] [z].");
				}
				return;
			} else if(command.equals("anim")) {
				if(args.length == 2 || args.length == 3) {
					int id = Integer.parseInt(args[1]);
					int delay = 0;
					if(args.length == 3) {
						delay = Integer.parseInt(args[2]);
					}
					player.playAnimation(Animation.create(id, delay));
				}
				return;
			} else if(command.equals("gfx")) {
				if(args.length == 2 || args.length == 3) {
					int id = Integer.parseInt(args[1]);
					int delay = 0;
					if(args.length == 3) {
						delay = Integer.parseInt(args[2]);
					}
					player.playGraphics(Graphic.create(id, delay, 0));
				}
				return;
			} else if(command.equals("max")) {
				for(Skill s : Skill.values()) {
					player.getSkills().setRealLevel(s, 99, true);
				}
				return;
			} else if(command.equals("resetme")) {
				for(Skill s : Skill.values()) {
					int level = 1;
					if(s == Skill.HITPOINTS)
						level = 10;
					player.getSkills().setRealLevel(s, level, true);
				}
				return;
			} else if(command.startsWith("yellban")) {
				String playerName = args[1];
				Player other = null;
				try {
					other = World.getInstance().getPlayerByName(playerName);
				} catch(Exception e) {
					player.getPacketSender().sendMessage(Misc.optimizeText(playerName + " must be offline."));
				}
				if(other != null) {
					if(!other.isStaff()) {
						player.getPacketSender().sendMessage("Only an owner can yell ban other staff members.");
						return;
					}
					other.yellBan();
					other.getPacketSender().sendMessage("You have been yell banned by: " + player.getName() + ".");
				}
				player.getPacketSender().sendMessage(other.getName() + " has successfully been yell banned.");
				return;
			} else if(command.startsWith("unyellban")) {
				String playerName = args[1];
				Player other = null;
				try {
					other = World.getInstance().getPlayerByName(playerName);
				} catch(Exception e) {
					player.getPacketSender().sendMessage(Misc.optimizeText(playerName + " must be offline."));
				}
				if(other != null) {
					other.unYellBan();
					other.getPacketSender().sendMessage("Your yell ban has been lifted by: " + player.getName() + ".");
				}
				player.getPacketSender().sendMessage(other.getName() + "s yell ban has been lifted..");
				return;
			} else if(command.startsWith("yellon")) {
				if(Constants.yellChatEnabled)
					player.getPacketSender().sendMessage("Yell chat is already on.");
				else {
					Constants.yellChatEnabled = true;
					World.getInstance().sendGlobalMessage("Yell chat has been enabled.");
				}
				return;
			} else if(command.equals("yelloff")) {
				if(!Constants.yellChatEnabled)
					player.getPacketSender().sendMessage("Yell chat is already off.");
				else {
					Constants.yellChatEnabled = false;
					World.getInstance().sendGlobalMessage("Yell chat has been disabled.");
				}
				return;
			} else if(command.equals("setlevel")) {
				Skill s;
				try {
					s = Skill.valueOf(args[1].toUpperCase());
				} catch(RuntimeException e) {
					player.getPacketSender().sendMessage("No such skill as : " + args[1].toUpperCase());
					return;
				}
				player.getSkills().setRealLevel(s, Integer.valueOf(args[2]), true);
				return;
			}
			handleModeratorCommand(player);
		} catch(Exception ex) {
			player.getPacketSender().sendMessage("Error while processing command.");
			ex.printStackTrace();
		}
	}

	void handleOwnerCommand(final Player player) {
		try {
			if(command.startsWith("sound")) {
				player.getPacketSender().sendSound(Integer.parseInt(args[1]), 000, 000);
				return;
			} else if(command.startsWith("setrank")) {
				String playerName = args[1];
				Player other = World.getInstance().getPlayerByName(playerName);
				Rights rights = Rights.PLAYER;
				String rightsString = args[2].toUpperCase();
				try {
					rights = Rights.valueOf(rightsString);
				} catch(Exception e) {
					player.getPacketSender().sendMessage("No such rights as: " + rightsString);
				}
				if(other != null) {
					other.setRights(rights);
					other.getPacketSender().sendMessage("Your player rights have been set to: " + rights.toString() + " by " + player.getName() + ".");
				} else
					player.getPacketSender().sendMessage(playerName + " must not be online.");
				return;
			} else if(command.startsWith("objid")) {
				String objectName = args[1];
				try {
					GameObjectDefinition def = GameObjectDefinition.forName(objectName);
					player.getPacketSender().sendMessage(objectName + "s id is: " + def.getId());
				} catch(Exception e) {
					player.getPacketSender().sendMessage("invalid name");
				}
				return;
			} else if(command.equals("item") || command.equals("i")) {
				ItemDefinition item = null;
				if(args.length == 2 || args.length == 3) {
					item = ItemDefinition.forName(args[1].replaceAll("_", " "));
					int id = 0;
					if(item == null)
						id = Integer.parseInt(args[1]);
					else
						id = item.getId();
					int count = 1;
					if(args.length == 3) {
						count = Integer.parseInt(args[2]);
					}
					player.getInventory().add(new Item(id, count));
				} else {
					player.getPacketSender().sendMessage("Syntax is ::item [id] [count].");
				}
				return;
			} else if(command.equals("delobj")) {
				Region region = player.getRegion();
				Location loc = Location.create(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getHeight());
				GameObject object = region.getGameObject(loc);
				if(object == null) {
					player.getPacketSender().sendMessage("There are no objects here!");
					return;
				}
				player.getPacketSender().sendMessage("Object Type: " + object.getType() + ", Object Rotation: " + object.getRotation());
				player.getPacketSender().sendRemoveObject(object.getLocation(), object.getType(), object.getRotation());
				return;
			} else if(command.startsWith("obj")) {
				try {
					int face = 0;
					if(args.length > 2) {
						face = Integer.parseInt(args[2]);
					}
					GameObjectDefinition definition = GameObjectDefinition.forId(Integer.parseInt(args[1]));
					GameObject object = new GameObject(definition, player.getLocation(), 10, face, false);
					object = GameObject.getObjectInstance(object);
					player.getPacketSender().sendAddObject(player.getLocation(), definition.getId(), object.getType(), object.getRotation());
				} catch(Exception e) {
					e.printStackTrace();
					player.getPacketSender().sendMessage("Syntax is ::obj [id].");
				}
				return;
			} else if(command.startsWith("noclip")) {
				if(args.length > 1) {
					String setting = args[1];
					if(setting.equals("on")) {
						player.getWalkingQueue().setNoClip(true);
					} else if(setting.equals("off")) {
						player.getWalkingQueue().setNoClip(false);
					}
				}
				return;
			} else if(command.startsWith("runes")) {
				Item[] runes = { new Item("air rune"), new Item("mind rune"), new Item("water rune"), new Item("earth rune"), new Item("fire rune"), new Item("blood rune"), new Item("choas rune"),
						new Item("death rune") };

				for(Item i : runes) {
					player.getInventory().add(new Item(i.getId(), 100000));
				}
				return;
			} else if(command.equals("proj")) {
				// ActionSender.sendProjectile(player, Location.create(3222,
				// 3222, 0), Location.create(3222, 3215, 0), 24, 50, 11, 6, 45,
				// 29, 11, 11, 11);
				NPC npc = (NPC) World.getInstance().getNPCs().get(Integer.parseInt(args[1]));
				ProjectileHandler.sendMagicProjectile(npc, player, Spells.AIR_STRIKE);
				return;
			} else if(command.equals("bank")) {
				Bank.open(player);
				return;
			} else if(command.equals("nt")) {
				NPC npc1 = (NPC) World.getInstance().getNPCs().get(Integer.parseInt(args[1]));
				npc1.transformNpc(Integer.parseInt(args[2]));
				return;
			} else if(command.equals("spellbook")) {
				String input = args[1].toUpperCase();
				SpellBook book = SpellBook.MODERN;
				try {
					book = SpellBook.valueOf(input);
				} catch(Exception e) {
					player.getPacketSender().sendMessage("There is no such spellbook as: " + input);
				}
				player.setCurrentSpellBook(book);
				return;
			} else if(command.equals("npc")) {
				int id = Integer.parseInt(args[1]);
				NPC npc = NPC.getNpcInstance(id);
				npc.setLocation(Location.create(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getHeight()));
				npc.setNPCSpawn(new NPCSpawn(npc));
				World.getInstance().register(npc);
				return;
			} else if(command.equals("reallevel")) {
				Skill s;
				try {
					s = Skill.valueOf(args[1].toUpperCase());
				} catch(RuntimeException e) {
					player.getPacketSender().sendMessage("No such skill as : " + args[1].toUpperCase());
					return;
				}
				player.getPacketSender().sendMessage("Your real " + s.toString() + " level is: " + player.getSkills().getRealLevel(s));
				player.getPacketSender().sendMessage("Your " + s.toString() + " level is: " + player.getSkills().getLevel(s));
				return;
			} else if(command.startsWith("cannon")) {
				player.getPacketSender().sendAddGlobalObject(player.getLocation(), 6, 10, 2);
				World.getInstance().submit(new Event(600) {
					int animId = 514;

					@Override
					public void execute() {
						if(animId == 521) {
							animId = 514;
						} else {
							animId++;
						}
						player.getPacketSender().sendObjectAnimation(3222, 3222, animId, 10, -1);
						// ActionSender.sendMessage(player, "Animation Id:" +
						// animId);
					}
				});
				return;
			} else if(command.startsWith("hintp")) {
				int index = Integer.parseInt(args[1]);
				player.getPacketSender().sendHintIconPlayer(index);
				return;
			} else if(command.equals("interface")) {
				int id = Integer.parseInt(args[1]);
				try {
					player.getPacketSender().sendInterface(id);
				} catch(Exception e) {
					player.getPacketSender().sendMessage("Invalid interface id: " + id);
				}
				return;
			} else if(command.equals("ci")) {
				int id = 968;
				try {
					id = Integer.parseInt(args[1]);
				} catch(Exception e) {

				}
				player.getPacketSender().sendChatBoxInterface(id);
				return;
			} else if(command.equals("pc")) {
				PestControl.sendPestControlRewardsInterface(player);
				return;
			} else if(command.equals("test")) {
				// TODO: use for testing
				return;
			} else if(command.equals("config")) {
				int settingState = Integer.parseInt(args[1]);
				int settingId = Integer.parseInt(args[2]);
				player.getPacketSender().sendConfig(settingState, settingId);
				return;
			} else if(command.startsWith("empty")) {
				player.getInventory().clear();
				player.getPacketSender().sendMessage("Your inventory has been emptied.");
				return;
			} else if(command.equals("config")) {
				int id = Integer.parseInt(args[1]);
				int id2 = Integer.parseInt(args[2]);
				player.getPacketSender().sendConfig(id, id2);
			} else if(command.startsWith("cl")) {
				World.getInstance().submit(new Event(600) {
					int configId = 0;

					@Override
					public void execute() {
						player.getPacketSender().sendConfig(configId, 15);
						player.getPacketSender().sendMessage("Config " + configId);
						configId++;
					}

				});
			} else if(command.startsWith("enablepvp")) {
				try {
					player.updatePlayerAttackOptions(true);
					player.getPacketSender().sendMessage("PvP combat enabled.");
					return;
				} catch(Exception e) {

				}
			} else if(command.startsWith("nvn")) {
				@SuppressWarnings("unused")
				AttackType type = AttackType.CRUSH;
				NPC npc1 = (NPC) World.getInstance().getNPCs().get(Integer.parseInt(args[1]));
				NPC npc2 = (NPC) World.getInstance().getNPCs().get(Integer.parseInt(args[2]));
				npc1.getActionQueue().addAction(new AttackAction(npc1, npc2));
				return;
			} else if(command.startsWith("dgoto")) {
				if(args.length == 3) {
					try {
						int radius = 16;

						int x = Integer.parseInt(args[1]) - player.getLocation().getX() + radius;
						int y = Integer.parseInt(args[2]) - player.getLocation().getY() + radius;

						TileMapBuilder bldr = new TileMapBuilder(player.getLocation(), radius);
						TileMap map = bldr.build();

						PathFinder pf = new DumbPathFinder();
						Path p = pf.findPath(player.getLocation(), radius, map, radius, radius, x, y);

						if(p == null)
							return;

						player.getWalkingQueue().reset();
						for(PFPoint p2 : p.getPoints()) {
							player.getWalkingQueue().addStep(p2.getX(), p2.getY());
						}
					} catch(Throwable ex) {
						ex.printStackTrace();
					}
				}
			} else if(command.startsWith("goto")) {
				if(args.length == 3) {
					try {
						/*
						 * int radius = 16;
						 * 
						 * int x = Integer.parseInt(args[1]) -
						 * player.getLocation().getX() + radius; int y =
						 * Integer.parseInt(args[2]) -
						 * player.getLocation().getY() + radius;
						 * 
						 * TileMapBuilder bldr = new
						 * TileMapBuilder(player.getLocation(), radius); TileMap
						 * map = bldr.build();
						 * 
						 * PathFinder pf = new AStarPathFinder(); Path p =
						 * pf.findPath(player.getLocation(), radius, map,
						 * radius, radius, x, y);
						 * 
						 * if(p == null) return;
						 * 
						 * player.getWalkingQueue().reset();
						 * player.getWalkingQueue
						 * ().setRunningQueue(player.getSettings
						 * ().getBool(Settings.RUNNING));
						 * player.getWalkingQueue(
						 * ).addStep(p.getPoints().getFirst().getX(),
						 * p.getPoints().getFirst().getY()); int count = 0;
						 * for(PFPoint p2 : p.getPoints()) {
						 * player.getWalkingQueue().addStep(p2.getX(),
						 * p2.getY()); count++; }
						 * player.getWalkingQueue().finish();
						 */
						player.getWalkingQueue().walkTo(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
					} catch(Throwable ex) {
						ex.printStackTrace();
					}
				}
			} else if(command.equals("fm")) {
				try {
					// TODO DumbPathFinder for this?...
					int radius = 4;

					int x = (player.getLocation().getX() - 1) - player.getLocation().getX() + radius;
					int y = player.getLocation().getY() - player.getLocation().getY() + radius;

					TileMapBuilder bldr = new TileMapBuilder(player.getLocation(), radius);
					TileMap map = bldr.build();

					int stepX = 0, stepY = 0;

					/*
					 * Move west.
					 */
					if(map.getTile(radius, radius).isWesternTraversalPermitted() && map.getTile(x, y).isEasternTraversalPermitted()) {
						player.getPacketSender().sendMessage("Can move west.");
						stepX = -1;
					} else {
						/*
						 * Can't move west, try moving east.
						 */
						x = (player.getLocation().getX() + 1) - player.getLocation().getX() + radius;
						y = player.getLocation().getY() - player.getLocation().getY() + radius;
						if(map.getTile(radius, radius).isEasternTraversalPermitted() && map.getTile(x, y).isWesternTraversalPermitted()) {
							player.getPacketSender().sendMessage("Can move east.");
							stepX = 1;
						} else {
							/*
							 * Can't move east, try moving south.
							 */
							x = player.getLocation().getX() - player.getLocation().getX() + radius;
							y = (player.getLocation().getY() - 1) - player.getLocation().getY() + radius;
							if(map.getTile(radius, radius).isSouthernTraversalPermitted() && map.getTile(x, y).isNorthernTraversalPermitted()) {
								player.getPacketSender().sendMessage("Can move south.");
								stepY = -1;
							} else {
								/*
								 * Can't move south, try moving north.
								 */
								x = player.getLocation().getX() - player.getLocation().getX() + radius;
								y = (player.getLocation().getY() + 1) - player.getLocation().getY() + radius;
								if(map.getTile(radius, radius).isNorthernTraversalPermitted() && map.getTile(x, y).isSouthernTraversalPermitted()) {
									player.getPacketSender().sendMessage("Can move north.");
									stepY = 1;
								} else {
									/*
									 * Can't move anywhere!
									 */
									player.getPacketSender().sendMessage("Found no location to move to!");
								}
							}
						}
					}
					if(stepX != 0 || stepY != 0) {
						/*
						 * Path p = new Path(); p.addPoint(new
						 * Point(player.getLocation().getX() + stepX,
						 * player.getLocation().getY() + stepY)); p.addPoint(new
						 * Point(radius + player.getLocation().getX() - radius,
						 * radius + player.getLocation().getY() - radius));
						 * 
						 * player.getWalkingQueue().reset(); for(Point p2 :
						 * p.getPoints()) {
						 * player.getWalkingQueue().addStep(p2.getX(),
						 * p2.getY()); }
						 */
						player.getWalkingQueue().reset();
						player.getWalkingQueue().addStep(player.getLocation().getX() + stepX, player.getLocation().getY() + stepY);
					}
					/*
					 * if(map.getTile(x, y).isWesternTraversalPermitted()) {
					 * System.out.println("Can move west."); } else {
					 * System.out.println("Can NOT move west."); }
					 */

					/*
					 * PathFinder pf = new AStarPathFinder(); Path p =
					 * pf.findPath(player.getLocation(), radius, map, radius,
					 * radius, x, y);
					 * 
					 * if(p == null) return;
					 * 
					 * player.getWalkingQueue().reset(); for(Point p2 :
					 * p.getPoints()) {
					 * player.getWalkingQueue().addStep(p2.getX(), p2.getY()); }
					 */
				} catch(Throwable ex) {
					ex.printStackTrace();
				}
			} else if(command.equals("fm2")) {
				Location loc = player.getLocation();
				int px = loc.getX();
				int py = loc.getY();
				int xPos = px - 1;
				int yPos = py;

				int radius = 4;
				int x = xPos - px + radius;
				int y = yPos - py + radius;

				TileMapBuilder bldr = new TileMapBuilder(loc, radius);
				TileMap map = bldr.build();

				PathFinder pf = new DumbPathFinder();
				Path p = pf.findPath(loc, radius, map, radius, radius, x, y);

				if(p == null) {
					player.getPacketSender().sendMessage("Can not move west.");
				} else {
					player.getWalkingQueue().addStep(xPos, yPos);
					player.getWalkingQueue().finish();
					// player.getWalkingQueue().walkTo(xPos, yPos);
				}
			} else if(command.startsWith("tmask")) {
				int radius = 0;
				TileMapBuilder bldr = new TileMapBuilder(player.getLocation(), radius);
				TileMap map = bldr.build();
				Tile t = map.getTile(0, 0);
				player.getPacketSender().sendMessage(
						"N: " + t.isNorthernTraversalPermitted() + " E: " + t.isEasternTraversalPermitted() + " S: " + t.isSouthernTraversalPermitted() + " W: " + t.isWesternTraversalPermitted());
				return;
			} else if(command.equals("poh")) {
				//player.setTeleportTarget(Location.create(48, 48, 0));
				//player.getPacketSender().sendSetCurrentPlacement(player.getLocation());
				player.getPacketSender().sendSetCurrentPlacement(Location.create(48, 48, 0));
				
				Palette palette = new Palette();
				for(int x = 0; x < 13; x++) {
					for(int y = 0; y < 13; y++) {
						palette.setTile(x, y, 0, new PaletteTile(2192, 3309, 0, 0));
					}
				}
				
				player.getPacketSender().sendConstructMapRegion(palette);
			} else if(command.equals("noclip")) {
				player.getWalkingQueue().setNoClip(true);
			}
			handleAdministratorCommand(player);
		} catch(Exception ex) {
			player.getPacketSender().sendMessage("Error while processing command.");
			ex.printStackTrace();
		}
	}

	String[] args;
	String command;
	String commandString;

	@Override
	public void handle(Packet packet) {
		commandString = packet.getRS2String();
		String[] args = commandString.split(" ");
		String command = args[0].toLowerCase();
		this.args = args;
		this.command = command;

		if(player.isAdmin()) {
			// handleAdministratorCommand(player);
			handleOwnerCommand(player);
		} else if(player.isMod()) {
			handleModeratorCommand(player);
		} else {
			handlePlayerCommand(player);
		}
	}

}
