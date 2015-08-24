package com.runescape.gameserver.task.impl;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.task.Task;

/**
 * Performs garbage collection and finalization.
 * @author Graham Edgecombe
 *
 */
public class CleanupTask implements Task {

	@Override
	public void execute(GameEngine context) {
		context.submitWork(new Runnable() {
			public void run() {
				System.gc();
				System.runFinalization();
			}
		});
	}

}
