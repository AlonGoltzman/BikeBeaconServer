package com.bikebeacon.utils.cloudant;

import java.util.Collection;

import com.cloudant.client.api.Database;

public interface DataStore<T> {

    Collection<T> getAll();

    Database getDB();

    long getCount();

    T get(String id);

    T upload(T element);

    T update(String id, T element);

    void delete(String id);

}
