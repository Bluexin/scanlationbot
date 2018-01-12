package be.bluexin.scanlationsbot.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.joda.time.DateTime
import java.time.LocalDateTime

fun <T : IntEntity> IntEntityClass<T>.findFirstOrCreate(find: SqlExpressionBuilder.() -> Op<Boolean>, create: T.() -> Unit)
        = this.find(find).firstOrNull() ?: this.new(create)

fun findOrCreatePerson(name: String)
        = Person.findFirstOrCreate({ PeopleTable.name like name }, { this.name = name })

fun LocalDateTime.toJoda() = DateTime(year, month.value, dayOfMonth, hour, minute, second)