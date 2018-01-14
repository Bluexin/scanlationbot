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

import be.bluexin.scanlationsbot.db.*
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime

fun Comic.Companion.findOrUpdate(from: FsComic): Comic {
    return findFirstOrUpdate(
            { ComicsTable.slug eq from.stub },
            {
                name = from.name
                slug = from.stub
                if (from.author.isNotBlank()) author = findOrCreatePerson(from.author)
                if (from.artist.isNotBlank()) artist = findOrCreatePerson(from.artist)
                description = from.description
                adult = from.adult
                created = from.created.toJoda()
                small_thumb_url = from.thumb_url
                big_thumb_url = from.fullsized_thumb_url
                href = from.href
            }
    )
}

fun Chapter.Companion.findOrUpdate(from: FsChapterInfo): Chapter {
    val comic = Comic.findOrUpdate(from.comic)
    return findFirstOrUpdate(
            { ChaptersTable.comic eq comic.id and (ChaptersTable.chapter eq from.chapter.chapter) and if (from.chapter.subchapter != 0) ChaptersTable.subchapter eq from.chapter.subchapter else ChaptersTable.subchapter.isNull() },
            {
                name = from.chapter.name
                slug = from.chapter.stub
                this.comic = comic
                if (from.teams.isNotEmpty()) this.team = Team.findOrUpdate(from.teams[0])
                chapter = from.chapter.chapter
                if (from.chapter.subchapter != 0) subchapter = from.chapter.subchapter
                volume = from.chapter.volume
                language = from.chapter.language
                if (from.chapter.description.isNotBlank()) description = from.chapter.description
                if (from.chapter.thumbnail.isNotBlank()) thumbnail = from.chapter.thumbnail
                created = from.chapter.created.toJoda()
                if (from.chapter.updated != LocalDateTime.MIN) updated = from.chapter.updated.toJoda()
                href = from.chapter.href
            }
    )
}

fun Team.Companion.findOrUpdate(from: FsTeam): Team {
    return findFirstOrUpdate(
            { TeamsTable.slug eq from.stub },
            {
                name = from.name
                slug = from.stub
                if (from.href.isNotBlank()) href = from.href
                if (from.facebook.isNotBlank()) facebook = from.facebook
                if (from.forum.isNotBlank()) forum = from.forum
                if (from.irc.isNotBlank()) irc = from.irc
                if (from.twitter.isNotBlank()) twitter = from.twitter
            }
    )
}