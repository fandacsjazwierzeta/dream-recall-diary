package com.dreamrecall.diary.domain.model

data class MonthDay(
    val dateIso: String,
    val dayNumber: Int,
    val inMonth: Boolean,
    val dreamCount: Int,
    val topVividness: Int,
    val isToday: Boolean
)

data class MonthView(
    val label: String,
    val year: Int,
    val month: Int,
    val days: List<MonthDay>,
    val nightsLogged: Int
)
