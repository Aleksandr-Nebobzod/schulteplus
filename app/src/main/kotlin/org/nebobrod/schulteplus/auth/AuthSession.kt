package org.nebobrod.schulteplus.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import org.nebobrod.schulteplus.ui.MainActivity
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.Utils
import org.nebobrod.schulteplus.data.AdminNote
import org.nebobrod.schulteplus.data.DataRepos
import org.nebobrod.schulteplus.data.UserHelper
import java.util.Objects

/**
 * Success-цепочка авторизации (B2): сверка/создание UserHelper и переход в MainActivity.
 * Вынесена из LoginActivity/SignupActivity — переиспользуется Compose-экранами.
 */
object AuthSession {

    /** Java-совместимый колбэк результата (SAM). */
    fun interface UserHelperCallback {
        fun onComplete(userHelper: UserHelper?)
    }

    /** Переход в MainActivity с UserHelper (null — демо-режим). */
    @JvmStatic
    fun runMainActivity(context: Activity, user: UserHelper?) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("user", user)
        context.startActivity(intent)
        context.finish()
    }

    /**
     * Сверка учётной записи в репозиториях (getLatestUserHelper) и колбэк результата.
     * Нет записи нигде → создаётся новый UserHelper + AdminNote "LogIn with new device".
     *
     * @param fallbackName имя для нового пользователя ("new" при логине, displayName при Google-синапе)
     */
    @JvmStatic
    fun loginWithUpdate(
        context: Activity,
        fbu: FirebaseUser,
        password: String,
        fallbackName: String,
        onSuccess: UserHelperCallback,
        onError: Runnable
    ) {
        val uid = Objects.requireNonNull(fbu).uid
        val repos = DataRepos<UserHelper>(UserHelper::class.java)
        repos.getLatestUserHelper(Utils.intStringHash(uid)).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess.onComplete(task.result)
            } else {
                if (task.exception?.cause is RuntimeException) {
                    // No actual user record in any repository!
                    Toast.makeText(context, R.string.msg_user_data_renewed, Toast.LENGTH_LONG).show()
                    val userHelper = UserHelper(
                        fbu.uid, fbu.email, fallbackName, password,
                        Utils.getDevId(), Utils.generateUak(), fbu.isEmailVerified
                    )
                    // Make Note about a new device LogIn
                    DataRepos<AdminNote>(AdminNote::class.java).create(
                        AdminNote(
                            Utils.generateUuidInt(), userHelper.uak, userHelper.uid,
                            "LogIn with new device", "Android: " + Utils.currentOsVersion(), "",
                            userHelper.timeStamp, Utils.getVersionCode(), 0, 0, userHelper.timeStamp
                        )
                    )
                    repos.create(userHelper).addOnCompleteListener { onSuccess.onComplete(userHelper) }
                } else {
                    Toast.makeText(context, R.string.err_unknown, Toast.LENGTH_SHORT).show()
                    onError.run()
                }
            }
        }
    }

    /** Регистрация нового пользователя: письмо верификации, UserHelper, AdminNote "SignUp". */
    @JvmStatic
    fun createUserHelper(
        context: Activity,
        fbUser: FirebaseUser,
        name: String,
        email: String,
        password: String
    ): UserHelper {
        val resMessage = arrayOf(name + " " + context.getString(R.string.msg_user_signed_up))
        fbUser.sendEmailVerification().addOnCompleteListener { task ->
            resMessage[0] += " " + context.getString(
                if (task.isSuccessful) R.string.msg_user_verif_sent else R.string.msg_user_verif_not_sent
            )
        }

        // Create the repositories copy of the new UserHelper
        val userHelper = UserHelper(
            fbUser.uid, email, name, password,
            Utils.getDevId(), Utils.generateUak(), false
        )
        DataRepos<UserHelper>(UserHelper::class.java).create(userHelper)
        Toast.makeText(context, resMessage[0], Toast.LENGTH_SHORT).show()

        // registration record
        DataRepos<AdminNote>(AdminNote::class.java).create(
            AdminNote(
                Utils.generateUuidInt(), userHelper.uak, userHelper.uid,
                "SignUp", "Android: " + Utils.currentOsVersion(), "",
                userHelper.timeStamp, Utils.getVersionCode(), 0, 0, userHelper.timeStamp
            )
        )
        return userHelper
    }
}
