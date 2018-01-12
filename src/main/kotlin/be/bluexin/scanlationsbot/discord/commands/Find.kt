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

import be.bluexin.scanlationsbot.db.*
import be.bluexin.scanlationsbot.discord.embedChapter
import be.bluexin.scanlationsbot.discord.embedComic
import be.bluexin.scanlationsbot.discord.embedError
import be.bluexin.scanlationsbot.discord.embedTeam
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IMessage
import kotlin.math.min

object Find : Command {
    override val name = "find"

    override fun process(trigger: IMessage) {
        val parts = trigger.content.split("--")
        if (parts.any { it.startsWith("team", ignoreCase = true) }) {
            handleTeams(parts, trigger)
            return
        }
        val slug = parts.firstOrNull { it.startsWith("slug", ignoreCase = true) }?.replace("slug", "")?.trim() ?: "*"
        val res = if (slug.contains('*')) transaction { Comic.find { ComicsTable.slug regexp slug.replace("^\\*".toRegex(), "") }.toList() }
        else transaction { Comic.find { ComicsTable.slug like slug }.toList() }
        val chStr = parts.firstOrNull { it.startsWith("ch", ignoreCase = true) }?.replace("ch", "")?.trim()
        if (chStr != null && res.count() == 1) {
            val matcher = "([0-9]+)".toRegex().find(chStr)
            if (matcher == null) {
                trigger.channel.sendMessage("Invalid chapter: $chStr")
                return
            }
            val chapter = matcher.value.toInt()
            val subchapter = matcher.next()?.value?.toInt() ?: 0
            val chs = transaction {
                Chapter.find { ChaptersTable.comic eq res[0].id and ((ChaptersTable.chapter eq chapter) and if (subchapter != 0) ChaptersTable.subchapter eq subchapter else ChaptersTable.subchapter.isNull()) }.toList()
            }

            when {
                chs.isEmpty() -> trigger.channel.sendMessage(embedError("Didn't find ${res[0]} chapter $chapter${if (subchapter != 0) ".$subchapter" else ""}."))
                chs.size == 1 -> trigger.channel.sendMessage(embedChapter(chs[0]))
                else -> trigger.channel.sendMessage("""
                    |Found ${chs.count()} chapter results for manga ${res[0].name}.
                    |${if (chs.count() > 5) "Displaying only the first 5 results.\n" else ""}
                    |${chs.subList(0, min(5, res.count())).joinToString(separator = "\n") { "${it.chapter}${if (it.subchapter != null) ".${it.subchapter}" else ""}: ${it.name} (${it.slug})" }}
                    |""".trimMargin()
                )
            }

            return
        }

        when {
            res.isEmpty() -> trigger.channel.sendMessage(embedError("Didn't find any manga with slug '$slug'"))
            res.size == 1 -> trigger.channel.sendMessage(embedComic(res[0]))
            else -> trigger.channel.sendMessage("""
                |Found ${res.count()} manga results with slug '$slug'.
                |${if (res.count() > 5) "Displaying only the first 5 results.\n" else ""}
                |${res.subList(0, min(5, res.count())).joinToString(separator = "\n") { "${it.name} (${it.slug})" }}
                |""".trimMargin())
        }
    }

    private fun handleTeams(parts: List<String>, trigger: IMessage) {
        val slug = parts.firstOrNull { it.startsWith("slug", ignoreCase = true) }?.replace("slug", "")?.trim() ?: "*"
        val res = if (slug.contains('*')) transaction { Team.find { TeamsTable.slug regexp slug.replace("^\\*".toRegex(), "") }.toList() }
        else transaction { Team.find { TeamsTable.slug like slug }.toList() }

        when {
            res.isEmpty() -> trigger.channel.sendMessage(embedError("Didn't find any team with slug '$slug'"))
            res.size == 1 -> trigger.channel.sendMessage(embedTeam(res[0]))
            else -> trigger.channel.sendMessage("""
                |Found ${res.count()} team results with slug '$slug'.
                |${if (res.count() > 5) "Displaying only the first 5 results.\n" else ""}
                |${res.subList(0, min(5, res.count())).joinToString(separator = "\n") { "${it.name} (${it.slug})" }}
                |""".trimMargin())
        }
    }
}