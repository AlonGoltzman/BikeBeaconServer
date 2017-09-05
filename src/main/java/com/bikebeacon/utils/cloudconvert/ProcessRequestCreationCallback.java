package com.bikebeacon.utils.cloudconvert;

import com.google.gson.JsonObject;
import okhttp3.*;
import okio.BufferedSink;

import java.io.IOException;

import static com.bikebeacon.pojo.Constants.CLOUD_CONVERT_PROCESS_REQUEST_INPUT_TYPE;
import static com.bikebeacon.pojo.Constants.CLOUD_CONVERT_PROCESS_REQUEST_OUTPUT_FORMAT;
import static com.bikebeacon.pojo.Constants.CLOUD_CONVERT_URL;
import static com.bikebeacon.utils.PrintUtil.error_f;
import static com.bikebeacon.utils.PrintUtil.log_f;

public class ProcessRequestCreationCallback extends CloudConvertCallback {

    private OkHttpClient clientHTTP;
    private String processURL;
    private String outputFormat;
    private String apiKey;

    public ProcessRequestCreationCallback(CloudConvertUtil util) {
        super(util);
        clientHTTP = util.getHTTPClient();
        processURL = util.getProcessURL();
        outputFormat = util.getOutputFormat();
        apiKey = util.getApiKey();
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
            ccUtil.failed(response);
        } else {
            JsonObject json = ccUtil.getJSONFromResponse(response);

            ccUtil.setProcessURL((processURL = "https:" + json.get(CLOUD_CONVERT_URL).getAsString()));

            final JsonObject requestJson = new JsonObject();
            requestJson.addProperty(CLOUD_CONVERT_PROCESS_REQUEST_INPUT_TYPE, "upload");
            requestJson.addProperty(CLOUD_CONVERT_PROCESS_REQUEST_OUTPUT_FORMAT, outputFormat == null ? "mp3" : outputFormat);
            log_f("CloudConvertUtil->ProcessRequestCreationCallback", "Json is: %s", requestJson.toString());

            Request starProcessingRequest = new Request.Builder()
                    .url(processURL).method("POST", new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return null;
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            sink.write(requestJson.toString().getBytes());
                        }
                    }).header("Authorization", "Bearer " + apiKey).header("Content-Type", "application/json").build();
            clientHTTP.newCall(starProcessingRequest).enqueue(new ProcessingRequestCallback(ccUtil));
            response.close();
        }
    }
}
