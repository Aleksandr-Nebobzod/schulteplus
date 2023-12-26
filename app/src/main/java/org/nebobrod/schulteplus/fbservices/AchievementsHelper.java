/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.fbservices;


import android.text.Html;
import android.text.Spanned;

import static org.nebobrod.schulteplus.Utils.*;
import org.nebobrod.schulteplus.Utils;

public class AchievementsHelper {
	String uid;
	String name;
	long timeStamp;
	String dateTime;
	String recordText;
	String recordValue;
	String specialMark; // May be i.e.: 1st achievement of day, one done 3 at once, selfrecords
	// (if I got today most point than the best day before, or for week), duels wins, etc...

	public AchievementsHelper() {
	}

	public AchievementsHelper(String uid, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark) {
		this.uid = uid;
		this.name = name;
		this.timeStamp = timeStamp;
		this.dateTime = dateTime;
		this.recordText = recordText;
		this.recordValue = recordValue;
		this.specialMark = specialMark;
	}

	@Override
	public String toString() {
		return "FbAchievementsHelper{" +
				"uid='" + uid + '\'' +
				", name='" + name + '\'' +
				", timeStamp=" + timeStamp +
				", dateTime='" + dateTime + '\'' +
				", recordText='" + recordText + '\'' +
				", recordValue='" + recordValue + '\'' +
				", specialMark='" + specialMark + '\'' +
				'}';
	}

	public Spanned toSpanned() {
		return Html.fromHtml("| \t" + this.getSpecialMark() + "\t| " + Utils.timeStampFormattedLocal(this.getTimeStamp()) + " | " + iHtml(this.getName()) + pHtml()
				+ "|\t." + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|");
	}


	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getRecordText() {
		return recordText;
	}

	public void setRecordText(String recordText) {
		this.recordText = recordText;
	}

	public String getRecordValue() {
		return recordValue;
	}

	public void setRecordValue(String recordValue) {
		this.recordValue = recordValue;
	}

	public String getSpecialMark() {
		return specialMark;
	}

	public void setSpecialMark(String specialMark) {
		this.specialMark = specialMark;
	}

}
