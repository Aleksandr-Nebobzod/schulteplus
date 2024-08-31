/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.iHtml;
import static org.nebobrod.schulteplus.Utils.pHtml;

import org.nebobrod.schulteplus.Utils;

import android.text.Html;
import android.text.Spanned;

import com.google.firebase.firestore.Exclude;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


import java.io.Serializable;

/**
 * Achievement data object saved to the local database through ormlite.
 *
 * @author nebobzod
 */
@DatabaseTable(tableName = "achievement")
public class Achievement implements Serializable, Identifiable<String> {
	private static final String TAG = "Achievement";

	private static final long serialVersionUID = -7874823823497497001L;
	public static final String UID_FIELD_NAME = "uid";
	public static final String UAK_FIELD_NAME = "uak";
	public static final String TIMESTAMP_FIELD_NAME = "timeStamp";
	public static final String DATE_FIELD_NAME = "dateTime";


	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String uak;

	@DatabaseField
	private String uid;

	@DatabaseField
	private String name;

	@DatabaseField
	private long timeStamp;

	@DatabaseField
	private String dateTime;

	@DatabaseField
	private String exType;

	@DatabaseField
	private String recordText;

	@DatabaseField
	private String recordValue;

	/**
	 * for example: 1st achievement of day, one done 3 at once, self-records
	 * (if I got today most point than the best day before, or for week), duels wins, etc...
	 */
	@DatabaseField
	private String specialMark;

	@Exclude
	private String layoutFlag = "";


	public Integer getId() {
		return id;
	}

	public String getUak() {
		return uak;
	}

	public void setUak(String uak) {
		this.uak = uak;
	}

	public String getUid() {return uid;}

	public void setUid(String uid) {this.uid = uid;}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimeStamp() {return timeStamp;}

	public void setTimeStamp(long timeStamp) {this.timeStamp = timeStamp;}

	public String getDateTime() {return dateTime;}

	public void setDateTime(String dateTime) {this.dateTime = dateTime;}

	public String getExType() {
		return exType;
	}

	public void setExType(String exType) {
		this.exType = exType;
	}

	public String getRecordText() {return recordText;}

	public void setRecordText(String recordText) {this.recordText = recordText;}

	public String getRecordValue() {return recordValue;}

	public void setRecordValue(String recordValue) {this.recordValue = recordValue;}

	public String getSpecialMark() {return specialMark;}

	public void setSpecialMark(String specialMark) {this.specialMark = specialMark;}

	public String getLayoutFlag() {
		return layoutFlag;
	}

	public Achievement setLayoutFlag(String layoutFlag) {
		this.layoutFlag = layoutFlag;
		return this;
	}

	@Override
	public String toString() {
		return name == null ? "<None>" : name + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|";
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Tab Separated Values
	 */
	public String toTabSeparatedString() {
		return TAG +
				"\t" + id +
				"\t" + uak +
				"\t" + dateTime +
				"\t" + name +
				"\t" + recordText +
				"\t" + recordValue +
				"\t" + specialMark;
	}
	public Spanned toSpanned() {
		return Html.fromHtml("| \t" + this.getSpecialMark() + "\t| " + Utils.timeStampFormattedLocal(this.getTimeStamp()) + " | " + iHtml(this.getName()) + pHtml()
				+ "|\t." + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|");
	}

	public  Achievement set(String uid, String uak, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark){
		this.uid = uid;
		this.uak = uak;
		this.name = name;
		this.timeStamp = timeStamp;
		this.dateTime = dateTime;
		this.recordText = recordText;
		this.recordValue = recordValue;
		this.specialMark = specialMark;

		return this;
	}

	@Exclude
	@Override
	public String getEntityKey() {
		return getUak() + "." + String.valueOf(id);
	}
}
