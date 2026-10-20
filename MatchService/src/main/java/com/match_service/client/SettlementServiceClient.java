package com.match_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.match_service.security.ServiceTokenProvider;

@Component
public class SettlementServiceClient {
	
	private final RestClient restClient;
    private final ServiceTokenProvider serviceTokenProvider;
    @Value("${settlement-service.finished-matches-path}")
    private String finishedMatchesPath;

    public SettlementServiceClient(@Qualifier("settlementRestClient") RestClient restClient, ServiceTokenProvider serviceTokenProvider) {
        this.restClient = restClient;
        this.serviceTokenProvider = serviceTokenProvider;
    }
    
    public void notifyMatchFinished(Long matchId) {
        restClient.post()
                .uri(finishedMatchesPath, matchId)
                .headers(headers -> headers.setBearerAuth(serviceTokenProvider.createToken()))
                .retrieve()
                .toBodilessEntity();
    }
}
