package com.dreamrecall.diary.presentation.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.domain.repository.LockRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LockViewModel(private val lockRepository: LockRepository) : ViewModel() {

    private val _state = MutableStateFlow(LockUiState())
    val state: StateFlow<LockUiState> = _state.asStateFlow()

    private val unlockChannel = Channel<Unit>(Channel.BUFFERED)
    val unlockEvents: Flow<Unit> = unlockChannel.receiveAsFlow()

    fun press(digit: String) {
        val current = _state.value
        if (current.locked || current.entered.length >= GameConfig.PIN_LENGTH) return
        _state.value = current.copy(entered = current.entered + digit, wrong = false)
    }

    fun clear() {
        _state.value = _state.value.copy(entered = "", wrong = false)
    }

    fun confirm() {
        val current = _state.value
        if (current.locked || current.entered.length < GameConfig.PIN_LENGTH) return
        if (lockRepository.matches(current.entered)) {
            _state.value = LockUiState()
            viewModelScope.launch { unlockChannel.send(Unit) }
            return
        }
        val attempts = current.failedAttempts + 1
        _state.value = current.copy(entered = "", wrong = true, failedAttempts = attempts)
        if (attempts >= GameConfig.PIN_MAX_ATTEMPTS) startCooldown()
    }

    private fun startCooldown() {
        viewModelScope.launch {
            var remaining = (GameConfig.PIN_COOLDOWN_MS / 1000L).toInt()
            _state.value = _state.value.copy(cooldownSeconds = remaining, failedAttempts = 0)
            while (remaining > 0) {
                delay(1000L)
                remaining--
                _state.value = _state.value.copy(cooldownSeconds = remaining)
            }
            _state.value = _state.value.copy(wrong = false)
        }
    }
}
