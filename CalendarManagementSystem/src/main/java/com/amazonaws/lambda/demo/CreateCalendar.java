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
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CreateCalendar implements RequestStreamHandler {

    CalendarsDAO cDao = new CalendarsDAO();

    private class Body {
        String calendarName;
        String startDate;
        String endDate;
        String startTime;
        String endTime;
        int duration;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream: ");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Methods", "POST");

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);

        Body body = gson.fromJson(request.getGody(), Body.class);

        CalendarModel cModel = new CalendarModel();
        cModel.name = body.calendarName;
        cModel.startDate = body.startDate;
        cModel.endDate = body.endDate;
        cModel.startTime = body.startTime;
        cModel.endTime = body.endTime;
        cModel.duration = body.duration;

        String result = "";
        int statusCode = 0;

        try {
            cModel.generateTimeslots();
            if (cDao.createCalendar(cModel)) {
                result = gson.toJson(cDao.loadCalendar(cModel.name));
                statusCode = 200;
            } else {
                result = "This calendar is not valid, please create another one";
                statusCode = 400;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = "Something goes wrong here, please check it!";
            statusCode = 400;

        }

        APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(statusCode, headers, result);
        String response = gson.toJson(apiGatewayResponse);

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(response);
        writer.close();

    }
}
