package com.runescape.gameserver.world.entity.mob.npc.impl;

import com.runescape.gameserver.world.content.dialogue.*;
import com.runescape.gameserver.world.content.dialogue.Dialogue.DialogueType;
import com.runescape.gameserver.world.content.dialogue.sequence.*;
import com.runescape.gameserver.world.content.faction.GodFaction;
import com.runescape.gameserver.world.content.quest.Quest;
import com.runescape.gameserver.world.content.quest.impl.DivineIntervention;
import com.runescape.gameserver.world.definitions.CacheNPCDefinition;
import com.runescape.gameserver.world.entity.action.impl.CoordinateAction;
import com.runescape.gameserver.world.entity.action.impl.DialogueAction;
import com.runescape.gameserver.world.entity.mob.npc.NPC;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class LumbridgeGuide extends NPC {

	public LumbridgeGuide(CacheNPCDefinition def) {
		super(def);
	}
	
	@Override
	public boolean handleSecondClick(Player player) {
		BasicDialogueSequence seq = new BasicDialogueSequence(player);
		seq.addDialogue(new Dialogue(
			this,
			new String[] {
				"Hello there adventurer, what can I help you with?"
			},
			ChatAnimation.HAPPY
		));
		
		BasicDialogueSequence opt1 = new BasicDialogueSequence(player);
		opt1.addDialogue(new Dialogue(
			this,
			new String[] {
				"What can you get money? Ha! How about..."
			},
			ChatAnimation.LAUGH_1
		));
		opt1.addDialogue(new Dialogue(
			this,
			new String[] {
				"Cooking, cleaning, mercenary for hire, slayer, mining,",
				"smithing, woodcutting, becoming a tramp, adventuring,",
				"stealing, herblore, crafting, fletching..."
			},
			ChatAnimation.CALM_TALK
		));
		opt1.addDialogue(new Dialogue(
			new String[] {
				"Alright I get it, I get it."
			},
			ChatAnimation.ANGER_1
		));
		opt1.addDialogue(new Dialogue(
			this,
			new String[] {
				"Well, you did ask! Now, is there",
				"anything else I can help you with?"
			},
			ChatAnimation.THINKING
		));
		
		BasicDialogueSequence opt2 = new BasicDialogueSequence(player);
		opt2.addDialogue(new Dialogue(
			this,
			new String[] {
				"Things to do around Lumbridge... Well, there is a rather", 
				"large battle going on just east of here, behind the", 
				"castle, that one may choose to take part in. Other than",
				"that, there are planty of other things!"
			},
			ChatAnimation.THINKING
		));
		opt2.addDialogue(new Dialogue(
			this,
			new String[] {
				"There are plenty of fishing spots to the north-west along",
				"the River Lum. Plenty of starter monsters to train on,",
				"such as Cows, Chickens, and Goblins. Though I don't",
				"suggest going around and murdering the townsfolk,"
			},
			ChatAnimation.CALM
		));
		opt2.addDialogue(new Dialogue(
			this,
			new String[] {
				"as I don't think the guards would like that very much!"
			},
			ChatAnimation.LAUGH_2
		));
		opt2.addDialogue(new Dialogue(
			new String[] {
				"What do you mean by that?"
			},
			ChatAnimation.THINKING
		));
		opt2.addDialogue(new Dialogue(
			this,
			new String[] {
				"Well obviously if you go around murdering people in",
				"town, the guards there are going to come after you."
			},
			ChatAnimation.CALM_TALK
		));
		opt2.addDialogue(new Dialogue(
			new String[] {
				"Wait, really? I don't remember them ever",
				"doing that in..."
			},
			ChatAnimation.THINKING
		));
		opt2.addDialogue(new Dialogue(
			this,
			new String[] {
				"RuneScape? Well, this isn't RuneScape my " + (player.getAppearance().getGender() == 0 ? "boy" : "girl") + "!"
			},
			ChatAnimation.CALM_TALK
		));
		opt2.addDialogue(new Dialogue(
			new String[] {
				"Well, I guess that's true."
			},
			ChatAnimation.CALM
		));
		opt2.addDialogue(new Dialogue(
			this,
			new String[] {
				"Sure is, keep that in mind. If you go around attacking",
				"people in town, you will get a bounty in that town and",
				"the guards will most likely attack you on sight, depending",
				"on how high your bounty is..."
			},
			ChatAnimation.THINKING
		));
		opt2.addDialogue(new Dialogue(
			new String[] {
				"Thanks, I'll keep that in mind."
			},
			ChatAnimation.CALM
		));
		opt2.addDialogue(new Dialogue(
			this,
			new String[] {
				"Is there anything else I can help you with?"
			},
			ChatAnimation.THINKING
		));
		
		QuestDialogueSequence opt3 = null;
		
		String[] options = new String[] {
			"Where can I get money?",
			"What can I do around here?",
			"I'm fine, thanks."
		};
		Quest di = player.getQuestHandler().forId(DivineIntervention.ID);
		if(di.getStage() == 0) {
			options = new String[] {
				"Where can I get money?",
				"What can I do around here?",
				"The Gods are back? What's happening?!",
				"I'm fine, thanks."
			};
			
			opt3 = new QuestDialogueSequence(player, DivineIntervention.ID);
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					"Well, I'm not sure quite how it happened, but it seems",
					"that " + GodFaction.GUTHIX.colouredString("Guthix's") + " edicts have been broken, allowing the", 
					"Gods to return to this world."
				},
				ChatAnimation.THINKING
			));
			opt3.addDialogue(new Dialogue(
				new String[] {
					"But, what's happened to " + GodFaction.GUTHIX.colouredName() + "? Can't he just create",
					"new edicts and banish the Gods again?"
				},
				ChatAnimation.SAD
			));
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					"There are some things I just don't know, " + player.getName() + "."
				},
				ChatAnimation.THINKING
			));
			opt3.addDialogue(new Dialogue(
				new String[] {
					"Well, is there anything I can do?"
				},
				ChatAnimation.CALM
			));
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					"Well, if you're interested, you can choose to join up",
					"with a God and assist them in the God war. Those",
					"wishing to join up with " + GodFaction.GUTHIX.colouredName() + " may speak with " + GodFaction.GUTHIX.colouredString("Juna"),
					"over in Taverly. If " + GodFaction.SARADOMIN.colouredName() + " is your"
				},
				ChatAnimation.THINKING
			));
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					"God of choice, then you can find " + GodFaction.SARADOMIN.colouredString("Sir Tiffy Cashien"),
					"in the park in the middle of Falador. Worshippers",
					"of " + GodFaction.ZAMORAK.colouredName() + " should go and see " + GodFaction.ZAMORAK.colouredString("Lord Daquarius"), 
					"at the Black Knights' fortress, to the west of Edgeville."
				},
				ChatAnimation.THINKING
			));
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					"Then there's also " + GodFaction.ARMADYL.colouredName() + ", " + GodFaction.BANDOS.colouredName() + ",",
					"and " + GodFaction.ZAROS.colouredName() + "."
				},
				ChatAnimation.THINKING
			));
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					GodFaction.ARMADYL.colouredName() + " and " + GodFaction.ZAROS.colouredName() + " have yet to show",
					"up to battle, so I'm not sure if there would be any way",
					"to fight for them, or if they would even want you to.", 
					"As for " + GodFaction.BANDOS.colouredName() + ", should you choose to"
				},
				ChatAnimation.CALM
			));
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					"side with him, you can find his dim-wittedly loyal",
					"followers, " + GodFaction.BANDOS.colouredString("General Wartface") + " and " + GodFaction.BANDOS.colouredString("General Bentnoze"),
					"at Goblin Village just north of Falador."
				},
				ChatAnimation.CALM
			));
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					"Though it seems only goblins and ogres are stupid",
					"enough to follow him..."
				},
				ChatAnimation.THINKING
			));
			opt3.addDialogue(new Dialogue(
				new String[] {
					"Thanks. I suppose I'll decide who to join, then",
					"pay their commander a visit..."
				},
				ChatAnimation.CALM
			));
			opt3.addDialogue(new Dialogue(
				this,
				new String[] {
					"Good luck to you, " + player.getName() + ".",
					"Is there anything else I can help you with?"
				},
				ChatAnimation.CALM_TALK
			));
		}
		
		OptionDialogueSequence opt = new OptionDialogueSequence(
			player, 
			new Dialogue(
				options,
				DialogueType.OPTIONS
			),
			new DialogueSequence[] { opt1, opt2, opt3, null }
		);
		
		seq.setNextSequence(opt);
		opt1.setNextSequence(opt);
		opt2.setNextSequence(opt);
		opt3.setNextSequence(opt);
		
		player.getActionQueue().addAction(
			new CoordinateAction(
				player, this.getLocation(), 1, new DialogueAction(player, seq)
			)
		);
		
		return true;
	}

}
