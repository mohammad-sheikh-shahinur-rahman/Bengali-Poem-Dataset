package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.PoetSummary
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GenrePill
import com.example.ui.components.PoetInitialAvatar
import com.example.ui.components.SearchBarField
import com.example.ui.theme.NotoSerifBengaliFamily
import com.example.ui.theme.PlayfairDisplayFamily
import java.text.NumberFormat
import kotlin.math.roundToInt

@Composable
fun PoetsScreen(
    poets: List<PoetSummary>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPoetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("poets_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "poets_header") {
            Column {
                Text(
                    text = stringResource(R.string.poets_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = PlayfairDisplayFamily
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.poets_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item(key = "poets_search") {
            SearchBarField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                placeholder = stringResource(R.string.poets_search_hint),
                testTag = "poets_search_input"
            )
        }

        if (poets.isEmpty()) {
            item(key = "poets_empty") {
                EmptyStateView(
                    icon = Icons.Rounded.PersonSearch,
                    title = stringResource(R.string.poets_empty_title),
                    subtitle = stringResource(R.string.poets_empty_subtitle),
                    actionLabel = stringResource(R.string.explore_clear_filters),
                    onAction = { onSearchQueryChange("") }
                )
            }
        } else {
            items(poets, key = { it.name }) { poet ->
                PoetDirectoryCard(
                    poet = poet,
                    onClick = { onPoetClick(poet.name) }
                )
            }
        }

        item(key = "poets_bottom_spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PoetDirectoryCard(
    poet: PoetSummary,
    onClick: () -> Unit
) {
    val formatter = NumberFormat.getIntegerInstance()
    val ttrPercent = (poet.avgLexicalDiversity * 100).roundToInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .testTag("poet_card_${poet.name}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PoetInitialAvatar(poetName = poet.name)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = poet.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = NotoSerifBengaliFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.poets_poem_count, poet.poemCount)} · ${stringResource(R.string.poets_word_count, formatter.format(poet.totalWords))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (poet.dominantGenre.isNotBlank()) {
                        GenrePill(genre = poet.dominantGenre)
                    }
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = stringResource(R.string.poets_ttr_badge, ttrPercent),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
