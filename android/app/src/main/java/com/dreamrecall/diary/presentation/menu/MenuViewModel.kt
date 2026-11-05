package com.dreamrecall.diary.presentation.menu

import androidx.lifecycle.ViewModel
import com.dreamrecall.diary.domain.usecase.GetDreamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class MenuViewModel(private val getDreams: GetDreamsUseCase) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val dreams = getDreams()
        val average = getDreams.averageVividness()
        _state.update {
            it.copy(
                dreamCount = dreams.size,
                nightCount = getDreams.nights(),
                averageVividness = String.format(Locale.US, "%.1f", average),
                hasVividness = average > 0f,
                lastDreamTitle = dreams.firstOrNull()?.title.orEmpty(),
                isLoading = false
            )
        }
    }
}
