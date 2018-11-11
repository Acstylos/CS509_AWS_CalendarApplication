package com.amazonaws.lambda.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.GsonBuilder;

public class GetAllCalendars implements RequestStreamHandler {

    // connect to DB and load all existed calendars later

    // used for JScript present
    private class CalendarList {
        List<String> calendars;
    }

    // connect with db here
    CalendarsDAO cDao = new CalendarsDAO();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        // TODO Auto-generated method stub
        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");

        CalendarList calendarList = new CalendarList();

        try {
            calendarList.calendars = cDao.getAllCalendars();
            logger.log("Loaded calednar name is " + calendarList.calendars.size());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // annoyance to ensure integration with S3 can support CORS
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();

        String calendars = builder.create().toJson(calendarList);
        logger.log("Print all calendars: " + calendars);

        APIGatewayResponse APIRespone = new APIGatewayResponse(200, headers, calendars);
        String response = builder.create().toJson(APIRespone);
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(response);
        writer.close();

    }

}
