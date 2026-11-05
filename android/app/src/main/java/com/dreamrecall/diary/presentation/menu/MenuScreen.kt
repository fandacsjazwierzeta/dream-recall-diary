package com.dreamrecall.diary.presentation.menu

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.dreamrecall.diary.core.theme.Accent
import com.dreamrecall.diary.core.theme.BorderSoft
import com.dreamrecall.diary.core.theme.BorderStrong
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.SurfaceSheet
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary
import com.dreamrecall.diary.core.ui.AppIcons
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.PrimaryButton
import com.dreamrecall.diary.core.ui.SecondaryButton
import com.dreamrecall.diary.core.ui.StatCard

@Composable
fun MenuScreen(
    onCapture: () -> Unit,
    onArchive: () -> Unit,
    onPatterns: () -> Unit,
    onConstellation: () -> Unit,
    onCalendar: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }

    val transition = rememberInfiniteTransition(label = "menuDecor")
    val moonDrift by transition.animateFloat(
        -6f,
        6f,
        infiniteRepeatable(tween(5600, easing = LinearEasing), RepeatMode.Reverse),
        label = "moonDrift"
    )
    val clusterDrift by transition.animateFloat(
        6f,
        -6f,
        infiniteRepeatable(tween(7200, easing = LinearEasing), RepeatMode.Reverse),
        label = "clusterDrift"
    )

    AuroraBackground(
        image = painterResource(R.drawable.bg_game),
        overlay = listOf(
            NightDeep.copy(alpha = 0.33f),
            NightDeep.copy(alpha = 0.10f),
            NightDeep.copy(alpha = 0.93f)
        ),
        blobs = 2
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 96.dp, end = 28.dp)
                .offset(y = moonDrift.dp)
                .size(92.dp)
        )
        Image(
            painter = painterResource(R.drawable.sprite_2),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 190.dp, start = 24.dp)
                .offset(y = clusterDrift.dp)
                .size(64.dp)
                .alpha(0.8f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = SurfaceSheet.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, BorderSoft)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .background(BorderStrong, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.app_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.menu_tagline),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (state.visibleChips >= 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (state.dreamCount > 0) {
                                StatCard(
                                    icon = AppIcons.Entries,
                                    value = state.dreamCount.toString(),
                                    label = stringResource(R.string.stat_dreams),
                                    tint = Primary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (state.nightCount > 0) {
                                StatCard(
                                    icon = AppIcons.Calendar,
                                    value = state.nightCount.toString(),
                                    label = stringResource(R.string.stat_nights),
                                    tint = Accent,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (state.hasVividness) {
                                StatCard(
                                    icon = AppIcons.Star,
                                    value = state.averageVividness,
                                    label = stringResource(R.string.stat_vividness),
                                    tint = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.menu_empty),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = stringResource(R.string.menu_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    PrimaryButton(
                        text = stringResource(R.string.cta_capture),
                        onClick = onCapture,
                        icon = AppIcons.Write,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            text = stringResource(R.string.cta_archive),
                            onClick = onArchive,
                            icon = AppIcons.Entries,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = stringResource(R.string.cta_patterns),
                            onClick = onPatterns,
                            icon = AppIcons.Star,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = onConstellation, modifier = Modifier.height(48.dp)) {
                            Text(
                                text = stringResource(R.string.cta_constellation),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        TextButton(onClick = onCalendar, modifier = Modifier.height(48.dp)) {
                            Text(
                                text = stringResource(R.string.cta_calendar),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                        TextButton(onClick = onSettings, modifier = Modifier.height(48.dp)) {
                            Text(
                                text = stringResource(R.string.cta_settings),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
