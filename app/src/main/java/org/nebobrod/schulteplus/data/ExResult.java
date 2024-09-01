/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.timeStampFormattedLocal;
import static org.nebobrod.schulteplus.Utils.timeStampU;
import static org.nebobrod.schulteplus.common.Const.*;

import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.common.Const;

import com.google.firebase.firestore.Exclude;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Uniform data-class for Exercise Results of any exercise
 */
@DatabaseTable(tableName = "exresult")
public class ExResult implements Serializable, Identifiable<String> {
	public static final String TAG = "ExResult";

	private static final long serialVersionUID = -7874823823497497002L; // after Achievement
	public static final String UID_FIELD_NAME = "uid";
	public static final String UAK_FIELD_NAME = "uak";
	public static final String TIMESTAMP_FIELD_NAME = "timeStamp";
	public static final String TIMESTAMP_START_FIELD_NAME = "timeStampStart";
	public static final String DATE_FIELD_NAME = "dateTime";
	public static final String EXTYPE_FIELD_NAME = "exType";

	@DatabaseField(generatedId = true)
	private Integer id; 			// transactID()

	@DatabaseField
	private String uak;				// Used as the complex key an as a filter for local user-data

	@DatabaseField
	private long seed;

	@DatabaseField
	private String uid; 			// Used as a filter for local user-data

	@DatabaseField
	private String name; 			// UserName

	@DatabaseField
	private long timeStamp;			// actual updated timeStamp

	@DatabaseField
	private String dateTime;

	@DatabaseField
	private boolean isValid = false;

	/** same as {@link Const#KEY_PRF_EX_S1}
	 * @see org.nebobrod.schulteplus.R.array#ex_type */
	@DatabaseField
	private String exType; 			// see <string-array name="ex_type"> & Const

	@DatabaseField
	private String exDescription; 	//description of Ex, set of settings

	// and result data itself:
	@DatabaseField
	private long timeStampStart = 0;	// useful for Sssr

	@DatabaseField
	private long numValue; 				// used as number of milliseconds, spent through the exercise */

	/** Calculated in {@link #calculatePsycoins()} and kept in UserHelper
	 * (means cents so they shown 1/100)
	 */
	@DatabaseField
	private int psyCoins;

	/** @see org.nebobrod.schulteplus.R.array#level_of_emotion_values */
	@DatabaseField
	private int levelOfEmotion;

	/** @see org.nebobrod.schulteplus.R.array#level_of_energy_values */
	@DatabaseField
	private int levelOfEnergy;

	@DatabaseField
	protected String note;				// user notes

	// section of Schulte-exercises data:
	@DatabaseField
	private int turns; 					// also use as number of events in Basics

	@DatabaseField
	private int turnsMissed;

	@DatabaseField
	private float average;

	@DatabaseField
	private float rmsd; 				// Root-mean-square deviation as a sign of stability & rhythm in exercises

	// section of SSSR-exercises data:
	@DatabaseField
	private long lng01;

	@DatabaseField
	private long lng02;

	@DatabaseField
	private long lng03;

	@DatabaseField
	private float flo01;

	@DatabaseField
	private float flo02;

	@DatabaseField
	private float flo03;


	// section of Other-exercises data:

	/** Non-database field for layout elements' visibility control */
	private String layoutFlag = "";


	public ExResult() {}

	public ExResult(long seed, long numValue, int levelOfEmotion, int levelOfEnergy, String note) {
//		this.id = ((Long) Utils.transactID()).intValue(); // autokey by ORMLite
		ExerciseRunner exerciseRunner = ExerciseRunner.getInstance();

		// common exercise-defined fields
		this.uak = exerciseRunner.getUserHelper().getUak();
		this.uid = exerciseRunner.getUserHelper().getUid();
		this.name = exerciseRunner.getUserHelper().getName();
		this.timeStamp = timeStampU();
		this.timeStampStart = this.timeStamp;
		this.dateTime = timeStampFormattedLocal(this.timeStamp);
		this.exType = exerciseRunner.getExType();
		this.exDescription = ""; // TODOne: 26.02.2024 gather settings & screen width in String

		// additional fields
		this.seed = seed;
		this.numValue = numValue;
		this.levelOfEmotion = levelOfEmotion;
		this.levelOfEnergy = levelOfEnergy;
		this.note = note;
	}

	public Integer getId() {
		return id;
	}

