package com.amazonaws.lambda.db;

public class TimeslotsDAO {

    java.sql.Connection connection;

    public TimeslotsDAO() {
        // TODO Auto-generated constructor stub
        try {
            connection = DatabaseUtil.connect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            connection = null;
        }

    }

    public void closeTSByDate(String date) {
        
    }
    
    public void closeTSByTime(String startTime) {
        
    }
    
    public void closeTSByDay(String day) {
        
    }
}
