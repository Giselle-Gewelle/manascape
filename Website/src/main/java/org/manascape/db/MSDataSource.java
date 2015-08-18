package org.manascape.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public final class MSDataSource {
	
	private static final Logger LOG = Logger.getLogger(MSDataSource.class);
	
	private static DataSource dataSource = null;
	
	public static void init() {
		if(dataSource != null) {
			LOG.error("RSDataSource is already initialized!");
			return;
		}
		
		try {
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/manascape");
			
			LOG.info("RSDataSource initialized.");
		} catch(NamingException e) {
			LOG.error("NamingException occured while attempting to initialize the MSDataSource.", e);
		}
	}
	
	protected static Connection getConnection() {
		LOG.info("Polling DataSource for a valid connection.");
		
		try {
			return dataSource.getConnection();
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch a database connection.", e);
			return null;
		}
	}
	
	protected static void closeConnection(Connection connection) {
		try {
			if(connection == null || connection.isClosed()) {
				LOG.warn("Attempting to close a null or already closed connection.");
				return;
			}
			
			connection.close();
			
			LOG.info("Database connection closed.");
		} catch(SQLException e) {
			LOG.error("SQLException occured while attempting to close a database connection!", e);
		}
	}
	
}
