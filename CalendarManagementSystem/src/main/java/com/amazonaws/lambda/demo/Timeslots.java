package com.amazonaws.lambda.demo;

import java.io.Serializable;

public class Timeslots implements Serializable {
    public int id = 0;
    public String date;
    public String startTime;
    public String endTime;
    public boolean isOpen;
    public String attendee;
    public String location;
    
   public Timeslots() {
        // TODO Auto-generated constructor stub
       id++;
       date = "2018-11-06";
       startTime = "10:00";
       endTime = "10:30";
       isOpen = true;
       attendee = null;
       location = null;
    }
}
