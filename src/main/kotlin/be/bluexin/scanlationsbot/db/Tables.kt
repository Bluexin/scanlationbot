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

@file:Suppress("unused")

package be.bluexin.scanlationsbot.db

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ComicsTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val slug = varchar("slug", 255).uniqueIndex()
    val author = reference("author", PeopleTable).nullable()
    val artist = reference("artist", PeopleTable).nullable()
    val description = text("description").nullable()
    val adult = bool("adult")
    val created = datetime("created")
    val small_thumb_url = text("small_thumb_url").nullable()
    val big_thumb_url = text("big_thumb_url").nullable()
    val href = text("href")
}

class Comic(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Comic>(ComicsTable)

    var name by ComicsTable.name
    var slug by ComicsTable.slug
    var author by Person optionalReferencedOn ComicsTable.author
    var artist by Person optionalReferencedOn ComicsTable.artist
    var description by ComicsTable.description
    var adult by ComicsTable.adult
    var created by ComicsTable.created
    var small_thumb_url by ComicsTable.small_thumb_url
    var big_thumb_url by ComicsTable.big_thumb_url
    var href by ComicsTable.href
}

object PeopleTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
}

class Person(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Person>(PeopleTable)

    var name by PeopleTable.name
}

object ChaptersTable : IntIdTable() {
    val name = varchar("name", 255)
    val slug = varchar("slug", 255)
    val comic = reference("comic", ComicsTable)
    val team = reference("team", TeamsTable).nullable()
    val chapter = integer("chapter")
    val subchapter = integer("subchapter").nullable()
    val volume = integer("volume")
    val language = varchar("language", 10) // lang code, support for basic or extended format?
    val description = text("description").nullable()
    val thumbnail = text("thumbnail").nullable()
    val created = datetime("created")
    val updated = datetime("updated").nullable()
    val href = text("href")
}

class Chapter(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Chapter>(ChaptersTable)

    var name by ChaptersTable.name
    var slug by ChaptersTable.slug
    var comic by Comic referencedOn ChaptersTable.comic
    var team by Team optionalReferencedOn ChaptersTable.team
    var chapter by ChaptersTable.chapter
    var subchapter by ChaptersTable.subchapter
    var volume by ChaptersTable.volume
    var language by ChaptersTable.language
    var description by ChaptersTable.description
    var thumbnail by ChaptersTable.thumbnail
    var created by ChaptersTable.created
    var updated by ChaptersTable.updated
    var href by ChaptersTable.href
}

object TeamsTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex()
    val slug = varchar("slug", 255).uniqueIndex()
    val href = text("href").nullable()
    val facebook = text("facebook").nullable()
    val forum = text("forum").nullable()
    val irc = text("irc").nullable()
    val twitter = text("twitter").nullable()
    val discord = text("discord").nullable()
}

class Team(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Team>(TeamsTable)

    var name by TeamsTable.name
    var slug by TeamsTable.slug
    var href by TeamsTable.href
    var facebook by TeamsTable.facebook
    var forum by TeamsTable.forum
    var irc by TeamsTable.irc
    var twitter by TeamsTable.twitter
    var discord by TeamsTable.discord
}

// TODO: Genres, Tags, Publishers, Authors, Artists, Alternative Names, Staff members managing said series