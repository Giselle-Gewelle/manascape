package org.manascape.db.dao.account;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.manascape.db.Call;
import org.manascape.db.DatabaseHandler;
import org.manascape.security.Password;
import org.manascape.util.DateUtil;

public final class CreateAccountDAO {
	
	private static final Logger LOG = Logger.getLogger(CreateAccountDAO.class);
	
	private final DatabaseHandler db;
	
	public CreateAccountDAO(DatabaseHandler db) {
		this.db = db;
	}
	
	public boolean createAccount(String username, Password password, String dob, String countryCode, String ip) {
		try {
			System.out.println("countryCode = " + countryCode);
			Call dbCall = db.prepareCall("user_createAccount", 8)
				.setString("username", username)
				.setString("passwordHash", password.getHash())
				.setString("passwordSalt", password.getSalt())
				.setString("dob", dob)
				.setString("countryCode", countryCode)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setString("ip", ip)
				.registerOut("returnCode", Types.TINYINT)
				.execute();
			
			return dbCall.getBoolean("returnCode");
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to create a new account with the username [" + username + "], by the IP [" + ip + "].", e);
			return false;
		}
	}
	
	public boolean floodCheck(String ip, Calendar threshold, int max) {
		try {
			Call dbCall = db.prepareCall("user_creationFloodCheck", 4)
				.setString("ip", ip)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(threshold.getTime()))
				.setInt("max", max + 1)
				.registerOut("count", Types.SMALLINT)
				.execute();
			
			return (dbCall.getInt("count") > max);
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the number of recently created accounts for the ip [" + ip + "].", e);
			return true;
		}
	}
	
	public int checkUsername(String username) {
		try {
			Call dbCall = db.prepareCall("user_checkUsername", 2)
				.setString("username", username)
				.registerOut("exists", Types.BIT)
				.execute();
			
			return dbCall.getBoolean("exists") ? 3 : -1;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to check if the username [" + username + "] exists.", e);
			return 4;
		}
	}
	
}
