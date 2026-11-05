package com.dreamrecall.diary.data.local

import android.content.Context
import com.dreamrecall.diary.domain.model.DreamEntry
import com.dreamrecall.diary.domain.model.Tag
import com.dreamrecall.diary.domain.model.TagCategory
import org.json.JSONArray
import org.json.JSONObject

class DreamStore(context: Context) {

    private val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun read(): List<DreamEntry> = decode(prefs.getString(KEY_DREAMS, null).orEmpty())

    fun write(entries: List<DreamEntry>) {
        prefs.edit().putString(KEY_DREAMS, encode(entries)).apply()
    }

    fun encode(entries: List<DreamEntry>): String {
        val array = JSONArray()
        entries.forEach { entry ->
            val tags = JSONArray()
            entry.tags.forEach { tag ->
                tags.put(
                    JSONObject()
                        .put("name", tag.name)
                        .put("category", tag.category.name)
                )
            }
            array.put(
                JSONObject()
                    .put("id", entry.id)
                    .put("createdAt", entry.createdAt)
                    .put("nightDate", entry.nightDate)
                    .put("title", entry.title)
                    .put("body", entry.body)
                    .put("vividness", entry.vividness)
                    .put("tags", tags)
            )
        }
        return array.toString()
    }

    fun decode(raw: String): List<DreamEntry> {
        if (raw.isBlank()) return emptyList()
        val parsed = runCatching { JSONArray(raw) }.getOrNull() ?: return emptyList()
        val result = mutableListOf<DreamEntry>()
        for (index in 0 until parsed.length()) {
            val node = parsed.optJSONObject(index) ?: continue
            val id = node.optString("id")
            if (id.isEmpty()) continue
            val tagArray = node.optJSONArray("tags") ?: JSONArray()
            val tags = mutableListOf<Tag>()
            for (t in 0 until tagArray.length()) {
                val tagNode = tagArray.optJSONObject(t) ?: continue
                val name = tagNode.optString("name")
                if (name.isEmpty()) continue
                tags.add(Tag(name, TagCategory.from(tagNode.optString("category"))))
            }
            result.add(
                DreamEntry(
                    id = id,
                    createdAt = node.optLong("createdAt"),
                    nightDate = node.optString("nightDate"),
                    title = node.optString("title"),
                    body = node.optString("body"),
                    vividness = node.optInt("vividness"),
                    tags = tags
                )
            )
        }
        return result
    }

    private companion object {
        const val FILE = "dream_recall_dreams"
        const val KEY_DREAMS = "dreams_json"
    }
}
