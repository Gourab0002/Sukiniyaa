package com.nyaa.sukiniyaa.data.repository

import com.nyaa.sukiniyaa.data.api.SukebeiCommentParser
import com.nyaa.sukiniyaa.data.api.SukebeiRssParser
import com.nyaa.sukiniyaa.data.model.SearchParams
import com.nyaa.sukiniyaa.data.model.Torrent
import com.nyaa.sukiniyaa.data.model.TorrentPageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class SukebeiRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun search(params: SearchParams): Result<List<Torrent>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = buildUrl(params)
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Sukiniyaa/1.0 (Android)")
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body ?: return@withContext Result.failure(Exception("Empty response"))
                    val torrents = SukebeiRssParser.parse(body.byteStream())
                    Result.success(torrents)
                } else {
                    Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchTorrentPageData(torrentId: String): Result<TorrentPageData> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://sukebei.nyaa.si/view/$torrentId"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val html = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
                    val pageData = SukebeiCommentParser.parse(html)
                    Result.success(pageData)
                } else {
                    Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun buildUrl(params: SearchParams): String {
        val sb = StringBuilder("https://sukebei.nyaa.si/?page=rss")
        if (params.query.isNotBlank()) {
            sb.append("&q=${java.net.URLEncoder.encode(params.query.trim(), "UTF-8")}")
        }
        sb.append("&c=${params.category.value}")
        sb.append("&f=${params.filter.value}")
        sb.append("&s=${params.sortField.value}")
        sb.append("&o=${params.sortOrder.value}")
        return sb.toString()
    }
}
