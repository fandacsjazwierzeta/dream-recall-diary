package com.dreamrecall.diary.domain.model

import com.dreamrecall.diary.core.config.GameConfig
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId

object NightDates {
    private val SHORT_MONTHS = listOf(
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    )
    private val LONG_MONTHS = listOf(
        "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
    )

    fun forMillis(millis: Long): String {
        val moment = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        val date = if (moment.hour < GameConfig.NIGHT_CUTOFF_HOUR) {
            moment.toLocalDate().minusDays(1)
        } else {
            moment.toLocalDate()
        }
        return date.toString()
    }

    fun parse(iso: String): LocalDate? = runCatching { LocalDate.parse(iso) }.getOrNull()

    fun shortLabel(iso: String): String {
        val date = parse(iso) ?: return iso
        return buildString {
            if (date.dayOfMonth < 10) append('0')
            append(date.dayOfMonth)
            append(' ')
            append(SHORT_MONTHS[date.monthValue - 1])
        }
    }

    fun monthLabel(month: YearMonth): String =
        LONG_MONTHS[month.monthValue - 1] + " " + month.year

    fun today(): LocalDate = LocalDate.now(ZoneId.systemDefault())
}
