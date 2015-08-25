package org.manascape.db.dao.staff;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.UserListDTO;
import org.manascape.dto.UserListUserDTO;
import org.manascape.dto.UserSessionDTO;
import org.manascape.util.DateUtil;
import org.manascape.util.StringUtil;

/**
 * Date Access Object for the Staff Center user list pages.
 * @author DTB
 */
public final class UserListDAO {
	
	private static final Logger LOG = Logger.getLogger(UserListDAO.class);
	
	private final DatabaseHandler db;
	
	public UserListDAO(DatabaseHandler db) {
		this.db = db;
	}
	
	// TODO replace with UserDetailsDTO
	public UserSessionDTO getUser(int userId) {
		try {
			db.prepareCall("staff_getUserDetails", 1);
			
			
			return null;
		} catch(SQLException e){
			LOG.error("SQLException occurred while attempting to fetch the details for the user [" + userId + "].", e);
			return null;
		}
	}
	
	public UserListDTO getUserList(int page, String usernameSearch, String ipSearch, int limit) {
		UserListDTO returnList = new UserListDTO(1, 1, usernameSearch.replace("%", ""), ipSearch.replace("%", ""), null);
		
		try {
			// Inline because it makes life easier, in this instance...
			
			String sql = 
			"SELECT COUNT(`id`) AS `userCount` " + 
			"FROM `user_accounts` " + 
			"WHERE ((? = '') OR (`username` LIKE ?)) " + 
				"AND ((? = '') OR (`creationIP` LIKE ?) OR (`currentIP` LIKE ?));";
			ResultSet result = db.prepareStmt(sql)
				.setString(usernameSearch)
				.setString(usernameSearch)
				.setString(ipSearch)
				.setString(ipSearch)
				.setString(ipSearch)
				.getResults();
			
			if(result == null || !result.next()) {
				return returnList;
			}
			
			int accountCount = result.getInt("userCount");
			int pageCount = (int) Math.ceil((double) accountCount / (double) limit);
			
			if(page > pageCount) {
				page = pageCount;
			}
			if(page < 1) {
				page = 1;
			}
			
			int start = (page * limit) - limit;
			
			returnList.setCurrentPage(page);
			returnList.setPageCount(pageCount);
			
			sql = 
			"SELECT `id`, `username`, `creationDate`, `creationIP`, `currentIP`, `staff`, `pmod`, `fmod` " + 
			"FROM `user_accounts` " + 
			"WHERE ((? = '') OR (`username` LIKE ?)) " + 
				"AND ((? = '') OR (`creationIP` LIKE ?) OR (`currentIP` LIKE ?)) " +
			"ORDER BY `creationDate` DESC " + 
			"LIMIT " + start + "," + limit + ";";
			ResultSet results = db.prepareStmt(sql)
				.setString(usernameSearch)
				.setString(usernameSearch)
				.setString(ipSearch)
				.setString(ipSearch)
				.setString(ipSearch)
				.getResults();
			
			if(results == null) {
				return returnList;
			}
			
			while(results.next()) {
				returnList.getUserList().add(new UserListUserDTO(results.getInt("id"), results.getString("username"), StringUtil.formatUsername(results.getString("username")), 
						DateUtil.SHORT_DATETIME_FORMAT.format(results.getTimestamp("creationDate")), results.getString("creationIP"), results.getString("currentIP"), 
						results.getBoolean("staff"), results.getBoolean("fmod"), results.getBoolean("pmod")));
			}
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the list of registered users.", e);
		}
		
		return returnList;
	}
	
}
