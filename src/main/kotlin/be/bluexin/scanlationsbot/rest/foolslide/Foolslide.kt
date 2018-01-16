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

package be.bluexin.scanlationsbot.rest.foolslide

import be.bluexin.scanlationsbot.db.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject
import com.github.kittinunf.result.Result
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.BatchUpdateException

//FuelManager.instance.basePath = "https://hatigarmscans.eu/hs/api"
//    FuelManager.instance.basePath = "https://jaiminisbox.com/reader/api"

// /status/info -> status.json

/*
args :
/per_page/x -> x per page (default 30, max 100)
/page/x -> page x
 */

object Foolslide {
    private const val comics = "/reader/comics"
    private const val chapters = "/reader/chapters"

    private const val limit = "/per_page/100"
    private fun page(i: Int) = "/page/$i"
    private const val order = "/orderby/desc_created"

    suspend fun fetchComics(baseUrl: String) {
        var i = 1
        var cont = true
        while (cont) {
            val (_, response, result) = "${baseUrl.replace("/\$".toRegex(), "")}$comics$limit${page(i++)}".httpGet().responseObject<FsComics>()
            if (response.statusCode != 200) {
                println("Received ${response.statusCode} when querying ${response.url}")
                break
            }
            val comics = result.get().comics
            cont = comics.size > 99
            println("Fetched ${comics.size} comics from $baseUrl")

            launch {
                comics.forEach {
                    if (isActive) {
                        println("Found ${it.name} by ${it.author} (drawn by ${it.artist})")
                        try {
                            transaction {
                                Comic.findOrUpdate(it)
                            }
                        } catch (e: BatchUpdateException) {
                            println("Failed to store a Comic.\n$it")
                        }
                    }
                }
            }
        }
    }

    fun fetchComicsAsync(baseUrl: String) = async {
        fetchComics(baseUrl)
    }

    suspend fun fetchChapters(baseUrl: String) {
        var i = 1
        var cont = true
        while (cont) {
            val (_, response, result) = "${baseUrl.replace("/\$".toRegex(), "")}$chapters$order$limit${page(i++)}".httpGet().responseObject<FsChapters>()
            if (response.statusCode != 200) {
//                println("Received ${response.statusCode} when querying ${response.url}")
                println(response)
                throw (result as Result.Failure).error
            }
            val chapters = result.get().chapters
            cont = chapters.size > 99
            println("Fetched ${chapters.size} chapters from $baseUrl")

            launch {
                chapters.forEach {
                    if (isActive) {
                        println("Found ${it.comic.name} chapter ${it.chapter.chapter}${if (it.chapter.subchapter != 0) "." + it.chapter.subchapter else ""}")
                        try {
                            transaction {
                                val ch = Chapter.findOrUpdate(it)
                                if (ch.team != null) FoolslideEntity.findFirstOrUpdate(
                                        { FoolsTable.team eq ch.team!!.id },
                                        {
                                            team = ch.team!!
                                            url = baseUrl
                                        }
                                )
                            }
                        } catch (e: BatchUpdateException) {
                            println("Failed to store a Comic.\n$it")
                        }
                    }
                }
            }
        }
    }

    fun fetchChaptersAsync(baseUrl: String) = async {
        fetchChapters(baseUrl)
    }
}