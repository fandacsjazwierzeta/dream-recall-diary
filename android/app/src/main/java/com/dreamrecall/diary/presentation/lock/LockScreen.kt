package com.dreamrecall.diary.presentation.lock

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dreamrecall.diary.R
import com.dreamrecall.diary.core.di.AppViewModelFactory
import com.dreamrecall.diary.core.theme.Danger
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary
import com.dreamrecall.diary.core.ui.AppIcons
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.PinDots
import com.dreamrecall.diary.core.ui.PinKeypad

@Composable
fun LockScreen(onUnlocked: () -> Unit) {
    val lockViewModel: LockViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by lockViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        lockViewModel.unlockEvents.collect {
            onUnlocked()
        }
    }

    AuroraBackground(
        image = painterResource(R.drawable.bg_game),
        overlay = listOf(NightDeep.copy(alpha = 0.94f), NightDeep),
        blobs = 1,
        stars = 0
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = stringResource(R.string.cd_sleeping_eye),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(72.dp)
                    .alpha(0.85f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Icon(
                imageVector = AppIcons.Lock,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.lock_hint),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(22.dp))
            PinDots(
                filled = state.entered.length,
                wrong = state.wrong,
                stateLabel = if (state.wrong) {
                    stringResource(R.string.lock_wrong)
                } else {
                    stringResource(R.string.lock_entered, state.entered.length)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (state.locked) {
                Text(
                    text = stringResource(R.string.lock_cooldown, state.cooldownSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = Danger,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            } else if (state.wrong) {
                Text(
                    text = stringResource(R.string.lock_wrong),
                    style = MaterialTheme.typography.labelSmall,
                    color = Danger,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
            PinKeypad(
                clearLabel = stringResource(R.string.key_clear),
                confirmDescription = stringResource(R.string.cd_confirm_pin),
                enabled = !state.locked,
                onDigit = lockViewModel::press,
                onClear = lockViewModel::clear,
                onConfirm = lockViewModel::confirm
            )
        }
    }
}
