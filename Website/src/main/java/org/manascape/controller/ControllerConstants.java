package org.manascape.controller;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.manascape.controller.impl.GenericPage;
import org.manascape.controller.impl.account.CreateAccount;
import org.manascape.controller.impl.account.sessions.Login;

public final class ControllerConstants {
	
	public static final List<String> SECURE_MOD_LIST = new ArrayList<String>() {
		
		private static final long serialVersionUID = 1620735289983287126L;

		{
			add("password");
			add("recovery");
			add("offense");
		}
		
	};
	
	public static final Map<String, Class<? extends Controller>> CONTROLLER_MAP = new HashMap<String, Class<? extends Controller>>() {

		private static final long serialVersionUID = -6523666098696536388L;
		
		{
			put("main title.ws", GenericPage.class);
			
			put("create index.ws", CreateAccount.class);
			put("create checkusername.ws", CreateAccount.class);
			put("create submit.ws", CreateAccount.class);
			
			put("account login.ws", Login.class);
		}
		
	};

}
