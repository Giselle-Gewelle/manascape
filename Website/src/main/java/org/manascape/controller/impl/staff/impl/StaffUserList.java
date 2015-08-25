package org.manascape.controller.impl.staff.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.manascape.controller.impl.staff.StaffPage;
import org.manascape.db.dao.staff.UserListDAO;
import org.manascape.dto.UserListDTO;
import org.manascape.http.RequestHandler;

/**
 * Content controller all staff center "user list" pages; user list and user details.
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
