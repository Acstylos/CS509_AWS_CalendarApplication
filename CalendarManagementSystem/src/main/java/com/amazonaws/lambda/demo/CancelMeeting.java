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
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CancelMeeting implements RequestStreamHandler {

    MeetingsDAO mDao = new MeetingsDAO();

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");

        // annoyance to ensure integration with S3 can support CORS
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Methods", "POST");

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);

            String calendarName = request.getPathParameters().calendarName;
            String timeslotID = request.getPathParameters().timeslotID;
            logger.log("Received calendar name is " + calendarName);
            logger.log("Received timeslot id is " + timeslotID);

            String result = "";
            int statusCode = 0;

            if (!mDao.cancelMeeting(timeslotID)) {
                result = "This is no meeting during this period, try another one!";
                statusCode = 400;
            } else {
                result = "The meeting is canceled successfully!";
                statusCode = 200;
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
