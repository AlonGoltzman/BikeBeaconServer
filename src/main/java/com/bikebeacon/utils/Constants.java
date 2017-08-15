package com.bikebeacon.utils;

import java.io.File;

public final class Constants {

	public static final String GET = "GET REQUEST";
	public static final String POST = "POST REQUEST";

	public static final String JSON_ACTION = "action";
	public static final String JSON_OWNER = "owner";
	public static final String JSON_CELLTOWERS = "towers";
	public static final String JSON_GPS = "coords";
	public static final String JSON_PREVIOUS_ALERT = "alert";
	public static final String JSON_IS_CLOSED = "closed";
	
	public static final String ALERT_NEW = "newAlert";
	public static final String ALERT_CONVERSATION_RECEIVED = "conversationStarted";

	public static final String ALERT_DB_NAME = "flags_db";

	public static final File LOG_PATH = new File(System.getenv("user.dir") + "/log");

	public static enum LOG_TYPE {
		INFO, WARNING, ERROR;
	}
}
