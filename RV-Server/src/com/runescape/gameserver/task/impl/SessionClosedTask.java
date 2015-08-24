package com.runescape.gameserver.task.impl;

import java.net.SocketAddress;
import java.util.logging.Logger;

import org.apache.mina.core.session.IoSession;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.event.impl.SessionClosedEvent;
import com.runescape.gameserver.task.Task;
import com.runescape.gameserver.world.World;
import com.runescape.gameserver.world.entity.mob.player.Player;

/**
 * A task that is executed when a session is closed.
 * @author Graham Edgecombe
 *
 */
public class SessionClosedTask implements Task {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(SessionClosedTask.class.getName());
	
	/**
	 * The session that closed.
	 */
	private IoSession session;
	
	/**
	 * Creates the session closed task.
	 * @param session The session.
	 */
	public SessionClosedTask(IoSession session) {
		this.session = session;
	}

	@Override
	public void execute(GameEngine context) {
		SocketAddress address = (SocketAddress) session.getAttribute("remote");
		logger.fine("Session closed : " + address);
		if(session.containsAttribute("player")) {
			Player p = (Player) session.getAttribute("player");
			//if(!p.getEntityCooldowns().get(CooldownFlags.COMBAT)) {
			//	World.getWorld().unregister(p);
			//} else {
				World.getInstance().submit(new SessionClosedEvent(p));
			//}
		}
	}

}
