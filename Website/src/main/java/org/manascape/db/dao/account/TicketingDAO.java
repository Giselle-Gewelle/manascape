package org.manascape.db.dao.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.TicketInboxDTO;
import org.manascape.dto.UserSessionDTO;
import org.manascape.util.DateUtil;

public final class TicketingDAO {
	
	private static final Logger LOG = Logger.getLogger(TicketingDAO.class);
	
	private final DatabaseHandler db;
	private final UserSessionDTO user;
	
	public TicketingDAO(DatabaseHandler db, UserSessionDTO user) {
		this.db = db;
		this.user = user;
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
