@file:Suppress("unused")

package be.bluexin.scanlationsbot.db

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ComicsTable : IntIdTable() {
    val name = varchar("name", 200).uniqueIndex()
    val slug = varchar("slug", 200).uniqueIndex()
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
    val name = varchar("name", 200).uniqueIndex()
}

class Person(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Person>(PeopleTable)

    var name by PeopleTable.name
}

object ChaptersTable : IntIdTable() {
    val name = varchar("name", 200)
    val slug = varchar("slug", 200)
    val comic = reference("comic", ComicsTable)
    val team = reference("team", TeamsTable)
    val chapter = integer("chapter")
    val subchapter = integer("subchapter").nullable()
    val volume = integer("volume")
    val language = varchar("language", 10) // lang code, support for basic or extended format?
    val description = text("description").nullable()
    val thumbnail = text("thumbnail")
    val created = datetime("created")
    val updated = datetime("updated").nullable()
    val href = text("href")
}

class Chapter(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Chapter>(ChaptersTable)

    var name by ChaptersTable.name
    var slug by ChaptersTable.slug
    var comic by Comic referencedOn ChaptersTable.comic
    var team by Team referencedOn ChaptersTable.team
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
    val name = varchar("name", 200).uniqueIndex()
    val slug = varchar("slug", 200).uniqueIndex()
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