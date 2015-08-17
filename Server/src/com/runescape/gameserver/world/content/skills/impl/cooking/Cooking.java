package com.runescape.gameserver.world.content.skills.impl.cooking;

import java.security.SecureRandom;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.player.Player;
import com.runescape.gameserver.world.entity.object.GameObject;

public class Cooking {

	public Cooking(Player player) {
			this.player = player;	
	}
	
	int xAmount;
	
	public void setXAmount(int amount) {
		this.xAmount = amount;
	}
	
	Player player;
	
	CookingItem item = null;
	
	public CookingItem getItem() {
		return item;
	}
	
	static int buttonId;
	
	boolean fire = false;
	
	public enum CookingItem {
		
		BEEF_MEAT(2132, 2142, 2146, 1, 30, 7, true, false),
		RAT_MEAT(2134, 2142, 2146, 1, 30, 7, true, false),
		BEAR_MEAT(2136, 2142, 2146, 1, 30, 7, true, false),
		CHICKEN(2138, 2140, 2144, 1, 30, 7, true, false),
		RABBIT(3226, 3228, 7222, 1, 30, 7, true, false),
		UTHANKI(1859, 1861, 323, 1, 40, 7, true, false),
		RED_BERRY_PIE(2321, 2325, 2329, 10, 78, 15, false, true),
		MEAT_PIE(2319, 2327, 2329, 20, 110, 25, false, true),
		MUD_PIE(7168, 7170, 2329, 29, 128, 35, false, true),
		APPLE_PIE(2317, 2323, 2329, 30, 180, 35, false, true),
		GARDEN_PIE(7186, 7188, 2329, 47, 164, 52, false, true),//TODO: GET CORRECT CONFIGS
		FISH_PIE(7186, 7188, 2329, 47, 164, 52, false, true),
		ADMIRIAL_PIE(7196, 1798, 2329, 70, 210, 77, false, true),
		WILD_PIE(7206, 7208, 2329, 85, 140, 90, false, true),
		SUMMER_PIE(7216, 7218, 2329, 95, 260, 100, false, true),
		PIZZA(2287, 2289, 2305, 35, 143, 38, true, true),
		CAKE(1889, 1891, 1903, 40, 180, 38, false, true),
		BREAD(2307, 2309, 2311, 1, 40, 5, false, true),
		PIITA_BREAD(1863, 1865, 1867, 58, 40, 65, true, true),
		SHRIMP(317, 315, 323, 1, 30, 34, true, false),
		SARDINE(327, 325, 369, 1, 40, 38, true, false),
		ANCHOVIES(321, 319, 323, 1, 30, 34, true, false),
		HERRING(345, 347, 357, 5, 50, 37, true, false),
		MACKEREL(353, 355, 357, 10, 60, 35, true, false),
		TROUT(335, 333, 343, 15, 70, 50, true, false),
		COD(341, 339, 343, 17, 75, 39, true, false),
		PIKE(349, 351, 343, 20, 80, 52, true, false),
		SALMON(331, 329, 343, 25, 90, 58, true, false),
		TUNA(359, 361, 367, 30, 100, 65, true, false),
		LOBSTER(377, 379, 381, 40, 120, 74, true, false),
		BASS(363, 365, 367, 43, 130, 80, true, false),
		SWORDFISH(371, 373, 375, 45, 140, 86, true, false),
		SHARK(383, 385, 387, 80, 210, 104, true, false),
		MANTA_RAY(389, 391, 393, 91, 216, 112, true, false),
		;
		
		private int rawItem, cookedItem, burntItem;
		private int levelReq, stopBurning;
		private double expGiven;
		boolean canCookOnFire, bakes;
		CookingItem(int rawItem, int cookedItem, int burntItem, int levelReq, double expGiven, int stopBurning, boolean canCookOnFire, boolean bakes) {
			this.rawItem = rawItem;
			this.cookedItem = cookedItem;
			this.burntItem = burntItem;
			this.levelReq = levelReq;
			this.expGiven = expGiven;
			this.stopBurning = stopBurning;
			this.canCookOnFire = canCookOnFire;
			this.bakes = bakes;
		}
		
		public Item getRawItem() {
			return new Item(rawItem);
		}

		public Item getCookedItem() {
			return new Item(cookedItem);
		}
		
		public Item getBurntItem() {
			return new Item(burntItem);
		}

		public int getLevelReq() {
			return levelReq;
		}

		public double getExpGiven() {
			return expGiven;
		}

