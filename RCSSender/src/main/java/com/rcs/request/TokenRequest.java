package com.rcs.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import ib.rcs.msg.RCSResponse;

public class TokenRequest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenRequest.class);
	
	private String sendUrl;
	private String id;
	private String password;
	
	private String response;

	private ObjectMapper mapper;
	
	public TokenRequest(String sendUrl, String id, String password) throws Exception {
		// TODO Auto-generated constructor stub
		this.sendUrl = sendUrl;
		this.id = id;
		this.password = password;
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public int requestToken() throws UnsupportedEncodingException, MalformedURLException{
		URL url = new URL(sendUrl+"/v1/token");
		LOGGER.info("Request Token {} {} {}",url, id, password);
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(3000)
				.setSocketTimeout(3000).build();
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
		
		CloseableHttpClient httpClient = httpClientBuilder.build();
		HttpPost httpPost = new HttpPost(url.toString());
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", id));
		params.add(new BasicNameValuePair("client_secret", password));
		params.add(new BasicNameValuePair("grant_type", "client_credentials"));
		params.add(new BasicNameValuePair("scope", "agency"));
		
		httpPost.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		}
		try {
			response = EntityUtils.toString(httpResponse.getEntity());
			httpClient.close();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error", e);
		}
		
		return httpResponse.getStatusLine().getStatusCode();
	}
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public static class TokenInfo{
		private String token;
		private long expired;
		private String tokenType;
		private long requestTime;
		
		public TokenInfo(){
			
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public long getExpired() {
			return expired;
		}
		public void setExpired(long expired) {
			this.expired = expired;
		}
		public long getRequestTime() {
			return requestTime;
		}
		public void setRequestTime(long requestTime) {
			this.requestTime = requestTime;
		}
		public String getTokenType() {
			return tokenType;
		}
		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}
		
	}
}
