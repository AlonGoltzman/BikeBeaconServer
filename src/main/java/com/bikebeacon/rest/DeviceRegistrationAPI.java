package com.bikebeacon.rest;

import com.bikebeacon.cch.CCHDelegate;
import com.bikebeacon.utils.MailingListUtil;
import com.bikebeacon.utils.UUIDUtil;
import com.bikebeacon.utils.cloudant.BaseCloudantElement;
import com.bikebeacon.utils.cloudant.mailing_list.CloudantMailingUtil;
import com.bikebeacon.utils.cloudant.mailing_list.MailingList;
import com.bikebeacon.utils.cloudant.mailing_list.Person;
import com.bikebeacon.utils.cloudant.uuid.Unique;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet("/registration_api")
public class DeviceRegistrationAPI extends CCHDelegate {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (InputStream in = req.getInputStream(); OutputStream stream = resp.getOutputStream()) {
            StringBuilder json = new StringBuilder();
            byte[] buffer = new byte[1024];
            int aRead;

            while ((aRead = in.read(buffer)) != -1)
                json.append(new String(buffer, 0, aRead));
            Unique UUID = UUIDUtil.generate();
            if (UUID == null) {
                resp.sendError(500);
                return;
            }
            stream.write(UUID.getUUID().getBytes());

            JsonArray object = (JsonArray) new JsonParser().parse(json.toString());
            MailingList list = new MailingList();
            list.setOwner(UUID.getUUID());
            for (JsonElement element : object)
                list.getList().add(new Person(element.getAsJsonObject()));
            new CloudantMailingUtil().getDatastore(UUID).upload(list);
        }
    }
}
