package org.max.learning.common.config;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;

 
@Configuration
public class BeanConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(createIgnoreVerifySSL())).build();
        HttpClientBuilder builder =  HttpClients.custom();
        builder.setRetryHandler(new DefaultHttpRequestRetryHandler(3,false));
        PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        clientConnectionManager.setMaxTotal(100);
        clientConnectionManager.setDefaultMaxPerRoute(100);
        builder.setConnectionManager(clientConnectionManager);
        HttpClient httpClient = builder.build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(9000);
        requestFactory.setReadTimeout(9000);
        requestFactory.setConnectionRequestTimeout(9000);
        requestFactory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    private SSLContext createIgnoreVerifySSL() {
        try{
            SSLContext sc = SSLContext.getInstance("TLS");
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }
                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                        String paramString) throws CertificateException {
                }
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sc.init(null, new TrustManager[] { trustManager }, null);
            return sc;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
