package com.dreamrecall.diary.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.navigation.Screen
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.presentation.archive.ArchiveScreen
import com.dreamrecall.diary.presentation.backup.BackupScreen
import com.dreamrecall.diary.presentation.calendar.CalendarScreen
import com.dreamrecall.diary.presentation.capture.CaptureScreen
import com.dreamrecall.diary.presentation.constellation.ConstellationScreen
import com.dreamrecall.diary.presentation.dreampage.DreamPageScreen
import com.dreamrecall.diary.presentation.gameover.ResultScreen
import com.dreamrecall.diary.presentation.lock.LockScreen
import com.dreamrecall.diary.presentation.menu.MenuScreen
import com.dreamrecall.diary.presentation.settings.SettingsScreen
import com.dreamrecall.diary.presentation.splash.SplashScreen
import com.dreamrecall.diary.presentation.tags.RecurringTagsScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NightDeep)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                fadeIn(tween(GameConfig.SCREEN_FADE_MS)) togetherWith
                    fadeOut(tween(GameConfig.SCREEN_FADE_MS))
            },
            label = "screenTransition"
        ) { target ->
            when (target) {
                Screen.Splash -> SplashScreen(
                    onFinished = appViewModel::onSplashFinished
                )

                Screen.Lock -> LockScreen(
                    onUnlocked = appViewModel::onUnlocked
                )

                Screen.Menu -> MenuScreen(
                    onCapture = { appViewModel.navigate(Screen.Capture) },
                    onArchive = { appViewModel.navigate(Screen.Archive()) },
                    onPatterns = { appViewModel.navigate(Screen.RecurringTags) },
                    onConstellation = { appViewModel.navigate(Screen.Constellation) },
                    onCalendar = { appViewModel.navigate(Screen.Calendar) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                Screen.Capture -> CaptureScreen(
                    onStored = appViewModel::openResult,
                    onBack = appViewModel::back
                )

                is Screen.Result -> ResultScreen(
                    dreamId = target.dreamId,
                    onDone = { appViewModel.replaceWith(Screen.DreamPage(it)) },
                    onWriteAgain = { appViewModel.replaceWith(Screen.Capture) },
                    onMenu = appViewModel::toMenu
                )

                is Screen.Archive -> ArchiveScreen(
                    tagFilter = target.tagFilter,
                    dateFilter = target.dateFilter,
                    onOpenDream = { appViewModel.navigate(Screen.DreamPage(it)) },
                    onCapture = { appViewModel.navigate(Screen.Capture) },
                    onBack = appViewModel::back
                )

                is Screen.DreamPage -> DreamPageScreen(
                    dreamId = target.dreamId,
                    onConstellation = { appViewModel.navigate(Screen.Constellation) },
                    onBack = appViewModel::back
                )

                Screen.Constellation -> ConstellationScreen(
                    onOpenDream = { appViewModel.navigate(Screen.DreamPage(it)) },
                    onBack = appViewModel::back
                )

                Screen.RecurringTags -> RecurringTagsScreen(
                    onOpenTag = { appViewModel.navigate(Screen.Archive(tagFilter = it)) },
                    onBack = appViewModel::back
                )

                Screen.Calendar -> CalendarScreen(
                    onOpenDate = { appViewModel.navigate(Screen.Archive(dateFilter = it)) },
                    onBack = appViewModel::back
                )

                Screen.Settings -> SettingsScreen(
                    onBackup = { appViewModel.navigate(Screen.Backup) },
                    onBack = appViewModel::back
                )

                Screen.Backup -> BackupScreen(
                    onBack = appViewModel::back
                )
            }
        }
    }
}
