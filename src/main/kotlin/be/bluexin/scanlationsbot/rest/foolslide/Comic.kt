package be.bluexin.scanlationsbot.rest.foolslide

import java.time.LocalDateTime

data class Comics(
        val comics: List<Comic>
)

data class Comic(
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
         * image.jpg (need to find endpoint)
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