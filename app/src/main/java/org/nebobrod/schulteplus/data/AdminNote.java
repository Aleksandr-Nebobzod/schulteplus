/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import com.google.firebase.firestore.Exclude;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Data-class keeps notes from
 */
@DatabaseTable(tableName = "adminNote")
public class AdminNote implements Serializable, Identifiable<String>, Cloneable {

	private static final String TAG = "AdminNote";

	private static final long serialVersionUID = -7874823823497497005L; // after Turn

	@DatabaseField (id = true)
	private Integer id;

	@DatabaseField
	private String uak; 			// "0" means Note from the Server

	@DatabaseField
	private String uidAddress; 		// null means "all" or addressed uid

	@DatabaseField
	private String title;

	@DatabaseField
	private String message;

	@DatabaseField
	private String appendix;

	@DatabaseField
	private long timeStamp;

	@DatabaseField
	private int verAppLatest;

	@DatabaseField
	private int verDeprecating;

	@DatabaseField
	private int verDeprecated;

	@DatabaseField
	private long timeStampConfirmed; 	// the moment when the user confirmed the Note


	public AdminNote() {}

	public AdminNote(Integer id, String uak, String uidAddress, String title, String message, String appendix, long timeStamp, int verAppLatest, int verDeprecating, int verDeprecated, long timeStampConfirmed) {
		this.id = id;
		this.uak = uak;
		this.uidAddress = uidAddress;
		this.title = title;
		this.message = message;
		this.appendix = appendix;
		this.timeStamp = timeStamp;
		this.verAppLatest = verAppLatest;
		this.verDeprecating = verDeprecating;
		this.verDeprecated = verDeprecated;
		this.timeStampConfirmed = timeStampConfirmed;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUak() {
		return uak;
	}

	public void setUak(String uak) {
		this.uak = uak;
	}

	public String getUidAddress() {
		return uidAddress == null ? "" : uidAddress;
	}

	public void setUidAddress(String uidAddress) {
		this.uidAddress = uidAddress;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAppendix() {
		return appendix;
	}

	public void setAppendix(String appendix) {
		this.appendix = appendix;
	}

	public int getVerAppLatest() {
		return verAppLatest;
	}

	public void setVerAppLatest(int verAppLatest) {
		this.verAppLatest = verAppLatest;
	}

	public int getVerDeprecating() {
		return verDeprecating;
	}

	public void setVerDeprecating(int verDeprecating) {
		this.verDeprecating = verDeprecating;
	}

	public int getVerDeprecated() {
		return verDeprecated;
	}

	public void setVerDeprecated(int verDeprecated) {
		this.verDeprecated = verDeprecated;
	}

	public long getTimeStampConfirmed() {
		return timeStampConfirmed;
	}

	public void setTimeStampConfirmed(long timeStampConfirmed) {
		this.timeStampConfirmed = timeStampConfirmed;
	}

	@Override
	public AdminNote clone() {

		try {
			AdminNote cloned = (AdminNote) super.clone();

			cloned.id = this.id;
			cloned.uak = this.uak;
			cloned.uidAddress = this.uidAddress;
			cloned.title = this.title;
			cloned.message = this.message;
			cloned.appendix = this.appendix;
			cloned.timeStamp = this.timeStamp;
			cloned.verAppLatest = this.verAppLatest;
			cloned.verDeprecating = this.verDeprecating;
			cloned.verDeprecated = this.verDeprecated;
			cloned.timeStampConfirmed = this.timeStampConfirmed;

			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(TAG + " clone ERROR", e);
		}
	}

	@Override
	public String toString() {
		return "AdminNote{" +
				"id=" + id +
				", uak='" + uak + '\'' +
				", uidAddress='" + uidAddress + '\'' +
				", title='" + title + '\'' +
				", message='" + message + '\'' +
				", appendix='" + appendix + '\'' +
				", timeStamp=" + timeStamp +
				", verAppLatest=" + verAppLatest +
				", verDeprecating=" + verDeprecating +
				", verDeprecated=" + verDeprecated +
				", timeStampConfirmed=" + timeStampConfirmed +
				'}';
	}

	@Exclude
	@Override
	public String getEntityKey() {
		return getUak() + "." + String.valueOf(id);
	}
}
