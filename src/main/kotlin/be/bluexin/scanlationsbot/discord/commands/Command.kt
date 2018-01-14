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

import be.bluexin.scanlationsbot.discord.embedError
import kotlinx.coroutines.experimental.launch
import sx.blah.discord.handle.obj.IMessage

/**
 * Base interface for all bot commands.
 */
interface Command {

    /**
     * Name for this command.
     */
    val name: String

    /**
     * List of aliases for this command (excluding [name]).
     */
    val aliases: List<String>
        get() = listOf()

    /**
     * Called when this command is used.
     */
    suspend fun process(trigger: IMessage)

    /**
     * Help string
     */
    val help: String?
        get() = null
}

enum class Commands(val command: Command) {
    FIND(Find),
    HELP(Help),
    INDEX(Index);

    private fun canProcess(trigger: IMessage)
            = with(trigger.content.replace("<@!?[0-9]+>".toRegex(), "").trim()) {
        startsWith(command.name, ignoreCase = true) || command.aliases.any { this@with.startsWith(it, ignoreCase = true) }
    }

    companion object {
        fun process(trigger: IMessage) {
            launch {
                try {
                    Commands.values().firstOrNull {
                        it.canProcess(trigger)
                    }?.command?.process(trigger)
                } catch (e: Throwable) {
                    trigger.channel.sendMessage(embedError("An error occurred. ${e.message}"))
                    e.printStackTrace()
                }
            }
        }
    }
}