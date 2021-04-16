package com.nike.gcsc.auth.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSON;
import com.nike.gcsc.auth.exception.HttpBadRequestException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtils {

	/**
	 * http post common method
	 * @param <T>
	 * @param url
	 * @param header
	 * @param body
	 * @param tClass
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T httpPost(String url,Map<String,String> header,Map<String,String> body,Class<T> tClass) throws InstantiationException, IllegalAccessException {
		HttpPost post = new HttpPost(url);
		for( String key : header.keySet()) {
			post.addHeader(key, header.get(key));
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for( String key : body.keySet()) {
			params.add(new BasicNameValuePair(key, body.get(key)));
		}
		try(CloseableHttpClient psotCloseableHttpClient = HttpClientBuilder.create().build()){
			post.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
			try(CloseableHttpResponse psotResponse = psotCloseableHttpClient.execute(post)){
				log.info(String.format("the httpPost status code is ： %s", psotResponse.getStatusLine().getStatusCode()));
				if(psotResponse.getStatusLine().getStatusCode()==HttpStatus.OK.value()) {
					
					HttpEntity postEntity = psotResponse.getEntity();
					String postResp = EntityUtils.toString(postEntity, "utf-8");
					log.info("response entity : "+postResp);
					T responseBean = JSON.parseObject(postResp, tClass);
					return responseBean;
				} else if(psotResponse.getStatusLine().getStatusCode()==HttpStatus.BAD_REQUEST.value()) {
				    throw new HttpBadRequestException(EntityUtils.toString(psotResponse.getEntity()));
				} else {
					log.info("response entity : "+EntityUtils.toString(psotResponse.getEntity(), "utf-8"));
				}
			}
		}catch(IOException e ) {
			log.error("the post http error ：" + e);
		}
		return  tClass.newInstance();
	}
	
	/**
	 * http get common method
	 * @param <T>
	 * @param url
	 * @param header
	 * @param body
	 * @param tClass
	 * @return
	 * @throws URISyntaxException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> T httpGet(String url,Map<String,String> header,Map<String,String> body,Class<T> tClass) throws URISyntaxException, InstantiationException, IllegalAccessException {
		//Since the parameters of the GET request are assembled after the URL address, we need to build a URL with parameters
		URIBuilder uriBuilder = new URIBuilder(url);
		List <NameValuePair> nvps = new LinkedList<NameValuePair>();
		for(String key : body.keySet()) {
			BasicNameValuePair param = new BasicNameValuePair(key,body.get(key));
			nvps.add(param);
		}
		uriBuilder.setParameters(nvps);
		HttpGet get = new HttpGet(uriBuilder.build());
		for( String key : header.keySet()) {
			get.addHeader(key, header.get(key));
		}
		try(CloseableHttpClient getCloseableHttpClient = HttpClientBuilder.create().build()){
			try(CloseableHttpResponse getResponse = getCloseableHttpClient.execute(get)){
				if(getResponse.getStatusLine().getStatusCode()==HttpStatus.OK.value()) {
					HttpEntity postEntity = getResponse.getEntity();
					String postResp = EntityUtils.toString(postEntity, "utf-8");
					T responseBean = JSON.parseObject(postResp, tClass);
					return responseBean;
				}
			}
		}catch(IOException e ) {
			log.error("the get http error ：" + e);
		}
		return tClass.newInstance();
	}
	
	
}
