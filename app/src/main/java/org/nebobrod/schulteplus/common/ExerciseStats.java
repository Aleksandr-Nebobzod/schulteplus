/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

/**
 * Статистика упражнения, вычисляемая движком (STable) и передаваемая в Result
 * полиморфно (этап 1.3 развязки ядра) — вместо кастов к подклассам ExResult.
 */
public class ExerciseStats {

	private final long timeStamp;
	private final long numValue;			// общее время упражнения, ms
	private final int turns;
	private final int turnsMissed;
	private final float average;
	private final float rmsd;
	private final int levelOfEmotion;
	private final int levelOfEnergy;
	private final String note;

	public ExerciseStats(long timeStamp, long numValue, int turns, int turnsMissed,
			float average, float rmsd, int levelOfEmotion, int levelOfEnergy, String note) {
		this.timeStamp = timeStamp;
		this.numValue = numValue;
		this.turns = turns;
		this.turnsMissed = turnsMissed;
		this.average = average;
		this.rmsd = rmsd;
		this.levelOfEmotion = levelOfEmotion;
		this.levelOfEnergy = levelOfEnergy;
		this.note = note;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public long getNumValue() {
		return numValue;
	}

	public int getTurns() {
		return turns;
	}

	public int getTurnsMissed() {
		return turnsMissed;
	}

	public float getAverage() {
		return average;
	}

	public float getRmsd() {
		return rmsd;
	}

	public int getLevelOfEmotion() {
		return levelOfEmotion;
	}

	public int getLevelOfEnergy() {
		return levelOfEnergy;
	}

	public String getNote() {
		return note;
	}
}
