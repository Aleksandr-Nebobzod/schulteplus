package org.nebobrod.schulteplus.common

/**
 * Migrated from Java SCell.java (B1). Keep package org.nebobrod.schulteplus.common
 * so Java code keeps using it without import changes.
 */
class SCell(x: Int, y: Int, value: Int) {
	/** x,y are a bit extra here 'cos they are recalculated each shuffle() */
	val x: Int = x
	val y: Int = y

	/** It's a main sequence (no colored, to wit: the view and its content defined in STable ) */
	var value: Int = value

	/** text form of visible value */
	var text: String = value.toString()

	var color: Int = 0xFFFFFF

	override fun toString(): String =
		"SCell [x:$x, y:$y, value:$value, chance:=chance, passed:=isPassed] \n"
}
