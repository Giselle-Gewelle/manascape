package org.manascape.controller.impl.staff.impl;

import org.manascape.controller.impl.staff.StaffPage;

/**
 * Content controller all staff center "news" pages.
 * @author DTB
 */
public final class StaffNews extends StaffPage {
	
	@Override
	public void init() {
		super.init();
		
		if(!isAuthorized()) {
			return;
		}
		
		
	}
	
	@Override
	public String getActualPage() {
		return getDest().replace("news", "news/");
	}
	
}
