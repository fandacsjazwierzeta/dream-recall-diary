package com.dreamrecall.diary.domain.model

data class ConstellationNode(
    val dreamId: String,
    val x: Float,
    val y: Float,
    val vividness: Int,
    val dateLabel: String,
    val excerpt: String
)

data class ConstellationEdge(
    val from: Int,
    val to: Int,
    val shared: Int
)

data class ConstellationGraph(
    val nodes: List<ConstellationNode>,
    val edges: List<ConstellationEdge>
)
