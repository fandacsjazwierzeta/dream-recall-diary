package com.dreamrecall.diary.domain.usecase

import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.repository.DreamRepository

class ComputeStreakUseCase(private val repository: DreamRepository) {

    operator fun invoke(): Int {
        val nights = repository.all().map { it.nightDate }.toSet()
        if (nights.isEmpty()) return 0
        var cursor = NightDates.today()
        if (!nights.contains(cursor.toString())) cursor = cursor.minusDays(1)
        var streak = 0
        while (nights.contains(cursor.toString())) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }
}
