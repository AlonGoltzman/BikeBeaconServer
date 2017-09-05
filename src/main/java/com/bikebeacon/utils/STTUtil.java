package com.bikebeacon.utils;

import com.bikebeacon.cch.Case;
import com.bikebeacon.cch.CentralControlHub;
import com.bikebeacon.pojo.BaseUtilClass;
import com.bikebeacon.pojo.CCHResponders;
import com.bikebeacon.pojo.TaskCompletionListener;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;

import com.bikebeacon.utils.AssetsUtil.FileContentDistributor;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import org.jetbrains.annotations.Nullable;

import java.io.File;

import static com.bikebeacon.utils.PrintUtil.log;

public class STTUtil extends BaseUtilClass implements Runnable {

    private static final int RESPONDER_CODE = CCHResponders.STT.getId();

    private SpeechToText service;
    private Case originCase;
    private File audioFile;


    public STTUtil(TaskCompletionListener listener) {
        super(listener);

        FileContentDistributor distributor = AssetsUtil.load("stt.creds").extractContent();

        String username = distributor.getLine(1);
        String password = distributor.getLine(2);

        service = new SpeechToText(username, password);

        setRunnable(this);

    }

    public void setOriginCase(Case originCase) {
        this.originCase = originCase;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    @Override
    public void failed(@Nullable Object reason) {
        if (reason != null) {
            if (reason instanceof Exception)
                listener.onFailed(RESPONDER_CODE, (Throwable) reason);
        } else
            listener.onFailed(RESPONDER_CODE, null);
    }

    @Override
    public void success(Object... results) {
        service = null;
        listener.onSuccess(RESPONDER_CODE, results);
    }

    @Override
    public void run() {
        try {
            RecognizeOptions options = new RecognizeOptions.Builder().contentType("audio/wav").model("en-US_NarrowbandModel").build();
            SpeechResults results = service.recognize(audioFile, options).execute();
            success(originCase, results);
        } catch (Exception e) {
            e.printStackTrace();
            failed(e);
        }
    }

}
