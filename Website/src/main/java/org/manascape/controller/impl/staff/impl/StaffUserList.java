package org.manascape.controller.impl.staff.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.manascape.controller.impl.staff.StaffPage;
import org.manascape.db.DatabaseHandler;
import org.manascape.db.dao.staff.UserListDAO;
import org.manascape.dto.LoginAttemptEntryDTO;
import org.manascape.dto.LoginAttemptInfoDTO;
import org.manascape.dto.LoginSessionEntryDTO;
import org.manascape.dto.LoginSessionInfoDTO;
import org.manascape.dto.PasswordChangeInfoDTO;
import org.manascape.dto.UserDetailsDTO;
import org.manascape.dto.UserListDTO;
import org.manascape.http.RequestHandler;

/**
 * Content controller for all staff center "user list" pages; user list and user details.
 * @author DTB
 */
public final class StaffUserList extends StaffPage {

	private UserListDAO dao;
	
	@Override
	public void init() {
		super.init();
		
		if(!isAuthorized()) {
			return;
		}
		
		dao = new UserListDAO(getDb());
		
		switch(getDest()) {
			case "userlist.ws":
				prepareUserList();
				break;
			case "userdetails.ws":
				prepareUserDetails();
				break;
			case "userloginattempts.ws":
				prepareUserLoginAttempts();
				break;
			case "userloginsessions.ws":
				prepareUserLoginSessions();
				break;
			case "userpasswordchanges.ws":
				prepareUserPasswordChanges();
				break;
		}
	}
	
	private void prepareUserPasswordChanges() {
		UserDetailsDTO user = getUser();
		if(user == null) {
			return;
		}
		
		int page = getPage();
		
		PasswordChangeInfoDTO dto = dao.getPasswordChanges(user.getId(), page, 20);
		getRequest().setAttribute("passwordChanges", dto);
	}
	
	private void prepareUserLoginSessions() {
		UserDetailsDTO user = getUser();
		if(user == null) {
			return;
		}
		
		int page = getPage();
		
		LoginSessionInfoDTO dto = dao.getLoginSessions(user.getId(), page, 20);
		getRequest().setAttribute("loginSessions", dto);
	}
	
	private void prepareUserLoginAttempts() {
		UserDetailsDTO user = getUser();
		if(user == null) {
			return;
		}
		
		int page = getPage();
		
		LoginAttemptInfoDTO dto = dao.getLoginAttempts(user.getUsername(), page, 20);
		getRequest().setAttribute("loginAttempts", dto);
	}
	
	private UserDetailsDTO getUser() {
		long userId = RequestHandler.getLongParam(getRequest(), "id");
		if(userId < 1 || userId > DatabaseHandler.MAX_VALUE_INT) {
			return null;
		}
		
		UserDetailsDTO user = dao.getUser(userId);
		if(user == null) {
			return null;
		}
		
		getRequest().setAttribute("user", user);
		return user;
	}
	
	private int getPage() {
		int page = RequestHandler.getIntParam(getRequest(), "page");
		if(page < 1 || page > Integer.MAX_VALUE) {
			return 1;
		}
		
		return page;
	}
	
	private void prepareUserDetails() {
		long userId = RequestHandler.getLongParam(getRequest(), "id");
		if(userId < 1 || userId > DatabaseHandler.MAX_VALUE_INT) {
			return;
		}
		
		UserDetailsDTO user = dao.getUser(userId);
		if(user != null) {
			getRequest().setAttribute("user", user);
			
			int failedLoginAttempts = 0;
			List<LoginAttemptEntryDTO> loginAttempts = dao.getLoginAttempts(user.getUsername(), 1, 5).getEntries();
			for(LoginAttemptEntryDTO entry : loginAttempts) {
				if(!entry.isSuccessful()) {
					failedLoginAttempts++;
				}
			}
			
			String currentlyLoggedIn = null;
			List<LoginSessionEntryDTO> loginSessions = dao.getLoginSessions(user.getId(), 1, 5).getEntries();
			for(LoginSessionEntryDTO entry : loginSessions) {
				if(!entry.isActive()) {
					continue;
				}
				
				currentlyLoggedIn = entry.getIp();
			}
			
			getRequest().setAttribute("failedLoginAttempts", failedLoginAttempts);
			getRequest().setAttribute("currentlyLoggedIn", currentlyLoggedIn);
			getRequest().setAttribute("loginAttempts", loginAttempts);
			getRequest().setAttribute("loginSessions", loginSessions);
			getRequest().setAttribute("passwordChanges", dao.getPasswordChanges(user.getId(), 1, 5).getEntries());
		}
	}
	
	private void prepareUserList() {
		int page = RequestHandler.getIntParam(getRequest(), "page");
		if(page < 1 || page > Short.MAX_VALUE) {
			page = 1;
		}
		
		String usernameSearch = getRequest().getParameter("usernameSearch");
		if(usernameSearch != null) {
			if(usernameSearch.length() < 1 || usernameSearch.length() > 12) {
				usernameSearch = null;
			} else {
				Pattern pattern = Pattern.compile("^[a-z0-9_]{1,12}$");
				Matcher matcher = pattern.matcher(usernameSearch);
				if(!matcher.find()) {
					usernameSearch = null;
				}
			}
		}
		
		if(usernameSearch == null) {
			usernameSearch = "";
		}
		
		if(!usernameSearch.equals("")) {
			usernameSearch = "%" + usernameSearch + "%";
		}
		
		String ipSearch = getRequest().getParameter("ipSearch");
		if(ipSearch != null) {
			if(ipSearch.length() < 1 || ipSearch.length() > 50) {
				ipSearch = null;
			} else {
				Pattern pattern = Pattern.compile("^[0-9\\.]{1,50}$");
				Matcher matcher = pattern.matcher(ipSearch);
				if(!matcher.find()) {
					ipSearch = null;
				}
			}
		}
		
		if(ipSearch == null) {
			ipSearch = "";
		}
		
		if(!ipSearch.equals("")) {
			ipSearch = "%" + ipSearch + "%";
		}
		
		UserListDTO userList = dao.getUserList(page, usernameSearch, ipSearch, 20);
		getRequest().setAttribute("userList", userList);
	}
	
	@Override
	public String getActualPage() {
		return getDest().replace("user", "user/");
	}
	
}
