package com.qingguang.qingnote.ui.page.home

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.tasks.data.entity.Task
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.qingguang.qingnote.R
import com.qingguang.qingnote.bean.NoteShowBean
import com.qingguang.qingnote.component.CardCalender
import com.qingguang.qingnote.component.EmptyComponent
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.qingguang.qingnote.utils.lunchIo
import com.qingguang.qingnote.utils.str
import com.moriafly.salt.ui.SaltTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalenderPage(navController: NavHostController) {

    val noteViewModel = LocalMemosViewModel.current
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(500) }
    val endMonth = remember { currentMonth.plusMonths(500) }
    val daysOfWeek = remember { daysOfWeek() }
    val today = remember { LocalDate.now() }
    var currentLocalDate by remember { mutableStateOf(LocalDate.now()) }
    val filterList: SnapshotStateList<NoteShowBean> = remember { mutableStateListOf<NoteShowBean>() }
    val taskList: SnapshotStateList<Task> = remember { mutableStateListOf<Task>() }
    val scope = rememberCoroutineScope()

    val calendarState: CalendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
    )

    LaunchedEffect(currentLocalDate) {
        lunchIo {
            filterList.clear()
            filterList.addAll(noteViewModel.getNotesOnSelectedDate(currentLocalDate))
            
            // 加载截止日期为当天的任务
            val zone = java.time.ZoneId.systemDefault()
            val start = currentLocalDate.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = currentLocalDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
            val entryPoint = dagger.hilt.EntryPoints.get(
                com.qingguang.qingnote.App.instance,
                com.qingguang.qingnote.AppEntryPoint::class.java
            )
            val tasks = entryPoint.tasksRepository().getTasksByDueDate(start, end)
            taskList.clear()
            taskList.addAll(tasks)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SaltTheme.colors.background)
            .statusBarsPadding()
    ) {
        IndexTopBar(currentLocalDate, navigateToToday = {
            currentLocalDate = LocalDate.now()
            scope.launch {
                calendarState.animateScrollToMonth(YearMonth.now())
            }
        })
        LazyColumn {
            stickyHeader {
                Column {
                    HorizontalCalendar(
                        modifier = Modifier
                            .testTag("Calendar")
                            .background(SaltTheme.colors.background),
                        state = calendarState,
                        dayContent = { day: CalendarDay ->
                            val hasScheme = noteViewModel.levelMemosMap.containsKey(day.date)
                            Day(day, today, hasScheme = hasScheme, isSelected = currentLocalDate == day.date) { calendarDay: CalendarDay ->
                                currentLocalDate = calendarDay.date
                            }
                        },
                        monthHeader = {
                            MonthHeader(daysOfWeek = daysOfWeek)
                        },
                    )
                }
            }

            // 笔记列表
            if (filterList.isEmpty() && taskList.isEmpty()) {
                item {
                    EmptyComponent(
                        Modifier
                            .fillMaxWidth()
                            .height(height = 300.dp)
                    )
                }
            }

            if (filterList.isNotEmpty()) {
                items(count = filterList.size, key = { it }) { index ->
                    CardCalender(noteShowBean = filterList[index], navController)
                }
            }

            // 任务分隔线
            if (taskList.isNotEmpty()) {
                item {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "任务",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = SaltTheme.colors.text
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // 任务列表
            if (taskList.isNotEmpty()) {
                items(count = taskList.size, key = { "task_${it}" }) { index ->
                    TaskCalendarItem(task = taskList[index])
                }
            }

            if (filterList.isNotEmpty() || taskList.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexTopBar(
    date: LocalDate, navigateToToday: () -> Unit, modifier: Modifier = Modifier
) {
    // 日期
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SaltTheme.colors.background),
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = date.month.getDisplayName(
                        TextStyle.SHORT, Locale.getDefault()
                    ) + date.dayOfMonth + R.string.day.str, style = SaltTheme.textStyles.main.copy(fontSize = 24.sp).copy(fontWeight = FontWeight.Bold)
                )
                Column {
                    Text(
                        text = date.year.toString(), style = SaltTheme.textStyles.main.copy(fontSize = 12.sp).copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = date.dayOfWeek.getDisplayName(
                            TextStyle.SHORT, Locale.getDefault()
                        ),
                        style = SaltTheme.textStyles.main.copy(fontSize = 12.sp).copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { navigateToToday() }) {
                Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarToday, contentDescription = "Today", tint = SaltTheme.colors.text
                    )
                    Text(
                        text = LocalDate.now().dayOfMonth.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(color = SaltTheme.colors.text),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
    )
}

@Composable
fun TaskCalendarItem(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 完成复选框
        androidx.compose.material3.Checkbox(
            checked = task.completionDate > 0,
            onCheckedChange = null // 只显示，不交互
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 标题
        Text(
            text = task.title ?: "",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (task.completionDate > 0) 
                androidx.compose.ui.text.style.TextDecoration.LineThrough 
            else null,
            color = if (task.completionDate > 0) 
                SaltTheme.colors.subText 
            else SaltTheme.colors.text
        )
    }
}