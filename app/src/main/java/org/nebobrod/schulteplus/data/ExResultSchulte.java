/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

public class ExResultSchulte  extends ExResult {
	private static final String TAG = "ExResultBasics";

	/* numValue is used as number of milliseconds, spent */
//	private int turns;
//	int turnsMissed;
//	float average;
//	float rmsd; // Root-mean-square deviation as a sign of stability & rhythm in exercises

	public ExResultSchulte(long time, int levelOfEmotion, int levelOfEnergy, String comment, int turns, int turnsMissed, float average, float rmsd) {
		super(time, levelOfEmotion, levelOfEnergy, comment);
		this.setTurns(turns);
		this.setTurnsMissed(turnsMissed);
		this.setAverage(average);
		this.setRmsd(rmsd);
	}
}
