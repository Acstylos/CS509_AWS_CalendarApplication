package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.db.MeetingsDAO;
import com.amazonaws.lambda.db.TimeslotsDAO;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.lambda.model.Timeslots;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CancelMeetingTest {
	private static InputStream validInput;
	private static InputStream invalidInput;
    private static String testCalendarName = "department";
    private static String testValidMeetingId = "7739450c-54d3-4e78-bfe9-758ff33d1f9a";
    private static String testValidMeetingAttendee = "someone";
    private static String testValidMeetingLocation = "somewhere";
    private static String testInvalidMeetingId = "invalidMeetingId";
    
	@BeforeClass
    public static void setup() throws IOException {
        // Valid
		JSONObject validPathParameters = new JSONObject();
        validPathParameters.put("calendarName", testCalendarName);
		validPathParameters.put("timeslotID", testValidMeetingId);
		JSONObject validJsonInput = new JSONObject();
    	validJsonInput.put("pathParameters", validPathParameters);
    	validInput = new ByteArrayInputStream(validJsonInput.toString().getBytes(StandardCharsets.UTF_8));
		
    	// invalid
    	JSONObject invalidPathParameters = new JSONObject();
        invalidPathParameters.put("calendarName", testCalendarName);
		invalidPathParameters.put("timeslotID", "asjd;kjfa");
		JSONObject invalidJsonInput = new JSONObject();
		invalidJsonInput.put("pathParameters", invalidPathParameters);
    	invalidInput = new ByteArrayInputStream(invalidJsonInput.toString().getBytes(StandardCharsets.UTF_8));
	}
	
	@Test
    public void testCancelValidMeeting() {		
		Timeslots timeslot = new Timeslots(testValidMeetingId, testValidMeetingAttendee, testValidMeetingLocation);
		MeetingsDAO meetings = new MeetingsDAO();
		try {
			meetings.scheduleMeeting(timeslot);
		} catch (Exception e) {
			Assert.fail("Couldn't create initial meeting to cancel");
		}
		
		APIGatewayResponse response = handlerWrapper(validInput);
        
        // Validate
        Assert.assertEquals(200, response.getStatusCode());
        if(response.getStatusCode() != 200) {
            // Remove from live when we fail
            meetings.cancelMeeting(testValidMeetingId);	
        }
        
    }
	
	@Test
    public void testCancelInvalidMeeting() {
        APIGatewayResponse response = handlerWrapper(invalidInput);
        
        // Validate
        Assert.assertEquals(400, response.getStatusCode());
    }
	
	private APIGatewayResponse handlerWrapper(InputStream input) {
		// Minimal Setup
		CancelMeeting handler = new CancelMeeting();
        Context context = createContext();
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        // Run
        OutputStream output = new ByteArrayOutputStream();
        try {
			handler.handleRequest(input, output, context);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return gson.fromJson(output.toString(), APIGatewayResponse.class);
	}
	
	// Need Mock-Context for handlers
	private Context createContext() {
        TestContext ctx = new TestContext();
        ctx.setFunctionName("Test CreateCalendar");
        return ctx;
    }
}