package com.amazonaws.lambda.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.model.APIGatewayRequest;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RemoveDayFromCalendar implements RequestStreamHandler {

    CalendarsDAO cDao = new CalendarsDAO();

    private class Body {
        String date;
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream: ");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Methods", "DELETE");

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayRequest request = gson.fromJson(new InputStreamReader(input), APIGatewayRequest.class);

        String result = null;
        int statusCode = 0;

        try {
            if (request.getPathParameters() != null) {
                String calendarName = request.getPathParameters().calendarName;
                logger.log("Remove a day into the calendar with a name " + calendarName);

                Body body = gson.fromJson(request.getGody(), Body.class);
                logger.log("Remove a day into the calendar that the date is  " + body.date);

                // check the input of body.date is valid or not
                SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateToRemove = dFormat.parse(body.date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateToRemove);

                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                        || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    result = "The date is not valid, please choose another date.";
                    statusCode = 400;
                } else {

                    if (cDao.removeDayFromCalendar(body.date, calendarName) == false) {
                        result = "The date is not existed in the calendar, please try another one!";
                        statusCode = 404;
                    } else {
                        result = "Succeed in removing a new day from this calendar!";
                        statusCode = 200;
                    }

                }

            } else {

                result = "Bad Request!";
                statusCode = 400;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(statusCode, headers, result);
        String response = gson.toJson(apiGatewayResponse);
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(response);
        writer.close();

    }

}
