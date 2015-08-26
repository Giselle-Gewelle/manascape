package org.manascape.http;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.manascape.Config;
import org.manascape.controller.Controller;
import org.manascape.controller.ControllerConstants;
import org.manascape.db.DatabaseHandler;

public final class RequestHandler {
	
	private static final Logger LOG = Logger.getLogger(RequestHandler.class);
	
	public static void sendError(HttpServletResponse response, int errorCode) {
		try {
			response.sendError(errorCode);
		} catch(IOException e) {
			LOG.error("IOException occured while attempting to send an error code back to the client.", e);
		}
	}
	
	public static int getIntParam(HttpServletRequest request, String name) {
		String s = request.getParameter(name);
		if(s == null || s.length() < 1) {
			return -1;
		}
		
		try {
			return Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
	public static long getLongParam(HttpServletRequest request, String name) {
		String s = request.getParameter(name);
		if(s == null || s.length() < 1) {
			return -1;
		}
		
		try {
			return Long.parseLong(s);
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
	static void submitViewRequest(HttpRequestType requestType, HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		String requestIP = request.getRemoteAddr();
		long requestTime = Calendar.getInstance().getTimeInMillis();
		
		String dest = "";
		if(uri != null && uri.length() > 1) {
			dest = uri.substring(1);
		} else {
			sendError(response, 404);
			return;
		}
		
		int truncateUriIdx = Config.getHostName().indexOf('/');
		if(truncateUriIdx >= 0) {
			String stringToTruncate = Config.getHostName().substring(truncateUriIdx, Config.getHostName().length());
			dest = dest.substring(stringToTruncate.length());
		}
		
		String mod = "main";
		if(dest.indexOf("m=") == 0) {
			int endIdx = dest.indexOf('/');
			mod = dest.substring(2,  endIdx);
			dest = dest.substring(endIdx + 1, dest.length());
		}
		
		if(!ControllerConstants.CONTROLLER_MAP.containsKey(mod + " " + dest)) {
			LOG.info("Controller not found: " + mod + ":" + dest);
			sendError(response, 404);
			return;
		}
		
		LOG.info("View request received for " + mod + ":" + dest + " by " + requestIP + " at " + requestTime);

		Controller controller = null;
		try {
			Class<? extends Controller> controllerClass = ControllerConstants.CONTROLLER_MAP.get(mod + " " + dest);
			controller = controllerClass.newInstance();
			
			if(Config.isSslEnabled()) {
				if(controller.isSecure() && !request.isSecure()) {
					// TODO make this more secure (for the client?)
					LOG.warn("Client attempted an insecure connection on a secure-only section of the website, 403 response returned.");
					sendError(response, 403);
					return;
				}
			}
		} catch(Exception e) {
			LOG.error("Controller is null: " + mod + ":" + dest, e);
			sendError(response, 404);
			return;
		}
		
		DatabaseHandler db = null;
		
		try {
			db = new DatabaseHandler();
			
			if(db.isClosed()) {
				LOG.error("The database connection that was fetched is already closed!");
				sendError(response, 503);
				return;
			}
		} catch(SQLException e) {
			LOG.error("SQLException occured while attempting to fetch a database connection.", e);
			sendError(response, 503);
			return;
		}
		
		try {
			controller.setup(request, response, requestType, requestIP, requestTime, db, mod, dest);
			
			if(!controller.isRedirecting()) {
				controller.init();
			}
			
			if(!controller.isRedirecting()) {
				if(controller.getJsonData() != null) {
					response.getWriter().write(controller.getJsonData().toString());
				} else {
					request.setAttribute("rsTime", requestTime);
					request.setAttribute("hostName", Config.getHostName());
					request.setAttribute("formattedHostName", Config.getFormattedHostName());
					request.setAttribute("sslEnabled", Config.isSslEnabled());
					request.setAttribute("gameName", Config.getGameName());
					request.setAttribute("companyName", Config.getCompanyName());
					request.setAttribute("securePage", controller.isSecure());
					
					String page = dest + ".ftl";
					if(controller.getActualPage() != null) {
						page = controller.getActualPage() + ".ftl";
					}
					
					char s = '/';
					String location = new StringBuilder().append(s).append("WEB-INF").append(s).append("view").append(s).append("content").append(s).append(mod).append(s).append(page).toString();
					RequestDispatcher dispatcher = request.getRequestDispatcher(location);
					if(dispatcher != null && !response.isCommitted()) {
						dispatcher.forward(request, response);
					}
				}
			}
		} catch(Exception e) {
			LOG.error("Exception occured while attempting to load the controller for a view request.", e);
			sendError(response, 500);
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}
	
}
