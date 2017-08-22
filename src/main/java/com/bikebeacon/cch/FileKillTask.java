package com.bikebeacon.cch;

import java.util.TimerTask;

public class FileKillTask extends TimerTask {

    private String fileKey;

    private boolean execute = true;

    FileKillTask(String key) {
        fileKey = key;
    }

    void setExecute(boolean execute) {
        this.execute = execute;
    }

    @Override
    public void run() {
        if (execute) {
            CentralControlHub.getCCH().destroyKey(fileKey).delete();
        }
    }
}
