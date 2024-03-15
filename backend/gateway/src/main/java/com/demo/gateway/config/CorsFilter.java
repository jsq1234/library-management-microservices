package com.demo.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CorsFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
        response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "*");
        return chain.filter(exchange);
    }

}
