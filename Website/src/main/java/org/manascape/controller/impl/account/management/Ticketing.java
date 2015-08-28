package org.manascape.controller.impl.account.management;

import java.util.List;

import org.manascape.controller.Controller;
import org.manascape.db.DatabaseHandler;
import org.manascape.db.dao.account.TicketingDAO;
import org.manascape.dto.TicketInboxDTO;
import org.manascape.dto.TicketMessageDTO;
import org.manascape.dto.TicketThreadDTO;
import org.manascape.http.HttpRequestType;
import org.manascape.http.RequestHandler;
import org.manascape.util.UrlUtil;

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
			case "view.ws":
				prepareView();
				break;
			case "reply.ws":
				prepareReply();
				break;
		}
	}
	
	private void prepareReply() {
		TicketThreadDTO thread = getThread();
		if(thread == null) {
			return;
		}
		
		getRequest().setAttribute("thread", thread);
		
		TicketMessageDTO lastMessage = thread.getMessageList().get(thread.getMessageList().size() - 1);
		getRequest().setAttribute("lastMessage", lastMessage);
		
		if(getRequestType().equals(HttpRequestType.POST)) {
			if(getRequest().getParameter("inputCancel") != null) {
				setRedirecting(true);
				UrlUtil.redirect(getResponse(), "ticketing", "view.ws?id=" + thread.getThreadId());
				return;
			}
			
			if(getRequest().getParameter("inputSubmit") == null) {
				return;
			}
			
			String reply = getRequest().getParameter("inputMessage");
			if(reply == null) {
				getRequest().setAttribute("errorCode", 0);
				return;
			}
			
			int length = 1024;
			if(getLoginSession().getUser().isStaff()) {
				length = DatabaseHandler.MAX_LENGTH_TEXT;
			}
			
			reply = reply.trim();
			if(reply.length() < 1 || reply.length() > length) {
				getRequest().setAttribute("errorCode", 1);
				return;
			}
			
			// TODO canReply
			if(!dao.sendReply(thread.getThreadId(), lastMessage.getReceiver(), reply, true)) {
				getRequest().setAttribute("errorCode", 2);
			} else {
				getRequest().setAttribute("successful", true);
			}
		}
	}
	
	private void prepareView() {
		TicketThreadDTO thread = getThread();
		if(thread != null) {
			getRequest().setAttribute("thread", thread);
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
	
	/**
	 * Fetches the thread via the thread id in the URL (param name = "id"), then sets the latest message in that thread to "read".
	 * @return TicketThreadDTO, or null if a valid thread is not found.
	 */
	private TicketThreadDTO getThread() {
		long threadId = RequestHandler.getLongParam(getRequest(), "id");
		if(threadId < 1 || threadId > DatabaseHandler.MAX_VALUE_INT) {
			return null;
		}
		
		TicketThreadDTO thread = dao.getThread(threadId);
		if(thread == null) {
			return null;
		}
		
		TicketMessageDTO lastMessage = thread.getMessageList().get(thread.getMessageList().size() - 1);
		dao.setMessageRead(lastMessage.getMessageId());
		
		return thread;
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
