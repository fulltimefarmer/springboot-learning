package org.max.gateway.service;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class HttpService {

	private static String CONTENT_TYPE="Content-Type";

	private static String APPLICATION_JSON="application/json";

	@Autowired
	private CloseableHttpClient httpClient;

	@Autowired
	private RequestConfig requestConfig;

	public <T> T doGet(String url,Map<String,String> header,Map<String,String> body,Class<T> tClass) throws URISyntaxException, InstantiationException, IllegalAccessException {
		//Since the parameters of the GET request are assembled after the URL address, we need to build a URL with parameters
		URIBuilder uriBuilder = new URIBuilder(url);
		List <NameValuePair> nvps = new LinkedList<NameValuePair>();
		for(String key : body.keySet()) {
			BasicNameValuePair param = new BasicNameValuePair(key,body.get(key));
			nvps.add(param);
		}
		uriBuilder.setParameters(nvps);
		HttpGet httpGet = new HttpGet(uriBuilder.build());
		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		String result =null;
		try {
			response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 200){
				result = EntityUtils.toString(response.getEntity(),"UTF-8");
				return JSON.parseObject(result, tClass);
			}
		} catch (IOException e) {
			log.error("http get error : " + e);
		}finally {
			if(response!=null) {
				try {
					response.close();
				} catch (IOException e) {
					log.error("http response close error :"+e);
				}
			}
		}
		return tClass.newInstance();
	}

	public <T> T doPost(String url,Map<String,String> header,Map<String,String> body,Class<T> tClass) throws InstantiationException, IllegalAccessException, UnsupportedEncodingException{

		HttpPost httpPost = new HttpPost(url);
		for( String key : header.keySet()) {
			httpPost.addHeader(key, header.get(key));
		}
		if(APPLICATION_JSON.equals(header.get(CONTENT_TYPE))) {
			StringEntity entity = new StringEntity(JSON.toJSONString(body),"utf-8");
			httpPost.setEntity(entity);
		}else {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for( String key : body.keySet()) {
				params.add(new BasicNameValuePair(key, body.get(key)));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
		}

		CloseableHttpResponse response = null;
		String result =null;
		try {
			response = httpClient.execute(httpPost);
			if(response.getStatusLine().getStatusCode() == 200){
				result = EntityUtils.toString(response.getEntity(),"UTF-8");
				return JSON.parseObject(result, tClass);
			}
		} catch (IOException e) {
			log.error("http post error : " + e);
		}finally {
			if(response!=null) {
				try {
					response.close();
				} catch (IOException e) {
					log.error("http response close error :"+e);
				}
			}
		}
		return tClass.newInstance();
	}

}
