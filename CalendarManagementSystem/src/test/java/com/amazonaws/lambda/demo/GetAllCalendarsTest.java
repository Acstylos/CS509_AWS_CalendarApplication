package com.amazonaws.lambda.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.json.simple.JSONObject;
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
public class GetAllCalendarsTest {

    private static InputStream input;

    @SuppressWarnings("unchecked")
	@BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Test GetAllCalendars");

        return ctx;
    }

	@Test
    public void testLambdaFunctionHandler() {
        GetAllCalendars handler = new GetAllCalendars();
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
        // connect with db here and validate if the new one has created
    }
}

