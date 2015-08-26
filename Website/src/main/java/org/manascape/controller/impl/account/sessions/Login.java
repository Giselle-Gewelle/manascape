package org.manascape.controller.impl.account.sessions;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.manascape.controller.Controller;
import org.manascape.controller.ControllerConstants;
import org.manascape.db.dao.account.LoginSessionDAO;
import org.manascape.dto.PasswordRequestDTO;
import org.manascape.http.HttpRequestType;
import org.manascape.security.Hashing;
import org.manascape.security.Password;
import org.manascape.util.StringUtil;
import org.manascape.util.UrlUtil;

public final class Login extends Controller {
	
	public static final int 
		IDLE_TIME = 360,
		SECURE_IDLE_TIME = 60;

	private String toMod;
	private String toDest;
	private String toQuery;
	
	@Override
	public void init() {
		getRequest().setAttribute("hideSessionButton", true);
		
		setupRedirectLocation();
		
		if(isRedirecting()) {
			return;
		}
		
		if(getRequestType().equals(HttpRequestType.POST)) {
			getRequest().setAttribute("loginAttempted", true);
			
			int loginResponse = attemptLogin();
			if(loginResponse > -1) {
				getRequest().setAttribute("errorCode", loginResponse);
			}
		}
	}
	
	private int attemptLogin() {
		HttpServletRequest request = getRequest();
		
		String username = request.getParameter("loginPageUsername");
		if(!validInput(username, "^[a-zA-Z0-9 ]{1,12}$")) {
			return 0;
		}
		
		username = StringUtil.deFormatUsername(username);
		
		String password = request.getParameter("loginPagePassword");
		if(!validInput(password, "^[a-zA-Z0-9]{5,20}$")) {
			return 1;
		}
		
		LoginSessionDAO dao = new LoginSessionDAO(getDb());
		
		if(!floodCheck(dao)) {
			return 2;
		}

		Calendar startCal = Calendar.getInstance();
		startCal.setTimeInMillis(getRequestTime());
		
		PasswordRequestDTO userInfo = dao.getUserInfo(username, startCal, getRequestIP());
		if(userInfo == null) {
			return 1;
		}
		
		Password userPassword = new Password(userInfo.getPasswordHash(), userInfo.getPasswordSalt());
		if(!userPassword.equals(password)) {
			return 1;
		}
		
		boolean secure = ControllerConstants.SECURE_MOD_LIST.contains(toMod);
		String sessionHash = Hashing.generateSessionHash();
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(getRequestTime());
		endCal.add(Calendar.MINUTE, secure ? SECURE_IDLE_TIME : IDLE_TIME);
		
		dao.submitLoginSession(userInfo.getUserId(), getRequestIP(), sessionHash, startCal, endCal, toMod, toDest, secure);
		request.getSession().setAttribute("sessionHash", Hashing.shuffle(sessionHash));
		setRedirecting(true);
		UrlUtil.redirect(getResponse(), toMod, toDest + toQuery);
		
		return -1;
	}
	
	private boolean floodCheck(LoginSessionDAO dao) {
		String ip = getRequestIP();
		
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, -5);
		int max1 = 3;
		if(dao.floodCheck(ip, cal1, max1)) {
			return false;
		}
		
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, -10);
		int max2 = 5;
		if(dao.floodCheck(ip, cal2, max2)) {
			return false;
		}
		
		Calendar cal3 = Calendar.getInstance();
		cal3.add(Calendar.MINUTE, -15);
		int max3 = 10;
		if(dao.floodCheck(ip, cal3, max3)) {
			return false;
		}
		
		return true;
	}
	
	private boolean validInput(String input, String regex) {
		if(input == null || input.equals("")) {
			return false;
		}
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if(!matcher.find()) {
			return false;
		}
		
		return true;
	}
	
	private void setupRedirectLocation() {
		HttpServletRequest request = getRequest();
		
		String toMod = request.getParameter("mod");
		String toDest = request.getParameter("dest");
		
		if(toMod == null || toDest == null) {
			setRedirecting(true);
			UrlUtil.sendHome(getResponse());
			return;
		}
		
		String toQuery = "";
		if(toDest.indexOf('?') != -1) {
			toQuery = toDest.substring(toDest.indexOf('?'));
			toDest = toDest.substring(0, toDest.indexOf('?'));
		}
		
		if(!ControllerConstants.CONTROLLER_MAP.containsKey(toMod + " " + toDest)) {
			setRedirecting(true);
			UrlUtil.sendHome(getResponse());
			return;
		}
		
		if(toDest.equals("login.ws") || toDest.equals("logout.ws")) {
			setRedirecting(true);
			UrlUtil.sendHome(getResponse());
			return;
		}
		
		this.toMod = toMod;
		this.toDest = toDest;
		this.toQuery = toQuery;
		
		request.setAttribute("toMod", toMod);
		request.setAttribute("toDest", toDest + toQuery);
	}

}
