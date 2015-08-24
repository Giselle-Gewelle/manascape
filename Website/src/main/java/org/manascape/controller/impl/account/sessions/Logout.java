package org.manascape.controller.impl.account.sessions;

import javax.servlet.http.HttpServletRequest;

import org.manascape.controller.Controller;
import org.manascape.controller.ControllerConstants;
import org.manascape.db.dao.account.LoginSessionDAO;
import org.manascape.util.UrlUtil;

public final class Logout extends Controller {

	private String toMod;
	private String toDest;
	private String toQuery;

	@Override
	public void init() {
		setupRedirectLocation();
		
		setRedirecting(true);
		
		if(getLoginSession().isLoggedIn()) {
			new LoginSessionDAO(getDb()).killSession(getLoginSession().getUser().getSessionId());
		}
		
		UrlUtil.redirect(getResponse(), toMod, toDest + toQuery);
	}
	
	private void setDefaults() {
		toMod = "main";
		toDest = "title.ws";
		toQuery = "";
	}
	
	private void setupRedirectLocation() {
		HttpServletRequest request = getRequest();
		
		String toMod = request.getParameter("mod");
		String toDest = request.getParameter("dest");
		
		if(toMod == null || toDest == null) {
			setDefaults();
			return;
		}
		
		String toQuery = "";
		if(toDest.indexOf('?') != -1) {
			toQuery = toDest.substring(toDest.indexOf('?'));
			toDest = toDest.substring(0, toDest.indexOf('?'));
		}
		
		if(!ControllerConstants.CONTROLLER_MAP.containsKey(toMod + " " + toDest)) {
			setDefaults();
			return;
		}
		
		if(toDest.equals("login.ws") || toDest.equals("logout.ws")) {
			setDefaults();
			return;
		}
		
		this.toMod = toMod;
		this.toDest = toDest;
		this.toQuery = toQuery;
	}

}
