/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

public class ExResultSchulte  extends ExResult<ExResultSchulte> {
	private static final String TAG = "ExResultBasics";

	/* numValue is used as number of milliseconds, spent */
	int turns;
	int turnsMissed;
	float average;
	float rmsd; // Root-mean-square deviation as a sign of stability & rhythm in exercises

	public ExResultSchulte(String exType, String exDescription, long time, String comment, int turns, int turnsMissed, float average, float rmsd) {
		super(exType, exDescription, time, comment);
		this.turns = turns;
		this.turnsMissed = turnsMissed;
		this.average = average;
		this.rmsd = rmsd;
	}

	@Override
	public ExResultSchulte getResult() {
		return null;
	}

	@Override
	public void setResult(ExResultSchulte result) {
		this.exDescription = result.exDescription;
		this.numValue = result.numValue;
		this.turns = result.turns;
		this.turnsMissed = result.turnsMissed;
		this.average = result.average;
		this.rmsd = result.rmsd;
		this.comment = result.comment;
	}
}
