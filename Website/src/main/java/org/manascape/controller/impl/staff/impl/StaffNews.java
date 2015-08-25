package org.manascape.controller.impl.staff.impl;

import org.manascape.controller.impl.media.News.NewsCategory;
import org.manascape.controller.impl.staff.StaffPage;
import org.manascape.db.DatabaseHandler;
import org.manascape.db.dao.media.NewsDAO;
import org.manascape.dto.NewsErrorDTO;
import org.manascape.dto.NewsFieldDTO;
import org.manascape.dto.NewsItemDTO;
import org.manascape.http.HttpRequestType;
import org.manascape.http.RequestHandler;
import org.manascape.util.UrlUtil;

/**
 * Content controller all staff center "news" pages.
 * @author DTB
 */
public final class StaffNews extends StaffPage {
	
	private NewsFieldDTO fields;
	private NewsErrorDTO errors;
	
	private NewsDAO dao;
	private NewsItemDTO article;
	
	@Override
	public void init() {
		super.init();
		
		if(!isAuthorized()) {
			return;
		}
		
		dao = new NewsDAO(getDb(), getLoginSession().getUser());
		
		switch(getDest()) {
			case "newsarticle.ws":
				prepareNewsArticle();
				setFields();
				break;
			case "newsdelete.ws":
				prepareNewsDelete();
				break;
		}
	}
	
	private void prepareNewsDelete() {
		int id = RequestHandler.getIntParam(getRequest(), "id");
		if(id >= 1 && id <= DatabaseHandler.MAX_VALUE_MEDIUMINT) {
			article = dao.getNewsArticle(id);
			if(article != null) {
				getRequest().setAttribute("article", article);
			}
		}
		
		if(article == null) {
			return;
		}
		
		String cancel = getRequest().getParameter("inputNo");
		if(cancel != null) {
			setRedirecting(true);
			UrlUtil.redirect(getResponse(), "news", "article.ws?id=" + article.getId());
			return;
		}
		
		String submit = getRequest().getParameter("inputYes");
		if(submit != null) {
			dao.deleteNewsArticle(article.getId());
			getRequest().setAttribute("deleted", true);
		}
	}
	
	private void prepareNewsArticle() {
		getRequest().setAttribute("categories", NewsCategory.getCategories());
		
		fields = new NewsFieldDTO("", "", "", "");
		errors = new NewsErrorDTO(null, false, null, null);
		
		int id = RequestHandler.getIntParam(getRequest(), "id");
		if(id >= 1 && id <= DatabaseHandler.MAX_VALUE_MEDIUMINT) {
			article = dao.getNewsArticle(id);
			if(article != null) {
				getRequest().setAttribute("update", id);
				fields.setTitle(article.getTitle());
				fields.setCategory(String.valueOf(article.getCategory()));
				fields.setDescription(article.getDescription());
				fields.setBody(article.getBody());
			}
		}
		
		if(getRequestType().equals(HttpRequestType.POST)) {
			boolean returnType = true;
			
			if(!validateTitle()) {
				returnType = false;
			}
			if(!validateCategory()) {
				errors.setCategory(true);
				returnType = false;
			}
			if(!validateDescription()) {
				returnType = false;
			}
			if(!validateBody()) {
				returnType = false;
			}
			
			if(returnType) {
				int newArticleId = -1;
				if(article == null) {
					newArticleId = dao.postNewsArticle(fields);
				} else {
					dao.updateNewsArticle(article.getId(), fields);
					newArticleId = article.getId();
				}
				
				if(newArticleId < 1) {
					getRequest().setAttribute("postError", true);
				} else {
					getRequest().setAttribute("newArticleId", newArticleId);
				}
			}
		}
	}
	
	private boolean validateBody() {
		String body = getRequest().getParameter("inputBody");
		if(body == null) {
			errors.setBody("Please input a valid article body:");
			return false;
		}
		
		body = body.trim();
		if(body.length() < 1 || body.length() > 65535) {
			errors.setBody("Article bodies must be between 1 and 65535 characters in length:");
			return false;
		}
		
		fields.setBody(body);
		return true;
	}
	
	private boolean validateDescription() {
		String desc = getRequest().getParameter("inputDescription");
		if(desc == null) {
			errors.setDescription("Please input a valid description:");
			return false;
		}
		
		desc = desc.trim();
		if(desc.length() < 1 || desc.length() > 1024) {
			errors.setDescription("Descriptions must be between 1 and 1024 characters in length:");
			return false;
		}
		
		fields.setDescription(desc);
		return true;
	}
	
	private boolean validateCategory() {
		String category = getRequest().getParameter("inputCategory");
		if(category == null) {
			return false;
		}
		
		try {
			int catInt = Integer.parseInt(category);
			if(catInt < 1) {
				return false;
			}
			if(NewsCategory.forId(catInt) == null) {
				return false;
			}
		} catch(NumberFormatException e) {
			return false;
		}
		
		fields.setCategory(category);
		return true;
	}
	
	private boolean validateTitle() {
		String title = getRequest().getParameter("inputTitle");
		if(title == null) {
			errors.setTitle("Please input a valid title:");
			return false;
		}
		
		title = title.trim();
		if(title.length() < 1 || title.length() > 50) {
			errors.setTitle("Titles must be between 1 and 50 characters in length:");
			return false;
		}
		
		fields.setTitle(title);
		return true;
	}
	
	private void setFields() {
		getRequest().setAttribute("fieldValues", fields);
		getRequest().setAttribute("errors", errors);
	}
	
	@Override
	public String getActualPage() {
		return getDest().replace("news", "news/");
	}
	
}
