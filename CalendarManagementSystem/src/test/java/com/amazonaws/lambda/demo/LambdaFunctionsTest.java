package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.lambda.db.CalendarsDAO;
import com.amazonaws.lambda.db.MeetingsDAO;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionsTest {

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
    public void testCreateCalendars() throws Exception {
        File inputFile = new File("src/test/java/createCalendar.json");
        input = new FileInputStream(inputFile);

        CreateCalendar createCalendar = new CreateCalendar();
        OutputStream outputStream = new ByteArrayOutputStream();
        Context context = createContext("createCalendar");

        createCalendar.handleRequest(input, outputStream, context);

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayResponse response = gson.fromJson(outputStream.toString(), APIGatewayResponse.class);

        Assert.assertEquals(200, response.getStatusCode());

        CalendarsDAO cDao = new CalendarsDAO();
        cDao.deleteCalendar(testname);

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

    @Test
    public void testLoadCalendarByName() throws Exception {
        File inputFile = new File("src/test/java/LoadCalendarByName.json");

        input = new FileInputStream(inputFile);
        LoadCalendarByName name = new LoadCalendarByName();
        Context context = createContext("LoadCalendarByName");

        OutputStream output = new ByteArrayOutputStream();
        name.handleRequest(input, output, context);

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayResponse response = gson.fromJson(output.toString(), APIGatewayResponse.class);

        Assert.assertEquals(200, response.getStatusCode());

    }

    @Test
    public void testRemoveDayFromCalendar() throws Exception {
        File inputFile = new File("src/test/java/RemoveDayFromCalendar.json");

        input = new FileInputStream(inputFile);
        RemoveDayFromCalendar rdc = new RemoveDayFromCalendar();
        Context context = createContext("RemoveDayFromCalendar");

        OutputStream output = new ByteArrayOutputStream();
        rdc.handleRequest(input, output, context);

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayResponse response = gson.fromJson(output.toString(), APIGatewayResponse.class);

        Assert.assertEquals(400, response.getStatusCode());
    }

    @Test
    public void testScheduleMeeting() throws Exception {
        File inputFile = new File("src/test/java/ScheduleMeeting.json");

        input = new FileInputStream(inputFile);
        ScheduleMeeting SM = new ScheduleMeeting();
        Context context = createContext("ScheduleMeeting");

        OutputStream output = new ByteArrayOutputStream();
        SM.handleRequest(input, output, context);

        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        APIGatewayResponse response = gson.fromJson(output.toString(), APIGatewayResponse.class);

        Assert.assertEquals(200, response.getStatusCode());
        
        MeetingsDAO mDao = new MeetingsDAO();
        mDao.cancelMeeting("0d5b1ab7-86f2-4d0c-a506-ec87a9d0f731");//TimeslotID comes from DB and is stored in scheduleMeeting.json file

    }
}
