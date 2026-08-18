package com.nyaa.sukiniyaa.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nyaa.sukiniyaa.data.model.Torrent
import com.nyaa.sukiniyaa.data.repository.BookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookmarkRepository(application)

    private val _bookmarks = MutableStateFlow<List<Torrent>>(emptyList())
    val bookmarks: StateFlow<List<Torrent>> = _bookmarks.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            _bookmarks.value = withContext(Dispatchers.IO) { repository.getBookmarks() }
        }
    }

    fun toggleBookmark(torrent: Torrent) {
        viewModelScope.launch {
            _bookmarks.value = withContext(Dispatchers.IO) {
                if (repository.isBookmarked(torrent.id.ifEmpty { torrent.infoHash })) {
                    repository.removeBookmark(torrent.id.ifEmpty { torrent.infoHash })
                } else {
                    repository.addBookmark(torrent)
                }
                repository.getBookmarks()
            }
        }
    }

    fun removeBookmark(torrentId: String) {
        viewModelScope.launch {
            _bookmarks.value = withContext(Dispatchers.IO) {
                repository.removeBookmark(torrentId)
                repository.getBookmarks()
            }
        }
    }

    fun isBookmarked(torrentId: String): Boolean {
        return _bookmarks.value.any { it.id == torrentId || it.infoHash == torrentId }
    }

    fun torrentByNavId(navId: String): Torrent? =
        _bookmarks.value.find { it.matchesNavId(navId) }
}
