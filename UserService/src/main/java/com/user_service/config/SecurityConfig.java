package com.user_service.config;

import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/user/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/user/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/user/internal/**").hasAnyRole("ADMIN","SERVICE")
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
            	.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setHeader(
                        HttpHeaders.WWW_AUTHENTICATE,
                        "Bearer"
                    );

                    response.getWriter().write(
                        "{\"mensaje\":\"Token JWT inválido o caducado\"}"
                    );
                })
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    response.getWriter().write(
                        "{\"mensaje\":\"Debes autenticarte con un token JWT válido\"}"
                    );
                })
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/user/register")
                .ignoringRequestMatchers("/auth/login")
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.secret}") String secret) {

        byte[] keyBytes = Base64.getDecoder().decode(secret);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(key)
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");

            if (role == null || role.isBlank()) {
                return List.of();
            }

            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });

        return converter;
    }
}