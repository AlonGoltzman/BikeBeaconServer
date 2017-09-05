package com.bikebeacon.utils.cloudconvert;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.bikebeacon.utils.PrintUtil.error_f;

public class DownloadTask extends CloudConvertCallback {

    private OkHttpClient clientHTTP;
    private String processURL;
    private File convertedAudioFile;

    public DownloadTask(CloudConvertUtil util) {
        super(util);
        clientHTTP = util.getHTTPClient();
        processURL = util.getProcessURL();
        convertedAudioFile = util.getConvertedAudioFile();
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
            InputStream stream = response.body().byteStream();

            FileOutputStream output = new FileOutputStream(convertedAudioFile);

            byte[] buffer = new byte[1024];

            while (stream.read(buffer) != -1)
                output.write(buffer);

            stream.close();
            output.close();

            ccUtil.success(convertedAudioFile);

            clientHTTP.newCall(new Request.Builder()
                    .method("DELETE", null).url(processURL).build()).execute();
            response.close();
        }
    }
}
