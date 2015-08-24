package com.runescape.gameserver.task.impl;

import org.apache.mina.core.session.IoSession;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * A task that is executed when a session receives a message.
 * @author Graham Edgecombe
 *
 */
public class SessionMessageTask implements Task {
	
	/**
	 * The session.
	 */
	private IoSession session;
	
	/**
	 * The packet.
	 */
	private Packet message;

	/**
	 * Creates the session message task.
	 * @param session The session.
	 * @param message The packet.
	 */
	public SessionMessageTask(IoSession session, Packet message) {
		this.session = session;
		this.message = message;
	}

	@Override
	public void execute(GameEngine context) throws Throwable {
		Player player = (Player) session.getAttribute("player");
		player.getPacketReceiver().handle(session, message);
	}

}
