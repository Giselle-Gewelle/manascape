package com.runescape.gameserver.task.impl;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * A task which resets a player after an update cycle.
 * @author Graham Edgecombe
 *
 */
public class PlayerResetTask implements Task {
	
	/**
	 * The player to reset.
	 */
	private Player player;
	
	/**
	 * Creates a reset task.
	 * @param player The player to reset.
	 */
	public PlayerResetTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		player.getUpdateFlags().reset();
		player.setTeleporting(false);
		player.setMapRegionChanging(false);
		player.resetTeleportTarget();
		player.resetCachedUpdateBlock();
		player.reset();
	}

}
