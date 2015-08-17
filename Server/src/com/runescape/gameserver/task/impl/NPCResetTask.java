package com.runescape.gameserver.task.impl;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.entity.mob.npc.NPC;

/**
 * A task which resets an NPC after an update cycle.
 * @author Graham Edgecombe
 *
 */
public class NPCResetTask implements Task {
	
	/**
	 * The npc to reset.
	 */
	private NPC npc;
	
	/**
	 * Creates the reset task.
	 * @param npc The npc to reset.
	 */
	public NPCResetTask(NPC npc) {
		this.npc = npc;
	}

	@Override
	public void execute(GameEngine context) {
		npc.getUpdateFlags().reset();
		npc.setTeleporting(false);
		npc.reset();
	}

}
