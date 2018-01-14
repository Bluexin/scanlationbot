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

import be.bluexin.scanlationsbot.Global
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient

object ScanBot {

    private var client: IDiscordClient? = null

    private fun init(client: IDiscordClient) {
        this.client = client
        client.dispatcher.registerListener(EventHandler)
    }

    fun login() {
        println("Logging bot in...")
        init(ClientBuilder()
                .withToken(Global.settings.token)
                .withRecommendedShardCount()
                .login()
        )
    }
    // Invite link for teh bot: https://discordapp.com/oauth2/authorize?&client_id=203910186270851073&scope=bot (no perms cuz cba)
    // See BotInviteBuilder
}