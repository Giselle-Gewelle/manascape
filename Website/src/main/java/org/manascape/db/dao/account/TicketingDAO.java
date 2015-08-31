package org.manascape.db.dao.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.manascape.Config;
import org.manascape.db.Call;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.TicketInboxDTO;
import org.manascape.dto.TicketMessageDTO;
import org.manascape.dto.TicketThreadDTO;
import org.manascape.dto.UserSessionDTO;
import org.manascape.util.DateUtil;
import org.manascape.util.StringUtil;

public final class TicketingDAO {
	
	private static final Logger LOG = Logger.getLogger(TicketingDAO.class);
	
	private final DatabaseHandler db;
	private final UserSessionDTO user;
	
	public TicketingDAO(DatabaseHandler db, UserSessionDTO user) {
		this.db = db;
		this.user = user;
	}
	
	public boolean flooding(Calendar cal, int max) {
		try {
			Call dbCall = db.prepareCall("user_ticketFloodCheck", 4)
				.setString("ip", user.getCurrentIP())
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(cal.getTime()))
				.setInt("max", max + 1)
				.registerOut("count", Types.SMALLINT)
				.execute();
			
			return dbCall.getInt("count") >= max;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to check if the user [" + user.getUsername() + "] is attempting to flood the ticketing system.", e);
			return true;
		}
	}
	
	public boolean submitNewThread(String authorName, String receiverName, String title, boolean canReply, String message) {
		try {
			Call dbCall = db.prepareCall("user_ticketNewThread", 10)
				.setString("title", title)
				.setBoolean("canReply", canReply)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setString("author", authorName)
				.setBoolean("authorStaff", user.isStaff())
				.setLong("authorId", user.getId())
				.setString("authorIP", user.getCurrentIP())
				.setString("receiver", receiverName)
				.setString("message", message)
				.registerOut("success", Types.BOOLEAN)
				.execute();
			
			return dbCall.getBoolean("success");
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to submit a new ticket thread, requested by [" + user.getUsername() + "].", e);
			return false;
		}
	}
	
	public boolean receiverDelete(long threadId) {
		try {
			db.prepareCall("user_ticketReceiverDelete", 1)
				.setLong("id", threadId)
				.execute();
			
			return true;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to delete a ticketing thread of the receiver [" + user.getUsername() + "].", e);
			return false;
		}
	}
	
	public boolean authorDelete(long threadId) {
		try {
			db.prepareCall("user_ticketAuthorDelete", 1)
				.setLong("id", threadId)
				.execute();
			
			return true;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to delete a ticketing thread of the author [" + user.getUsername() + "].", e);
			return false;
		}
	}
	
	public boolean sendReply(long threadId, String receiverName, String message, boolean canReply) {
		try {
			Call dbCall = db.prepareCall("user_ticketReply", 10)
				.setLong("id", threadId)
				.setLong("userId", user.getId())
				.setString("username", user.getUsername())
				.setString("userIP", user.getCurrentIP())
				.setBoolean("userStaff", user.isStaff())
				.setString("receiver", receiverName)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setString("message", message)
				.setBoolean("canReply", canReply)
				.registerOut("messageId", Types.BIGINT)
				.execute();
			
			return dbCall.getLong("messageId") > 0;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to submit a reply to the ticket [" + threadId + "] by the user [" + user.getUsername() + "].", e);
			return false;
		}
	}
	
	public void setMessageRead(long id) {
		try {
			db.prepareCall("user_ticketSetMessageRead", 2)
				.setLong("id", id)
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.execute();
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to set the message [" + id + "] as read by the user [" + user.getUsername() + "].", e);
		}
	}
	
	public TicketThreadDTO getThread(long id) {
		try {
			Call dbCall = db.prepareCall("user_ticketGetThread", 5)
				.setLong("id", id)
				.registerOut("title", Types.VARCHAR)
				.registerOut("canReply", Types.BOOLEAN)
				.registerOut("receiverDel", Types.BOOLEAN)
				.registerOut("authorDel", Types.BOOLEAN)
				.execute();
			
			String title = dbCall.getString("title");
			boolean canReply = dbCall.getBoolean("canReply");
			boolean receiverDel = dbCall.getBoolean("receiverDel");
			boolean authorDel = dbCall.getBoolean("authorDel");
			
			ResultSet results = dbCall.getResults(false);
			if(results == null) {
				return null;
			}
			
			List<TicketMessageDTO> messageList = new LinkedList<>();
			while(results.next()) {
				Date readOn = results.getTimestamp("readOn");
				long authorId = results.getLong("authorId");
				String receiver = results.getString("receiver");
				if(receiver == null) {
					receiver = Config.getCompanyName();
				} else {
					receiver = StringUtil.formatUsername(receiver);
				}
				
				messageList.add(new TicketMessageDTO(
					results.getLong("id"), DateUtil.SHORT_DATETIME_FORMAT.format(results.getTimestamp("date")), StringUtil.formatUsername(results.getString("author")), 
					results.getBoolean("authorStaff"), authorId, results.getString("authorIP"), receiver, results.getString("message"), 
					(readOn == null ? null : DateUtil.SHORT_DATETIME_FORMAT.format(readOn))
				));
			}
			
			if(messageList.size() < 1) {
				return null;
			}
			
			return new TicketThreadDTO(id, title, canReply, authorDel, receiverDel, messageList);
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch a support ticket thread with the id [" + id + "] for the user [" + user.getUsername() + "].", e);
			return null;
		}
	}
	
	public List<TicketInboxDTO> getMessages(String type) {
		try {
			ResultSet results = db.prepareCall("user_ticket" + type + "Messages", 1)
				.setString("username", user.getUsername())
				.getResults();
			
			if(results == null) {
				return null;
			}
			
			List<TicketInboxDTO> ticketList = new LinkedList<>();
			while(results.next()) {
				ticketList.add(new TicketInboxDTO(results.getLong("id"), results.getString("title"), 
						DateUtil.SHORT_DATETIME_FORMAT.format(results.getTimestamp("lastMessageDate")), results.getInt("messageCount")));
			}
			
			if(ticketList.size() < 1) {
				return null;
			}
			
			return ticketList;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch ticket replies of the type [" + type + "] for the user [" + user.getUsername() + "].", e);
			return null;
		}
	}
	
}
