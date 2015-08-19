package org.manascape.controller.impl.account;

import org.manascape.controller.Controller;
import org.manascape.util.CountryUtil;

public final class CreateAccount extends Controller {

	@Override
	public void init() {
		getRequest().setAttribute("countryMap", CountryUtil.COUNTRY_MAP);
	}

}
