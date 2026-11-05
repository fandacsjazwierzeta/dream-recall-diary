package com.dreamrecall.diary.presentation.archive

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.ui.AppIcons
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.DreamCard
import com.dreamrecall.diary.core.ui.GlassTextField
import com.dreamrecall.diary.core.ui.PrimaryButton
import com.dreamrecall.diary.core.ui.ScreenHeader
import com.dreamrecall.diary.core.ui.TagOrb
import com.dreamrecall.diary.presentation.dialog.ConfirmDialog

@Composable
fun ArchiveScreen(
    tagFilter: String?,
    dateFilter: String?,
    onOpenDream: (String) -> Unit,
    onCapture: () -> Unit,
    onBack: () -> Unit
) {
    val archiveViewModel: ArchiveViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by archiveViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(tagFilter, dateFilter) {
        archiveViewModel.open(tagFilter, dateFilter)
    }

    AuroraBackground(
        overlay = listOf(NightDeep, NightDeep.copy(alpha = 0.92f)),
        blobs = 2
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.archive_title),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack,
                trailing = {
                    IconButton(onClick = archiveViewModel::toggleSearch) {
                        Icon(
                            imageVector = AppIcons.Search,
                            contentDescription = stringResource(R.string.cd_search),
                            tint = if (state.searchOpen) Primary else TextMuted
                        )
                    }
                }
            )
            if (state.searchOpen) {
                GlassTextField(
                    value = state.query,
                    onValueChange = archiveViewModel::onQueryChange,
                    placeholder = stringResource(R.string.archive_search),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            val activeFilter = state.tagFilter ?: state.dateFilter
            if (activeFilter != null) {
                TagOrb(
                    label = activeFilter,
                    dotColor = Primary,
                    compact = true,
                    removeDescription = stringResource(R.string.cd_remove_filter),
                    onRemove = archiveViewModel::clearFilters,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
            }
            if (state.rows.isEmpty() && state.loaded) {
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
                            .alpha(0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.archive_empty),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PrimaryButton(
                        text = stringResource(R.string.cta_capture),
                        onClick = onCapture,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = state.rows, key = { it.dream.id }) { row ->
                        DreamCard(
                            dream = row.dream,
                            dateLabel = row.dateLabel,
                            onOpen = { onOpenDream(row.dream.id) },
                            deleteDescription = stringResource(R.string.cd_delete),
                            onDelete = { archiveViewModel.askDelete(row.dream.id) }
                        )
                    }
                }
            }
        }

        if (state.pendingDelete != null) {
            ConfirmDialog(
                title = stringResource(R.string.delete_title),
                message = stringResource(R.string.delete_message),
                confirmLabel = stringResource(R.string.action_delete),
                dismissLabel = stringResource(R.string.action_keep),
                onConfirm = archiveViewModel::confirmDelete,
                onDismiss = archiveViewModel::dismissDelete
            )
        }
    }
}
