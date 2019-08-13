package com.nike.gcsc.gateway.config;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class HttpCilentConfig {

	    @Value("${http.maxTotal}")
	    private Integer maxTotal;

	    @Value("${http.defaultMaxPerRoute}")
	    private Integer defaultMaxPerRoute;

	    @Value("${http.connectTimeout}")
	    private Integer connectTimeout;

	    @Value("${http.connectionRequestTimeout}")
	    private Integer connectionRequestTimeout;

	    @Value("${http.socketTimeout}")
	    private Integer socketTimeout;

	    @Value("${http.staleConnectionCheckEnabled}")
	    private boolean staleConnectionCheckEnabled;
	    
	    @Bean(name = "httpClientConnectionManager")
	    public PoolingHttpClientConnectionManager getHttpClientConnectionManager(){
	        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
	        //Maximum number of connections
	        httpClientConnectionManager.setMaxTotal(maxTotal);
	        //concurrency
	        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
	        return httpClientConnectionManager;
	    }
	
	    @Bean(name = "httpClientBuilder")
	    public HttpClientBuilder getHttpClientBuilder(@Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager){
	        //The constructor in /HttpClientBuilder is protected, so instead of instantiating an HttpClientBuilder directly with new, you can use the static method create() provided by HttpClientBuilder to obtain the HttpClientBuilder object
	        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	        httpClientBuilder.setRetryHandler(retryHandler(5));
	        httpClientBuilder.setConnectionManager(httpClientConnectionManager);

	        return httpClientBuilder;
	    }
	    
	    @Bean
	    public CloseableHttpClient getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder){
	        return httpClientBuilder.build();
	    }
	    
	    @Bean(name = "builder")
	    public RequestConfig.Builder getBuilder(){
	        RequestConfig.Builder builder = RequestConfig.custom();
	        return builder.setConnectTimeout(connectTimeout)
	                .setConnectionRequestTimeout(connectionRequestTimeout)
	                .setSocketTimeout(socketTimeout)
	                .setStaleConnectionCheckEnabled(staleConnectionCheckEnabled);
	    }
	    
	    @Bean
	    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder){
	        return builder.build();
	    }

	    //Rejection policies
	    public  HttpRequestRetryHandler retryHandler(final int tryTimes){

	        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
	            @Override
	            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
	                // If you've tried again n times, give up
	                if (executionCount >= tryTimes) {
	                    return false;
	                }
	                // If the server loses the connection, try again
	                if (exception instanceof NoHttpResponseException) {
	                    return true;
	                }
	                // Do not retry the SSL handshake exception
	                if (exception instanceof SSLHandshakeException) {
	                    return false;
	                }
	                // timeout
	                if (exception instanceof InterruptedIOException) {
	                    return false;
	                }
	                // The target server is not reachable
	                if (exception instanceof UnknownHostException) {
	                    return true;
	                }
	                // Connection denied
	                if (exception instanceof ConnectTimeoutException) {
	                    return false;
	                }
	                // SSL handshake exception
	                if (exception instanceof SSLException) {
	                    return false;
	                }
	                HttpClientContext clientContext = HttpClientContext .adapt(context);
	                HttpRequest request = clientContext.getRequest();
	                // If the request is idempotent, try again
	                if (!(request instanceof HttpEntityEnclosingRequest)) {
	                    return true;
	                }
	                return false;
	            }

	        };
	        return httpRequestRetryHandler;
	    }
	    
}
