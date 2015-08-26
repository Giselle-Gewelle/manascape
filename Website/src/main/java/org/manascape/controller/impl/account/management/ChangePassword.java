package org.manascape.controller.impl.account.management;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.manascape.controller.Controller;
import org.manascape.db.dao.account.AccountManagementDAO;
import org.manascape.http.HttpRequestType;
import org.manascape.security.Password;

/**
 * Content controller for the Change Your Password page.
 * @author DTB
 */
public final class ChangePassword extends Controller {

	private AccountManagementDAO dao;
	
	@Override
	public void init() {
		if(isRedirecting()) {
			return;
		}
		
		if(getRequestType().equals(HttpRequestType.POST)) {
			getRequest().setAttribute("submitted", true);
			
			dao = new AccountManagementDAO(getDb(), getLoginSession().getUser());
			int returnCode = validate();
			getRequest().setAttribute("successful", returnCode == -1);
			if(returnCode != -1) {
				getRequest().setAttribute("errorCode", returnCode);
			}
		}
	}
	
	private int validate() {
		String current = getRequest().getParameter("inputCurrentPassword");
		String pass1 = getRequest().getParameter("inputPassword1");
		String pass2 = getRequest().getParameter("inputPassword2");
		
		if(current == null || pass1 == null || pass2 == null) {
			return 0;
		}
		if(current.equals("") || pass1.equals("") || pass2.equals("")) {
			return 0;
		}
		
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{5,20}$");
		Matcher currentMatcher = pattern.matcher(current);
		Matcher pass1Matcher = pattern.matcher(pass1);
		Matcher pass2Matcher = pattern.matcher(pass2);
		
		if(!currentMatcher.matches() || !pass1Matcher.matches() || !pass2Matcher.matches()) {
			return 1;
		}
		
		if(!pass1.equals(pass2)) {
			return 2;
		}
		
		Password password = dao.getPassword();
		if(!password.equals(current)) {
			return 3;
		}
		
		if(pass1.equals(current)) {
			return 4;
		}
		
		Password newPassword = new Password(pass1);
		if(!dao.changePassword(getRequestIP(), newPassword)) {
			return 5;
		}
		
		return -1;
	}

	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public boolean holdSecureSession() {
		return true;
	}

	@Override
	public boolean loginRequired() {
		return true;
	}

}
