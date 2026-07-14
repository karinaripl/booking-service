package com.booking.gateway_auth_starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway.auth")
public class GatewayAuthProperties {
    private String secret;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
}