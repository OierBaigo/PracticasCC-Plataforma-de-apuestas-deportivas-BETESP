package com.bets_service.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;

import com.bets_service.dto.OddResponse;
import com.bets_service.model.ResultType;
import com.bets_service.security.ServiceTokenProvider;

@Component
public class OddsServiceClient {

    private final RestClient restClient;
    private final ServiceTokenProvider tokenProvider;

    public OddsServiceClient(@Qualifier("oddsRestClient") RestClient restClient, ServiceTokenProvider tokenProvider) {
        this.restClient = restClient;
        this.tokenProvider = tokenProvider;
    }

    public Double getActiveOdd(Long matchId, ResultType resultType) {

        List<OddResponse> odds = restClient.get()
                .uri("/internal/odds/match/{matchId}", matchId)
                .headers(headers -> headers.setBearerAuth(tokenProvider.createToken()))
                .retrieve()
                .body(new ParameterizedTypeReference<List<OddResponse>>() {});
        
        if (odds == null) {
            throw new IllegalStateException("OddsService no devolvió cuotas");
        }
        
        Double v = odds.stream()
                .filter(odd -> Boolean.TRUE.equals(odd.active()))
                .filter(odd -> resultType.name().equals(odd.resultType()))
                .map(OddResponse::value)
                .filter(value -> value != null
                        && Double.isFinite(value)
                        && value > 1.0)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No hay una cuota activa para " + resultType));
        
        System.out.println("ODDS 3: Cuota seleccionada: " + v);
        
        return v;
    }
}