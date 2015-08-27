package org.manascape.db.dao.staff.users;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.manascape.db.DatabaseHandler;

public final class StaffUserBanDAO {
	
	private static final Logger LOG = Logger.getLogger(StaffUserBanDAO.class);
	
	private final DatabaseHandler db;
	
	public StaffUserBanDAO(DatabaseHandler db) {
		this.db = db;
	}
	
	public long getBanForType(long userId, String type) {
		try {
			ResultSet result = db.prepareCall("staff_getUserBanForType", 2)
				.setLong("userId", userId)
				.setString("type", type)
				.getResults();
			
			if(result == null || !result.next()) {
				return -1;
			}
			
			return result.getLong("id");
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to check if the user [" + userId + "] has any active bans for the type [" + type + "].", e);
			return -1;
		}
	}
	
}
