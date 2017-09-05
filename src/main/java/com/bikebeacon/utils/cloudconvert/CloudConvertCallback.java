package com.bikebeacon.utils.cloudconvert;

import com.bikebeacon.utils.cloudconvert.CloudConvertUtil;
import okhttp3.Callback;

public abstract class CloudConvertCallback implements Callback {

    protected CloudConvertUtil ccUtil;

    public CloudConvertCallback(CloudConvertUtil util) {
        ccUtil = util;
    }
}
