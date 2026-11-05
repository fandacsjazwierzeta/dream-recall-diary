package com.dreamrecall.diary.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Lock : Screen
    data object Menu : Screen
    data object Capture : Screen
    data class Result(val dreamId: String) : Screen
    data class Archive(val tagFilter: String? = null, val dateFilter: String? = null) : Screen
    data class DreamPage(val dreamId: String) : Screen
    data object Constellation : Screen
    data object RecurringTags : Screen
    data object Calendar : Screen
    data object Settings : Screen
    data object Backup : Screen
}
