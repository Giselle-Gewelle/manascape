package org.manascape.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Statement {
	
	private final PreparedStatement stmt;
	private int paramIdx;
	
	protected Statement(Connection con, String sql) throws SQLException {
		stmt = con.prepareStatement(sql);
		paramIdx = 1;
	}
	
	public Statement setString(String value) throws SQLException {
		stmt.setString(paramIdx++, value);
		return this;
	}
	
	public Statement setInt(int value) throws SQLException {
		stmt.setInt(paramIdx++, value);
		return this;
	}
	
	public Statement setBoolean(boolean value) throws SQLException {
		stmt.setBoolean(paramIdx++, value);
		return this;
	}
	
	public Statement execute() throws SQLException {
		stmt.execute();
		return this;
	}
	
	public ResultSet getResults() throws SQLException {
		execute();
		return stmt.getResultSet();
	}
	
}
