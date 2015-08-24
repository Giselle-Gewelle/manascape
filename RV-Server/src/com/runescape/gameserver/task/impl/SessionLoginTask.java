package com.runescape.gameserver.task.impl;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * A task that is executed when a player has logged in.
 * @author Graham Edgecombe
 *
 */
public class SessionLoginTask implements Task {

	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates the session login task.
	 * @param player The player that logged in.
	 */
	public SessionLoginTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute(GameEngine context) {
		World.getInstance().register(player);
	}

}
