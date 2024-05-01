/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.timeStampU;
import static org.nebobrod.schulteplus.Utils.timeStampFormattedLocal;

import org.nebobrod.schulteplus.data.fbservices.Identifiable;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * UserHelper data object saved to the local database through ormlite.
 *
 * @author nebobzod
 */
@DatabaseTable(tableName = "userhelper")
public class UserHelper implements Serializable, Identifiable<String> {
	private static final String TAG = "UserHelper";

	private static final long serialVersionUID = -7874823823497497003L; // after ExResult
	public static final String DATE_FIELD_NAME = "dateCreated";
	public static final String TIMESTAMP_FIELD_NAME = "tsUpdated";

	@DatabaseField
	private String uid;
	@DatabaseField
	private String name;
	@DatabaseField
	private String email;
	@DatabaseField
	private String password;
	@DatabaseField
	private String deviceId;
	@DatabaseField
	private boolean verified=false;
	@DatabaseField
	private int psyCoins;
	@DatabaseField
	private int hours;
	@DatabaseField
	private int level;
	@DatabaseField
	private long tsUpdated;
	@DatabaseField
	private String dateCreated;
	@DatabaseField
	private String dateChanged;

	public UserHelper() {	}

	public UserHelper(String uid, String email, String name, String password, String deviceId, boolean verified) {
		this.uid = uid;
		this.name = name;
		this.email = email;
		this.password = String.valueOf(password.hashCode()+password.hashCode());
		this.deviceId = deviceId;
		this.verified = verified;
		this.psyCoins = 0;
		this.hours = 0;
		this.level = 1;
		this.tsUpdated = timeStampU();

		this.dateCreated = timeStampFormattedLocal(this.tsUpdated);
		this.dateChanged = this.dateCreated;
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

	/**
	 * @return not the password indeed but hash code which is comparable by {@link UserHelper#isPasswordMatches(String)}
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = String.valueOf(password.hashCode()+password.hashCode());
	}

	public boolean isPasswordMatches (String password)	{
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

	public void setDateChanged(String dateChanged) {		this.dateChanged = dateChanged;	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public int getPsyCoins() {
		return psyCoins;
	}

	public void setPsyCoins(int psyCoins) {
		this.psyCoins = psyCoins;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getTsUpdated() {
		return tsUpdated;
	}

	public void setTsUpdated(long tsUpdated) {
		this.tsUpdated = tsUpdated;
	}

	@Override
	public String toString()
	{
		if (this == null) return null;

		return "FbUserHelper{at: " + timeStampFormattedLocal(timeStampU()) + '\n' + '\'' +
				" uid='" + uid + '\'' +
				", email='" + email + '\'' +
				", name='" + name + '\'' +
				", password='" + password + '\'' +
				", deviceId='" + deviceId + '\'' +
				", verified=" + verified +
				", dateCreated='" + dateCreated + '\'' +
				", dateChanged='" + dateChanged + '\'' +
				'}';
	}

	@Override
	public String getEntityKey() {
		return String.valueOf(uid);
	}
}

// TODOne: 15.04.2024 got rid from Parcelable (anyway this object is going to be ORMed so it should be Serializable)

