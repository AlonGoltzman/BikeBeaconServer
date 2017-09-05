package com.bikebeacon.utils.cloudant.mailing_list;


import com.google.gson.JsonObject;

import java.io.Serializable;

import static com.bikebeacon.pojo.Constants.PERSON_JSON_NAME;
import static com.bikebeacon.pojo.Constants.PERSON_JSON_NUMBER;

public class Person implements Serializable {

    private String name;
    private String phoneNumber;

    public Person(JsonObject object) {
        if (object == null || object.size() != 2)
            throw new IllegalArgumentException("Object might not be correct.\n" + object);
        setName(object.get(PERSON_JSON_NAME).getAsString());
        setPhoneNumber(object.get(PERSON_JSON_NUMBER).getAsString());
    }

    public Person(String name, String phoneNumber) {
        setName(name);
        setPhoneNumber(phoneNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
