

// API-Endpoints
const apiUrl = "https://x325bb0xrc.execute-api.us-east-2.amazonaws.com/Alpha/";
const calendarsEndpoint = "calendars/";
const timeslotsEndpoint = "timeslots/";
// HTML IDs/Names
const calendarNameSelect = "calendarNameSelection";
const createCalendarName = "createCalendarName";
const createCalendarStartDate = "createCalendarStartDate";
const createCalendarEndDate = "createCalendarEndDate";
const createCalendarDuration = "createCalendarDuration";
const createCalendarStartTime = "createCalendarStartTime";
const createCalendarEndTime = "createCalendarEndTime";
const loadedCalendarLocation = "loadedCalendar";
// JSON object property names
// newCalendarModel
const calendarName = "calendarName";
const startTime = "startTime";
const endTime = "endTime";
const startDate = "startDate";
const endDate = "endDate";
const duration = "duration";

const dateFormat = "YYYY-MM-DD";

var monthLongStrings = [ 'January',
'February',
'March',
'April',
'May',
'June',
'July',
'August',
'September',
'October',
'November',
'December' ]

var monthShortStrings = [ 'Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sept','Oct','Nov','Dec' ]

// REST Requests

/**
 * Makes the API call to get the calendar specified by the name
 *
 */
function getCalendarByName() {
    var calendarName = document.getElementById(calendarNameSelect).value;

    var request = apiUrl + calendarsEndpoint + calendarName;

    var xhr = new XMLHttpRequest();
    xhr.open("GET", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                displayCalendar(xhr.responseText);
            }
        }
    };
    xhr.send(null);
}

/**
 * Makes the API call to delete the calendar specified by the name
 *
 */
function deleteCalendarByName() {
    var calendarName = document.getElementById(calendarNameSelect).value;

    var request = apiUrl + calendarsEndpoint + calendarName;

    var xhr = new XMLHttpRequest();
    xhr.open("DELETE", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                deleteCalendar(xhr.responseText);
            }
        }
    };
    xhr.send(null);
}

/**
 * Makes the API call to get all calendar names
 *
 */
function getCalendarNames() {
    var request = apiUrl + calendarsEndpoint;

    var xhr = new XMLHttpRequest();
    xhr.open("GET", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                updateSelectCalendarDropdown(xhr.responseText);
            }
        }
    };
    xhr.send(null);
}

/**
 * Makes the API call to create a new calendar
 *
 */
function postCreateCalendar() {
    var formData = {};
    formData[calendarName] = document.getElementById(createCalendarName).value;
    formData[startTime] = document.getElementById(createCalendarStartTime).value;
    formData[endTime] = document.getElementById(createCalendarEndTime).value;
    formData[startDate] = document.getElementById(createCalendarStartDate).value;
    formData[endDate] = document.getElementById(createCalendarEndDate).value;
    formData[duration] = document.getElementById(createCalendarDuration).value;
    var jsonRequest = JSON.stringify(formData);

    var request = apiUrl + calendarsEndpoint;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                displayCalendar(xhr.responseText);
            }
        }
    };
    xhr.send(jsonRequest);
}

// Callback Functions
function displayCalendar(data) {
    document.getElementById(loadedCalendarLocation).innerHTML = "<p>New Calendar: </p>" + data;
}

function deleteCalendar(data) {
    alert(data);
    // don't forget to re-update the list of available calendars
    getCalendarNames();
}

// Helper Functions
/**
 * Updates the calendarNameSelect select element with a set of options
 * based on the given list of calendars.
 *
 * @param {*} listOfCalendars String format of a JSON array of calendar objects.
 */
function updateSelectCalendarDropdown(listOfCalendars){
    var select = document.getElementById(calendarNameSelect);
    jsonCalendars = JSON.parse(listOfCalendars);
    jsonCalendars.calendars.forEach(calendarName => addCalendarOptionToSelect(calendarName, select));
    mockLoadSingleCalendar();
}

/**
 * Adds calendar information as an option to a given select element
 *
 * @param {*} calendarName A calendar name
 * @param {*} select The select element to add the option to
 */
