package com.bikebeacon.cch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public final class CentralControlHubFactChecker {

    private static CentralControlHubFactChecker dataHandler;

    private ArrayList<Case> allActiveCases;
    private ArrayList<CCHDelegate> delegates;
    private HashMap<String, String> MACToToken;
    private HashMap<String, File> keyForFile;
    private HashMap<String, FileKillTask> keyToTask;

    static CentralControlHubFactChecker getFactChecker() {
        return dataHandler == null ? new CentralControlHubFactChecker() : dataHandler;
    }

    private CentralControlHubFactChecker() {
        dataHandler = this;

        allActiveCases = new ArrayList<>();
        delegates = new ArrayList<>();
        MACToToken = new HashMap<>();
        keyForFile = new HashMap<>();
        keyToTask = new HashMap<>();
    }

    public boolean keyExists(String key) {
        return keyForFile.containsKey(key);
    }

    public boolean macExists(String MAC) {
        return MACToToken.containsKey(MAC);
    }

    void addActiveCase(Case activeCase) {
        if (!activeCase.isActive())
            throw new IllegalStateException("Only active cases can be added, use archiveCase instead.");
        allActiveCases.add(activeCase);
    }

    void addCCHDelegate(CCHDelegate delegate) {
        delegates.add(delegate);
    }

    void deleteCCHDelegate(CCHDelegate delegate) {
        delegates.remove(delegate);
    }

    void addNewKillTask(String key, FileKillTask task) {
        keyToTask.put(key, task);
    }

    Case getCase(String Mac) {
        ArrayList<Case> allCases = getAllActiveCases();
        try {
            return allCases.stream().filter(aCase -> aCase.getOriginAlert().getOwner().equals(Mac)).collect(Collectors.toList()).get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    FileKillTask getKillTask(String key) {
        return keyToTask.get(key);
    }

    ArrayList<Case> getAllActiveCases() {
        return allActiveCases;
    }

    ArrayList<CCHDelegate> getDelegates() {
        return delegates;
    }

    HashMap<String, String> getMACToTokenMap() {
        return MACToToken;
    }

    HashMap<String, File> getKeyForFileMap() {
        return keyForFile;
    }

    HashMap<String, FileKillTask> getKeyToTask() {
        return keyToTask;
    }
}