		public int getStopBuring() {
			return stopBurning;
		}
		public boolean canCookOnFire() {
			return canCookOnFire;
		}

		public static CookingItem forItem(Item item) {
			for (CookingItem c : values()) 
				if (c.getRawItem().getId() == item.getId())
					return c;
			return null;
		}
		
		public static boolean isAPie(CookingItem item) {
			return (item == CookingItem.ADMIRIAL_PIE || item == CookingItem.APPLE_PIE || item == CookingItem.GARDEN_PIE
					|| item == CookingItem.FISH_PIE || item == CookingItem.MEAT_PIE || item == CookingItem.MUD_PIE || 
					item == CookingItem.RED_BERRY_PIE || item == CookingItem.WILD_PIE || item == CookingItem.SUMMER_PIE);
		}
	}
	
	public static boolean isACookingButton(int button) {
		boolean bol = (button == 13720 || button == 13719
				|| button == 13718 || button == 13717);
		if (bol)
			buttonId = button;
		return bol;
	}
	
	public void sendCookingInterface(CookingItem item, GameObject o) {
		if (o.getDefinition().getName().contains("fire")) {
			if (!item.canCookOnFire()) {
				player.getPacketSender().sendMessage("You cannot cook this on a fire!");
				return;
			}
			this.fire = true;
		}
		this.item = item;
		Item cookedItem = item.getCookedItem();
		player.getPacketSender().sendInterfaceModel(13716, 150, cookedItem.getId());
		player.getPacketSender().sendString(13720, cookedItem.getDefinition().getName());
		player.getPacketSender().sendChatBoxInterface(1743);
	}
	
	SecureRandom cookingRandom = new SecureRandom();
	private boolean successfullyCooked(Player p, int burnBonus, int levelReq, int stopBurn) {
		if (p.getSkills().getLevel(Skill.COOKING) >= stopBurn) {
			return true;
		}
		double burn_chance = (55.0 - burnBonus);
		double cook_level = p.getSkills().getLevel(Skill.COOKING);
		double lev_needed = levelReq;
		double burn_stop = stopBurn;
		double multi_a = (burn_stop - lev_needed);
		double burn_dec = (burn_chance / multi_a);
		double multi_b = (cook_level - lev_needed);
		burn_chance -= (multi_b * burn_dec);
		double randNum = cookingRandom.nextDouble() * 100.0;
		if (fire) {
			burn_chance += 3;
		}
		return burn_chance <= randNum;
	}
	
	public boolean cookItem(final int count) {
		player.getPacketSender().sendCloseInterfaces();
		if (player.getSkills().getLevel(Skill.COOKING) >= item.getLevelReq()) {
			if (player.getInventory().contains(item.getRawItem().getId())) {
				player.getActionQueue().addAction(new Action(player, 0) {
					
					private int cycles = count;
					
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
						Animation anim = Animation.create(0);
						if (fire)
							anim = Animation.create(897);
						else
							anim = Animation.create(896);
						
						if(this.getDelay() == 0) {
							this.setDelay(3000);
						}
						
						player.playAnimation(anim);

						if (!player.getInventory().contains(item.getRawItem().getId())) {
							player.getPacketSender().sendMessage("You have run out of " + item.getRawItem().getDefinition().getName() + ".");
							this.stop();
							player.playAnimation(Animation.RESET);
							return;
						}
						
						if(cycles <= 0) {
							this.stop();
							player.playAnimation(Animation.RESET);
							return;
						}
						player.getInventory().remove(item.getRawItem());
						if (successfullyCooked(player, 5, item.getLevelReq(), item.getStopBuring())) {
							player.getInventory().add(item.getCookedItem());
							player.getSkills().addSkillXP(item.getExpGiven(), Skill.COOKING);
							if (CookingItem.isAPie(item))
								player.getInventory().add(new Item("pie dish"));
							if (item == CookingItem.CAKE)
								player.getInventory().add(new Item("cake tin"));
							String itemName = item.getRawItem().getDefinition().getName();
							String message = "You successfully cook the " + itemName;
							if (item.bakes)
								message = "You successfully bake the " + itemName + ".";
							player.getPacketSender().sendMessage(message);
						} else {
							player.getInventory().add(item.getBurntItem());
							player.getPacketSender().sendMessage("You accidentally burn the " + item.getRawItem().getDefinition().getName() + ".");
						}
						cycles--;
					}
					
				});
				return true;
			}
		} else
			player.getPacketSender().sendMessage("You need a Cooking level of " + item.getLevelReq() + " to cook this.");	
		return false;
	}
}
