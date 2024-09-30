package com.spotify.currentlyPlaying.infrastracture.inbound

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
class SpotifyController(
    @Autowired private val clientRegistrationRepository: ClientRegistrationRepository,
    @Autowired private val authorizedClientService: OAuth2AuthorizedClientService
) {
    private val logger: Logger = LoggerFactory.getLogger(SpotifyController::class.java)

    @GetMapping("/currentSong")
    @CircuitBreaker(name = "spotify", fallbackMethod = "fallbackCurrentSong")
    fun getCurrentSong( @RegisteredOAuth2AuthorizedClient("spotify") authorizedClient: OAuth2AuthorizedClient?): String {
        logger.info("Fetching current song from Spotify")

        val restTemplate = RestTemplate()
        val accessToken = authorizedClient?.accessToken?.tokenValue

        val headers = org.springframework.http.HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
        }
        val entity = org.springframework.http.HttpEntity<String>(headers)
        val response = restTemplate.exchange(
            "https://api.spotify.com/v1/me/player/currently-playing",
            org.springframework.http.HttpMethod.GET,
            entity,
            String::class.java
        )

        return response.body ?: "No song is currently playing."
    }

    fun fallbackCurrentSong(exception: Throwable): String {
        return "Spotify service is currently unavailable. Please try again later."
    }

}