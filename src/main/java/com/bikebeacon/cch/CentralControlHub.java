package com.bikebeacon.cch;

import com.bikebeacon.utils.AssetsUtil;
import com.bikebeacon.utils.ConversationUtil;
import com.bikebeacon.utils.FCMUtil;
import com.bikebeacon.utils.TTSUtil;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Timer;

import static com.bikebeacon.utils.Constants.FCM_RESPONSE;
import static com.bikebeacon.utils.PrintUtil.error;
import static com.bikebeacon.utils.PrintUtil.log;

public final class CentralControlHub implements CaseHandler {

    private String path;
    private Timer killTimer;

    private static CentralControlHub instance;
    private static CentralControlHubFactChecker factChecker;

    public static CentralControlHub getCCH() {
        return instance;
    }

    public static CentralControlHubFactChecker getFactChecker() {
        return factChecker;
    }

    public static CentralControlHub getCCH(String defaultPath) {
        return instance == null ? new CentralControlHub(defaultPath) : instance;
    }

    private CentralControlHub(String defaultPath) {
        instance = this;
        factChecker = CentralControlHubFactChecker.getFactChecker();
        path = defaultPath;
        killTimer = new Timer();


    }

    public void entrustCase(CCHDelegate creator, Case newCase) {
        ArrayList<CCHDelegate> delegates = factChecker.getDelegates();
        if (!delegates.contains(creator)) {
            error("CCH->entrustCase", "Unknown CCHDelegate.");
            return;
        }
        if (newCase.isActive()) {
            newCase.setCaseHandler(this);
            factChecker.addActiveCase(newCase);
            beginSequence(newCase);
        } else
            log("###   CCH   ###", "Tried entrusting inactive case, use archiveCase instead.");
    }

    public void archiveCase(Case doneCase) {
        if (factChecker.getAllActiveCases().contains(doneCase))
            factChecker.getAllActiveCases().remove(doneCase);
        else
            log("###   CCH   ###", "Tried archiving a case that doesn't exist in the active cases, will archive anyway, but take note.");
        AssetsUtil.save(doneCase.fileify());
    }

    public void notifyDelegateCreation(CCHDelegate delegate) {
        factChecker.addCCHDelegate(delegate);
    }

    public void notifyDelegateEliminated(CCHDelegate delegate) {
        factChecker.deleteCCHDelegate(delegate);
    }

    private void beginSequence(Case caseToInitiate) {
        ConversationUtil convoUtil = new ConversationUtil();
        String owner = caseToInitiate.getOriginAlert().getOwner();
        FCMUtil util = new FCMUtil(owner);
        JsonObject FCMMessage = new JsonObject();
        JsonObject FCMData = new JsonObject();

        MessageResponse response = convoUtil.tellJerry("");

        FCMData.addProperty(FCM_RESPONSE, response.getText().get(0));

        caseToInitiate.setJerry(convoUtil);

        FCMMessage.addProperty("to", getTokenForOwner(owner));
        FCMMessage.addProperty("priority", "high");
        FCMMessage.add("data", FCMData);

        util.send(FCMMessage);
    }

    public void setToken(String MAC, String token) {
        factChecker.getMACToTokenMap().put(MAC, token);
    }

    public String getTokenForOwner(String MAC) {
        return factChecker.getMACToTokenMap().get(MAC);
    }

    private String forgeURLForFile(File file) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10; i++)
            builder.append(new BigInteger(128, new SecureRandom()).toString(32));
        builder.append(file.hashCode());
        factChecker.getKeyForFileMap().put(builder.toString(), file);
        return builder.toString();
    }

    private void scheduleKillForFileKey(String key) {
        FileKillTask task = new FileKillTask(key);
        factChecker.addNewKillTask(key, task);
        killTimer.schedule(task, 1000 * 60 * 60);

    }

    public File destroyKey(String key) {
        factChecker.getKillTask(key).setExecute(false);
        return factChecker.getKeyForFileMap().remove(key);
    }

}
