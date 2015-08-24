package com.runescape.gameserver.world.content.skills.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.runescape.gameserver.event.impl.SoundEvent;
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

public class Mining extends Action {
	
	public static enum Pickaxe {
		
		RUNE(1275, 41, 624, 7),
		ADAMANT(1271, 31, 628, 5),
		MITHRIL(1273, 21, 629, 3.5),
		STEEL(1269, 6, 627, 2),
		IRON(1267, 1, 626, 1.25),
		BRONZE(1265, 1, 625, 1);
		
		private int id;
		private int level;
		private Animation animation;
		private double multiplier;
		
		private Pickaxe(int id, int level, int animation, double multiplier) {
			this.id = id;
			this.level = level;
			this.animation = Animation.create(animation);
			this.multiplier = multiplier;
		}
		
		public int getId() {
			return id;
		}
		
		public int getRequiredLevel() {
			return level;
		}
		
		public Animation getAnimation() {
			return animation;
		}
		
		public double getMultiplier() {
			return multiplier;
		}
		
	}
	
	/**
	 * @TODO replacement rock ids
	 */
	public static enum Rock {
		
		COPPER(436, 1, 17.5, 6, new int[] { 2090, 2091 }, new int[] { 450, 451 }),
		TIN(438, 1, 17.5, 6, new int[] { 2094, 2095 }, new int[] { 450, 451 }),
		CLAY(434, 1, 5, 2, new int[] { 2108, 2109 }, new int[] { 450, 451 }),
		IRON(440, 15, 35, 15, new int[] { 2092, 2093 }, new int[] { 450, 451 }),
		SILVER(442, 20, 40, 100, new int[] { 2100, 2101 }, new int[] { 450, 451 }),
		COAL(453, 30, 50, 50, new int[] { 2096, 2097 }, new int[] { 450, 451 }),
		GOLD(444, 40, 65, 100, new int[] { 2098, 2099 }, new int[] { 450, 451 }),
		MITHRIL(447, 55, 80, 200, new int[] { 2102, 2103 }, new int[] { 450, 451 }),
		ADAMANTITE(449, 70, 95, 400, new int[] { 2104, 2105 }, new int[] { 450, 451 }),
		RUNE(451, 85, 125, 1000, new int[] { 2106, 2107}, new int[] { 450, 451 }),
		EMPTY(new int[] { 450, 451 });
		
		private static Map<Integer, Rock> rocks = new HashMap<Integer, Rock>();
		
		public static Rock forId(int object) {
			return rocks.get(object);
		}
		
		static {
			for(Rock rock : Rock.values()) {
				for(int object : rock.objects) {
					rocks.put(object, rock);
				}
			}
		}
		
		private int[] objects;
		private int[] replacements;
		private int level;
		private Item ore;
		private double experience;
		private int respawnTime;
		private boolean empty;
		
		private Rock(int ore, int level, double experience, int respawnTime, int[] objects, int[] replacements) {
			this.objects = objects;
			this.replacements = replacements;
			this.level = level;
			this.experience = experience;
			this.respawnTime = respawnTime;
			this.ore = new Item(ore, 1);
			empty = false;
		}
		
		private Rock(int[] objects) {
			this.objects = objects;
			empty = true;
		}
		
		public Item getOre() {
			return ore;
		}
		
		public int[] getObjectIds() {
			return objects;
		}
		
		public int[] getReplacementIds() {
			return replacements;
		}
		
		public int getRequiredLevel() {
			return level;
		}
		
		public double getExperience() {
			return experience;
		}
		
		public int getRespawnTime() {
			return respawnTime;
		}
		
		public boolean isEmpty() {
			return empty;
		}
		
	}

	private Pickaxe pickaxe;
	private Rock rock;
	private int cycles;
	private Location location;
	private int lastAnim;
	private GameObject object;
	private Item ore;
	private SoundEvent soundEvent = null;
	
