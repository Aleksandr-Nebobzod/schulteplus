package org.nebobrod.schulteplus.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.Utils
import org.nebobrod.schulteplus.data.AdminNote
import org.nebobrod.schulteplus.data.DataOrmRepo
import org.nebobrod.schulteplus.data.DataRepos
import org.nebobrod.schulteplus.data.DataRepository
import java.util.Locale

/**
 * Фоновые проверки после входа в Main (D-17): версия приложения по AdminNote и сеть.
 * Порт checkApp/checkNetwork из SplashViewModel (удалён в SP-03 Inc 1); стартовый
 * сплэш больше их не ждёт.
 */
object StartupChecks {

    @JvmStatic
    fun run(context: Context) {
        checkAppVersion(context)
        checkNetwork(context)
    }

    private fun checkAppVersion(context: Context) {
        DataOrmRepo<AdminNote>(AdminNote::class.java)
            .getListByField("uak", DataRepository.WhereCond.EQ, "0")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = task.result
                    var verDeprecated = 0
                    var verDeprecating = 0
                    var verAppLatest = 0
                    for (note in list) {
                        verDeprecated = note.verDeprecated
                        verDeprecating = note.verDeprecating
                        verAppLatest = note.verAppLatest
                        if (0 != verDeprecated * verDeprecating * verAppLatest) break
                    }
                    val message = when {
                        Utils.getVersionCode() <= verDeprecated ->
                            String.format(Locale.US, context.getString(R.string.msg_app_deprecated), verDeprecated, verAppLatest)
                        Utils.getVersionCode() <= verDeprecating ->
                            String.format(Locale.US, context.getString(R.string.msg_app_deprecating), verDeprecated, verAppLatest)
                        else -> null
                    }
                    if (message != null) showAppNotice(context, message, verDeprecated > 0 && Utils.getVersionCode() <= verDeprecated)

                    // Обновить AdminNotes (при сбое — полный fetch)
                    val latestLocalTS = list.firstOrNull()?.timeStamp ?: 0L
                    DataRepos<AdminNote>(AdminNote::class.java).fetchAdminNotes(
                        if (list.isNotEmpty()) latestLocalTS else 0L)
                } else {
                    DataRepos<AdminNote>(AdminNote::class.java).fetchAdminNotes(0L)
                }
            }
    }

    /** ERROR (версия устарела) — диалог с выходом; WARN — тост. */
    private fun showAppNotice(context: Context, message: String, deprecated: Boolean) {
        if (deprecated) {
            AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.lbl_go_on) { _, _ ->
                    (context as? Activity)?.finishAffinity()
                }
                .show()
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun checkNetwork(context: Context) {
        NetworkConnectivity(AppExecutors(), context).checkInternetConnection(
            { connected ->
                if (!connected) {
                    Toast.makeText(context, R.string.msg_user_network_failed, Toast.LENGTH_LONG).show()
                }
            },
            "http://attplus.in/schulte/ru/attention_schulte_plus_info_ru.html"
        )
    }
}
