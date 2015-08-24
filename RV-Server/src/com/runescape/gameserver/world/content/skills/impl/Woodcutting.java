package com.runescape.gameserver.world.content.skills.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

/**
 * Handles the entirety of the Woodcutting skill.
 * @author Aizen Sousuke
 */
public class Woodcutting extends Action {
	
	private static enum Axe {
		
		RUNE(1359, 41, 867, 7),
		ADAMANT(1357, 31, 869, 5),
		MITHRIL(1355, 21, 871, 3.5),
		BLACK(1361, 6, 873, 2.5),
		STEEL(1353, 6, 875, 2),
		IRON(1349, 1, 877, 1.25),
		BRONZE(1351, 1, 879, 1);
		
		private int id;
		private int requiredLevel;
		private Animation animation;
		private double modifier;
		
		private Axe(int id, int requiredLevel, int animationId, double modifier) {
			this.id = id;
			this.requiredLevel = requiredLevel;
			this.animation = Animation.create(animationId);
			this.modifier = modifier;
		}
		
		public int getId() {
			return id;
		}
		
		public int getRequiredLevel() {
			return requiredLevel;
		}
		
		public Animation getAnimation() {
			return animation;
		}
		
		public double getModifier() {
			return modifier;
		}
		
	}
	
	public static enum Tree {
		
		OAK(1521, 15, 75, 22, new int[] { 1281 }, new int[] { 1356 }),
		NORMAL(1, 1511, 1, 50, 50, new int[] { 1276, 1278 }, new int[] { 1342, 1342 });
		
		private static Map<Integer, Tree> trees = new HashMap<Integer, Tree>();
		
		public static Tree forId(int object) {
			return trees.get(object);
		}
		
		static {
			for(Tree tree : Tree.values()) {
				for(int object : tree.objects) {
					trees.put(object, tree);
				}
			}
		}
		
		private Item log;
		private int requiredLevel;
		private double expGained;
		private int respawnTime;
		private int maxLogs;
		private int[] objects;
		private int[] replacements;

		private Tree(int logId, int requiredLevel, double expGained, int respawnTime, int[] objects, int[] replacements) {
			this(25, logId, requiredLevel, expGained, respawnTime, objects, replacements);
		}
		
		private Tree(int maxLogs, int logId, int requiredLevel, double expGained, int respawnTime, int[] objects, int[] replacements) {
			this.log = new Item(logId, 1);
			this.requiredLevel = requiredLevel;
			this.expGained = expGained;
			this.respawnTime = respawnTime;
			this.maxLogs = maxLogs;
			this.objects = objects;
			this.replacements = replacements;
		}
		
		public Item getLog() {
			return log;
		}
		
		public int getRequiredLevel() {
			return requiredLevel;
		}
		
		public double getExpGained() {
			return expGained;
		}
		
		public int getRespawnTime() {
			return respawnTime;
		}
		
		public int[] getObjectIds() {
			return objects;
		}
		
		public int[] getReplacementIds() {
			return replacements;
		}
		
		public int getMaxLogs() {
			return maxLogs;
		}
		
	}

	private Axe axe;
	private Tree tree;
	private int cycles = -1;
	private Location location;
	private int lastAnim;
	private GameObject object;
	private Item log;

	public Woodcutting(Mob entity, Tree tree, GameObject object) {
		super(entity, 0);
		this.location = object.getLocation();
		this.tree = tree;
		this.object = object;
		log = tree.getLog();
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
		for(int i = 0; i < tree.getObjectIds().length; i++) {
			if(tree.getObjectIds()[i] == object.getDefinition().getId()) {
				index = i;
				break;
			}
		}
		return new GameObject(GameObjectDefinition.forId(tree.getReplacementIds()[index]), object.getLocation(), object.getType(), object.getRotation(), false);
	}

