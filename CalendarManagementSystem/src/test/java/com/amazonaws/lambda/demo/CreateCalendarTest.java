package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.lambda.model.APIGatewayRequest;
import com.amazonaws.lambda.model.APIGatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class CreateCalendarTest {

    private static InputStream input;

    @SuppressWarnings("unchecked")
	@BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
    	JSONObject Body = new JSONObject();
    	Body.put("calendarName", "personal");
    	Body.put("startTime", "09:00");
    	Body.put("endTime", "12:00");
    	Body.put("startDate", "2018-10-12");
    	Body.put("endDate", "2018-10-30");
    	Body.put("duration", 30);
    	JSONObject in = new JSONObject();
    	in.put("body", Body.toString());
        input = new ByteArrayInputStream(in.toString().getBytes(StandardCharsets.UTF_8));
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Test CreateCalendar");

        return ctx;
    }

	@Test
    public void testLambdaFunctionHandler() {
        CreateCalendar handler = new CreateCalendar();
        Context ctx = createContext();
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();

        OutputStream output = new ByteArrayOutputStream();
        try {
			handler.handleRequest(input,output, ctx);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        APIGatewayResponse response = gson.fromJson(output.toString(), APIGatewayResponse.class);
        
        //System.out.println(output.toString());

        // TODO: validate output here if needed.
        Assert.assertEquals(200, response.getStatusCode());
    }
}

