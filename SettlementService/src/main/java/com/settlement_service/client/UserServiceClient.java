package com.settlement_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.settlement_service.dto.AmountRequest;
import com.settlement_service.security.ServiceTokenProvider;

@Component
public class UserServiceClient {
	private final RestClient userRestClient;
    private final String userPath;
    private final ServiceTokenProvider serviceTokenProvider;


    public UserServiceClient(@Qualifier("userRestClient") RestClient userRestClient, @Value("${user-service.deposit-path}") String userPath,
    		ServiceTokenProvider serviceTokenProvider) {
        this.userRestClient = userRestClient;
        this.userPath = userPath;
        this.serviceTokenProvider = serviceTokenProvider;
    }

    public AmountRequest depositWiningBet(Long userId, Double returnAmount) {
        return userRestClient.post()
                .uri(userPath, userId)
                .headers(headers -> headers.setBearerAuth(serviceTokenProvider.createToken()))
                .body(new AmountRequest(returnAmount))
                .retrieve()
                .body(AmountRequest.class);
    }
}
