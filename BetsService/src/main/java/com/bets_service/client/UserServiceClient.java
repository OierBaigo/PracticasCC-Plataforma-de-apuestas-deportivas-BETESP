package com.bets_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.bets_service.dto.BalanceResponse;
import com.bets_service.dto.WithdrawRequest;

@Component
public class UserServiceClient {

    private final RestClient restClient;
    private final String balancePath;
    private final String withdrawPath;

    public UserServiceClient( @Qualifier("userRestClient") RestClient restClient,
    		@Value("${user-service.balance-path}") String balancePath,
            @Value("${user-service.withdraw-path}") String withdrawPath) {

        this.restClient = restClient;
        this.balancePath = balancePath;
        this.withdrawPath = withdrawPath;
    }

    public Double getBalance() {
        BalanceResponse response = restClient.get()
                .uri(balancePath)
                .headers(headers -> headers.setBearerAuth(AuthorizedRequest.currentToken()))
                .retrieve()
                .body(BalanceResponse.class);

        if (balanceInvalid(response)) {
            throw new IllegalStateException("User Service devolvió un saldo inválido");
        }

        return response.balance();
    }

	private boolean balanceInvalid(BalanceResponse response) {
		return response == null || response.balance() == null || !Double.isFinite(response.balance());
	}

    public void withdrawBalance(Double amount) {

        WithdrawRequest request = new WithdrawRequest(amount);

        BalanceResponse response = restClient.post()
                .uri(withdrawPath)
                .headers(headers -> headers.setBearerAuth(AuthorizedRequest.currentToken()))
                .body(request)
                .retrieve()
                .body(BalanceResponse.class);

        if (response == null) {
            throw new IllegalStateException("User Service no confirmó la reserva");
        }
    }
}