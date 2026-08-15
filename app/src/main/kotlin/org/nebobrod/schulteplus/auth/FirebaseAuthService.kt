package org.nebobrod.schulteplus.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import org.nebobrod.schulteplus.common.AuthService
import kotlin.coroutines.resume

/**
 * Реализация AuthService на существующем Firebase SDK (firebase-auth 16.0.3).
 * Апгрейд отложен (ранее падал с error 240805 — см. комментарии в build.gradle);
 * Task API оборачивается в suspend через suspendCancellableCoroutine.
 */
object FirebaseAuthService : AuthService {

    private val fbAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    override val currentUid: String?
        get() = fbAuth.currentUser?.uid

    override suspend fun signInEmail(email: String, password: String): Boolean =
        awaitTask { fbAuth.signInWithEmailAndPassword(email, password) }

    override suspend fun signUpEmail(email: String, password: String, name: String): Boolean {
        if (!awaitTask { fbAuth.createUserWithEmailAndPassword(email, password) }) return false
        val user = fbAuth.currentUser ?: return false
        return awaitTask {
            user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
        }
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): Boolean =
        awaitTask { fbAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)) }

    override suspend fun signOut() {
        fbAuth.signOut()
    }

    /** Task API (без отмены) → suspend Boolean; результат только по завершении. */
    private suspend fun awaitTask(taskProvider: () -> Task<*>): Boolean =
        suspendCancellableCoroutine { cont ->
            taskProvider().addOnCompleteListener { task ->
                if (cont.isActive) cont.resume(task.isSuccessful)
            }
        }
}
