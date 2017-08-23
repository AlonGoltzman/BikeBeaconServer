package com.bikebeacon.utils;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;

import com.bikebeacon.utils.AssetsUtil.FileContentDistributer;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import java.io.File;

public class STTUtil {

    private static STTUtil util;

    private SpeechToText service;

    public static STTUtil getUtil() {
        return util == null ? new STTUtil() : util;
    }

    private STTUtil() {
        util = this;

        FileContentDistributer distributer = AssetsUtil.load("stt.creds").extractContent();

        String username = distributer.getLine(1);
        String password = distributer.getLine(2);

        service = new SpeechToText(username, password);

    }

    public void recognize(File audioFile) {
        RecognizeOptions options = new RecognizeOptions.Builder().contentType("audio/wav").build();
        SpeechResults results = service.recognize(audioFile, options).execute();

        System.out.println(results);
    }

}
