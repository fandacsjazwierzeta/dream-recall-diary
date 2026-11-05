package com.dreamrecall.diary.domain.repository

import com.dreamrecall.diary.domain.model.DreamEntry

interface DreamRepository {
    fun all(): List<DreamEntry>
    fun byId(id: String): DreamEntry?
    fun put(entry: DreamEntry)
    fun remove(id: String)
    fun merge(entries: List<DreamEntry>): Int
    fun exportJson(): String
    fun parseJson(raw: String): List<DreamEntry>
}
