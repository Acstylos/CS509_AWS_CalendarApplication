package com.amazonaws.lambda.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.amazonaws.lambda.model.CalendarModel;
import com.amazonaws.lambda.model.Timeslots;

//Access calendars data from DB
public class CalendarsDAO {

    java.sql.Connection connection;

    public CalendarsDAO() {
        try {
            connection = DatabaseUtil.connect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            connection = null;
        }
    }

    public CalendarModel loadCalendar(String calendarName) throws Exception {

        CalendarModel loadCalendar = new CalendarModel(calendarName);
        try {
            // table name is case sensitive
            PreparedStatement ps = connection
                    .prepareStatement("select * from timeslots where calendarName = ? order by date, startTime;");
            ps.setString(1, calendarName);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {

                loadCalendar.timeslots.add(setTimeslots(resultSet));
            }
            resultSet.close();
            ps.close();

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

        return loadCalendar;

    }

    public CalendarModel getCalendar(String calendarName) throws SQLException {

        // add variables into calendars table on 11.13 to-do-list
        CalendarModel c = new CalendarModel(calendarName);
        PreparedStatement ps = connection.prepareStatement("select * from calendars where name =?");
        ps.setString(1, calendarName);
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            c.startTime = resultSet.getString("startTime");
            c.endTime = resultSet.getString("endTime");
            c.duration = resultSet.getInt("duration");
        }

        return c;
    }

    // Add duplicate calendar check mechanism later and fill up ps1 to complete the
    // calendars table;
    public boolean createCalendar(CalendarModel c) throws Exception {

        try {
            PreparedStatement ps1 = connection
                    .prepareStatement("insert into calendars (name, startTime, endTime, duration) value (?, ?, ?, ?);");
            ps1.setString(1, c.name);
            ps1.setString(2, c.startTime);
            ps1.setString(3, c.endTime);
            ps1.setInt(4, c.duration);
            ps1.executeUpdate();

            ps1.close();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }

        try {
            PreparedStatement ps2 = connection.prepareStatement(
                    "insert into timeslots (date, startTime, endTime, calendarName, id) value (?, ?, ?, ?, ?);");

            for (int i = 0; i < c.timeslots.size(); i++) {
                ps2.setString(1, c.timeslots.get(i).date);
                ps2.setString(2, c.timeslots.get(i).startTime);
                ps2.setString(3, c.timeslots.get(i).endTime);
                ps2.setString(4, c.name);
                ps2.setString(5, UUID.randomUUID().toString());

                ps2.executeUpdate();
            }

            ps2.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public boolean deleteCalendar(String calendarName) {
        try {
            PreparedStatement ps = connection.prepareStatement("delete from timeslots where calendarName =?;");
            ps.setString(1, calendarName);
            ps.executeUpdate();
            ps.close();

            PreparedStatement ps1 = connection.prepareStatement("delete from calendars where name =?;");
            ps1.setString(1, calendarName);
            int numAffected = ps1.executeUpdate();
            ps1.close();

            return (numAffected == 1);
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }

    }

    public List<String> getAllCalendars() throws Exception {
        List<String> calendars = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from calendars order by name;");

        if (resultSet != null) {
            while (resultSet.next()) {
                String calendarName = resultSet.getString("name");
                calendars.add(calendarName);
            }

            resultSet.close();
            statement.close();
        }

        return calendars;

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

    public boolean addDaytoCalendar(CalendarModel c) {

        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(
                    "insert into timeslots (date, startTime, endTime, calendarName, id) value (?, ?, ?, ?, ?);");
            for (int i = 0; i < c.timeslots.size(); i++) {
                ps.setString(1, c.timeslots.get(i).date);
                ps.setString(2, c.timeslots.get(i).startTime);
                ps.setString(3, c.timeslots.get(i).endTime);
                ps.setString(4, c.name);
                ps.setString(5, UUID.randomUUID().toString());

                ps.executeUpdate();

            }

            ps.close();
            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }

    }

    public boolean removeDayFromCalendar(String dateToRemove, String calendarName) {
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement("delete from timeslots where date = ? and calendarName = ?;");
            ps.setString(1, dateToRemove);
            ps.setString(2, calendarName);
            int numAffected = ps.executeUpdate();
            ps.close();
            if (numAffected == 0)
                return false;
            else
                return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }

}
