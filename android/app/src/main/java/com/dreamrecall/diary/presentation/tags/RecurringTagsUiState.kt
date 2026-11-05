package com.dreamrecall.diary.presentation.tags

import com.dreamrecall.diary.domain.model.TagCategory
import com.dreamrecall.diary.domain.model.TagCount

data class TagSection(
    val category: TagCategory,
    val items: List<TagCount>
)

data class RecurringTagsUiState(
    val sections: List<TagSection> = emptyList(),
    val maxCount: Int = 1,
    val loaded: Boolean = false
) {
    val isEmpty: Boolean get() = sections.isEmpty()
}
