package org.nebobrod.schulteplus.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Быстрая локальная проверка сети (SP-03 Inc 6) — для форм входа/регистрации.
 * Только API 23+ (minSdk 26). НЕ заменяет ping-проверку StartupChecks: здесь
 * проверяется наличие интернет-сети, а не реальная доступность сервера.
 */
object AppNetwork {

	/** Есть ли активная сеть с интернет-капабилити (по ConnectivityManager). */
	fun isConnected(context: Context): Boolean {
		val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val network = cm.activeNetwork ?: return false
		val capabilities = cm.getNetworkCapabilities(network) ?: return false
		return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
	}
}
