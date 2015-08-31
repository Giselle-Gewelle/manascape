package org.manascape.controller.impl.account.management;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.manascape.controller.Controller;
import org.manascape.db.DatabaseHandler;
import org.manascape.db.dao.account.TicketingDAO;
import org.manascape.dto.TicketInboxDTO;
import org.manascape.dto.TicketMessageDTO;
import org.manascape.dto.TicketThreadDTO;
import org.manascape.http.HttpRequestType;
import org.manascape.http.RequestHandler;
import org.manascape.util.StringUtil;
import org.manascape.util.UrlUtil;

public final class Ticketing extends Controller {

	private static final List<String> QUERY_TYPES = new ArrayList<String>() {
		
		private static final long serialVersionUID = 7272964870205325366L;

		{
			add("privacy");
			add("complaint");
			add("feedback");
			add("other");
		}
		
	};
	
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
			case "delete.ws":
				prepareDelete();
				break;
			case "query.ws":
				prepareQuery();
				break;
		}
	}
	
	private void prepareQuery() {
		if(getLoginSession().getUser().isSupportDisabled()) {
			getRequest().setAttribute("supportDisabled", true);
			return;
		}
		
		String preQueryType = getRequest().getParameter("type");
		if(preQueryType != null) {
			if(QUERY_TYPES.contains(preQueryType)) {
				getRequest().setAttribute("queryType", preQueryType);
			}
		}
		
		if(getRequestType().equals(HttpRequestType.POST)) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -15);
			if(dao.flooding(cal, 1)) {
				getRequest().setAttribute("flooding", true);
				return;
			}
			
			boolean success = true;
			
			String queryType = validateQueryType();
			if(queryType == null) {
				success = false;
				getRequest().setAttribute("queryTypeError", true);
			} else {
				getRequest().setAttribute("queryType", queryType);
			}
			
			String message = getRequest().getParameter("inputMessage");
			if(message == null) {
				success = false;
				getRequest().setAttribute("messageError", 0);
			} else {
				message = message.trim();
				if(message.length() < 1 || message.length() > 1024) {
					success = false;
					getRequest().setAttribute("messageError", 1);
				} else {
					getRequest().setAttribute("message", message);
				}
			}
			
			if(success) {
				if(!dao.submitNewThread(getLoginSession().getUser().getUsername(), null, "Support Query: " + StringUtil.formatUsername(queryType), true, message)) {
					getRequest().setAttribute("submissionError", true);
				} else {
					getRequest().setAttribute("successful", true);
				}
			}
		}
	}
	
	private String validateQueryType() {
		String queryType = getRequest().getParameter("inputType");
		if(queryType == null) {
			return null;
		}
		
		if(!QUERY_TYPES.contains(queryType)) {
			return null;
		}
		
		return queryType;
	}
	
	private void prepareDelete() {
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
			
			if(lastMessage.getAuthor().equals(getLoginSession().getUser().getDisplayName())) {
				if(!dao.authorDelete(thread.getThreadId())) {
					getRequest().setAttribute("error", true);
					return;
				}
			} else if(lastMessage.getReceiver().equals(getLoginSession().getUser().getDisplayName())) {
				if(!dao.receiverDelete(thread.getThreadId())) {
					getRequest().setAttribute("error", true);
					return;
				}
			}
			
			setRedirecting(true);
			UrlUtil.redirect(getResponse(), "ticketing", "inbox.ws");
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
			
			boolean canReply = true;
			if(getLoginSession().getUser().isStaff()) {
				String canReplyStr = getRequest().getParameter("inputCanReply");
				if(canReplyStr == null) {
					canReply = false;
				} else {
					if(canReplyStr.equals("yes")) {
						canReply = true;
					}
				}
			}
			
			if(!dao.sendReply(thread.getThreadId(), lastMessage.getAuthor(), reply, canReply)) {
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
		
		boolean authorDel = false;
		boolean receiverDel = false;
		boolean canReply = thread.isCanReply();
		if(lastMessage.getAuthor().equals(getLoginSession().getUser().getDisplayName())) {
			if(authorDel) {
				return null;
			}
			
			// User can not reply to their own message
			canReply = false;
		} else if(lastMessage.getReceiver().equals(getLoginSession().getUser().getDisplayName())) {
			if(receiverDel) {
				return null;
			}
			
			// Set the message to read for the receiver
			dao.setMessageRead(lastMessage.getMessageId());
		} else {
			return null;
		}
		
		thread.setCanReply(canReply);
		
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
