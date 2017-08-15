package com.bikebeacon.cloudant;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.bikebeacon.Alert;
import com.bikebeacon.utils.CloudantUtil;
import com.bikebeacon.utils.DataStore;
import com.cloudant.client.api.Database;

import static com.bikebeacon.utils.PrintUtils.*;

public class AlertStore implements DataStore<Alert> {

	private Database db;
	private String owner;

	public AlertStore(String name) {
		CloudantUtil util = CloudantUtil.getInstance();
		db = util.getAlertDB();
		owner = name;
	}

	@Override
	public Collection<Alert> getAll() {
		List<Alert> list = null;
		try {
			list = db.getAllDocsRequestBuilder()
					.includeDocs(true)
					.build()
					.getResponse()
					.getDocsAs(Alert.class)
					.stream()
					.filter((Alert alert)-> alert.getOwner().equals(owner))
					.collect(Collectors.toList());
		} catch (IOException e) {
			error_f("AlertStore->getAll", "Failed fetching documents from server.\n%s", e.getMessage());
		}
		return list;
	}

	@Override
	public Database getDB() {
		return db;
	}

	@Override
	public Alert get(String id) {
		return db.find(Alert.class, id);
	}

	@Override
	public long getCount() {
		return getAll().size();
	}

	@Override
	public Alert upload(Alert element) {
		String id = db.save(element).getId();
		return db.find(Alert.class, id);
	}

	@Override
	public void delete(String id) {
		Alert alert = db.find(Alert.class, id);
		db.remove(alert.getID(), alert.getRev());
	}

	@Override
	public Alert update(String id, Alert element) {
		Alert alert = db.find(Alert.class, id);
		//TODO: override details.
		db.update(alert);
		return db.find(Alert.class, id);
	}

}
