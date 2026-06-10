package com.qingguang.qingnote.ui.page.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.moriafly.salt.ui.SaltTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("MonthHeader"),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = SaltTheme.colors.text,
                text = dayOfWeek.displayText(),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun Day(day: CalendarDay, today: LocalDate, hasScheme: Boolean, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
    val backgroundColor = if (day.date == today) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent
    }
    Box(
        modifier = Modifier
            .aspectRatio(1.2f) // This is important for square-sizing!
            .testTag("MonthDay")
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                color = backgroundColor
            )
            // Disable clicks on inDates/outDates
            .clickable(
//                enabled = day.position == DayPosition.MonthDate,
                showRipple = !hasScheme,
                onClick = { onClick(day) },
            ),
    ) {
        var textColor = when (day.position) {
            // Color.Unspecified will use the default text color from the current theme
            DayPosition.MonthDate -> SaltTheme.colors.text
            DayPosition.InDate, DayPosition.OutDate -> Color.Gray
        }
        if (day.date == today) {
            textColor = Color.White
        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
        if (hasScheme) {
            Canvas(
                modifier = Modifier
                    .size(4.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            ) {
                val radius = size.width / 2f
                drawCircle(
                    color = Color.Gray,
                    radius = radius
                )
            }
        }
    }
}

fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }
}


fun YearMonth.displayText(short: Boolean = false): String {
    return "${this.month.displayText(short = short)} ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return getDisplayName(style, Locale.getDefault())
}

fun Modifier.clickable(
    enabled: Boolean = true,
    showRipple: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = if (showRipple) LocalIndication.current else null,
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = onClick,
    )
}
