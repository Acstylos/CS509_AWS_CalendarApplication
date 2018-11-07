package com.amazonaws.lambda.demo;

import com.amazonaws.services.s3.Headers;

public class APIGatewayRequest {
    String httpMethod;    
    String body; //A JSON string of the request payload;
    
    private PathParameters pathParameters;
    QueryStringParameters queryStringParameters;
    Headers headers;
    
    
    class PathParameters{
        String calendarName;
    }
    
    class QueryStringParameters{
        //Refactor the name later 
        String arg1;
    }
    
    class Headers{
        String headers;
    }
    
    public PathParameters getPathParameters() {
        return this.pathParameters;
    }
    

    
}
