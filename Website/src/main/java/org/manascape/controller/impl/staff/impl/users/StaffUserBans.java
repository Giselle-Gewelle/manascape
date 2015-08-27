package org.manascape.controller.impl.staff.impl.users;

import org.manascape.controller.impl.staff.StaffPage;

public final class StaffUserBans extends StaffPage {
	
	@Override
	public void init() {
		super.init();
		
		if(!isAuthorized()) {
			return;
		}
		
		switch(getDest()) {
			case "userbansedit.ws":
				
				break;
		}
	}
	
	@Override
	public String getActualPage() {
		return getDest().replace("userbans", "user/bans/");
	}
	
}
