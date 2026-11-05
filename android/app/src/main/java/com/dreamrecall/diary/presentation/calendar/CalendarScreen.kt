package com.dreamrecall.diary.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.Accent
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.TextFaint
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary
import com.dreamrecall.diary.core.theme.vividnessColor
import com.dreamrecall.diary.core.ui.AppIcons
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.ScreenHeader
import com.dreamrecall.diary.core.ui.StatCard
import com.dreamrecall.diary.domain.model.MonthDay

@Composable
fun CalendarScreen(
    onOpenDate: (String) -> Unit,
    onBack: () -> Unit
) {
    val calendarViewModel: CalendarViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by calendarViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        calendarViewModel.refresh()
    }

    AuroraBackground(
        overlay = listOf(NightDeep, NightDeep.copy(alpha = 0.93f)),
        blobs = 2
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = state.month?.label.orEmpty(),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack,
                trailing = {
                    IconButton(onClick = calendarViewModel::previousMonth) {
                        Icon(
                            imageVector = AppIcons.ArrowBack,
                            contentDescription = stringResource(R.string.cd_previous_month),
                            tint = TextMuted
                        )
                    }
                    IconButton(onClick = calendarViewModel::nextMonth) {
                        Icon(
                            imageVector = AppIcons.ArrowBack,
                            contentDescription = stringResource(R.string.cd_next_month),
                            tint = TextMuted,
                            modifier = Modifier.graphicsLayer { scaleX = -1f }
                        )
                    }
                }
            )
            val month = state.month
            if (month != null) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    val gap = 6.dp
                    val cell = (maxWidth - gap * 6) / 7
                    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                        month.days.chunked(7).forEach { week ->
                            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                                week.forEach { day ->
                                    DayCell(
                                        day = day,
                                        size = cell,
                                        stateLabel = stringResource(
                                            R.string.calendar_day_state,
                                            day.dayNumber,
                                            day.dreamCount
                                        ),
                                        onClick = { onOpenDate(day.dateIso) }
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                if (month.nightsLogged > 0 && state.streak > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            icon = AppIcons.Calendar,
                            value = month.nightsLogged.toString(),
                            label = stringResource(R.string.stat_nights_logged),
                            tint = Primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = AppIcons.Star,
                            value = state.streak.toString(),
                            label = stringResource(R.string.stat_streak),
                            tint = Accent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: MonthDay,
    size: Dp,
    stateLabel: String,
    onClick: () -> Unit
) {
    val hasDreams = day.dreamCount > 0
    val fill = if (hasDreams) {
        vividnessColor(if (day.topVividness > 0) day.topVividness else 1)
            .copy(alpha = (0.22f + 0.15f * day.dreamCount).coerceAtMost(0.75f))
    } else {
        Color.Transparent
    }
    Box(
        modifier = Modifier
            .size(size)
            .background(fill, CircleShape)
            .border(
                width = if (day.isToday) 1.5.dp else 1.dp,
                color = when {
                    day.isToday -> Accent
                    hasDreams -> Primary.copy(alpha = 0.40f)
                    else -> Color.Transparent
                },
                shape = CircleShape
            )
            .then(
                if (hasDreams) {
                    Modifier
                        .clickable(onClick = onClick)
                        .semantics { stateDescription = stateLabel }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayNumber.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = when {
                !day.inMonth -> TextFaint.copy(alpha = 0.45f)
                hasDreams -> TextPrimary
                else -> TextMuted
            }
        )
    }
}
