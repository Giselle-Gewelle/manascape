package org.manascape.controller.impl.staff.impl.users;

import java.util.ArrayList;
import java.util.List;

import org.manascape.controller.impl.staff.StaffPage;
import org.manascape.db.DatabaseHandler;
import org.manascape.db.dao.staff.users.StaffUserBanDAO;
import org.manascape.db.dao.staff.users.StaffUserListDAO;
import org.manascape.dto.BanDetailsDTO;
import org.manascape.dto.UserDetailsDTO;
import org.manascape.http.HttpRequestType;
import org.manascape.http.RequestHandler;
import org.manascape.util.UrlUtil;

/**
 * Content controller for all staff center "user ban system" pages.
 * @author DTB
 */
public final class StaffUserBans extends StaffPage {
	
	private static final List<String> TYPES = new ArrayList<String>() {
		
		private static final long serialVersionUID = -3945687520548193927L;

		{
			add("forums");
			add("support");
		}
		
	};
	
	private StaffUserBanDAO dao;
	
	@Override
	public void init() {
		super.init();
		
		if(!isAuthorized()) {
			return;
		}
		
		dao = new StaffUserBanDAO(getDb(), getLoginSession().getUser());
		
		switch(getDest()) {
			case "userbansedit.ws":
				prepareEdit();
				break;
			case "userbansview.ws":
				prepareView();
				break;
		}
	}
	
	private void prepareView() {
		long banId = RequestHandler.getLongParam(getRequest(), "id");
		if(banId < 1 || banId > DatabaseHandler.MAX_VALUE_INT) {
			return;
		}
		
		BanDetailsDTO banDetails = dao.getBanDetails(banId);
		if(banDetails == null) {
			return;
		}
		
		getRequest().setAttribute("ban", banDetails);
	}
	
	private void prepareEdit() {
		long userId = RequestHandler.getLongParam(getRequest(), "userId");
		if(userId < 1 || userId > DatabaseHandler.MAX_VALUE_INT) {
			return;
		}
		
		UserDetailsDTO user = new StaffUserListDAO(getDb()).getUser(userId);
		if(user == null) {
			return;
		}
		
		getRequest().setAttribute("user", user);
		
		String type = getRequest().getParameter("type");
		if(type == null) {
			setRedirecting(true);
			UrlUtil.redirect(getResponse(), "staff", "userdetails.ws?id=" + user.getId());
			return;
		}
		if(!TYPES.contains(type)) {
			setRedirecting(true);
			UrlUtil.redirect(getResponse(), "staff", "userdetails.ws?id=" + user.getId());
			return;
		}
		
		getRequest().setAttribute("banType", type);
		
		long existingBan = dao.getBanForType(user.getId(), type);
		if(existingBan > 0) {
			BanDetailsDTO banDetails = dao.getBanDetails(existingBan);
			if(banDetails == null) {
				existingBan = -1;
			} else {
				getRequest().setAttribute("ban", banDetails);
			}
		}
		
		getRequest().setAttribute("lifting", existingBan > 0);
		
		if(getRequestType().equals(HttpRequestType.POST)) {
			if(getRequest().getParameter("inputCancel") != null) {
				setRedirecting(true);
				UrlUtil.redirect(getResponse(), "staff", "userdetails.ws?id=" + user.getId());
				return;
			}
			
			if(getRequest().getParameter("inputSubmit") == null) {
				return;
			}
			
			getRequest().setAttribute("submitted", true);
			
			String reason = getRequest().getParameter("inputReason");
			if(reason == null) {
				getRequest().setAttribute("errorCode", 0);
				return;
			}
			
			reason = reason.trim();
			if(reason.equals("") || reason.length() > DatabaseHandler.MAX_LENGTH_TEXT) {
				getRequest().setAttribute("errorCode", 1);
				return;
			}
			
			if(existingBan < 0) {
				existingBan = 0;
			}
			
			if(type.equals("forums")) {
				if(!dao.applyOrLiftForumBan(existingBan, user.getId(), reason)) {
					getRequest().setAttribute("errorCode", 2);
				}
			} else if(type.equals("support")) {
				if(!dao.applyOrLiftSupportBan(existingBan, user.getId(), reason)) {
					getRequest().setAttribute("errorCode", 2);
				}
			}
		}
	}
	
	@Override
	public String getActualPage() {
		return getDest().replace("userbans", "user/bans/");
	}
	
}
