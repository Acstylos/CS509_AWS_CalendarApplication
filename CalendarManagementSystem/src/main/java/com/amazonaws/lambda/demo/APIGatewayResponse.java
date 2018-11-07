package com.amazonaws.lambda.demo;

import java.util.Map;

/**
 * Helpful tip from https://willhamill.com/2016/12/12/aws-api-gateway-lambda-proxy-request-and-response-objects
 * 
 * @param statusCode
 * @param headers
 * @param body
 */
public class APIGatewayResponse {

	private int statusCode;
	private Map<String, String> headers;
	private String body;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public APIGatewayResponse (int statusCode, Map<String, String> headers, String body) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.body = body;
	}
}
