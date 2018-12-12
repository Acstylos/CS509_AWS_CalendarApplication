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
import org.junit.AfterClass;
import org.junit.Test;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.lambda.model.Timeslots;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CloseTimeslotsTest {
	private static InputStream validInput;
	private static InputStream invalidInput;
	private static InputStream dateInput;
	private static InputStream timeInput;
	private static InputStream dayAndTimeInput;
	private static String testSpecificTimeslot;
    private static String testCalendarName = "testCT";
    private static String testDate = "2018-12-19";
    private static String testTime = "10:00";
    private static String testDayAndTimeDate = "2018-12-20";
    private static String testDayAndTimeTime = "11:00";
    
	@BeforeClass
    public static void setup() throws Exception {
		// setup the calendar we're going to be testing on
		CalendarModel testCalendar = new CalendarModel();
		testCalendar.duration = 30;
		testCalendar.endDate = "2018-12-30";
		testCalendar.startDate = "2018-12-10";
		testCalendar.endTime = "17:00";
		testCalendar.startTime = "8:00";
		testCalendar.name = testCalendarName;
		testCalendar.generateTimeslots();
		CalendarsDAO calendarDao = new CalendarsDAO();
		calendarDao.createCalendar(testCalendar);
		
		CalendarModel testCalendarModel = calendarDao.loadCalendar(testCalendarName);
		Timeslots specificTimeslot;//, dateTimeslot, timeTimeslot, dayAndTimeTimeslot;
		if(testCalendarModel.timeslots.size() > 0) {
			specificTimeslot = testCalendarModel.timeslots.get(0);
			testSpecificTimeslot = specificTimeslot.id;
		} else {
			throw new Exception("Didn't generate any timeslots to close.");
		}
		
		// Valid
		JSONObject validPathParameters = new JSONObject();
        validPathParameters.put("calendarName", testCalendarName);
		validPathParameters.put("timeslotID", specificTimeslot.id.toString());
		JSONObject validJsonInput = new JSONObject();
    	validJsonInput.put("pathParameters", validPathParameters);
    	validInput = new ByteArrayInputStream(validJsonInput.toString().getBytes(StandardCharsets.UTF_8));
    	
    	// Invalid
		JSONObject invalidPathParameters = new JSONObject();
        invalidPathParameters.put("calendarName", testCalendarName);
		invalidPathParameters.put("timeslotID", "garbageIdToCloseHere");
		JSONObject invalidJsonInput = new JSONObject();
    	invalidJsonInput.put("pathParameters", invalidPathParameters);
    	invalidInput = new ByteArrayInputStream(invalidJsonInput.toString().getBytes(StandardCharsets.UTF_8));
    	
    	// By Date
		JSONObject datePathParameters = new JSONObject();
        datePathParameters.put("calendarName", testCalendarName);
        JSONObject dateQueryParameters = new JSONObject();
        dateQueryParameters.put("date", testDate);
		JSONObject dateJsonInput = new JSONObject();
    	dateJsonInput.put("pathParameters", datePathParameters);
    	dateJsonInput.put("queryStringParameters", dateQueryParameters);
    	dateInput = new ByteArrayInputStream(dateJsonInput.toString().getBytes(StandardCharsets.UTF_8));
    	
    	// By Time
		JSONObject timePathParameters = new JSONObject();
        timePathParameters.put("calendarName", testCalendarName);
        JSONObject timeQueryParameters = new JSONObject();
        timeQueryParameters.put("startTime", testTime);
		JSONObject timeJsonInput = new JSONObject();
    	timeJsonInput.put("pathParameters", timePathParameters);
    	timeJsonInput.put("queryStringParameters", timeQueryParameters);
    	timeInput = new ByteArrayInputStream(timeJsonInput.toString().getBytes(StandardCharsets.UTF_8));
    	
    	// By Day and Time
		JSONObject dayAndTimePathParameters = new JSONObject();
        dayAndTimePathParameters.put("calendarName", testCalendarName);
        JSONObject dayAndTimeQueryParameters = new JSONObject();
        dayAndTimeQueryParameters.put("date", testDayAndTimeDate);
        dayAndTimeQueryParameters.put("startTime", testDayAndTimeTime);
		JSONObject dayAndTimeJsonInput = new JSONObject();
    	dayAndTimeJsonInput.put("pathParameters", dayAndTimePathParameters);
    	dayAndTimeJsonInput.put("queryStringParameters", dayAndTimeQueryParameters);
    	dayAndTimeInput = new ByteArrayInputStream(dayAndTimeJsonInput.toString().getBytes(StandardCharsets.UTF_8));
	}
	
	@AfterClass
	public static void cleanUp() {
		CalendarsDAO cDao = new CalendarsDAO();
		cDao.deleteCalendar(testCalendarName);
	}
	
	@Test
    public void testCloseValidTimeslot() {
		APIGatewayResponse response = handlerWrapper(validInput);
        
        // Validate
        Assert.assertEquals(200, response.getStatusCode());
        
        CalendarsDAO cDao = new CalendarsDAO();
        CalendarModel calendar;
		try {
			calendar = cDao.loadCalendar(testCalendarName);
			List<Timeslots> closedTimeslots = new ArrayList<Timeslots>();
			for (Timeslots timeslot : calendar.timeslots) {
	        	if(timeslot.id.equals(testSpecificTimeslot)) {
	        		closedTimeslots.add(timeslot);
	        	}
	        }
			Assert.assertTrue(closedTimeslots.size() == 1);
		} catch (Exception e) {
			Assert.fail("failed to read timeslot information about the calendar");
		}
    }
	
	@Test
    public void testCloseInvalidTimeslot() {
        APIGatewayResponse response = handlerWrapper(invalidInput);
        
        // Validate
        Assert.assertEquals(400, response.getStatusCode());
    }
	
	@Test
    public void testCloseTimeslotByTime() {
        APIGatewayResponse response = handlerWrapper(timeInput);
        
        // Validate
        Assert.assertEquals(200, response.getStatusCode());
    }
	
	@Test
    public void testCloseTimeslotByDay() {
        APIGatewayResponse response = handlerWrapper(dateInput);
        
        // Validate
        Assert.assertEquals(200, response.getStatusCode());
    }
	
	@Test
    public void testCloseTimeslotByDayAndTime() {
        APIGatewayResponse response = handlerWrapper(dayAndTimeInput);
        
        // Validate
        Assert.assertEquals(200, response.getStatusCode());
    }
	
	private APIGatewayResponse handlerWrapper(InputStream input) {
		// Minimal Setup
		CloseTimeslots handler = new CloseTimeslots();
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