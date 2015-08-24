package org.manascape.db.dao.media;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.NewsFeedDTO;
import org.manascape.util.DateUtil;

public final class NewsDAO {
	
	private static final Logger LOG = Logger.getLogger(NewsDAO.class);
	
	private final DatabaseHandler db;
	
	public NewsDAO(DatabaseHandler db) {
		this.db = db;
	}
	
	public List<NewsFeedDTO> getNewsFeed() {
		try {
			ResultSet results = db.prepareCall("media_getNewsFeed", 1)
				.setInt("limit", 2)
				.getResults();
			
			if(results == null) {
				return null;
			}
			
			List<NewsFeedDTO> newsFeed = new LinkedList<>();
			while(results.next()) {
				newsFeed.add(new NewsFeedDTO(results.getInt("id"), results.getString("title"), DateUtil.NEWS_FEED_FORMAT.format(results.getTimestamp("date")), results.getString("description")));
			}
			
			if(newsFeed.size() < 1) {
				return null;
			}
			
			return newsFeed;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the title page news feed.", e);
			return null;
		}
	}
	
}
