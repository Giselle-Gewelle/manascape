package org.manascape.controller.impl.staff.impl.users;

import org.manascape.controller.impl.staff.StaffPage;

public final class StaffTicketing extends StaffPage {

	@Override
	public void init() {
		super.init();
		
		if(!isAuthorized()) {
			return;
		}
		
		
	}
	
	@Override
	public String getActualPage() {
		return getDest().replace("ticket", "ticketing/");
	}
	
}
