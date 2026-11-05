package com.dreamrecall.diary.domain.usecase

import com.dreamrecall.diary.domain.model.TagCategory
import com.dreamrecall.diary.domain.model.TagCount
import com.dreamrecall.diary.domain.repository.DreamRepository

class CountRecurringTagsUseCase(private val repository: DreamRepository) {

    operator fun invoke(): Map<TagCategory, List<TagCount>> {
        val flat = repository.all().flatMap { it.tags }
        return TagCategory.entries.associateWith { category ->
            flat.filter { it.category == category }
                .groupBy { it.name }
                .map { (name, items) -> TagCount(name, category, items.size) }
                .sortedWith(compareByDescending<TagCount> { it.count }.thenBy { it.name })
        }
    }

    fun mostFrequent(limit: Int): List<TagCount> =
        invoke().values.flatten().sortedByDescending { it.count }.take(limit)
}
