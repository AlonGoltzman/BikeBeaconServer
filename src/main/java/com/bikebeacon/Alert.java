package com.bikebeacon;

import com.bikebeacon.cloudant.CloudantElement;
import com.google.gson.JsonObject;

import static com.bikebeacon.utils.Constants.*;

public class Alert extends CloudantElement {

	private String owner;
	private String gpsCoords;
	private String previousAlertID;
	private String cellTowers;
	
	private boolean closed;

	Alert() {
		super();
	}

	public String getOwner() {
		return owner;
	}

	public void getOwner(String owner) {
		this.owner = owner;
	}

	public String getGPSCoords() {
		return gpsCoords;
	}

	public void setGPSCoords(String coords) {
		gpsCoords = coords;
	}

	public String getPreviousAlertID() {
		return previousAlertID;
	}

	public void setPreviousAlertID(String prevID) {
		previousAlertID = prevID;
	}

	public String getCellTowers() {
		return cellTowers;
	}

	public void setCellTowers(String towers) {
		cellTowers = towers;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public static Alert fromJSON(JsonObject command) {	
		Alert alert = new Alert();
		alert.setCellTowers(command.get(JSON_CELLTOWERS).getAsString());
		alert.setGPSCoords(command.get(JSON_GPS).getAsString());
		alert.setClosed(command.get(JSON_IS_CLOSED).getAsBoolean());
		alert.setPreviousAlertID(command.get(JSON_PREVIOUS_ALERT).getAsString());
		return alert;
	}

}
