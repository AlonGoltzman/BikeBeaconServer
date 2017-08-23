package com.bikebeacon.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okio.BufferedSink;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.bikebeacon.utils.Constants.*;
import static com.bikebeacon.utils.PrintUtil.error;
import static com.bikebeacon.utils.PrintUtil.error_f;
import static com.bikebeacon.utils.PrintUtil.log;

/**
 * Created by Mgr on 8/23/2017.
 */
public class CloudConvertUtil {

    private URL cloudConvertURL;
    private String apiKey;
    private OkHttpClient clientHTTP;

    private String inputFormat;
    private String outputFormat;

    private String processURL;
    private String processID;

    private File inputAudioFile;
    private File convertedAudioFile;

    public CloudConvertUtil() {
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
    }

    public void processRequest(final JsonObject requestJSON, File uploadFile, File outputFile) {
        inputAudioFile = uploadFile;
        convertedAudioFile = outputFile;
        if (!convertedAudioFile.exists()) {
            assert convertedAudioFile.getParentFile().mkdirs();
            try {
                assert convertedAudioFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Request conversionRequest = new Request.Builder()
                .url(cloudConvertURL).method("POST", new RequestBody() {
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {
                        sink.write(requestJSON.toString().getBytes());
                    }
                }).header("Authorization", "Bearer " + apiKey).build();
        clientHTTP.newCall(conversionRequest).enqueue(new ProcessRequestCreationCallback());
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }


    private JsonObject getJSONFromResponse(Response response) throws IOException {
        InputStream resp = response.body().byteStream();

        StringBuilder jsonResponseString = new StringBuilder();

        byte[] buffer = new byte[1024];
        int aRead;

        while ((aRead = resp.read(buffer)) != -1)
            jsonResponseString.append(new String(buffer, 0, aRead));

        resp.close();

        return (JsonObject) new JsonParser().parse(jsonResponseString.toString());
    }

    private class ProcessRequestCreationCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            error_f("CloudConvertUtil->ProcessRequestCreationCallback", "Failed creating a process: %s", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                error_f("CloudConvertUtil->ProcessRequestCreationCallback", "Returned response that isn't 200. %s", response.message());
            } else {
                JsonObject json = getJSONFromResponse(response);

                String url = json.get(CLOUD_CONVERT_URL).getAsString();
                String id = json.get(CLOUD_CONVERT_URL).getAsString();

                final JsonObject requestJson = new JsonObject();
                requestJson.addProperty(CLOUD_CONVERT_PROCESS_REQUEST_OUTPUT_FORMAT, outputFormat == null ? "mp3" : outputFormat);
                requestJson.addProperty(CLOUD_CONVERT_PROCESS_REQUEST_INPUT_TYPE, "upload");

                Request starProcessingRequest = new Request.Builder()
                        .url(url).method("POST", new RequestBody() {
                            @Override
                            public MediaType contentType() {
                                return null;
                            }

                            @Override
                            public void writeTo(BufferedSink sink) throws IOException {
                                sink.write(requestJson.toString().getBytes());
                            }
                        }).build();
                clientHTTP.newCall(starProcessingRequest).enqueue(new ProcessingRequestCallback());
            }
        }
    }

    private class ProcessingRequestCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            error_f("CloudConvertUtil->ProcessingRequestCallback", "Failed creating a process: %s", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                error_f("CloudConvertUtil->ProcessingRequestCallback", "Returned response that isn't 200. %s", response.message());
            } else {
                JsonObject json = getJSONFromResponse(response);
                String uploadUrl = json.get(CLOUD_CONVERT_UPLOAD_JSON).getAsJsonObject().get(CLOUD_CONVERT_URL).getAsString();
                processURL = json.get(CLOUD_CONVERT_URL).getAsString();
                processID = json.get(CLOUD_CONVERT_ID).getAsString();

                if (inputAudioFile == null) {
                    error("CloudConvertUtil->ProcessingRequestCallback", "No audio file.");
                    return;
                }
                final InputStream fileStream = new FileInputStream(inputAudioFile);

                Request uploadRequest = new Request.Builder()
                        .url(uploadUrl + "/" + inputAudioFile.getName()).header("Content-Length", String.valueOf(inputAudioFile.length())).method("PUT", new RequestBody() {
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
                clientHTTP.newCall(uploadRequest).enqueue(new StatusPullCallback());
            }
        }
    }

    private class StatusPullCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            error_f("CloudConvertUtil->StatusUpdateCallback", "Failed creating a process: %s", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                error_f("CloudConvertUtil->StatusUpdateCallback", "Returned response that isn't 200. %s", response.message());
            } else {
                Request statusUpdate = new Request.Builder()
                        .url(processURL).method("GET", null).build();

                clientHTTP.newCall(statusUpdate).enqueue(new StatusUpdateCallback());

            }
        }
    }

    private class StatusUpdateCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            error_f("CloudConvertUtil->StatusUpdateCallback", "Failed creating a process: %s", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                error_f("CloudConvertUtil->StatusUpdateCallback", "Returned response that isn't 200. %s", response.message());
            } else {
                JsonObject json = getJSONFromResponse(response);

                String currentStep = json.get(CLOUD_CONVERT_STEP).getAsString();
                Request newRequest;
                switch (currentStep) {
                    case CLOUD_CONVERT_STEP_CONVERT:
                        log("CloudConvertUtil->StatusUpdateCallback", "Still converting.");
                        newRequest = new Request.Builder()
                                .url(processURL).method("GET", null).build();
                        clientHTTP.newCall(newRequest).enqueue(new StatusUpdateCallback());
                        break;
                    case CLOUD_CONVERT_STEP_ERROR:
                        log("CloudConvertUtil->StatusUpdateCallback", "Error");
                        //TODO: check error.
                        break;
                    case CLOUD_CONVERT_STEP_FINISHED:
                        newRequest = new Request.Builder()
                                .url(processURL).method("GET", null).build();
                        clientHTTP.newCall(newRequest).enqueue(new ProcessDoneCallback());
                        break;
                }
            }
        }
    }

    private class ProcessDoneCallback implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {
            error_f("CloudConvertUtil->ProcessDoneCallback", "Failed creating a process: %s", e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.code() != 200) {
                error_f("CloudConvertUtil->ProcessDoneCallback", "Returned response that isn't 200. %s", response.message());
            } else {
                JsonObject json = getJSONFromResponse(response);

                JsonObject outputObject = json.getAsJsonObject(CLOUD_CONVERT_STEP_OUTPUT);
                String downloadURL = outputObject.get(CLOUD_CONVERT_URL).getAsString();
                String extention = outputObject.get(CLOUD_CONVERT_EXTENTION).getAsString();

                Request newRequest = new Request.Builder().url(downloadURL).method()

            }
        }
    }
}

