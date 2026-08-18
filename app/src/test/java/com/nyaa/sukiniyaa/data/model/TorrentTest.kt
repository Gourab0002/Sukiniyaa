package com.nyaa.sukiniyaa.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TorrentTest {

    private fun torrent(id: String = "1", infoHash: String = "abc") = Torrent(
        id = id,
        title = "Title",
        link = "https://example.com/dl",
        guid = "https://example.com/view/1",
        pubDate = "Wed, 01 Jan 2025 00:00:00 -0000",
        seeders = 0,
        leechers = 0,
        downloads = 0,
        infoHash = infoHash,
        category = "Art",
        size = "1.0 MiB",
        comments = 0,
        trusted = false,
        remake = false,
        magnetLink = ""
    )

    @Test
    fun identity_prefersIdThenHash() {
        assertEquals("1", torrent(id = "1", infoHash = "abc").identity())
        assertEquals("abc", torrent(id = "", infoHash = "abc").identity())
    }

    @Test
    fun matchesNavId_acceptsIdHashAndFallback() {
        assertTrue(torrent(id = "12", infoHash = "ff").matchesNavId("12"))
        assertTrue(torrent(id = "", infoHash = "ff").matchesNavId("ff"))
        assertTrue(torrent(id = "", infoHash = "").matchesNavId("unknown"))
        assertFalse(torrent(id = "12", infoHash = "ff").matchesNavId("nope"))
    }

    @Test
    fun listKey_isStableForRealIds() {
        assertEquals("id:12", torrent(id = "12").listKey(0))
        assertEquals("ih:ff", torrent(id = "", infoHash = "ff").listKey(3))
    }
}
