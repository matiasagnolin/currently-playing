package com.spotify.currentlyPlaying.infrastracture.inbound

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class UserController {
    @GetMapping("/")
    fun index(): String {
        return "index"  // Show a login page or home
    }

    @GetMapping("/login")
    fun login(): RedirectView {
        return RedirectView("/oauth2/authorization/spotify")  // Redirect to Spotify OAuth2 login
    }

}