package com.dreamrecall.diary.presentation.constellation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.Accent
import com.dreamrecall.diary.core.theme.NightAbyss
import com.dreamrecall.diary.core.theme.NightRaised
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.TextBody
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.vividnessColor
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.GlassSurface
import com.dreamrecall.diary.core.ui.PrimaryButton
import com.dreamrecall.diary.core.ui.ScreenHeader
import com.dreamrecall.diary.domain.model.ConstellationNode

private const val MIN_ZOOM = 0.6f
private const val MAX_ZOOM = 2.6f

private fun project(
    node: ConstellationNode,
    size: Size,
    pad: Float,
    zoom: Float,
    pan: Offset
): Offset {
    val baseX = pad + node.x * (size.width - 2 * pad)
    val baseY = pad + node.y * (size.height - 2 * pad)
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    return Offset(
        centerX + (baseX - centerX) * zoom + pan.x,
        centerY + (baseY - centerY) * zoom + pan.y
    )
}

@Composable
fun ConstellationScreen(
    onOpenDream: (String) -> Unit,
    onBack: () -> Unit
) {
    val constellationViewModel: ConstellationViewModel =
        viewModel(factory = AppViewModelFactory.factory)
    val state by constellationViewModel.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        constellationViewModel.refresh()
    }

    var zoom by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    val edgeAlpha by animateFloatAsState(
        targetValue = if (state.loaded) 1f else 0f,
        animationSpec = tween(GameConfig.EDGE_FADE_MS),
        label = "edgeFade"
    )

    AuroraBackground(
        overlay = listOf(NightAbyss, NightRaised.copy(alpha = 0.90f)),
        blobs = 1,
        stars = GameConfig.CONSTELLATION_STARS
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.constellation_title),
                backDescription = stringResource(R.string.cd_back),
                subtitle = stringResource(
                    R.string.constellation_legend,
                    state.graph.nodes.size,
                    state.graph.edges.size
                ),
                onBack = onBack
            )
            if (!state.hasGraph && state.loaded) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.sprite_4),
                        contentDescription = stringResource(R.string.cd_star_node),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(96.dp)
                            .alpha(0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.constellation_thin),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    val padPx = with(LocalDensity.current) {
                        48.dp.toPx()
                    }
                    val hitPx = with(LocalDensity.current) {
                        24.dp.toPx()
                    }
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(state.graph) {
                                detectTapGestures { tap ->
                                    val hit = state.graph.nodes.indexOfFirst { node ->
                                        (project(node, size.toSize(), padPx, zoom, pan) - tap)
                                            .getDistance() < hitPx
                                    }
                                    if (hit >= 0) {
                                        haptics.performHapticFeedback(
                                            HapticFeedbackType.LongPress
                                        )
                                        constellationViewModel.select(hit)
                                    } else {
                                        constellationViewModel.clearSelection()
                                    }
                                }
                            }
                            .pointerInput(state.graph) {
                                detectTransformGestures { _, gesturePan, gestureZoom, _ ->
                                    zoom = (zoom * gestureZoom).coerceIn(MIN_ZOOM, MAX_ZOOM)
                                    val limit = size.width * 0.6f
                                    pan = Offset(
                                        (pan.x + gesturePan.x).coerceIn(-limit, limit),
                                        (pan.y + gesturePan.y).coerceIn(-limit, limit)
                                    )
                                }
                            }
                    ) {
                        val nodes = state.graph.nodes
                        state.graph.edges.forEach { edge ->
                            val from = project(nodes[edge.from], size, padPx, zoom, pan)
                            val to = project(nodes[edge.to], size, padPx, zoom, pan)
                            drawLine(
                                color = Primary.copy(
                                    alpha = (0.18f + 0.10f * edge.shared) * edgeAlpha
                                ),
                                start = from,
                                end = to,
                                strokeWidth = 1f + 0.8f * edge.shared
                            )
                        }
                        nodes.forEachIndexed { index, node ->
                            val center = project(node, size, padPx, zoom, pan)
                            val radius = (7f + node.vividness * 1.6f) * zoom
                            val tint = vividnessColor(
                                if (node.vividness > 0) node.vividness else 1
                            )
                            drawCircle(
                                color = tint.copy(alpha = 0.18f),
                                radius = radius * 2.4f,
                                center = center
                            )
                            drawCircle(color = tint, radius = radius, center = center)
                            if (index == state.selectedIndex) {
                                drawCircle(
                                    color = Accent,
                                    radius = radius * 1.8f,
                                    center = center,
                                    style = Stroke(width = 3f)
                                )
                            }
                        }
                    }

                    val selected = state.graph.nodes.getOrNull(state.selectedIndex)
                    if (selected != null) {
                        GlassSurface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp),
                            cornerRadius = 24.dp
                        ) {
                            Text(
                                text = selected.dateLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = selected.excerpt,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextBody
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PrimaryButton(
                                text = stringResource(R.string.cta_open_dream),
                                onClick = { onOpenDream(selected.dreamId) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
