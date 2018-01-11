package be.bluexin.scanlationsbot.rest.foolslide

import java.time.LocalDateTime

data class Chapters(
        val chapters: List<ChapterInfo>
)

data class ChapterInfo(
        val comic: Comic,
        val chapter: Chapter,
        val teams: List<Team>
)

data class Chapter(
        val id: Int,
        val comic_id: Int,
        val team_id: Int,
        val joint_id: Int,
        val chapter: Int,
        val subchapter: Int,
        val volume: Int,
        /**
         * Language code (en, pt, fr, ...)
         */
        val language: String,
        val name: String,
        val stub: String,
        val uniqid: String,
        val hidden: Boolean,
        val description: String,
        val thumbnail: String,
        val created: LocalDateTime,
        val lastseen: LocalDateTime,
        val updated: LocalDateTime,
        val creator: Int,
        val editor: Int,
        val href: String,
        val title: String,
        val download_href: String
)
