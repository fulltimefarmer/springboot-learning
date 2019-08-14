package org.max.gateway.filter;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    DiscoveryClient discoveryClient;
	
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        String[] path = uri.getPath().split("/");
        if(path.length < 4){
            exchange.getResponse().setStatusCode(HttpStatus.LENGTH_REQUIRED);
            return exchange.getResponse().setComplete();
        }
        String type = path[4];
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
