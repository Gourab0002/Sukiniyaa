package com.nyaa.sukiniyaa.data.api

import com.nyaa.sukiniyaa.data.model.TorrentComment
import com.nyaa.sukiniyaa.data.model.TorrentPageData
import org.jsoup.Jsoup

object SukebeiCommentParser {

    fun parse(html: String): TorrentPageData {
        val doc = Jsoup.parse(html)

        // div#torrent-description stores raw markdown text (HTML-escaped, &#10; for newlines).
        // Use .wholeText() to preserve newlines required for markdown tables and paragraphs.
        val descriptionEl = doc.selectFirst("div#torrent-description")
        val description = descriptionEl?.wholeText()?.trim() ?: ""

        // sukebei.nyaa.si uses the same comment structure as nyaa.si:
        // <div class="comment-panel panel-default" id="com-N">
        //   <div class="panel-body">
        //     <div class="col-md-2">
        //       <a href="/user/username">username</a>
        //       <img class="avatar" src="..." />
        //     </div>
        //     <div class="col-md-10 comment">
        //       <div class="row comment-details">
        //         <a href="#com-N"><small data-timestamp-swap>timestamp</small></a>
        //       </div>
        //       <div class="row comment-body">
        //         <div markdown-text class="comment-content">raw markdown text</div>
        //       </div>
        //     </div>
        //   </div>
        // </div>
        val comments = mutableListOf<TorrentComment>()
        val commentElements = doc.select("div#comments div.comment-panel")
        for (element in commentElements) {
            val links = element.select("a")
            val username = links.firstOrNull()?.text()?.trim() ?: "Anonymous"
            val avatarSrc = element.selectFirst("img.avatar")?.attr("src") ?: ""
            val avatarUrl = when {
                avatarSrc.startsWith("//") -> "https:$avatarSrc"
                avatarSrc.startsWith("/") -> "https://sukebei.nyaa.si$avatarSrc"
                else -> avatarSrc
            }
            val date = links.asSequence()
                .flatMap { it.children().asSequence() }
                .firstOrNull()
                ?.text()?.trim() ?: ""
            val contentEl = element.selectFirst("div.comment-body div.comment-content")
            val content = contentEl?.wholeText()?.trim() ?: ""
            val id = element.attr("id").removePrefix("com-")
            if (content.isNotEmpty()) {
                comments.add(TorrentComment(id = id, username = username, avatarUrl = avatarUrl, date = date, content = content))
            }
        }
        return TorrentPageData(description = description, comments = comments)
    }
}
