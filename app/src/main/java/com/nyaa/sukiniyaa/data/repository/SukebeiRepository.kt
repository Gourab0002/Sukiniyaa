package com.nyaa.sukiniyaa.data.repository

import com.nyaa.sukiniyaa.data.api.SukebeiCommentParser
import com.nyaa.sukiniyaa.data.api.SukebeiRssParser
import com.nyaa.sukiniyaa.data.model.SearchParams
import com.nyaa.sukiniyaa.data.model.Torrent
import com.nyaa.sukiniyaa.data.model.TorrentPageData
import com.nyaa.sukiniyaa.data.network.AppHttpClient
import com.nyaa.sukiniyaa.data.network.await
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

internal const val SUKEBEI_PAGE_SIZE = 75

internal fun torrentIdentity(torrent: Torrent): String = torrent.identity()

internal fun mergeSearchPages(
    existing: List<Torrent>,
    incoming: List<Torrent>,
    replace: Boolean
): Pair<List<Torrent>, Boolean> {
    if (replace) {
        val unique = incoming.distinctBy(::torrentIdentity)
        val canLoadMore = incoming.size >= SUKEBEI_PAGE_SIZE && unique.isNotEmpty()
        return unique to canLoadMore
    }
    if (incoming.isEmpty()) return existing to false
    val seen = existing.mapTo(HashSet(existing.size + incoming.size)) { torrentIdentity(it) }
    val added = ArrayList<Torrent>(incoming.size)
    for (torrent in incoming) {
        if (seen.add(torrentIdentity(torrent))) {
            added.add(torrent)
        }
    }
    val merged = if (added.isEmpty()) existing else existing + added
    val canLoadMore = added.isNotEmpty() && incoming.size >= SUKEBEI_PAGE_SIZE
    return merged to canLoadMore
}

internal fun buildSearchUrl(params: SearchParams): String {
    val sb = StringBuilder("https://sukebei.nyaa.si/?page=rss")
    if (params.query.isNotBlank()) {
        sb.append("&q=${URLEncoder.encode(params.query.trim(), "UTF-8")}")
    }
    sb.append("&c=${params.category.value}")
    sb.append("&f=${params.filter.value}")
    sb.append("&s=${params.sortField.value}")
    sb.append("&o=${params.sortOrder.value}")
    if (params.page > 1) {
        sb.append("&p=${params.page}")
    }
    return sb.toString()
}

class SukebeiRepository(
    private val client: okhttp3.OkHttpClient = AppHttpClient.instance
) {

    suspend fun search(params: SearchParams): Result<List<Torrent>> {
        return withContext(Dispatchers.IO) {
            try {
                val request = AppHttpClient.newRequest(buildSearchUrl(params))
                client.newCall(request).await().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("HTTP ${response.code}: ${response.message}")
                        )
                    }
                    val body = response.body
                        ?: return@withContext Result.failure(Exception("Empty response"))
                    Result.success(SukebeiRssParser.parse(body.byteStream()))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchTorrentPageData(torrentId: String): Result<TorrentPageData> {
        return withContext(Dispatchers.IO) {
            try {
                val request = AppHttpClient.newRequest("https://sukebei.nyaa.si/view/$torrentId")
                client.newCall(request).await().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(
                            Exception("HTTP ${response.code}: ${response.message}")
                        )
                    }
                    val html = response.body?.string()
                        ?: return@withContext Result.failure(Exception("Empty response"))
                    Result.success(SukebeiCommentParser.parse(html))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
