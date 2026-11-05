package com.dreamrecall.diary.presentation.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.BorderStrong
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.SurfaceSecondary
import com.dreamrecall.diary.core.theme.TextDim
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.GlassSurface
import com.dreamrecall.diary.core.ui.ScreenHeader
import com.dreamrecall.diary.core.ui.SecondaryButton
import com.dreamrecall.diary.core.ui.SegmentedSelector
import com.dreamrecall.diary.presentation.dialog.PinDialog

@Composable
fun SettingsScreen(
    onBackup: () -> Unit,
    onBack: () -> Unit
) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by settingsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingsViewModel.refresh()
    }

    val brightnessOptions = listOf(
        stringResource(R.string.bright_soft),
        stringResource(R.string.bright_dim),
        stringResource(R.string.bright_darkest)
    )
    val fontOptions = listOf(
        stringResource(R.string.font_small),
        stringResource(R.string.font_medium),
        stringResource(R.string.font_large)
    )

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
                title = stringResource(R.string.settings_title),
                backDescription = stringResource(R.string.cd_back),
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
                        text = stringResource(R.string.settings_brightness),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SegmentedSelector(
                        options = brightnessOptions,
                        selectedIndex = state.brightnessIndex,
                        stateLabel = brightnessOptions[state.brightnessIndex],
                        onSelect = settingsViewModel::setBrightness
                    )
                }
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.settings_font),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SegmentedSelector(
                        options = fontOptions,
                        selectedIndex = state.fontIndex,
                        stateLabel = fontOptions[state.fontIndex],
                        onSelect = settingsViewModel::setFontSize
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.settings_preview),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = state.previewSp.sp
                        ),
                        color = TextDim
                    )
                }
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.settings_require_pin),
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = state.lockEnabled,
                            onCheckedChange = settingsViewModel::onLockToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextPrimary,
                                checkedTrackColor = Primary,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SurfaceSecondary,
                                uncheckedBorderColor = BorderStrong
                            )
                        )
                    }
                    if (state.lockEnabled) {
                        TextButton(
                            onClick = settingsViewModel::requestPinChange,
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.cta_change_pin),
                                style = MaterialTheme.typography.labelLarge,
                                color = Primary
                            )
                        }
                    }
                }
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.settings_backup),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SecondaryButton(
                        text = stringResource(R.string.cta_open_backup),
                        onClick = onBackup,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                GlassSurface(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.sprite_5),
                            contentDescription = stringResource(R.string.cd_sleeping_eye),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.app_title),
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = stringResource(R.string.app_version),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.about_privacy),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (state.pinFlow != PinFlow.NONE) {
            PinDialog(
                title = when (state.pinFlow) {
                    PinFlow.SET -> stringResource(R.string.pin_set)
                    PinFlow.CONFIRM -> stringResource(R.string.pin_confirm)
                    else -> stringResource(R.string.pin_verify)
                },
                entered = state.pinEntered,
                wrong = state.pinWrong,
                wrongLabel = stringResource(R.string.lock_wrong),
                dotsStateLabel = stringResource(R.string.lock_entered, state.pinEntered.length),
                clearLabel = stringResource(R.string.key_clear),
                confirmDescription = stringResource(R.string.cd_confirm_pin),
                cancelLabel = stringResource(R.string.action_cancel),
                onDigit = settingsViewModel::pinDigit,
                onClear = settingsViewModel::pinClear,
                onConfirm = settingsViewModel::pinConfirm,
                onDismiss = settingsViewModel::dismissPinFlow
            )
        }
    }
}
