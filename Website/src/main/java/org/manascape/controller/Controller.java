package org.manascape.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.manascape.Config;
import org.manascape.db.DatabaseHandler;
import org.manascape.http.HttpRequestType;
import org.manascape.security.LoginSession;
import org.manascape.util.UrlUtil;

public abstract class Controller {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpRequestType requestType;
	private String requestIP;
	private long requestTime;
	private DatabaseHandler db;
	private String mod;
	private String dest;
	
	private JSONObject jsonData;
	
	private LoginSession loginSession;
	
	private boolean redirecting;
	
	public void setup(HttpServletRequest request, HttpServletResponse response, HttpRequestType requestType, String requestIP, long requestTime, 
			DatabaseHandler db, String mod, String dest) {
		this.request = request;
		this.response = response;
		this.requestType = requestType;
		this.requestIP = requestIP;
		this.requestTime = requestTime;
		this.db = db;
		this.mod = mod;
		this.dest = dest;
		this.redirecting = false;
		
		String queryString = request.getQueryString();
		if(queryString == null) {
			queryString = "";
		} else {
			queryString = "?" + queryString;
		}
		
		/*
		 * Check for an active login session.
		 */
		this.loginSession = new LoginSession(db, this, request);
		if(!this.loginSession.isLoggedIn() && this.loginRequired()) {
			this.redirecting = true;
			UrlUtil.redirect(getResponse(), "account", "login.ws?mod=" + mod + "&dest=" + dest + queryString);
			return;
		}
		
		request.setAttribute("loginSession", loginSession);
		request.setAttribute("currentMod", mod);
		request.setAttribute("currentDest", dest);
		request.setAttribute("currentQuery", queryString);
		request.setAttribute("currentFullDest", dest + queryString);
		
		this.jsonData = null;
	}
	
	public abstract void init();
	
	protected final void setJsonData(JSONObject jsonData) {
		this.jsonData = jsonData;
	}
	
	protected final void setRedirecting(boolean redirecting) {
		this.redirecting = redirecting;
	}
	
	/**
	 * Whether or not this page must be accessed through HTTPS (if SSL is enabled in the configuration file).
	 * @return True if it is secure, false if not.
	 */
	public boolean isHTTPS() {
		return Config.isSslEnabled();
	}
	
	/**
	 * Whether or not the page requires a secure login session (DB flag).
	 * @return True if a secure flag is required, false if not.
	 */
	public boolean isSecure() {
		return false;
	}
	
	/**
	 * Whether or not to update/refresh a secure login session.
	 * @return True if the session should be refreshed, false if it should expire after the given time runs out.
	 */
	public boolean holdSecureSession() {
		return true;
	}
	
	/**
	 * Whether or not the user must be logged in to view the current page.
	 * @return True if they must be logged in, false if not.
	 */
	public boolean loginRequired() {
		return false;
	}
	
	/**
	 * Gets the actual FTL (FreeMarker) page for this page.
	 * @return Null if the default path is desired, or a string containing the desired endpoint file.
	 */
	public String getActualPage() {
		return null;
	}
	
	protected final HttpServletRequest getRequest() {
		return request;
	}
	
	protected final HttpServletResponse getResponse() {
		return response;
	}
	
	protected final HttpRequestType getRequestType() {
		return requestType;
	}
	
	public final String getRequestIP() {
		return requestIP;
	}
	
	public final long getRequestTime() {
		return requestTime;
	}
	
	protected final DatabaseHandler getDb() {
		return db;
	}
	
	public final String getMod() {
		return mod;
	}
	
	public final String getDest() {
		return dest;
	}
	
	public final LoginSession getLoginSession() {
		return loginSession;
	}
	
	public final boolean isRedirecting() {
		return redirecting;
	}
	
	public final JSONObject getJsonData() {
		return jsonData;
	}
	
}
