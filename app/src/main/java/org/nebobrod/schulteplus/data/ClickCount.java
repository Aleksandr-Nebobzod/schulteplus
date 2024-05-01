/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Counter information object saved to the database.
 *
 * @author kevingalligan
 */
@DatabaseTable
public class ClickCount implements Serializable {

	private static final long serialVersionUID = -6582623980712135028L;

	public static final String DATE_FIELD_NAME = "lastClickDate";

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField(columnName = DATE_FIELD_NAME)
	private Date lastClickDate;

	@DatabaseField(index = true)
	private String name;

	@DatabaseField
	private String description;

	@DatabaseField
	private int value;

	@DatabaseField(canBeNull = true, foreign = true)
	private ClickGroup group;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ClickGroup getGroup() {
		return group;
	}

	public void setGroup(ClickGroup group) {
		this.group = group;
	}

	public Date getLastClickDate() {
		return lastClickDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * This updates the value and adjusts the date.
	 */
	public void changeValue(int value) {
		this.value = value;
		this.lastClickDate = new Date();
	}

	@Override
	public String toString() {
		return name + " " + value;
	}
}