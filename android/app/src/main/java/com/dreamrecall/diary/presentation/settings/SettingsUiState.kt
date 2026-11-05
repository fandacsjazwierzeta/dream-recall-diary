package com.dreamrecall.diary.presentation.settings

enum class PinFlow {
    NONE,
    SET,
    CONFIRM,
    VERIFY_OFF,
    VERIFY_CHANGE
}

data class SettingsUiState(
    val brightnessIndex: Int = 1,
    val fontIndex: Int = 1,
    val previewSp: Int = 19,
    val lockEnabled: Boolean = false,
    val pinFlow: PinFlow = PinFlow.NONE,
    val pinEntered: String = "",
    val pinWrong: Boolean = false,
    val loaded: Boolean = false
)
