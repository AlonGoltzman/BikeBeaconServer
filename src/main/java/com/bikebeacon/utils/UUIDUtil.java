package com.bikebeacon.utils;

import com.bikebeacon.utils.cloudant.uuid.CloudantUUIDUtil;
import com.bikebeacon.utils.cloudant.uuid.UUIDDatastore;
import com.bikebeacon.utils.cloudant.uuid.Unique;
import com.cloudant.client.api.Database;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class UUIDUtil {

    public static Unique generate() {
        String uuid = UUID.randomUUID().toString();
        CloudantUUIDUtil uuidUtil = new CloudantUUIDUtil();
        UUIDDatastore store = (UUIDDatastore) uuidUtil.getDatastore();
        List<Unique> docs = (List<Unique>) store.getAll();
        for (Unique doc : docs)
            if (uuid.equals(doc.getUUID()))
                return generate();
        return store.upload(new Unique(uuid));
    }

}
