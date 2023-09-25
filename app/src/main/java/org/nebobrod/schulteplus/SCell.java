package org.nebobrod.schulteplus;

import androidx.annotation.NonNull;

public class SCell {
	private int x,y, value;
	private boolean isPassed = false;
	private double chance = 0.5;

	public SCell(int x, int y, int value) {
		this.x = x;
		this.y = y;
		this.value = value;
	}

	@NonNull
	@Override
	public String toString() {
		return "SCell [x:" + x + ", y:" + y +", value:" + value + ", chance:" + chance + ", passed:" + isPassed + "] \n";
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

	public boolean isPassed() {
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
	}
}
