package com.dreamrecall.diary.presentation.tags

import androidx.lifecycle.ViewModel
import com.dreamrecall.diary.domain.usecase.CountRecurringTagsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecurringTagsViewModel(
    private val countTags: CountRecurringTagsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RecurringTagsUiState())
    val state: StateFlow<RecurringTagsUiState> = _state.asStateFlow()

    fun refresh() {
        val grouped = countTags()
        val sections = grouped
            .filterValues { it.isNotEmpty() }
            .map { (category, items) -> TagSection(category, items) }
        val max = sections.flatMap { it.items }.maxOfOrNull { it.count } ?: 1
        _state.update { it.copy(sections = sections, maxCount = max, loaded = true) }
    }
}
