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
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.lambda.model.Timeslots;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AddDayToCalendarTest {
	private static InputStream cleanInput;
	private static InputStream dirtyInput;
	private static InputStream existsInput;
    private static String testCalendarName = "department";
    private static String testDate = "2018-12-26";
    private static String testInvalidDate = "junkdateformat";
    private static String testDateExists = "2018-11-15";
    
	@BeforeClass
    public static void setup() throws IOException {
		JSONObject calendarName = new JSONObject();
        calendarName.put("calendarName", testCalendarName);
		
		JSONObject cleanBody = new JSONObject();
		cleanBody.put("date", "2018-12-26");
		JSONObject cleanJsonInput = new JSONObject();
    	cleanJsonInput.put("body", cleanBody.toString());
    	cleanJsonInput.put("pathParameters", calendarName);
    	cleanInput = new ByteArrayInputStream(cleanJsonInput.toString().getBytes(StandardCharsets.UTF_8));
		
		JSONObject dirtyBody = new JSONObject();
		dirtyBody.put("date", testInvalidDate);
		JSONObject dirtyJsonInput = new JSONObject();
		dirtyJsonInput.put("body", dirtyBody.toString());
		dirtyJsonInput.put("pathParameters", calendarName);
    	dirtyInput = new ByteArrayInputStream(dirtyJsonInput.toString().getBytes(StandardCharsets.UTF_8));

    	JSONObject existsBody = new JSONObject();
		existsBody.put("date", testDateExists);
		JSONObject existsJsonInput = new JSONObject();
		existsJsonInput.put("body", existsBody.toString());
		existsJsonInput.put("pathParameters", calendarName);
		existsInput = new ByteArrayInputStream(existsJsonInput.toString().getBytes(StandardCharsets.UTF_8));
	}
	
	@Test
    public void testAddDayCleanInput() {
		APIGatewayResponse response = handlerWrapper(cleanInput);
        
        // Validate
        Assert.assertEquals(200, response.getStatusCode());
        CalendarsDAO cDao = new CalendarsDAO();
        try {
			CalendarModel calendar = cDao.loadCalendar(testCalendarName);
			List<Timeslots> timeslotsInDate = new ArrayList<Timeslots>();
			for (Timeslots timeslot : calendar.timeslots){
				if(timeslot.date.equals(testDate)) {
					timeslotsInDate.add(timeslot);
				}
			}
			Assert.assertTrue(!timeslotsInDate.isEmpty());
		} catch (Exception e) {
			Assert.fail("Not created");
		}
        
        // Remove from live
        cDao.removeDayFromCalendar(testDate, testCalendarName);
    }
	
	@Test
    public void testAddDayDirtyInput() {
        APIGatewayResponse response = handlerWrapper(dirtyInput);
        
        // Validate
        Assert.assertEquals(400, response.getStatusCode());
        // Prove data doesn't exist in DB
        CalendarsDAO cDao = new CalendarsDAO();
        try {
        	CalendarModel calendar = cDao.loadCalendar(testCalendarName);
        	List<Timeslots> timeslotsInDate = new ArrayList<Timeslots>();
			for (Timeslots timeslot : calendar.timeslots){
				if(timeslot.date.equals(testInvalidDate)) {
					timeslotsInDate.add(timeslot);
				}
			}
			Assert.assertTrue(timeslotsInDate.isEmpty());
		} catch (Exception e) {
			Assert.fail("Not created");
		}
    }
	
	@Test
    public void testAddDayAlreadyExists() {
        APIGatewayResponse response = handlerWrapper(existsInput);
        
        // Validate
        Assert.assertEquals(204, response.getStatusCode());
        // Prove data already exists in DB
        CalendarsDAO cDao = new CalendarsDAO();
        try {
        	CalendarModel calendar = cDao.loadCalendar(testCalendarName);
			List<Timeslots> timeslotsInDate = new ArrayList<Timeslots>();
			for (Timeslots timeslot : calendar.timeslots){
				if(timeslot.date.equals(testDateExists)) {
					timeslotsInDate.add(timeslot);
				}
			}
			Assert.assertTrue(!timeslotsInDate.isEmpty());
		} catch (Exception e) {
			Assert.fail("Already Exists");
		}
    }
	
	private APIGatewayResponse handlerWrapper(InputStream input) {
		// Minimal Setup
		AddDayToCalendar handler = new AddDayToCalendar();
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