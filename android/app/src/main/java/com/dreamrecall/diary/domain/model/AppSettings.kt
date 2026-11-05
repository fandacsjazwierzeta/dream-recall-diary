package com.dreamrecall.diary.domain.model

enum class BrightnessLevel(val overlayAlpha: Float) {
    SOFT(0f),
    DIM(0.18f),
    DARKEST(0.34f);

    companion object {
        fun of(index: Int): BrightnessLevel = entries[index.coerceIn(0, entries.lastIndex)]
    }
}

enum class FontSizeLevel(val dreamTextSp: Int) {
    SMALL(17),
    MEDIUM(19),
    LARGE(22);

    companion object {
        fun of(index: Int): FontSizeLevel = entries[index.coerceIn(0, entries.lastIndex)]
    }
}

data class AppSettings(
    val brightness: BrightnessLevel = BrightnessLevel.DIM,
    val fontSize: FontSizeLevel = FontSizeLevel.MEDIUM,
    val lockEnabled: Boolean = false
)
