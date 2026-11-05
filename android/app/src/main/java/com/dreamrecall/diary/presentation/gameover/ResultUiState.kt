package com.dreamrecall.diary.presentation.gameover

import com.dreamrecall.diary.domain.model.Tag

data class ResultUiState(
    val dreamId: String = "",
    val dateLabel: String = "",
    val title: String = "",
    val vividness: Int = 0,
    val tags: List<Tag> = emptyList(),
    val tagDraft: String = "",
    val categoryIndex: Int = 0,
    val suggestions: List<String> = emptyList(),
    val loaded: Boolean = false
)
