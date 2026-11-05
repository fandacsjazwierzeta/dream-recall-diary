package com.dreamrecall.diary.domain.usecase

import com.dreamrecall.diary.domain.model.ConstellationEdge
import com.dreamrecall.diary.domain.model.ConstellationGraph
import com.dreamrecall.diary.domain.model.ConstellationNode
import com.dreamrecall.diary.domain.model.NightDates
import com.dreamrecall.diary.domain.repository.DreamRepository
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class BuildConstellationUseCase(private val repository: DreamRepository) {

    operator fun invoke(): ConstellationGraph {
        val dreams = repository.all()
            .filter { it.tags.isNotEmpty() }
            .sortedBy { it.createdAt }
        if (dreams.isEmpty()) return ConstellationGraph(emptyList(), emptyList())

        val raw = dreams.indices.map { index ->
            val angle = index * GOLDEN_ANGLE
            val radius = 34f * sqrt(index + 1f)
            Pair(cos(angle) * radius, sin(angle) * radius)
        }
        val minX = raw.minOf { it.first }
        val maxX = raw.maxOf { it.first }
        val minY = raw.minOf { it.second }
        val maxY = raw.maxOf { it.second }
        val spanX = if (maxX - minX > 1f) maxX - minX else 1f
        val spanY = if (maxY - minY > 1f) maxY - minY else 1f

        val nodes = dreams.mapIndexed { index, dream ->
            val point = raw[index]
            ConstellationNode(
                dreamId = dream.id,
                x = if (dreams.size == 1) 0.5f else (point.first - minX) / spanX,
                y = if (dreams.size == 1) 0.5f else (point.second - minY) / spanY,
                vividness = dream.vividness,
                dateLabel = NightDates.shortLabel(dream.nightDate),
                excerpt = dream.excerpt
            )
        }

        val edges = mutableListOf<ConstellationEdge>()
        for (i in dreams.indices) {
            val names = dreams[i].tags.map { it.name }.toSet()
            for (j in i + 1 until dreams.size) {
                val shared = dreams[j].tags.count { names.contains(it.name) }
                if (shared > 0) edges.add(ConstellationEdge(i, j, shared))
            }
        }
        return ConstellationGraph(nodes, edges)
    }

    private companion object {
        const val GOLDEN_ANGLE = 2.399963f
    }
}
