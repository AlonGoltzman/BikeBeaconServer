package com.bikebeacon.rest;

import com.bikebeacon.cch.CCHDelegate;
import com.bikebeacon.cch.CentralControlHub;
import com.bikebeacon.cch.CentralControlHubFactChecker;
import okhttp3.internal.http.HttpHeaders;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import static com.bikebeacon.utils.PrintUtil.log_f;

@WebServlet("/jerry_api")
public class MessageFileAPI extends CCHDelegate {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String key = req.getQueryString();
        OutputStream stream = resp.getOutputStream();
        if (key.isEmpty()) {
            log_f("MessageFileAPI->doGet", "Someone tried to get file for key: %s", req.getQueryString());
            resp.sendError(403);
            return;
        }
        resp.setContentType("audio/wav");
        CentralControlHubFactChecker factChecker = CentralControlHub.getFactChecker();
        if (factChecker.macExists(req.getRemoteAddr()))
            if (factChecker.keyExists(key)) {
                FileInputStream in = new FileInputStream(CCH.destroyKey(key));
                byte[] buffer = new byte[1024];
                while (in.read(buffer) != -1)
                    stream.write(buffer);
                stream.flush();
                stream.close();
                in.close();
                return;
            }

        log_f("MessageFileAPI->doGet", "Someone tried to get file for key: %s", req.getQueryString());
        resp.sendError(403);
    }

}
