package com.rcs.request;

import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.rcs.request.TokenRequest.TokenInfo;

import ib.pdu.mfep.PDUGwMt;

public class MessageRequest{
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageRequest.class);
	private String sendUrl;
	private TokenInfo tokenInfo;
	private RestTemplate restTemplate;
	
	private String statusCode;
	private String response;
	
	public MessageRequest(RestTemplate restTemplate, String sendUrl, TokenInfo tokenInfo) throws Exception {
		// TODO Auto-generated constructor stub
		this.restTemplate = restTemplate;
		this.sendUrl = sendUrl;
		this.tokenInfo = tokenInfo;
	}
	
	public int sendMessage(String message, PDUGwMt pduReq) throws Exception{
		String url = sendUrl+"/v1/messages";
		
		LOGGER.info("(request_rcsmt) carrierKey:{}, rsMsgKey:{}, clientMsgKey:{}, rcsMessage:{}",
					pduReq.getCarrierKey(), pduReq.getRsMsgKey(), pduReq.getClientMsgKey(), message);
		
		URI uri = new URI(url);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+ tokenInfo.getToken());
		MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
		headers.setContentType(mediaType);
		HttpEntity<String> request = new HttpEntity<>(message, headers);
		try{
			ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
			response = result.getBody();
			statusCode = result.getStatusCode().toString();
		}catch (HttpStatusCodeException e){
			statusCode = e.getStatusCode().toString();
			response = e.getResponseBodyAsString();
		}
		
		LOGGER.info("(response_rcsmt) carrierKey:{}, rsMsgKey:{}, clientMsgKey:{}, statusCode:{}, data:{} ",
					pduReq.getCarrierKey(), pduReq.getRsMsgKey(), pduReq.getClientMsgKey(), statusCode, response);
		
		return Integer.parseInt(statusCode);
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	
}
