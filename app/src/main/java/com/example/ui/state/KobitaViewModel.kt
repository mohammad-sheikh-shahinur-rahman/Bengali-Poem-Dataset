package com.example.ui.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LoadedCorpus
import com.example.data.PoemRepository
import com.example.data.UserPreferencesStore
import com.example.model.GenreCount
import com.example.model.Poem
import com.example.model.PoemSortOrder
import com.example.model.PoetSummary
import com.example.model.ReaderFontStyle
import com.example.model.ReaderPreferences
import com.example.model.ReaderSurfaceMode
import com.example.model.WordFrequency
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface ArchiveUiState {
    data object Loading : ArchiveUiState
    data class Success(val corpus: LoadedCorpus) : ArchiveUiState
    data class Error(val message: String) : ArchiveUiState
}

class KobitaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PoemRepository(application)
    private val preferencesStore = UserPreferencesStore(application)

    private val _archiveState = MutableStateFlow<ArchiveUiState>(ArchiveUiState.Loading)
    val archiveState: StateFlow<ArchiveUiState> = _archiveState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()

    private val _sortOrder = MutableStateFlow(PoemSortOrder.CURATED)
    val sortOrder: StateFlow<PoemSortOrder> = _sortOrder.asStateFlow()

    private val _poetSearchQuery = MutableStateFlow("")
    val poetSearchQuery: StateFlow<String> = _poetSearchQuery.asStateFlow()

    val bookmarkedIds: StateFlow<Set<Int>> = preferencesStore.bookmarkedIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val recentPoemIds: StateFlow<List<Int>> = preferencesStore.recentPoemIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val readerPreferences: StateFlow<ReaderPreferences> = preferencesStore.readerPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReaderPreferences())

    val filteredPoems: StateFlow<List<Poem>> = combine(
        _archiveState,
        _searchQuery,
        _selectedGenre,
        _sortOrder
    ) { state, query, genre, sort ->
        val poems = (state as? ArchiveUiState.Success)?.corpus?.poems.orEmpty()
        val trimmedQuery = query.trim()

        val matched = poems.filter { poem ->
            val genreOk = genre == null || poem.genre == genre
            val queryOk = trimmedQuery.isEmpty() ||
                poem.title.contains(trimmedQuery, ignoreCase = true) ||
                poem.poet.contains(trimmedQuery, ignoreCase = true) ||
                poem.genre.contains(trimmedQuery, ignoreCase = true) ||
                poem.text.contains(trimmedQuery, ignoreCase = true)
            genreOk && queryOk
        }

        when (sort) {
            PoemSortOrder.CURATED -> matched
            PoemSortOrder.WORDS_DESC -> matched.sortedByDescending { it.wordCount }
            PoemSortOrder.TTR_DESC -> matched.sortedByDescending { it.lexicalDiversity }
            PoemSortOrder.TITLE_ASC -> matched.sortedBy { it.title }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filteredPoets: StateFlow<List<PoetSummary>> = combine(
        _archiveState,
        _poetSearchQuery
    ) { state, query ->
        val poets = (state as? ArchiveUiState.Success)?.corpus?.poets.orEmpty()
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            poets
        } else {
            poets.filter {
                it.name.contains(trimmed, ignoreCase = true) ||
                    it.dominantGenre.contains(trimmed, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val bookmarkedPoems: StateFlow<List<Poem>> = combine(
        _archiveState,
        bookmarkedIds
    ) { state, ids ->
        val poems = (state as? ArchiveUiState.Success)?.corpus?.poems.orEmpty()
        poems.filter { it.id in ids }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentPoems: StateFlow<List<Poem>> = combine(
        _archiveState,
        recentPoemIds
    ) { state, recentIds ->
        val poemsById = (state as? ArchiveUiState.Success)?.corpus?.poems?.associateBy { it.id }.orEmpty()
        recentIds.mapNotNull { poemsById[it] }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        loadCorpus()
    }

    fun loadCorpus() {
        viewModelScope.launch {
            _archiveState.value = ArchiveUiState.Loading
            runCatching { repository.loadCorpus() }
                .onSuccess { corpus ->
                    _archiveState.value = ArchiveUiState.Success(corpus)
                }
                .onFailure { error ->
                    _archiveState.value = ArchiveUiState.Error(
                        error.localizedMessage ?: "Failed to parse bundled Bengali poem dataset."
                    )
                }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectGenre(genre: String?) {
        _selectedGenre.value = genre
    }

    fun setSortOrder(order: PoemSortOrder) {
        _sortOrder.value = order
    }

    fun clearExploreFilters() {
        _searchQuery.value = ""
        _selectedGenre.value = null
        _sortOrder.value = PoemSortOrder.CURATED
    }

    fun setPoetSearchQuery(query: String) {
        _poetSearchQuery.value = query
    }

    fun toggleBookmark(poemId: Int) {
        viewModelScope.launch {
            preferencesStore.toggleBookmark(poemId)
        }
    }

    fun recordPoemRead(poemId: Int) {
        viewModelScope.launch {
            preferencesStore.recordRead(poemId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            preferencesStore.clearHistory()
        }
    }

    fun updateFontSize(sizeSp: Float) {
        viewModelScope.launch {
            preferencesStore.updateFontSize(sizeSp)
        }
    }

    fun updateSurfaceMode(mode: ReaderSurfaceMode) {
        viewModelScope.launch {
            preferencesStore.updateSurfaceMode(mode)
        }
    }

    fun updateFontStyle(style: ReaderFontStyle) {
        viewModelScope.launch {
            preferencesStore.updateFontStyle(style)
        }
    }

    fun getPoemById(poemId: Int): Poem? {
        return (_archiveState.value as? ArchiveUiState.Success)
            ?.corpus
            ?.poems
            ?.firstOrNull { it.id == poemId }
    }

    fun getPoetSummary(poetName: String): PoetSummary? {
        return (_archiveState.value as? ArchiveUiState.Success)
            ?.corpus
            ?.poets
            ?.firstOrNull { it.name == poetName }
    }

    fun getPoemsByPoet(poetName: String): List<Poem> {
        return (_archiveState.value as? ArchiveUiState.Success)
            ?.corpus
            ?.poems
            ?.filter { it.poet == poetName }
            .orEmpty()
    }

    fun getGenres(): List<GenreCount> {
        return (_archiveState.value as? ArchiveUiState.Success)
            ?.corpus
            ?.genres
            .orEmpty()
    }

    fun getDailyFeaturedPoem(poems: List<Poem>): Poem? {
        if (poems.isEmpty()) return null
        val daySeed = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return poems[daySeed % poems.size]
    }

    fun getRandomPoemId(excludingId: Int? = null): Int? {
        val poems = (_archiveState.value as? ArchiveUiState.Success)?.corpus?.poems.orEmpty()
        if (poems.isEmpty()) return null
        val candidates = if (excludingId != null && poems.size > 1) {
            poems.filter { it.id != excludingId }
        } else poems
        return candidates.randomOrNull()?.id
    }

    fun computeTopWords(poem: Poem): List<WordFrequency> = repository.computeTopWords(poem)
}
