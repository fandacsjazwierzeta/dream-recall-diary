package com.dreamrecall.diary.presentation.constellation

import com.dreamrecall.diary.domain.model.ConstellationGraph

data class ConstellationUiState(
    val graph: ConstellationGraph = ConstellationGraph(emptyList(), emptyList()),
    val selectedIndex: Int = -1,
    val loaded: Boolean = false
) {
    val hasGraph: Boolean get() = graph.nodes.size >= 2
}
