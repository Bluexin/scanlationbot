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

package be.bluexin.scanlationsbot.rest

import be.bluexin.scanlationsbot.BooleanDeserializer
import be.bluexin.scanlationsbot.DateTimeDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.jackson.mapper
import java.time.LocalDateTime

fun setupRest(hostemail: String) {
    println("Setting up REST...")
    val module = SimpleModule("customldt")
    module.addDeserializer(LocalDateTime::class.java, DateTimeDeserializer())
    module.addDeserializer(Boolean::class.java, BooleanDeserializer())

    mapper.registerModule(module)

    FuelManager.instance.baseHeaders = mapOf(
            "User-Agent" to "Discord Bot",
            "Accept-Encoding" to "gzip",
            "Accept-Language" to "en-US,en;q=0.5",
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Contact" to hostemail
    )
    println("Set up REST!")
}