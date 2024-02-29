/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.timeStamp;
import static org.nebobrod.schulteplus.Utils.timeStampFormattedLocal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.ExerciseRunner;

import java.io.Serializable;

/**
 * Uniform class for Exercise Results of:
 */
@DatabaseTable
public class ExResult implements Serializable {
	public static final String TAG = "ExResult";

	private static final long serialVersionUID = -7874823823497497002L;
	public static final String DATE_FIELD_NAME = "dateTime";

	@DatabaseField(generatedId = true)
	private Integer id; // transactID()
	@DatabaseField
	private String uid;
	@DatabaseField
	private String name; // UserName is "" for local data
	@DatabaseField
	private long timeStamp;
	@DatabaseField
	private String dateTime;
	/**
	 * same as {@link org.nebobrod.schulteplus.Const#KEY_PRF_EX_S1}
	 * @see org.nebobrod.schulteplus.R.array#ex_type
	 */
	@DatabaseField
	private String exType; // see <string-array name="ex_type"> & Const
	@DatabaseField
	private String exDescription; //description of Ex, set of settings

	// and result data itself:
	@DatabaseField
	private long numValue; /* used as number of milliseconds, spent through the exercise */
	/**
	 * @see org.nebobrod.schulteplus.R.array#level_of_emotion_values
	 */
	@DatabaseField
	private int levelOfEmotion;
	/**
	 * @see org.nebobrod.schulteplus.R.array#level_of_energy_values
	 */
	@DatabaseField
	private int levelOfEnergy;
	@DatabaseField
	private String comment;


	// section of Schulte-exercises data:
	@DatabaseField
	private int turns;
	@DatabaseField
	private int turnsMissed;
	@DatabaseField
	private float average;
	@DatabaseField
	private float rmsd; // Root-mean-square deviation as a sign of stability & rhythm in exercises

	public ExResult(long numValue, int levelOfEmotion, int levelOfEnergy, String comment) {
//		this.id = ((Long) Utils.transactID()).intValue(); //
		// common exercise-defined fields
		this.uid = ExerciseRunner.getUserHelper().getUid();
		this.name = ExerciseRunner.getUserHelper().getName();
		this.timeStamp = timeStamp();
		this.dateTime = timeStampFormattedLocal(this.timeStamp);
		this.exType = ExerciseRunner.getExType();
		this.exDescription = ""; // TODO: 26.02.2024 gather settings & screen width in String

		// additional fields
		this.numValue = numValue;
		this.levelOfEmotion = levelOfEmotion;
		this.levelOfEnergy = levelOfEnergy;
		this.comment = comment;
	}

	public Integer id() {
		return id;
	}
/* not required
	public ExResult<T> setId(Integer id) {
		this.id = id;
		return this;
	}*/

	public String uid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long timeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String dateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String exType() {
		return exType;
	}

	public void setExType(String exType) {
		this.exType = exType;
	}

	public String exDescription() {
		return exDescription;
	}

	public void setExDescription(String exDescription) {
		this.exDescription = exDescription;
	}

	public long numValue() {
		return numValue;
	}

	public void setNumValue(long numValue) {
		this.numValue = numValue;
	}

	public int levelOfEmotion() {
		return levelOfEmotion;
	}

	public void setLevelOfEmotion(int levelOfEmotion) {
		this.levelOfEmotion = levelOfEmotion;
	}

	public int levelOfEnergy() {
		return levelOfEnergy;
	}

	public void setLevelOfEnergy(int levelOfEnergy) {
		this.levelOfEnergy = levelOfEnergy;
	}

	public String comment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int turns() {
		return turns;
	}

	public void setTurns(int turns) {
		this.turns = turns;
	}

	public int turnsMissed() {
		return turnsMissed;
	}

	public void setTurnsMissed(int turnsMissed) {
		this.turnsMissed = turnsMissed;
	}

	public float average() {
		return average;
	}

	public void setAverage(float average) {
		this.average = average;
	}

	public float rmsd() {
		return rmsd;
	}

	public void setRmsd(float rmsd) {
		this.rmsd = rmsd;
	}
}
