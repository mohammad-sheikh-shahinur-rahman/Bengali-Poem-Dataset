package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Poem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PoemCard
import com.example.ui.theme.PlayfairDisplayFamily

@Composable
fun SavedScreen(
    bookmarkedPoems: List<Poem>,
    recentPoems: List<Poem>,
    bookmarkedIds: Set<Int>,
    onPoemClick: (Int) -> Unit,
    onPoetClick: (String) -> Unit,
    onToggleBookmark: (Int) -> Unit,
    onClearHistory: () -> Unit,
    onBrowseExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableIntStateOf(0) }
    val shownPoems = if (activeTab == 0) bookmarkedPoems else recentPoems

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("saved_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "saved_header") {
            Column {
                Text(
                    text = stringResource(R.string.saved_title),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontFamily = PlayfairDisplayFamily
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        modifier = Modifier.testTag("saved_tab_bookmarks")
                    ) {
                        Text(
                            text = stringResource(R.string.saved_bookmarks_tab, bookmarkedPoems.size),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    SegmentedButton(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        modifier = Modifier.testTag("saved_tab_recent")
                    ) {
                        Text(
                            text = stringResource(R.string.saved_recent_tab, recentPoems.size),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        if (activeTab == 1 && recentPoems.isNotEmpty()) {
            item(key = "clear_history_row") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Text(text = stringResource(R.string.saved_clear_history))
                    }
                }
            }
        }

        if (shownPoems.isEmpty()) {
            item(key = "saved_empty") {
                if (activeTab == 0) {
                    EmptyStateView(
                        icon = Icons.Outlined.BookmarkBorder,
                        title = stringResource(R.string.saved_empty_bookmarks_title),
                        subtitle = stringResource(R.string.saved_empty_bookmarks_subtitle),
                        actionLabel = stringResource(R.string.saved_browse_cta),
                        onAction = onBrowseExplore
                    )
                } else {
                    EmptyStateView(
                        icon = Icons.Rounded.History,
                        title = stringResource(R.string.saved_empty_recent_title),
                        subtitle = stringResource(R.string.saved_empty_recent_subtitle),
                        actionLabel = stringResource(R.string.saved_browse_cta),
                        onAction = onBrowseExplore
                    )
                }
            }
        } else {
            items(shownPoems, key = { "${activeTab}_${it.id}" }) { poem ->
                PoemCard(
                    poem = poem,
                    isBookmarked = poem.id in bookmarkedIds,
                    onClick = { onPoemClick(poem.id) },
                    onToggleBookmark = { onToggleBookmark(poem.id) },
                    onPoetClick = { onPoetClick(poem.poet) }
                )
            }
        }

        item(key = "saved_bottom_spacer") {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