	private int calculateCycles() {
		final Mob entity = getMob();
		int skillLevel = 99;
		if(entity instanceof Player) {
			skillLevel = ((Player) entity).getSkills().getLevel(Skill.WOODCUTTING);
		}
		final int treeLevel = tree.getRequiredLevel();
		final double modifier = axe.getModifier();
		final int random = new Random().nextInt(3);
		double cycleCount = 1;
		cycleCount = Math.ceil((treeLevel * 50 - skillLevel * 10) / modifier * 0.0625 - random * 4);
		if(cycleCount < 3) {
			cycleCount = 3;
		}
		//ActionSender.sendMessage((Player) entity, "Cycles: " + cycleCount);
		return (int) cycleCount;
	}
	
	private void sendFullMessage(String logName) {
		Player player = (Player) getMob();
		player.getPacketSender().sendString(357, "Your inventory is too full to hold any more " + logName + ".");
		player.getPacketSender().sendChatBoxInterface(356);
	}
	
	private int getRandomHealth() {
		int max = tree.getMaxLogs();
		int random = new Random().nextInt(max);
		if(random < 1) {
			random = 1;
		}
		return random;
	}

	@Override
	public void execute() {
		final Mob entity = getMob();
		String logName = "";
		boolean isPlayer = false;
		if(entity instanceof Player) {
			isPlayer = true;
		}
		
		if(entity == null || entity.isDestroyed() || entity.isDead()) {
			this.stop();
		}
		
		entity.face(location);
		
		if(isPlayer) {
			Player player = (Player) entity;
			logName = log.getDefinition().getName().toLowerCase();
			final int woodcutting = player.getSkills().getLevel(Skill.WOODCUTTING);
			Axe currentAxe = null;
			boolean found = false;
			for(Axe axe : Axe.values()) {
				if((player.getEquipment().contains(axe.getId()) || player.getInventory().contains(axe.getId()))) {
					found = true;
					if(woodcutting >= axe.getRequiredLevel()) {
						currentAxe = axe;
						break;
					}
				}
			}
			if(!found) {
				player.getPacketSender().sendMessage("You need an axe to chop down this tree.");
			}
			if(currentAxe == null) {
				player.getPacketSender().sendMessage("You do not have an axe which you have the woodcutting level to use.");
			}
			if(!found || currentAxe == null) {
				this.stop();
				entity.resetFace();
				return;
			}
			
			this.axe = currentAxe;
			if(woodcutting < tree.getRequiredLevel()) {
				player.getPacketSender().sendMessage("You need a woodcutting level of " + tree.getRequiredLevel() + " to cut this tree.");
				this.stop();
				entity.resetFace();
				return;
			}
			if(!player.getInventory().hasRoomFor(log)) {
				sendFullMessage(logName);
				this.stop();
				entity.resetFace();
				return;
			}
		}
		if(this.getDelay() == 0) {
			this.setDelay(600);
			if(isPlayer) {
				Player player = (Player) entity;
				entity.playAnimation(axe.getAnimation());
				lastAnim = 1;
				player.getPacketSender().sendMessage("You swing your axe at the tree...");
			}
			cycles = calculateCycles();
			if(object.getHealth() <= -1) {
				object.setHealth(getRandomHealth());
			}
		} else {
			if(cycles == 0) {
				if(isPlayer) {
					Player player = (Player) entity;
					if(!player.getInventory().hasRoomFor(log)) {
						sendFullMessage(logName);
						this.stop();
						entity.playAnimation(Animation.RESET);
						entity.resetFace();
					} else if(object.getHealth() > 0) {
						object.setHealth(object.getHealth() - 1);
						if(object.getHealth() <= 0) {
							World.getInstance().replaceObject(object, getReplacementObject(), tree.getRespawnTime());
							this.stop();
							entity.playAnimation(Animation.RESET);
							entity.resetFace();
						} else {
							cycles = calculateCycles();
						}
						
						player.getInventory().add(log);
						player.getPacketSender().sendMessage("You get some " + logName + ".");
						player.getSkills().addSkillXP(tree.getExpGained(), Skill.WOODCUTTING);
					} else {
						this.stop();
						entity.resetFace();
					}
				}
			} else {
				if(object.getHealth() <= 0) {
					this.stop();
					entity.resetFace();
					return;
				}
				if(lastAnim > 4) {
					entity.playAnimation(axe.getAnimation());
					lastAnim = 0;
				}
				lastAnim++;
				cycles--;
			}
		}
	}

}
