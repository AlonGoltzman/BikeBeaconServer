package com.bikebeacon.rest;

import com.bikebeacon.cch.CCHDelegate;
import com.bikebeacon.cch.CentralControlHub;
import com.bikebeacon.cch.CentralControlHubFactChecker;
import okhttp3.internal.http.HttpHeaders;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;

import static com.bikebeacon.utils.PrintUtil.log_f;

@WebServlet("/jerry_api")
public class MessageFileAPI extends CCHDelegate {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        String key = req.getQueryString();
        OutputStream stream = response.getOutputStream();
        if (key.isEmpty()) {
            log_f("MessageFileAPI->doGet", "No key given");
            response.sendError(403);
            return;
        }
        CentralControlHubFactChecker factChecker = CentralControlHub.getFactChecker();
        if (factChecker.macExists(req.getRemoteAddr()))
            if (factChecker.keyExists(key)) {
                File file = CCH.destroyKey(key);
                ServletContext ctx = getServletContext();
                InputStream fis = new FileInputStream(file);
                String mimeType = ctx.getMimeType(file.getAbsolutePath());
                response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
                response.setContentLength((int) file.length());
                response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

                ServletOutputStream os = response.getOutputStream();
                byte[] bufferData = new byte[1024];
                int read;
                while ((read = fis.read(bufferData)) != -1) {
                    os.write(bufferData, 0, read);
                }
                os.flush();
                os.close();
                fis.close();
                return;
            }
        log_f("MessageFileAPI->doGet", "Someone tried to get file for key: %s", req.getQueryString());
        response.sendError(403);
    }

}
