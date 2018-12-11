package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class MeetingScheduleTest {

    private static InputStream input;
    private static String testname = "personal";

    @BeforeClass
    public static void createDBConnect() throws IOException {

        CalendarsDAO cDao = new CalendarsDAO();
        try {
            CalendarModel calendar = cDao.getCalendar(testname);
            Assert.assertEquals(calendar.name, testname);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            Assert.fail("Not created");
        }
        
        

    }

    private Context createContext(String lambdaFunction) {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Current test lambdaFunction" + lambdaFunction);

        return ctx;
    }
    
    @Test
    public void testGetAllCalendars() throws Exception {
        input = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        
        GetAllCalendars getAllCalendars = new GetAllCalendars();
        OutputStream output = new ByteArrayOutputStream();
        
        Context context = createContext("GetAllCalendars");
        getAllCalendars.handleRequest(input, output, context);
        
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayResponse response = gson.fromJson(output.toString(), APIGatewayResponse.class);

        Assert.assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testShowDailySchedule() throws IOException {

        File inputFile = new File("src/test/java/showDailySchedule.json");

        input = new FileInputStream(inputFile);
        
        ShowDailySchedule dailySchedule = new ShowDailySchedule();
        Context context = createContext("showDailySchedule");

        OutputStream output = new ByteArrayOutputStream();
        dailySchedule.handleRequest(input, output, context);

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayResponse response = gson.fromJson(output.toString(), APIGatewayResponse.class);

        Assert.assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testShowMonthlySchedule() throws Exception {
        File inputFile = new File("src/test/java/showMonthlySchedule.json");

        input = new FileInputStream(inputFile);
        ShowMothlySchedule monthlySchedule = new ShowMothlySchedule();
        Context context = createContext("showMonthlySchedule");

        OutputStream output = new ByteArrayOutputStream();
        monthlySchedule.handleRequest(input, output, context);

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayResponse response = gson.fromJson(output.toString(), APIGatewayResponse.class);

        Assert.assertEquals(200, response.getStatusCode());
    }
}
