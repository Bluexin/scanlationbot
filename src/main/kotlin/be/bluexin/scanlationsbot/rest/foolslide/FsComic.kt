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

import java.time.LocalDateTime

data class FsComics(
        val comics: List<FsComic>
)

data class FsComic(
        val id: Int,
        val name: String,
        val stub: String,
        /**
         * Alphanumeric
         */
        val uniqid: String,
        /**
         * Seems to be 0 or 1 as string
         */
        val hidden: Int,
        val author: String,
        val artist: String,
        val description: String,
        /**
         * image.jpg (see [fullsized_thumb_url])
         */
        val thumbnail: String,
        /**
         * ?
         */
        val customchapter: String,
        val format: Boolean,
        val adult: Boolean,
        val created: LocalDateTime,
        /**
         * Date, didn't see anything but 0's
         */
        val lastseen: LocalDateTime,
        val updated: LocalDateTime,
        val creator: Int,
        val editor: Int,
        /**
         * Full URL
         */
        val thumb_url: String,
        /**
         * Full URL
         */
        val fullsized_thumb_url: String,
        /**
         * Base URL for chapters
         */
        val href: String
)