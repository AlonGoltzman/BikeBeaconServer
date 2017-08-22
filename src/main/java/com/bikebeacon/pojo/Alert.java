package com.bikebeacon.pojo;

import com.bikebeacon.cloudant.CloudantElement;
import com.bikebeacon.utils.Fileable;
import com.google.gson.JsonObject;

import java.io.Serializable;

import static com.bikebeacon.utils.Constants.*;

public class Alert extends CloudantElement implements Fileable {

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

    public void setOwner(String owner) {
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
        alert.setOwner(command.get(JSON_OWNER).getAsString());
        return alert;
    }

    @Override
    public JsonObject fileify() {
        JsonObject object = new JsonObject();
        object.addProperty(JSON_CELLTOWERS, getCellTowers());
        object.addProperty(JSON_GPS, getGPSCoords());
        object.addProperty(JSON_IS_CLOSED, isClosed());
        object.addProperty(JSON_PREVIOUS_ALERT, getPreviousAlertID());
        return object;
    }
}
