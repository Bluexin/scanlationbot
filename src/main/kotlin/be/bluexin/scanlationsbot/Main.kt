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

package be.bluexin.scanlationsbot

import be.bluexin.scanlationsbot.Global.settings
import be.bluexin.scanlationsbot.db.setupDB
import be.bluexin.scanlationsbot.discord.ScanBot
import be.bluexin.scanlationsbot.rest.setupRest
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.jackson.mapper
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val settingsFile = File("bot.json")
    if (!settingsFile.exists()) {
        mapper.writerWithDefaultPrettyPrinter().writeValue(settingsFile, BotSettings(
                token = "private bot token",
                owners = listOf(),
                dburl = "database url (in the form of jdbc:mysql://ip:port/database)",
                dbuser = "database username",
                dbpassword = "database password",
                hostemail = "host e-mail"
        ))
        println("Default config file was generated. Please edit with the correct info.")
        exitProcess(0)
    } else {
        try {
            settings = mapper.readValue(settingsFile)
        } catch (e: Exception) {
            println("Config file couldn't be parsed. Please consider deleting it to regenerate it.")
            exitProcess(1)
        }
    }

    val restSetup = launch {
        setupRest()
    }

    val dbSetup = launch {
        setupDB()
    }

    ScanBot.login()

    runBlocking {
        restSetup.join()
        dbSetup.join()
    }
}

object Global {
    lateinit var settings: BotSettings
}
