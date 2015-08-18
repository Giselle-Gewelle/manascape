package org.manascape.http;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.manascape.Config;
import org.manascape.db.MSDataSource;

@MultipartConfig
public final class MSServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2464603724605833400L;
	
	private static final Logger LOG = Logger.getLogger(MSServlet.class);
	
	@Override
	public void init() {
		Config.init();
		MSDataSource.init();
		
		LOG.info("MSServlet initialized.");
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		RequestHandler.submitViewRequest(HttpRequestType.GET, request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		RequestHandler.submitViewRequest(HttpRequestType.POST, request, response);
	}

}
