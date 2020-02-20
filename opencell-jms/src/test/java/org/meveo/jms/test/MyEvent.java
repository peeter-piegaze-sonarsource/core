package org.meveo.jms.test;

import java.util.UUID;

public class MyEvent {
	private String uid;
	private String firstName;
	private String lastName;
	
	public MyEvent() {
		uid = UUID.randomUUID().toString();
	}

	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
