package com.odds_service.client;

import com.odds_service.dto.MatchResponse;
import com.odds_service.dto.SyncStatusResponse;
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
    private final String matchesSyncStatusPath;

    public MatchServiceClient(RestClient matchRestClient, @Value("${match-service.matches-path}") String matchesPath,
    		ServiceTokenProvider tokenProvider, 
    		@Value("${match-service.matches-sync-status-path}") String matchesSyncStatusPath) {
        this.matchRestClient = matchRestClient;
        this.matchesPath = matchesPath;
        this.tokenProvider = tokenProvider;
        this.matchesSyncStatusPath = matchesSyncStatusPath;
    }

    public List<MatchResponse> getMatches() {
        List<MatchResponse> matches = matchRestClient.get()
            .uri(matchesPath)
            .headers(headers -> headers.setBearerAuth(tokenProvider.createToken()))
            .retrieve()
            .body(new ParameterizedTypeReference<List<MatchResponse>>() {});

        return matches == null ? List.of() : matches;
    }
    
    public boolean isInitialSyncCompleted() {
        SyncStatusResponse response = matchRestClient.get()
            .uri(matchesSyncStatusPath)
            .headers(headers -> headers.setBearerAuth(tokenProvider.createToken()))
            .retrieve()
            .body(SyncStatusResponse.class);

        return response != null && response.ready();
    }
}