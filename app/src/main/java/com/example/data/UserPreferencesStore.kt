package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ReaderFontStyle
import com.example.model.ReaderPreferences
import com.example.model.ReaderSurfaceMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kobita_preferences")

class UserPreferencesStore(private val context: Context) {

    private object Keys {
        val BOOKMARKED_IDS = stringSetPreferencesKey("bookmarked_poem_ids")
        val RECENT_IDS = stringPreferencesKey("recent_poem_ids_csv")
        val FONT_SIZE_SP = floatPreferencesKey("reader_font_size_sp")
        val SURFACE_MODE = stringPreferencesKey("reader_surface_mode")
        val FONT_STYLE = stringPreferencesKey("reader_font_style")
    }

    val bookmarkedIds: Flow<Set<Int>> = context.dataStore.data.map { prefs ->
        prefs[Keys.BOOKMARKED_IDS]
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()
    }

    val recentPoemIds: Flow<List<Int>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.RECENT_IDS].orEmpty()
        if (raw.isBlank()) {
            emptyList()
        } else {
            raw.split(",").mapNotNull { it.toIntOrNull() }
        }
    }

    val readerPreferences: Flow<ReaderPreferences> = context.dataStore.data.map { prefs ->
        val fontSize = (prefs[Keys.FONT_SIZE_SP] ?: 18f).coerceIn(14f, 26f)
        val surface = prefs[Keys.SURFACE_MODE]?.let { name ->
            ReaderSurfaceMode.entries.firstOrNull { it.name == name }
        } ?: ReaderSurfaceMode.PAPER
        val fontStyle = prefs[Keys.FONT_STYLE]?.let { name ->
            ReaderFontStyle.entries.firstOrNull { it.name == name }
        } ?: ReaderFontStyle.SERIF

        ReaderPreferences(
            fontSizeSp = fontSize,
            surfaceMode = surface,
            fontStyle = fontStyle
        )
    }

    suspend fun toggleBookmark(poemId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.BOOKMARKED_IDS]?.toMutableSet() ?: mutableSetOf()
            val key = poemId.toString()
            if (current.contains(key)) {
                current.remove(key)
            } else {
                current.add(key)
            }
            prefs[Keys.BOOKMARKED_IDS] = current
        }
    }

    suspend fun recordRead(poemId: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.RECENT_IDS]
                .orEmpty()
                .split(",")
                .mapNotNull { it.toIntOrNull() }
                .filter { it != poemId }
                .toMutableList()
            current.add(0, poemId)
            val trimmed = current.take(25)
            prefs[Keys.RECENT_IDS] = trimmed.joinToString(",")
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { prefs ->
            prefs[Keys.RECENT_IDS] = ""
        }
    }

    suspend fun updateFontSize(sizeSp: Float) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FONT_SIZE_SP] = sizeSp.coerceIn(14f, 26f)
        }
    }

    suspend fun updateSurfaceMode(mode: ReaderSurfaceMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SURFACE_MODE] = mode.name
        }
    }

    suspend fun updateFontStyle(style: ReaderFontStyle) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FONT_STYLE] = style.name
        }
    }
}
