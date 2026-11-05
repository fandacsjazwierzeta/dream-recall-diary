package com.dreamrecall.diary.presentation.lock

data class LockUiState(
    val entered: String = "",
    val wrong: Boolean = false,
    val failedAttempts: Int = 0,
    val cooldownSeconds: Int = 0
) {
    val locked: Boolean get() = cooldownSeconds > 0
}
