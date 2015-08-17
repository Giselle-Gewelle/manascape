package com.runescape.gameserver.world.content.quest.impl;

import com.runescape.gameserver.Constants;
import com.runescape.gameserver.world.content.quest.Quest;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class DivineIntervention extends Quest {

	public static final int ID = 19042;
	private static final String NAME = "Divine Intervention";
	
	public DivineIntervention(Player player) {
		super(player, ID, NAME);
	}

	@Override
	public void updateQuestLog() {
		switch(stage) {
			default:
			case Quest.STAGE_NOT_STARTED:
				log("With the God war currently raging, I may be forced to join a");
				log("side soon... Maybe I can ask the <col=" + Constants.COLOUR_BLUE + ">Lumbridge Guide</col> for help.");
				break;
			
			case 1:
				log("The <col=" + Constants.COLOUR_BLUE + ">Lumbridge Guide</col> told me that Guthix's edicts have been");
				log("broken and the Gods are returning to our world once more. He");
				log("said that I should probably choose a side and try to help a");
				log("God win the war and put a stop to all of this.");
				
				log("");
				
				log("<col=" + Constants.COLOUR_BLUE + ">Armadyl</col> and <col=" + Constants.COLOUR_BLUE + ">Zaros</col> have yet to show themselves, but to join");
				log("up with (or just get more information on) any of the other main");
				log("Gods that are battling, I should visit their respective");
				log("commanders:");
				
				log("");
				
				log("<col=" + Constants.COLOUR_BLUE + ">Guthix:</col>");
				log("To get more information about <col=" + Constants.COLOUR_BLUE + ">Guthix</col>, I can speak to <col=" + Constants.COLOUR_BLUE + ">Juna</col> who");
				log("resides somewhere in <col=" + Constants.COLOUR_BLUE + ">Taverly</col>.");
				
				log("");
				
				log("<col=" + Constants.COLOUR_BLUE + ">Saradomin:</col>");
				log("<col=" + Constants.COLOUR_BLUE + ">Saradomin's</col> commander, <col=" + Constants.COLOUR_BLUE + ">Sir Tiffy Cashien</col>, can be found at");
				log("the park in the middle of <col=" + Constants.COLOUR_BLUE + ">Falador</col>.");
				
				log("");
				
				log("<col=" + Constants.COLOUR_BLUE + ">Zamorak:</col>");
				log("I can speak with <col=" + Constants.COLOUR_BLUE + ">Lord Daquarius</col> in the <col=" + Constants.COLOUR_BLUE + ">Black Knights' Fortress</col>");
				log("just west of Edgeville about <col=" + Constants.COLOUR_BLUE + ">Zamorak</col>.");
				
				log("");
				
				log("<col=" + Constants.COLOUR_BLUE + ">Bandos:</col>");
				log("<col=" + Constants.COLOUR_BLUE + ">Bandos'</col> commanders, <col=" + Constants.COLOUR_BLUE + ">General Wartface</col> and <col=" + Constants.COLOUR_BLUE + ">General Bentnoze</col>,");
				log("may have some information about <col=" + Constants.COLOUR_BLUE + ">Bandos</col> for me. They can be");
				log("found at <col=" + Constants.COLOUR_BLUE + ">Goblin Village</col> just north of Falador.");
				break;
				
			case Quest.STAGE_COMPLETED:
				
				break;
		}
	}
	
}
