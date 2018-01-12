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

package be.bluexin.scanlationsbot.discord

import be.bluexin.scanlationsbot.db.Chapter
import be.bluexin.scanlationsbot.db.Comic
import be.bluexin.scanlationsbot.db.Team
import be.bluexin.scanlationsbot.db.toJava
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.api.internal.json.objects.EmbedObject
import sx.blah.discord.util.EmbedBuilder
import java.awt.Color
import java.time.format.DateTimeFormatterBuilder
import java.time.format.SignStyle
import java.time.temporal.ChronoField

private val dow = hashMapOf(
        1L to "Mon",
        2L to "Tue",
        3L to "Wed",
        4L to "Thu",
        5L to "Fri",
        6L to "Sat",
        7L to "Sun"
)

private val moy = hashMapOf(
        1L to "Jan",
        2L to "Feb",
        3L to "Mar",
        4L to "Apr",
        5L to "May",
        6L to "Jun",
        7L to "Jul",
        8L to "Aug",
        9L to "Sep",
        10L to "Oct",
        11L to "Nov",
        12L to "Dec"
)

//RFC_1123_DATE_TIME = (DateTimeFormatterBuilder()).parseCaseInsensitive().parseLenient().optionalStart().appendText(ChronoField.DAY_OF_WEEK, dow).appendLiteral(", ").optionalEnd().appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE).appendLiteral(' ').appendText(ChronoField.MONTH_OF_YEAR, moy).appendLiteral(' ').appendValue(ChronoField.YEAR, 4).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).optionalStart().appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).optionalEnd().appendLiteral(' ').appendOffset("+HHMM", "GMT").toFormatter(ResolverStyle.SMART, IsoChronology.INSTANCE)

private val format = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .parseLenient()
//        .optionalStart()
//        .appendText(ChronoField.DAY_OF_WEEK, dow)
//        .appendLiteral(", ")
//        .optionalEnd()
        .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
        .appendLiteral(' ')
        .appendText(ChronoField.MONTH_OF_YEAR, moy)
        .appendLiteral(' ')
        .appendValue(ChronoField.YEAR, 4)
        .appendLiteral(' ')
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .optionalStart()
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .optionalEnd()
        .appendLiteral(" GMT")
        .toFormatter()

fun embedComic(comic: Comic): EmbedObject {
    val builder = EmbedBuilder().withTitle(comic.name)

    if (comic.href.isNotBlank()) builder.withUrl(comic.href)
    if (!comic.description.isNullOrBlank()) builder.appendField("Description", comic.description, false)
    val author = transaction { listOfNotNull(comic.author?.name, comic.artist?.name).distinct().joinToString(separator = " & ") }
    if (author.isNotBlank()) builder.withAuthorName(author)
    if (comic.small_thumb_url != null) builder.withThumbnail(comic.small_thumb_url)
    if (comic.adult) builder.appendDescription("\uD83D\uDD1E")

    return builder.build()
}

fun embedChapter(chapter: Chapter): EmbedObject {
    val builder = EmbedBuilder()
    if (chapter.name.isNotBlank()) builder.withTitle(chapter.name)

    builder.withTitle("${chapter.chapter}${if (chapter.subchapter != null) ".${chapter.subchapter}" else ""}" +
            if (chapter.name.isNotBlank()) ": ${chapter.name}" else "")
    if (chapter.thumbnail != null) builder.withThumbnail(chapter.thumbnail)
    if (!chapter.description.isNullOrBlank()) builder.appendField("Description", chapter.description, false)
    if (chapter.href.isNotBlank()) builder.withUrl(chapter.href)
    builder.appendField("Volume", chapter.volume.toString(), true)
    builder.appendField("Chapter", "${chapter.chapter}${if (chapter.subchapter != null) ".${chapter.subchapter}" else ""}", true)
    builder.appendField("Uploaded", chapter.created.toJava().format(format), true)
    if (chapter.updated != null) builder.appendField("Edited", chapter.updated!!.toJava().format(format), true)
    builder.appendField("Lang", chapter.language, true)

    transaction {
        builder.withAuthorName(chapter.comic.name)
        if (chapter.comic.href.isNotBlank()) builder.withAuthorUrl(chapter.comic.href)
        val comicThumb = chapter.comic.small_thumb_url
        if (comicThumb != null) {
            if (chapter.thumbnail != null) builder.withAuthorIcon(comicThumb)
            else builder.withThumbnail(comicThumb)
        }

        val team = chapter.team
        if (team != null) {
            builder.appendField("Scanlators", team.name, true)
        }
    }

    return builder.build()
}

fun embedTeam(team: Team): EmbedObject {
    val builder = EmbedBuilder().withTitle(team.name)

    builder.appendField("Members", "TODO", true)

    if (team.href != null) builder.withUrl(team.href)
    if (team.discord != null) builder.appendField("Discord", team.discord, false)
    if (team.irc != null) builder.appendField("IRC", team.irc, false)
    if (team.twitter != null) builder.appendField("Twitter", team.twitter, false)
    if (team.facebook != null) builder.appendField("Facebook", "[test](${team.facebook})", false)
    if (team.forum != null) builder.appendField("Forum", team.forum, false)

    return builder.build()
}

fun embedError(text: String) = EmbedBuilder().withDescription(text).withColor(Color.RED).build()
