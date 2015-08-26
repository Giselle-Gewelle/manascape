package org.manascape.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Call {
	
	private final CallableStatement stmt;
	
	protected Call(Connection con, String name, int paramCount) throws SQLException {
		StringBuilder params = new StringBuilder("");
		if(paramCount > 0) {
			for(int i = 0; i < paramCount; i++) {
				if(i != 0) {
					params.append(",");
				}
				
				params.append("?");
			}
		}
		
		String query = "CALL `" + name + "`(" + params.toString() + ");";
		
		this.stmt = con.prepareCall(query);
	}
	
	public Call setString(String paramName, String value) throws SQLException {
		stmt.setString("in_" + paramName, value);
		return this;
	}
	
	public Call setLong(String paramName, long value) throws SQLException {
		stmt.setLong("in_" + paramName, value);
		return this;
	}
	
	public Call setInt(String paramName, int value) throws SQLException {
		stmt.setInt("in_" + paramName, value);
		return this;
	}
	
	public Call setBoolean(String paramName, boolean value) throws SQLException {
		stmt.setBoolean("in_" + paramName, value);
		return this;
	}
	
	public Call registerOut(String paramName, int type) throws SQLException {
		stmt.registerOutParameter("out_" + paramName, type);
		return this;
	}
	
	public Call execute() throws SQLException {
		stmt.execute();
		return this;
	}
	
	public String getString(String paramName) throws SQLException {
		return stmt.getString("out_" + paramName);
	}
	
	public long getLong(String paramName) throws SQLException {
		return stmt.getLong("out_" + paramName);
	}
	
	public int getInt(String paramName) throws SQLException {
		return stmt.getInt("out_" + paramName);
	}
	
	public boolean getBoolean(String paramName) throws SQLException {
		return stmt.getBoolean("out_" + paramName);
	}
	
	public ResultSet getResults(boolean execute) throws SQLException {
		if(execute)
			execute();
		return stmt.getResultSet();
	}
	
	public ResultSet getResults() throws SQLException {
		return getResults(true);
	}
	
}
