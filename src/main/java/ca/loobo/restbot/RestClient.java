package ca.loobo.restbot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;


public class RestClient {
	private int timeout = 30 * 1000;	//30 seconds by default
	private String mHost = null;

	public RestClient() {
	}
	
	public RestClient(int timeout) {
		this.timeout = timeout;
	}
	
	public String getForString(String url) {
			return exchangeForString(url, null, HttpMethod.GET);
	}
	
	public String getForString(String templateUrl, Map<String, ?> vars)  {
	  return exchangeForString(templateUrl, null, HttpMethod.GET, vars);
	}
	
	private String exchangeForString(String url, String data, HttpMethod method) {
	  return exchangeForString(url, data, method, new HashMap<String, String>());
	}

	private String exchangeForString(String url, String data, HttpMethod method, Map<String, ?> vars)  {

			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.set("Connection",  "Close");
			if (mHost != null) {
				requestHeaders.set("Host", mHost);
			}

			requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
			HttpEntity<?> requestEntity = null;
			if (data == null) {
				requestEntity = new HttpEntity<Object>(requestHeaders);
			} else {
				requestHeaders.setContentType(MediaType.APPLICATION_JSON);
				requestEntity = new HttpEntity<String>(data, requestHeaders);
			}
	
			RestTemplate restTemplate = newRestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.exchange(
					url, method, requestEntity, String.class, vars);
			
			return responseEntity.getBody();

	}
	

	public RestClient setHost(String host) {
		mHost = host;
		return this;
	}
	
	private RestTemplate newRestTemplate () {
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestFactory rf = restTemplate.getRequestFactory();
        if (rf instanceof SimpleClientHttpRequestFactory) {
            ((SimpleClientHttpRequestFactory) rf).setConnectTimeout(timeout);
            ((SimpleClientHttpRequestFactory) rf).setReadTimeout(timeout);
        } else if (rf instanceof HttpComponentsClientHttpRequestFactory) {
            ((HttpComponentsClientHttpRequestFactory) rf).setReadTimeout(timeout);
            ((HttpComponentsClientHttpRequestFactory) rf).setConnectTimeout(timeout);
        }        
        
		//remove all default message converters, only use the one defined below for removing AcceptCharset header which is huge
		restTemplate.getMessageConverters().clear();
		StringHttpMessageConverter converter = new StringHttpMessageConverter();
		converter.setWriteAcceptCharset(false);
		restTemplate.getMessageConverters().add(converter);		
        
        return restTemplate;
	}
}
