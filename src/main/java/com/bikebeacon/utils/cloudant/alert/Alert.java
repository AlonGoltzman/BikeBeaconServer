package com.bikebeacon.utils.cloudant.alert;

import com.bikebeacon.pojo.Fileable;
import com.bikebeacon.utils.cloudant.BaseCloudantElement;
import com.google.gson.JsonObject;

import static com.bikebeacon.pojo.Constants.*;

public class Alert extends BaseCloudantElement implements Fileable {

    private String owner;
    private String gpsCoords;
    private String previousAlertID;
    private String cellTowers;
    private String uuid;

    private boolean closed;

    private Alert() {
        super();
    }

    public String getOwner() {
        return owner;
    }

    private String getGPSCoords() {
        return gpsCoords;
    }

    private String getPreviousAlertID() {
        return previousAlertID;
    }

    private String getCellTowers() {
        return cellTowers;
    }

    public String getUuid() {
        return uuid;
    }

    private boolean isClosed() {
        return closed;
    }

    private void setOwner(String owner) {
        this.owner = owner;
    }

    private void setGPSCoords(String coords) {
        gpsCoords = coords;
    }

    private void setPreviousAlertID(String prevID) {
        previousAlertID = prevID;
    }

    private void setCellTowers(String towers) {
        cellTowers = towers;
    }

    private void setClosed(boolean closed) {
        this.closed = closed;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public static Alert fromJSON(JsonObject command) {
        Alert alert = new Alert();
        alert.setCellTowers(command.get(JSON_CELLTOWERS).getAsString());
        alert.setGPSCoords(command.get(JSON_GPS).getAsString());
        alert.setClosed(command.get(JSON_IS_CLOSED).getAsBoolean());
        alert.setPreviousAlertID(command.get(JSON_PREVIOUS_ALERT).getAsString());
        alert.setOwner(command.get(JSON_OWNER).getAsString());
        alert.setUuid(command.get(JSON_UUID).getAsString());
        return alert;
    }

    @Override
    public void override(BaseCloudantElement otherAlert) {
        if (!(otherAlert instanceof Alert))
            throw new IllegalArgumentException("Alert->override was given BaseCloudElement that isn't an Alert.");
        Alert alert = (Alert) otherAlert;
        setCellTowers(alert.getCellTowers());
        setClosed(alert.isClosed());
        setGPSCoords(alert.getGPSCoords());
    }

    @Override
    public JsonObject fileify() {
        JsonObject object = new JsonObject();
        object.addProperty(JSON_CELLTOWERS, getCellTowers());
        object.addProperty(JSON_GPS, getGPSCoords());
        object.addProperty(JSON_IS_CLOSED, isClosed());
        object.addProperty(JSON_PREVIOUS_ALERT, getPreviousAlertID());
        object.addProperty(JSON_OWNER, getOwner());
        object.addProperty(JSON_UUID, getUuid());
        return object;
    }
}
