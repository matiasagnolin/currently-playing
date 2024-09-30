package com.spotify.currentlyPlaying

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CurrentlyPlayingApplication

fun main(args: Array<String>) {
	runApplication<CurrentlyPlayingApplication>(*args)
}
