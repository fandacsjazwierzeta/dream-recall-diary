package com.dreamrecall.diary.presentation.menu

data class MenuUiState(
    val dreamCount: Int = 0,
    val nightCount: Int = 0,
    val averageVividness: String = "",
    val hasVividness: Boolean = false,
    val lastDreamTitle: String = "",
    val isLoading: Boolean = true
) {
    val visibleChips: Int
        get() = listOf(dreamCount > 0, nightCount > 0, hasVividness).count { it }
}
