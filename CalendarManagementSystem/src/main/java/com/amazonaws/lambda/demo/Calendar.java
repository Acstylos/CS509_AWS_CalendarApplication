package com.amazonaws.lambda.demo;

import java.io.Serializable;
import java.util.ArrayList;

public class Calendar implements Serializable {
    
    public String name;
    public ArrayList<Timeslots> timeslots;

    public Calendar() {
        // TODO Auto-generated constructor stub
        this.name = "";
        timeslots = new ArrayList<Timeslots>();
        timeslots.add(new Timeslots());
        
    }
    
    public Calendar(String calendarName) {
        this.name = calendarName;
        timeslots = new ArrayList<Timeslots>();
        timeslots.add(new Timeslots());
    }
}
