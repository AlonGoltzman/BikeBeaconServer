package com.bikebeacon.cch;

import com.bikebeacon.pojo.Alert;
import com.bikebeacon.utils.ConversationUtil;
import com.bikebeacon.utils.Fileable;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import static com.bikebeacon.utils.Constants.*;
import static com.bikebeacon.utils.PrintUtil.error_f;
import static com.bikebeacon.utils.PrintUtil.log;

public class Case implements Fileable {

    private Alert originAlert;

    private ConversationUtil jerry;

    private String caseID;
    private boolean active;

    private CaseHandler handler;

    public Case() {
        this(true);
    }

    public Case(boolean isActive) {
        active = isActive;
        caseID = new BigInteger(128, new SecureRandom()).toString(32);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean isActive) {
        active = isActive;
    }

    public void setCaseHandler(CaseHandler finalHandler) {
        if (handler == null)
            handler = finalHandler;
        else
            error_f("Case->setCaseHandler", "Tried changing the CaseHandler after one was already set.\n%s", Arrays.toString(Thread.currentThread().getStackTrace()));
    }

    @Override
    public JsonObject fileify() {
        JsonObject object = new JsonObject();
        object.addProperty(CASE_ID, caseID);
        object.addProperty(CASE_OWNER, originAlert == null ? "NA" : originAlert.getOwner());
        object.addProperty(CASE_WAS_ACTIVE, active);
        if (originAlert != null)
            object.add(CASE_ALERT_INFO, originAlert.fileify());
        else
            object.addProperty(CASE_ALERT_INFO, "NA");
        return object;
    }

    public void setOriginAlert(Alert originAlert) {
        this.originAlert = originAlert;
    }

    public void setJerry(ConversationUtil jerry) {
        this.jerry = jerry;
    }
}
