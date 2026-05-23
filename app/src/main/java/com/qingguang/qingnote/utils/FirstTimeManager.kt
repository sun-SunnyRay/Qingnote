package com.qingguang.qingnote.utils

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.qingguang.qingnote.App
import com.qingguang.qingnote.R
import com.qingguang.qingnote.bean.Note
import com.qingguang.qingnote.db.repo.TagNoteRepo
import com.moriafly.salt.ui.ItemOutHalfSpacer
import com.moriafly.salt.ui.ItemOutSpacer
import com.moriafly.salt.ui.SaltTheme
import com.moriafly.salt.ui.UnstableSaltApi
import com.moriafly.salt.ui.dialog.BasicDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirstTimeManager @Inject constructor() {

    @Inject
    lateinit var tagNoteRepo: TagNoteRepo

    suspend fun generateIntroduceNoteList() = withContext(Dispatchers.IO) {
        try {
            // 双重安全保障：
            // 保障一：传统的 DataStore + 实时同步且重启 100% 可靠的 SharedPreferences 作为首启标志
            val sp = App.instance.getSharedPreferences("QingNoteFirstLaunchPrefs", Context.MODE_PRIVATE)
            val isSpFirst = sp.getBoolean("is_first_launch", true)
            
            if (!isSpFirst) {
                return@withContext
            }
            
            // 保障二：直接检查数据库，如果数据库本身已经不为空，则坚决不再生成示例笔记
            val allNotes = tagNoteRepo.queryAllNoteList()
            if (allNotes.isNotEmpty()) {
                // 如果数据库不为空，即使 sp 判定为首次，也同步把 SP 更新为 false 并退出，防止后续重新生成
                sp.edit().putBoolean("is_first_launch", false).apply()
                return@withContext
            }
            
            // 首次启动，且数据库为空，执行创建
            if (App.instance.isSystemLanguageEnglish()) {
                generateEnglishIntroduceNoteList()
            } else {
                generateChineseIntroduceNoteList()
            }
            
            // 创建成功后，保存状态
            sp.edit().putBoolean("is_first_launch", false).apply()
            
            // 原有的 DataStore 标志也顺手更新
            SettingsPreferences.changeFirstLaunch(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateChineseIntroduceNoteList() {
        val functionNote = Note(
            content = "#灵感 \n生活不止眼前的苟且 还有诗和远方。",
        )
        tagNoteRepo.insertOrUpdate(functionNote)
    }

    private fun generateEnglishIntroduceNoteList() {
        val functionNote = Note(
            content = "#Life \nLess is more.",
        )
        tagNoteRepo.insertOrUpdate(functionNote)
    }

}

@OptIn(UnstableSaltApi::class)
@Composable
fun FirstTimeWarmDialog(block: () -> Unit) {
    val context = LocalContext.current
    BasicDialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ItemOutSpacer()
            Text(
                text = stringResource(R.string.welcome),
                style = SaltTheme.textStyles.main.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                color = SaltTheme.colors.text
            )

            ItemOutSpacer()

            // Features Section
            FeatureItem(
                title = stringResource(R.string.welcome_pure_title),
                desc = stringResource(R.string.welcome_pure_desc),
                icon = Icons.Outlined.AutoAwesome
            )
            ItemOutSpacer()
            FeatureItem(
                title = stringResource(R.string.welcome_card_title),
                desc = stringResource(R.string.welcome_card_desc),
                icon = Icons.Outlined.Style
            )
            ItemOutSpacer()
            FeatureItem(
                title = stringResource(R.string.welcome_privacy_title),
                desc = stringResource(R.string.welcome_privacy_desc),
                icon = Icons.Outlined.Security
            )

            ItemOutSpacer()

            // Usage Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SaltTheme.colors.subBackground)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.usage_title),
                    style = SaltTheme.textStyles.main.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    color = SaltTheme.colors.text
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.warm_reminder_desc),
                    style = SaltTheme.textStyles.sub.copy(fontSize = 13.sp, lineHeight = 20.sp),
                    color = SaltTheme.colors.subText
                )
            }

            ItemOutSpacer()

            // Legal Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.browse_tos_tips_service),
                    style = SaltTheme.textStyles.sub.copy(fontSize = 12.sp, textDecoration = TextDecoration.Underline),
                    color = SaltTheme.colors.highlight,
                    modifier = Modifier.clickable { Constant.startUserAgreeUrl(context) }
                )
                Text(
                    text = " & ",
                    style = SaltTheme.textStyles.sub.copy(fontSize = 12.sp),
                    color = SaltTheme.colors.subText,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = stringResource(R.string.browse_tos_tips_privacy),
                    style = SaltTheme.textStyles.sub.copy(fontSize = 12.sp, textDecoration = TextDecoration.Underline),
                    color = SaltTheme.colors.highlight,
                    modifier = Modifier.clickable { Constant.startPrivacyUrl(context) }
                )
            }

            ItemOutSpacer()

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                com.moriafly.salt.ui.TextButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.exit),
                    onClick = { if (context is Activity) context.finish() }
                )
                com.moriafly.salt.ui.TextButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.agree),
                    onClick = block
                )
            }
            ItemOutSpacer()
        }
    }
}

@Composable
private fun FeatureItem(title: String, desc: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp).padding(top = 2.dp),
            tint = SaltTheme.colors.highlight
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = SaltTheme.textStyles.main.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                color = SaltTheme.colors.text
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                style = SaltTheme.textStyles.sub.copy(fontSize = 13.sp, lineHeight = 18.sp),
                color = SaltTheme.colors.subText
            )
        }
    }
}


@Composable
fun TipsDialog(block: () -> Unit) {
    BasicDialog(
        onDismissRequest = { },
        properties = DialogProperties()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.warm_reminder),
                style = SaltTheme.textStyles.main.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            ItemOutHalfSpacer()
            Text(
                text = stringResource(id = R.string.warm_reminder_desc),
                style = SaltTheme.textStyles.sub
            )
            ItemOutSpacer()
            com.moriafly.salt.ui.TextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.ok),
                onClick = block
            )
        }
    }
}
