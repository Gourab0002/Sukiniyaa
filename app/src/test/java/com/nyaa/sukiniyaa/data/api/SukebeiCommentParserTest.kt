package com.nyaa.sukiniyaa.data.api

import org.junit.Assert.assertEquals
import org.junit.Test

class SukebeiCommentParserTest {

    @Test
    fun parse_readsDescriptionCommentsAndNestedFiles() {
        val html = """
            <html>
              <body>
                <div id="torrent-description">Line 1
Line 2</div>
                <div class="torrent-file-list">
                  <ul>
                    <li class="torrent-file-list-folder">
                      folderName
                      <ul>
                        <li>inner.mp4 <span class="pull-right">800.0 MiB</span></li>
                      </ul>
                    </li>
                    <li>root.txt <span class="pull-right">200.0 MiB</span></li>
                  </ul>
                </div>
                <div id="comments">
                  <div class="comment-panel" id="com-9">
                    <a href="/user/alice">alice</a>
                    <img class="avatar" src="/static/img/avatar.png" />
                    <div class="comment-body">
                      <div class="comment-content">Nice release</div>
                    </div>
                  </div>
                </div>
              </body>
            </html>
        """.trimIndent()

        val page = SukebeiCommentParser.parse(html)
        assertEquals("Line 1\nLine 2", page.description)
        assertEquals(2, page.fileList.size)
        assertEquals("folderName/inner.mp4", page.fileList[0].name)
        assertEquals("800.0 MiB", page.fileList[0].size)
        assertEquals("root.txt", page.fileList[1].name)
        assertEquals(1, page.comments.size)
        assertEquals("alice", page.comments[0].username)
        assertEquals("https://sukebei.nyaa.si/static/img/avatar.png", page.comments[0].avatarUrl)
        assertEquals("Nice release", page.comments[0].content)
        assertEquals("9", page.comments[0].id)
    }
}
