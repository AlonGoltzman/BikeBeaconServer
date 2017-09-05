package com.bikebeacon.utils.cloudant.alert;

import static com.bikebeacon.pojo.Constants.ALERT_DB_NAME;
import static com.bikebeacon.utils.PrintUtil.log;
import static com.bikebeacon.utils.PrintUtil.log_f;
import static com.bikebeacon.utils.PrintUtil.error_f;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;

import com.bikebeacon.utils.AssetsUtil;
import com.bikebeacon.utils.cloudant.BaseCloudantElement;
import com.bikebeacon.utils.cloudant.BaseCloudantUtil;
import com.bikebeacon.utils.cloudant.DataStore;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.bikebeacon.utils.AssetsUtil.FileContentDistributor;

public class CloudantAlertUtil extends BaseCloudantUtil {

    public CloudantAlertUtil() {
        super("cloudantAlert.creds", ALERT_DB_NAME);
    }

    @Override
    public DataStore<Alert> getDatastore(Object... params) {
        if (params[0] == null || !(params[0] instanceof String))
            throw new IllegalArgumentException("For CloudantAlertUtil-> getDatastore you need to pass the owner name as a parameter.");
        String owner = params[0].toString();
        return new AlertStore(owner);
    }
}
