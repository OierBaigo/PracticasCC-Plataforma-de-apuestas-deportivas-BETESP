package com.settlement_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean("betRestClient")
    public RestClient betRestClient(@Value("${bet-service.base-url}") String baseUrl) {
    	return RestClient.builder().baseUrl(baseUrl).build();
    }
    
    @Bean("userRestClient")
    public RestClient userRestClient(@Value("${user-service.base-url}") String baseUrl) {
    	return RestClient.builder().baseUrl(baseUrl).build();
    }
}
