package com.bikebeacon.utils.cloudant.mailing_list;

import com.bikebeacon.utils.cloudant.BaseCloudantElement;
import com.bikebeacon.utils.cloudant.BaseCloudantUtil;
import com.bikebeacon.utils.cloudant.DataStore;
import com.bikebeacon.utils.cloudant.uuid.Unique;

import static com.bikebeacon.pojo.Constants.MAILING_LIST_DB;

public class CloudantMailingUtil extends BaseCloudantUtil {
    public CloudantMailingUtil() {
        super("cloudantMailingList.creds", MAILING_LIST_DB);
    }

    @Override
    public DataStore<MailingList> getDatastore(Object... params) {
        if (params[0] == null || (!(params[0] instanceof Unique) && !(params[0] instanceof String)))
            throw new IllegalArgumentException("For CloudantUUIDUtil->getDatastore you need to pass the owner name as a parameter.");
        return new MailingListStore(params[0] instanceof Unique ? (Unique) params[0] : new Unique(params[0].toString()));
    }
}
