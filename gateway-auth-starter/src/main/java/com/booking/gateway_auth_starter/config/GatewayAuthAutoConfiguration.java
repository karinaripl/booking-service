package com.booking.gateway_auth_starter.config;

import com.booking.gateway_auth_starter.filter.GatewayAuthFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class GatewayAuthAutoConfiguration {

    @Bean
    public FilterRegistrationBean<GatewayAuthFilter> gatewayAuthFilter() {
        FilterRegistrationBean<GatewayAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new GatewayAuthFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }
}