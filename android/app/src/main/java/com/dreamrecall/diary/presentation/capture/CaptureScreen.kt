package com.dreamrecall.diary.presentation.capture

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.config.GameConfig
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.BorderSoft
import com.dreamrecall.diary.core.theme.BorderStrong
import com.dreamrecall.diary.core.theme.Danger
import com.dreamrecall.diary.core.theme.NightAbyss
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.NightRaised
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.SurfaceSecondary
import com.dreamrecall.diary.core.theme.TextDim
import com.dreamrecall.diary.core.theme.TextFaint
import com.dreamrecall.diary.core.ui.AppIcons
import com.dreamrecall.diary.core.ui.PrimaryButton
import com.dreamrecall.diary.presentation.dialog.ConfirmDialog

private val DeepBreath = Color(0xFF1A1440)
private val Placeholder = Color(0xFF514A78)

@Composable
fun CaptureScreen(
    onStored: (String) -> Unit,
    onBack: () -> Unit
) {
    val captureViewModel: CaptureViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by captureViewModel.state.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        captureViewModel.begin()
        runCatching { focusRequester.requestFocus() }
    }
    LaunchedEffect(Unit) {
        captureViewModel.storedEvents.collect { id ->
            onStored(id)
        }
    }
    DisposableEffect(Unit) {
        onDispose { captureViewModel.pause() }
    }

    val transition = rememberInfiniteTransition(label = "breathe")
    val breath by transition.animateFloat(
        0f,
        1f,
        infiniteRepeatable(
            tween(GameConfig.BREATH_CYCLE_MS, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "breathValue"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        lerp(NightDeep, NightRaised, breath),
                        lerp(NightDeep, DeepBreath, breath),
                        NightAbyss
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = AppIcons.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = TextFaint
                    )
                }
                Text(
                    text = stringResource(R.string.capture_night, state.nightLabel),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextFaint,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.capture_words, state.wordCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextFaint,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                if (state.body.isEmpty()) {
                    Text(
                        text = stringResource(R.string.capture_placeholder),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Placeholder
                    )
                }
                BasicTextField(
                    value = state.body,
                    onValueChange = captureViewModel::onBodyChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDim),
                    cursorBrush = SolidColor(Primary),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                )
            }
            Spacer(modifier = Modifier.height(148.dp))
        }

        if (state.overlayAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = state.overlayAlpha))
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.emptyWarning) {
                Text(
                    text = stringResource(R.string.capture_empty),
                    style = MaterialTheme.typography.labelSmall,
                    color = Danger,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            Text(
                text = stringResource(R.string.capture_dimmer),
                style = MaterialTheme.typography.labelSmall,
                color = TextFaint
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                for (step in 0..2) {
                    val active = step == state.dimmerStep
                    IconButton(onClick = { captureViewModel.setDimmer(step) }) {
                        Box(
                            modifier = Modifier
                                .size((12 + step * 4).dp)
                                .background(
                                    color = if (active) Primary else BorderStrong,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SurfaceSecondary.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, BorderSoft),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = captureViewModel::askClear,
                        enabled = state.canStore
                    ) {
                        Icon(
                            imageVector = AppIcons.Refresh,
                            contentDescription = stringResource(R.string.cd_clear_draft),
                            tint = if (state.canStore) TextDim else TextFaint
                        )
                    }
                    IconButton(onClick = onBack) {
                        Image(
                            painter = painterResource(R.drawable.icon_pause),
                            contentDescription = stringResource(R.string.cd_keep_draft),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    PrimaryButton(
                        text = stringResource(R.string.cta_save),
                        onClick = captureViewModel::store,
                        icon = AppIcons.Check,
                        compact = true,
                        modifier = Modifier.width(132.dp)
                    )
                }
            }
        }

        if (state.askClear) {
            ConfirmDialog(
                title = stringResource(R.string.clear_title),
                message = stringResource(R.string.clear_message),
                confirmLabel = stringResource(R.string.action_clear),
                dismissLabel = stringResource(R.string.action_keep),
                onConfirm = captureViewModel::confirmClear,
                onDismiss = captureViewModel::dismissClear
            )
        }
    }
}
