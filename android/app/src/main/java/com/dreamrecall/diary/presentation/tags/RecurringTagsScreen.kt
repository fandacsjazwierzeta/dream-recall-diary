package com.dreamrecall.diary.presentation.tags

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
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
import com.dreamrecall.diary.core.theme.NightDeep
import com.dreamrecall.diary.core.theme.Primary
import com.dreamrecall.diary.core.theme.SurfaceSecondary
import com.dreamrecall.diary.core.theme.TextMuted
import com.dreamrecall.diary.core.theme.TextPrimary
import com.dreamrecall.diary.core.ui.AuroraBackground
import com.dreamrecall.diary.core.ui.GlassSurface
import com.dreamrecall.diary.core.ui.ScreenHeader
import com.dreamrecall.diary.core.ui.categoryColor
import com.dreamrecall.diary.domain.model.TagCategory
import com.dreamrecall.diary.domain.model.TagCount

@Composable
fun RecurringTagsScreen(
    onOpenTag: (String) -> Unit,
    onBack: () -> Unit
) {
    val tagsViewModel: RecurringTagsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by tagsViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        tagsViewModel.refresh()
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
                title = stringResource(R.string.patterns_title),
                backDescription = stringResource(R.string.cd_back),
                onBack = onBack
            )
            if (state.isEmpty && state.loaded) {
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
                            .alpha(0.55f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.patterns_empty),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.sections.forEach { section ->
                        item(key = section.category.name) {
                            Text(
                                text = sectionTitle(section.category),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                            )
                        }
                        items(
                            count = section.items.size,
                            key = { index -> section.category.name + section.items[index].name }
                        ) { index ->
                            TagRow(
                                item = section.items[index],
                                maxCount = state.maxCount,
                                onClick = { onOpenTag(section.items[index].name) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun sectionTitle(category: TagCategory): String = when (category) {
    TagCategory.PLACE -> stringResource(R.string.patterns_places)
    TagCategory.PERSON -> stringResource(R.string.patterns_people)
    TagCategory.FEELING -> stringResource(R.string.patterns_feelings)
}

@Composable
private fun TagRow(
    item: TagCount,
    maxCount: Int,
    onClick: () -> Unit
) {
    GlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(categoryColor(item.category), CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = item.count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = Accent
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val fraction = (item.count.toFloat() / maxCount.coerceAtLeast(1)).coerceIn(0.08f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(SurfaceSecondary, RoundedCornerShape(3.dp))
            )
            Box(
                modifier = Modifier
                    .width(maxWidth * fraction)
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Primary, Accent)),
                        RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}
