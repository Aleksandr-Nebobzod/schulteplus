package org.nebobrod.schulteplus.common

/**
 * Единый интерфейс авторизации (этап 2; основа B2 — миграция Splash/Login/Signup).
 * Реализации: Android — Firebase Auth (в app), iOS — (B4).
 */
interface AuthService {

    /** Текущий uid пользователя, или null если не вошёл */
    val currentUid: String?

    suspend fun signInEmail(email: String, password: String): Boolean

    suspend fun signUpEmail(email: String, password: String, name: String): Boolean

    suspend fun signInGoogle(): Boolean

    suspend fun signOut()
}
