package com.dreamrecall.diary.presentation.settings

import androidx.lifecycle.ViewModel
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.domain.model.BrightnessLevel
import com.dreamrecall.diary.domain.model.FontSizeLevel
import com.dreamrecall.diary.domain.repository.LockRepository
import com.dreamrecall.diary.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val lockRepository: LockRepository
) : ViewModel() {

    private var pendingPin: String = ""

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun refresh() {
        val settings = settingsRepository.load()
        _state.update {
            it.copy(
                brightnessIndex = settings.brightness.ordinal,
                fontIndex = settings.fontSize.ordinal,
                previewSp = settings.fontSize.dreamTextSp,
                lockEnabled = settings.lockEnabled && lockRepository.hasPin(),
                loaded = true
            )
        }
    }

    fun setBrightness(index: Int) {
        val level = BrightnessLevel.of(index)
        settingsRepository.setBrightness(level)
        _state.update { it.copy(brightnessIndex = level.ordinal) }
    }

    fun setFontSize(index: Int) {
        val level = FontSizeLevel.of(index)
        settingsRepository.setFontSize(level)
        _state.update { it.copy(fontIndex = level.ordinal, previewSp = level.dreamTextSp) }
    }

    fun onLockToggle(enabled: Boolean) {
        pendingPin = ""
        _state.update {
            it.copy(
                pinFlow = if (enabled) PinFlow.SET else PinFlow.VERIFY_OFF,
                pinEntered = "",
                pinWrong = false
            )
        }
    }

    fun requestPinChange() {
        pendingPin = ""
        _state.update { it.copy(pinFlow = PinFlow.VERIFY_CHANGE, pinEntered = "", pinWrong = false) }
    }

    fun dismissPinFlow() {
        pendingPin = ""
        _state.update { it.copy(pinFlow = PinFlow.NONE, pinEntered = "", pinWrong = false) }
    }

    fun pinDigit(digit: String) {
        val current = _state.value
        if (current.pinEntered.length >= GameConfig.PIN_LENGTH) return
        _state.update { it.copy(pinEntered = it.pinEntered + digit, pinWrong = false) }
    }

    fun pinClear() {
        _state.update { it.copy(pinEntered = "", pinWrong = false) }
    }

    fun pinConfirm() {
        val current = _state.value
        if (current.pinEntered.length < GameConfig.PIN_LENGTH) return
        when (current.pinFlow) {
            PinFlow.SET -> {
                pendingPin = current.pinEntered
                _state.update { it.copy(pinFlow = PinFlow.CONFIRM, pinEntered = "") }
            }
            PinFlow.CONFIRM -> {
                if (current.pinEntered == pendingPin) {
                    lockRepository.definePin(pendingPin)
                    settingsRepository.setLockEnabled(true)
                    pendingPin = ""
                    _state.update {
                        it.copy(pinFlow = PinFlow.NONE, pinEntered = "", lockEnabled = true)
                    }
                } else {
                    _state.update { it.copy(pinEntered = "", pinWrong = true) }
                }
            }
            PinFlow.VERIFY_OFF -> {
                if (lockRepository.matches(current.pinEntered)) {
                    lockRepository.clearPin()
                    settingsRepository.setLockEnabled(false)
                    _state.update {
                        it.copy(pinFlow = PinFlow.NONE, pinEntered = "", lockEnabled = false)
                    }
                } else {
                    _state.update { it.copy(pinEntered = "", pinWrong = true) }
                }
            }
            PinFlow.VERIFY_CHANGE -> {
                if (lockRepository.matches(current.pinEntered)) {
                    _state.update { it.copy(pinFlow = PinFlow.SET, pinEntered = "", pinWrong = false) }
                } else {
                    _state.update { it.copy(pinEntered = "", pinWrong = true) }
                }
            }
            PinFlow.NONE -> Unit
        }
    }
}
