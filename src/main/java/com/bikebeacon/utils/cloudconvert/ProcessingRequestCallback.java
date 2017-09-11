package com.bikebeacon.utils.cloudconvert;

import com.google.gson.JsonObject;
import okhttp3.*;
import okio.BufferedSink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.bikebeacon.pojo.Constants.CLOUD_CONVERT_UPLOAD_JSON;
import static com.bikebeacon.pojo.Constants.CLOUD_CONVERT_URL;
import static com.bikebeacon.utils.PrintUtil.error;
import static com.bikebeacon.utils.PrintUtil.error_f;

public class ProcessingRequestCallback extends CloudConvertCallback {

    private OkHttpClient clientHTTP;
    private String processURL;
    private File inputAudioFile;

    public ProcessingRequestCallback(CloudConvertUtil util) {
        super(util);
        clientHTTP = util.getHTTPClient();
        processURL = util.getProcessURL();
        inputAudioFile = util.getInputAudioFile();
        System.out.println("Initialized ProcessingRequestCallback.");
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
            String uploadUrl = json.get(CLOUD_CONVERT_UPLOAD_JSON).getAsJsonObject().get(CLOUD_CONVERT_URL).getAsString();
            processURL = "https://" + json.get(CLOUD_CONVERT_URL).getAsString();

            if (inputAudioFile == null) {
                error("CloudConvertUtil->ProcessingRequestCallback", "No audio file.");
                return;
            }
            final InputStream fileStream = new FileInputStream(inputAudioFile);

            Request uploadRequest = new Request.Builder()
                    .url("https:" + uploadUrl + "/" + inputAudioFile.getName()).header("Content-Length", String.valueOf(inputAudioFile.length())).method("PUT", new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return null;
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            byte[] buffer = new byte[1024];
                            while (fileStream.read(buffer) != -1)
                                sink.write(buffer);
                        }
                    }).build();
            clientHTTP.newCall(uploadRequest).enqueue(new StatusPullCallback(ccUtil));
            response.close();
        }
    }
}
