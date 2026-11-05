package com.dreamrecall.diary.core.di

import android.content.Context
import com.dreamrecall.diary.data.local.BackupCodec
import com.dreamrecall.diary.data.local.DreamStore
import com.dreamrecall.diary.data.local.LockStore
import com.dreamrecall.diary.data.local.SettingsStore
import com.dreamrecall.diary.data.repository.DreamRepositoryImpl
import com.dreamrecall.diary.data.repository.LockRepositoryImpl
import com.dreamrecall.diary.data.repository.SettingsRepositoryImpl
import com.dreamrecall.diary.data.sample.SampleData
import com.dreamrecall.diary.domain.repository.DreamRepository
import com.dreamrecall.diary.domain.repository.LockRepository
import com.dreamrecall.diary.domain.repository.SettingsRepository
import com.dreamrecall.diary.domain.usecase.BuildConstellationUseCase
import com.dreamrecall.diary.domain.usecase.BuildMonthUseCase
import com.dreamrecall.diary.domain.usecase.ComputeStreakUseCase
import com.dreamrecall.diary.domain.usecase.CountRecurringTagsUseCase
import com.dreamrecall.diary.domain.usecase.DeleteDreamUseCase
import com.dreamrecall.diary.domain.usecase.GetDreamsUseCase
import com.dreamrecall.diary.domain.usecase.SaveDreamUseCase
import com.dreamrecall.diary.domain.usecase.UpdateDreamUseCase

object ServiceLocator {

    private var dreamRepository: DreamRepository? = null
    private var settingsRepository: SettingsRepository? = null
    private var lockRepository: LockRepository? = null

    val dreams: DreamRepository get() = requireNotNull(dreamRepository)
    val settings: SettingsRepository get() = requireNotNull(settingsRepository)
    val lock: LockRepository get() = requireNotNull(lockRepository)

    val getDreams: GetDreamsUseCase by lazy { GetDreamsUseCase(dreams) }
    val saveDream: SaveDreamUseCase by lazy { SaveDreamUseCase(dreams) }
    val updateDream: UpdateDreamUseCase by lazy { UpdateDreamUseCase(dreams) }
    val deleteDream: DeleteDreamUseCase by lazy { DeleteDreamUseCase(dreams) }
    val countTags: CountRecurringTagsUseCase by lazy { CountRecurringTagsUseCase(dreams) }
    val buildConstellation: BuildConstellationUseCase by lazy { BuildConstellationUseCase(dreams) }
    val buildMonth: BuildMonthUseCase by lazy { BuildMonthUseCase(dreams) }
    val computeStreak: ComputeStreakUseCase by lazy { ComputeStreakUseCase(dreams) }
    val backupCodec: BackupCodec by lazy { BackupCodec() }

    fun start(context: Context) {
        if (dreamRepository != null) return
        val app = context.applicationContext
        val repository = DreamRepositoryImpl(DreamStore(app))
        val prefs = SettingsRepositoryImpl(SettingsStore(app))
        dreamRepository = repository
        settingsRepository = prefs
        lockRepository = LockRepositoryImpl(LockStore(app))
        if (!prefs.isSeeded()) {
            SampleData.starterDreams(System.currentTimeMillis()).forEach { repository.put(it) }
            prefs.markSeeded()
        }
    }
}
