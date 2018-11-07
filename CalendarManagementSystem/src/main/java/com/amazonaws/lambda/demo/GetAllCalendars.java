package com.amazonaws.lambda.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.GsonBuilder;

public class GetAllCalendars implements RequestStreamHandler {

    // connect to DB and load all existed calendars later
    
    
    private class CalendarList{
        ArrayList<String> calendars = new ArrayList<String>();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        // TODO Auto-generated method stub
        CalendarList calendarList = new CalendarList();
        calendarList.calendars.add("person");
        calendarList.calendars.add("business");
        
        // annoyance to ensure integration with S3 can support CORS
        HashMap<String,String> headers = new HashMap<>();
        headers.put("Content-Type",  "application/json");
        headers.put("Access-Control-Allow-Origin",  "*");

        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        String calendars = builder.create().toJson(calendarList);
        logger.log("Print all calendars: " + calendars);
        //calendarList.clear();

        APIGatewayResponse APIRespone = new APIGatewayResponse(200, headers, calendars);
        String response = builder.create().toJson(APIRespone);
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(response);
        writer.close();

    }

}
