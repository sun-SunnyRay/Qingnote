package com.qingguang.qingnote.ui.page.settings

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.qingguang.qingnote.App
import com.qingguang.qingnote.ui.page.router.debouncedPopBackStack
import com.moriafly.salt.ui.Item
import com.moriafly.salt.ui.ItemTitle
import com.moriafly.salt.ui.RoundedColumn
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.TitleBar
import com.moriafly.salt.ui.UnstableSaltApi

@OptIn(UnstableSaltApi::class)
@Composable
fun NotificationGuardPage(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 权限实时状态
    var hasNotificationPermission by remember { mutableStateOf(false) }
    var hasExactAlarmPermission by remember { mutableStateOf(false) }
    var isIgnoringBattery by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }

    // 状态检测函数
    fun checkPermissions() {
        // 1. 通知权限检测
        hasNotificationPermission = NotificationManagerCompat.from(context).areNotificationsEnabled()

        // 2. 精确闹钟权限检测
        hasExactAlarmPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        // 3. 电池优化忽略状态检测
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        isIgnoringBattery = powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    // 动态监听生命周期，在用户从系统设置返回时自动更新状态
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 初始化检测
    LaunchedEffect(Unit) {
        checkPermissions()
    }

    // 动态通知权限申请 Launcher (仅 API 33+)
    val requestNotificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            hasNotificationPermission = true
        } else {
            // 引导去系统设置
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SaltTheme.colors.background)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        TitleBar(
            onBack = { navController.debouncedPopBackStack() },
            text = "通知与后台提醒守护"
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            RoundedColumn {
                ItemTitle(text = "系统权限管理")

                // 1. 发送通知权限
                Item(
                    onClick = {
                        if (!hasNotificationPermission) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestNotificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            }
                        }
                    },
                    text = "发送通知权限",
                    sub = if (hasNotificationPermission) "已允许 (推荐)" else "未允许 (点击去开启)",
                    iconPainter = rememberVectorPainter(Icons.Outlined.Notifications),
                )

                // 2. 精确闹钟权限 (Android 12+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Item(
                        onClick = {
                            if (!hasExactAlarmPermission) {
                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            }
                        },
                        text = "允许设置精确闹钟",
                        sub = if (hasExactAlarmPermission) "已允许 (推荐)" else "未允许 (点击去开启以保证准时提醒)",
                        iconPainter = rememberVectorPainter(Icons.Outlined.AccessTime),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            RoundedColumn {
                ItemTitle(text = "后台防杀与优化")

                // 3. 忽略电池优化
                Item(
                    onClick = {
                        if (!isIgnoringBattery) {
                            try {
                                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                try {
                                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                                    context.startActivity(intent)
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                            }
                        }
                    },
                    text = "忽略电池优化 (后台防杀)",
                    sub = if (isIgnoringBattery) "已开启 (已加入白名单)" else "未开启 (建议开启，以保证后台闹钟稳定运行)",
                    iconPainter = rememberVectorPainter(Icons.Outlined.Info),
                )

                // 4. 防杀后台配置指南
                Item(
                    onClick = { showGuideDialog = true },
                    text = "后台准时提醒防杀指南",
                    sub = "若遇到清理后台后不提醒，请查阅此指南进行配置",
                    iconPainter = rememberVectorPainter(Icons.Outlined.HelpOutline),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
            title = { Text("后台提醒防杀配置指南") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "由于 Android 系统及各大手机厂商的严格省电策略，当您将软件退到后台或清空后台时，应用进程极易被强杀，导致设定的任务提醒不能准时弹出。为了获得最完美的通知提醒体验，强烈建议您进行以下系统配置：",
                        style = SaltTheme.textStyles.paragraph,
                        color = SaltTheme.colors.subText
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "1. 将应用在多任务卡片中锁定", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(
                        text = "进入手机“多任务管理（后台应用卡片）”界面，向下拖拽或点击本应用的卡片，点击“锁定”小挂锁图标。这样一键清理后台时，应用就不会被一并清除。",
                        style = SaltTheme.textStyles.sub,
                        color = SaltTheme.colors.subText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "2. 开启“允许自启动”权限", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(
                        text = "• 华为：手机管家 -> 应用启动管理 -> 找到 QingNote -> 关闭自动管理，开启“允许自启动”、“允许关联启动”、“允许后台活动”。\n" +
                                "• 小米：系统设置 -> 应用设置 -> 授权管理 -> 自启动管理 -> 允许 QingNote 自启动。\n" +
                                "• OPPO/VIVO：系统设置 -> 应用管理 -> 自启动管理 / 权限管理 -> 勾选允许自启动。",
                        style = SaltTheme.textStyles.sub,
                        color = SaltTheme.colors.subText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "3. 允许后台高耗电行为", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    Text(
                        text = "在系统电池或应用详情管理中，允许本应用在后台进行“高耗电活动”并不加限制，可以最大化提升提醒通知的送达率。",
                        style = SaltTheme.textStyles.sub,
                        color = SaltTheme.colors.subText
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showGuideDialog = false }) {
                    Text("我已知晓")
                }
            }
        )
    }
}
