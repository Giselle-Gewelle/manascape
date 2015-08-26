package org.manascape.db.dao.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import org.apache.log4j.Logger;
import org.manascape.db.Call;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.UserSessionDTO;
import org.manascape.security.Password;
import org.manascape.util.DateUtil;

/**
 * Data Access Object for various types of Account Management.
 * @author DTB
 */
public final class AccountManagementDAO {
	
	private static final Logger LOG = Logger.getLogger(AccountManagementDAO.class);
	
	private final DatabaseHandler db;
	private final UserSessionDTO user;
	
	public AccountManagementDAO(DatabaseHandler db, UserSessionDTO user) {
		this.db = db;
		this.user = user;
	}
	
	public Password getPassword() {
		try {
			ResultSet result = db.prepareCall("user_getPassword", 1)
				.setLong("userId", user.getId())
				.getResults();
			
			if(result == null || !result.next()) {
				return null;
			}
			
			return new Password(result.getString("passwordHash"), result.getString("passwordSalt"));
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the password (hash) for the user [" + user.getUsername() + "].", e);
			return null;
		}
	}
	
	public boolean changePassword(String ip, Password newPassword) {
		try {
			Call dbCall = db.prepareCall("user_changePassword", 6)
				.setLong("userId", user.getId())
				.setString("ip", ip)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setString("newHash", newPassword.getHash())
				.setString("newSalt", newPassword.getSalt())
				.registerOut("successful", Types.BIT)
				.execute();
			
			return dbCall.getBoolean("successful");
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to change the password for the user [" + user.getUsername() + "].", e);
			return false;
		}
	}
	
}
