package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.spi.CalendarNameProvider;

import org.apache.http.client.protocol.ResponseProcessCookies;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class CreateCalendar implements RequestStreamHandler {
	
	
	
	
	
	
	@Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
	
		JSONParser parser = new JSONParser();
	    LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of RequestStreamHandler");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        JSONObject responseJson = new JSONObject();
        JSONObject headerJson = new JSONObject();
        JSONObject body = new JSONObject();
        JSONObject responseBody = new JSONObject();
        String id = "1";
        String name = "";
        String responseCode = "200";
        String message = "Success";
        try {
        	JSONObject event = (JSONObject)parser.parse(reader);
        	if (event.get("body")!=null) {
        		body = (JSONObject)parser.parse((String)event.get("body"));
        		// if the name exist, return 400
        		if (body.get("calendarName") != null) {
        			name = (String) body.get("calendarName");
        			if (name .equals("personal"))  {
        				responseCode = "400";
        				message = name + " is already exist.";
        			}
        		} else  {
        			responseCode = "400";
        			message = "parameter not valid";
        		}
        		// should save in database
            }
        	// should read from the database
        	id = "2";
        	if (responseCode.equals("200") ) {
        		JSONObject calendar = new JSONObject();
        		calendar.put("name", name);
        		calendar.put("id",id);
        		responseBody.put("calendar", calendar);
        	}
        	
        	headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?

            // annoyance to ensure integration with S3 can support CORS
            headerJson.put("Access-Control-Allow-Origin",  "*");
            headerJson.put("Access-Control-Allow-Methods", "GET,POST");
               
            responseJson.put("isBase64Encoded", false);
            responseJson.put("headers", headerJson);
                    
     		responseBody.put("message", message );
            responseJson.put("statusCode", responseCode);
            responseJson.put("body", responseBody.toString()); 
        } catch (ParseException pex) {
        	responseJson.put("statusCode", "400");
        	responseJson.put("exception", pex);
        }
        	
             
       
			
        logger.log("end result:" + responseJson.toJSONString());
        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toJSONString());  
        writer.close();
     }
}
	
