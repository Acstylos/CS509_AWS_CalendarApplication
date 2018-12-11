package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.model.APIGatewayRequest;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class DeleteCalendarTest {

    private static InputStream input;
    private static String testname = "personal";

    @SuppressWarnings("unchecked")
	@BeforeClass
    public static void createInput() throws IOException {
    	// Create the test calendar first
    	CalendarModel cModel = new CalendarModel();
    	cModel.name = testname;
        cModel.startDate = "2018-10-12";
        cModel.endDate = "2018-10-30";
        cModel.startTime = "09:00";
        cModel.endTime = "12:00";
        cModel.duration = 30;
        CalendarsDAO cDao = new CalendarsDAO();
        try {
			cModel.generateTimeslots();
			if (cDao.createCalendar(cModel)) {
				System.out.println("Created target calendar");
			} else {
				System.out.println("Target calendar already existed");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // TODO: set up your sample input object here.
        JSONObject cn = new JSONObject();
        cn.put("calendarName", testname);
    	JSONObject in = new JSONObject();
    	in.put("pathParameters", cn);
        input = new ByteArrayInputStream(in.toString().getBytes(StandardCharsets.UTF_8));
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Test DeleteCalendar");

        return ctx;
    }

	@Test
    public void testLambdaFunctionHandler() {
        CalendarsDAO cDao = new CalendarsDAO();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));  
        DeleteCalendar handler = new DeleteCalendar();
        Context ctx = createContext();
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        APIGatewayRequest request = gson.fromJson(reader, APIGatewayRequest.class);
        try {
			String calendarName = java.net.URLDecoder.decode(request.getPathParameters().calendarName, "UTF-8");
			System.out.println(calendarName);
			cDao.deleteCalendar(calendarName);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        OutputStream output = new ByteArrayOutputStream();
        try {
			handler.handleRequest(input,output, ctx);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        APIGatewayResponse response = gson.fromJson(output.toString(), APIGatewayResponse.class);

        // TODO: validate output here if needed.
        Assert.assertEquals(200, response.getStatusCode());

        try {
			@SuppressWarnings("unused")
			CalendarModel calendar = cDao.getCalendar(testname);
			Assert.fail("Not deleted");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
    }
}

