package com.qingguang.qingnote.ui.page.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.qingguang.qingnote.biometric.AppBioMetricManager
import com.qingguang.qingnote.biometric.BiometricAuthListener
import com.qingguang.qingnote.state.NoteState
import com.qingguang.qingnote.ui.page.LocalMemosState
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.ui.page.LocalTags
import com.qingguang.qingnote.ui.page.NoteViewModel
import com.qingguang.qingnote.ui.page.router.App
import com.qingguang.qingnote.utils.FirstTimeManager
import com.qingguang.qingnote.utils.SharedPreferencesUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firstTimeManager: FirstTimeManager

    @Inject
    lateinit var appBioMetricManager: AppBioMetricManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置全局异常捕获处理
        setGlobalExceptionHandler()

        installSplashScreen()
        enableEdgeToEdge()

        //https://github.com/android/compose-samples/issues/1256
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }

        lifecycleScope.launch {
            try {
                firstTimeManager.generateIntroduceNoteList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            handleAuthentication()
        }
    }

    private fun setGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val stackTrace = throwable.stackTraceToString()
            try {
                val dir = getExternalFilesDir(null)
                if (dir != null) {
                    val file = java.io.File(dir, "crash_log.txt")
                    file.writeText("Crash on thread ${thread.name}:\n" + stackTrace)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val intent = android.content.Intent(this, CrashActivity::class.java).apply {
                    putExtra("stack_trace", stackTrace)
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }

    // 提取公共的 setContent 逻辑
    private fun setupContent() {
        setContent {
            SettingsProvider {
                App()
            }
        }
    }

    private suspend fun handleAuthentication() {
        val useSafe = SharedPreferencesUtils.useSafe.firstOrNull() ?: false
        if (useSafe && appBioMetricManager.canAuthenticate()) {
            showBiometricPrompt {
                setupContent()
            }
        } else {
            setupContent()
        }
    }


    @Composable
    fun SettingsProvider(
        noteViewModel: NoteViewModel = hiltViewModel(),
        content: @Composable () -> Unit
    ) {
        val state: NoteState by noteViewModel.state.collectAsState()
        val tags by noteViewModel.tags.collectAsState()

        CompositionLocalProvider(
            LocalMemosViewModel provides noteViewModel,
            LocalMemosState provides state,
            LocalTags provides tags,
        ) {
            content()
        }
    }


    private fun showBiometricPrompt(success: (Boolean) -> Unit) {
        appBioMetricManager.initBiometricPrompt(activity = this, listener = object : BiometricAuthListener {
            override fun onBiometricAuthSuccess() {
                // 验证完成后显示主界面
                success(true)
            }

            override fun onUserCancelled() {
                finish()
            }

            override fun onErrorOccurred() {
                finish()
            }
        })
    }

}

