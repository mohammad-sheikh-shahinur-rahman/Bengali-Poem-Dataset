package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CorpusStats
import com.example.model.GenreCount
import com.example.model.Poem
import com.example.model.PoemSortOrder
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GenrePill
import com.example.ui.components.PoemCard
import com.example.ui.components.SearchBarField
import com.example.ui.theme.NotoSerifBengaliFamily
import com.example.ui.theme.PlayfairDisplayFamily

@Composable
fun ExploreScreen(
    poems: List<Poem>,
    totalPoemCount: Int,
    stats: CorpusStats,
    genres: List<GenreCount>,
    featuredPoem: Poem?,
    searchQuery: String,
    selectedGenre: String?,
    sortOrder: PoemSortOrder,
    bookmarkedIds: Set<Int>,
    onSearchQueryChange: (String) -> Unit,
    onSelectGenre: (String?) -> Unit,
    onSortOrderChange: (PoemSortOrder) -> Unit,
    onClearFilters: () -> Unit,
    onPoemClick: (Int) -> Unit,
    onPoetClick: (String) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onRandomPoemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_poem_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "hero_banner") {
            HeroArchiveBanner(
                stats = stats,
                onRandomPoemClick = onRandomPoemClick
            )
        }

        if (featuredPoem != null && searchQuery.isBlank() && selectedGenre == null) {
            item(key = "featured_verse") {
                FeaturedVerseCard(
                    poem = featuredPoem,
                    onClick = { onPoemClick(featuredPoem.id) }
                )
            }
        }

        item(key = "search_bar") {
            SearchBarField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                placeholder = stringResource(R.string.explore_search_hint),
                testTag = "explore_search_input"
            )
        }

        item(key = "genre_chips") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item(key = "genre_all") {
                    FilterChip(
                        selected = selectedGenre == null,
                        onClick = { onSelectGenre(null) },
                        label = {
                            Text(
                                text = stringResource(R.string.explore_all_genres, totalPoemCount),
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("genre_chip_all")
                    )
                }

                items(genres, key = { it.genre }) { genreItem ->
                    FilterChip(
                        selected = selectedGenre == genreItem.genre,
                        onClick = {
                            onSelectGenre(
                                if (selectedGenre == genreItem.genre) null else genreItem.genre
                            )
                        },
                        label = {
                            Text(
                                text = "${genreItem.genre} (${genreItem.count})",
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("genre_chip_${genreItem.genre}")
                    )
                }
            }
        }

        item(key = "results_toolbar") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.explore_results_count, poems.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { sortMenuExpanded = true }
                            .testTag("explore_sort_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Sort,
                                contentDescription = stringResource(R.string.sort_label),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sortLabel(sortOrder),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        PoemSortOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text = { Text(sortLabel(order)) },
                                onClick = {
                                    onSortOrderChange(order)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (poems.isEmpty()) {
            item(key = "empty_poems") {
                EmptyStateView(
                    icon = Icons.Rounded.SearchOff,
                    title = stringResource(R.string.explore_empty_title),
                    subtitle = stringResource(R.string.explore_empty_subtitle),
                    actionLabel = stringResource(R.string.explore_clear_filters),
                    onAction = onClearFilters
                )
            }
        } else {
            items(poems, key = { it.id }) { poem ->
                PoemCard(
                    poem = poem,
                    isBookmarked = poem.id in bookmarkedIds,
                    onClick = { onPoemClick(poem.id) },
                    onToggleBookmark = { onToggleBookmark(poem.id) },
                    onPoetClick = { onPoetClick(poem.poet) }
                )
            }
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HeroArchiveBanner(
    stats: CorpusStats,
    onRandomPoemClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(188.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_poetry),
                contentDescription = stringResource(R.string.cd_hero_banner),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xEA241610),
                                Color(0xC2321D14),
                                Color(0x38241610)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.explore_hero_eyebrow),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFF3C87B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.explore_hero_title),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = PlayfairDisplayFamily
                        ),
                        color = Color(0xFFFDF8F2)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.explore_hero_subtitle,
                            stats.totalPoems,
                            stats.totalPoets,
                            stats.totalGenres
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFEBDCD0)
                    )
                }

                FilledTonalButton(
                    onClick = onRandomPoemClick,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFF3C87B),
                        contentColor = Color(0xFF281800)
                    ),
                    modifier = Modifier.testTag("random_poem_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Casino,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.explore_random_poem),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun FeaturedVerseCard(
    poem: Poem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .testTag("featured_poem_card"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.explore_featured_label),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                GenrePill(genre = poem.genre)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = poem.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = NotoSerifBengaliFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = poem.poet,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (poem.excerpt.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = poem.excerpt,
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = NotoSerifBengaliFamily),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${poem.lineCount} lines · ${poem.wordCount} words",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun sortLabel(order: PoemSortOrder): String = when (order) {
    PoemSortOrder.CURATED -> stringResource(R.string.sort_default)
    PoemSortOrder.WORDS_DESC -> stringResource(R.string.sort_length_desc)
    PoemSortOrder.TTR_DESC -> stringResource(R.string.sort_richness_desc)
    PoemSortOrder.TITLE_ASC -> stringResource(R.string.sort_title_asc)
}
