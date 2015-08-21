package org.manascape.security;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.manascape.controller.Controller;
import org.manascape.controller.impl.account.sessions.Login;
import org.manascape.db.DatabaseHandler;
import org.manascape.db.dao.account.LoginSessionDAO;
import org.manascape.dto.SessionCheckDTO;
import org.manascape.dto.UserSessionDTO;
import org.manascape.util.DateUtil;

public final class LoginSession {
	
	private static final Logger LOG = Logger.getLogger(LoginSession.class);
	
	private UserSessionDTO user;
	private boolean loggedIn;
	
	public LoginSession(DatabaseHandler db, Controller viewController, String hash) {
		this.user = null;
		this.loggedIn = false;
		
		if(hash == null || hash.length() != 128) {
			return;
		}
		
		int idleTime = Login.IDLE_TIME;
		if(viewController.isSecure()) {
			// For pages like password changing, recovery question changing, etc...
			
			idleTime = Login.SECURE_IDLE_TIME;
		}
		
		Calendar minCal = Calendar.getInstance();
		minCal.add(Calendar.MINUTE, -idleTime);
		
		LoginSessionDAO dao = new LoginSessionDAO(db);
		
		SessionCheckDTO sessionCheck = dao.findSession(hash, viewController.getRequestIP(), minCal);
		if(sessionCheck == null) {
			return;
		}
		
		if(viewController.isSecure() && !sessionCheck.isSecure()) {
			// User is coming from a general part of the website to a part of the website that requires an additional login, 
			// force a new login by killing their current login.
			
			LOG.info("User attempting to access a secure website section using an insecure login session. Killing their session...");
			dao.killSession(sessionCheck.getSessionId());
			return;
		}
		
		String endDate = sessionCheck.getEndDate();
		if(viewController.holdSecureSession() || !sessionCheck.isSecure()) {
			Calendar newEndCal = Calendar.getInstance();
			newEndCal.add(Calendar.MINUTE, idleTime);
			endDate = DateUtil.SQL_DATETIME_FORMAT.format(newEndCal.getTime());
		}
		
		//this.user = LoginSessionDAO.getLoginSessionDetails(dbConnection, sessionCheck.getSessionId(), viewController.isSecure(), viewController.getMod(), viewController.getDest(), endDate);
		if(this.user != null) {
			this.loggedIn = true;
		}
	}
	
	public UserSessionDTO getUser() {
		return user;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
}
