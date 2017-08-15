package com.bikebeacon.rest;

import static com.bikebeacon.utils.Constants.ALERT_CONVERSATION_RECEIVED;
import static com.bikebeacon.utils.Constants.ALERT_NEW;
import static com.bikebeacon.utils.Constants.GET;
import static com.bikebeacon.utils.Constants.JSON_ACTION;
import static com.bikebeacon.utils.Constants.JSON_OWNER;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bikebeacon.Alert;
import com.bikebeacon.cloudant.AlertStore;
import com.bikebeacon.utils.PrintUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@WebServlet("/alert_api")
public class AlertAPI extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 721982435L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendError(403);
		PrintUtils.log(GET, "IP: " + request.getRemoteAddr() + " tried to send alert.");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		InputStream stream = request.getInputStream();
		StringBuilder builder = new StringBuilder();

		byte[] buffer = new byte[1024];
		int aRead;

		while ((aRead = stream.read(buffer)) != -1)
			builder.append(new String(buffer, 0, aRead));

		stream.close();

		JsonObject command = (JsonObject) new JsonParser().parse(builder.toString());
		String action;
		String owner;
		if (command.get(JSON_ACTION) == null || command.get(JSON_OWNER) == null
				|| (action = command.get(JSON_ACTION).getAsString()) == null
				|| (owner = command.get(JSON_OWNER).getAsString()) == null) {
			response.sendError(400);
			return;
		}
		AlertStore alerts = new AlertStore(owner);

		switch (action) {
		case ALERT_NEW:
			Alert createdAlert = alerts.upload(Alert.fromJSON(command));
			response.getWriter().write(createdAlert.getID());
			break;
		case ALERT_CONVERSATION_RECEIVED:
			// TODO: inform CCH
			break;
		}
	}

}