	public Mining(Mob entity, Rock rock, GameObject object) {
		super(entity, 0);
		this.location = object.getLocation();
		this.rock = rock;
		this.object = object;
		ore = rock.getOre();
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
	
	private GameObject getReplacementObject() {
		int index = 0;
		for(int i = 0; i < rock.getObjectIds().length; i++) {
			if(rock.getObjectIds()[i] == object.getDefinition().getId()) {
				index = i;
				break;
			}
		}
		return new GameObject(GameObjectDefinition.forId(rock.getReplacementIds()[index]), object.getLocation(), object.getType(), object.getRotation(), false);
	}

	private int calculateCycles() {
		final Mob entity = getMob();
		int skillLevel = 99;
		if(entity instanceof Player) {
			skillLevel = ((Player) entity).getSkills().getLevel(Skill.MINING);
		}
		final int rockLevel = rock.getRequiredLevel();
		final double multiplier = pickaxe.getMultiplier();
		final int random = new Random().nextInt(3);
		double cycleCount = 1;
		cycleCount = Math.ceil((rockLevel * 50 - skillLevel * 10) / multiplier * 0.0625 - random * 4);
		if(cycleCount < 3) {
			cycleCount = 3;
		}
		return (int) cycleCount;
	}
	
	private void sendFullMessage(String oreName) {
		Player player = (Player) getMob();
		player.getPacketSender().sendString(357, "Your inventory is too full to hold any more " + oreName + ".");
		player.getPacketSender().sendChatBoxInterface(356);
	}
	
	@Override
	public void stop() {
		stop(true);
	}
	
	public void stop(boolean stopAnim) {
		super.stop();
		final Mob entity = getMob();
		if(stopAnim) {
			if(soundEvent != null) {
				soundEvent.stop();
			}
			entity.playAnimation(Animation.RESET);
		}
		entity.resetFace();
	}

	@Override
	public void execute() {
		final Mob entity = getMob();
		String oreName = "";
		boolean isPlayer = false;
		if(entity instanceof Player) {
			isPlayer = true;
		}
		
		if(entity == null || entity.isDestroyed() || entity.isDead()) {
			stop(true);
		}
		
		entity.face(location);
		
		if(isPlayer) {
			Player player = (Player) entity;
			if(rock.isEmpty()) {
				player.getPacketSender().sendMessage("There is no ore currently available in this rock.");
				stop(false);
				return;
			}
			oreName = ore.getDefinition().getName().toLowerCase().replaceAll(" ore", "");
			final int mining = player.getSkills().getLevel(Skill.MINING);
			Pickaxe currentPick = null;
			boolean found = false;
			for(Pickaxe pickaxe : Pickaxe.values()) {
				if((player.getEquipment().contains(pickaxe.getId()) || player.getInventory().contains(pickaxe.getId()))) {
					found = true;
					if(mining >= pickaxe.getRequiredLevel()) {
						currentPick = pickaxe;
						break;
					}
				}
			}
			if(!found) {
				player.getPacketSender().sendMessage("You need a pickaxe to mine this rock.");
			}
			if(currentPick == null) {
				player.getPacketSender().sendMessage("You do not have a pickaxe which you have the mining level to use.");
			}
			if(!found || currentPick == null) {
				stop(true);
				return;
			}
			
			this.pickaxe = currentPick;
			if(mining < rock.getRequiredLevel()) {
				player.getPacketSender().sendMessage("You need a mining level of " + rock.getRequiredLevel() + " to mine this rock.");
				stop(true);
				return;
			}
			if(!player.getInventory().hasRoomFor(ore)) {
				sendFullMessage(oreName);
				stop(true);
				return;
			}
		}
		if(this.getDelay() == 0) {
			this.setDelay(600);
			if(isPlayer) {
				Player player = (Player) entity;
				entity.playAnimation(pickaxe.getAnimation());
				soundEvent = new SoundEvent((Player) entity, 432, 700, 6);
				World.getInstance().submit(soundEvent);
				lastAnim = 1;
				player.getPacketSender().sendMessage("You swing your pick at the rock...");
			}
			cycles = calculateCycles();
			if(object.getHealth() <= -1) {
				object.setHealth(1);
			}
		} else {
			if(cycles == 0) {
				if(isPlayer) {
					Player player = (Player) entity;
					if(!player.getInventory().hasRoomFor(ore)) {
						sendFullMessage(oreName);
					} else if(object.getHealth() > 0) {
						object.setHealth(object.getHealth() - 1);
						if(object.getHealth() <= 0) {
							World.getInstance().replaceObject(object, getReplacementObject(), rock.getRespawnTime());
						}
						player.getInventory().add(ore);
						player.getPacketSender().sendMessage("You manage to mine some " + oreName + ".");
						player.getSkills().addSkillXP(rock.getExperience(), Skill.MINING);
					}
				}
				stop(true);
			} else {
				if(object.getHealth() <= 0) {
					stop(false);
					return;
				}
				if(lastAnim > 6) {
					entity.playAnimation(pickaxe.getAnimation());
					soundEvent = new SoundEvent((Player) entity, 432, 800, 700, 6);
					World.getInstance().submit(soundEvent);
					lastAnim = 0;
				}
				lastAnim++;
				cycles--;
			}
		}
	}

}
