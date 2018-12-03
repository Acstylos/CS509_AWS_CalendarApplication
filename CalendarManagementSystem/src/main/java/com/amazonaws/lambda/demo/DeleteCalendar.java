package com.amazonaws.lambda.demo;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.model.APIGatewayRequest;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DeleteCalendar implements RequestStreamHandler {

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
        headers.put("Access-Control-Methods", "POST, DELETE");

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);

            if (request.getPathParameters() != null) {
                String calendarName = java.net.URLDecoder.decode(request.getPathParameters().calendarName, "UTF-8");
                logger.log("Received calendar name is " + calendarName);

                String result = "";
                int statusCode = 200;
                if (cDao.deleteCalendar(calendarName) != false) {
                    result = "Delete calendar is successful!";
                } else {
                    result = "The calendar is not exist! Try another one";
                    statusCode = 404;
                }

                APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(statusCode, headers, result);
                String response = gson.toJson(apiGatewayResponse);

                OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
                writer.write(response);
                writer.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
            APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(400, headers,
                    "Something goes wrong here, please check angin");
            String response = gson.toJson(apiGatewayResponse);

            OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
            writer.write(response);
            writer.close();
        }

    }
}
