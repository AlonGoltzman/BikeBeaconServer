package com.bikebeacon.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bikebeacon.cch.CCHDelegate;
import com.bikebeacon.cch.Case;
import com.bikebeacon.utils.cloudant.alert.Alert;
import com.bikebeacon.utils.cloudant.alert.AlertStore;
import com.bikebeacon.pojo.Constants;
import com.bikebeacon.utils.PrintUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static com.bikebeacon.pojo.Constants.*;

@WebServlet("/alert_api")
public class AlertAPI extends CCHDelegate {
    /**
     *
     */
    private static final long serialVersionUID = 721982435L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(403);
        PrintUtil.log(GET, "IP: " + request.getRemoteAddr() + " tried to send alert.");
        System.out.println(getServletContext().getRealPath(""));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        InputStream stream = request.getInputStream();
        OutputStream out = response.getOutputStream();
        StringBuilder builder = new StringBuilder();

        byte[] buffer = new byte[1024];
        int aRead;

        while ((aRead = stream.read(buffer)) != -1)
            builder.append(new String(buffer, 0, aRead));

        JsonObject command = (JsonObject) new JsonParser().parse(builder.toString());
        String action;
        String uuid;
        String owner = request.getRemoteAddr();
        if (command.get(JSON_UUID) == null || (uuid = command.get(JSON_UUID).getAsString()) == null ||
                command.get(JSON_ACTION) == null
                || (action = command.get(JSON_ACTION).getAsString()) == null) {
            response.sendError(400);
            out.close();
            stream.close();
            return;
        }
        if (command.has(JSON_OWNER))
            command.remove(JSON_OWNER);
        command.addProperty(JSON_OWNER, owner);
        AlertStore alerts = new AlertStore(owner);
        String ID = null;
        if (command.get(JSON_ID) != null)
            ID = command.get(JSON_ID).getAsString();
        switch (action) {
            case ALERT_NEW:
                Alert createdAlert = alerts.upload(Alert.fromJSON(command));
                out.write(createdAlert.getID().getBytes());
                new Thread(() -> {
                    Case c = new Case();
                    c.setActive(true);
                    c.setOriginAlert(createdAlert);
                    CCH.entrustCase(AlertAPI.this, c);
                }).start();
                break;
            case ALERT_DELTE:
                alerts.delete(ID);
                break;
            case ALERT_UPDATE:
                Alert alertUpdate = alerts.update(ID, Alert.fromJSON(command));
                out.write(alertUpdate.getID().getBytes());
                break;
        }

        stream.close();
        out.close();
    }

}
