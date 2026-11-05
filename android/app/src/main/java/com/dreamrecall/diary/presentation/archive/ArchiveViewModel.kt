package com.dreamrecall.diary.presentation.archive

import androidx.lifecycle.ViewModel
import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.usecase.DeleteDreamUseCase
import com.dreamrecall.diary.domain.usecase.GetDreamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArchiveViewModel(
    private val getDreams: GetDreamsUseCase,
    private val deleteDream: DeleteDreamUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ArchiveUiState())
    val state: StateFlow<ArchiveUiState> = _state.asStateFlow()

    fun open(tagFilter: String?, dateFilter: String?) {
        _state.update { it.copy(tagFilter = tagFilter, dateFilter = dateFilter, query = "") }
        reload()
    }

    fun onQueryChange(text: String) {
        _state.update { it.copy(query = text) }
        reload()
    }

    fun toggleSearch() {
        _state.update { it.copy(searchOpen = !it.searchOpen, query = "") }
        reload()
    }

    fun clearFilters() {
        _state.update { it.copy(tagFilter = null, dateFilter = null) }
        reload()
    }

    fun askDelete(id: String) {
        _state.update { it.copy(pendingDelete = id) }
    }

    fun dismissDelete() {
        _state.update { it.copy(pendingDelete = null) }
    }

    fun confirmDelete() {
        val id = _state.value.pendingDelete ?: return
        deleteDream(id)
        _state.update { it.copy(pendingDelete = null) }
        reload()
    }

    fun reload() {
        val current = _state.value
        val rows = getDreams
            .filtered(current.query, current.tagFilter, current.dateFilter)
            .map { DreamRow(it, NightDates.shortLabel(it.nightDate)) }
        _state.update { it.copy(rows = rows, loaded = true) }
    }
}
