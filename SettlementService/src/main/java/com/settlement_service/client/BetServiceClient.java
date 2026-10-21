package com.settlement_service.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.settlement_service.dto.BetResponse;
import com.settlement_service.dto.UpdateBetStatusRequest;
import com.settlement_service.security.ServiceTokenProvider;

@Component
public class BetServiceClient {
	private final RestClient betRestClient;
    private final String betPath;
    private final ServiceTokenProvider serviceTokenProvider;

    public BetServiceClient(@Qualifier("betRestClient") RestClient betRestClient, @Value("${bet-service.bets-path}") String betPath,
    		ServiceTokenProvider serviceTokenProvider) {
        this.betRestClient = betRestClient;
        this.betPath = betPath;
        this.serviceTokenProvider = serviceTokenProvider;
    }

    public List<BetResponse> getPendingBetsByMatch(Long matchId) {
        return betRestClient.get()
                .uri(betPath, matchId)
                .headers(headers -> headers.setBearerAuth(serviceTokenProvider.createToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<List<BetResponse>>() {});
    }
    
    public void updateBetStatus(Long betId, String status) {
        betRestClient.post()
                .uri("/bets/internal/{betId}/status", betId)
                .headers(headers -> headers.setBearerAuth(serviceTokenProvider.createToken()))
                .body(new UpdateBetStatusRequest(status))
                .retrieve()
                .toBodilessEntity();
    }
}
