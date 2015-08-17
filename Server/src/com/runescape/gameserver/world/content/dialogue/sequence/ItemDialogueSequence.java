package com.runescape.gameserver.world.content.dialogue.sequence;

import com.runescape.gameserver.world.content.dialogue.ChatInterfaceAction;
import com.runescape.gameserver.world.content.dialogue.Dialogue;
import com.runescape.gameserver.world.content.dialogue.DialogueSequence;
import com.runescape.gameserver.world.entity.mob.player.Player;

public final class ItemDialogueSequence extends DialogueSequence {

	private ChatInterfaceAction action; 
	private Dialogue dialogue;
	
	public ItemDialogueSequence(Player player, int[] itemIds, ChatInterfaceAction action) {
		super(player);
		this.dialogue = new Dialogue(itemIds);
		dialogue.setPlayer(player);
		this.action = action;
	}
	
	public void showNextSequence(int buttonIndex, int itemIndex) {
		player.setCurrentDialogue((DialogueSequence) null);
		action.execute(player, buttonIndex, itemIndex);
	}
	
	@Override
	public boolean showNextDialogue() {
		super.showNextDialogue();
		
		dialogue.show();
		
		return false;
	}

}
