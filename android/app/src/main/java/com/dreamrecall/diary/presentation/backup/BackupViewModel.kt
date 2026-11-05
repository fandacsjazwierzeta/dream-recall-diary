package com.dreamrecall.diary.presentation.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamrecall.diary.data.local.BackupCodec
import com.dreamrecall.diary.domain.repository.DreamRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BackupViewModel(
    private val dreamRepository: DreamRepository,
    private val codec: BackupCodec
) : ViewModel() {

    private val _state = MutableStateFlow(BackupUiState())
    val state: StateFlow<BackupUiState> = _state.asStateFlow()

    fun refresh() {
        _state.update { it.copy(dreamCount = dreamRepository.all().size, loaded = true) }
    }

    fun onExportPassphraseChange(text: String) {
        _state.update { it.copy(exportPassphrase = text, failed = false) }
    }

    fun onImportPassphraseChange(text: String) {
        _state.update { it.copy(importPassphrase = text, failed = false) }
    }

    fun onImportCodeChange(text: String) {
        _state.update { it.copy(importCode = text, failed = false, added = -1) }
    }

    fun exportArchive() {
        val current = _state.value
        if (!current.canExport) return
        _state.update { it.copy(working = true, failed = false, exportCode = "") }
        viewModelScope.launch {
            val payload = dreamRepository.exportJson()
            val code = withContext(Dispatchers.Default) {
                codec.encode(payload, current.exportPassphrase)
            }
            _state.update {
                it.copy(working = false, exportCode = code.orEmpty(), failed = code == null)
            }
        }
    }

    fun importArchive() {
        val current = _state.value
        if (!current.canImport) return
        _state.update { it.copy(working = true, failed = false, added = -1) }
        viewModelScope.launch {
            val payload = withContext(Dispatchers.Default) {
                codec.decode(current.importCode, current.importPassphrase)
            }
            if (payload == null) {
                _state.update { it.copy(working = false, failed = true) }
                return@launch
            }
            val entries = dreamRepository.parseJson(payload)
            if (entries.isEmpty()) {
                _state.update { it.copy(working = false, failed = true) }
                return@launch
            }
            val inserted = dreamRepository.merge(entries)
            _state.update {
                it.copy(
                    working = false,
                    added = inserted,
                    skipped = entries.size - inserted,
                    dreamCount = dreamRepository.all().size,
                    importCode = ""
                )
            }
        }
    }
}
