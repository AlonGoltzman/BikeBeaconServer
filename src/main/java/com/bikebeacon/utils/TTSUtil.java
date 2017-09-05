package com.bikebeacon.utils;

import com.bikebeacon.cch.Case;
import com.bikebeacon.pojo.BaseUtilClass;
import com.bikebeacon.pojo.CCHResponders;
import com.bikebeacon.pojo.TaskCompletionListener;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.bikebeacon.utils.AssetsUtil.FileContentDistributor;
import static com.bikebeacon.utils.GeneralUtils.addObjects;

public class TTSUtil extends BaseUtilClass implements Runnable {

    private static final int RESPONDER_CODE = CCHResponders.TTS.getId();

    private TextToSpeech service;
    private File output;
    private String input;
    private Case handlingCase;

    public TTSUtil(TaskCompletionListener listener) {
        super(listener);
        FileContentDistributor distributer = AssetsUtil.load("tts.creds");
        distributer.extractContent();
        String username = distributer.getLine(2);
        String password = distributer.getLine(3);
        service = new TextToSpeech(username, password);
        setRunnable(this);
    }

    public void setOutFile(File out) {
        output = out;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setHandlingCase(Case handlingCase) {
        this.handlingCase = handlingCase;
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
        if (results == null)
            listener.onSuccess(RESPONDER_CODE, handlingCase);
        else {
            listener.onSuccess(RESPONDER_CODE, addObjects(handlingCase, results));
        }
    }

    @Override
    public void run() {
        try {
            InputStream inStream = service.synthesize(input, Voice.EN_MICHAEL, AudioFormat.WAV).execute();
            InputStream wave = WaveUtils.reWriteWaveHeader(inStream);
            OutputStream out = new FileOutputStream(output);
            byte[] buffer = new byte[1024];

            while (wave.read(buffer) != -1)
                out.write(buffer);

            inStream.close();
            out.close();
            wave.close();
            success(output);
        } catch (Exception e) {
            e.printStackTrace();
            failed(e);
        }
    }
}
