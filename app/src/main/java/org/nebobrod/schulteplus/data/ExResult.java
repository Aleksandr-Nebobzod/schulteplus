/*
 * Copyright (c) 2024. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.nebobrod.schulteplus.data;

import static org.nebobrod.schulteplus.Utils.timeStamp;
import static org.nebobrod.schulteplus.Utils.timeStampFormattedLocal;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.Utils;

import java.io.Serializable;

/**
 * Uniform class for Exercise Results of:
 * @param <T> Basics or Schulte or other types of exercise
 */
public abstract class ExResult<T> implements Serializable {
	public static final String TAG = "ExResult";

	protected long id; // transactID()
	protected String uid;
	protected String name; // UserName is "" for local data
	protected long timeStamp;
	protected String dateTime;
	protected String exType;
	protected String exDescription; //description of Ex, set of settings
	protected long numValue;

	protected String comment;

	public ExResult(String exType, String exDescription, long numValue, String comment) {
		// auto-defined fields
		this.id = Utils.transactID();
		this.uid = ExerciseRunner.getUserHelper().getUid();
		this.name = ExerciseRunner.getUserHelper().getName();
		this.timeStamp = timeStamp();
		this.dateTime = timeStampFormattedLocal(this.timeStamp);
		this.exType = ExerciseRunner.getExType();
		// external-defined fields
		this.exDescription = exDescription;
		this.numValue = numValue;
		this.comment = comment;
	}

	public abstract T getResult();
	public abstract void setResult(T result);
	/** Puts into a DataRepository */
	public abstract void putResult(T result);
}
