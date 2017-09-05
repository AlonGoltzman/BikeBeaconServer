package com.bikebeacon.utils;

public class GeneralUtils {

    public static Object[] addObjects(Object objectToAdd, Object... objects) {
        Object[] newObjects = new Object[objects == null ? 1 : objects.length + 1];
        newObjects[0] = objectToAdd;
        if (objects != null)
            System.arraycopy(objects, 0, newObjects, 1, objects.length);
        return newObjects;
    }

}
