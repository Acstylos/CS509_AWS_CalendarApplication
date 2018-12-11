# CS509_AWS_CalendarApplication

Basic use of the website:

Controls for Create, Load, and Delete of entire calendars is located on the left. 
- Note: it is possible to click delete/load/create without filling out the web-forms, which won't work. Please avoid doing so.
- It is also possible to select end times before start times, so pelase avoid this as well.

All Calendars in the DB are populated in the Load/Delete dropdown menu when the page is loaded, so avoid going back or refreshing.

Calendar Specific controls exist in the right side of the webpage.
Unless a calendar is "loaded" show full schedule, show monthly schedule, close timeslots, and manage days won't work properly. Please ensure you have a calendar loaded before using these controls
To see the daily schedule click on "Show Schedule" for the specific day you want to see.
Only the first month is shown at the top of the page; all subsequent months are shown next to their first day as an abbreviation.
Show full schedule gives you all days in the entire calendar.
Show monthly schedule gives you just the days in the month. Sometimes you might notice a missing first or last day of the month, and that's because weekends are excluded from the calendar, and thus not used when determining when the calendar starts/ends.
Close timeslots opens the possbility to close multiple timeslots at once. It is not possible to close a single specific timeslot from this location.
- Note: For closing all timeslots at given day of the week and time, you must select any day that falls on the desired day of the week.
Modify calendar day allows you to choose a day to add or remove from the loaded calendar.

Most Timeslot Controls exist in daily schedule view (accessed by clicking "See Schedule")
We make a distinction between open/closed timeslots and meetings. Meetings are only those timeslots who have an attendee, thus it's possible to have an "open" Meeting.
IMPORTANT: Once a timeslot is closed there is no way to reopen it.
To schedule a meeting from here you must enter an attendee name and click "Schedule Meeting" for the specific timeslot you want to occupy.
- An optional location can also be input, but the webpage will alert you when you don't have an attendee.
Only from this daily schedule view can you close specific timeslots; Done by click on the "close timeslot" button associated with the timeslot you want to close.
