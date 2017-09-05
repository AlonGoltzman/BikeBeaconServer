package com.bikebeacon.utils.cloudant;

public abstract class BaseCloudantElement {

    private String id;
    private String rev;

    public BaseCloudantElement() {
    }

    public BaseCloudantElement(String _id, String _rev) {
        id = _id;
        rev = _rev;
    }

    public String getID() {
        return id;
    }

    public String getRev() {
        return rev;
    }

    public void setID(String _id) {
        id = _id;
    }

    public void setRev(String _rev) {
        rev = _rev;
    }

    public abstract void override(BaseCloudantElement object);
}
