package com.bikebeacon.utils;

import com.bikebeacon.cch.Case;
import com.bikebeacon.pojo.BaseUtilClass;
import com.bikebeacon.pojo.CCHResponders;
import com.bikebeacon.pojo.TaskCompletionListener;
import com.bikebeacon.utils.cloudant.mailing_list.CloudantMailingUtil;
import com.bikebeacon.utils.cloudant.mailing_list.MailingList;
import com.bikebeacon.utils.cloudant.mailing_list.MailingListStore;
import com.bikebeacon.utils.cloudant.mailing_list.Person;
import com.bikebeacon.utils.cloudant.uuid.Unique;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.bikebeacon.utils.GeneralUtils.addObjects;
import static com.bikebeacon.utils.PrintUtil.log;

public class MailingListUtil extends BaseUtilClass implements Runnable {

    private static final int RESPONDER_CODE = CCHResponders.MAILINGLIST.getId();

    private MailingList list;
    private String person;
    private String phoneNumber;
    private String message;

    private boolean shouldSkip = false;

    private Case ownerCase;

    public MailingListUtil(Unique unique, TaskCompletionListener completionListener) {
        super(completionListener);
        list = ((MailingListStore) new CloudantMailingUtil().getDatastore(unique)).getList();
        setRunnable(this);
    }

    @Override
    public void failed(@Nullable Object reason) {
        if (reason != null) {
            if (reason instanceof Exception)
                listener.onFailed(RESPONDER_CODE, (Throwable) reason);
        } else
            listener.onFailed(RESPONDER_CODE, null);
    }

    @Override
    public void success(Object... results) {
        listener.onSuccess(RESPONDER_CODE, addObjects(ownerCase, addObjects(message, results)));
    }

    @Override
    public void run() {
        if (shouldSkip || message == null || message.isEmpty()) {
            success();
            return;
        }
        boolean hasPerson = person != null && !person.isEmpty();
        boolean hasNumber = phoneNumber != null && !phoneNumber.isEmpty();
        Person personToCall = null;
        //noinspection ConstantConditions
        ArrayList<String> allWords = new ArrayList<>(Arrays.stream(message.split(" ")).collect(Collectors.toList()));
        for (Person personFromList : list.getList()) {
            if (hasPerson) {
                if (personFromList.getName().equalsIgnoreCase(person)) {
                    personToCall = personFromList;
                    break;
                }
            } else if (hasNumber) {
                if (personFromList.getPhoneNumber().equalsIgnoreCase(phoneNumber)) {
                    personToCall = personFromList;
                    break;
                }
            }
            ArrayList<String> matches = new ArrayList<>(allWords.stream().filter((String word) -> word.equalsIgnoreCase(personFromList.getName())).collect(Collectors.toList()));
            if (matches.size() > 0) {
                if (matches.size() > 1) {
                    log("MailingListUtil->run", "matches contains more than 1 person.");
                } else {
                    personToCall = personFromList;
                    break;
                }
            }
        }
        success(personToCall);
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setOwnerCase(Case ownerCase) {
        this.ownerCase = ownerCase;
    }

    public void shouldSkip(boolean shouldSkip) {
        this.shouldSkip = shouldSkip;
    }
}
