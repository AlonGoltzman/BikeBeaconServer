package com.bikebeacon.utils.cloudconvert;

import com.bikebeacon.cch.Case;
import com.bikebeacon.utils.AssetsUtil;
import com.bikebeacon.utils.cloudconvert.ProcessRequestCreationCallback;
import com.bikebeacon.pojo.BaseUtilClass;
import com.bikebeacon.pojo.CCHResponders;
import com.bikebeacon.pojo.TaskCompletionListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.regexp.internal.RE;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.bikebeacon.utils.AssetsUtil.ensureFileReady;
import static com.bikebeacon.utils.GeneralUtils.addObjects;
import static com.bikebeacon.utils.PrintUtil.*;

/**
 * Created by Mgr on 8/23/2017.
 */
public class CloudConvertUtil extends BaseUtilClass implements Runnable {

    private static final int RESPONDER_CODE = CCHResponders.CLOUDCONVERT.getId();

    private URL cloudConvertURL;
    private String apiKey;
    private OkHttpClient clientHTTP;

    private String inputFormat;
    private String outputFormat;

    private String processURL;
    private Case caseFile;

    private File inputAudioFile;
    private File convertedAudioFile;
    private JsonObject requestJson;


    public CloudConvertUtil(TaskCompletionListener listener) {
        super(listener);
        apiKey = AssetsUtil.load("cloudconvert.creds").extractContent().getLine(1);
        clientHTTP = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        try {
            cloudConvertURL = new URL("https://api.cloudconvert.com/process");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setRunnable(this);
    }

    @Override
    public void failed(@Nullable Object reason) {
        if (reason != null) {
            if (reason instanceof Exception) {
                listener.onFailed(RESPONDER_CODE, (Throwable) reason);
            } else if (reason instanceof Response) {
                try {
                    log("CloudConvertUtil->failed", ((Response) reason).body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    listener.onFailed(RESPONDER_CODE, null);
                }
            }
        } else
            listener.onFailed(RESPONDER_CODE, null);
    }

    @Override
    public void success(Object... results) {
        if (results == null)
            listener.onSuccess(RESPONDER_CODE, caseFile);
        else {
            listener.onSuccess(RESPONDER_CODE, addObjects(caseFile, results));
        }
    }

    @Contract(pure = true)
    public int getResponderCode() {
        return RESPONDER_CODE;
    }

    public Case getCaseFile() {
        return caseFile;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public URL getCloudConvertURL() {
        return cloudConvertURL;
    }

    public String getApiKey() {
        return apiKey;
    }

    public OkHttpClient getHTTPClient() {
        return clientHTTP;
    }

    public String getProcessURL() {
        return processURL;
    }

    public File getInputAudioFile() {
        return inputAudioFile;
    }

    public File getConvertedAudioFile() {
        return convertedAudioFile;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public void setRequestJson(JsonObject requestJson) {
        this.requestJson = requestJson;
    }

    public void setConvertedAudioFile(File convertedAudioFile) {
        this.convertedAudioFile = convertedAudioFile;
    }

    public void setInputAudioFile(File inputAudioFile) {
        this.inputAudioFile = inputAudioFile;
    }

    public void setCaseFile(Case caseFile) {
        this.caseFile = caseFile;
    }

    public void setProcessURL(String processURL) {
        this.processURL = processURL;
    }

    @Contract("null->fail")
    public JsonObject getJSONFromResponse(Response response) throws IOException {
        InputStream resp = response.body().byteStream();

        StringBuilder jsonResponseString = new StringBuilder();

        byte[] buffer = new byte[1024];
        int aRead;

        while ((aRead = resp.read(buffer)) != -1)
            jsonResponseString.append(new String(buffer, 0, aRead));

        resp.close();

        return (JsonObject) new JsonParser().parse(jsonResponseString.toString());
    }

    @Override
    public void run() {
        try {
            if (!ensureFileReady(convertedAudioFile)) {
                failed(null);
                return;
            }
            Request conversionRequest = new Request.Builder()
                    .url(cloudConvertURL).method("POST", new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return null;
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            sink.write(requestJson.toString().getBytes());
                        }
                    }).header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json").build();
            clientHTTP.newCall(conversionRequest).enqueue(new ProcessRequestCreationCallback(this));
        } catch (Exception e) {
            e.printStackTrace();
            failed(e);
        }
    }


}

