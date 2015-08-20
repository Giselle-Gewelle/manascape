package org.manascape.controller.impl.account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.manascape.controller.Controller;
import org.manascape.db.dao.account.CreateAccountDAO;
import org.manascape.http.HttpRequestType;
import org.manascape.security.Password;
import org.manascape.util.CountryUtil;
import org.manascape.util.DateUtil;
import org.manascape.util.StringUtil;
import org.manascape.util.UrlUtil;

public final class CreateAccount extends Controller {

	private static final Logger LOG = Logger.getLogger(CreateAccount.class);
	
	private CreateAccountDAO mainDao = null;
	
	private Date dob;
	private String countryCode;
	private String username;
	private String password;
	
	@Override
	public void init() {
		mainDao = new CreateAccountDAO(getDb());
		
		switch(getDest()) {
			case "index.ws":
				getRequest().setAttribute("countryMap", CountryUtil.COUNTRY_MAP);
				setupIndex();
				break;
			case "checkusername.ws":
				setupCheckUsername();
				break;
			case "submit.ws":
				submit();
				break;
		}
	}
	
	private void setupIndex() {
		if(validateForm(false)) {
			getRequest().setAttribute("passState", true);
			getRequest().setAttribute("dob", DateUtil.CREATE_FORMAT.format(this.dob));
			getRequest().setAttribute("country", countryCode);
			getRequest().setAttribute("username", this.username);
		}
	}
	
	private void submit() {
		if(!getRequestType().equals(HttpRequestType.POST)) {
			setRedirecting(true);
			UrlUtil.redirect(getResponse(), "create", "index.ws");
			return;
		}
		
		if(!validateForm(true)) {
			getRequest().setAttribute("error", true);
			return;
		}
		
		try {
			if(!checkRecaptcha()) {
				setRedirecting(true);
				UrlUtil.redirect(getResponse(), "create", "index.ws?dob=" + DateUtil.CREATE_FORMAT.format(this.dob) + "&country=" + countryCode + "&username=" + username + "&terms=on");
				return;
			}
		} catch(IOException e) {
			LOG.error("IOException occurred while attempting to check a recaptcha response value during account creation.", e);
			setRedirecting(true);
			UrlUtil.redirect(getResponse(), "create", "index.ws?dob=" + DateUtil.CREATE_FORMAT.format(this.dob) + "&country=" + countryCode + "&username=" + username + "&terms=on");
			return;
		}
		
		if(!floodCheck()) {
			getRequest().setAttribute("flooding", true);
			return;
		}
		
		Password password = new Password(this.password);
		if(!mainDao.createAccount(username, password, DateUtil.SQL_DATE_FORMAT.format(dob), countryCode, getRequestIP())) {
			getRequest().setAttribute("error", true);
		}
	}
	
	private boolean checkRecaptcha() throws IOException {
		String captchaResponse = getRequest().getParameter("g-recaptcha-response");
		if(captchaResponse == null || captchaResponse.equals("")) {
			return false;
		}
		
		URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
	    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	    writer.write("secret=6Lc8kAsTAAAAAJaaXDBjkaxLdW1g_wHCHFGUENax&response=" + URLEncoder.encode(captchaResponse, "UTF-8") + "&remoteip=" + getRequestIP());
	    writer.flush();
	    String line;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    StringBuilder str = new StringBuilder();
	    while((line = reader.readLine()) != null) {
	    	str.append(line);
	    }
	    writer.close();
	    reader.close();
	    
	    JSONObject obj = new JSONObject(str.toString());
	    if(obj.getBoolean("success")) {
	    	return true;
	    }
	    
	    return false;
	}
	
