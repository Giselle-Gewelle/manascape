package com.runescape.gameserver.world.content.skills.impl;

import java.util.Random;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.content.dialogue.Dialogue;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.ItemDefinition;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

public final class Fishing extends Action {

	public enum SpotType {

		NORMAL_NETTING(new int[] { 316 }, 303, -1, 621, new int[] { 1, 15 }, new int[] { 317, 321 }, new double[] { 10, 50 }),

		SEA_BAITING(new int[] { 316 }, 307, 313, 622, new int[] { 5, 10 }, new int[] { 327, 345 }, new double[] { 20, 30 }),

		FLY_LURING(new int[] { 309, 328 }, 309, 314, 622, new int[] { 20, 30 }, new int[] { 335, 331 }, new double[] { 50, 70 }),

		RIVER_BAITING(new int[] { 309, 328 }, 307, 313, 622, new int[] { 25 }, new int[] { 349 }, new double[] { 60 }),

		LOBSTER_CAGING(new int[] { 312 }, 301, -1, 619, new int[] { 40 }, new int[] { 377 }, new double[] { 90 }),

		HARPOONING(new int[] { 312, 1333 }, 311, -1, 618, new int[] { 35, 50 }, new int[] { 359, 371 }, new double[] { 80, 100 }),

		BIG_NETTING(new int[] { 313, 322, 334 }, 305, -1, 620, new int[] { 16, 23, 46 }, new int[] { 353, 341, 363 }, new double[] { 20, 45, 100 }),

		SHARK_HARPOONING(new int[] { 313, 322, 334 }, 311, -1, 618, new int[] { 76 }, new int[] { 383 }, new double[] { 110 }),

		MONKFISH_NETTING(new int[] { 1333 }, 303, -1, 621, new int[] { 62 }, new int[] { 7944 }, new double[] { 120 });
		
		private SpotType(int[] npcIds, int tool, int bait, int animation, int[] levels, int[] fish, double[] exp) {
			this.npcIds = npcIds;
			this.tool = tool;
			if(bait == -1) {
				this.bait = null;
			} else {
				this.bait = new Item(bait, 1);
			}
			this.animation = animation;
			this.levels = levels;
			final Item[] fishItems = new Item[fish.length];
			for(int index = 0; index < fish.length; index++) {
				fishItems[index] = new Item(fish[index]);
			}
			this.fish = fishItems;
			this.exp = exp;
		}

		public Item getBait() {
			return bait;
		}

		public Item[] getFish() {
			return fish;
		}

		public int getAnimation() {
			return animation;
		}

		public int[] getLevels() {
			return levels;
		}

		public double[] getExperience() {
			return exp;
		}

		public int getTool() {
			return tool;
		}

		public int[] getNPCIds() {
			return npcIds;
		}

		private final int[] npcIds;
		private final int tool;
		private final int[] levels;
		private final Item[] fish;
		private final double[] exp;
		private final int animation;
		private final Item bait;

		public static SpotType forId(int id) {
			for(SpotType d : values()) {
				for(int i : d.getNPCIds()) {
					if(i == id) {
						return d;
					}
				}
			}
			return null;
		}

		public static boolean isFish(int id) {
			for(SpotType d : values()) {
				for(int i : d.getNPCIds()) {
					if(i == id) {
						return true;
					}
				}
			}
			return false;
		}

		public String getStartMessage() {
			switch(this) {
				default:
				case HARPOONING:
					return null;
				case NORMAL_NETTING:
				case BIG_NETTING:
				case MONKFISH_NETTING:
					return "You cast out your net...";
				case SEA_BAITING:
				case RIVER_BAITING:
				case FLY_LURING:
					return "You cast out your line...";
				case LOBSTER_CAGING:
					return "You attempt to catch a lobster.";
				case SHARK_HARPOONING:
					return "You attempt to catch a shark.";
			}
		}

		public static SpotType getSpot(int id, int option) {
			if(option == 2) {
				switch(id) {
					case 309:
					case 328:
						return FLY_LURING;
					case 312:
						return LOBSTER_CAGING;
					case 316:
						return NORMAL_NETTING;
					case 313:
					case 322:
					case 334:
						return BIG_NETTING;
				}
			} else if(option == 3) {
				switch(id) {
					case 309:
					case 328:
						return RIVER_BAITING;
					case 312:
						return HARPOONING;
					case 316:
						return SEA_BAITING;
					case 313:
					case 334:
					case 322:
						return SHARK_HARPOONING;
				}
			}
			
			return null;
		}
		
	}
	
