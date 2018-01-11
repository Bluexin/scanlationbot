package be.bluexin.scanlationsbot.rest.foolslide

import java.time.LocalDateTime

data class Team(
        val id: Int,
        val name: String,
        val stub: String,
        val url: String,
        val forum: String,
        val irc: String,
        val twitter: String,
        val facebook: String,
        val facebookid: String,
        val created: LocalDateTime,
        val lastseen: LocalDateTime,
        val updated: LocalDateTime,
        val creator: Int,
        val editor: Int,
        val href: String
)