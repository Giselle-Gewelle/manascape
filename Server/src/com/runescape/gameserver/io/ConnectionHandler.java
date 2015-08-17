package com.runescape.gameserver.io;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.io.codec.RS2CodecFactory;
import com.runescape.gameserver.io.packet.Packet;
import com.runescape.gameserver.task.impl.SessionClosedTask;
import com.runescape.gameserver.task.impl.SessionMessageTask;
import com.runescape.gameserver.task.impl.SessionOpenedTask;
import com.runescape.gameserver.world.World;


/**
 * The <code>ConnectionHandler</code> processes incoming events from MINA,
 * submitting appropriate tasks to the <code>GameEngine</code>.
 * @author Graham Edgecombe
 *
 */
public class ConnectionHandler extends IoHandlerAdapter {
	
	/**
	 * The <code>GameEngine</code> instance.
	 */
	private final GameEngine engine = World.getInstance().getEngine();

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
		session.close(false);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		engine.pushTask(new SessionMessageTask(session, (Packet) message));
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		engine.pushTask(new SessionClosedTask(session));
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		session.close(false);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.setAttribute("remote", session.getRemoteAddress());
		session.getFilterChain().addFirst("protocol", new ProtocolCodecFilter(RS2CodecFactory.LOGIN));
		engine.pushTask(new SessionOpenedTask(session));
	}

}
