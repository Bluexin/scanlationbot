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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class DateTimeDeserializer : StdDeserializer<LocalDateTime>(LocalDateTime::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        return try {
            LocalDateTime.parse(p.valueAsString.replace(' ', 'T'))
        } catch (e: DateTimeParseException) {
            LocalDateTime.MIN
        }
    }
}

class BooleanDeserializer: StdDeserializer<Boolean>(Boolean::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Boolean {
        return p.valueAsString == "true" || p.valueAsInt != 0
    }

}