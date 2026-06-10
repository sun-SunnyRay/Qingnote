package com.qingguang.qingnote.ui.page.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.HeatMapCalendar
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapCalendarState
import com.kizitonwose.calendar.compose.heatmapcalendar.HeatMapWeek
import com.kizitonwose.calendar.compose.heatmapcalendar.rememberHeatMapCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.qingguang.qingnote.ui.page.home.displayText
import com.qingguang.qingnote.ui.page.LocalMemosViewModel
import com.moriafly.salt.ui.SaltTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

enum class Level(val color: Color) {
    Zero(Color(0xFFEBEDF0)),
    One(Color(0xFF9BE9A8)),
    Two(Color(0xFF40C463)),
    Three(Color(0xFF30A14E)),
    Four(Color(0xFF216E3A)),
}

@Composable
fun HeatContent() {
    val noteViewModel = LocalMemosViewModel.current

    val endDate = remember { LocalDate.now() }
    // GitHub only shows contributions for the past 12 months
    val startDate = remember { endDate.minusMonths(12) }
    var selection by remember { mutableStateOf<Pair<LocalDate, Level>?>(null) }

    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxSize()
    ) {
        val state = rememberHeatMapCalendarState(
            startMonth = startDate.yearMonth,
            endMonth = endDate.yearMonth,
            firstVisibleMonth = endDate.yearMonth,
            firstDayOfWeek = firstDayOfWeekFromLocale(),
        )
        HeatMapCalendar(
            modifier = Modifier.padding(vertical = 10.dp),
            state = state,
            contentPadding = PaddingValues(end = 6.dp),
            dayContent = { day, week ->
                Day(
                    day = day,
                    startDate = startDate,
                    endDate = endDate,
                    week = week,
                    level = noteViewModel.levelMemosMap[day.date] ?: Level.Zero,
                ) { clicked ->
                    selection = Pair(clicked, noteViewModel.levelMemosMap[clicked] ?: Level.Zero)
                }
            },
            weekHeader = { WeekHeader(it) },
            monthHeader = { MonthHeader(it, endDate, state) },
        )
        CalendarInfo(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End),
        )
    }
}

@Composable
private fun CalendarInfo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Less", style = SaltTheme.textStyles.sub.copy(fontSize = 10.sp))
        Level.values().forEach { level ->
            LevelBox(level.color)
        }
        Text(text = "More", style = SaltTheme.textStyles.sub.copy(fontSize = 10.sp))
    }
}

private val daySize = 15.dp

@Composable
private fun Day(
    day: CalendarDay,
    startDate: LocalDate,
    endDate: LocalDate,
    week: HeatMapWeek,
    level: Level,
    onClick: (LocalDate) -> Unit,
) {
    val weekDates = week.days.map { it.date }
    if (day.date in startDate..endDate) {
        LevelBox(level.color) { onClick(day.date) }
    } else if (weekDates.contains(startDate)) {
        LevelBox(Color.Transparent)
    }
}

@Composable
private fun LevelBox(color: Color, onClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .size(daySize) // Must set a size on the day.
            .padding(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(color = color)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
    )
}

@Composable
private fun WeekHeader(dayOfWeek: DayOfWeek) {
    Box(
        modifier = Modifier
            .height(daySize) // Must set a height on the day of week so it aligns with the day.
            .padding(horizontal = 4.dp),
    ) {
        Text(
            text = dayOfWeek.displayText(),
            modifier = Modifier.align(Alignment.Center),
            color = SaltTheme.colors.subText,
            fontSize = 8.sp,
        )
    }
}

@Composable
private fun MonthHeader(
    calendarMonth: CalendarMonth,
    endDate: LocalDate,
    state: HeatMapCalendarState,
) {
    val density = LocalDensity.current
    val firstFullyVisibleMonth by remember {
        // Find the first index with at most one box out of bounds.
        derivedStateOf { getMonthWithYear(state.layoutInfo, daySize, density) }
    }
    if (calendarMonth.weekDays.first().first().date <= endDate) {
        val month = calendarMonth.yearMonth
        val title = if (month == firstFullyVisibleMonth) {
            month.displayText(short = true)
        } else {
            month.month.displayText()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 1.dp, start = 2.dp),
        ) {
            Text(text = title, fontSize = 10.sp, color = SaltTheme.colors.subText)
        }
    }
}

// Find the first index with at most one box out of bounds.
private fun getMonthWithYear(
    layoutInfo: CalendarLayoutInfo,
    daySize: Dp,
    density: Density,
): YearMonth? {
    val visibleItemsInfo = layoutInfo.visibleMonthsInfo
    return when {
        visibleItemsInfo.isEmpty() -> null
        visibleItemsInfo.count() == 1 -> visibleItemsInfo.first().month.yearMonth
        else -> {
            val firstItem = visibleItemsInfo.first()
            val daySizePx = with(density) { daySize.toPx() }
            if (
                firstItem.size < daySizePx * 3 || // Ensure the Month + Year text can fit.
                firstItem.offset < layoutInfo.viewportStartOffset && // Ensure the week row size - 1 is visible.
                (layoutInfo.viewportStartOffset - firstItem.offset > daySizePx)
            ) {
                visibleItemsInfo[1].month.yearMonth
            } else {
                firstItem.month.yearMonth
            }
        }
    }
}
