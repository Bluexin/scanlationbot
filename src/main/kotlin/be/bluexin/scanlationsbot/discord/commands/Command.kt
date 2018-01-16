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
import be.bluexin.scanlationsbot.discord.ScanBot
import be.bluexin.scanlationsbot.discord.embedError
import be.bluexin.scanlationsbot.discord.replyInChannel
import kotlinx.coroutines.experimental.launch
import sx.blah.discord.handle.obj.IMessage
import java.time.LocalDateTime

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

    fun requireOwner(trigger: IMessage) {
        // TODO: more fine-grained permissions, ie allow some people to edit perms/data regarding their own team etc
        if (trigger.author.longID !in Global.settings.owners) throw Exception("You need to be owner to use this command.")
    }

    fun String.startsWith(prefix: String) = startsWith(prefix, false)

    fun mapArgs(trigger: IMessage) = trigger.content.split("--").asSequence().flatMap {
        val s = it.replace("${ScanBot.client!!.ourUser.mention(true)}|${ScanBot.client!!.ourUser.mention(false)}".toRegex(), "").trim()
        val key = s.split(' ')[0].toLowerCase()
        val value = if (s.contains(' ')) s.substring(s.indexOf(' '), s.length).trim() else null
        if (value != null) sequenceOf(key to value) else sequenceOf()
    }.toMap()
}

enum class Commands(val command: Command) {
    DELETE(Delete),
    FIND(Find),
    HELP(Help),
    INDEX(Index),
    UPDATE(Update);

    private fun canProcess(trigger: IMessage)
            = with(trigger.content.replace("${ScanBot.client!!.ourUser.mention(true)}|${ScanBot.client!!.ourUser.mention(false)}".toRegex(), "").trim()) {
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
                    trigger.replyInChannel(embedError("An error occurred: ${e.message}"))
                    System.err.print("${LocalDateTime.now()}: ")
                    e.printStackTrace() // TODO: use proper logger
                }
            }
        }
    }
}