package org.manascape.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public final class DatabaseHandler {
	
	private static final Logger LOG = Logger.getLogger(DatabaseHandler.class);
	
	private final Connection connection;
	
	public DatabaseHandler() {
		this.connection = MSDataSource.getConnection();
	}
	
	public void close() {
		MSDataSource.closeConnection(connection);
	}
	
	public boolean isClosed() throws SQLException {
		if(connection == null) {
			return true;
		}
		
		return connection.isClosed();
	}
	
}
