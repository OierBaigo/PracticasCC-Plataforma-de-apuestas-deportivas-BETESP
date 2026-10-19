package com.bets_service.config;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {

        byte[] keyBytes = Base64.getDecoder().decode(secret);

        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
            	    .jwt(Customizer.withDefaults())
            	    .authenticationEntryPoint((request, response, exception) -> {
            	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	        response.setContentType("application/json");
            	        response.setCharacterEncoding("UTF-8");
            	        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");

            	        response.getWriter().write(
            	            "{\"mensaje\":\"Token JWT inválido o caducado\"}"
            	        );
            	    })
            )
            .headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}