function addCalendarOptionToSelect(calendarName, select){
    var option = document.createElement("option");
    option.value = calendarName;
    option.appendChild(document.createTextNode(calendarName));

    select.appendChild(option);
}

/**
 * Updates the stepping of the start and end time pickers based on
 * the duration that gets selected, forcing the pickers to use that
 * duration when inputing start/end times.
 *
 */
function onSelectDuration(){
    var duration = document.getElementById(createCalendarDuration).value;
    $(startTimePicker).datetimepicker('stepping', duration);
    $(endTimePicker).datetimepicker('stepping', duration);
}

/**
 * A function that wraps updateSelectCalendarDropdown with
 * a mock object for testing.
 *
 */
function mockGetCalendarDropdownOptions(){
    updateSelectCalendarDropdown(mockCalendarList);
}

function mockLoadSingleCalendar(){
    updateCalendarTemplate(mockCalendarData);
}


function getUtcMoment(dateString){
    // We should be using UTC so we can convert between timezones
    return moment(dateString + "Z", dateFormat);
}


function updateCalendarTemplate(calendar){
    var calendarName = calendar.name;
    var timeslots = calendar.timeslots;

    var destination = document.getElementById("templateOutput");
    destination.innerHTML = "";

    var template = document.getElementById("fullCalendar");
    var clone = document.importNode(template.content, true);

    var calendarBody = clone.getElementById("calendarBody");

    // Determine where in the actual calendar the data-calendar starts and ends
    // and use this to properly create the HTML elements to display the data
    // properly
    var firstDay = getUtcMoment(timeslots[0].date);
    var firstWeek = firstDay.week();
    var lastWeek = getUtcMoment(timeslots[timeslots.length-1].date).week();

    for(i = 0; i <= (lastWeek - firstWeek); i++){
        // select all timeslots where week = firstWeek + i
        var currentWeekTimeslots = timeslots.filter(function(timeslot){
            var timeslotDay = getUtcMoment(timeslot.date);
            return timeslotDay.week() == (firstWeek+i);
        });
        // add that array of timeslots to the calendar
        addWeekToCalendar(currentWeekTimeslots, calendarBody);
    }

    var calendarMonth = clone.getElementById("calendarMonth");
    calendarMonth.textContent = monthLongStrings[firstDay.month()];
    var calendarYear = clone.getElementById("calendarYear");
    calendarYear.textContent = firstDay.year();

    destination.appendChild(clone);
}

function addWeekToCalendar(timeslots, calendarBody){
    var template = document.getElementById("calendarWeek");
    var clone = document.importNode(template.content, true);

    var newWeek = clone.getElementById("newWeek");

    firstDate = getUtcMoment(timeslots[0].date);
    var weekInYear = firstDate.week();
    var year = firstDate.year();

    // Loop through the days in the week
    for(j = 0; j < 7; j++){
        var timeslotsForDay = timeslots.filter(function(timeslot){
            var timeslotDay = getUtcMoment(timeslot.date);
            return timeslotDay.day() == j;
        });
        if(timeslotsForDay.length == 0){
            var nonDay = moment().year(year).week(weekInYear).day(j);
            addNonDayToWeek(nonDay, newWeek);
        } else {
            addDayToWeek(timeslotsForDay, newWeek);
        }
    }

    calendarBody.appendChild(clone);
}

function addNonDayToWeek(day, week){
    var template = document.getElementById("nonCalendarDay");
    var clone = document.importNode(template.content, true);

    var dayDate = clone.getElementById("dayDate");
    dayDate.textContent = getDateAsMonthAndDay(day.date(), day.month());
    var dayInfo = clone.getElementById("dayInfo");
    dayInfo.textContent = "Not-In-Calendar";

    week.appendChild(clone);
}

function addDayToWeek(timeslots, week){
    var template = document.getElementById("calendarDay");
    var clone = document.importNode(template.content, true);

    var day = getUtcMoment(timeslots[0].date);

    var dayDate = clone.getElementById("dayDate");
    dayDate.textContent = getDateAsMonthAndDay(day.date(), day.month());
    var dayInfo = clone.getElementById("dayInfo");
    var meetingCount = 0;
    for(k = 0; k < timeslots.length; k++){
        if(!timeslots[k].isOpen){
            meetingCount++;
        }
    }
    dayInfo.textContent = meetingCount.toString() + " Meetings";

    week.appendChild(clone);
}

