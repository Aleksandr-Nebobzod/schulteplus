/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.data;

import com.google.firebase.firestore.Exclude;
import com.j256.ormlite.table.DatabaseTable;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

@DatabaseTable(tableName = "exresult")
public class ExResultSssr extends ExResult{
	private static final String TAG = "ExResultSssr";

	/** Additional fields [0:9] goes to lng01 as sumSssr */
	@Exclude
	private int job; 			// regular work (or learning for students)
	@Exclude
	private int physical; 		// body care
	@Exclude
	private int family; 		// close emotional relations
	@Exclude
	private int friends; 		// distant emotional relations
	@Exclude
	private int leisure; 		// rest or relaxing activities
	@Exclude
	private int chores; 		// housekeeping self view care
	@Exclude
	private int sleep; 			// full-fledged sleep and naps
	@Exclude
	private int sssr; 			//  sphere of separate self-realization (fulfillment)
	@Exclude
	private int sum; 			//  sum of 0-9 values to get ratio
	@Exclude
	private long pack; 			//  each value on its own place


	public ExResultSssr(){}

	public ExResultSssr(int job, int chores, int physical, int family, int friends, int leisure, int sleep, int sssr, int levelOfEmotion, int levelOfEnergy, String note) {
		super(0, 0, levelOfEmotion, levelOfEnergy, note);

		update(getTimeStamp(), job, chores, physical, family, friends, leisure, sleep, sssr,  levelOfEmotion, levelOfEnergy, note);
	}

	/** minimum update */
	public void update(long timeStamp, int job, int chores, int physical, int family, int friends, int leisure, int sleep, int sssr,  int levelOfEmotion, int levelOfEnergy, String note) {
		super.update(timeStamp, 0, levelOfEmotion, levelOfEnergy, note);

		this.job = job % 10;
		this.physical = physical % 10;
		this.leisure = leisure % 10;
		this.family = family % 10;
		this.friends = friends % 10;
		this.chores = chores % 10;
		this.sleep = sleep % 10;
		this.sssr = sssr % 10;

//		Aggregates
		this.sum = this.job +
				this.physical +
				this.leisure +
				this.family +
				this.friends +
				this.chores +
				this.sleep +
				this.sssr;
		this.setTurns(this.sum);
		this.pack = 1000000L * this.job +
				100000L * this.physical +
				10000L * this.family +
				1000L * this.friends +
				100L * this.chores +
				10L * this.sleep +
				this.sssr;
		this.setLng01(pack);
	}

	@Override
	public Map<String, String> toMap() {
		Map<String, String> stringMap;
		stringMap = super.toMap();

		stringMap.put(Utils.getRes().getString(R.string.lbl_sssr_job), job + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_sssr_physical), physical + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_sssr_leisure), leisure + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_sssr_family), family + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_sssr_friends), friends + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_sssr_chores), chores + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_sssr_sleep), sleep + "");
		stringMap.put(Utils.getRes().getString(R.string.lbl_sssr_main), sssr + "");

		return stringMap;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public int getPhysical() {
		return physical;
	}

	public void setPhysical(int physical) {
		this.physical = physical;
	}

	public int getFamily() {
		return family;
	}

	public void setFamily(int family) {
		this.family = family;
	}

	public int getFriends() {
		return friends;
	}

	public void setFriends(int friends) {
		this.friends = friends;
	}

	public int getLeisure() {
		return leisure;
	}

	public void setLeisure(int leisure) {
		this.leisure = leisure;
	}

	public int getChores() {
		return chores;
	}

	public void setChores(int chores) {
		this.chores = chores;
	}

	public int getSleep() {
		return sleep;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	public int getSssr() {
		return sssr;
	}

	public void setSssr(int sssr) {
		this.sssr = sssr;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public long getPack() {
		return pack;
	}

	public void setPack(long pack) {
		this.pack = pack;
	}
}
