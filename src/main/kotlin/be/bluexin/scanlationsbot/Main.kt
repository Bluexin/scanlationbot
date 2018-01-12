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

import be.bluexin.scanlationsbot.db.ChaptersTable
import be.bluexin.scanlationsbot.db.ComicsTable
import be.bluexin.scanlationsbot.db.PeopleTable
import be.bluexin.scanlationsbot.db.TeamsTable
import be.bluexin.scanlationsbot.rest.foolslide.Foolslide
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.jackson.mapper
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.SQLException
import java.time.LocalDateTime
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println("Hello world !")

    val settings: BotSettings
    val settingsFile = File("bot.json")
    if (!settingsFile.exists()) {
        mapper.writeValue(settingsFile, BotSettings(
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
            settings = mapper.readValue<BotSettings>(settingsFile)
        } catch (e: Exception) {
            println("Config file couldn't be parsed. Please consider deleting it to regenerate it.")
            exitProcess(1)
        }
    }

    val module = SimpleModule("customldt")
    module.addDeserializer(LocalDateTime::class.java, DateTimeDeserializer())
    module.addDeserializer(Boolean::class.java, BooleanDeserializer())

    mapper.registerModule(module)

    FuelManager.instance.baseHeaders = mapOf(
            "User-Agent" to "Discord Bot",
            "Accept-Encoding" to "gzip",
            "Accept-Language" to "en-US,en;q=0.5",
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Contact" to settings.hostemail
    )

    val db = if (settings.dbpassword == null) Database.connect(settings.dburl, "com.mysql.cj.jdbc.Driver", user = settings.dbuser)
    else Database.connect(settings.dburl, "com.mysql.cj.jdbc.Driver", user = settings.dbuser, password = settings.dbpassword)
    try {
        println("Connected using ${db.vendor} database on version ${db.version}")
        transaction {
            SchemaUtils.create(
                    ComicsTable,
                    PeopleTable,
                    ChaptersTable,
                    TeamsTable
            )
        }
    } catch (e: SQLException) {
        println("Couldn't connect to database.")
        e.printStackTrace()
        exitProcess(1)
    }

    runBlocking {
        Foolslide.fetchComics("https://hatigarmscans.eu/hs/api").join()
        Foolslide.fetchChapters("https://hatigarmscans.eu/hs/api").join()
    }
}
