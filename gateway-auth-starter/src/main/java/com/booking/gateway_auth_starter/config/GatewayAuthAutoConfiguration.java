package com.booking.gateway_auth_starter.config;

import com.booking.gateway_auth_starter.filter.GatewayAuthFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(GatewayAuthProperties.class)
public class GatewayAuthAutoConfiguration {

    @Bean
    public GatewayAuthFilter gatewayAuthFilter(GatewayAuthProperties properties) {
        return new GatewayAuthFilter(properties);
    }
}