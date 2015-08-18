package org.manascape.db;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseHandler {
	
	private final Connection connection;
	
	public DatabaseHandler() {
		this.connection = MSDataSource.getConnection();
	}
	
	public Call prepareCall(String statementName, int paramCount) throws SQLException {
		return new Call(connection, statementName, paramCount);
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
