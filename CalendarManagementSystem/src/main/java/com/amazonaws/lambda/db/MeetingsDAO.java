package com.amazonaws.lambda.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    .prepareStatement("update timeslots set isOpen = ?, attendee = ?, location = ? where id = ?;");
            ps1.setBoolean(1, false);
            ps1.setString(2, timeslots.attendee);
            ps1.setString(3, timeslots.location);
            ps1.setString(4, timeslots.id);

            ps1.executeUpdate();
            ps1.close();

            return true;
        } else {

            return false;
        }
    }

    public boolean cancelMeeting(String timeslotID) {

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "update timeslots set isOpen = ?, attendee = ?, location = ? where id = ?;");
            ps.setBoolean(1, true);
            ps.setString(2, null);
            ps.setString(3, null);
            ps.setString(4, timeslotID);
            ps.executeUpdate();
            ps.close();

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

    }
}
