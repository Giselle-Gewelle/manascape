package org.manascape.db.dao.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.manascape.controller.impl.account.sessions.Login;
import org.manascape.db.Call;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.PasswordRequestDTO;
import org.manascape.dto.SessionCheckDTO;
import org.manascape.dto.UserSessionDTO;
import org.manascape.util.DateUtil;
import org.manascape.util.StringUtil;

public final class LoginSessionDAO {
	
	private static final Logger LOG = Logger.getLogger(LoginSessionDAO.class);
	
	private final DatabaseHandler db;
	
	public LoginSessionDAO(DatabaseHandler db) {
		this.db = db;
	}
	
	public boolean floodCheck(String ip, Calendar threshold, int max) {
		try {
			Call dbCall = db.prepareCall("user_loginFloodCheck", 4)
				.setString("ip", ip)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(threshold.getTime()))
				.setInt("max", max + 1)
				.registerOut("count", Types.SMALLINT)
				.execute();
			
			return (dbCall.getInt("count") > max);
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the number of recent login attempts for the ip [" + ip + "].", e);
			return true;
		}
	}
	
	public UserSessionDTO getSession(long sessionId, String newHash, boolean secure, String mod, String dest, String endDate) {
		try {
			ResultSet result = db.prepareCall("user_getLoginSessionDetails", 6)
				.setLong("sessionId", sessionId)
				.setBoolean("secure", secure)
				.setString("newMod", mod)
				.setString("newDest", dest)
				.setString("newHash", newHash)
				.setString("endDate", endDate)
				.getResults();
			
			if(result == null || !result.next()) {
				return null;
			}
			
			String username = result.getString("username");
			return new UserSessionDTO(result.getLong("id"), username, StringUtil.formatUsername(username), result.getBoolean("staff"), result.getBoolean("fmod"), result.getBoolean("pmod"), 
					result.getString("currentIP"), result.getBoolean("supportDisabled"), sessionId, newHash, secure, mod, dest);
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the user login session [" + sessionId + "].", e);
			return null;
		}
	}
	
	public void killSession(long id) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -Login.IDLE_TIME);
			
			db.prepareCall("user_killLoginSession", 2)
				.setLong("id", id)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(cal.getTime()))
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
			
			return new SessionCheckDTO(result.getLong("id"), result.getBoolean("secure"), DateUtil.SQL_DATETIME_FORMAT.format(result.getTimestamp("endDate")));
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to find a user login session.", e);
			return null;
		}
	}
	
	public void submitLoginSession(long userId, String ip, String hash, Calendar start, Calendar end, String mod, String dest, boolean secure) {
		try {
			db.prepareCall("user_submitLoginSession", 8)
				.setLong("userId", userId)
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
	
	public PasswordRequestDTO getUserInfo(String username, Calendar cal, String ip) {
		try {
			ResultSet result = db.prepareCall("user_getInfoForLogin", 3)
				.setString("username", username)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(cal.getTime()))
				.setString("ip", ip)
				.getResults();
			
			if(result == null || !result.next()) {
				return null;
			}
			
			return new PasswordRequestDTO(result.getLong("id"), result.getString("passwordHash"), result.getString("passwordSalt"));
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch login information for the user [" + username + "] by the IP [" + ip + "].", e);
			return null;
		}
	}
	
}
