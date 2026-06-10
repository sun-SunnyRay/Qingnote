@file:JvmName("SettingsPageKt")

package com.qingguang.qingnote.ui.page.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastSumBy
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.material.color.DynamicColors
import com.qingguang.qingnote.R
import com.qingguang.qingnote.component.ItemPopup
import com.qingguang.qingnote.ui.page.LocalMemosState
import com.qingguang.qingnote.ui.page.LocalTags
import com.qingguang.qingnote.ui.page.main.MainActivity
import com.qingguang.qingnote.ui.page.router.Screen
import com.qingguang.qingnote.utils.LanguageUtils
import com.qingguang.qingnote.utils.SettingsPreferences
import com.qingguang.qingnote.utils.str
import com.qingguang.qingnote.utils.toYYMMDD
import com.moriafly.salt.ui.Item
import com.moriafly.salt.ui.ItemSwitcher
import com.moriafly.salt.ui.ItemTitle
import com.moriafly.salt.ui.RoundedColumn
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.UnstableSaltApi
import com.moriafly.salt.ui.popup.PopupMenuItem
import com.moriafly.salt.ui.popup.rememberPopupState
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun SettingsPage(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SaltTheme.colors.background)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Column {
            Text(
                text = R.string.settings.str,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                style = SaltTheme.textStyles.main.copy(fontSize = 24.sp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        SettingsPreferenceScreen(navController)
    }
}

data class SettingsBean(val title: Int, val imageVector: ImageVector, val onClick: () -> Unit)

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(UnstableSaltApi::class)
@Composable
fun SettingsPreferenceScreen(navController: NavHostController) {
    val context = LocalContext.current
    val themeModePopupMenuState = rememberPopupState()
    val languagePopupMenuState = rememberPopupState()
    val settingsViewModel = hiltViewModel<SettingsViewModel>()
    val biometricAuthState by settingsViewModel.biometricAuthState.collectAsState()
    val dynamicColor by SettingsPreferences.dynamicColor.collectAsState(false)
    val themeMode by SettingsPreferences.themeMode.collectAsState(SettingsPreferences.ThemeMode.SYSTEM)
    val scope = rememberCoroutineScope()

    val settingList = listOf(
        SettingsBean(
            R.string.random_walk,
            Icons.Outlined.Explore
        ) { navController.navigate(Screen.RandomWalk) },
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        // 固定两列
        content = {
            item(content = {
                HeatContent()
            })

            item(content = {
                SettingsHeadLayout()
            })

            item {
                RoundedColumn {
                    ItemTitle(text = stringResource(R.string.user_interface))
                    if (DynamicColors.isDynamicColorAvailable()) {
                        ItemSwitcher(
                            state = dynamicColor,
                            onChange = { checked ->
                                scope.launch {
                                    SettingsPreferences.changeDynamicColor(checked)
                                }
                            },
                            text = stringResource(R.string.dynamic_color_switcher_text),
                            sub = stringResource(R.string.dynamic_color_switcher_sub),
                            iconPainter = painterResource(id = R.drawable.color),
                            iconPaddingValues = PaddingValues(all = 1.7.dp),
                            iconColor = SaltTheme.colors.text,
                        )
                    }
                    ItemPopup(
                        state = themeModePopupMenuState,
                        iconPainter = painterResource(id = R.drawable.app_theme),
                        iconPaddingValues = PaddingValues(all = 1.8.dp),
                        iconColor = SaltTheme.colors.text,
                        text = stringResource(R.string.theme_mode_switcher_text),
                        selectedItem = stringResource(id = themeMode.resId),
                        popupWidth = 140
                    ) {

                        val options =
                            SettingsPreferences.ThemeMode.entries.map { stringResource(id = it.resId) }
                        val selectedIndex = SettingsPreferences.ThemeMode.entries.indexOf(themeMode)

                        options.forEachIndexed { index, label ->
                            PopupMenuItem(
                                onClick = {
                                    scope.launch {
                                        SettingsPreferences.changeThemeMode(SettingsPreferences.ThemeMode.entries[index])
                                    }
                                    themeModePopupMenuState.dismiss()
                                },
                                selected = selectedIndex == index,
                                text = label,
                                iconColor = SaltTheme.colors.text
                            )
                        }
                    }

                    // 语言切换 Item
                    val currentLocale = LanguageUtils.getAppLocale(context)
                    val languageOptions = listOf(
                        stringResource(R.string.language_chinese) to Locale.SIMPLIFIED_CHINESE,
                        stringResource(R.string.language_english) to Locale.ENGLISH,
                        stringResource(R.string.language_traditional_chinese) to Locale.TRADITIONAL_CHINESE
                    )

                    val currentLanguageName = when (currentLocale.language) {
                        "zh" -> if (currentLocale.country == "TW" || currentLocale.country == "HK") stringResource(R.string.language_traditional_chinese) else stringResource(R.string.language_chinese)
                        "en" -> stringResource(R.string.language_english)
                        else -> stringResource(R.string.language_english)
                    }

                    ItemPopup(
                        state = languagePopupMenuState,
                        iconPainter = rememberVectorPainter(Icons.Outlined.Translate),
                        iconColor = SaltTheme.colors.text,
                        text = stringResource(R.string.language_switcher_text),
                        selectedItem = currentLanguageName,
                        popupWidth = 160
                    ) {
                        languageOptions.forEach { (name, locale) ->
                            PopupMenuItem(
                                onClick = {
                                    LanguageUtils.setLanguage(context, locale)
                                    languagePopupMenuState.dismiss()
                                },
                                selected = currentLocale.language == locale.language &&
                                           (if (locale.language == "zh") currentLocale.country == locale.country else true),
                                text = name,
                                iconColor = SaltTheme.colors.text
                            )
                        }
                    }
                }
            }

            item {
                RoundedColumn {
                    ItemTitle(text = stringResource(R.string.safe))
                    ItemSwitcher(
                        state = biometricAuthState,
                        iconPainter = rememberVectorPainter(Icons.Outlined.Fingerprint),
                        iconColor = SaltTheme.colors.text,
                        onChange = {
                            settingsViewModel.showBiometricPrompt(context as MainActivity)
                        },
                        text = R.string.biometric.str
                    )
                    Item(
                        onClick = {
                            navController.navigate(Screen.DataManager)
                        },
                        text = R.string.local_data_manager.str,
                        iconPainter = rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_database))
                    )
                    Item(
                        onClick = {
                            navController.navigate(Screen.TaskSettings)
                        },
                        text = "任务设置",
                        iconPainter = rememberVectorPainter(Icons.Outlined.CheckCircle),
                    )
                    Item(
                        onClick = {
                            navController.navigate(Screen.NotificationGuard)
                        },
                        text = "通知与后台提醒守护",
                        iconPainter = rememberVectorPainter(Icons.Outlined.Notifications),
                    )

                    settingList.forEachIndexed { index, it ->
                        Item(
                            onClick = {
                                it.onClick()
                            },
                            text = it.title.str,
                            iconPainter = rememberVectorPainter(it.imageVector),
                        )
                    }

                }
            }


        })

//    if (showWarnDialog) {
//        TipsDialog(block = { showWarnDialog = false })
//    }
}

@Composable
fun SettingsHeadLayout() {
    val noteState = LocalMemosState.current
    val memos by lazy(noteState::notes)
    val tagList = LocalTags.current

    Row {
        val modifier = Modifier.weight(1f)

        boxText(
            modifier, memos.size.toString(), R.string.all_note.str
        )
        boxText(
            modifier,
            memos.fastSumBy { it.note.noteTitle?.length ?: (0 + it.note.content.length) }
                .toString(),
            R.string.characters.str
        )
        boxText(
            modifier,
            memos.map { it.note.createTime.toYYMMDD() }.toSet().size.toString(),
            R.string.dyas.str
        )

        boxText(
            modifier, tagList.size.toString(), R.string.tag.str
        )
    }
}

@Composable
private fun boxText(modifier: Modifier, title: String, desc: String) {

    Column(
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = SaltTheme.textStyles.main,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = desc,
            style = SaltTheme.textStyles.sub,
        )
    }
}

