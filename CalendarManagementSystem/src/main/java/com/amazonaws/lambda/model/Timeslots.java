package com.amazonaws.lambda.model;

import java.io.Serializable;

public class Timeslots implements Serializable {
    public String date;
    public String startTime;
    public String endTime;
    public boolean isOpen;
    public String attendee;
    public String location;
    
    public Timeslots() {
        // TODO Auto-generated constructor stub
        date = "2018-11-06";
        startTime = "10:00";
        endTime = "10:30";
        isOpen = true;
        attendee = null;
        location = null;
    }

    //
    public Timeslots(String date, String startTime, String endTime, Boolean isOpen) {
        // TODO Auto-generated constructor stub
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isOpen = isOpen;
        
    }
}
