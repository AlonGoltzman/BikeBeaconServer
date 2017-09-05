package com.bikebeacon.utils.cloudconvert;

import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import static com.bikebeacon.pojo.Constants.CLOUD_CONVERT_EXTENTION;
import static com.bikebeacon.pojo.Constants.CLOUD_CONVERT_STEP_OUTPUT;
import static com.bikebeacon.pojo.Constants.CLOUD_CONVERT_URL;
import static com.bikebeacon.utils.PrintUtil.error_f;

public class ProcessDoneCallback extends CloudConvertCallback {

    private OkHttpClient clientHTTP;
    private String processURL;

    public ProcessDoneCallback(CloudConvertUtil util) {
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

            JsonObject outputObject = json.getAsJsonObject(CLOUD_CONVERT_STEP_OUTPUT);
            String downloadURL = "https://" + outputObject.get(CLOUD_CONVERT_URL).getAsString();
            String extention = outputObject.get(CLOUD_CONVERT_EXTENTION).getAsString();

            Request newRequest = new Request.Builder().url(downloadURL).method("GET", null).build();

            clientHTTP.newCall(newRequest).enqueue(new DownloadTask(ccUtil));
            response.close();

        }
    }
}
