package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.joda.time.TimeOfDay;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.db.TimeslotsDAO;
import com.amazonaws.lambda.model.APIGatewayRequest;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.services.dynamodbv2.xspec.NULL;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CloseTimeslots implements RequestStreamHandler {

    TimeslotsDAO tDao = new TimeslotsDAO();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        // TODO Auto-generated method stub
        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream: ");

        // annoyance to ensure integration with S3 can support CORS
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Methods", "PUT");

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);

            String calendarName = request.getPathParameters().calendarName;
            logger.log("Received calendar name is " + calendarName);

            String result = "";
            int statusCode = 0;
            boolean sqlResult = true;

            if (request.getQueryStringParameters() != null) {
                if (request.getQueryStringParameters().startTime == null) {

                    sqlResult = tDao.closeTSByDate(request.getQueryStringParameters().date, calendarName);

                    if (sqlResult) {
                        result = "Timeslots are closed successfully!";
                        statusCode = 200;
                    } else {
                        result = "Invalid timeslots, please choose another one!";
                        statusCode = 400;
                    }
                } else if (request.getQueryStringParameters().date == null) {

                    sqlResult = tDao.closeTSByTime(request.getQueryStringParameters().startTime, calendarName);
                    if (sqlResult) {
                        result = "Timeslots are closed successfully!";
                        statusCode = 200;
                    } else {
                        result = "Invalid timeslots, please choose another one!";
                        statusCode = 400;
                    }
                } else {
                    sqlResult = tDao.closeTSByDay(request.getQueryStringParameters().date,
                            request.getQueryStringParameters().startTime, calendarName);
                    if (sqlResult) {
                        result = "Timeslots are closed successfully!";
                        statusCode = 200;
                    } else {
                        result = "Invalid timeslots, please choose another one!";
                        statusCode = 400;
                    }
                }

            } else {
                String timeslotID = request.getPathParameters().timeslotID;
                logger.log("Received timeslot id is " + timeslotID);

                if (tDao.closeTSByID(timeslotID) != false) {
                    result = "Timeslot is closed successfully!";
                    statusCode = 200;
                } else {
                    result = "This timeslot has already been closed, please choose another one!";
                    statusCode = 400;
                }
            }

            APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(statusCode, headers, result);
            String response = gson.toJson(apiGatewayResponse);

            OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
            writer.write(response);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
            APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(400, headers,
                    "Something goes wrong here, please check angin");
            String response = gson.toJson(apiGatewayResponse);

            OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
            writer.write(response);
            writer.close();
        }

    }

}
