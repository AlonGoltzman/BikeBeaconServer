package com.bikebeacon.utils;

import com.bikebeacon.pojo.BaseUtilClass;
import com.bikebeacon.pojo.CCHResponders;
import com.bikebeacon.pojo.TaskCompletionListener;
import com.google.gson.JsonObject;
import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.bikebeacon.utils.PrintUtil.error_f;
import static com.bikebeacon.utils.PrintUtil.log;
import static com.bikebeacon.utils.PrintUtil.log_f;

public final class FCMUtil extends BaseUtilClass implements Runnable {

    private static final String SERVER_KEY = "AAAAAW4AS3U:APA91bFPEkys4V_tUCYpRwf1BDDY15GaggjUbr-DvQF8d3PNPmEiBcar7o0ruqtdQuSk8CwqzdHaXHYmSTJ0W5CiMxI_v4dQZirniDfVg7auOl3_K2lMXQJkArYIhbZAPCd4TKAD3Y_J";
    private static final int RESPONDER_CODE = CCHResponders.FCM.getId();

    private URL serverURL;
    private JsonObject requestJson;

    public FCMUtil(TaskCompletionListener listener) {
        super(listener);
        try {
            serverURL = new URL("https://fcm.googleapis.com/fcm/send");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        setRunnable(this);
    }


    public void setRequestJson(JsonObject requestJson) {
        this.requestJson = requestJson;
    }

    @Override
    public void failed(@Nullable Object reason) {
        if (reason != null)
            if (reason instanceof Exception)
                listener.onFailed(RESPONDER_CODE, (Throwable) reason);
    }

    @Override
    public void success(Object... results) {
        listener.onSuccess(RESPONDER_CODE, results);
    }

    @Override
    public void run() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(serverURL)
                    .header("Authorization", "key=" + SERVER_KEY)
                    .header("Content-Type", "application/json")
                    .method("POST", new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return null;
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            sink.write(requestJson.toString().getBytes());
                        }
                    }).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    failed(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    success();
                    response.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            failed(e);
        }
    }
}
