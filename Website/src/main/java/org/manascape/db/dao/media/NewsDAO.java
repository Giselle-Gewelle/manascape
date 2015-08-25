package org.manascape.db.dao.media;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.NewsArchiveDTO;
import org.manascape.dto.NewsFeedDTO;
import org.manascape.dto.NewsItemDTO;
import org.manascape.util.DateUtil;

/**
 * Data Access Object for website news.
 * @author DTB
 */
public final class NewsDAO {
	
	private static final Logger LOG = Logger.getLogger(NewsDAO.class);
	
	private final DatabaseHandler db;
	
	public NewsDAO(DatabaseHandler db) {
		this.db = db;
	}
	
	public List<NewsArchiveDTO> getNewsArchive(int category) {
		try {
			ResultSet results = db.prepareCall("media_getNewsArchive", 1)
				.setInt("category", category)
				.getResults();
			
			if(results == null) {
				return null;
			}
			
			List<NewsArchiveDTO> newsArchive = new LinkedList<>();
			while(results.next()) {
				newsArchive.add(new NewsArchiveDTO(results.getInt("id"), results.getString("title"), DateUtil.SHORT_NEWS_FORMAT.format(results.getTimestamp("date")), results.getInt("category")));
			}
			
			if(newsArchive.size() < 1) {
				return null;
			}
			
			return newsArchive;
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the news archive for the category [" + category + "].", e);
			return null;
		}
	}
	
	public NewsItemDTO getNewsArticle(int id) {
		try {
			ResultSet result = db.prepareCall("media_getNewsArticle", 1)
				.setInt("id", id)
				.getResults();
			
			if(result == null || !result.next()) {
				return null;
			}
			
			return new NewsItemDTO(result.getString("title"), DateUtil.LONG_NEWS_FORMAT.format(result.getTimestamp("date")), result.getInt("category"), result.getString("body"));
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to fetch the news article with the id [" + id + "].", e);
			return null;
		}
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
				newsFeed.add(new NewsFeedDTO(results.getInt("id"), results.getString("title"), DateUtil.SHORT_NEWS_FORMAT.format(results.getTimestamp("date")), results.getString("description")));
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
