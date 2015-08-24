package org.manascape.util;

import java.text.SimpleDateFormat;

public final class DateUtil {
	
	public static final SimpleDateFormat 
		SQL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd"),
		SQL_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
		CREATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy"),
		NEWS_FEED_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
	
}
