package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoadCalendarByName implements RequestStreamHandler {

    CalendarsDAO cDao = new CalendarsDAO();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream: ");

        // annoyance to ensure integration with S3 can support CORS
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Methods", "GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);

        String result = "";
        int statusCode = 200;

        if (request.getPathParameters() != null) {
            String calendarName = request.getPathParameters().calendarName;
            logger.log("Received calendar name is " + calendarName);
            CalendarModel c1 = null;

            try {
                c1 = cDao.loadCalendar(calendarName);
                if (c1.timeslots.isEmpty()) {
                    result = "This calendar is not existed, please try another one!";
                    statusCode = 404;

                } else {
                    result = gson.toJson(c1);
                }

            } catch (Exception e) {

                e.printStackTrace();
                result = "Something goes wrong here, please check it!";
                statusCode = 400;
            }

            APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(statusCode, headers, result);
            String response = gson.toJson(apiGatewayResponse);

            OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
            writer.write(response);
            writer.close();

        }

    }
}
