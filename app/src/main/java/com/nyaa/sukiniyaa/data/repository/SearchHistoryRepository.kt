package com.nyaa.sukiniyaa.data.repository

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class SearchHistoryEntry(
    val query: String,
    val timestamp: Long
)

class SearchHistoryRepository(context: Context) {

    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HISTORY = "history_list"
        private const val MAX_HISTORY_SIZE = 50
    }

    fun getHistory(): List<SearchHistoryEntry> {
        val json = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        return parseHistory(json)
    }

    fun addEntry(query: String) {
        if (query.isBlank()) return
        val trimmedQuery = query.trim()
        val history = getHistory().toMutableList()
        // Remove existing entry with same query (case-insensitive) to avoid duplicates
        history.removeAll { it.query.equals(trimmedQuery, ignoreCase = true) }
        // Add new entry at the top
        history.add(0, SearchHistoryEntry(query = trimmedQuery, timestamp = System.currentTimeMillis()))
        // Keep only the most recent entries
        val trimmed = if (history.size > MAX_HISTORY_SIZE) history.take(MAX_HISTORY_SIZE) else history
        saveHistory(trimmed)
    }

    fun removeEntry(query: String) {
        val history = getHistory().filter { !it.query.equals(query, ignoreCase = true) }
        saveHistory(history)
    }

    fun clearHistory() {
        prefs.edit().putString(KEY_HISTORY, "[]").apply()
    }

    private fun parseHistory(json: String): List<SearchHistoryEntry> {
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                SearchHistoryEntry(
                    query = obj.optString("query", ""),
                    timestamp = obj.optLong("timestamp", 0L)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveHistory(history: List<SearchHistoryEntry>) {
        val array = JSONArray()
        history.forEach { entry ->
            array.put(JSONObject().apply {
                put("query", entry.query)
                put("timestamp", entry.timestamp)
            })
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }
}
