/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data.fbservices;

/*

import android.text.Html;
import android.text.Spanned;

import static org.nebobrod.schulteplus.Utils.*;
import org.nebobrod.schulteplus.Utils;

import java.util.Locale;

public class AchievementsHelper {
	String uid;
	String name;
	long timeStamp;
	String dateTime;
	String recordText;
	String recordValue;
	String specialMark; // May be i.e.: 1st achievement of day, one done 3 at once, self-record
	// (if I got today most point than the best day before, or for week), duel's win, etc...

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
// first try		return Html.fromHtml("| \t" + this.getSpecialMark() + "\t| " + Utils.timeStampFormattedLocal(this.getTimeStamp()) + " | " + iHtml(this.getName()) + pHtml()
//				+ "|\t." + "\t| " + bHtml(this.getRecordValue()) + "\t " + this.getRecordText() + "|");


		return Html.fromHtml( Utils.timeStampLocal(this.getTimeStamp())
				+ iHtml(String.format(Locale.ENGLISH, " %8s", this.getName())) // + pHtml()
				+ bHtml(String.format(Locale.ENGLISH, "=%6s", this.getRecordValue())) + String.format(Locale.ENGLISH, " %-6s ", this.getRecordText())  + String.format(Locale.ENGLISH, "|%3s|", this.getSpecialMark()));
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
*/
