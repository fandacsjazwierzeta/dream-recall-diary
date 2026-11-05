package com.dreamrecall.diary.presentation.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.domain.model.BrightnessLevel
import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.repository.SettingsRepository
import com.dreamrecall.diary.domain.usecase.SaveDreamUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CaptureViewModel(
    private val saveDream: SaveDreamUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CaptureUiState())
    val state: StateFlow<CaptureUiState> = _state.asStateFlow()

    private val storedChannel = Channel<String>(Channel.BUFFERED)
    val storedEvents: Flow<String> = storedChannel.receiveAsFlow()

    fun begin() {
        val settings = settingsRepository.load()
        val draft = settingsRepository.readDraft()
        _state.update {
            it.copy(
                body = draft,
                wordCount = countWords(draft),
                nightLabel = NightDates.shortLabel(NightDates.forMillis(System.currentTimeMillis())),
                dimmerStep = settings.brightness.ordinal,
                overlayAlpha = settings.brightness.overlayAlpha,
                askClear = false,
                emptyWarning = false
            )
        }
    }

    fun onBodyChange(text: String) {
        val trimmed = text.take(GameConfig.MAX_BODY_CHARS)
        _state.update {
            it.copy(body = trimmed, wordCount = countWords(trimmed), emptyWarning = false)
        }
    }

    fun setDimmer(step: Int) {
        val level = BrightnessLevel.of(step)
        settingsRepository.setBrightness(level)
        _state.update { it.copy(dimmerStep = level.ordinal, overlayAlpha = level.overlayAlpha) }
    }

    fun askClear() {
        if (_state.value.body.isBlank()) return
        _state.update { it.copy(askClear = true) }
    }

    fun dismissClear() {
        _state.update { it.copy(askClear = false) }
    }

    fun confirmClear() {
        settingsRepository.writeDraft("")
        _state.update { it.copy(body = "", wordCount = 0, askClear = false, emptyWarning = false) }
    }

    fun store() {
        val current = _state.value
        val entry = saveDream(current.body, System.currentTimeMillis())
        if (entry == null) {
            _state.update { it.copy(emptyWarning = true) }
            return
        }
        settingsRepository.writeDraft("")
        _state.update { it.copy(body = "", wordCount = 0, emptyWarning = false) }
        viewModelScope.launch { storedChannel.send(entry.id) }
    }

    fun pause() {
        settingsRepository.writeDraft(_state.value.body)
    }

    private fun countWords(text: String): Int =
        text.split(' ', '\n', '\t').count { it.isNotBlank() }
}
