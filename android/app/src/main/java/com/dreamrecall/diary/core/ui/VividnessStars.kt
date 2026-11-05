package com.dreamrecall.diary.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.core.theme.BorderStrong
import com.dreamrecall.diary.core.theme.vividnessColor

@Composable
fun VividnessStars(
    value: Int,
    stateLabel: String,
    modifier: Modifier = Modifier,
    interactive: Boolean = true,
    starDescriptions: List<String> = emptyList(),
    onChange: (Int) -> Unit = {}
) {
    Row(
        modifier = modifier.semantics { stateDescription = stateLabel },
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 1..GameConfig.VIVIDNESS_MAX) {
            val selected = step <= value
            val scale by animateFloatAsState(
                targetValue = if (selected) 1f else 0.86f,
                animationSpec = spring(dampingRatio = 0.45f),
                label = "starScale"
            )
            IconButton(
                onClick = { onChange(step) },
                enabled = interactive,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Star,
                    contentDescription = starDescriptions.getOrNull(step - 1),
                    tint = if (selected) vividnessColor(step) else BorderStrong,
                    modifier = Modifier
                        .size(34.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
        }
    }
}
