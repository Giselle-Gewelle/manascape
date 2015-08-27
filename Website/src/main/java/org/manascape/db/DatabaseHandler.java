package org.manascape.db;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseHandler {
	
	public static final int 
		MAX_VALUE_MEDIUMINT = 16777215,
		MAX_LENGTH_TEXT = 65535;
	public static final long 
		MAX_VALUE_INT = 4294967295L;
	
	private final Connection connection;
	
	public DatabaseHandler() {
		this.connection = MSDataSource.getConnection();
	}
	
	public Call prepareCall(String statementName, int paramCount) throws SQLException {
		return new Call(connection, statementName, paramCount);
	}
	
	public Statement prepareStmt(String sql) throws SQLException {
		return new Statement(connection, sql);
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
