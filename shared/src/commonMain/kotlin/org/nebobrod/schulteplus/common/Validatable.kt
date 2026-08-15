package org.nebobrod.schulteplus.common

/**
 * Result-validation contract extracted from Exercise (B1) so shared/commonMain
 * does not depend on ExResult (which stays in app until B2).
 */
interface Validatable {
	fun isValid(): Boolean
	fun setValid(valid: Boolean)
}
