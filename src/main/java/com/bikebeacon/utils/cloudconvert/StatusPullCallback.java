package com.bikebeacon.utils.cloudconvert;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static com.bikebeacon.utils.PrintUtil.error_f;

public class StatusPullCallback extends CloudConvertCallback {

    private OkHttpClient clientHTTP;
    private String processURL;

    public StatusPullCallback(CloudConvertUtil util) {
        super(util);
        clientHTTP = util.getHTTPClient();
        processURL = util.getProcessURL();
    }

    @Override
    public void onFailure(Call call, IOException e) {
        error_f("CloudConvertUtil->ProcessDoneCallback", "Failed creating a process: %s", e.getMessage());
        ccUtil.failed(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.code() != 200) {
            error_f("CloudConvertUtil->ProcessDoneCallback", "Returned response that isn't 200. %s", response.message());
            clientHTTP.newCall(new Request.Builder()
                    .method("DELETE", null).url(processURL).build()).execute();
            ccUtil.failed(response);
        } else {
            Request statusUpdate = new Request.Builder()
                    .url(processURL).method("GET", null).build();
            clientHTTP.newCall(statusUpdate).enqueue(new StatusUpdateCallback(ccUtil));
            response.close();

        }
    }
}
