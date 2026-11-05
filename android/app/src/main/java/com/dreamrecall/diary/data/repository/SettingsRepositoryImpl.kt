package com.dreamrecall.diary.data.repository

import com.dreamrecall.diary.data.local.SettingsStore
import com.dreamrecall.diary.domain.model.AppSettings
import com.dreamrecall.diary.domain.model.BrightnessLevel
import com.dreamrecall.diary.domain.model.FontSizeLevel
import com.dreamrecall.diary.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val store: SettingsStore) : SettingsRepository {

    override fun load(): AppSettings = store.read()

    override fun setBrightness(level: BrightnessLevel) = store.writeBrightness(level)

    override fun setFontSize(level: FontSizeLevel) = store.writeFontSize(level)

    override fun setLockEnabled(enabled: Boolean) = store.writeLockEnabled(enabled)

    override fun readDraft(): String = store.readDraft()

    override fun writeDraft(text: String) = store.writeDraft(text)

    override fun isSeeded(): Boolean = store.seeded()

    override fun markSeeded() = store.markSeeded()
}
