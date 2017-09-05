package com.bikebeacon.pojo;

public abstract class TaskRunnable implements Runnable {

    private TaskCompletionListener listener;

    public TaskRunnable(TaskCompletionListener completionListener) {
        listener = completionListener;
    }

    public TaskCompletionListener getListener() {
        return listener;
    }
}
