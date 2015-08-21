package org.manascape.db.dao.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.LoginRequestDTO;
import org.manascape.dto.SessionCheckDTO;
import org.manascape.util.DateUtil;

public final class LoginSessionDAO {
	
	private static final Logger LOG = Logger.getLogger(LoginSessionDAO.class);
	
	private final DatabaseHandler db;
	
	public LoginSessionDAO(DatabaseHandler db) {
		this.db = db;
	}
	
	public void killSession(int id) {
		try {
			db.prepareCall("user_killLoginSession", 2)
				.setInt("id", id)
				.setString("endDate", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.execute();
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to kill the login session [" + id + "].", e);
		}
	}
	
	public SessionCheckDTO findSession(String hash, String ip, Calendar timeRange) {
		try {
			ResultSet result = db.prepareCall("user_findLoginSession", 3)
				.setString("hash", hash)
				.setString("ip", ip)
				.setString("timeRange", DateUtil.SQL_DATETIME_FORMAT.format(timeRange.getTime()))
				.getResults();
			
			if(result == null || !result.next()) {
				return null;
			}
			
			return new SessionCheckDTO(result.getInt("id"), result.getBoolean("secure"), DateUtil.SQL_DATETIME_FORMAT.format(result.getTimestamp("endDate")));
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to find a user login session.", e);
			return null;
		}
	}
	
	public void submitLoginSession(int userId, String ip, String hash, Calendar start, Calendar end, String mod, String dest, boolean secure) {
		try {
			db.prepareCall("user_submitLoginSession", 8)
				.setInt("userId", userId)
				.setString("ip", ip)
				.setString("sessionHash", hash)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(start.getTime()))
				.setString("endDate", DateUtil.SQL_DATETIME_FORMAT.format(end.getTime()))
				.setString("mod", mod)
				.setString("dest", dest)
				.setBoolean("secure", secure)
				.execute();
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to start a new login session for the user [" + userId + "]:[" + ip + "].", e);
		}
	}
	
	public LoginRequestDTO getUserInfo(String username, String ip) {
		try {
			ResultSet result = db.prepareCall("user_getInfoForLogin", 3)
				.setString("username", username)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setString("ip", ip)
				.getResults();
			
			if(result == null || !result.next()) {
				return null;
			}
			
			return new LoginRequestDTO(result.getInt("id"), result.getString("passwordHash"), result.getString("passwordSalt"));
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch login information for the user [" + username + "] by the IP [" + ip + "].", e);
			return null;
		}
	}
	
}
