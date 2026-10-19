package com.bets_service.client;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class AuthorizedRequest {

    private AuthorizedRequest() {}

    public static String currentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }

        throw new IllegalStateException("No se encontró un JWT autenticado");
    }
}