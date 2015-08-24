package com.runescape.gameserver.world.entity.action.impl;

import java.util.HashMap;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.ItemDefinition;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class Eating extends Action {

	public Eating(Mob entity) {
		super(entity, 0);
		this.player = (Player)entity;
	}
	
	Player player;
	
	Food f;
	
	int healAmount, slot;
	
	String itemName;
	

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.ALWAYS;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.WALKABLE;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}
	
	Animation EAT = Animation.create(829);
	
	public boolean eat(int item, int slotid) {		
		f = Food.forId(item);
		
		if (f == null)
			return false;
		
		healAmount = (f == null) ? 2 : f.getHealAmount();
		itemName = ItemDefinition.forId(item).getName();
		slot = slotid;
		if (!player.getInventory().contains(item))
			return false;
		
		this.execute();
		return true;
	}
	@Override
	public void execute() {
		if (player.isDead() || player.getSkills().getLevel(Skill.HITPOINTS) <= 0)
			return;
		
		if (System.currentTimeMillis() - player.eatingDelay >= 1800) {
			player.getInventory().remove(slot, new Item(f.getItemId()));
			player.playAnimation(EAT);
			player.getSkills().raiseLevel(Skill.HITPOINTS, f.healAmount);
			player.getPacketSender().sendMessage("You eat the " + itemName + ".");
		}
	}
	
	public static enum Food {
		MANTA(391, 22), SHARK(385, 20), LOBSTER(379, 12), TROUT(333, 7),
		SALMON(329, 9), SWORDFISH(373, 14), TUNA(361, 10), ROCKTAIL(15272, 23),
		CAVEFISH(15266, 20), MONKFISH(7946, 16), SEA_TURTLE(397, 21),
		CAKE(1891, 4), BASS(365, 13), COD(339, 7), POTATO(1942, 1), BAKED_POTATO(6701, 4),
		POTATO_WITH_CHEESE(6705, 16), EGG_POTATO(7056, 16), CHILLI_POTATO(7054, 14), 
		MUSHROOM_POTATO(7058, 20), TUNA_POTATO(7060, 22), SHRIMPS(315, 3), HERRING(347, 5),
		SARDINE(325, 4), PURPLE_SWEETS(10476, 3), CHOCOLATE_CAKE(1897, 5), ANCHOVIES(319, 1), 
		PLAIN_PIZZA(2289, 7), MEAT_PIZZA(2293, 8), ANCHOVY_PIZZA(2297, 9),
		PINEAPPLE_PIZZA(2301, 11), BREAD(2309, 5), APPLE_PIE(2323, 7), REDBERRY_PIE(2325, 5),
		MEAT_PIE(2327, 6), PIKE(351, 8), POTATO_WITH_BUTTER(6703, 14), BANANA(1963, 2),
		PEACH(6883, 8), ORANGE(2108, 2), PINEAPPLE_RINGS(2118, 2), CAVE_MORAY(18177, 21),
		MORAY_AND_EDICAP_POTATO(18157, 31), PINEAPPLE_CHUNKS(2116,2), RED_BANANA(7572, 22),
		BANDAGES(4049, 20), WEB_SNIPPER(18169, 15), BOULDABASS(18171, 17), BLUE_CRAB(18175, 22),
		DUSK_EEL(18163, 7), GIANT_FLATFISH(18165, 10), SHORT_FINNED_EEL(18167, 12), RED_EYE(18161, 5),
		HEIM_CRAB(18159, 2), SALVE_EEL(18173, 20),GIANT_FROG_LEGS(4517, 6),
		CABBAGE(1965,1),CABBAGE_2(1967,1),ONION(1957, 1);

		private final int itemId;
		private final int healAmount;

		private Food(int id, int heal) {
			this.itemId = id;
			this.healAmount = heal;
		}

		public int getItemId() {
			return itemId;
		}

		public int getHealAmount() {
			return healAmount;
		}

		public static HashMap<Integer, Food> food = new HashMap<Integer, Food>();

		public static Food forId(int id) {
			return food.get(id);
		}
		
		public static boolean isFood(int id) {
			return food.containsKey(id);
		}

		static {
			for (final Food f : Food.values()) {
				food.put(f.getItemId(), f);
			}
		}
	}

}
