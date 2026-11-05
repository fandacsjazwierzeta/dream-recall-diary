package com.dreamrecall.diary.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dreamrecall.diary.core.theme.Accent
import com.dreamrecall.diary.core.theme.TextDim
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary
import com.dreamrecall.diary.core.theme.vividnessColor
import com.dreamrecall.diary.domain.model.DreamEntry

@Composable
fun DreamCard(
    dream: DreamEntry,
    dateLabel: String,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    deleteDescription: String? = null,
    onDelete: (() -> Unit)? = null
) {
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(if (dream.tags.isEmpty()) 92.dp else 116.dp)
                    .background(vividnessColor(if (dream.vividness > 0) dream.vividness else 1))
            )
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.weight(1f)
                    )
                    if (dream.vividness > 0) {
                        Icon(
                            imageVector = AppIcons.Star,
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dream.vividness.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Accent
                        )
                    }
                    if (onDelete != null) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                            Icon(
                                imageVector = AppIcons.Delete,
                                contentDescription = deleteDescription,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = dream.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dream.excerpt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDim,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (dream.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        dream.tags.take(3).forEachIndexed { index, tag ->
                            TagOrb(
                                label = tag.name,
                                dotColor = categoryColor(tag.category),
                                compact = true,
                                index = index
                            )
                        }
                    }
                }
            }
        }
    }
}
