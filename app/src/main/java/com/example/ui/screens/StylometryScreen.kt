package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Spellcheck
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CorpusStats
import com.example.model.PoetSummary
import com.example.ui.theme.ChartPalette
import com.example.ui.theme.NotoSansBengaliFamily
import com.example.ui.theme.NotoSerifBengaliFamily
import com.example.ui.theme.PlayfairDisplayFamily
import java.text.NumberFormat
import kotlin.math.roundToInt

@Composable
fun StylometryScreen(
    stats: CorpusStats,
    onGenreClick: (String) -> Unit,
    onPoetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getIntegerInstance()
    val uriHandler = LocalUriHandler.current
    var leaderboardTab by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("stylometry_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "stylometry_banner") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(156.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_stylometry_banner),
                        contentDescription = stringResource(R.string.cd_stylometry_banner),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x55171311),
                                        Color(0xDF171311)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.stylometry_title),
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontFamily = PlayfairDisplayFamily
                            ),
                            color = Color(0xFFFDF8F2)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.stylometry_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFEBDCD0)
                        )
                    }
                }
            }
        }

        item(key = "kpi_grid") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CorpusKpiCard(
                        icon = Icons.Rounded.Book,
                        label = stringResource(R.string.stylometry_metric_poems),
                        value = formatter.format(stats.totalPoems),
                        modifier = Modifier.weight(1f)
                    )
                    CorpusKpiCard(
                        icon = Icons.Rounded.Groups,
                        label = stringResource(R.string.stylometry_metric_poets),
                        value = formatter.format(stats.totalPoets),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CorpusKpiCard(
                        icon = Icons.Rounded.Analytics,
                        label = stringResource(R.string.stylometry_metric_words),
                        value = formatter.format(stats.totalWords),
                        modifier = Modifier.weight(1f)
                    )
                    CorpusKpiCard(
                        icon = Icons.Rounded.Spellcheck,
                        label = stringResource(R.string.stylometry_metric_vocab),
                        value = formatter.format(stats.uniqueCorpusWords),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item(key = "genre_distribution") {
            val maxCount = (stats.genreDistribution.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.stylometry_genre_dist_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.stylometry_genre_dist_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        stats.genreDistribution.forEachIndexed { index, item ->
                            val ratio = item.count.toFloat() / maxCount.toFloat()
                            val barColor = ChartPalette[index % ChartPalette.size]

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onGenreClick(item.genre) }
                                    .padding(vertical = 4.dp, horizontal = 2.dp)
                                    .testTag("stylometry_genre_bar_${item.genre}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.genre,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontFamily = NotoSansBengaliFamily,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${item.count} poems",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                ) {
                                    drawRoundRect(
                                        color = trackColor,
                                        cornerRadius = CornerRadius(12f, 12f),
                                        size = size
                                    )
                                    drawRoundRect(
                                        color = barColor,
                                        cornerRadius = CornerRadius(12f, 12f),
                                        size = Size(size.width * ratio, size.height)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item(key = "poet_leaderboard") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = leaderboardTab == 0,
                            onClick = { leaderboardTab = 0 },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("stylometry_tab_richness")
                        ) {
                            Text(
                                text = "Lexical Diversity",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        SegmentedButton(
                            selected = leaderboardTab == 1,
                            onClick = { leaderboardTab = 1 },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("stylometry_tab_volume")
                        ) {
                            Text(
                                text = "Most Prolific",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (leaderboardTab == 0) {
                            stringResource(R.string.stylometry_poet_richness_title)
                        } else {
                            stringResource(R.string.stylometry_poet_volume_title)
                        },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (leaderboardTab == 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.stylometry_poet_richness_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val leaders = if (leaderboardTab == 0) stats.topPoetsByRichness else stats.topPoetsByVolume
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        leaders.forEachIndexed { idx, poet ->
                            PoetLeaderboardRow(
                                rank = idx + 1,
                                poet = poet,
                                showTtr = leaderboardTab == 0,
                                onClick = { onPoetClick(poet.name) }
                            )
                        }
                    }
                }
            }
        }

        item(key = "research_attribution") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.stylometry_research_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.stylometry_research_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FilledTonalButton(
                        onClick = { uriHandler.openUri("https://shuhanmirza.com/files/NL069_NLPIR.pdf") },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("open_paper_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.stylometry_open_paper))
                    }
                }
            }
        }

        item(key = "stylometry_bottom_spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CorpusKpiCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PoetLeaderboardRow(
    rank: Int,
    poet: PoetSummary,
    showTtr: Boolean,
    onClick: () -> Unit
) {
    val metricText = if (showTtr) {
        "${(poet.avgLexicalDiversity * 100).roundToInt()}% TTR"
    } else {
        "${poet.poemCount} poems"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rank.",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(26.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = poet.name,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = NotoSerifBengaliFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${poet.poemCount} poems · ${poet.dominantGenre}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = metricText,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}
