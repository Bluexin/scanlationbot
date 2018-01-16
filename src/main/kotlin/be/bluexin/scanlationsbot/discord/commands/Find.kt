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

package be.bluexin.scanlationsbot.discord.commands

import be.bluexin.scanlationsbot.Global
import be.bluexin.scanlationsbot.db.*
import be.bluexin.scanlationsbot.discord.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IMessage
import kotlin.math.min

object Find : Command {
    override val name = "find"

    override suspend fun process(trigger: IMessage) {
        val args = mapArgs(trigger)
        val target = args[name] ?: throw Exception("You must specify a target.")
        val mapping = argsMap[target] ?: throw Exception("Target '$target' is invalid.")

        val op = with(SqlExpressionBuilder) {
            var b = mapping["slug"]!! lessEq "" or (mapping["slug"]!! greaterEq "") // Hax

            args.forEach { key, value ->
                if (mapping.containsKey(key)) {
                    b = b and if (value.startsWith("~")) mapping[key]!! regexp value.replace("^~".toRegex(), "").trim()
                    else mapping[key]!! eq value
                } else if (key.contains('.')) {
                    val (tkey, lkey) = key.split('.')
                    if (argsMap[tkey]?.containsKey(lkey) == true) {
                        b = b and if (value.startsWith("~")) argsMap[tkey]!![lkey]!! regexp value.replace("^~".toRegex(), "").trim()
                        else argsMap[tkey]!![lkey]!! eq value
                    }
                }
            }

            b
        }

        val result = transaction {
            if (trigger.author.longID in Global.settings.owners && args.containsKey("debug")) trigger.replyInChannel(embedNeutral("--OWNER DEBUG INFO--\nRequest: $op"))

            when (target) {
                "team" -> Team.find(op)
                "comic" -> Comic.find(op)
                "chapter" -> Chapter.wrapRows((ChaptersTable leftJoin TeamsTable leftJoin (ComicsTable/* innerJoin PeopleTable*/)).select(op))
                else -> throw Exception("Dafuq?")
            }.toList()
        }

        listOrEmbed(result, target, trigger)
    }

    private fun <T: Any>listOrEmbed(result: List<T>, target: String, trigger: IMessage) {
        when {
            result.isEmpty() -> throw Exception("Didn't find any $target with these parameters")
            result.size == 1 -> {
                val r = result[0]
                trigger.replyInChannel(when (r) {
                    is Comic -> embedComic(r)
                    is Team -> embedTeam(r)
                    is Chapter -> embedChapter(r)
                    else -> throw Exception("Unknown ${r::class}")
                })
            }
            else -> {
                val size = result.size
                val limit = min(result.size, 10)
                trigger.replyInChannel(embedSuccess(
                        "Found $size results:\n" +
                                result.asSequence().joinToString(separator = "\n", limit = limit) {
                                    when (it) {
                                        is Comic -> "${it.name} (${it.slug})"
                                        is Team -> "${it.name} (${it.slug})"
                                        is Chapter -> transaction { "${it.comic.name} ${it.chapter}${if (it.subchapter != null) ".${it.subchapter}" else ""}${if (it.name.isNotBlank()) ": ${it.name}" else ""}" }
                                        else -> throw Exception("Unknown ${it::class}")
                                    }
                                } + if (limit < size) "\n... and ${size - limit} more !" else ""
                ))
            }
        }
    }

    private val argsMap = mapOf(
            "team" to mapOf(
                    "name" to TeamsTable.name,
                    "slug" to TeamsTable.slug,
                    "href" to TeamsTable.href,
                    "facebook" to TeamsTable.facebook,
                    "forum" to TeamsTable.forum,
                    "irc" to TeamsTable.irc,
                    "twitter" to TeamsTable.twitter,
                    "discord" to TeamsTable.discord,
                    "rss" to TeamsTable.rss
            ),
            "comic" to mapOf(
                    "name" to ComicsTable.name,
                    "slug" to ComicsTable.slug,
                    "author" to PeopleTable.name,
                    "artist" to PeopleTable.name,
                    "description" to ComicsTable.description,
                    "adult" to ComicsTable.adult,
                    "created" to ComicsTable.created,
                    "small_thumb_url" to ComicsTable.small_thumb_url,
                    "big_thumb_url" to ComicsTable.big_thumb_url,
                    "href" to ComicsTable.href
            ),
            "chapter" to mapOf(
                    "name" to ChaptersTable.name,
                    "slug" to ChaptersTable.slug,
//                    "comic" to ChaptersTable.comic,
//                    "team" to ChaptersTable.team,
                    "chapter" to ChaptersTable.chapter,
                    "subchapter" to ChaptersTable.subchapter,
                    "volume" to ChaptersTable.volume,
                    "language" to ChaptersTable.language,
                    "description" to ChaptersTable.description,
                    "thumbnail" to ChaptersTable.thumbnail,
                    "created" to ChaptersTable.created,
                    "updated" to ChaptersTable.updated,
                    "href" to ChaptersTable.href
            )
    )
}