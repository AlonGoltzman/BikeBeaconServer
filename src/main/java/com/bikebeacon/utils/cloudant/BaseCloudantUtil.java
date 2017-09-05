package com.bikebeacon.utils.cloudant;

import com.bikebeacon.utils.AssetsUtil;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import static com.bikebeacon.utils.PrintUtil.error_f;
import static com.bikebeacon.utils.PrintUtil.log;
import static com.bikebeacon.utils.PrintUtil.log_f;

public abstract class BaseCloudantUtil {

    private String vcapServices;
    private CloudantClient client;
    private Database database;

    public BaseCloudantUtil(String fileName, String databaseName) {
        vcapServices = System.getenv("VCAP_SERVICES");
        client = createClient(fileName);
        if (client != null)
            database = client.database(databaseName, false);
    }

    private CloudantClient createClient(String fileName) {
        String url;
        String password = null;
        String username = null;
        if (System.getenv("VCAP_SERVICES") != null) {
            // We're inside bluemix.
            JsonObject cloudantCreds = getCreds();
            if (cloudantCreds == null) {
                log("CloudantUtil->createClient", "No Cloudant Bound to app.");
                return null;
            }
            url = cloudantCreds.get("url").getAsString();
        } else {
            log("CloudantUtil->createClient", "Running locally, searching inside assets/.creds");
            AssetsUtil.FileContentDistributor dist = AssetsUtil.load(fileName).extractContent();
            url = dist.getLine(1);
            if (url == null || url.length() == 0) {
                log("CloudantUtil->createClient",
                        "Couldn't load url from .creds, only possible option left is manual override, not supported.");
                return null;
            }
            password = dist.getLine(2);
            if (password == null || password.length() == 0) {
                log("CloudantUtil->createClient",
                        "Couldn't load url from .creds, only possible option left is manual override, not supported.");
                return null;
            }
            username = dist.getLine(3);
            if (username == null || username.length() == 0) {
                log("CloudantUtil->createClient",
                        "Couldn't load url from .creds, only possible option left is manual override, not supported.");
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

    private JsonObject getCreds() {
        if (vcapServices == null)
            return null;
        JsonObject obj = (JsonObject) new JsonParser().parse(vcapServices);
        Map.Entry<String, JsonElement> dbEntry = null;
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();

        for (Map.Entry<String, JsonElement> eachEntry : entries)
            if (eachEntry.getKey().toLowerCase().contains("cloudant")) {
                dbEntry = eachEntry;
                break;
            }
        if (dbEntry == null) {
            log_f("CloudantUtil->getCreds", "Cloudn't find %s in VCAP_SERVICES", "cloudant");
            return null;
        }

        obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
        log_f("CloudantUtil->getCreds", "Found: " + (String) dbEntry.getKey() + " inside VCAP_SERVICES.");

        return obj.get("credentials").getAsJsonObject();
    }

    public CloudantClient getClient() {
        return client;
    }

    public Database getDatabase() {
        return database;
    }

    public abstract DataStore<? extends BaseCloudantElement> getDatastore(Object... params);
}
