package com.odds_service.client;

import com.odds_service.dto.MatchResponse;
import com.odds_service.security.ServiceTokenProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class MatchServiceClient {

    private final RestClient matchRestClient;
    private final String matchesPath;
    private final ServiceTokenProvider tokenProvider;

    public MatchServiceClient(RestClient matchRestClient, @Value("${match-service.matches-path}") String matchesPath,
    		ServiceTokenProvider tokenProvider) {
        this.matchRestClient = matchRestClient;
        this.matchesPath = matchesPath;
        this.tokenProvider = tokenProvider;
    }

    public List<MatchResponse> getMatches() {
        List<MatchResponse> matches = matchRestClient.get()
            .uri(matchesPath)
            .headers(headers -> headers.setBearerAuth(tokenProvider.createToken()))
            .retrieve()
            .body(new ParameterizedTypeReference<List<MatchResponse>>() {});

        return matches == null ? List.of() : matches;
    }
}