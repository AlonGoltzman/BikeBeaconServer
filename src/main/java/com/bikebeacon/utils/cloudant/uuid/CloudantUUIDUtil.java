package com.bikebeacon.utils.cloudant.uuid;

import com.bikebeacon.utils.cloudant.BaseCloudantElement;
import com.bikebeacon.utils.cloudant.BaseCloudantUtil;
import com.bikebeacon.utils.cloudant.DataStore;

import static com.bikebeacon.pojo.Constants.UUID_DB;

public class CloudantUUIDUtil extends BaseCloudantUtil {
    public CloudantUUIDUtil() {
        super("cloudantUUID.creds", UUID_DB);
    }

    @Override
    public DataStore<Unique> getDatastore(Object... params) {
        return new UUIDDatastore();
    }
}
