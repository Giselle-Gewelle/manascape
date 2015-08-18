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
		stmt.setString(paramName, value);
		return this;
	}
	
	public Call setInt(String paramName, int value) throws SQLException {
		stmt.setInt(paramName, value);
		return this;
	}
	
	public Call registerOut(String paramName, int type) throws SQLException {
		stmt.registerOutParameter(paramName, type);
		return this;
	}
	
	public void execute() throws SQLException {
		stmt.execute();
	}
	
	public ResultSet getResults() throws SQLException {
		execute();
		return stmt.getResultSet();
	}
	
}
