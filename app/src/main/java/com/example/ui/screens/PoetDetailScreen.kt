package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Poem
import com.example.model.PoetSummary
import com.example.ui.components.PoemCard
import com.example.ui.components.PoetInitialAvatar
import com.example.ui.theme.ChartPalette
import com.example.ui.theme.NotoSerifBengaliFamily
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoetDetailScreen(
    poetSummary: PoetSummary?,
    poems: List<Poem>,
    bookmarkedIds: Set<Int>,
    onBack: () -> Unit,
    onPoemClick: (Int) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGenre by remember { mutableStateOf<String?>(null) }

    val visiblePoems = remember(poems, selectedGenre) {
        if (selectedGenre == null) poems else poems.filter { it.genre == selectedGenre }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = poetSummary?.name ?: "",
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = NotoSerifBengaliFamily),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("poet_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.reader_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (poetSummary != null) {
                item(key = "poet_profile") {
                    PoetStylometryCard(poetSummary = poetSummary)
                }

                if (poetSummary.genreBreakdown.size > 1) {
                    item(key = "poet_genre_chips") {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item(key = "poet_genre_all") {
                                FilterChip(
                                    selected = selectedGenre == null,
                                    onClick = { selectedGenre = null },
                                    label = { Text(stringResource(R.string.explore_all_genres, poems.size)) }
                                )
                            }
                            items(poetSummary.genreBreakdown, key = { it.genre }) { item ->
                                FilterChip(
                                    selected = selectedGenre == item.genre,
                                    onClick = {
                                        selectedGenre = if (selectedGenre == item.genre) null else item.genre
                                    },
                                    label = { Text("${item.genre} (${item.count})") }
                                )
                            }
                        }
                    }
                }
            }

            item(key = "poet_works_header") {
                Text(
                    text = stringResource(R.string.poet_detail_poems_header, visiblePoems.size),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            items(visiblePoems, key = { it.id }) { poem ->
                PoemCard(
                    poem = poem,
                    isBookmarked = poem.id in bookmarkedIds,
                    onClick = { onPoemClick(poem.id) },
                    onToggleBookmark = { onToggleBookmark(poem.id) }
                )
            }

            item(key = "poet_bottom_spacer") {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PoetStylometryCard(poetSummary: PoetSummary) {
    val formatter = NumberFormat.getIntegerInstance()
    val ttrPercent = (poetSummary.avgLexicalDiversity * 100).roundToInt()
    val wplFormatted = String.format(Locale.US, "%.1f", poetSummary.avgWordsPerLine)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PoetInitialAvatar(poetName = poetSummary.name)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = stringResource(R.string.poet_detail_profile),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = poetSummary.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = NotoSerifBengaliFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniMetricPill(
                    label = stringResource(R.string.stylometry_metric_poems),
                    value = poetSummary.poemCount.toString(),
                    modifier = Modifier.weight(1f)
                )
                MiniMetricPill(
                    label = stringResource(R.string.reader_words),
                    value = formatter.format(poetSummary.totalWords),
                    modifier = Modifier.weight(1f)
                )
                MiniMetricPill(
                    label = stringResource(R.string.poet_detail_avg_ttr),
                    value = "$ttrPercent%",
                    modifier = Modifier.weight(1f)
                )
                MiniMetricPill(
                    label = stringResource(R.string.poet_detail_avg_wpl),
                    value = wplFormatted,
                    modifier = Modifier.weight(1f)
                )
            }

            if (poetSummary.genreBreakdown.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.poet_detail_genre_mix),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                val total = poetSummary.poemCount.coerceAtLeast(1).toFloat()
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    var startX = 0f
                    poetSummary.genreBreakdown.forEachIndexed { index, item ->
                        val segmentWidth = (item.count / total) * size.width
                        drawRect(
                            color = ChartPalette[index % ChartPalette.size],
                            topLeft = Offset(startX, 0f),
                            size = Size(segmentWidth, size.height)
                        )
                        startX += segmentWidth
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    poetSummary.genreBreakdown.forEachIndexed { index, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(10.dp),
                                shape = RoundedCornerShape(3.dp),
                                color = ChartPalette[index % ChartPalette.size]
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${item.genre}: ${item.count}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniMetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
