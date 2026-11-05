package com.dreamrecall.diary.domain.usecase

import com.dreamrecall.diary.domain.model.DreamEntry
import com.dreamrecall.diary.domain.repository.DreamRepository

class GetDreamsUseCase(private val repository: DreamRepository) {

    operator fun invoke(): List<DreamEntry> = repository.all().sortedByDescending { it.createdAt }

    fun byId(id: String): DreamEntry? = repository.byId(id)

    fun filtered(query: String, tag: String?, date: String?): List<DreamEntry> {
        val needle = query.trim().lowercase()
        return invoke().filter { dream ->
            val matchesQuery = needle.isEmpty() ||
                dream.title.lowercase().contains(needle) ||
                dream.body.lowercase().contains(needle) ||
                dream.tags.any { it.name.contains(needle) }
            val matchesTag = tag == null || dream.tags.any { it.name == tag }
            val matchesDate = date == null || dream.nightDate == date
            matchesQuery && matchesTag && matchesDate
        }
    }

    fun nights(): Int = repository.all().map { it.nightDate }.distinct().size

    fun averageVividness(): Float {
        val scored = repository.all().filter { it.vividness > 0 }
        if (scored.isEmpty()) return 0f
        return scored.sumOf { it.vividness }.toFloat() / scored.size
    }
}
