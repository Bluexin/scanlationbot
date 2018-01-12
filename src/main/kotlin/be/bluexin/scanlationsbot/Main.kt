package be.bluexin.scanlationsbot

import be.bluexin.scanlationsbot.db.*
import be.bluexin.scanlationsbot.rest.foolslide.Chapters
import be.bluexin.scanlationsbot.rest.foolslide.Comics
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.mapper
import com.github.kittinunf.fuel.jackson.responseObject
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.BatchUpdateException
import java.sql.SQLException
import java.time.LocalDateTime

fun main(args: Array<String>) {
    println("Hello world !")

    val module = SimpleModule("customldt")
    module.addDeserializer(LocalDateTime::class.java, DateTimeDeserializer())
    module.addDeserializer(Boolean::class.java, BooleanDeserializer())

    mapper.registerModule(module)

    FuelManager.instance.baseHeaders = mapOf(
            "User-Agent" to "Discord Bot",
            "Accept-Encoding" to "gzip",
            "Accept-Language" to "en-US,en;q=0.5",
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Contact" to "bluexin.gamesATgmail.com"
    )
    FuelManager.instance.basePath = "https://hatigarmscans.eu/hs/api"
//    FuelManager.instance.basePath = "https://jaiminisbox.com/reader/api"

    val db = Database.connect("jdbc:mysql://localhost:3306/scanlationsdev", "com.mysql.cj.jdbc.Driver", user = "scanlations-dev", password = "scanbot")
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
        return
    }

//    val comics = getComicsBlocking().comics
    val comics = mapper.readValue<Comics>(File("samples/comics.json")).comics
    println("Found ${comics.size} comics at ${FuelManager.instance.basePath}")
    comics.forEach {
        println("Found ${it.name} by ${it.author} (drawn by ${it.artist})")
        transaction {
            if (Comic.find { ComicsTable.slug like it.stub }.empty()) {
                println("Adding ${it.name} (${it.stub}) to db...")
                try {
                    Comic.new {
                        name = it.name
                        slug = it.stub
                        if (it.author.isNotBlank()) author = findOrCreatePerson(it.author)
                        if (it.artist.isNotBlank()) artist = findOrCreatePerson(it.artist)
                        description = it.description
                        adult = it.adult
                        created = it.created.toJoda()
                        small_thumb_url = it.thumb_url
                        big_thumb_url = it.fullsized_thumb_url
                        href = it.href
                    }
                } catch (e: BatchUpdateException) {
                    println("Failed to store a Comic.\n$it")
                }
                println("Done !")
            }
        }
    }

//    val chapters = getReleasedChapters().chapters
    val chapters = mapper.readValue<Chapters>(File("samples/chapters.json")).chapters
    println("Found ${chapters.size} chapters at ${FuelManager.instance.basePath}")
    chapters.forEach {
        println("Released ${it.comic.name} chapter ${it.chapter.chapter}${if (it.chapter.subchapter != 0) {
            "." + it.chapter.subchapter
        } else ""}: ${it.chapter.name}\n\tat ${it.chapter.href} on ${it.chapter.created}${if (it.chapter.updated != LocalDateTime.MIN) " (updated ${it.chapter.updated})" else ""}\n\t${it.chapter.title}")
    }
}

fun getComicsBlocking() = "/reader/comics/per_page/100/".httpGet().responseObject<Comics>().third.get()

fun getReleasedChapters() = "/reader/chapters/orderby/desc_created/".httpGet().responseObject<Chapters>().third.get()

// /reader/chapters/orderby/desc_created/ -> chapters.json

// /status/info -> status.json

/*
args :
/per_page/x -> x per page (default 30, max 100)
/page/x -> page x
 */
