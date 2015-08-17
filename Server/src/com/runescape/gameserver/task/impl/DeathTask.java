package com.runescape.gameserver.task.impl;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.task.Task;

/**
 * A task which stops the game engine.
 * @author Graham Edgecombe
 *
 */
public class DeathTask implements Task {

	@Override
	public void execute(GameEngine context) {
		if(context.isRunning()) {
			context.stop();
		}
	}

}
