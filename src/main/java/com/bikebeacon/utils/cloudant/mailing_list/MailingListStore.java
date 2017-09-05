package com.bikebeacon.utils.cloudant.mailing_list;

import com.bikebeacon.pojo.Fileable;
import com.bikebeacon.utils.cloudant.DataStore;
import com.bikebeacon.utils.cloudant.alert.Alert;
import com.bikebeacon.utils.cloudant.uuid.Unique;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.bikebeacon.utils.PrintUtil.log;

public class MailingListStore implements DataStore<MailingList> {

    private Database db;
    private MailingList list;

    public MailingListStore(Unique unique) {
        CloudantMailingUtil util = new CloudantMailingUtil();
        db = util.getDatabase();
        try {
            List<MailingList> results = db.getAllDocsRequestBuilder()
                    .includeDocs(true)
                    .build()
                    .getResponse()
                    .getDocsAs(MailingList.class)
                    .stream()
                    .filter((MailingList m) -> m.getOwner().equals(unique.getUUID()))
                    .collect(Collectors.toList());
            if (results.size() > 0)
                list = results.get(0);
            else
                log("MailingListStore->constructor", "No document found for unqiue: " + unique.getUUID());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<MailingList> getAll() {
        try {
            return db.getAllDocsRequestBuilder()
                    .includeDocs(true)
                    .build()
                    .getResponse()
                    .getDocsAs(MailingList.class);
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
    public MailingList get(String id) {
        return db.find(MailingList.class, id);
    }

    @Override
    public MailingList upload(MailingList element) {
        Response saveRes = db.save(element);
        element.setID(saveRes.getId());
        element.setRev(saveRes.getRev());
        return element;
    }

    @Override
    public MailingList update(String id, MailingList element) {
        MailingList list = db.find(MailingList.class, id);
        //TODO: override details.
        db.update(list);
        return db.find(MailingList.class, id);
    }

    @Override
    public void delete(String id) {
        MailingList mailingList = db.find(MailingList.class, id);
        db.remove(mailingList.getID(), mailingList.getRev());
    }

    public MailingList getList() {
        return list;
    }
}
