package com.runescape;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.runescape.fileserver.FileServer;
import com.runescape.gameserver.GameServer;
import com.runescape.gameserver.world.World;

/**
 * A class to start both the file and game servers.
 * @author Graham Edgecombe
 *
 */
public class Server {
	
	/**
	 * The protocol version.
	 */
	public static final int RS_VERSION = 377, RV_VERSION = 1;
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(Server.class.getName());

	/**
	 * The entry point of the application.
	 * @param args The command line arguments.
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		logger.info("Starting Hyperion...");
		World.getInstance(); // this starts off background loading
		try {
			new FileServer().bind().start();
			new GameServer().bind().start();
		} catch(Exception ex) {
			logger.log(Level.SEVERE, "Error starting Hyperion.", ex);
			System.exit(1);
		}
	}

}
