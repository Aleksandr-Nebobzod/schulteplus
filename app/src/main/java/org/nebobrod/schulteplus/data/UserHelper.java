/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.intStringHash;
import static org.nebobrod.schulteplus.Utils.timeStampU;
import static org.nebobrod.schulteplus.Utils.timeStampFormattedLocal;

import com.google.firebase.firestore.Exclude;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * UserHelper data object represents user state.
 * Saved to the local database through ormlite and in central repository as well.
 *
 * @author nebobzod
 */
@DatabaseTable(tableName = "userhelper")
public class UserHelper implements Serializable, Identifiable<String> {
	private static final String TAG = "UserHelper";

	private static final long serialVersionUID = -7874823823497497003L; // after ExResult
	public static final String DATE_FIELD_NAME = "dateCreated";

	@DatabaseField(id = true)
	private int id;
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
	/** uak (user app key) -- value which is unique for 1 active user record on the device
	 * (after delete user it is inactive, after the same user sign-on the same device this value regenerated)
	 * (the only 1 uak is possible for 1 uid in the Table) */
	@DatabaseField (canBeNull = false)
	private String uak;
	@DatabaseField
	private boolean verified = false;
	@DatabaseField
	private int psyCoins;
	@DatabaseField
	private int hours;
	@DatabaseField
	private int level;
	@DatabaseField
	private long timeStamp;
	@DatabaseField
	private String dateCreated;
	@DatabaseField
	private String dateChanged;

	public UserHelper() {	}

	public UserHelper(String uid, String email, String name, String password, String deviceId, String uak, boolean verified) {
		this.id = intStringHash(uid);
		this.uid = uid;
		this.name = name;
		this.email = email;
		this.password = String.valueOf(password.hashCode()+password.hashCode());
		this.deviceId = deviceId;
		this.uak = uak;
		this.verified = verified;
		this.psyCoins = 0;
		this.hours = 0;
		this.level = 1;
		this.timeStamp = timeStampU();

		this.dateCreated = timeStampFormattedLocal(this.timeStamp);
		this.dateChanged = this.dateCreated;
	}

	/** Updates all fields except needed at creation */
	public void set(String email, String name, String password, boolean verified, int psyCoins, int hours, int level) {

		this.name = name;
		this.email = email;
		this.password = String.valueOf(password.hashCode()+password.hashCode());
		this.verified = verified;
		this.psyCoins = psyCoins;
		this.hours = hours;
		this.level = level;
		this.timeStamp = timeStampU();
		this.dateChanged = timeStampFormattedLocal(this.timeStamp);
	}
	/** Updates fields except needed at creation */
	public void setStatus(int psyCoins, int hours, int level, long timeStamp) {

		this.psyCoins = psyCoins;
		this.hours = hours;
		this.level = level;
		this.timeStamp = timeStamp;
		this.dateChanged = timeStampFormattedLocal(this.timeStamp);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUid() { return uid; }

	public void setUid(String uid) { this.uid = uid; }

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

	/**
	 *
	 * @param password not the password indeed but hash code
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPasswordMatches (String password)	{
		return (this.password.equals(String.valueOf(password.hashCode()+password.hashCode())));
	}

	public String getDeviceId() {		return deviceId;	}

	public void setDeviceId(String deviceId) {		this.deviceId = deviceId;	}

	public String getUak() {
		return uak;
	}

	public void setUak(String uak) {
		this.uak = uak;
	}

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

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString()
	{
		if (this == null) return null;

		return "UserHelper{at: " + timeStampFormattedLocal(timeStampU()) + '\n' + '\'' +
				" id='" + id + '\'' +
				", uid='" + uid + '\'' +
				", email='" + email + '\'' +
				", name='" + name + '\'' +
				", password='" + password + '\'' +
				", deviceId='" + deviceId + '\'' +
				", uak='" + uak + '\'' +
				", verified=" + verified +
				", dateCreated='" + dateCreated + '\'' +
				", dateChanged='" + dateChanged + '\'' +
				'}';
	}

	@Exclude
	@Override
	public String getEntityKey() {
		return getUak() + "." + String.valueOf(id);
	}
}

// TODOne: 15.04.2024 got rid from Parcelable (anyway this object is going to be ORMed so it should be Serializable)

