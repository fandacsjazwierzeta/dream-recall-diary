package com.dreamrecall.diary.presentation.dreampage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.model.Tag
import com.dreamrecall.diary.domain.model.TagCategory
import com.dreamrecall.diary.domain.repository.SettingsRepository
import com.dreamrecall.diary.domain.usecase.DeleteDreamUseCase
import com.dreamrecall.diary.domain.usecase.GetDreamsUseCase
import com.dreamrecall.diary.domain.usecase.UpdateDreamUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DreamPageViewModel(
    private val getDreams: GetDreamsUseCase,
    private val updateDream: UpdateDreamUseCase,
    private val deleteDream: DeleteDreamUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DreamPageUiState())
    val state: StateFlow<DreamPageUiState> = _state.asStateFlow()

    private val removedChannel = Channel<Unit>(Channel.BUFFERED)
    val removedEvents: Flow<Unit> = removedChannel.receiveAsFlow()

    fun load(dreamId: String) {
        val dream = getDreams.byId(dreamId) ?: return
        _state.value = DreamPageUiState(
            dreamId = dream.id,
            dateLabel = NightDates.shortLabel(dream.nightDate),
            title = dream.title,
            body = dream.body,
            vividness = dream.vividness,
            tags = dream.tags,
            fontSizeSp = settingsRepository.load().fontSize.dreamTextSp,
            loaded = true
        )
    }

    fun toggleEdit() {
        val current = _state.value
        if (current.editing) {
            commit()
            return
        }
        _state.update { it.copy(editing = true) }
    }

    fun onBodyChange(text: String) {
        _state.update { it.copy(body = text.take(GameConfig.MAX_BODY_CHARS)) }
    }

    fun onTagDraftChange(text: String) {
        _state.update { it.copy(tagDraft = text.take(GameConfig.MAX_TAG_CHARS)) }
    }

    fun setVividness(value: Int) {
        val dream = getDreams.byId(_state.value.dreamId) ?: return
        updateDream(dream.copy(vividness = value))
        _state.update { it.copy(vividness = value) }
    }

    fun addTag(category: TagCategory) {
        val dream = getDreams.byId(_state.value.dreamId) ?: return
        val raw = _state.value.tagDraft
        val updated = updateDream.addTag(dream, raw, Tag(raw, category))
        updateDream(updated)
        _state.update { it.copy(tags = updated.tags, tagDraft = "") }
    }

    fun removeTag(name: String) {
        val dream = getDreams.byId(_state.value.dreamId) ?: return
        val updated = updateDream.removeTag(dream, name)
        updateDream(updated)
        _state.update { it.copy(tags = updated.tags) }
    }

    fun commit() {
        val current = _state.value
        val dream = getDreams.byId(current.dreamId) ?: return
        updateDream(dream.copy(body = current.body.trim()))
        _state.update { it.copy(editing = false) }
    }

    fun askDelete() {
        _state.update { it.copy(askDelete = true) }
    }

    fun dismissDelete() {
        _state.update { it.copy(askDelete = false) }
    }

    fun confirmDelete() {
        deleteDream(_state.value.dreamId)
        _state.update { it.copy(askDelete = false) }
        viewModelScope.launch { removedChannel.send(Unit) }
    }
}
