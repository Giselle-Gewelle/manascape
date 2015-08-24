package com.runescape.gameserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.runescape.gameserver.GameEngine;
import com.runescape.gameserver.io.ConnectionHandler;
import com.runescape.gameserver.world.World;


/**
 * Starts everything else including MINA and the <code>GameEngine</code>.
 * @author Graham Edgecombe
 * 
 */
public class GameServer {

	/**
	 * The port to listen on.
	 */
	public static final int PORT = 43594;

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(GameServer.class.getName());

	/**
	 * The <code>IoAcceptor</code> instance.
	 */
	private final IoAcceptor acceptor = new NioSocketAcceptor();

	/**
	 * The <code>GameEngine</code> instance.
	 */
	private static final GameEngine engine = new GameEngine();

	/**
	 * Creates the server and the <code>GameEngine</code> and initializes the
	 * <code>World</code>.
	 * @throws Throwable 
	 */
	public GameServer() throws Throwable {
		World.getInstance().init(engine);
		acceptor.setHandler(new ConnectionHandler());
		//acceptor.getFilterChain().addFirst("throttleFilter", new ConnectionThrottleFilter());
	}

	/**
	 * Binds the server to the specified port.
	 * @param port The port to bind to.
	 * @return The server instance, for chaining.
	 * @throws IOException
	 */
	public GameServer bind() throws IOException {
		logger.info("Binding to port : " + PORT + "...");
		acceptor.bind(new InetSocketAddress(PORT));
		return this;
	}

	/**
	 * Starts the <code>GameEngine</code>.
	 * @throws ExecutionException if an error occured during background loading.
	 */
	public void start() throws ExecutionException {
		if(World.getInstance().getBackgroundLoader().getPendingTaskAmount() > 0) {
			logger.info("Waiting for pending background loading tasks...");
			World.getInstance().getBackgroundLoader().waitForPendingTasks();
		}
		World.getInstance().getBackgroundLoader().shutdown();
		engine.start();
		logger.info("Ready");
	}

	/**
	 * Gets the <code>GameEngine</code>.
	 * @return The game engine.
	 */
	public static GameEngine getEngine() {
		return engine;
	}
	
}
