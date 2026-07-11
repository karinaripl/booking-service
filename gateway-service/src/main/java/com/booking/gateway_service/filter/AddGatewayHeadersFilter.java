package com.booking.gateway_service.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AddGatewayHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    var principal = ctx.getAuthentication().getPrincipal();

                    if (principal instanceof Jwt jwt) {
                        String email = jwt.getSubject();
                        String role = jwt.getClaimAsString("role");

                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Email", email)
                                .header("X-User-Role", role != null ? role : "")
                                .header("X-Gateway-Signature", "trusted")
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }

                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}