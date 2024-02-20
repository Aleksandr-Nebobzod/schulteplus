/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

public class ExResultBasics extends ExResult<ExResultBasics> {
	private static final String TAG = "ExResultBasics";
	/** No fields yet only numValue & comment are used */

	public ExResultBasics(String exType, String exDescription, long numValue, String comment) {
		super(exType, exDescription, numValue, comment);
	}

	@Override
	public ExResultBasics getResult() {
		return this;
	}

	@Override
	// Taking updated values info this object
	public void setResult(ExResultBasics result) {
		this.exDescription = result.exDescription;
		this.numValue = result.numValue;
		this.comment = result.comment;
	}

	@Override
	// Putting data into Repositories
	public void putResult(ExResultBasics result) {
		понял -- объекта нет!
		DataRepositories.exResultPut(result);
	}
}
