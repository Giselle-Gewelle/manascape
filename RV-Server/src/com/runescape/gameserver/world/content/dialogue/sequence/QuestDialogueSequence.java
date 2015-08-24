package com.runescape.gameserver.world.content.dialogue.sequence;

import com.runescape.gameserver.world.content.quest.Quest;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class QuestDialogueSequence extends BasicDialogueSequence {

	private int questId;
	private int endStage;
	
	public QuestDialogueSequence(Player player, int questId) {
		this(player, questId, -1);
	}
	
	public QuestDialogueSequence(Player player, int questId, int endStage) {
		super(player);
		this.questId = questId;
		this.endStage = endStage;
	}
	
	@Override
	public boolean showNextDialogue() {
		boolean moreDialogue = super.showNextDialogue();
		
		if(!moreDialogue) {
			Quest quest = player.getQuestHandler().forId(questId);
			if(endStage == -1) {
				quest.incrementStage();
			} else {
				quest.setStage(endStage);
			}
		}
		
		return moreDialogue;
	}
		
	
}
