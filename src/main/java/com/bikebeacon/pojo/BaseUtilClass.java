package com.bikebeacon.pojo;

import com.bikebeacon.pojo.TaskCompletionListener;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseUtilClass {

    protected TaskCompletionListener listener;

    private static Map<String, Runnable> runnableMap;
    private Thread executionThread;

    public BaseUtilClass(TaskCompletionListener completionListener) {
        listener = completionListener;
        if(runnableMap == null)
            runnableMap = new HashMap<>();
    }

    public void setRunnable(Runnable r) {
        executionThread = new Thread(r);
    }

    public void setRunnable(String runnableName, Runnable r) {
        runnableMap.put(runnableName, r);
    }

    public void execute() {
        executionThread.start();
    }

    public void execute(String runnableName) {
        setRunnable(runnableMap.get(runnableName));
        execute();
    }

    public abstract void failed(@Nullable Object reason);

    public abstract void success(Object... results);

}
