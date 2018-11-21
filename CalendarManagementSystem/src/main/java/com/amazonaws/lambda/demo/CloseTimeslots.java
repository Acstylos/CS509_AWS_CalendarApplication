package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.model.APIGatewayRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CloseTimeslots implements RequestStreamHandler {

    CalendarsDAO cDao = new CalendarsDAO();
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        // TODO Auto-generated method stub
        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream: ");
        
     // annoyance to ensure integration with S3 can support CORS
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Methods", "GET,POST");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);
        
        //wait for request body input structure
        
        

    }

}
