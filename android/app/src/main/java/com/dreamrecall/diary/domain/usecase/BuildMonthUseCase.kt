package com.dreamrecall.diary.domain.usecase

import com.dreamrecall.diary.domain.model.MonthDay
import com.dreamrecall.diary.domain.model.MonthView
import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.repository.DreamRepository
import java.time.YearMonth

class BuildMonthUseCase(private val repository: DreamRepository) {

    operator fun invoke(month: YearMonth): MonthView {
        val byDate = repository.all().groupBy { it.nightDate }
        val today = NightDates.today()
        val first = month.atDay(1)
        val leading = (first.dayOfWeek.value - 1).toLong()
        val start = first.minusDays(leading)
        val days = (0 until CELLS).map { offset ->
            val date = start.plusDays(offset.toLong())
            val iso = date.toString()
            val entries = byDate[iso].orEmpty()
            MonthDay(
                dateIso = iso,
                dayNumber = date.dayOfMonth,
                inMonth = date.monthValue == month.monthValue && date.year == month.year,
                dreamCount = entries.size,
                topVividness = entries.maxOfOrNull { it.vividness } ?: 0,
                isToday = date == today
            )
        }
        return MonthView(
            label = NightDates.monthLabel(month),
            year = month.year,
            month = month.monthValue,
            days = days,
            nightsLogged = days.count { it.inMonth && it.dreamCount > 0 }
        )
    }

    private companion object {
        const val CELLS = 42
    }
}
