package com.bikebeacon.rest;

import com.bikebeacon.cch.CCHDelegate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.bikebeacon.utils.Constants.RESPONSE_INPUT;
import static com.bikebeacon.utils.Constants.RESPONSE_OUTPUT;

@WebServlet("/response_api")
public class ResponseAPI extends CCHDelegate {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> queryMappified = queryMappify(req.getQueryString());
        String inputFormat = queryMappified.get(RESPONSE_INPUT);
        String outputFormat = queryMappified.get(RESPONSE_OUTPUT);

        File inputFile = new File(getServletContext().getRealPath(""), "/" + req.getRemoteAddr() + "response." + inputFormat);
        try (InputStream stream = req.getInputStream();
             FileOutputStream out = new FileOutputStream(inputFile)) {
            byte[] buffer = new byte[1024];

            while (stream.read(buffer) != -1)
                out.write(buffer);
        }

        new Thread(() -> {
            CCH.receivedResponse(inputFormat, outputFormat, inputFile);
        }).start();
    }

    private HashMap<String, String> queryMappify(String query) {
        String[] queryMap = query.split("&");
        HashMap<String, String> result = new HashMap<>();
        for (String queryPart : queryMap) {
            String[] queryPartDeconstructed = queryPart.split("=");
            result.put(queryPartDeconstructed[0], queryPartDeconstructed[1]);
        }
        return result;
    }
}
