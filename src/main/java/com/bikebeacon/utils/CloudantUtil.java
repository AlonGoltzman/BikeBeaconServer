package com.bikebeacon.utils;

import static com.bikebeacon.utils.Constants.ALERT_DB_NAME;
import static com.bikebeacon.utils.PrintUtil.log;
import static com.bikebeacon.utils.PrintUtil.log_f;
import static com.bikebeacon.utils.PrintUtil.error_f;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.bikebeacon.utils.AssetsUtil.FileContentDistributor;

public class CloudantUtil {

    private final Class<CloudantUtil> LOCK = CloudantUtil.class;

    private String vcapServices;
    private Database alertDB = null;
    private CloudantClient client;

    private static CloudantUtil instance;

    public static CloudantUtil getInstance() {
        return instance == null ? new CloudantUtil() : instance;
    }

    private CloudantUtil() {
        instance = this;
        vcapServices = System.getenv("VCAP_SERVICES");
        client = createClient();
        if (client != null)
            setAlertDB(client.database(ALERT_DB_NAME, false));
    }

    private CloudantClient createClient() {
        String url;
        String password = null;
        String username = null;
        if (System.getenv("VCAP_SERVICES") != null) {
            // We're inside bluemix.
            JsonObject cloudantCreds = getCreds("cloudant");
            if (cloudantCreds == null) {
                log("CloudantUtil->createClient", "No Cloudant Bound to app.");
                return null;
            }
            url = cloudantCreds.get("url").getAsString();
        } else {
            log("CloudantUtil->createClient", "Running locally, searching inside assets/.cloudanturl");
            FileContentDistributor dist = AssetsUtil.load("cloudant.creds").extractContent();
            url = dist.getLine(1);
            if (url == null || url.length() == 0) {
                log("CloudantUtil->createClient",
                        "Couldn't load url from .cloudanturl, only possible option left is manual override, not supported.");
                return null;
            }
            password = dist.getLine(2);
            if (password == null || password.length() == 0) {
                log("CloudantUtil->createClient",
                        "Couldn't load url from .cloudanturl, only possible option left is manual override, not supported.");
                return null;
            }
            username = dist.getLine(3);
            if (username == null || username.length() == 0) {
                log("CloudantUtil->createClient",
                        "Couldn't load url from .cloudanturl, only possible option left is manual override, not supported.");
                return null;
            }
        }

        try {
            log("CloudantUtil->createClient", "Connecting to Cloudant...");
            CloudantClient c;
            if (password == null)
                c = ClientBuilder.url(new URL(url)).build();
            else
                c = ClientBuilder.url(new URL(url)).password(password).username(username).build();

            return c;
        } catch (MalformedURLException e) {
            error_f("CloudantUtil->createClient", "Unable to connect to the database, %s", e.toString());
            return null;
        }
    }

    private JsonObject getCreds(String service) {
        if (vcapServices == null)
            return null;
        JsonObject obj = (JsonObject) new JsonParser().parse(vcapServices);
        Entry<String, JsonElement> dbEntry = null;
        Set<Entry<String, JsonElement>> entries = obj.entrySet();

        for (Entry<String, JsonElement> eachEntry : entries)
            if (eachEntry.getKey().toLowerCase().contains(service)) {
                dbEntry = eachEntry;
                break;
            }
        if (dbEntry == null) {
            log_f("CloudantUtil->getCreds", "Cloudn't find %s in VCAP_SERVICES", service);
            return null;
        }

        obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
        log_f("CloudantUtil->getCreds", "Found: " + (String) dbEntry.getKey() + " inside VCAP_SERVICES.");

        return obj.get("credentials").getAsJsonObject();
    }

    public Database getAlertDB() {
        return alertDB;
    }

    public void setAlertDB(Database db) {
        alertDB = db;
    }

}
