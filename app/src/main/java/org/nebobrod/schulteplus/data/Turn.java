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
 * Data-class keeps click what user sends as a turn
 */
@DatabaseTable(tableName = "turn")
public class Turn implements Serializable, Identifiable<String> {

	private static final String TAG = "Turn";

	private static final long serialVersionUID = -7874823823497497004L; // after UserHelper

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField
	private String uak;

	@DatabaseField(canBeNull = true, foreign = true)
	private ExResult exResult;

	private Integer parentId;

	@DatabaseField
	private long timeStamp;

	@DatabaseField
	private long time;					// num of nanoseconds since previous turn

	@DatabaseField
	private int expected;

	@DatabaseField
	private int x;

	@DatabaseField
	private int y;

	@DatabaseField
	private int position;

	@DatabaseField
	private boolean isCorrect;

//	boolean isHinted; maybetodo for future and quadrant num


	public Turn() {}

	public Turn(ExResult exResult, long timeStamp, long time, int expected, int x, int y, int position, boolean isCorrect) {
		this.exResult = exResult;
		parentId = exResult.getId();
		uak = exResult.getUak();
		this.timeStamp = timeStamp;
		this.time = time;
		this.expected = expected;
		this.x = x;
		this.y = y;
		this.position = position;
		this.isCorrect = isCorrect;
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

	@Exclude
	public ExResult getExResult() {
		return exResult;
	}

	public void setExResult(ExResult exResult) {
		this.exResult = exResult;
		this.parentId = exResult.getId();
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getExpected() {
		return expected;
	}

	public void setExpected(int expected) {
		this.expected = expected;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean correct) {
		isCorrect = correct;
	}

	@Override
	public String toString() {
		return "\nTurn{" +
				"stamp=" + timeStamp +
				", time=" + time +
				", exp=" + expected +
				", x=" + x +
				", y=" + y +
				", pos=" + position +
				", isCorrect=" + isCorrect +
				", id=" + id +
				", exResultId=" + (exResult == null ? "no parent" : exResult.getId()) +
				'}';
	}

	@Exclude
	@Override
	public String getEntityKey() {
		return getUak() + "." + String.valueOf(id);
	}
}
