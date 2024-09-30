package com.spotify.currentlyPlaying.application.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

class CustomOAuth2ErrorHandler: DefaultOAuth2AuthorizationRequestResolver {
    override fun resolve(request: HttpServletRequest?): OAuth2AuthorizationRequest? {
        val authorizationRequest = super.resolve(request)
        if (authorizationRequest != null) {
            // Logica para manejar diferentes tipos de autenticacion, como 'client_secret_basic'
            if (authorizationRequest.authorizationRequestUri.contains("client_secret_basic")) {
                throw OAuth2AuthenticationException(OAuth2Error("unsupported_auth_method", "Spotify utiliza un método de autenticación no soportado.", null))
            }
        }
        return authorizationRequest
    }
}