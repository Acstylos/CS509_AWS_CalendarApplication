package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
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
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AddDayToCalendar implements RequestStreamHandler {

    CalendarsDAO cDao = new CalendarsDAO();

    private class Body {
        String date;
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        // TODO Auto-generated method stub
        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream: ");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Methods", "PUT");

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);

        String result = null;
        int statusCode = 0;
        try {
            if (request.getPathParameters() != null) {
                String calendarName = request.getPathParameters().calendarName;
                logger.log("Add a day into the calendar with a name " + calendarName);

                Body body = gson.fromJson(request.getGody(), Body.class);
                logger.log("Add a day into the calendar that the date is  " + body.date);

                // check the input of body.date is valid or not
                CalendarModel cModel = cDao.getCalendar(calendarName);
                CalendarModel tempC = new CalendarModel(calendarName);

                SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateToAdd = dFormat.parse(body.date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateToAdd);

                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                        || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    result = "The date is not valid, please choose another date.";
                    statusCode = 400;
                } else {

                    tempC.startDate = body.date;
                    tempC.startTime = cModel.startTime;
                    tempC.endTime = cModel.endTime;
                    tempC.duration = cModel.duration;

                    logger.log("new day information: " + tempC.startDate + " " + tempC.startTime + " " + tempC.endTime
                            + " " + tempC.duration);

                    // TODO: validate the input date as well as remove day from calendar
                    tempC.generateTimeslots();
                    if (cDao.addDaytoCalendar(tempC) != false) {
                        result = "Succeed in adding a new day into this calendar!";
                        statusCode = 200;
                    } else {
                        result = "This date is already existed in the calendar!";
                        statusCode = 204;
                    }

                }

            } else {

                result = "Bad Request!";
                statusCode = 400;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        APIGatewayResponse apiGatewayResponse = new APIGatewayResponse(statusCode, headers, result);
        String response = gson.toJson(apiGatewayResponse);
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        writer.write(response);
        writer.close();

    }

}
