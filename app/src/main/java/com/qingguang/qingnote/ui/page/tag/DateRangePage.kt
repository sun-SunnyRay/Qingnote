package com.qingguang.qingnote.ui.page.tag

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.qingguang.qingnote.bean.NoteShowBean
import com.qingguang.qingnote.component.NoteCard
import com.qingguang.qingnote.component.NoteCardFrom
import com.qingguang.qingnote.component.RYScaffold
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.utils.SettingsPreferences
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DateRangePage(startTime: Long, endTime: Long, navController: NavHostController) {
    val noteViewModel = LocalMemosViewModel.current
    val filterYearList = remember { mutableStateListOf<NoteShowBean>() }

    LaunchedEffect(key1 = Unit, block = {
        noteViewModel.getNotesByCreateTimeRange(startTime, endTime).collect {
            filterYearList.clear()
            filterYearList.addAll(it)
        }
    })


    // 转换时间戳为指定格式的字符串
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyyMMdd") }
    val startTimeStr = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
    val endTimeStr = Instant.ofEpochMilli(endTime).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
    val title = "$startTimeStr-$endTimeStr"


    RYScaffold(
        title = title,
        navController = navController,
    ) {
        LazyColumn {
            items(count = filterYearList.size, key = { it }) { index ->
                NoteCard(noteShowBean = filterYearList[index], navController, from = NoteCardFrom.TAG_DETAIL)
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}