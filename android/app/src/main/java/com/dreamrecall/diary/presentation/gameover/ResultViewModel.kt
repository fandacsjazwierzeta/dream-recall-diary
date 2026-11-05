package com.dreamrecall.diary.presentation.gameover

import androidx.lifecycle.ViewModel
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.model.Tag
import com.dreamrecall.diary.domain.model.TagCategory
import com.dreamrecall.diary.domain.usecase.CountRecurringTagsUseCase
import com.dreamrecall.diary.domain.usecase.GetDreamsUseCase
import com.dreamrecall.diary.domain.usecase.UpdateDreamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ResultViewModel(
    private val getDreams: GetDreamsUseCase,
    private val updateDream: UpdateDreamUseCase,
    private val countTags: CountRecurringTagsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ResultUiState())
    val state: StateFlow<ResultUiState> = _state.asStateFlow()

    fun load(dreamId: String) {
        val dream = getDreams.byId(dreamId) ?: return
        val taken = dream.tags.map { it.name }.toSet()
        _state.value = ResultUiState(
            dreamId = dream.id,
            dateLabel = NightDates.shortLabel(dream.nightDate),
            title = dream.title,
            vividness = dream.vividness,
            tags = dream.tags,
            suggestions = countTags.mostFrequent(GameConfig.SUGGESTED_TAGS * 2)
                .map { it.name }
                .filterNot { taken.contains(it) }
                .take(GameConfig.SUGGESTED_TAGS),
            loaded = true
        )
    }

    fun setVividness(value: Int) {
        val dream = getDreams.byId(_state.value.dreamId) ?: return
        updateDream(dream.copy(vividness = value))
        _state.update { it.copy(vividness = value) }
    }

    fun onTagDraftChange(text: String) {
        _state.update { it.copy(tagDraft = text.take(GameConfig.MAX_TAG_CHARS)) }
    }

    fun setCategory(index: Int) {
        _state.update { it.copy(categoryIndex = index.coerceIn(0, TagCategory.entries.lastIndex)) }
    }

    fun commitTag() {
        addTag(_state.value.tagDraft)
    }

    fun addTag(raw: String) {
        val dream = getDreams.byId(_state.value.dreamId) ?: return
        val category = TagCategory.entries[_state.value.categoryIndex]
        val updated = updateDream.addTag(dream, raw, Tag(raw, category))
        if (updated.tags.size == dream.tags.size) {
            _state.update { it.copy(tagDraft = "") }
            return
        }
        updateDream(updated)
        _state.update { current ->
            current.copy(
                tags = updated.tags,
                tagDraft = "",
                suggestions = current.suggestions.filterNot { name ->
                    updated.tags.any { it.name == name }
                }
            )
        }
    }

    fun removeTag(name: String) {
        val dream = getDreams.byId(_state.value.dreamId) ?: return
        val updated = updateDream.removeTag(dream, name)
        updateDream(updated)
        _state.update { it.copy(tags = updated.tags) }
    }
}
