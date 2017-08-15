package com.bikebeacon.cloudant;

public abstract class CloudantElement {

	private String id;
	private String rev;

	public CloudantElement(){}
	
	public CloudantElement(String _id, String _rev) {
		id = _id;
		rev = _rev;
	}

	public String getID() {
		return id;
	}

	public String getRev() {
		return rev;
	}
	
	public void setID(String _id) {
		id = _id;
	}
	
	public void setRev(String _rev) {
		rev = _rev;
	}
}
