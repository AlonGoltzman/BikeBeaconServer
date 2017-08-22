package com.bikebeacon.utils;

import java.io.File;

public final class Constants {

    public static final String GET = "GET REQUEST";
    public static final String POST = "POST REQUEST";

    public static File ASSETS_FOLDER;
    public static File LOG_PATH;

    public static final String JSON_ACTION = "action";
    public static final String JSON_OWNER = "owner";
    public static final String JSON_CELLTOWERS = "towers";
    public static final String JSON_GPS = "coords";
    public static final String JSON_PREVIOUS_ALERT = "alert";
    public static final String JSON_IS_CLOSED = "closed";
    public static final String JSON_ID = "id";

    public static final String ALERT_NEW = "newAlert";
    public static final String ALERT_CONVERSATION_RECEIVED = "conversationStarted";
    public static final String ALERT_DELTE = "deleteAlert";
    public static final String ALERT_UPDATE = "updateAlert";

    public static final String ALERT_DB_NAME = "flags_db";

    public static final String CASE_ID = "CaseID";
    public static final String CASE_OWNER = "CaseCreator(Human)";
    public static final String CASE_WAS_ACTIVE = "CaseStatusAtConversionToFile(Active=true,Inactive=false)";
    public static final String CASE_ALERT_INFO = "CaseOriginAlertInfo";

    public static final String CONVERSATION_WORKSPACE_ID = "8984a29f-be2f-4f5c-afb4-873e9a4b0cbe";

    public static final String FCM_RESPONSE = "response";
    public static final String FCM_URL = "trackDownloadURL";

    public enum LOG_TYPE {
        INFO, WARNING, ERROR;
    }
}
