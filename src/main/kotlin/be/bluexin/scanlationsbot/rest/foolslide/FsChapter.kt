/*
 * Copyright (c) 2018 Arnaud 'Bluexin' Solé
 *
 * This file is part of scanlationsbot.
 *
 * scanlationsbot is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * scanlationsbot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with scanlationsbot.  If not, see <http://www.gnu.org/licenses/>.
 */

package be.bluexin.scanlationsbot.rest.foolslide

import java.time.LocalDateTime

data class FsChapters(
        val chapters: List<FsChapterInfo>
)

data class FsChapterInfo(
        val comic: FsComic,
        val chapter: FsChapter,
        val teams: List<FsTeam>
)

data class FsChapter(
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
