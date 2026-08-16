package org.nebobrod.schulteplus.ui.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.nebobrod.schulteplus.R
import org.nebobrod.schulteplus.data.UserHelper
import org.nebobrod.schulteplus.ui.SplashViewModel

/**
 * Сплэш на Compose (B2): переиспользует Java SplashViewModel и его 5 проверок
 * (APP/USER/NETWORK/DATA/TIME), порт хендлеров и навигации из SplashActivity.
 */
@Composable
fun SplashScreen(onLogin: () -> Unit, onMain: (UserHelper?) -> Unit) {
    val context = LocalContext.current
    val viewModel: SplashViewModel = viewModel()
    val checkResult by viewModel.getCheckResult().observeAsState()
    val userHelper by viewModel.getUserHelperLD().observeAsState()

    var userHelperState by remember { mutableStateOf<UserHelper?>(null) }
    var navigated by remember { mutableStateOf(false) }
    val checks = remember { mutableStateMapOf<SplashViewModel.CheckType, SplashViewModel.InitialCheck>() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var queue by remember { mutableStateOf<List<Pair<String, (() -> Unit)?>>>(emptyList()) }

    val colorGreen = ContextCompat.getColor(context, R.color.light_grey_A_green)
    val colorYellow = ContextCompat.getColor(context, R.color.light_grey_A_yellow)
    val colorRed = ContextCompat.getColor(context, R.color.light_grey_A_red)
    val colorGrey = ContextCompat.getColor(context, R.color.light_grey_A)

    fun navigate(goLogin: Boolean) {
        if (navigated) return
        navigated = true
        if (goLogin) onLogin() else onMain(userHelperState)
    }

    fun showQueue(after: () -> Unit) {
        scope.launch {
            for ((text, action) in queue) {
                val result = snackbarHostState.showSnackbar(
                    text, actionLabel = if (action != null) context.getString(R.string.lbl_go_on) else null)
                if (result == SnackbarResult.ActionPerformed) action?.invoke()
            }
            after()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.startSplashProcess()
    }

    LaunchedEffect(checkResult) {
        val result = checkResult ?: return@LaunchedEffect
        checks[result.type] = result
        when (result.type) {
            SplashViewModel.CheckType.APP -> when (result.result) {
                SplashViewModel.CheckResult.WARN -> queue = queue + (result.message to null)
                SplashViewModel.CheckResult.ERROR -> queue = queue + (result.message to {
                    (context as Activity).finishAffinity()
                    System.exit(0)
                })
                else -> {}
            }
            SplashViewModel.CheckType.USER -> when (result.result) {
                SplashViewModel.CheckResult.WARN -> {
                    if (userHelperState == null) {
                        viewModel.postCheckResult(SplashViewModel.InitialCheck(
                            SplashViewModel.CheckType.USER, SplashViewModel.CheckResult.ERROR, ""))
                        return@LaunchedEffect
                    }
                    val msg = userHelperState!!.name + ", " + context.getString(R.string.msg_user_unverified)
                    queue = queue + (msg to {
                        viewModel.postCheckResult(SplashViewModel.InitialCheck(
                            SplashViewModel.CheckType.USER, SplashViewModel.CheckResult.OK, "Verification informed"))
                    })
                }
                else -> {}
            }
            SplashViewModel.CheckType.TIME ->
                if (result.result == SplashViewModel.CheckResult.WARN) {
                    queue = queue + (context.getString(R.string.msg_tests_failed) to null)
                    delay(1000)
                    showQueue { navigate(userHelperState == null) }
                }
            else -> {}
        }
        // все пять проверок получены → показываем очередь и решаем навигацию
        if (SplashViewModel.CheckType.entries.all { checks.containsKey(it) }) {
            showQueue {
                val userError = checks[SplashViewModel.CheckType.USER]?.result == SplashViewModel.CheckResult.ERROR
                navigate(userError || userHelperState == null)
            }
        }
    }

    LaunchedEffect(userHelper) {
        userHelper?.let { userHelperState = it }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(context.getString(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(context.getString(R.string.app_version_full), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SplashViewModel.CheckType.entries.forEach { type ->
                    val check = checks[type]
                    val color = when (check?.result) {
                        SplashViewModel.CheckResult.OK -> colorGreen
                        SplashViewModel.CheckResult.WARN -> colorYellow
                        SplashViewModel.CheckResult.ERROR -> colorRed
                        null -> colorGrey
                    }
                    Box(Modifier.size(18.dp).background(Color(color), CircleShape))
                }
            }
            Spacer(Modifier.height(32.dp))
            Text(
                "AttPlus",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    // Little cheat-code (паритет SplashActivity.tvVendor)
                    Toast.makeText(context, "Run Demo User...", Toast.LENGTH_LONG).show()
                    scope.launch { delay(100); navigate(false) }
                }
            )
        }
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
    }
}
