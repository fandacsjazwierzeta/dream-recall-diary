package com.dreamrecall.diary.presentation.dreampage

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.NightRaised
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.TextBody
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.ui.AppIcons
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.GlassSurface
import com.dreamrecall.diary.core.ui.GlassTextField
import com.dreamrecall.diary.core.ui.PrimaryButton
import com.dreamrecall.diary.core.ui.ScreenHeader
import com.dreamrecall.diary.core.ui.SecondaryButton
import com.dreamrecall.diary.core.ui.TagOrb
import com.dreamrecall.diary.core.ui.VividnessStars
import com.dreamrecall.diary.core.ui.categoryColor
import com.dreamrecall.diary.domain.model.TagCategory
import com.dreamrecall.diary.presentation.dialog.ConfirmDialog

@Composable
fun DreamPageScreen(
    dreamId: String,
    onConstellation: () -> Unit,
    onBack: () -> Unit
) {
    val pageViewModel: DreamPageViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by pageViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(dreamId) {
        pageViewModel.load(dreamId)
    }
    LaunchedEffect(Unit) {
        pageViewModel.removedEvents.collect {
            onBack()
        }
    }

    AuroraBackground(
        overlay = listOf(NightDeep, NightRaised.copy(alpha = 0.92f)),
        blobs = 1
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_2),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 12.dp)
                .size(120.dp)
                .alpha(0.10f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = state.dateLabel,
                backDescription = stringResource(R.string.cd_back),
                subtitle = state.title,
                onBack = onBack,
                trailing = {
                    IconButton(onClick = pageViewModel::toggleEdit) {
                        Icon(
                            imageVector = AppIcons.Write,
                            contentDescription = stringResource(R.string.cd_edit),
                            tint = if (state.editing) Primary else TextMuted
                        )
                    }
                    IconButton(onClick = pageViewModel::askDelete) {
                        Icon(
                            imageVector = AppIcons.Delete,
                            contentDescription = stringResource(R.string.cd_delete),
                            tint = TextMuted
                        )
                    }
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                GlassSurface(cornerRadius = 24.dp, modifier = Modifier.fillMaxWidth()) {
                    if (state.editing) {
                        BasicTextField(
                            value = state.body,
                            onValueChange = pageViewModel::onBodyChange,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = TextBody,
                                fontSize = state.fontSizeSp.sp
                            ),
                            cursorBrush = SolidColor(Primary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = state.body,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = state.fontSizeSp.sp
                            ),
                            color = TextBody
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                VividnessStars(
                    value = state.vividness,
                    stateLabel = stringResource(R.string.vividness_state, state.vividness),
                    interactive = state.editing,
                    starDescriptions = (1..GameConfig.VIVIDNESS_MAX).map {
                        stringResource(R.string.cd_star, it)
                    },
                    onChange = pageViewModel::setVividness
                )
                if (state.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
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
                                    onRemove = if (state.editing) {
                                        { pageViewModel.removeTag(tag.name) }
                                    } else {
                                        null
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                if (state.editing) {
                    Spacer(modifier = Modifier.height(6.dp))
                    GlassTextField(
                        value = state.tagDraft,
                        onValueChange = pageViewModel::onTagDraftChange,
                        placeholder = stringResource(R.string.tag_placeholder)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SecondaryButton(
                            text = stringResource(R.string.cat_place),
                            onClick = { pageViewModel.addTag(TagCategory.PLACE) },
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.cat_person),
                            onClick = { pageViewModel.addTag(TagCategory.PERSON) },
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.cat_feeling),
                            onClick = { pageViewModel.addTag(TagCategory.FEELING) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                if (state.editing) {
                    PrimaryButton(
                        text = stringResource(R.string.cta_save_changes),
                        onClick = pageViewModel::commit,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    SecondaryButton(
                        text = stringResource(R.string.cta_see_constellation),
                        onClick = onConstellation,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (state.askDelete) {
            ConfirmDialog(
                title = stringResource(R.string.delete_title),
                message = stringResource(R.string.delete_message),
                confirmLabel = stringResource(R.string.action_delete),
                dismissLabel = stringResource(R.string.action_keep),
                onConfirm = pageViewModel::confirmDelete,
                onDismiss = pageViewModel::dismissDelete
            )
        }
    }
}
