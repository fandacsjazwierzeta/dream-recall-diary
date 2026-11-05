package com.dreamrecall.diary.presentation.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.Danger
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.SurfaceSecondary
import com.dreamrecall.diary.core.theme.TextBody
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.GlassSurface
import com.dreamrecall.diary.core.ui.GlassTextField
import com.dreamrecall.diary.core.ui.PrimaryButton
import com.dreamrecall.diary.core.ui.ScreenHeader
import com.dreamrecall.diary.core.ui.SecondaryButton

@Composable
fun BackupScreen(onBack: () -> Unit) {
    val backupViewModel: BackupViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by backupViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        backupViewModel.refresh()
    }

    AuroraBackground(
        overlay = listOf(NightDeep, NightDeep.copy(alpha = 0.93f)),
        blobs = 2
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            ScreenHeader(
                title = stringResource(R.string.backup_title),
                backDescription = stringResource(R.string.cd_back),
                subtitle = stringResource(R.string.backup_subtitle),
                onBack = onBack
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.backup_export),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    GlassTextField(
                        value = state.exportPassphrase,
                        onValueChange = backupViewModel::onExportPassphraseChange,
                        placeholder = stringResource(R.string.backup_passphrase)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(
                        text = stringResource(R.string.cta_export),
                        onClick = backupViewModel::exportArchive,
                        enabled = state.canExport,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.exportCode.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.backup_count, state.dreamCount),
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassSurface(
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 16.dp,
                            color = SurfaceSecondary.copy(alpha = 0.80f)
                        ) {
                            SelectionContainer {
                                Text(
                                    text = state.exportCode,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextBody,
                                    maxLines = 6
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.backup_copy_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.backup_import),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    GlassTextField(
                        value = state.importCode,
                        onValueChange = backupViewModel::onImportCodeChange,
                        placeholder = stringResource(R.string.backup_paste),
                        singleLine = false,
                        minHeight = 96.dp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    GlassTextField(
                        value = state.importPassphrase,
                        onValueChange = backupViewModel::onImportPassphraseChange,
                        placeholder = stringResource(R.string.backup_passphrase)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryButton(
                        text = stringResource(R.string.cta_import),
                        onClick = backupViewModel::importArchive,
                        enabled = state.canImport,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (state.added >= 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(
                                R.string.backup_result,
                                state.added,
                                state.skipped
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                        )
                    }
                    if (state.failed) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.backup_error),
                            style = MaterialTheme.typography.labelSmall,
                            color = Danger,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                        )
                    }
                }

                if (state.working) {
                    LinearProgressIndicator(
                        color = Primary,
                        trackColor = SurfaceSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
