package com.dreamrecall.diary.presentation.gameover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.Accent
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary
import com.dreamrecall.diary.core.ui.AppIcons
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.GlassTextField
import com.dreamrecall.diary.core.ui.PrimaryButton
import com.dreamrecall.diary.core.ui.SecondaryButton
import com.dreamrecall.diary.core.ui.SegmentedSelector
import com.dreamrecall.diary.core.ui.TagOrb
import com.dreamrecall.diary.core.ui.VividnessStars
import com.dreamrecall.diary.core.ui.categoryColor

@Composable
fun ResultScreen(
    dreamId: String,
    onDone: (String) -> Unit,
    onWriteAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val resultViewModel: ResultViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by resultViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(dreamId) {
        resultViewModel.load(dreamId)
    }

    val categories = listOf(
        stringResource(R.string.cat_place),
        stringResource(R.string.cat_person),
        stringResource(R.string.cat_feeling)
    )

    AuroraBackground(
        image = painterResource(R.drawable.bg_game),
        overlay = listOf(NightDeep.copy(alpha = 0.80f), NightDeep.copy(alpha = 0.96f)),
        blobs = 2
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = stringResource(R.string.cd_quill),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.result_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = state.dateLabel,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))
                VividnessStars(
                    value = state.vividness,
                    stateLabel = stringResource(R.string.vividness_state, state.vividness),
                    starDescriptions = (1..GameConfig.VIVIDNESS_MAX).map {
                        stringResource(R.string.cd_star, it)
                    },
                    onChange = resultViewModel::setVividness
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassTextField(
                        value = state.tagDraft,
                        onValueChange = resultViewModel::onTagDraftChange,
                        placeholder = stringResource(R.string.tag_placeholder),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = resultViewModel::commitTag) {
                        Icon(
                            imageVector = AppIcons.Add,
                            contentDescription = stringResource(R.string.cd_add_tag),
                            tint = Primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                SegmentedSelector(
                    options = categories,
                    selectedIndex = state.categoryIndex,
                    stateLabel = categories[state.categoryIndex],
                    onSelect = resultViewModel::setCategory
                )
                if (state.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    state.tags.chunked(2).forEachIndexed { rowIndex, rowTags ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTags.forEachIndexed { columnIndex, tag ->
                                TagOrb(
                                    label = tag.name,
                                    dotColor = categoryColor(tag.category),
                                    index = rowIndex * 2 + columnIndex,
                                    removeDescription = stringResource(R.string.cd_remove_tag),
                                    onRemove = { resultViewModel.removeTag(tag.name) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                if (state.suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.result_suggested),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    state.suggestions.chunked(3).forEach { rowNames ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowNames.forEach { name ->
                                TextButton(
                                    onClick = { resultViewModel.addTag(name) },
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Accent
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = stringResource(R.string.result_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            PrimaryButton(
                text = stringResource(R.string.cta_done),
                onClick = { onDone(state.dreamId) },
                enabled = state.loaded,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_write_again),
                onClick = onWriteAgain,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_home),
                    contentDescription = stringResource(R.string.cd_home),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.cta_menu),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
