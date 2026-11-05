package com.dreamrecall.diary.presentation.dreampage

import com.dreamrecall.diary.domain.model.Tag

data class DreamPageUiState(
    val dreamId: String = "",
    val dateLabel: String = "",
    val title: String = "",
    val body: String = "",
    val vividness: Int = 0,
    val tags: List<Tag> = emptyList(),
    val tagDraft: String = "",
    val editing: Boolean = false,
    val askDelete: Boolean = false,
    val fontSizeSp: Int = 19,
    val loaded: Boolean = false
)
