/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.iHtml;
import static org.nebobrod.schulteplus.Utils.pHtml;

import android.database.SQLException;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.STable;
import org.nebobrod.schulteplus.Utils;

import java.io.Serializable;

/**
 * Achievement information object saved to the local database through ormlite.
 *
 * @author nebobzod
 */
@DatabaseTable
public class Achievement implements Serializable {

	private static final long serialVersionUID = -7874823823497497001L;
	public static final String DATE_FIELD_NAME = "dateTime";

	static Achievement achievement;

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String uid;

	@DatabaseField
	private String name;

	@DatabaseField
	private long timeStamp;

	@DatabaseField
	private String dateTime;

	@DatabaseField
	private String recordText;

	@DatabaseField
	private String recordValue;

	@DatabaseField
	private String specialMark; // May be i.e.: 1st achievement of day, one done 3 at once, selfrecords
	// (if I got today most point than the best day before, or for week), duels wins, etc...

	public Integer getId() {
		return id;
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

	public String getRecordText() {return recordText;}

	public void setRecordText(String recordText) {this.recordText = recordText;}

	public String getRecordValue() {return recordValue;}

	public void setRecordValue(String recordValue) {this.recordValue = recordValue;}

	public String getSpecialMark() {return specialMark;}

	public void setSpecialMark(String specialMark) {this.specialMark = specialMark;}

	@Override
	public String toString() {
		return name == null ? "<None>" : name + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|";
	}
	///////////////////////////////////////////////////////////////
	public Spanned toSpanned() {
		return Html.fromHtml("| \t" + this.getSpecialMark() + "\t| " + Utils.timeStampFormattedLocal(this.getTimeStamp()) + " | " + iHtml(this.getName()) + pHtml()
				+ "|\t." + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|");
	}

	public  Achievement setAchievement(String uid, String name, long timeStamp, String dateTime, String recordText, String recordValue, String specialMark){
		this.uid = uid;
		this.name = name;
		this.timeStamp = timeStamp;
		this.dateTime = dateTime;
		this.recordText = recordText;
		this.recordValue = recordValue;
		this.specialMark = specialMark;

		return this;


	}



}
