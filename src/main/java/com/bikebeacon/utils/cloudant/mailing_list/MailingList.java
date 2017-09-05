package com.bikebeacon.utils.cloudant.mailing_list;

import com.bikebeacon.utils.cloudant.BaseCloudantElement;

import java.util.ArrayList;
import java.util.List;

public class MailingList extends BaseCloudantElement {

    private List<Person> list;
    private String owner;

    public MailingList() {
        list = new ArrayList<>();
    }

    @Override
    public void override(BaseCloudantElement object) {
        if (!(object instanceof MailingList))
            throw new IllegalArgumentException("MailingList->override was given BaseCloudantElement that isn't a MailingList.");
        MailingList mailingList = (MailingList) object;
        list.clear();
        list.addAll(mailingList.getList());
    }


    public List<Person> getList() {
        return list;
    }

    public void setList(List<Person> list) {
        this.list = list;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
