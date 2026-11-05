package com.dreamrecall.diary.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.TextPrimary

@Composable
fun TagOrb(
    label: String,
    dotColor: Color,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    index: Int = 0,
    removeDescription: String? = null,
    onRemove: (() -> Unit)? = null
) {
    var revealed by remember(label) { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(260, delayMillis = index * GameConfig.ORB_STAGGER_MS),
        label = "orbReveal"
    )
    LaunchedEffect(label, index) {
        revealed = true
    }
    val shape = RoundedCornerShape(18.dp)
    Surface(
        shape = shape,
        color = Primary.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.40f)),
        modifier = modifier
            .height(if (compact) 28.dp else 36.dp)
            .alpha(progress)
            .graphicsLayer {
                val scale = 0.8f + 0.2f * progress
                scaleX = scale
                scaleY = scale
            }
            .shadow(6.dp, shape, ambientColor = Primary, spotColor = Primary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 10.dp, end = if (onRemove == null) 12.dp else 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(dotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary
            )
            if (onRemove != null) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = AppIcons.Close,
                        contentDescription = removeDescription,
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
