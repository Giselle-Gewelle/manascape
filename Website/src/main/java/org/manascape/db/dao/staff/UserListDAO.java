package org.manascape.db.dao.staff;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.manascape.db.Call;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.UserListDTO;
import org.manascape.dto.UserListUserDTO;
import org.manascape.dto.LoginAttemptEntryDTO;
import org.manascape.dto.LoginAttemptInfoDTO;
import org.manascape.dto.LoginSessionEntryDTO;
import org.manascape.dto.LoginSessionInfoDTO;
import org.manascape.dto.PageInfoDTO;
import org.manascape.dto.UserDetailsDTO;
import org.manascape.util.CountryUtil;
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
	
	public LoginSessionInfoDTO getLoginSessions(long userId, int page, int limit) {
		LoginSessionInfoDTO dto = new LoginSessionInfoDTO(new PageInfoDTO(1, 1, 0), new LinkedList<>());
		
		try {
			Call dbCall = db.prepareCall("staff_getUserLoginSessions", 6)
				.setLong("userId", userId)
				.setInt("page", page)
				.setInt("limit", limit)
				.registerOut("pageCount", Types.INTEGER)
				.registerOut("realPage", Types.INTEGER)
				.registerOut("count", Types.BIGINT)
				.execute();
			
			int realPage = dbCall.getInt("realPage");
			int pageCount = dbCall.getInt("pageCount");
			long fullEntryCount = dbCall.getLong("count");
			
			dto.getPageInfo().setCurrentPage(realPage);
			dto.getPageInfo().setPageCount(pageCount);
			dto.getPageInfo().setFullEntryCount(fullEntryCount);
			
			ResultSet results = dbCall.getResults(false);
			if(results == null) {
				return dto;
			}

			Date currentDate = new Date();
			
			while(results.next()) {
				boolean active = false;
				Date endDate = results.getTimestamp("endDate");
				if(endDate.after(currentDate)) {
					active = true;
				}
				
				dto.getEntries().add(new LoginSessionEntryDTO(results.getString("ip"), 
					DateUtil.SHORT_DATETIME_FORMAT.format(results.getTimestamp("startDate")), DateUtil.SHORT_DATETIME_FORMAT.format(endDate), 
					results.getBoolean("secure"), results.getString("startMod"), results.getString("currentMod"), results.getString("startDest"), results.getString("currentDest"), active));
			}
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch a list of login sessions for the user [" + userId + "].", e);
		}
		
		return dto;
	}
	
	public LoginAttemptInfoDTO getLoginAttempts(String username, int page, int limit) {
		LoginAttemptInfoDTO dto = new LoginAttemptInfoDTO(new PageInfoDTO(1, 1, 0), new LinkedList<>());
		
		try {
			Call dbCall = db.prepareCall("staff_getUserLoginAttempts", 6)
				.setString("username", username)
				.setInt("page", page)
				.setInt("limit", limit)
				.registerOut("pageCount", Types.INTEGER)
				.registerOut("realPage", Types.INTEGER)
				.registerOut("count", Types.BIGINT)
				.execute();
			
			int realPage = dbCall.getInt("realPage");
			int pageCount = dbCall.getInt("pageCount");
			long fullEntryCount = dbCall.getLong("count");
			
			dto.getPageInfo().setCurrentPage(realPage);
			dto.getPageInfo().setPageCount(pageCount);
			dto.getPageInfo().setFullEntryCount(fullEntryCount);
			
			ResultSet results = dbCall.getResults(false);
			if(results == null) {
				return dto;
			}
			
			while(results.next()) {
				dto.getEntries().add(new LoginAttemptEntryDTO(results.getString("ip"), DateUtil.SHORT_DATETIME_FORMAT.format(results.getTimestamp("date")), results.getInt("successful") > 0));
			}
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch a list of login attempts for the user [" + username + "].", e);
		}
		
		return dto;
	}
	
	public UserDetailsDTO getUser(long userId) {
		try {
			ResultSet result = db.prepareCall("staff_getUserDetails", 1)
				.setLong("id", userId)
				.getResults();
			
			if(result == null || !result.next()) {
				return null;
			}
			
			UserDetailsDTO dto = new UserDetailsDTO();
			String username = result.getString("username");
			dto.setId(result.getLong("id"));
			dto.setUsername(username);
			dto.setDisplayName(StringUtil.formatUsername(username));
			dto.setDob(DateUtil.SHORT_DATE_FORMAT.format(result.getTimestamp("dob")));
			dto.setCountry(CountryUtil.get(result.getInt("countryCode")));
			dto.setCreationDate(DateUtil.SHORT_DATETIME_FORMAT.format(result.getTimestamp("creationDate")));
			dto.setCreationIP(result.getString("creationIP"));
			dto.setCurrentIP(result.getString("currentIP"));
			dto.setStaff(result.getBoolean("staff"));
			dto.setFmod(result.getBoolean("fmod"));
			dto.setPmod(result.getBoolean("pmod"));
			dto.setSupportDisabled(result.getBoolean("supportDisabled"));
			
			return dto;
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
