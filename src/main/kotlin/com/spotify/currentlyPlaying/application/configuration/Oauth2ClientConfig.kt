package com.spotify.currentlyPlaying.application.configuration

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.web.client.HttpClientErrorException


@Configuration
class Oauth2ClientConfig {
    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientRepository: OAuth2AuthorizedClientRepository
    ): OAuth2AuthorizedClientManager {

        val authorizedClientProvider: OAuth2AuthorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .refreshToken()
                .clientCredentials()
                .build()

        return OAuth2AuthorizedClientManager {
                clientRegistrationRepository,
                authorizedClientRepository
        }.apply {
            setAuthorizedClientProvider(authorizedClientProvider)
        }
    }

    @Bean
    fun customAuthorizationRequestResolver(
        clientRegistrationRepository: ClientRegistrationRepository,
        authorizedClientService: OAuth2AuthorizedClientService
    ): DefaultOAuth2AuthorizationRequestResolver {
        return object : DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository,
            "/oauth2/authorization"
        ) {
            override fun resolve(request: HttpServletRequest?): OAuth2AuthorizationRequest? {
                val authorizationRequest = super.resolve(request)
                if (authorizationRequest != null) {
                    // Agrega lógica para manejar diferentes tipos de autenticación aquí
                    if (authorizationRequest.authorizationRequestUri.contains("client_secret_basic")) {
                        throw OAuth2AuthenticationException(
                            OAuth2Error("unsupported_auth_method", "Spotify utiliza un método de autenticación no soportado.", null)
                        )
                    }
                }
                return authorizationRequest
            }
        }
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}