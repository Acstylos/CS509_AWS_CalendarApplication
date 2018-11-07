package com.amazonaws.lambda.demo;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class DeleteCalendar implements RequestStreamHandler {
	JSONParser parser = new JSONParser();

	
	
	

	
	
	@Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));	 
        JSONObject responseJson = new JSONObject();
        JSONObject headerJson = new JSONObject();
        JSONObject body = new JSONObject();
        JSONObject responseBody = new JSONObject();
        
        String name = "";
        String message = "Successfully Deleted";
        String responseCode = "200";
        try {
        	JSONObject event = (JSONObject)parser.parse(reader);
        	if(event.get("pathParameters")!=null) {
        		JSONObject pps = (JSONObject)event.get("pathParameters");
        		if ( pps.get("calendarName") != null) {
        			name = (String)pps.get("calendarName");
        			
        		}
           	
        	}else {
        			responseCode = "400";
        			message = "parameter not valid";	
        	}
        	if(responseCode.equals("200")) {
        		//JSONObject delete = new JSONObject();
        		responseBody.put("result", message);
        	}
        	if(responseCode.equals("400")) {
        		//JSONObject delete = new JSONObject();
        		responseBody.put("result", "not exist");
        	}
        	
        	headerJson.put("Content-Type",  "application/json");  // not sure if needed anymore?

            // annoyance to ensure integration with S3 can support CORS
            headerJson.put("Access-Control-Allow-Origin",  "*");
            headerJson.put("Access-Control-Allow-Methods", "DELETE");
               
            responseJson.put("isBase64Encoded", false);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody.toString());         
     		
            responseJson.put("statusCode", responseCode);
            
        }catch (ParseException pex) {
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
