package com.dreamrecall.diary.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.core.theme.BorderStrong
import com.dreamrecall.diary.core.theme.Danger
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.SurfaceSecondary
import com.dreamrecall.diary.core.theme.TextFaint
import com.dreamrecall.diary.core.theme.TextPrimary

@Composable
fun PinDots(
    filled: Int,
    wrong: Boolean,
    stateLabel: String,
    modifier: Modifier = Modifier
) {
    val shake by animateFloatAsState(
        targetValue = if (wrong) 1f else 0f,
        animationSpec = tween(320),
        label = "pinShake"
    )
    Row(
        modifier = modifier
            .graphicsLayer { translationX = (1f - shake) * shake * 40f }
            .semantics { stateDescription = stateLabel },
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        for (index in 0 until GameConfig.PIN_LENGTH) {
            val active = index < filled
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(
                        color = when {
                            wrong -> Danger
                            active -> Primary
                            else -> Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .border(1.5.dp, if (wrong) Danger else BorderStrong, CircleShape)
            )
        }
    }
}

@Composable
fun PinKeypad(
    clearLabel: String,
    confirmDescription: String,
    enabled: Boolean,
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                row.forEach { digit ->
                    KeypadKey(enabled = enabled, onClick = { onDigit(digit) }) {
                        Text(
                            text = digit,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp),
                            color = if (enabled) TextPrimary else TextFaint
                        )
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            KeypadKey(enabled = enabled, onClick = onClear) {
                Text(
                    text = clearLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (enabled) TextPrimary else TextFaint
                )
            }
            KeypadKey(enabled = enabled, onClick = { onDigit("0") }) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp),
                    color = if (enabled) TextPrimary else TextFaint
                )
            }
            KeypadKey(enabled = enabled, onClick = onConfirm) {
                Icon(
                    imageVector = AppIcons.Check,
                    contentDescription = confirmDescription,
                    tint = if (enabled) Primary else TextFaint,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun KeypadKey(
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = SurfaceSecondary.copy(alpha = if (enabled) 0.80f else 0.35f),
        border = BorderStroke(1.dp, BorderStrong),
        modifier = Modifier.size(72.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}
