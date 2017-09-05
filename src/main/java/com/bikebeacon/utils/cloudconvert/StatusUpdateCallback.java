package com.bikebeacon.utils.cloudconvert;

import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static com.bikebeacon.pojo.Constants.*;
import static com.bikebeacon.utils.PrintUtil.error_f;
import static com.bikebeacon.utils.PrintUtil.log;

public class StatusUpdateCallback extends CloudConvertCallback {

    private OkHttpClient clientHTTP;
    private String processURL;

    public StatusUpdateCallback(CloudConvertUtil util) {
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
            JsonObject json = ccUtil.getJSONFromResponse(response);

            String currentStep = json.get(CLOUD_CONVERT_STEP).getAsString();
            Request newRequest;
            switch (currentStep) {
                case CLOUD_CONVERT_STEP_CONVERT:
                    log("CloudConvertUtil->StatusUpdateCallback", "Still converting.");
                    newRequest = new Request.Builder()
                            .url(processURL).method("GET", null).build();
                    clientHTTP.newCall(newRequest).enqueue(new StatusUpdateCallback(ccUtil));
                    break;
                case CLOUD_CONVERT_STEP_ERROR:
                    log("CloudConvertUtil->StatusUpdateCallback", "Error");
                    //TODO: check error.
                    break;
                case CLOUD_CONVERT_STEP_FINISHED:
                    newRequest = new Request.Builder()
                            .url(processURL).method("GET", null).build();
                    clientHTTP.newCall(newRequest).enqueue(new ProcessDoneCallback(ccUtil));
                    break;
            }
            response.close();
        }
    }
}
