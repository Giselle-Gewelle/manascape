package com.runescape.gameserver.world.content.skills.impl;

import java.util.HashMap;
import java.util.Map;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.util.NameUtils;
import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.content.dialogue.ChatInterfaceAction;
import com.runescape.gameserver.world.content.dialogue.Dialogue;
import com.runescape.gameserver.world.content.dialogue.Dialogue.DialogueType;
import com.runescape.gameserver.world.content.dialogue.sequence.ItemDialogueSequence;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.entity.Animation;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class Fletching {

	public enum KnifeItem {
		
		ARROWSHAFTS(new Item(52, 15), 5, 1),
		SHORTBOW(new Item(50, 1), 5, 5),
		LONGBOW(new Item(48, 1), 10, 10),
		OAK_SHORTBOW(new Item(54, 1), 16.5, 20),
		OAK_LONGBOW(new Item(56, 1), 25, 25);
		
		private Item item;
		private double expGained;
		private int requiredLevel;
		
		private KnifeItem(Item item, double expGained, int requiredLevel) {
			this.item = item;
			this.expGained = expGained;
			this.requiredLevel = requiredLevel;
		}
		
		public Item getItem() {
			return item;
		}
		
		public double getExpGained() {
			return expGained;
		}
		
		public int getRequiredLevel() {
			return requiredLevel;
		}
		
	}
	
	public enum KnifeAction {
		
		NORMAL(
			1511, 
			new KnifeItem[] {
				KnifeItem.ARROWSHAFTS, KnifeItem.SHORTBOW, KnifeItem.LONGBOW
			}
		),
		OAK(
			1521,
			new KnifeItem[] {
				KnifeItem.OAK_SHORTBOW, KnifeItem.OAK_LONGBOW
			}
		);
		
		private static Map<Integer, KnifeAction> logs = new HashMap<Integer, KnifeAction>();
		
		public static KnifeAction forLogId(int logId) {
			return logs.get(logId);
		}
		
		static {
			for(KnifeAction log : KnifeAction.values()) {
				logs.put(log.logId, log);
			}
		}
		
		private int logId;
		private KnifeItem[] returnedItems;
		
		private KnifeAction(int logId, KnifeItem[] returnedItems) {
			this.logId = logId;
			this.returnedItems = returnedItems;
		}
		
		public int getLogId() {
			return logId;
		}
		
		public KnifeItem[] getReturnedItems() {
			return returnedItems;
		}
		
	}
	
	public static final Item KNIFE = new Item(946, 1);
	private static final Animation KNIFE_ANIM = Animation.create(1248);
	private static final int[] AMOUNTS = new int[] { 
		1, 5, 10, -1 
	};
	
	private Mob entity;
	
	public Fletching(Mob entity) {
		this.entity = entity;
	}
	
	public void handleKnifeAction(final KnifeAction action) {
		if(entity instanceof Player) {
			Player player = (Player) entity;
			
			KnifeItem[] returnedItems = action.getReturnedItems();
			int possibleItems = returnedItems.length;
			int[] returnedItemIds = new int[possibleItems];
			for(int i = 0; i < possibleItems; i++) {
				returnedItemIds[i] = returnedItems[i].getItem().getId();
			}
			
			player.setCurrentDialogue(new ItemDialogueSequence(player, returnedItemIds, new ChatInterfaceAction() {

				@Override
				public void execute(Player player, int id, int itemIndex) {
					int cycleCount = AMOUNTS[id];
					KnifeItem knifeItem = action.getReturnedItems()[itemIndex];
					performKnifeAction(action, knifeItem, cycleCount + 1);
				}
				
			}));
		}
	}
	
	private void performKnifeAction(final KnifeAction action, final KnifeItem item, final int cycleCount) {
		if(entity instanceof Player) {
			final Player player = (Player) entity;
			final Item removeItem = new Item(action.getLogId(), 1);
			
			player.getActionQueue().addAction(new Action(entity, 0) {
				
				private int cyclesRemaining = cycleCount;
				
				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.NEVER;
				}

				@Override
				public WalkablePolicy getWalkablePolicy() {
					return WalkablePolicy.WALKABLE;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public void execute() {

					if(cyclesRemaining <= 0) {
						this.stop();
						entity.playAnimation(Animation.RESET);
						return;
					}
					
					if(this.getDelay() == 0) {
						this.setDelay(cycleCount <= 2 ? 600 : 2200);
					}
					
					int itemId = removeItem.getId();
					String itemName = item.getItem().getDefinition().getName().replace(" (u)", "");
					String removeItemName = removeItem.getDefinition().getName().toLowerCase();
					String a = "a";
					if(NameUtils.startsWithVowel(itemName)) {
						a += "n";
					}
					
					if(!player.getInventory().contains(itemId)) {
						this.stop();
						entity.playAnimation(Animation.RESET);
						player.getPacketSender().sendMessage("You have run out of " + removeItemName + ".");
						return;
					}

					int requiredLevel = item.requiredLevel;
					int currentLevel = player.getSkills().getLevel(Skill.FLETCHING);
					if(currentLevel < requiredLevel) {
						this.stop();
						entity.playAnimation(Animation.RESET);
						player.setCurrentDialogue(
							new Dialogue(
								new String[] { "You need a <col=" + Constants.COLOUR_BLUE + ">Fletching skill of " + requiredLevel + " or above</col> to make " + a + " <col=" + Constants.COLOUR_BLUE + ">" + itemName + "</col>." },
								DialogueType.STATEMENT
							)
						);
						return;
					}
					
					if(cyclesRemaining == cycleCount || cycleCount > 2) {
						player.playAnimation(KNIFE_ANIM);
					}
					if(cyclesRemaining < cycleCount) {
						player.getInventory().remove(removeItem);
						player.getInventory().add(item.getItem());
						player.getSkills().addSkillXP(item.getExpGained(), Skill.FLETCHING);
						player.getPacketSender().sendMessage("You carefully cut the wood into " + (item == KnifeItem.ARROWSHAFTS ? "15 arrow shafts" : a + " " + itemName.toLowerCase()) + ".");
					}
					
					cyclesRemaining--;

					if(cyclesRemaining <= 0) {
						this.setDelay(600 * 2);
					}
					
				}
				
			});
		}
	}
	
}
