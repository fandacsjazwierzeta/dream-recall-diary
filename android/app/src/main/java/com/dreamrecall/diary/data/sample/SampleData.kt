package com.dreamrecall.diary.data.sample

import com.dreamrecall.diary.domain.model.DreamEntry
import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.model.Tag
import com.dreamrecall.diary.domain.model.TagCategory

object SampleData {

    private const val DAY_MS = 86_400_000L

    fun starterDreams(now: Long): List<DreamEntry> = listOf(
        build(
            now, 1, "The staircase that kept adding floors",
            "I was climbing a stairwell in a building I used to know, and every time I reached a " +
                "landing another flight had appeared above it. My brother was two steps behind me " +
                "telling me not to look down, so of course I looked down.",
            4,
            listOf(
                Tag("stairwell", TagCategory.PLACE),
                Tag("my brother", TagCategory.PERSON),
                Tag("vertigo", TagCategory.FEELING)
            )
        ),
        build(
            now, 4, "Swimming through the old library",
            "The reading room was flooded to the height of the shelves and none of the books were " +
                "ruined. I swam between the stacks very slowly and the pages turned themselves as " +
                "I passed. It was completely quiet and I was not afraid of drowning.",
            5,
            listOf(
                Tag("library", TagCategory.PLACE),
                Tag("calm", TagCategory.FEELING)
            )
        ),
        build(
            now, 7, "Missed the last train again",
            "The platform clock kept resetting itself and the train was always leaving as I came " +
                "down the steps. I had a ticket in my hand that said a station I had never heard of.",
            3,
            listOf(
                Tag("station", TagCategory.PLACE),
                Tag("rush", TagCategory.FEELING)
            )
        ),
        build(
            now, 11, "Grandmother's kitchen, lit from outside",
            "Everything in the kitchen was exactly right except the light, which came in through " +
                "the window as if the garden were on fire very gently. She was making tea and did " +
                "not seem surprised that I was the age I am now.",
            5,
            listOf(
                Tag("kitchen", TagCategory.PLACE),
                Tag("grandmother", TagCategory.PERSON),
                Tag("warmth", TagCategory.FEELING)
            )
        ),
        build(
            now, 15, "A city where it was always just before dawn",
            "The sky never finished turning. I walked for what felt like hours past shuttered shops " +
                "and the light stayed at that one blue minute before sunrise the whole time.",
            4,
            listOf(
                Tag("city", TagCategory.PLACE),
                Tag("awe", TagCategory.FEELING)
            )
        ),
        build(
            now, 19, "The cat that spoke in my brother's voice",
            "A grey cat sat on the end of the bed and told me, in my brother's voice and with his " +
                "exact way of pausing, that I had forgotten something downstairs.",
            2,
            listOf(
                Tag("my brother", TagCategory.PERSON),
                Tag("unease", TagCategory.FEELING)
            )
        )
    )

    private fun build(
        now: Long,
        daysAgo: Int,
        title: String,
        body: String,
        vividness: Int,
        tags: List<Tag>
    ): DreamEntry {
        val stamp = now - daysAgo * DAY_MS
        return DreamEntry(
            id = "seed-" + daysAgo,
            createdAt = stamp,
            nightDate = NightDates.forMillis(stamp),
            title = title,
            body = body,
            vividness = vividness,
            tags = tags
        )
    }
}
