package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Poem(
    val id: Int,
    val title: String,
    val poet: String,
    val genre: String,
    val sourceUrl: String = "",
    val scrapedAt: String = "",
    val text: String,
    val excerpt: String = "",
    val lineCount: Int = 0,
    val wordCount: Int = 0,
    val uniqueWordCount: Int = 0,
    val lexicalDiversity: Double = 0.0,
    val avgWordsPerLine: Double = 0.0
)

data class GenreCount(
    val genre: String,
    val count: Int
)

data class PoetSummary(
    val name: String,
    val poemCount: Int,
    val totalWords: Int,
    val avgLexicalDiversity: Double,
    val avgWordsPerLine: Double,
    val dominantGenre: String,
    val genreBreakdown: List<GenreCount>
)

data class CorpusStats(
    val totalPoems: Int,
    val totalPoets: Int,
    val totalGenres: Int,
    val totalWords: Long,
    val uniqueCorpusWords: Int,
    val meanLexicalDiversity: Double,
    val genreDistribution: List<GenreCount>,
    val topPoetsByVolume: List<PoetSummary>,
    val topPoetsByRichness: List<PoetSummary>
)

data class WordFrequency(
    val word: String,
    val count: Int
)

enum class ReaderSurfaceMode {
    PAPER,
    SEPIA,
    NIGHT
}

enum class ReaderFontStyle {
    SERIF,
    SANS
}

data class ReaderPreferences(
    val fontSizeSp: Float = 18f,
    val surfaceMode: ReaderSurfaceMode = ReaderSurfaceMode.PAPER,
    val fontStyle: ReaderFontStyle = ReaderFontStyle.SERIF
)

enum class PoemSortOrder {
    CURATED,
    WORDS_DESC,
    TTR_DESC,
    TITLE_ASC
}
