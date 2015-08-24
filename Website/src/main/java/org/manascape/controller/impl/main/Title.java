package org.manascape.controller.impl.main;

import java.util.List;

import org.manascape.controller.Controller;
import org.manascape.db.dao.media.NewsDAO;
import org.manascape.dto.NewsFeedDTO;

public final class Title extends Controller {

	@Override
	public void init() {
		List<NewsFeedDTO> newsFeed = new NewsDAO(getDb()).getNewsFeed();
		if(newsFeed != null) {
			getRequest().setAttribute("newsFeed", newsFeed);
		}
	}

}
