package org.nebobrod.schulteplus.common

/**
 * Migrated from Java Exercise.java (B1). Parent for any type of exercise with
 * minimum data set. `validateResult()` is public (was package-private in Java)
 * because ExerciseRunner (same package in app) calls it from a non-subclass.
 */
abstract class Exercise<T : Validatable> {
	var exerciseId: Long = 0
	var seed: Long = 0
	var timeStamp: Long = 0 // updated time timeStampU()
	var random: kotlin.random.Random? = null
	var exResult: T? = null
	var isFinished: Boolean = false
		set(value) {
			field = value
			timeStamp = timeStampU()
		}

	fun isValid(): Boolean = exResult!!.isValid()

	fun setValid(valid: Boolean) {
		exResult!!.setValid(valid)
	}

	/** True by default and might be overridden */
	fun validateResult(): Boolean {
		setValid(true)
		return true
	}
}
