/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

public abstract class ExResult<T> {
	public static final String TAG = "ExResult";

	protected long id; // transactID()
	protected String uid;
	protected String name; // UserName is "" for local data
	protected long timeStamp;
	protected String dateTime;
	protected String exType;
	protected String text; //description of Ex, set of settings
	protected long numValue;

	protected String comment;


	public abstract T getResult();
	public abstract void setResult(T result);
}
