package org.manascape.controller.impl.staff;

import org.manascape.controller.Controller;
import org.manascape.util.UrlUtil;

/**
 * Content controller for all staff center pages.
 * @author DTB
 */
public class StaffPage extends Controller {
	
	private boolean authorized;
	
	@Override
	public void init() {
		if(!getLoginSession().getUser().isStaff()) {
			setRedirecting(true);
			
			UrlUtil.redirect(getResponse(), "main", "title.ws");
			
			authorized = false;
		} else {
			authorized = true;
		}
	}
	
	protected final boolean isAuthorized() {
		return authorized;
	}
	
	@Override
	public boolean isSecure() {
		return true;
	}
	
	@Override
	public boolean loginRequired() {
		return true;
	}

}
