package com.bikebeacon.utils.compression;

import com.bikebeacon.utils.compression.CompressionUtil;

public abstract class BaseCompressionRunnable implements Runnable {

    protected CompressionUtil cUtil;

    public BaseCompressionRunnable(CompressionUtil util) {
        cUtil = util;
    }
}
