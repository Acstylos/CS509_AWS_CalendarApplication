package com.amazonaws.lambda.model;

public class APIGatewayRequest {
    String httpMethod;    
    String body; //A JSON string of the request payload;
    
    PathParameters pathParameters;
    QueryStringParameters queryStringParameters;
    Headers headers;
    
    
    public class PathParameters{
        public String calendarName;
        public String timeslotID;
    }
    
    public class QueryStringParameters{
        //Refactor the name later 
        String arg1;
    }
    
    public class Headers{
        String headers;
    }
    
    public PathParameters getPathParameters() {
        return this.pathParameters;
    }
    
    public String getGody() {
        return this.body;
    }
    

    
}
