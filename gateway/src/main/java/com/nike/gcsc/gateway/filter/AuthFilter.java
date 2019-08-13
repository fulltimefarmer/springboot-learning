package com.nike.gcsc.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.nike.gcsc.authapi.response.AccessDto;
import com.nike.gcsc.common.GlobalResponse;
import com.nike.gcsc.gateway.constant.HttpUrlConstant;
import com.nike.gcsc.gateway.service.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "custom.filter.auth",name="enable",havingValue ="true")
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${custom.swagger.enable}")
    private Boolean swaggerConfig ;

    @Autowired
    DiscoveryClient discoveryClient;

	@Autowired
	private HttpService httpService;
	
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        String[] path = uri.getPath().split(HttpUrlConstant.SLASH);
        if(path.length < 4){
            exchange.getResponse().setStatusCode(HttpStatus.LENGTH_REQUIRED);
            return exchange.getResponse().setComplete();
        }
        String type = path[4];
        switch (type){
            case "public":
                return chain.filter(exchange);
            case "service":
                return chain.filter(exchange);
            case "api":
            	log.info(String.format("the request uri is : %s ", uri));
                HttpHeaders headers = exchange.getRequest().getHeaders();
                String clientId = headers.getFirst(HttpUrlConstant.HEADER_CLIENT_ID_KEY);
                String token = headers.getFirst(HttpUrlConstant.HEADER_AUTHORIZATION_KEY);
                //load token from cookie if can not find in header
                if(StringUtils.isBlank(token)){
                    MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
                    token = cookies.getFirst(HttpUrlConstant.HEADER_TOKEN_KEY).getValue();
                }
                if (StringUtils.isBlank(clientId) || StringUtils.isBlank(token)) {
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    return exchange.getResponse().setComplete();
                }
                Map<String, String> reqBean = Maps.newHashMapWithExpectedSize(4);
                reqBean.put(HttpUrlConstant.HEADER_CLIENT_ID_KEY, clientId);
                reqBean.put(HttpUrlConstant.HEADER_TOKEN_KEY, token);
                reqBean.put(HttpUrlConstant.HEADER_URI_KEY, uri.getPath());
                reqBean.put(HttpUrlConstant.HEADER_METHOD_ID_KEY, exchange.getRequest().getMethodValue());
                String checkTokenUrl = getUri();
                log.info(String.format("the checkAccess request url is : %s ", checkTokenUrl));
                Map<String, String> header = new HashMap<>();
                header.put(HttpUrlConstant.CONTENT_TYPE, HttpUrlConstant.APPLICATION_JSON);
                AccessDto accessDto = null;
                try {
                    GlobalResponse postResp = httpService.doPost(checkTokenUrl, header, reqBean, GlobalResponse.class);
                    Object data = postResp.getData();
                    if(null != data) {
                        accessDto = JSON.parseObject(data.toString(), AccessDto.class);
                    }
                } catch (InstantiationException | IllegalAccessException | UnsupportedEncodingException e) {
                    log.error("AuthFilter:check_access_by_token:" + e);
                }
                log.info(String.format("the checkAccess response dto is : %s ", accessDto));
                if(null != accessDto && accessDto.isAccess()) {
                    ServerHttpRequest oldExchange = exchange.getRequest().mutate().header(HttpUrlConstant.HEADER_USERNAME_KEY, accessDto.getUsername()).build();
                    ServerWebExchange newExchange = exchange.mutate().request(oldExchange).build();
                    return chain.filter(newExchange);
                }
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
//            case "special":
//                return chain.filter(exchange);
            case "api-docs" ://swagger request
            	if(swaggerConfig) {
            		return chain.filter(exchange);
            	}else {
            		 exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                     return exchange.getResponse().setComplete();
            	}
            	
            default:
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
        }
    }

    private String getUri(){
        StringBuilder sb = new StringBuilder();
        String ipPort = getRandomIpPort();
        if(StringUtils.isNotBlank(ipPort)){
            sb.append(HttpUrlConstant.HTTP_PROTOCOL)
                    .append(ipPort)
                    .append(HttpUrlConstant.AUTH_URL_PREFIX)
                    .append(HttpUrlConstant.AUTH_CHECK_ACCESS_BY_TOKEN);
        }
        return sb.toString();
    }

    private String getRandomIpPort(){
        StringBuilder sb = new StringBuilder();
        List<ServiceInstance> instances = discoveryClient.getInstances(HttpUrlConstant.AUTH_SERVICE_NAME);
        int size = instances.size();
        if(null != instances && size > 0){
            ServiceInstance instance = instances.get(Instant.now().getNano()%size);
            sb.append(instance.getHost()).append(HttpUrlConstant.COLON).append(instance.getPort());
        }
        return sb.toString();
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
