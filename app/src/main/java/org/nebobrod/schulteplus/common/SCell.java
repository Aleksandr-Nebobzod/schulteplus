/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;



import androidx.annotation.NonNull;

public class SCell {
	private static final String TAG = "SCell";


	/** It's a main sequence (no colored, to wit: the view and its content defined in STable ) */
	private int	value;
	/** x,y are a bit extra here 'cos they are recalculated each shuffle() */
	private int x,y;

/*	private byte sequence = SEQ1_SINGLE; // by default it's for 5x5 ex.
	private boolean isPassed = false; // set "true" for cell's been out of the game
	private double chance = 0.5;*/

	public SCell(int x, int y, int value) {
		this.x = x;
		this.y = y;
		this.value = value;
	}

	@NonNull
	@Override
	public String toString() {
		return "SCell [x:" + x + ", y:" + y +", value:" + value + ", chance:" + "=chance" + ", passed:" + "=isPassed" + "] \n";
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

/*	public boolean isPassed() {
		return isPassed;
	}

	public void setPassed(boolean passed) {
		isPassed = passed;
	}

	public double getChance() {
		return chance;
	}

	public void setChance(double chance) {
		this.chance = chance;
	}*/
}
