package com.dreamrecall.diary.domain.usecase

import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.domain.model.DreamEntry
import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.repository.DreamRepository
import java.util.UUID

class SaveDreamUseCase(private val repository: DreamRepository) {

    operator fun invoke(body: String, now: Long): DreamEntry? {
        val text = body.trim().take(GameConfig.MAX_BODY_CHARS)
        if (text.isEmpty()) return null
        val entry = DreamEntry(
            id = UUID.randomUUID().toString(),
            createdAt = now,
            nightDate = NightDates.forMillis(now),
            title = titleOf(text),
            body = text,
            vividness = 0,
            tags = emptyList()
        )
        repository.put(entry)
        return entry
    }

    private fun titleOf(text: String): String {
        val words = text.split(' ', '\n', '\t').filter { it.isNotBlank() }
        return words.take(5).joinToString(" ").take(60)
    }
}
