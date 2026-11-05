package com.dreamrecall.diary.presentation.constellation

import androidx.lifecycle.ViewModel
import com.dreamrecall.diary.domain.usecase.BuildConstellationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConstellationViewModel(
    private val buildConstellation: BuildConstellationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ConstellationUiState())
    val state: StateFlow<ConstellationUiState> = _state.asStateFlow()

    fun refresh() {
        _state.update { it.copy(graph = buildConstellation(), selectedIndex = -1, loaded = true) }
    }

    fun select(index: Int) {
        _state.update { it.copy(selectedIndex = index) }
    }

    fun clearSelection() {
        _state.update { it.copy(selectedIndex = -1) }
    }
}
