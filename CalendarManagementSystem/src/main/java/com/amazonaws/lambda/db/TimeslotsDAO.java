package com.amazonaws.lambda.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public boolean closeTSByDate(String date, String calendarName) throws Exception {
        PreparedStatement ps = connection.prepareStatement("update timeslots set isOpen = ? where date = ? and calendarName = ?");
        ps.setBoolean(1, false);
        ps.setString(2, date);
        ps.setString(3, calendarName);
        int numbAffected = ps.executeUpdate();
        ps.close();
        
        if (numbAffected == 0) {
            return false;
        }
        return true;
    }
    
    public boolean closeTSByTime(String startTime, String calendarName) throws Exception {
        PreparedStatement ps = connection.prepareStatement("update timeslots set isOpen = ? where startTime = ? and calendarName = ?");
        ps.setBoolean(1, false);
        ps.setString(2, startTime);
        ps.setString(3, calendarName);
        int numbAffected = ps.executeUpdate();
        ps.close();
        
        if (numbAffected == 0) {
            return false;
        }
        return true;
    }
    
    public boolean closeTSByDay(String date, String startTime, String calendarName) throws Exception {
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date sqlDate = dFormat.parse(date);
        Calendar sqlDay = Calendar.getInstance();
        sqlDay.setTime(sqlDate);
        int dayOfWeek = sqlDay.get(Calendar.DAY_OF_WEEK);
        
        PreparedStatement cs = connection.prepareStatement("update timeslots set isOpen = ? where dayofweek(date) = ? and startTime = ? and calendarName = ?;");
        cs.setBoolean(1, false);
        cs.setInt(2, dayOfWeek);
        cs.setString(3, startTime);
        cs.setString(4, calendarName);
        int numbAffected = cs.executeUpdate();
        cs.close();
        if (numbAffected == 0) {
            return false;
        }
        return true;
    }
    
    public boolean closeTSByID(String id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update timeslots set isOpen = ? where id = ?");
        ps.setBoolean(1, false);
        ps.setString(2, id);
        
        int numbAffected = ps.executeUpdate();
        ps.close();
        
        if (numbAffected == 0) {
            return false;
        }
        return true;
    }
}
