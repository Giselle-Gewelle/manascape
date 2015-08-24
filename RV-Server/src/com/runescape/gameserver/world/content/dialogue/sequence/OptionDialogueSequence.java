package com.runescape.gameserver.world.content.dialogue.sequence;

import com.runescape.gameserver.world.content.dialogue.Dialogue;
import com.runescape.gameserver.world.content.dialogue.DialogueSequence;
import com.runescape.gameserver.world.entity.mob.player.Player;

public final class OptionDialogueSequence extends DialogueSequence {
	
	private DialogueSequence[] sequences;
	private Dialogue optionDialogue;
	
	public OptionDialogueSequence(Player player, Dialogue optionDialogue, DialogueSequence[] sequences) {
		super(player);
		optionDialogue.setPlayer(player);
		this.optionDialogue = optionDialogue;
		this.sequences = sequences;
	}
	
	public void showNextSequence(int buttonId) {
		int interfaceId = optionDialogue.getInterfaceId();
		int index = buttonId - (interfaceId + 2);
		
		super.setNextSequence(sequences[index]);
		super.showNextSequence();
	}
	
	@Override
	public boolean showNextDialogue() {
		super.showNextDialogue();
		
		optionDialogue.show();
		
		return false;
	}

}
