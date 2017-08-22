package com.bikebeacon.utils;

import com.google.gson.JsonObject;
import okhttp3.*;
import okio.BufferedSink;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.bikebeacon.utils.Constants.POST;
import static com.bikebeacon.utils.PrintUtil.error_f;
import static com.bikebeacon.utils.PrintUtil.log;
import static com.bikebeacon.utils.PrintUtil.log_f;

public final class FCMUtil {

    private static final String SERVER_KEY = "AAAAAW4AS3U:APA91bFPEkys4V_tUCYpRwf1BDDY15GaggjUbr-DvQF8d3PNPmEiBcar7o0ruqtdQuSk8CwqzdHaXHYmSTJ0W5CiMxI_v4dQZirniDfVg7auOl3_K2lMXQJkArYIhbZAPCd4TKAD3Y_J";

    private URL serverURL;
    private String MAC;

    public FCMUtil(String mac) {
        MAC = mac;
        try {
            serverURL = new URL("https://fcm.googleapis.com/fcm/send");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void send(JsonObject json) {
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
                        sink.write(json.toString().getBytes());
                    }
                }).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                error_f("FCMUtil->send", "Failed sending message.\n%s", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log_f("FCMUtil->send", "Sent message using FCM, %d", response.code());
                response.close();
            }
        });
    }
}
