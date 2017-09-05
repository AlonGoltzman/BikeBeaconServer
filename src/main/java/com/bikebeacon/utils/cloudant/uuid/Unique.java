package com.bikebeacon.utils.cloudant.uuid;

import com.bikebeacon.utils.cloudant.BaseCloudantElement;

public class Unique extends BaseCloudantElement {

    private String uuid;

    public Unique(String uuid) {
        setUUID(uuid);
    }


    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public void override(BaseCloudantElement object) {
    }
}
