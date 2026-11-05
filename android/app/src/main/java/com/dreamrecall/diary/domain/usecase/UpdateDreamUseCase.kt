package com.dreamrecall.diary.domain.usecase

import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.domain.model.DreamEntry
import com.dreamrecall.diary.domain.model.Tag
import com.dreamrecall.diary.domain.repository.DreamRepository

class UpdateDreamUseCase(private val repository: DreamRepository) {

    operator fun invoke(entry: DreamEntry) {
        repository.put(entry.copy(tags = entry.tags.take(GameConfig.MAX_TAGS_PER_DREAM)))
    }

    fun addTag(entry: DreamEntry, raw: String, tag: Tag): DreamEntry {
        val clean = raw.trim().lowercase().take(GameConfig.MAX_TAG_CHARS)
        if (clean.isEmpty()) return entry
        if (entry.tags.size >= GameConfig.MAX_TAGS_PER_DREAM) return entry
        if (entry.tags.any { it.name == clean }) return entry
        return entry.copy(tags = entry.tags + tag.copy(name = clean))
    }

    fun removeTag(entry: DreamEntry, name: String): DreamEntry =
        entry.copy(tags = entry.tags.filterNot { it.name == name })
}
