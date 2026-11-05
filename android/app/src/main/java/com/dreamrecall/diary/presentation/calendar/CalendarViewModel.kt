package com.dreamrecall.diary.presentation.calendar

import androidx.lifecycle.ViewModel
import com.dreamrecall.diary.domain.usecase.BuildMonthUseCase
import com.dreamrecall.diary.domain.usecase.ComputeStreakUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.YearMonth

class CalendarViewModel(
    private val buildMonth: BuildMonthUseCase,
    private val computeStreak: ComputeStreakUseCase
) : ViewModel() {

    private var cursor: YearMonth = YearMonth.now()

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    fun refresh() {
        _state.update {
            it.copy(month = buildMonth(cursor), streak = computeStreak(), loaded = true)
        }
    }

    fun previousMonth() {
        cursor = cursor.minusMonths(1)
        refresh()
    }

    fun nextMonth() {
        cursor = cursor.plusMonths(1)
        refresh()
    }
}