	private boolean floodCheck() {
		String ip = getRequestIP();
		
		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 5);
		int max1 = 3;
		if(mainDao.floodCheck(ip, cal1, max1)) {
			return false;
		}
		
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, 30);
		int max2 = 5;
		if(mainDao.floodCheck(ip, cal2, max2)) {
			return false;
		}
		
		Calendar cal3 = Calendar.getInstance();
		cal3.add(Calendar.MINUTE, 120);
		int max3 = 10;
		if(mainDao.floodCheck(ip, cal3, max3)) {
			return false;
		}
		
		Calendar cal4 = Calendar.getInstance();
		cal4.add(Calendar.MINUTE, 1440);
		int max4 = 15;
		if(mainDao.floodCheck(ip, cal4, max4)) {
			return false;
		}
		
		return true;
	}
	
	private boolean validateForm(boolean checkPass) {
		if(!validateDob()) {
			return false;
		}
		
		if(!validateCountry()) {
			return false;
		}
		
		if(validateUsername() != -1) {
			return false;
		}
		
		if(!validateTerms()) {
			return false;
		}
		
		if(checkPass && !validatePasswords()) {
			return false;
		}
		
		return true;
	}
	
	private boolean validatePasswords() {
		String pass1 = getRequest().getParameter("password1");
		String pass2 = getRequest().getParameter("password2");
		
		if(pass1 == null || pass2 == null || pass1.equals("") || pass2.equals("")) {
			return false;
		}
		
		if(pass1.length() < 5 || pass1.length() > 20) {
			return false;
		}
		
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{5,20}$");
		Matcher matcher = pattern.matcher(pass1);
		if(!matcher.find()) {
			return false;
		}
		
		if(!pass1.equals(pass2)) {
			return false;
		}
		
		this.password = pass1;
		return true;
	}
	
	private boolean validateTerms() {
		String termStr = getRequest().getParameter("terms");
		if(termStr == null || termStr.equals("")) {
			return false;
		}
		
		if(!termStr.equals("on")) {
			return false;
		}
		
		return true;
	}
	
	private int validateUsername() {
		String username = getRequest().getParameter("username");
		if(username == null || username.equals("")) {
			return 0;
		}
		
		// Get rid of leading/trailing white space, as well as double-spaces.
		username = username.trim();
		while(username.contains("  ")) {
			username = username.replace("  ", " ");
		}
		
		if(username.length() < 1 || username.length() > 12) {
			return 1;
		}
		
		// Get the username Database-ready
		username = StringUtil.deFormatUsername(username);
		
		Pattern pattern = Pattern.compile("^[a-z0-9_]{1,12}$");
		Matcher matcher = pattern.matcher(username);
		if(!matcher.find()) {
			return 2;
		}
		
		int checkUsernameCode = mainDao.checkUsername(username);
		if(checkUsernameCode == -1) {
			this.username = username;
		}
		return checkUsernameCode;
	}
	
	private boolean validateCountry() {
		String countryCode = getRequest().getParameter("country");
		if(countryCode == null || countryCode.equals("")) {
			return false;
		}
		
		if(countryCode.length() < 1 || countryCode.length() > 3) {
			return false;
		}
		
		if(!CountryUtil.validCountry(countryCode)) {
			return false;
		}
		
		this.countryCode = countryCode;
		return true;
	}
	
	private boolean validateDob() {
		String dobStr = getRequest().getParameter("dob");
		if(dobStr == null || dobStr.equals("")) {
			return false;
		}
		
		if(dobStr.length() < 8 || dobStr.length() > 10) {
			return false;
		}
		
		String[] parts = dobStr.split("/");
		if(parts.length != 3) {
			return false;
		}
		
		int day = -1;
		int month = -1;
		int year = -1;
		
		try {
			day = Integer.parseInt(parts[0]);
			month = Integer.parseInt(parts[1]);
			year = Integer.parseInt(parts[2]);
		} catch(NumberFormatException e) {
			return false;
		}
		
		if(day < 1 || day > 31) {
			return false;
		}
		if(month < 1 || month > 12) {
			return false;
		}
		if(year < 1900 || year > 2015) {
			return false;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, 0, 0, 0);
		
		if(cal.get(Calendar.YEAR) != year) {
			return false;
		}
		if(cal.get(Calendar.MONTH) != month) {
			return false;
		}
		if(cal.get(Calendar.DAY_OF_MONTH) != day) {
			return false;
		}
		
		this.dob = cal.getTime();
		return true;
	}
	
	private void setupCheckUsername() {
		JSONObject object = new JSONObject();
		object.put("code", validateUsername());
		setJsonData(object);
	}

}
