package com.amazonaws.lambda.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarModel {

    public String name;
    public String startTime;
    public String endTime;
    public int duration;

    public List<Timeslots> timeslots;

    private List<String> days;
    public String startDate;
    public String endDate;

    public CalendarModel() {
        // TODO Auto-generated constructor stub
        this.name = "";
        this.timeslots = new ArrayList<>();
        this.days = new ArrayList<>();

    }

    public CalendarModel(String calendarName) {
        this.name = calendarName;
        this.timeslots = new ArrayList<>();
        this.days = new ArrayList<>();
    }

    private void generateDays() throws ParseException {
        // set up dateList based on the inputs of the user
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Eliminate all the weekdays
        if (this.endDate == null) {
            Date startDate = dFormat.parse(this.startDate);
            this.days.add(dFormat.format(startDate));

        } else {

            Date startDate = dFormat.parse(this.startDate);
            Date endDate = dFormat.parse(this.endDate);

            Calendar tempStartDate = Calendar.getInstance();
            tempStartDate.setTime(startDate);
            Calendar tempEndDate = Calendar.getInstance();
            tempEndDate.setTime(endDate);
            tempEndDate.add(Calendar.DATE, +1); // Include the end date

            while (tempStartDate.before(tempEndDate)) {
                if (tempStartDate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                        && tempStartDate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {

                    this.days.add(dFormat.format(tempStartDate.getTime()));
                    tempStartDate.add(Calendar.DAY_OF_YEAR, 1);
                } else {
                    tempStartDate.add(Calendar.DAY_OF_YEAR, 1);

                }
            }
        }

    }

    public void generateTimeslots() throws ParseException {

        generateDays();

        SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm");

        // combine the list of days in this calendar and then generate the timeslots
        for (int i = 0; i < this.days.size(); i++) {
            Date sTime = tFormat.parse(this.startTime);
            Date eTime = tFormat.parse(this.endTime);
            while (sTime.getTime() < eTime.getTime()) {
                Timeslots tempT = new Timeslots();
                tempT.date = this.days.get(i);
                tempT.startTime = tFormat.format(sTime);

                Calendar c = Calendar.getInstance();
                c.setTime(sTime);
                c.add(Calendar.MINUTE, this.duration);
                sTime = c.getTime();
                tempT.endTime = tFormat.format(sTime);

                this.timeslots.add(tempT);
            }
        }

    }

}
