package com.amazonaws.lambda.model;

public class APIGatewayRequest {
    String httpMethod;    
    String body; //A JSON string of the request payload;
    
    PathParameters pathParameters;
    QueryStringParameters queryStringParameters;
    Headers headers;
    
    
    public QueryStringParameters getQueryStringParameters() {
        return queryStringParameters;
    }


    public class PathParameters{
        public String calendarName;
        public String timeslotID;
    }
    
    public class QueryStringParameters{
        
        public String date;
        public String month;
        public String startTime;
        
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
