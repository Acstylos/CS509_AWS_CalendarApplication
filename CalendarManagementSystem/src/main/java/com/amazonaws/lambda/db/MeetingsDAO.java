package com.amazonaws.lambda.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.lambda.model.Timeslots;

public class MeetingsDAO {
    java.sql.Connection connection;

    public MeetingsDAO() {
        try {
            connection = DatabaseUtil.connect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            connection = null;
        }
    }

    public boolean scheduleMeeting(Timeslots timeslots) throws Exception {

        PreparedStatement ps = connection.prepareStatement("select * from timeslots where id = ?;");
        ps.setString(1, timeslots.id);
        ResultSet resultSet = ps.executeQuery();

        boolean isOpen = true;

        if (resultSet.next()) {
            isOpen = resultSet.getBoolean("isOpen");

        }
        ps.close();

        if (isOpen) {
            PreparedStatement ps1 = connection
                    .prepareStatement("update timeslots set attendee = ?, location = ? where id = ?;");
           
            ps1.setString(1, timeslots.attendee);
            ps1.setString(2, timeslots.location);
            ps1.setString(3, timeslots.id);

            ps1.executeUpdate();
            ps1.close();

            return true;
        } else {

            return false;
        }
    }

    public boolean cancelMeeting(String timeslotID) {

        try {
            PreparedStatement ps = connection
                    .prepareStatement("update timeslots set attendee = ?, location = ? where id = ?;");
            
            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, timeslotID);
            int numbAffected = ps.executeUpdate();
            ps.close();

            if (numbAffected == 0) {
                return false;
            } else {
                return true;

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

    public CalendarModel showDailySchedule(String calendarName, String date) {
        CalendarModel tempC = new CalendarModel(calendarName);
        
        try {
            PreparedStatement ps = connection.prepareStatement("select * from timeslots where date = ? and calendarName = ? and attendee is not null order by date, startTime");
            ps.setString(1, date);
            ps.setString(2, calendarName);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                tempC.timeslots.add(setTimeslots(resultSet));
            }
            
            resultSet.close();
            ps.close();
            
            return tempC;
        } catch (Exception e) {
            return null;
        }
       
        
    }
    
    private Timeslots setTimeslots(ResultSet resultSet) throws Exception {
        // modify later when decide how a calendar will be present in the browser
        String id = resultSet.getString("id");
        String date = resultSet.getString("date");
        String startTime = resultSet.getString("startTime");
        String endTime = resultSet.getString("endTime");
        Boolean isOpen = resultSet.getBoolean("isOpen");
        String attendee = resultSet.getString("attendee");
        String location = resultSet.getString("location");

        return new Timeslots(id, date, startTime, endTime, isOpen, attendee, location);

    }

    public CalendarModel showMonthlySchedule(String calendarName, String month) {
        CalendarModel tempC = new CalendarModel(calendarName);
        
        try {
            PreparedStatement ps = connection.prepareStatement("select * from timeslots where calendarName = ? and attendee is not null and date like ? order by date, startTime;");
            ps.setString(1, calendarName);
            ps.setString(2, "%_" + month + "_%");
            
            ResultSet resultSet = ps.executeQuery();
            
            while(resultSet.next()) {
                tempC.timeslots.add(setTimeslots(resultSet));
            }
            
            resultSet.close();
            ps.close();
            
            return tempC;
        } catch (Exception e) {
            return null;
        }
        
    }
}
