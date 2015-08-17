package com.runescape.gameserver.world.entity.action.impl;

import com.runescape.gameserver.world.content.dialogue.DialogueSequence;
import com.runescape.gameserver.world.entity.action.Action;
import com.runescape.gameserver.world.entity.mob.Mob;
import com.runescape.gameserver.world.entity.mob.player.Player;

public class DialogueAction extends Action {

	private DialogueSequence sequence;
	
	public DialogueAction(Mob mob, DialogueSequence sequence) {
		super(mob, 0);
		this.sequence = sequence;
	}

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
		return AnimationPolicy.RESET_NONE;
	}

	@Override
	public void execute() {
		if(this.getMob() instanceof Player) {
			Player player = (Player) this.getMob();
			player.setCurrentDialogue(sequence);
		}
		this.stop();
	}

}
