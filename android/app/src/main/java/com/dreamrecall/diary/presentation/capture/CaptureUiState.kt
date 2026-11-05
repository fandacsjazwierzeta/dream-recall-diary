package com.dreamrecall.diary.presentation.capture

data class CaptureUiState(
    val body: String = "",
    val wordCount: Int = 0,
    val nightLabel: String = "",
    val dimmerStep: Int = 1,
    val overlayAlpha: Float = 0.18f,
    val askClear: Boolean = false,
    val emptyWarning: Boolean = false
) {
    val canStore: Boolean get() = body.isNotBlank()
}
