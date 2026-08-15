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

    /**
     * Обмен ID-токена Google на сессию Firebase. UI-флоу Google (ActivityResultLauncher)
     * остаётся на стороне клиента; сюда передаётся уже полученный idToken.
     */
    suspend fun signInWithGoogleIdToken(idToken: String): Boolean

    suspend fun signOut()
}
