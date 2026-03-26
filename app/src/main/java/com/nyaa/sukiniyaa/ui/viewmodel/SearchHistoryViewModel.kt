package com.nyaa.sukiniyaa.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nyaa.sukiniyaa.data.repository.SearchHistoryEntry
import com.nyaa.sukiniyaa.data.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SearchHistoryRepository(application)

    private val _history = MutableStateFlow<List<SearchHistoryEntry>>(emptyList())
    val history: StateFlow<List<SearchHistoryEntry>> = _history.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        _history.update { repository.getHistory() }
    }

    fun addEntry(query: String) {
        repository.addEntry(query)
        loadHistory()
    }

    fun removeEntry(query: String) {
        repository.removeEntry(query)
        loadHistory()
    }

    fun clearHistory() {
        repository.clearHistory()
        loadHistory()
    }
}
