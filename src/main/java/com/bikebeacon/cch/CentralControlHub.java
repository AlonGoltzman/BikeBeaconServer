package com.bikebeacon.cch;

import com.bikebeacon.pojo.TaskCompletionListener;
import com.bikebeacon.utils.*;
import com.bikebeacon.utils.cloudant.mailing_list.Person;
import com.bikebeacon.utils.cloudant.uuid.Unique;
import com.bikebeacon.utils.cloudconvert.CloudConvertUtil;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Timer;

import static com.bikebeacon.pojo.Constants.*;
import static com.bikebeacon.utils.AssetsUtil.ensureFileReady;
import static com.bikebeacon.utils.PrintUtil.error;
import static com.bikebeacon.utils.PrintUtil.log;

public final class CentralControlHub implements CaseHandler, TaskCompletionListener {

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

    void notifyDelegateCreation(CCHDelegate delegate) {
        if (factChecker == null)
            factChecker = CentralControlHubFactChecker.getFactChecker();
        factChecker.addCCHDelegate(delegate);
    }

    void notifyDelegateEliminated(CCHDelegate delegate) {
        factChecker.deleteCCHDelegate(delegate);
    }

    private void beginSequence(Case caseToInitiate) {
        ConversationUtil convoUtil = new ConversationUtil(this, caseToInitiate);
        caseToInitiate.setJerry(convoUtil);
        convoUtil.setInput("");
        convoUtil.setContext(null);
        convoUtil.execute();
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

    public void setToken(String MAC, String token) {
        factChecker.getMACToTokenMap().put(MAC, token);
    }

    public String getTokenForOwner(String MAC) {
        return factChecker.getMACToTokenMap().get(MAC);
    }

    public void receivedResponse(String inputFormat, String outputFormat, File responseFile) {
        String ownerAddr = responseFile.getName().replace("response." + inputFormat, "");
        Case handlingCase = getFactChecker().getCase(ownerAddr);

        JsonObject request = new JsonObject();
        request.addProperty("inputformat", inputFormat);
        request.addProperty("outputformat", outputFormat);

        CloudConvertUtil util = new CloudConvertUtil(this);
        util.setInputFormat(inputFormat);
        util.setOutputFormat(outputFormat);
        util.setInputAudioFile(responseFile);
        util.setConvertedAudioFile(new File(responseFile.getParentFile(), ownerAddr + "." + outputFormat));
        util.setCaseFile(handlingCase);
        util.setRequestJson(request);

        util.execute();

    }


    @Override
    public void onSuccess(int responder, Object... replyParams) {
        TTSUtil ttsUtil;
        STTUtil sttUtil;
        FCMUtil fcmUtil;
        ConversationUtil convoUtil;
        MailingListUtil mailingListUtil;
        JsonObject FCMMessage;
        JsonObject FCMData;
        Case ownerCase;
        String UUID;
        File outputFile;
        LinkedTreeMap<String, Object> jerryContext;
        switch (responder) {
            case 1: //TTS Util
                ownerCase = (Case) replyParams[0];
                outputFile = (File) replyParams[1];
                String key = forgeURLForFile(outputFile);
                scheduleKillForFileKey(key);
                fcmUtil = new FCMUtil(this);
                FCMMessage = new JsonObject();
                FCMData = new JsonObject();
                FCMData.addProperty(FCM_URL, key);
                jerryContext = (LinkedTreeMap<String, Object>) ownerCase.getJerryContext();
                if (jerryContext.containsKey(CONVERSATION_CONTEXT_NUMBER) &&
                        jerryContext.containsKey(CONVERSATION_CONTEXT_CALL) &&
                        (Boolean) jerryContext.get(CONVERSATION_CONTEXT_CALL))
                    FCMData.addProperty(FCM_CALL, ownerCase.getJerryContext().get(CONVERSATION_CONTEXT_NUMBER).toString());
                else if ((Boolean) jerryContext.get(CONVERSATION_CONTEXT_CALL_POLICE))
                FCMMessage.addProperty("to", getTokenForOwner(ownerCase.getOwner()));
                FCMMessage.addProperty("priority", "high");
                FCMMessage.add("data", FCMData);
                log("CCH->onSuccess", FCMMessage.toString());
                fcmUtil.setRequestJson(FCMMessage);
                fcmUtil.execute();
                break;
            case 2:// STT Util
                ownerCase = (Case) replyParams[0];
                SpeechResults results = (SpeechResults) replyParams[1];
                UUID = ownerCase.getOriginAlert().getUuid();
                String sttResponse;
                String actualSTTResponse = null;
                try {
                    sttResponse = results.getResults().get(0).getAlternatives().get(0).getTranscript();
                    actualSTTResponse = ownerCase.adaptContext(sttResponse);
                } catch (IndexOutOfBoundsException e) {
                    error("CCH->onSuccess", "Nothing understood from what the person was saying.");
                }
                log("CCH->onSuccess", "STT Response: " + actualSTTResponse);
                mailingListUtil = new MailingListUtil(new Unique(UUID), this);
                mailingListUtil.setOwnerCase(ownerCase);
                jerryContext = (LinkedTreeMap<String, Object>) ownerCase.getJerryContext();
                if (jerryContext.containsKey(CONVERSATION_CONTEXT_NUMBER))
                    mailingListUtil.setPhoneNumber(jerryContext.get(CONVERSATION_CONTEXT_NUMBER).toString());
                else if (jerryContext.containsKey(CONVERSATION_CONTEXT_CALL_PERSON))
                    mailingListUtil.setPerson(jerryContext.get(CONVERSATION_CONTEXT_CALL_PERSON).toString());
                mailingListUtil.setMessage(actualSTTResponse);
                mailingListUtil.execute();
                break;
            case 3: //Conversation Util
                ownerCase = (Case) replyParams[0];
                MessageResponse response = (MessageResponse) replyParams[1];
                String jerryResponse = response.getText().get(0);
                log("CCH->onSuccess", "Conversation Response: " + jerryResponse);
                ownerCase.setJerryContext(response.getContext());
                ttsUtil = new TTSUtil(this);
                ttsUtil.setInput(jerryResponse);
                ttsUtil.setHandlingCase(ownerCase);
                outputFile = new File(path, ownerCase.getOwner() + ".wav");
                if (!ensureFileReady(outputFile)) {
                    error("CCH->onSuccess", "Failed creating file for TTS service.");
                    return;
                }
                ttsUtil.setOutFile(outputFile);
                ttsUtil.execute();
                break;
            case 5: //FCM Util
                break;
            case 6: //CloudConvert Util
                ownerCase = (Case) replyParams[0];
                outputFile = (File) replyParams[1];
                sttUtil = new STTUtil(this);
                sttUtil.setOriginCase(ownerCase);
                sttUtil.setAudioFile(outputFile);
                sttUtil.execute();
                break;
            case 7://Mailing List Util
                ownerCase = (Case) replyParams[0];
                String message = "";
                if (replyParams.length >= 2)
                    message = (String) replyParams[1];
                Person personToCall = null;
                if (replyParams.length == 3)
                    personToCall = (Person) replyParams[2];
                jerryContext = (LinkedTreeMap<String, Object>) ownerCase.getJerryContext();
                if (personToCall != null) {
                    jerryContext.put(CONVERSATION_CONTEXT_NUMBER_CONTAINED_IN_MAILING_LIST, true);
                    jerryContext.put(CONVERSATION_CONTEXT_NUMBER, personToCall.getPhoneNumber());
                    jerryContext.put(CONVERSATION_CONTEXT_CALL_PERSON, personToCall.getName());
                }
                convoUtil = ownerCase.getJerry();
                convoUtil.reset();
                convoUtil.setContext(ownerCase.getJerryContext());
                convoUtil.setInput(message);
                convoUtil.execute();
                break;
        }
    }

    @Override
    public void onFailed(int responder, Throwable reason) {

    }
}
