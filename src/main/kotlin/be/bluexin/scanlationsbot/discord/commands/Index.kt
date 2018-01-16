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

import be.bluexin.scanlationsbot.db.FoolslideEntity
import be.bluexin.scanlationsbot.discord.embedSuccess
import be.bluexin.scanlationsbot.discord.replyInChannel
import be.bluexin.scanlationsbot.rest.foolslide.Foolslide
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IMessage

object Index : Command {
    override val name = "index"

    override suspend fun process(trigger: IMessage) {
        requireOwner(trigger)

        val args = mapArgs(trigger)
        if (args[name]?.startsWith("fs") == true) handleFoolslide(args[name]!!.replace("fs", "").trim(), trigger)
    }

    private suspend fun handleFoolslide(url: String, trigger: IMessage) {
        if (url == "all") {
            var count = 0
            transaction {
                FoolslideEntity.all().map { it.url }
            }.forEach {
                Foolslide.fetchChaptersAsync(it)
                ++count
            }
            trigger.replyInChannel(embedSuccess("Indexed $count registered Foolslide-based website${if (count != 1) "s" else ""}."))
        } else {
            Foolslide.fetchChapters(url)
            trigger.replyInChannel(embedSuccess("Indexed Foolslide-based website at url `$url`."))
        }
    }
}