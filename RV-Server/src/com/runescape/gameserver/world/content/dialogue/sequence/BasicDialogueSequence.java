package com.runescape.gameserver.world.content.dialogue.sequence;

import java.util.ArrayList;
import java.util.List;

import com.runescape.gameserver.world.content.dialogue.Dialogue;
import com.runescape.gameserver.world.content.dialogue.DialogueSequence;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class BasicDialogueSequence extends DialogueSequence {

	private List<Dialogue> dialogues;
	private int dialogueIndex;
	
	public BasicDialogueSequence(Player player) {
		super(player);
		dialogues = new ArrayList<Dialogue>();
		dialogueIndex = 0;
	}
	
	public BasicDialogueSequence(Player player, DialogueSequence nextSequence) {
		super(player, nextSequence);
		dialogues = new ArrayList<Dialogue>();
		dialogueIndex = 0;
	}
	
	public void addDialogue(Dialogue dialogue) {
		dialogue.setPlayer(player);
		dialogues.add(dialogue);
	}
	
	@Override
	public boolean showNextDialogue() {
		if(dialogueIndex >= dialogues.size()) {
			dialogueIndex = 0;
			super.showNextSequence();
			return false;
		}
		
		Dialogue dialogue = dialogues.get(dialogueIndex++);
		dialogue.show();
		return true;
	}
		
	
}
