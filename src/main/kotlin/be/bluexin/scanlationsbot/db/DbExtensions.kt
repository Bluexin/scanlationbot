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

package be.bluexin.scanlationsbot.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import java.time.LocalDateTime

fun <T : IntEntity> IntEntityClass<T>.findFirstOrUpdate(find: SqlExpressionBuilder.() -> Op<Boolean>, update: T.() -> Unit)
        = this.find(find).firstOrNull()?.also { it.update() } ?: this.new(update)

fun findOrCreatePerson(name: String)
        = Person.findFirstOrUpdate({ PeopleTable.name like name }, { this.name = name })

fun LocalDateTime.toJoda() = DateTime(year, month.value, dayOfMonth, hour, minute, second)
fun DateTime.toJava() = LocalDateTime.of(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute)

infix fun<T:Any?> ExpressionWithColumnType<T>.regexp(pattern: String): Op<Boolean> = RegexpOp(this, QueryParameter(pattern, columnType))