package com.bikebeacon.utils.cloudant.uuid;

import com.bikebeacon.utils.cloudant.DataStore;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import java.io.IOException;
import java.util.Collection;

public class UUIDDatastore implements DataStore<Unique> {

    private Database db;

    public UUIDDatastore() {
        CloudantUUIDUtil util = new CloudantUUIDUtil();
        db = util.getDatabase();
    }

    @Override
    public Collection<Unique> getAll() {
        try {
            return db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Unique.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Database getDB() {
        return db;
    }

    @Override
    public long getCount() {
        return getAll().size();
    }

    @Override
    public Unique get(String id) {
        return db.find(Unique.class, id);
    }

    @Override
    public Unique upload(Unique element) {
        Response response = db.save(element);
        element.setID(response.getId());
        element.setRev(response.getRev());
        return element;
    }

    @Override
    public Unique update(String id, Unique element) {
        Unique unique = db.find(Unique.class, id);

        return null;
    }

    @Override
    public void delete(String id) {

    }
}
