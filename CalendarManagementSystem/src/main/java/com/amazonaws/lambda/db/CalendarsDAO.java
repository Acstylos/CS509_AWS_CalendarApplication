package com.amazonaws.lambda.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.Media;

import com.amazonaws.lambda.model.Calendar;
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

    public Calendar getCalendar(String calendarName) throws Exception {

        Calendar loadCalendar = new Calendar(calendarName);
        try {
            // table name is case sensitive
            PreparedStatement ps = connection
                    .prepareStatement("select * from timeslots" + "where calendarName = ?" + "and isOpen =? ;");
            ps.setString(1, calendarName);
            ps.setBoolean(2, true);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {

                loadCalendar.timeslots.add(setTimeslots(resultSet));
            }

            resultSet.close();
            ps.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return loadCalendar;

    }

    // Add duplicate calendar check mechanism later
    public boolean createCalendar(Calendar c) throws Exception {

        PreparedStatement ps1 = connection.prepareStatement("insert into calendars (name) value (?);");
        ps1.setString(1, c.name);
        ps1.executeUpdate();
        ps1.close();

        PreparedStatement ps2 = connection
                .prepareStatement("insert into timeslots (date, startTime, endTime, isOpen) " + "value (?, ?, ?, ?);");

        c.timeslots.forEach(ts -> {
            try {
                ps2.setString(1, ts.date);
                ps2.setString(2, ts.startTime);
                ps2.setString(3, ts.endTime);
                ps2.setBoolean(3, ts.isOpen);

                ps2.executeUpdate();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        });
        ps2.close();

        return true;

    }

    public boolean deleteCalendar(String calendarName) throws Exception {
        PreparedStatement ps = connection.prepareStatement("delete from timeslots" + "where calendarName =?;");
        ps.setString(1, calendarName);
        ps.executeUpdate();
        ps = connection.prepareStatement("delete from calendars" + "where name =?;");
        ps.setString(1, calendarName);
        int numAffected = ps.executeUpdate();

        return (numAffected == 1);
    }

    public List<String> getAllCalendars() throws Exception {
        List<String> calendars = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from calendars;");

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
        String date = resultSet.getString("date");
        String startTime = resultSet.getString("startTime");
        String endTime = resultSet.getString("endTime");
        Boolean isOpen = resultSet.getBoolean("isOpen");

        return new Timeslots(date, startTime, endTime, isOpen);

    }

}
