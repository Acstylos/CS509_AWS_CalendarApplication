package com.amazonaws.lambda.model;

public class Timeslots {
    
    public String id;
    public String date;
    public String startTime;
    public String endTime;
    public boolean isOpen;
    public String attendee;
    public String location;
    
    public Timeslots() {
        // TODO Auto-generated constructor stub
        
    }

    //
    public Timeslots(String id, String date, String startTime, String endTime, boolean isOpen, String attendee, String location) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isOpen = isOpen;
        this.attendee = attendee;
        this.location = location;
        
    }

    public Timeslots(String timeslotID, String attendee, String location) {
        this.id = timeslotID;
        this.attendee = attendee;
        this.location = location;
    }

   
}
