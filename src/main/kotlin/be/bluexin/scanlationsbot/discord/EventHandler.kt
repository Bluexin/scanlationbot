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

import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent

object EventHandler {

    var ready = false

    @EventSubscriber
    fun onMention(e: MentionEvent) {
        if (!e.author.isBot) e.channel.sendMessage("I'm awake! I can't do anything yet though :c")
    }

    @EventSubscriber
    fun onReady(e: ReadyEvent) {
        println("Logged in as ${e.client.ourUser.name} (using app ${e.client.applicationName}).")
        e.client.online("Scanning your mind")
        ready = true
    }
}