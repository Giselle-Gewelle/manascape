package org.manascape.controller.impl.media;

import java.util.HashMap;
import java.util.Map;

import org.manascape.controller.Controller;
import org.manascape.db.DatabaseHandler;
import org.manascape.db.dao.media.NewsDAO;
import org.manascape.dto.NewsItemDTO;
import org.manascape.http.RequestHandler;

/**
 * Content controller for the news system.
 * @author DTB
 */
public final class News extends Controller {
	
	public enum NewsCategory {
		
		GAME(1, "Game"),
		WEBSITE(2, "Website"),
		CUSTOMER_SUPPORT(3, "Customer Support"),
		TECHNICAL(4, "Technical"),
		BEHIND_THE_SCENES(5, "Behind the Scenes");
		
		private int id;
		private String name;
		
		private NewsCategory(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		// String key, because Freemarker would be unhappy if it were an integer.
		private static Map<String, NewsCategory> categories = new HashMap<>();
		
		static {
			for(NewsCategory category : NewsCategory.values()) {
				categories.put(String.valueOf(category.getId()), category);
			}
		}
		
		public static Map<String, NewsCategory> getCategories() {
			return categories;
		}
		
		public static NewsCategory forId(int id) {
			return categories.get(String.valueOf(id));
		}
		
	}
	
	private NewsDAO dao;
	
	@Override
	public void init() {
		dao = new NewsDAO(getDb());
		getRequest().setAttribute("categories", NewsCategory.getCategories());
		
		switch(getDest()) {
			case "article.ws":
				prepareArticle();
				break;
		}
	}
	
	private void prepareArticle() {
		int id = RequestHandler.getIntParam(getRequest(), "id");
		if(id < 1 || id > DatabaseHandler.MAX_VALUE_MEDIUMINT) {
			return;
		}
		
		NewsItemDTO article = dao.getNewsArticle(id);
		if(article != null) {
			getRequest().setAttribute("article", article);
		}
	}

}
