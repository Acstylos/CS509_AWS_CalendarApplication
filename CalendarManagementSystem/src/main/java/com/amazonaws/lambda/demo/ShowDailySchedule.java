package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import com.amazonaws.lambda.db.MeetingsDAO;
import com.amazonaws.lambda.model.APIGatewayRequest;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ShowDailySchedule implements RequestStreamHandler {

    MeetingsDAO mDao = new MeetingsDAO();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");

        // annoyance to ensure integration with S3 can support CORS
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Methods", "GET");

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);
        String result = "";
        int statusCode = 0;

        try {
            if (request.getPathParameters() != null) {
                String calendarName = request.getPathParameters().calendarName;
                logger.log("Received calendar name is " + calendarName);

                CalendarModel c1 = null;
                if (request.getQueryStringParameters() != null) {
                    String date = request.getQueryStringParameters().date;

                    c1 = mDao.showDailySchedule(calendarName, date);
                    result = gson.toJson(c1);
                    statusCode = 200;

                    APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(statusCode, headers, result);
                    String response = gson.toJson(apiGatewayResponse);

                    OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
                    writer.write(response);
                    writer.close();
                }

            }
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