	private NPC spot;
	private SpotType spotType;
	private Random random;
	private boolean started;
	
	public Fishing(Mob mob, NPC spot, SpotType spotType) {
		super(mob, 0);
		this.spot = spot;
		this.spotType = spotType;
		random = new Random();
		started = false;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.ALWAYS;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	private boolean canGetFish() {
		double chance = 20;
		
		chance += (this.getMob().getSkills().getLevel(Skill.FISHING.getId()) - spotType.getLevels()[0]);
		chance += random.nextInt(7);
		chance /= 100;
		
		if(chance > 0.95) {
			chance = 0.95;
		}
		if(chance < 0.05) {
			chance = 0.05;
		}
		
		return (random.nextDouble() <= chance);
	}
	
	private void stopFishing() {
		this.stop();
		this.getMob().playAnimation(Animation.RESET);
	}
	
	@Override
	public void execute() {
		Mob mob = this.getMob();
		int fishingLevel = mob.getSkills().getLevel(Skill.FISHING);
		
		if(spot == null) {
			stopFishing();
			return;
		}
		if(spot.isDead() || spot.isDestroyed()) {
			if(mob instanceof Player) {
				((Player) mob).getPacketSender().sendMessage("It seems that the fish have left this area.");
			}
			stopFishing();
			return;
		}
		
		if(!started) {
			if(mob instanceof Player) {
				Player player = (Player) mob;
	
				if(fishingLevel < spotType.getLevels()[0]) {
					player.getPacketSender().sendMessage("You need a Fishing level of at least " + spotType.getLevels()[0] + " to fish at this spot.");
					stopFishing();
					return;
				}
				
				if(!player.getInventory().contains(spotType.getTool())) {
					player.getPacketSender().sendMessage("You need a " + ItemDefinition.forId(spotType.getTool()).getName().toLowerCase() + " to fish here.");
					stopFishing();
					return;
				}
				
				if(spotType.getBait() != null && !player.getInventory().contains(spotType.getBait().getId())) {
					player.getPacketSender().sendMessage("You need some bait to fish here.");
					stopFishing();
					return;
				}
				
				if(player.getInventory().freeSlots() < 1) {
					player.setCurrentDialogue(Dialogue.NOT_ENOUGH_SPACE);
					stopFishing();
					return;
				}
				
				mob.playAnimation(Animation.create(spotType.getAnimation()));
				
				String msg = spotType.getStartMessage();
				if(msg != null) {
					player.getPacketSender().sendMessage(msg);
				}
			} else {
				mob.playAnimation(Animation.create(spotType.getAnimation()));
			}
			
			started = true;
			this.setDelay(3000);
			return;
		}

		if(mob instanceof Player) {
			Player player = (Player) mob;

			if(fishingLevel < spotType.getLevels()[0]) {
				stopFishing();
				return;
			}
			
			if(!player.getInventory().contains(spotType.getTool())) {
				stopFishing();
				return;
			}
			
			if(spotType.getBait() != null && !player.getInventory().contains(spotType.getBait().getId())) {
				player.getPacketSender().sendMessage("You have run out of bait.");
				stopFishing();
				return;
			}
			
			if(player.getInventory().freeSlots() < 1) {
				player.setCurrentDialogue(Dialogue.NOT_ENOUGH_SPACE);
				stopFishing();
				return;
			}
		}
		
		mob.playAnimation(Animation.create(spotType.getAnimation()));
		if(canGetFish()) {
			int maxIndex = 0;
			for(final int level : spotType.getLevels()) {
				if(mob.getSkills().getRealLevel(Skill.FISHING) >= level) {
					maxIndex++;
				}
			}
			
			int index = random.nextInt(maxIndex);
			Item fish = spotType.getFish()[index];
			
			if(mob instanceof Player) {
				((Player) mob).getInventory().add(fish);
			}
			
			mob.getSkills().addSkillXP(spotType.getExperience()[index], Skill.FISHING);

			if(mob instanceof Player) {
				int fishId = fish.getId();
				String fishName = fish.getDefinition().getName().toLowerCase().replace("raw ", "");
				
				((Player) mob).getPacketSender().sendMessage("You catch " + (fishId == 321 || fishId == 317 ? "some" : "a") + " " + fishName + ".");
				
				if(spotType.getBait() != null) {
					((Player) mob).getInventory().remove(spotType.getBait());
				}
			}
			
			spot.getSkills().detractLevel(Skill.HITPOINTS, 1);
		}
	}

}
