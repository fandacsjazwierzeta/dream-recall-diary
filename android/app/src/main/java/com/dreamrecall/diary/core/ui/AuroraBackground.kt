package com.dreamrecall.diary.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dreamrecall.diary.core.theme.Accent
import com.dreamrecall.diary.core.theme.AccentCool
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import kotlin.math.abs
import kotlin.random.Random

private class StarSpec(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val phase: Float
)

private fun starField(count: Int): List<StarSpec> {
    if (count <= 0) return emptyList()
    val random = Random(count * 7919)
    return List(count) {
        StarSpec(
            x = random.nextFloat(),
            y = random.nextFloat(),
            radius = 1f + random.nextFloat() * 1.5f,
            alpha = 0.25f + random.nextFloat() * 0.45f,
            phase = random.nextFloat()
        )
    }
}

@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    image: Painter? = null,
    overlay: List<Color> = listOf(NightDeep.copy(alpha = 0.70f), NightDeep),
    base: Color = NightDeep,
    blobs: Int = 2,
    stars: Int = 0,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val drift by transition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(5200, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift"
    )
    val counterDrift by transition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(6800, easing = LinearEasing), RepeatMode.Reverse),
        label = "counterDrift"
    )
    val twinkle by transition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Reverse),
        label = "twinkle"
    )
    val field = remember(stars) { starField(stars) }

    Box(modifier = modifier.fillMaxSize().background(base)) {
        if (image != null) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(brush = Brush.verticalGradient(overlay))
            if (blobs > 0) {
                val center = Offset(size.width * 0.24f, size.height * (0.15f + 0.06f * drift))
                val radius = size.minDimension * 0.62f
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Primary.copy(alpha = 0.22f), Color.Transparent),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }
            if (blobs > 1) {
                val center = Offset(size.width * (0.84f - 0.08f * counterDrift), size.height * 0.70f)
                val radius = size.minDimension * 0.58f
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Accent.copy(alpha = 0.14f), Color.Transparent),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }
            if (blobs > 2) {
                val center = Offset(size.width * 0.55f, size.height * (0.42f + 0.05f * drift))
                val radius = size.minDimension * 0.45f
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(AccentCool.copy(alpha = 0.10f), Color.Transparent),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
            }
            field.forEach { star ->
                val wave = abs(((twinkle + star.phase) % 1f) * 2f - 1f)
                drawCircle(
                    color = Color.White.copy(alpha = star.alpha * (0.4f + 0.6f * wave)),
                    radius = star.radius.dp.toPx(),
                    center = Offset(size.width * star.x, size.height * star.y)
                )
            }
        }
        content()
    }
}
