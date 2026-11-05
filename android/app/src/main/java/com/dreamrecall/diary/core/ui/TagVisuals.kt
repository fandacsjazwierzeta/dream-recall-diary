package com.dreamrecall.diary.core.ui

import androidx.compose.ui.graphics.Color
import com.dreamrecall.diary.core.theme.Accent
import com.dreamrecall.diary.core.theme.AccentCool
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.domain.model.TagCategory

fun categoryColor(category: TagCategory): Color = when (category) {
    TagCategory.PLACE -> Primary
    TagCategory.PERSON -> Accent
    TagCategory.FEELING -> AccentCool
}
