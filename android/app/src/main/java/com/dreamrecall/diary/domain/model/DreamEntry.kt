package com.dreamrecall.diary.domain.model

data class DreamEntry(
    val id: String,
    val createdAt: Long,
    val nightDate: String,
    val title: String,
    val body: String,
    val vividness: Int,
    val tags: List<Tag>
) {
    val wordCount: Int
        get() = body.split(' ', '\n', '\t').count { it.isNotBlank() }

    val excerpt: String
        get() = body.trim().replace('\n', ' ').take(140)
}
