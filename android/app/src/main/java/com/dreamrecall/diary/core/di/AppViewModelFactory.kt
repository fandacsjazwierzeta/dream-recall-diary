package com.dreamrecall.diary.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.dreamrecall.diary.presentation.app.AppViewModel
import com.dreamrecall.diary.presentation.archive.ArchiveViewModel
import com.dreamrecall.diary.presentation.backup.BackupViewModel
import com.dreamrecall.diary.presentation.calendar.CalendarViewModel
import com.dreamrecall.diary.presentation.capture.CaptureViewModel
import com.dreamrecall.diary.presentation.constellation.ConstellationViewModel
import com.dreamrecall.diary.presentation.dreampage.DreamPageViewModel
import com.dreamrecall.diary.presentation.gameover.ResultViewModel
import com.dreamrecall.diary.presentation.lock.LockViewModel
import com.dreamrecall.diary.presentation.menu.MenuViewModel
import com.dreamrecall.diary.presentation.settings.SettingsViewModel
import com.dreamrecall.diary.presentation.splash.SplashViewModel
import com.dreamrecall.diary.presentation.tags.RecurringTagsViewModel

object AppViewModelFactory {

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { AppViewModel(ServiceLocator.lock) }
        initializer { SplashViewModel() }
        initializer { LockViewModel(ServiceLocator.lock) }
        initializer { MenuViewModel(ServiceLocator.getDreams) }
        initializer {
            CaptureViewModel(ServiceLocator.saveDream, ServiceLocator.settings)
        }
        initializer {
            ResultViewModel(
                ServiceLocator.getDreams,
                ServiceLocator.updateDream,
                ServiceLocator.countTags
            )
        }
        initializer {
            ArchiveViewModel(ServiceLocator.getDreams, ServiceLocator.deleteDream)
        }
        initializer {
            DreamPageViewModel(
                ServiceLocator.getDreams,
                ServiceLocator.updateDream,
                ServiceLocator.deleteDream,
                ServiceLocator.settings
            )
        }
        initializer { ConstellationViewModel(ServiceLocator.buildConstellation) }
        initializer { RecurringTagsViewModel(ServiceLocator.countTags) }
        initializer {
            CalendarViewModel(ServiceLocator.buildMonth, ServiceLocator.computeStreak)
        }
        initializer {
            SettingsViewModel(ServiceLocator.settings, ServiceLocator.lock)
        }
        initializer { BackupViewModel(ServiceLocator.dreams, ServiceLocator.backupCodec) }
    }
}
