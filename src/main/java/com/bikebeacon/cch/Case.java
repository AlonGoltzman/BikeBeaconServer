package com.bikebeacon.cch;

import com.bikebeacon.utils.cloudant.alert.Alert;
import com.bikebeacon.utils.ConversationUtil;
import com.bikebeacon.pojo.Fileable;
import com.google.gson.JsonObject;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import static com.bikebeacon.pojo.Constants.*;
import static com.bikebeacon.utils.PrintUtil.error_f;

public class Case implements Fileable {

    private Alert originAlert;

    private ConversationUtil jerry;
    private Map<String, Object> jerryContext;

    private String caseID;
    private String uuid;
    private boolean active;

    private CaseHandler handler;

    public Case() {
        this(true);
    }

    public Case(boolean isActive) {
        active = isActive;
        caseID = new BigInteger(128, new SecureRandom()).toString(32);
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

    Alert getOriginAlert() {
        return originAlert;
    }

    ConversationUtil getJerry() {
        return jerry;
    }

    Map<String, Object> getJerryContext() {
        return jerryContext;
    }

    String getOwner() {
        return getOriginAlert().getOwner();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean isActive) {
        active = isActive;
    }

    public void setOriginAlert(Alert originAlert) {
        this.originAlert = originAlert;
    }

    void setCaseHandler(CaseHandler finalHandler) {
        if (handler == null)
            handler = finalHandler;
        else
            error_f("Case->setCaseHandler", "Tried changing the CaseHandler after one was already set.\n%s", Arrays.toString(Thread.currentThread().getStackTrace()));
    }

    void setJerry(ConversationUtil jerry) {
        this.jerry = jerry;
    }

    void setJerryContext(Map<String, Object> jerryContext) {
        this.jerryContext = jerryContext;
    }

    String adaptContext(String response) {
        return convertMessage(response);
    }

    private String convertMessage(String message) {
        String[] words = message.split(" ");
        StringBuilder builder = new StringBuilder();
        boolean needSpace = false;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            word = convertWord(word);
            if (!words[i].equals(word)) {
                words[i] = word;
                builder.append(word);
                needSpace = true;
            } else {
                if (needSpace) {
                    builder.append(" ");
                    needSpace = false;
                }
                builder.append(words[i]).append(" ");
            }
        }
        return builder.toString().trim();
    }

    private String convertWord(String word) {
        String newWord = word.toLowerCase();
        switch (newWord) {
            case "one":
                return "1";
            case "two":
                return "2";
            case "three":
                return "3";
            case "four":
                return "4";
            case "five":
                return "5";
            case "six":
                return "6";
            case "seven":
                return "7";
            case "eight":
                return "8";
            case "nine":
                return "9";
            case "zero":
                return "0";
            default:
                return word;
        }
    }
}
