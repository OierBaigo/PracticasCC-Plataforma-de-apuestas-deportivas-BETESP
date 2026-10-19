package com.bets_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.bets_service.dto.MatchResponse;

@Component
public class MatchServiceClient {

    private final RestClient restClient;

    public MatchServiceClient(@Qualifier("matchRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public MatchResponse getMatch(Long matchId) {
        return restClient.get()
                .uri("/matches/{id}", matchId)
                .headers(headers -> headers.setBearerAuth(AuthorizedRequest.currentToken()))
                .retrieve()
                .body(MatchResponse.class);
    }
}