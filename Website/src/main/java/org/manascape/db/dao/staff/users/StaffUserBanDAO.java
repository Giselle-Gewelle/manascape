package org.manascape.db.dao.staff.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.BanDetailsDTO;
import org.manascape.dto.UserSessionDTO;
import org.manascape.util.DateUtil;
import org.manascape.util.StringUtil;

/**
 * Data Access Object for user ban administration.
 * @author DTB
 */
public final class StaffUserBanDAO {
	
	private static final Logger LOG = Logger.getLogger(StaffUserBanDAO.class);
	
	private final DatabaseHandler db;
	private final UserSessionDTO staff;
	
	public StaffUserBanDAO(DatabaseHandler db, UserSessionDTO user) {
		this.db = db;
		this.staff = user;
	}
	
	public boolean applyOrLiftSupportBan(long banId, long userId, String reason) {
		try {
			db.prepareCall("staff_applyOrLiftSupportBan", 5)
				.setLong("userId", userId)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setString("addedBy", staff.getUsername())
				.setString("reason", reason)
				.setLong("banId", banId)
				.execute();
			
			return true;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to apply or lift a forum ban for the user [" + userId + "], banId = [" + banId + "].", e);
			return false;
		}
	}
	
	public boolean applyOrLiftForumBan(long banId, long userId, String reason) {
		try {
			db.prepareCall("staff_applyOrLiftForumBan", 5)
				.setLong("userId", userId)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setString("addedBy", staff.getUsername())
				.setString("reason", reason)
				.setLong("banId", banId)
				.execute();
			
			return true;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to apply or lift a forum ban for the user [" + userId + "], banId = [" + banId + "].", e);
			return false;
		}
	}
	
	public BanDetailsDTO getBanDetails(long banId) {
		try {
			ResultSet result = db.prepareCall("staff_getUserBan", 1)
				.setLong("id", banId)
				.getResults();
			
			if(result == null || !result.next()) {
				return null;
			}
			
			Date liftDate = result.getTimestamp("liftDate");
			String liftor = result.getString("liftor");
			return new BanDetailsDTO(result.getLong("id"), result.getLong("userId"), DateUtil.SHORT_DATETIME_FORMAT.format(result.getTimestamp("date")), 
					StringUtil.formatUsername(result.getString("addedBy")), result.getString("type"), result.getString("reason"), result.getBoolean("active"), 
					liftDate == null ? null : DateUtil.SHORT_DATETIME_FORMAT.format(liftDate), liftor == null ? null : StringUtil.formatUsername(liftor), result.getString("liftReason"),
					StringUtil.formatUsername(result.getString("username")));
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the details for the user ban [" + banId + "].", e);
			return null;
		}
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
