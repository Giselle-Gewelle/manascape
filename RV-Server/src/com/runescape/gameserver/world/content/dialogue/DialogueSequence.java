package com.runescape.gameserver.world.content.dialogue;

import com.runescape.gameserver.world.entity.mob.player.Player;

public abstract class DialogueSequence {

	protected Player player;
	protected DialogueSequence nextSequence;
	
	public DialogueSequence(Player player) {
		this.player = player;
	}
	
	public DialogueSequence(Player player, DialogueSequence nextSequence) {
		this.player = player;
		this.nextSequence = nextSequence;
	}
	
	public void setNextSequence(DialogueSequence nextSequence) {
		this.nextSequence = nextSequence;
	}
	
	public void showNextSequence() {
		if(nextSequence != null) {
			player.setCurrentDialogue(nextSequence);
		} else {
			player.setCurrentDialogue((DialogueSequence) null);
		}
	}
	
	public boolean showNextDialogue() {
		return false;
	}
	
}
