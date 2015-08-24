package com.runescape.gameserver.world.entity.mob.npc.impl;

import com.runescape.gameserver.world.Item;
import com.runescape.gameserver.world.container.Container;
import com.runescape.gameserver.world.container.Equipment;
import com.runescape.gameserver.world.content.dialogue.ChatAnimation;
import com.runescape.gameserver.world.content.dialogue.Dialogue;
import com.runescape.gameserver.world.content.dialogue.Dialogue.DialogueType;
import com.runescape.gameserver.world.content.dialogue.DialogueSequence;
import com.runescape.gameserver.world.content.dialogue.sequence.BasicDialogueSequence;
import com.runescape.gameserver.world.content.dialogue.sequence.OptionDialogueSequence;
import com.runescape.gameserver.world.content.faction.GodFaction;
import com.runescape.gameserver.world.content.skills.Skills;
import com.runescape.gameserver.world.content.skills.Skills.Skill;
import com.runescape.gameserver.world.definitions.WeaponDefinition;
import com.runescape.gameserver.world.entity.action.impl.CoordinateAction;
import com.runescape.gameserver.world.entity.action.impl.DialogueAction;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class CommanderAdriea extends NPC {

	public CommanderAdriea() {
		super();
		super.setDynamic(true);
		super.setAction(0, "Talk-to");
		super.setName("Commander Adriea");
		super.setDescription("A Temple Knight Commander in the service of Saradomin.");
	}
	
	@Override
	public void setAppearance() {
		int[] look = getAppearance().getLook();
		look[0] = 1;
		look[1] = 5;
		look[2] = 5;
		look[3] = 6;
		look[4] = 2;
		look[5] = 1;
		look[6] = 47;
		look[7] = 56;
		look[8] = 63;
		look[9] = 67;
		look[10] = 71;
		look[11] = 80;
		look[12] = -1;
		getAppearance().setLook(look);
	}
	
	@Override
	public void setSkills() {
		Skills skills = getSkills();
		
		skills.setCombatLevel(126);
		skills.setLevels(Skill.HITPOINTS, 99);
		skills.setLevels(Skill.STRENGTH, 99);
	}
	
	@Override
	public void setEquipment() {
		Container equipment = getEquipment();
		
		int[][] equips = new int[][] {
			// White 2h Sword
			{ Equipment.SLOT_WEAPON, 6609 },
			// TK Commander Platebody
			{ Equipment.SLOT_CHEST, 7966 },
			// White Boots
			{ Equipment.SLOT_BOOTS, 6619 },
			// TK Commander Platelegs
			{ Equipment.SLOT_BOTTOMS, 7967 },
			// White Gloves
			{ Equipment.SLOT_GLOVES, 6629 },
			// Saradomin Cape
			{ Equipment.SLOT_CAPE, 2412 },
			// White Sq Shield
			{ Equipment.SLOT_SHIELD, 6631 }
		};
		for(int[] equip : equips) {
			equipment.set(equip[0], new Item(equip[1], 1));
		}
		
		setEquippedWeapon(WeaponDefinition.forId(6609));
	}
	
	@Override
	public boolean handleSecondClick(Player player) {
		GodFaction faction = player.getFactions().getGodFaction();
		if(faction == null) {
			/*
			 * Initial Chat
			 */
			DialogueSequence sequence = new BasicDialogueSequence(player);
			((BasicDialogueSequence) sequence).addDialogue(new Dialogue(
				this, 
				new String[] {
					"Hello there, would you like to join us and fight for", 
					"lord of order and justice, <col=000080>Saradomin</col>?"
				},
				ChatAnimation.CALM_TALK
			));
			((BasicDialogueSequence) sequence).addDialogue(new Dialogue(
				new String[] {
					"I'm not sure really... Why should I choose to", 
					"fight for <col=000080>Saradomin</col> instead of anyone else?"
				},
				ChatAnimation.THINKING
			));
			((BasicDialogueSequence) sequence).addDialogue(new Dialogue(
				this,
				new String[] {
					"Well, for starters, <col=000080>Saradomin</col> is the God of", 
					"order and wisdom. So if you're into that, lord <col=000080>Saradomin</col>",
					"is certainly the God for you! That being said, there is", 
					"far too much to be told about <col=000080>Saradomin</col> for me to"
				},
				ChatAnimation.CALM_TALK
			));
			((BasicDialogueSequence) sequence).addDialogue(new Dialogue(
				this,
				new String[] {
					"go through all of it with you right now, what with this", 
					"war going on and everything. If you'd like to know",
					"anything else, feel free to ask me a question or two!"
				},
				ChatAnimation.CALM_TALK
			));
			
			/*
			 * Initial Option Dialogues
			 */
			BasicDialogueSequence option1 = new BasicDialogueSequence(player);
			option1.addDialogue(new Dialogue(
				this,
				new String[] {
					"<col=000080>Saradomin</col> is the holy God of order and", 
					"wisdom. He stands for everything that is holy and", 
					"good, looking out for everyone on Gielinor."
				},
				ChatAnimation.CALM_TALK
			));
			option1.addDialogue(new Dialogue(
				this,
				new String[] {
					"Do you have any other questions?"
				},
				ChatAnimation.CALM_TALK
			));
			
			BasicDialogueSequence option2 = new BasicDialogueSequence(player);
			option2.addDialogue(new Dialogue(
				this,
				new String[] {
					"To join us and fight for <col=000080>Saradomin</col>, all", 
					"you have to do is ask! Would you like to join",
					"<col=000080>Saradomin's</col> faction and fight for <col=000080>order</col>?"
				},
				ChatAnimation.CALM_TALK
			));
			
			BasicDialogueSequence join1 = new BasicDialogueSequence(player);
			join1.addDialogue(new Dialogue(
				this,
				new String[] {
					"Well then, you've certainly made the correct choice!", 
					"Head on over to Captain Zeike and we'll see about",
					"getting you outfitted with some gear."
				},
				ChatAnimation.HAPPY
			));
			DialogueSequence joinSequence = new OptionDialogueSequence(
				player, new Dialogue(
					new String[] {
						"Yes, I would like to join <col=000080>Saradomin</col>.",
						"No thanks."
					},
					DialogueType.OPTIONS
				), new DialogueSequence[] {
					join1, null
				}
			);
			
			DialogueSequence optionSequence = new OptionDialogueSequence(
				player, new Dialogue(
					new String[] {
						"What does <col=000080>Saradomin</col> stand for?",
						"How can I join <col=000080>Saradomin's</col> forces?"
					},
					DialogueType.OPTIONS
				), new DialogueSequence[] {
					option1, option2
				}
			);

			option1.setNextSequence(optionSequence);
			option2.setNextSequence(joinSequence);
			sequence.setNextSequence(optionSequence);
			
			player.getActionQueue().addAction(
				new CoordinateAction(
					player, this.getLocation(), 1, new DialogueAction(player, sequence)
				)
			);
		} else if(faction == GodFaction.SARADOMIN) {
			
		}
		
		return true;
	}

}
