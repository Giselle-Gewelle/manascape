package org.manascape.controller.impl.account.management;

import java.util.List;

import org.manascape.controller.Controller;
import org.manascape.db.dao.account.TicketingDAO;
import org.manascape.dto.TicketInboxDTO;

public final class Ticketing extends Controller {

	private TicketingDAO dao;
	
	@Override
	public void init() {
		if(isRedirecting()) {
			return;
		}
		
		dao = new TicketingDAO(getDb(), getLoginSession().getUser());
		
		switch(getDest()) {
			case "inbox.ws":
				prepareInbox();
				break;
		}
	}
	
	private void prepareInbox() {
		List<TicketInboxDTO> received = dao.getMessages("Unread");
		List<TicketInboxDTO> sent = dao.getMessages("Sent");
		List<TicketInboxDTO> read = dao.getMessages("Read");
		
		if(received != null) {
			getRequest().setAttribute("receivedList", received);
		}
		if(sent != null) {
			getRequest().setAttribute("sentList", sent);
		}
		if(read != null) {
			getRequest().setAttribute("readList", read);
		}
	}

	@Override
	public boolean isSecure() {
		return true;
	}

	@Override
	public boolean holdSecureSession() {
		return true;
	}

	@Override
	public boolean loginRequired() {
		return true;
	}

}
