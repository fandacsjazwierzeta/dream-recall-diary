package com.dreamrecall.diary.data.local

import android.content.Context
import com.dreamrecall.diary.domain.model.AppSettings
import com.dreamrecall.diary.domain.model.BrightnessLevel
import com.dreamrecall.diary.domain.model.FontSizeLevel

class SettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun read(): AppSettings = AppSettings(
        brightness = BrightnessLevel.of(prefs.getInt(KEY_BRIGHTNESS, 1)),
        fontSize = FontSizeLevel.of(prefs.getInt(KEY_FONT, 1)),
        lockEnabled = prefs.getBoolean(KEY_LOCK, false)
    )

    fun writeBrightness(level: BrightnessLevel) {
        prefs.edit().putInt(KEY_BRIGHTNESS, level.ordinal).apply()
    }

    fun writeFontSize(level: FontSizeLevel) {
        prefs.edit().putInt(KEY_FONT, level.ordinal).apply()
    }

    fun writeLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_LOCK, enabled).apply()
    }

    fun readDraft(): String = prefs.getString(KEY_DRAFT, null).orEmpty()

    fun writeDraft(text: String) {
        prefs.edit().putString(KEY_DRAFT, text).apply()
    }

    fun seeded(): Boolean = prefs.getBoolean(KEY_SEEDED, false)

    fun markSeeded() {
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    private companion object {
        const val FILE = "dream_recall_settings"
        const val KEY_BRIGHTNESS = "brightness"
        const val KEY_FONT = "font_size"
        const val KEY_LOCK = "lock_enabled"
        const val KEY_DRAFT = "capture_draft"
        const val KEY_SEEDED = "seeded"
    }
}
