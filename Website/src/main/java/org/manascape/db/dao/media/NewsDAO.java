package org.manascape.db.dao.media;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.manascape.db.Call;
import org.manascape.db.DatabaseHandler;
import org.manascape.dto.NewsArchiveDTO;
import org.manascape.dto.NewsFeedDTO;
import org.manascape.dto.NewsFieldDTO;
import org.manascape.dto.NewsItemDTO;
import org.manascape.dto.UserSessionDTO;
import org.manascape.util.DateUtil;
import org.manascape.util.StringUtil;

/**
 * Data Access Object for website news.
 * @author DTB
 */
public final class NewsDAO {
	
	private static final Logger LOG = Logger.getLogger(NewsDAO.class);
	
	private final DatabaseHandler db;
	private final UserSessionDTO user;
	
	public NewsDAO(DatabaseHandler db) {
		this.db = db;
		this.user = null;
	}
	
	public NewsDAO(DatabaseHandler db, UserSessionDTO user) {
		this.db = db;
		this.user = user;
	}
	
	public void deleteNewsArticle(int articleId) {
		try {
			db.prepareCall("media_deleteNewsArticle", 3)
				.setInt("articleId", articleId)
				.setString("username", user.getUsername())
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.execute();
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to update a news article with the id [" + articleId + "], by user [" + user.getDisplayName() + "].", e);
		}
	}
	
	public void updateNewsArticle(int articleId, NewsFieldDTO fields) {
		try {
			db.prepareCall("media_updateNewsArticle", 7)
				.setInt("articleId", articleId)
				.setString("username", user.getUsername())
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setString("title", fields.getTitle())
				.setInt("category", Integer.parseInt(fields.getCategory()))
				.setString("description", fields.getDescription())
				.setString("body", fields.getBody())
				.execute();
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to update a news article with the id [" + articleId + "], by user [" + user.getDisplayName() + "].", e);
		}
	}
	
	public int postNewsArticle(NewsFieldDTO fields) {
		try {
			Call dbCall = db.prepareCall("media_postNewsArticle", 7)
				.setInt("authorId", user.getId())
				.setString("title", fields.getTitle())
				.setString("date", DateUtil.SQL_DATETIME_FORMAT.format(new Date()))
				.setInt("category", Integer.parseInt(fields.getCategory()))
				.setString("description", fields.getDescription())
				.setString("body", fields.getBody())
				.registerOut("articleId", Types.INTEGER)
				.execute();
			
			return dbCall.getInt("articleId");
		} catch(SQLException e) {
			LOG.error("SQLException occurred while attempting to post a news article, by user [" + user.getDisplayName() + "].", e);
			return -1;
		}
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
			
			String lastEditor = result.getString("lastEditor");
			Date lastEditDate = result.getTimestamp("lastEditDate");
			return new NewsItemDTO(result.getInt("id"), result.getString("title"), DateUtil.LONG_NEWS_FORMAT.format(result.getTimestamp("date")), 
					result.getInt("category"), result.getString("description"), result.getString("body"), 
					lastEditor == null ? null : StringUtil.formatUsername(result.getString("lastEditor")), lastEditDate == null ? null : DateUtil.SHORT_DATETIME_FORMAT.format(lastEditDate));
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
