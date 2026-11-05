package com.dreamrecall.diary.data.repository

import com.dreamrecall.diary.data.local.DreamStore
import com.dreamrecall.diary.domain.model.DreamEntry
import com.dreamrecall.diary.domain.repository.DreamRepository

class DreamRepositoryImpl(private val store: DreamStore) : DreamRepository {

    private var cache: List<DreamEntry>? = null

    private fun snapshot(): List<DreamEntry> {
        val current = cache ?: store.read()
        cache = current
        return current
    }

    private fun commit(entries: List<DreamEntry>) {
        cache = entries
        store.write(entries)
    }

    override fun all(): List<DreamEntry> = snapshot()

    override fun byId(id: String): DreamEntry? = snapshot().firstOrNull { it.id == id }

    override fun put(entry: DreamEntry) {
        val rest = snapshot().filterNot { it.id == entry.id }
        commit(rest + entry)
    }

    override fun remove(id: String) {
        commit(snapshot().filterNot { it.id == id })
    }

    override fun merge(entries: List<DreamEntry>): Int {
        val existing = snapshot().associateBy { it.id }
        val fresh = entries.filterNot { existing.containsKey(it.id) }
        if (fresh.isNotEmpty()) commit(snapshot() + fresh)
        return fresh.size
    }

    override fun exportJson(): String = store.encode(snapshot())

    override fun parseJson(raw: String): List<DreamEntry> = store.decode(raw)
}
