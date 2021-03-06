package com.bikebeacon.pojo;

import java.io.File;

public final class Constants {

    public static final String GET = "GET REQUEST";
    public static final String POST = "POST REQUEST";

    public static File ASSETS_FOLDER;
    public static File LOG_PATH;

    public static final String JSON_UUID = "uuid";
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
    public static final String MAILING_LIST_DB = "mailing_list_db";
    public static final String UUID_DB = "uuid_db";

    public static final String CASE_ID = "CaseID";
    public static final String CASE_OWNER = "CaseCreator(Human)";
    public static final String CASE_WAS_ACTIVE = "CaseStatusAtConversionToFile(Active=true,Inactive=false)";
    public static final String CASE_ALERT_INFO = "CaseOriginAlertInfo";

    public static final String CONVERSATION_WORKSPACE_ID = "8984a29f-be2f-4f5c-afb4-873e9a4b0cbe";

    public static final String FCM_RESPONSE = "response";
    public static final String FCM_URL = "trackDownloadURL";
    public static final String FCM_CALL = "callNumber";

    public static final String RESPONSE_INPUT = "inputFormat";
    public static final String RESPONSE_OUTPUT = "outputFormat";

    public static final String CLOUD_CONVERT_ID = "id";
    public static final String CLOUD_CONVERT_URL = "url";
    public static final String CLOUD_CONVERT_PROCESS_REQUEST_OUTPUT_FORMAT = "outputformat";
    public static final String CLOUD_CONVERT_PROCESS_REQUEST_INPUT_TYPE = "input";
    public static final String CLOUD_CONVERT_UPLOAD_JSON = "upload";
    public static final String CLOUD_CONVERT_STEP = "step";
    public static final String CLOUD_CONVERT_STEP_INPUT = "input";
    public static final String CLOUD_CONVERT_STEP_WAIT = "wait";
    public static final String CLOUD_CONVERT_STEP_CONVERT = "convert";
    public static final String CLOUD_CONVERT_STEP_OUTPUT = "output";
    public static final String CLOUD_CONVERT_STEP_ERROR = "error";
    public static final String CLOUD_CONVERT_STEP_FINISHED = "finished";
    public static final String CLOUD_CONVERT_EXTENTION = "ext";

    public static final String CONVERSATION_CONTEXT_NUMBER = "numberToCall";
    public static final String CONVERSATION_CONTEXT_NUMBER_CONTAINED_IN_MAILING_LIST = "containedInMailingList";
    public static final String CONVERSATION_CONTEXT_CHECKING_ACTION = "checkingAction";
    public static final String CONVERSATION_CONTEXT_CALL_POLICE = "callPolice";
    public static final String CONVERSATION_CONTEXT_DISTRESS = "distress";
    public static final String CONVERSATION_CONTEXT_STEALTH_BEACON = "stealthBeacon";
    public static final String CONVERSATION_CONTEXT_CALL_PERSON = "person";
    public static final String CONVERSATION_CONTEXT_CALL = "call";

    public static final String PERSON_JSON_NUMBER = "number";
    public static final String PERSON_JSON_NAME = "name";


    public enum LOG_TYPE {
        INFO, WARNING, ERROR;
    }
}
