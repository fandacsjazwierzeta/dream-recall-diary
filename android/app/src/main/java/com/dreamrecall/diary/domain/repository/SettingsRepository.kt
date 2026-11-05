package com.dreamrecall.diary.domain.repository

import com.dreamrecall.diary.domain.model.AppSettings
import com.dreamrecall.diary.domain.model.BrightnessLevel
import com.dreamrecall.diary.domain.model.FontSizeLevel

interface SettingsRepository {
    fun load(): AppSettings
    fun setBrightness(level: BrightnessLevel)
    fun setFontSize(level: FontSizeLevel)
    fun setLockEnabled(enabled: Boolean)
    fun readDraft(): String
    fun writeDraft(text: String)
    fun isSeeded(): Boolean
    fun markSeeded()
}
