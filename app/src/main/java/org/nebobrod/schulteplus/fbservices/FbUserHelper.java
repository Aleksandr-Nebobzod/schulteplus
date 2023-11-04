/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;

import static org.nebobrod.schulteplus.Utils.timeStamp;
import static org.nebobrod.schulteplus.Utils.timeStampFormatted;

public class FbUserHelper {
	String email;
	String name;
	String password;
	String uid;
	String deviceId;
	String dateCreated;
	String dateChanged;
	boolean verified=false;

	public FbUserHelper() {	}

	public FbUserHelper(String email, String name, String password, String uid, String deviceId, boolean verified) {
		this.email = email;
		this.name = name;
		this.password = String.valueOf(password.hashCode()+password.hashCode());
		this.uid = uid;
		this.deviceId = deviceId;
		this.dateCreated = timeStampFormatted(timeStamp());
		this.dateChanged = this.dateCreated;
		this.verified = verified;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isPasswordMatches (String password)
	{
		return (this.password.equals(String.valueOf(password.hashCode()+password.hashCode())));
	}

	public String getUid() { return uid; }

	public void setUid(String uid) { this.uid = uid; }

	public String getDeviceId() {		return deviceId;	}

	public void setDeviceId(String deviceId) {		this.deviceId = deviceId;	}

	public String getDateCreated() {		return dateCreated;	}

	public void setDateCreated(String dateCreated) {		this.dateCreated = dateCreated;	}

	public String getDateChanged() {
		return dateChanged;
	}

	public void setDateChanged(String dateChanged) {
		this.dateChanged = dateChanged;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	@Override
	public String toString()
	{
		if (this == null) return null;

		return "FbUserHelper{at: " + timeStampFormatted(timeStamp()) + '\n' + '\'' +
				"email='" + email + '\'' +
				", name='" + name + '\'' +
				", password='" + password + '\'' +
				", uid='" + uid + '\'' +
				", deviceId='" + deviceId + '\'' +
				", dateCreated='" + dateCreated + '\'' +
				", dateChanged='" + dateChanged + '\'' +
				", verified=" + verified +
				'}';
	}
}

