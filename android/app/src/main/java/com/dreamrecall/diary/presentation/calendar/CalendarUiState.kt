package com.dreamrecall.diary.presentation.calendar

import com.dreamrecall.diary.domain.model.MonthView

data class CalendarUiState(
    val month: MonthView? = null,
    val streak: Int = 0,
    val loaded: Boolean = false
)
