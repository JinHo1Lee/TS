package com.rcs.request;

import java.net.URL;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityRequest{
	private static final Logger LOGGER = LoggerFactory.getLogger(CapabilityRequest.class);
	private String sendUrl;
	private String token;
	
	private String response;
	
	public CapabilityRequest(String sendUrl, String token) throws Exception {
		// TODO Auto-generated constructor stub
		this.sendUrl = sendUrl;
		this.token = token;
	}
	
	public int requestCapability(String contactNum) throws Exception{
		URL url = new URL(sendUrl+"/v1/capability?contactNum="+contactNum);
		LOGGER.info("Request RCSMessage {} {}",url, contactNum);
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(1000)
				.setSocketTimeout(1000).build();
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
		
		CloseableHttpClient httpClient = httpClientBuilder.build();
		HttpGet httpGet = new HttpGet(url.toString());
		
		httpGet.addHeader("Authorization", "Bearer " + token);
		
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		response = EntityUtils.toString(httpResponse.getEntity());
		System.out.println(response);
		httpClient.close();
		return httpResponse.getStatusLine().getStatusCode();
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	
}
