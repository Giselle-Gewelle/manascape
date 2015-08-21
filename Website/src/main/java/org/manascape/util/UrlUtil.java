package org.manascape.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.manascape.Config;

public final class UrlUtil {
	
	private static final Logger LOG = Logger.getLogger(UrlUtil.class);
	
	public static void sendHome(HttpServletResponse response) {
		redirect(response, "main", "title.ws");
	}
	
	public static void redirect(HttpServletResponse response, String mod, String dest) {
		if(!mod.equals("main")) {
			mod = "/m=" + mod;
		} else {
			mod = "";
		}
		
		try {
			response.sendRedirect((Config.isSslEnabled() ? "https" : "http") + "://www." + Config.getHostName() + mod + "/" + dest);
		} catch(IOException e) {
			LOG.error("IOException occurred while attempting to redirect a user to " + mod + ":" + dest, e);
		}
	}
	
}
