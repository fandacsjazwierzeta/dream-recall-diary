package com.dreamrecall.diary.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.dreamrecall.diary.core.theme.BorderStrong
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.SurfaceSecondary
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary

@Composable
fun SegmentedSelector(
    options: List<String>,
    selectedIndex: Int,
    stateLabel: String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics { stateDescription = stateLabel },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, option ->
            val active = index == selectedIndex
            Surface(
                onClick = { onSelect(index) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (active) Primary.copy(alpha = 0.12f) else SurfaceSecondary.copy(alpha = 0.70f),
                border = BorderStroke(
                    width = if (active) 1.5.dp else 1.dp,
                    color = if (active) Primary else BorderStrong
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (active) TextPrimary else TextMuted
                    )
                }
            }
        }
    }
}
