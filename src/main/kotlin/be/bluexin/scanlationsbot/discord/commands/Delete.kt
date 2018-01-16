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
import be.bluexin.scanlationsbot.discord.embedNeutral
import be.bluexin.scanlationsbot.discord.embedSuccess
import be.bluexin.scanlationsbot.discord.replyInChannel
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.IMessage

object Delete : Command {

    private val confirmationList = mutableMapOf<String, String>()

    override val name = "delete"

    suspend override fun process(trigger: IMessage) {
        requireOwner(trigger)

        val args = mapArgs(trigger)
        when (args[name]) {
            "team" -> deleteTeam(args, trigger)
            "confirm" -> confirm(args, trigger)
            "pending" -> pending(args, trigger)
            null -> throw Exception("You need to specify what you want to delete.")
            else -> throw Exception("Unknown deletion target: '${args[name]}'.")
        }
    }

    private fun deleteTeam(args: Map<String, String>, trigger: IMessage) {
        val slug = args["slug"] ?: throw Exception("Please specify a team slug.")

        val idx = confirmationList[slug]
        if (idx == "team") {
            throw Exception("Team already marked for deletion. Please confirm using `delete confirm --slug $slug`")
        }

        val team = transaction {
            Team.find { TeamsTable.slug eq slug }.firstOrNull()?.name
        }

        if (team == null) {
            throw Exception("Couldn't find a team with slug '$slug'.")
        } else {
            confirmationList[slug] = "team"
            trigger.replyInChannel(embedSuccess("Marked $team for deletion. Please confirm using `delete confirm --slug $slug`"))
        }
    }

    private fun confirm(args: Map<String, String>, trigger: IMessage) {
        val slug = args["slug"] ?: throw Exception("Please specify a slug.")

        val name = when (confirmationList[slug] ?: throw Exception("No '$slug' marked for deletion.")) {
            "team" -> transaction {
                val team = Team.find { TeamsTable.slug eq slug }.firstOrNull()?: return@transaction null
                val name = team.name
                team.delete()
                name
            }
            else -> null
        }

        confirmationList.remove(slug)

        if (name == null) throw Exception("Unspecified.")
        else trigger.replyInChannel(embedSuccess("Deleted $name"))
    }

    private fun pending(args: Map<String, String>, trigger: IMessage) {
        if (confirmationList.isEmpty()) throw Exception("No pending deletion.")
        else {
            trigger.replyInChannel(embedNeutral("List of pending deletions:\n${confirmationList.asSequence().joinToString(separator = "\n", transform = { "${it.value}: ${it.key}" })}"))
        }
    }
}