package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.R
import com.example.ui.components.EmptyStateView
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.PoemReaderScreen
import com.example.ui.screens.PoetDetailScreen
import com.example.ui.screens.PoetsScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.StylometryScreen
import com.example.ui.state.ArchiveUiState
import com.example.ui.state.KobitaViewModel
import kotlinx.serialization.Serializable

@Serializable
object ExploreRoute

@Serializable
object PoetsRoute

@Serializable
object StylometryRoute

@Serializable
object SavedRoute

@Serializable
data class PoetDetailRoute(val poetName: String)

@Serializable
data class PoemReaderRoute(val poemId: Int)

private data class TopLevelDestination<T : Any>(
    val route: T,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val TopDestinations = listOf(
    TopLevelDestination(
        route = ExploreRoute,
        labelRes = R.string.nav_explore,
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        testTag = "nav_explore"
    ),
    TopLevelDestination(
        route = PoetsRoute,
        labelRes = R.string.nav_poets,
        selectedIcon = Icons.Filled.Groups,
        unselectedIcon = Icons.Outlined.Groups,
        testTag = "nav_poets"
    ),
    TopLevelDestination(
        route = StylometryRoute,
        labelRes = R.string.nav_stylometry,
        selectedIcon = Icons.Filled.Analytics,
        unselectedIcon = Icons.Outlined.Analytics,
        testTag = "nav_stylometry"
    ),
    TopLevelDestination(
        route = SavedRoute,
        labelRes = R.string.nav_saved,
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder,
        testTag = "nav_saved"
    )
)

@Composable
fun KobitaApp(
    viewModel: KobitaViewModel = viewModel()
) {
    val archiveState by viewModel.archiveState.collectAsStateWithLifecycle()
    val filteredPoems by viewModel.filteredPoems.collectAsStateWithLifecycle()
    val filteredPoets by viewModel.filteredPoets.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val poetSearchQuery by viewModel.poetSearchQuery.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.bookmarkedIds.collectAsStateWithLifecycle()
    val bookmarkedPoems by viewModel.bookmarkedPoems.collectAsStateWithLifecycle()
    val recentPoems by viewModel.recentPoems.collectAsStateWithLifecycle()
    val readerPreferences by viewModel.readerPreferences.collectAsStateWithLifecycle()

    when (val state = archiveState) {
        is ArchiveUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.loading_corpus),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        is ArchiveUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EmptyStateView(
                        icon = Icons.Rounded.ErrorOutline,
                        title = stringResource(R.string.error_corpus),
                        subtitle = state.message
                    )
                    Button(onClick = { viewModel.loadCorpus() }) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }

        is ArchiveUiState.Success -> {
            val corpus = state.corpus
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val isTopLevel = currentDestination == null ||
                TopDestinations.any { dest -> currentDestination.hasRoute(dest.route::class) }

            val navigateToTopLevel: (Any) -> Unit = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val useNavRail = maxWidth >= 600.dp

                Scaffold(
                    contentWindowInsets = if (isTopLevel) WindowInsets.safeDrawing else WindowInsets(0, 0, 0, 0),
                    bottomBar = {
                        if (isTopLevel && !useNavRail) {
                            NavigationBar(
                                windowInsets = WindowInsets.navigationBars,
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ) {
                                TopDestinations.forEach { dest ->
                                    val selected = currentDestination?.hasRoute(dest.route::class) == true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navigateToTopLevel(dest.route) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                contentDescription = stringResource(dest.labelRes)
                                            )
                                        },
                                        label = { Text(stringResource(dest.labelRes)) },
                                        modifier = Modifier.testTag(dest.testTag)
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (isTopLevel && useNavRail) {
                            NavigationRail(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Spacer(modifier = Modifier.height(12.dp))
                                TopDestinations.forEach { dest ->
                                    val selected = currentDestination?.hasRoute(dest.route::class) == true
                                    NavigationRailItem(
                                        selected = selected,
                                        onClick = { navigateToTopLevel(dest.route) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                contentDescription = stringResource(dest.labelRes)
                                            )
                                        },
                                        label = { Text(stringResource(dest.labelRes)) },
                                        modifier = Modifier.testTag(dest.testTag)
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = ExploreRoute,
                                modifier = Modifier
                                    .widthIn(max = 860.dp)
                                    .fillMaxSize()
                            ) {
                                composable<ExploreRoute> {
                                    val featuredPoem = remember(corpus.poems) {
                                        viewModel.getDailyFeaturedPoem(corpus.poems)
                                    }
                                    ExploreScreen(
                                        poems = filteredPoems,
                                        totalPoemCount = corpus.poems.size,
                                        stats = corpus.stats,
                                        genres = corpus.genres,
                                        featuredPoem = featuredPoem,
                                        searchQuery = searchQuery,
                                        selectedGenre = selectedGenre,
                                        sortOrder = sortOrder,
                                        bookmarkedIds = bookmarkedIds,
                                        onSearchQueryChange = viewModel::setSearchQuery,
                                        onSelectGenre = viewModel::selectGenre,
                                        onSortOrderChange = viewModel::setSortOrder,
                                        onClearFilters = viewModel::clearExploreFilters,
                                        onPoemClick = { id ->
                                            navController.navigate(PoemReaderRoute(id))
                                        },
                                        onPoetClick = { poet ->
                                            navController.navigate(PoetDetailRoute(poet))
                                        },
                                        onToggleBookmark = viewModel::toggleBookmark,
                                        onRandomPoemClick = {
                                            viewModel.getRandomPoemId()?.let { id ->
                                                navController.navigate(PoemReaderRoute(id))
                                            }
                                        }
                                    )
                                }

                                composable<PoetsRoute> {
                                    PoetsScreen(
                                        poets = filteredPoets,
                                        searchQuery = poetSearchQuery,
                                        onSearchQueryChange = viewModel::setPoetSearchQuery,
                                        onPoetClick = { poetName ->
                                            navController.navigate(PoetDetailRoute(poetName))
                                        }
                                    )
                                }

                                composable<StylometryRoute> {
                                    StylometryScreen(
                                        stats = corpus.stats,
                                        onGenreClick = { genre ->
                                            viewModel.selectGenre(genre)
                                            navigateToTopLevel(ExploreRoute)
                                        },
                                        onPoetClick = { poetName ->
                                            navController.navigate(PoetDetailRoute(poetName))
                                        }
                                    )
                                }

                                composable<SavedRoute> {
                                    SavedScreen(
                                        bookmarkedPoems = bookmarkedPoems,
                                        recentPoems = recentPoems,
                                        bookmarkedIds = bookmarkedIds,
                                        onPoemClick = { id ->
                                            navController.navigate(PoemReaderRoute(id))
                                        },
                                        onPoetClick = { poet ->
                                            navController.navigate(PoetDetailRoute(poet))
                                        },
                                        onToggleBookmark = viewModel::toggleBookmark,
                                        onClearHistory = viewModel::clearHistory,
                                        onBrowseExplore = { navigateToTopLevel(ExploreRoute) }
                                    )
                                }

                                composable<PoetDetailRoute> { backStackEntry ->
                                    val args = backStackEntry.toRoute<PoetDetailRoute>()
                                    val summary = remember(args.poetName, corpus) {
                                        viewModel.getPoetSummary(args.poetName)
                                    }
                                    val poetPoems = remember(args.poetName, corpus) {
                                        viewModel.getPoemsByPoet(args.poetName)
                                    }
                                    PoetDetailScreen(
                                        poetSummary = summary,
                                        poems = poetPoems,
                                        bookmarkedIds = bookmarkedIds,
                                        onBack = { navController.popBackStack() },
                                        onPoemClick = { id ->
                                            navController.navigate(PoemReaderRoute(id))
                                        },
                                        onToggleBookmark = viewModel::toggleBookmark
                                    )
                                }

                                composable<PoemReaderRoute> { backStackEntry ->
                                    val args = backStackEntry.toRoute<PoemReaderRoute>()
                                    val poem = remember(args.poemId, corpus) {
                                        viewModel.getPoemById(args.poemId)
                                    }
                                    val topWords = remember(poem) {
                                        if (poem != null) viewModel.computeTopWords(poem) else emptyList()
                                    }
                                    PoemReaderScreen(
                                        poem = poem,
                                        topWords = topWords,
                                        isBookmarked = args.poemId in bookmarkedIds,
                                        readerPreferences = readerPreferences,
                                        onBack = { navController.popBackStack() },
                                        onRecordRead = viewModel::recordPoemRead,
                                        onToggleBookmark = viewModel::toggleBookmark,
                                        onPoetClick = { poet ->
                                            navController.navigate(PoetDetailRoute(poet))
                                        },
                                        onFontSizeChange = viewModel::updateFontSize,
                                        onSurfaceModeChange = viewModel::updateSurfaceMode,
                                        onFontStyleChange = viewModel::updateFontStyle
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