	/**
	 * For testing ONLY!
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	public String getUak() {
		return uak;
	}

	public void setUak(String uak) {
		this.uak = uak;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
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

	public String getExType() {
		return exType;
	}

	public void setExType(String exType) {
		this.exType = exType;
	}

	public String getExDescription() {
		return exDescription;
	}

	public void setExDescription(String exDescription) {
		this.exDescription = exDescription;
	}

	public long getTimeStampStart() {
		return timeStampStart;
	}

	public void setTimeStampStart(long timeStampStart) {
		this.timeStampStart = timeStampStart;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean valid) {
		isValid = valid;
	}

	public long getNumValue() {
		return numValue;
	}

	public void setNumValue(long numValue) {
		this.numValue = numValue;
	}

	public int getLevelOfEmotion() {
		return levelOfEmotion;
	}

	public void setLevelOfEmotion(int levelOfEmotion) {
		this.levelOfEmotion = levelOfEmotion;
	}

	public int getLevelOfEnergy() {
		return levelOfEnergy;
	}

	public void setLevelOfEnergy(int levelOfEnergy) {
		this.levelOfEnergy = levelOfEnergy;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getTurns() {
		return turns;
	}

	public void setTurns(int turns) {
		this.turns = turns;
	}

	public int getTurnsMissed() {
		return turnsMissed;
	}

	public void setTurnsMissed(int turnsMissed) {
		this.turnsMissed = turnsMissed;
	}

	public float getAverage() {
		return average;
	}

	public void setAverage(float average) {
		this.average = average;
	}

	public float getRmsd() {
		return rmsd;
	}

	public void setRmsd(float rmsd) {
		this.rmsd = rmsd;
	}

	public int getPsyCoins() {
		return psyCoins;
	}

	public void setPsyCoins(int psyCoins) {
		this.psyCoins = psyCoins;
	}

	public float getFlo01() {
		return flo01;
	}

	public void setFlo01(float flo01) {
		this.flo01 = flo01;
	}

	public float getFlo02() {
		return flo02;
	}

	public void setFlo02(float flo02) {
		this.flo02 = flo02;
	}

	public float getFlo03() {
		return flo03;
	}

	public void setFlo03(float flo03) {
		this.flo03 = flo03;
	}

	public long getLng01() {
		return lng01;
	}

	public void setLng01(long lng01) {
		this.lng01 = lng01;
	}

	public long getLng02() {
		return lng02;
	}

	public void setLng02(long lng02) {
		this.lng02 = lng02;
	}

	public long getLng03() {
		return lng03;
	}

	public void setLng03(long lng03) {
		this.lng03 = lng03;
	}

	/** service fields */
	public String getLayoutFlag() {
		return layoutFlag;
	}

	public ExResult setLayoutFlag(String layoutFlag) {
		this.layoutFlag = layoutFlag;
		return this;
	}


	@Override
	public String toString() {
		return "ExResult{" +
				"id=" + id +
				", dateTime='" + dateTime + '\'' +
				", exType='" + exType + '\'' +
				", numValue=" + numValue +
				", comment='" + note + '\'' +
				", turns=" + turns +
				'}';
	}

	/**
	 * Tab Separated Values
	 */
	public String toTabSeparatedString() {
		return TAG +
				"\t" + id +
				"\t" + uak +
				"\t" + dateTime +
				"\t" + exType +
				"\t" + numValue +
				"\t" + note +
				"\t" + turns;
	}

	public Map<String, String> toMap() {
		Map<String, String> stringMap = new LinkedHashMap<>();

		stringMap.put(Utils.getRes().getString(R.string.lbl_time), String.format(Locale.ENGLISH, "%.2f", (numValue /1000F)));

//		stringMap.put(Utils.getRes().getString(R.string.lbl_turns_missed), turnsMissed + "");
//		stringMap.put(Utils.getRes().getString(R.string.lbl_average), String.format(Locale.ENGLISH, "%.2f", (average /1000F)));
//		stringMap.put(Utils.getRes().getString(R.string.lbl_sd), String.format(Locale.ENGLISH, "%.2f", (rmsd /1000F)));

//		stringMap.put(Utils.getRes().getString(R.string.lbl_level_of_emotion), levelOfEmotion + "");
//		stringMap.put(Utils.getRes().getString(R.string.lbl_level_of_energy), levelOfEnergy + "");
//		stringMap.put(Utils.getRes().getString(R.string.lbl_note), note + "");
		return stringMap;
	}

	/** minimum of update */
	public void update(long timeStamp, long numValue, int levelOfEmotion, int levelOfEnergy, String note) {
		this.timeStamp = timeStamp; // Finish
		this.numValue = numValue;
		this.levelOfEmotion = levelOfEmotion;
		this.levelOfEnergy = levelOfEnergy;
		this.note = note;
	}

	@Exclude
	public int calculatePsycoins() {
		switch (this.exType) {
			// Basics is Seconds multiplied by hardness
			case KEY_PRF_EX_B0:
			case KEY_PRF_EX_B1:
				return (int) this.numValue / 1000 * 1;
			case KEY_PRF_EX_B2:
			case KEY_PRF_EX_B3:
				return (int) this.numValue / 1000 * 2;
			case KEY_PRF_EX_B4:
			case KEY_PRF_EX_B5:
			case KEY_PRF_EX_B6:
			case KEY_PRF_EX_B7:
				return (int) this.numValue / 1000 * 3;
			case KEY_PRF_EX_B8:
			case KEY_PRF_EX_B9:
			case KEY_PRF_EX_BA:
			case KEY_PRF_EX_BB:
			case KEY_PRF_EX_BC:
				return (int) this.numValue / 1000 * 4;
			case KEY_PRF_EX_BD:
			case KEY_PRF_EX_BE:
				return (int) this.numValue / 1000 * 5;

				// Schulte
			case KEY_PRF_EX_S0:
			case KEY_PRF_EX_S1:
				// number of seconds plus success turns (fine of missed turns) multiplied by hardness
				return (int) (this.numValue / 1000 + turns - turnsMissed) * 10;
			case KEY_PRF_EX_S2:
				return (int) (this.numValue / 1000 + turns - turnsMissed) * 15;
			case KEY_PRF_EX_S3:
			case KEY_PRF_EX_S4:
				return (int) (this.numValue / 1000 + turns - turnsMissed) * 20;

				// SSSR standard costs
			case KEY_PRF_EX_R1:
				return (int) (500);
			case KEY_PRF_EX_R2:
				return (int) (20);
			case KEY_PRF_EX_R3:
				return (int) (50);
			case KEY_PRF_EX_R4:
				return (int) (100);
			default:
				return (int) this.numValue / 1000;
		}
	}

	@Exclude
	@Override
	public String getEntityKey() {
		return getUak() + "." + String.valueOf(id);
	}

}
