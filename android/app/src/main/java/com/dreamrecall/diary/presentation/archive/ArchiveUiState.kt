package com.dreamrecall.diary.presentation.archive

import com.dreamrecall.diary.domain.model.DreamEntry

data class DreamRow(
    val dream: DreamEntry,
    val dateLabel: String
)

data class ArchiveUiState(
    val rows: List<DreamRow> = emptyList(),
    val query: String = "",
    val searchOpen: Boolean = false,
    val tagFilter: String? = null,
    val dateFilter: String? = null,
    val pendingDelete: String? = null,
    val loaded: Boolean = false
)
