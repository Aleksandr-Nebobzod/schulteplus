/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import org.nebobrod.schulteplus.data.ExResult;

import java.util.Random;

/** Parent for any type of exercise with minimum data set */
public abstract class Exercise<T extends ExResult> {
	long exerciseId = 0;
	long seed = 0;
	Random random = null;
	T exResult = null;
	boolean isFinished = false;


	public long getExerciseId() {
		return exerciseId;
	}

	public void setExerciseId(long exerciseId) {
		this.exerciseId = exerciseId;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public T getExResult() {
		return exResult;
	}

	public void setExResult(T exResult) {
		this.exResult = exResult;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean finished) {
		isFinished = finished;
	}

	public boolean isValid() {
		return exResult.isValid();
	}

	public void setValid(boolean valid) {
		exResult.setValid(valid);
	}

	abstract boolean validateResult();
}
