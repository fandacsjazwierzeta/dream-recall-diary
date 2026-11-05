package com.dreamrecall.diary.domain.model

enum class TagCategory {
    PLACE,
    PERSON,
    FEELING;

    companion object {
        fun from(raw: String): TagCategory = entries.firstOrNull { it.name == raw } ?: FEELING
    }
}
