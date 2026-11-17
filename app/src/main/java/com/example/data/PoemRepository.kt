package com.example.data

import android.content.Context
import com.example.model.CorpusStats
import com.example.model.GenreCount
import com.example.model.Poem
import com.example.model.PoetSummary
import com.example.model.WordFrequency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class PoemRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val punctuationRegex = Regex("[।,.;:!?\"'‘’“”()\\[\\]\\-—–]+")

    suspend fun loadCorpus(): LoadedCorpus = withContext(Dispatchers.IO) {
        val rawJson = context.assets.open("poems_dataset.json")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }

        val poems = json.decodeFromString<List<Poem>>(rawJson)
        val poets = buildPoetSummaries(poems)
        val stats = buildCorpusStats(poems, poets)
        val genres = stats.genreDistribution

        LoadedCorpus(
            poems = poems,
            poets = poets,
            genres = genres,
            stats = stats
        )
    }

    fun computeTopWords(poem: Poem, limit: Int = 5): List<WordFrequency> {
        val counts = LinkedHashMap<String, Int>()
        poem.text.lineSequence()
            .flatMap { it.split(Regex("\\s+")).asSequence() }
            .map { punctuationRegex.replace(it, "").trim() }
            .filter { it.length >= 2 }
            .forEach { word ->
                counts[word] = (counts[word] ?: 0) + 1
            }

        return counts.entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .take(limit)
            .map { WordFrequency(it.key, it.value) }
    }

    private fun buildPoetSummaries(poems: List<Poem>): List<PoetSummary> {
        return poems.groupBy { it.poet }
            .entries
            .map { (poetName, poetPoems) ->
                val genreCounts = poetPoems.groupingBy { it.genre }
                    .eachCount()
                    .entries
                    .sortedByDescending { it.value }
                    .map { GenreCount(it.key, it.value) }

                val totalWords = poetPoems.sumOf { it.wordCount }
                val avgTtr = if (poetPoems.isNotEmpty()) {
                    poetPoems.map { it.lexicalDiversity }.average()
                } else 0.0
                val avgWpl = if (poetPoems.isNotEmpty()) {
                    poetPoems.map { it.avgWordsPerLine }.average()
                } else 0.0

                PoetSummary(
                    name = poetName,
                    poemCount = poetPoems.size,
                    totalWords = totalWords,
                    avgLexicalDiversity = avgTtr,
                    avgWordsPerLine = avgWpl,
                    dominantGenre = genreCounts.firstOrNull()?.genre ?: "",
                    genreBreakdown = genreCounts
                )
            }
            .sortedWith(compareByDescending<PoetSummary> { it.poemCount }.thenBy { it.name })
    }

    private fun buildCorpusStats(poems: List<Poem>, poets: List<PoetSummary>): CorpusStats {
        val genreDist = poems.groupingBy { it.genre }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { GenreCount(it.key, it.value) }

        val corpusVocabulary = HashSet<String>(16384)
        poems.forEach { poem ->
            poem.text.split(Regex("\\s+")).forEach { token ->
                val cleaned = punctuationRegex.replace(token, "").trim()
                if (cleaned.isNotEmpty()) {
                    corpusVocabulary.add(cleaned)
                }
            }
        }

        val totalWords = poems.sumOf { it.wordCount.toLong() }
        val meanTtr = if (poems.isNotEmpty()) poems.map { it.lexicalDiversity }.average() else 0.0

        val topByVolume = poets.sortedByDescending { it.poemCount }.take(8)
        val topByRichness = poets
            .filter { it.poemCount >= 3 }
            .sortedByDescending { it.avgLexicalDiversity }
            .take(8)

        return CorpusStats(
            totalPoems = poems.size,
            totalPoets = poets.size,
            totalGenres = genreDist.size,
            totalWords = totalWords,
            uniqueCorpusWords = corpusVocabulary.size,
            meanLexicalDiversity = meanTtr,
            genreDistribution = genreDist,
            topPoetsByVolume = topByVolume,
            topPoetsByRichness = topByRichness
        )
    }
}

data class LoadedCorpus(
    val poems: List<Poem>,
    val poets: List<PoetSummary>,
    val genres: List<GenreCount>,
    val stats: CorpusStats
)
