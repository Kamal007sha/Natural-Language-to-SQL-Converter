package com.nlsql.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {
    
    @Value("${nlsql.ai.openai.api-key}")
    private String apiKey;
    
    @Value("${nlsql.ai.openai.model}")
    private String model;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getModel() {
        return model;
    }
}