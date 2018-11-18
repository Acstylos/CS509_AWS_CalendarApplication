package com.amazonaws.lambda.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Calendar implements Serializable {
    
    public String name;
    public List<Timeslots> timeslots;

    public Calendar() {
        // TODO Auto-generated constructor stub
        this.name = "";
        timeslots = new ArrayList<>();
        //timeslots.add(new Timeslots());
        
    }
    
    public Calendar(String calendarName) {
        this.name = calendarName;
        timeslots = new ArrayList<>();
        //timeslots.add(new Timeslots());
    }
}