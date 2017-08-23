package com.bikebeacon.utils;

import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.AudioFormat;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.text_to_speech.v1.util.WaveUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.bikebeacon.utils.AssetsUtil.FileContentDistributer;

public class TTSUtil {

    private TextToSpeech textToSpeech;

    public TTSUtil() {
        FileContentDistributer distributer = AssetsUtil.load("tts.creds");
        distributer.extractContent();
        String username = distributer.getLine(2);
        String password = distributer.getLine(3);
        textToSpeech = new TextToSpeech(username, password);
    }

    public void readOutText(String input, OutputStream outStream) throws IOException {
        InputStream inStream = textToSpeech.synthesize(input, Voice.EN_MICHAEL, AudioFormat.WAV).execute();
        InputStream wave = WaveUtils.reWriteWaveHeader(inStream);

        byte[] buffer = new byte[1024];

        while (wave.read(buffer) != -1)
            outStream.write(buffer);

        inStream.close();
        outStream.close();
        wave.close();
    }

}