function getDateAsMonthAndDay(date, month){
    if(date == 1){
        date = monthShortStrings[month] + " " + date;
    }
    return date;
}








function showDailySchedule(event){
    var destination = document.getElementById("templateOutput");
    destination.innerHTML = "";

    var template = document.getElementById("fullDay");
    var clone = document.importNode(template.content, true);

    var dailySchedule = clone.getElementById("dayTimeslots");

    addDailyScheduleHeader(dailySchedule);
    // TODO: use the event to get the day data?
    addTimeslotToDailySchedule("10:30", dailySchedule);
    addTimeslotToDailySchedule("11:00", dailySchedule);
    addTimeslotToDailySchedule("11:30", dailySchedule);
    addTimeslotToDailySchedule("12:00", dailySchedule);
    addTimeslotToDailySchedule("12:30", dailySchedule);
    addTimeslotToDailySchedule("13:00", dailySchedule);
    addTimeslotToDailySchedule("13:30", dailySchedule);
    addTimeslotToDailySchedule("14:00", dailySchedule);
    addTimeslotToDailySchedule("14:30", dailySchedule);
    addTimeslotToDailySchedule("15:00", dailySchedule);
    addTimeslotToDailySchedule("16:30", dailySchedule);
    addTimeslotToDailySchedule("17:00", dailySchedule);
    addTimeslotToDailySchedule("17:30", dailySchedule);
    addTimeslotToDailySchedule("18:00", dailySchedule);
    addTimeslotToDailySchedule("18:30", dailySchedule);
    addTimeslotToDailySchedule("19:00", dailySchedule);
    addTimeslotToDailySchedule("19:30", dailySchedule);
    addTimeslotToDailySchedule("20:00", dailySchedule);

    destination.appendChild(clone);
}

function addDailyScheduleHeader(schedule){
    // TODO: force this to stay at the top of the list group?
    
    var template = document.getElementById("dailyScheduleHeader");
    var clone = document.importNode(template.content, true);

    schedule.appendChild(clone);
}

function addTimeslotToDailySchedule(timeslot, day){
    var template = document.getElementById("dayTimeslot");
    var clone = document.importNode(template.content, true);

    var timeslotStartTime = clone.getElementById("timeslotStartTime");
    timeslotStartTime.textContent = timeslot;
    var timeslotIsOpen = clone.getElementById("timeslotIsOpen");
    timeslotIsOpen.textContent = "true";
    var timeslotAttendee = clone.getElementById("timeslotAttendee");
    timeslotAttendee.textContent = "G. Heineman";
    var timeslotLocation = clone.getElementById("timeslotLocation");
    timeslotLocation.textContent = "";

    // TODO: if is timeslot is a meeting change button text to be cancel meeting
    // otherwise leave it as schedule meeting.

    day.appendChild(clone);
}

function showScheduleMeetingForm(){
    var destination = document.getElementById("templateOutput");
    destination.innerHTML = "";

    var template = document.getElementById("scheduleMeetingForm");
    var clone = document.importNode(template.content, true);

    destination.appendChild(clone);
}

function showMonthlySchedule(){
    // TODO: have this work on a loaded calendar name or something
    // updateCalendarTemplate();
    mockLoadSingleCalendar()
}

// Initialize date/time pickers
$(function () {
    $(startDatePicker).datetimepicker({
        locale: 'en',
        format: 'L',
        defaultDate: new Date()
    });
    $(endDatePicker).datetimepicker({
        local: 'en',
        format: 'L',
        defaultDate: new Date()
    });
    $(startTimePicker).datetimepicker({
        local: 'en',
        format: 'HH:mm',
        defaultDate: '0',
        stepping: 10
    });
    $(endTimePicker).datetimepicker({
        local: 'en',
        format: 'HH:mm',
        defaultDate: '0',
        stepping: 10
    });
});


window.onload = getCalendarNames();
//window.onload = mockLoadCalendars();