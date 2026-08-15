package org.nebobrod.schulteplus.common

import java.time.Instant

actual fun timeStampU(): Long = Instant.now().epochSecond
