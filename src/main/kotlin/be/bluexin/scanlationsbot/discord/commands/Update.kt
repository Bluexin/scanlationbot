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

import be.bluexin.scanlationsbot.db.Team
import be.bluexin.scanlationsbot.db.TeamsTable
import be.bluexin.scanlationsbot.db.findFirstOrUpdate
import be.bluexin.scanlationsbot.discord.embedSuccess
import be.bluexin.scanlationsbot.discord.replyInChannel
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IMessage

object Update : Command {
    override val name = "update"

    suspend override fun process(trigger: IMessage) {
        requireOwner(trigger)

        val args = mapArgs(trigger)
        when (args[name]) {
            "team" -> updateTeam(args, trigger)
            null -> throw Exception("You need to specify what you want to update.")
            else -> throw Exception("Unknown update target: '${args[name]}'.")
        }
    }

    private fun updateTeam(args: Map<String, String?>, trigger: IMessage) {
        val slug = args["slug"]?: throw Exception("Please specify at least team slug.")
        val newslug = args["newslug"] ?: slug

        val name = args["name"]
        val href = args["href"]
        val facebook = args["facebook"]
        val forum = args["forum"]
        val irc = args["irc"]
        val twitter = args["twitter"]
        val discord = args["discord"]
        val rss = args["rss"]

        val team = transaction {
            Team.findFirstOrUpdate(
                    { TeamsTable.slug eq slug },
                    {
                        if (name != null) this.name = name
                        this.slug = newslug
                        if (href != null) this.href = href
                        if (facebook != null) this.facebook = facebook
                        if (forum != null) this.forum = forum
                        if (irc != null) this.irc = irc
                        if (twitter != null) this.twitter = twitter
                        if (discord != null) this.discord = discord
                        if (rss != null) this.rss = rss
                    }
            ).name
        }

        trigger.replyInChannel(embedSuccess("Updated $team."))
    }
}