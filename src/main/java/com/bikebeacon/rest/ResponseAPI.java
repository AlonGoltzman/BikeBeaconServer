package com.bikebeacon.rest;

import com.bikebeacon.cch.CCHDelegate;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;

import static com.bikebeacon.pojo.Constants.RESPONSE_INPUT;
import static com.bikebeacon.pojo.Constants.RESPONSE_OUTPUT;

@WebServlet("/response_api")
public class ResponseAPI extends CCHDelegate {
    private ServletFileUpload uploader = null;

    @Override
    public void init() throws ServletException {
        DiskFileItemFactory fileFactory = new DiskFileItemFactory();
        File filesDir = new File(getServletContext().getRealPath(""), "/assets");
        fileFactory.setRepository(filesDir);
        this.uploader = new ServletFileUpload(fileFactory);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, String> queryMappified = queryMappify(request.getQueryString());
        String inputFormat = queryMappified.get(RESPONSE_INPUT);
        String outputFormat = queryMappified.get(RESPONSE_OUTPUT);

        File inputFile = new File(getServletContext().getRealPath(""), "/" + request.getRemoteAddr() + "response." + inputFormat);
        try {
            List<FileItem> fileItemsList = uploader.parseRequest(request);
            for (FileItem fileItem : fileItemsList) {
                fileItem.write(inputFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
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
