

//#region API-Endpoints

const apiUrl = "https://x325bb0xrc.execute-api.us-east-2.amazonaws.com/Alpha/";
const calendarsEndpoint = "calendars/";
const timeslotsEndpoint = "timeslots/";

//#endregion

//#region HTML IDs/Names

const calendarNameSelect = "calendarNameSelection";
const createCalendarName = "createCalendarName";
const createCalendarStartDate = "createCalendarStartDate";
const createCalendarEndDate = "createCalendarEndDate";
const createCalendarDuration = "createCalendarDuration";
const createCalendarStartTime = "createCalendarStartTime";
const createCalendarEndTime = "createCalendarEndTime";
const loadedCalendarLocation = "loadedCalendar";

//#endregion

//#region JSON object property names

// newCalendarModel
const calendarName = "calendarName";
const startTime = "startTime";
const endTime = "endTime";
const startDate = "startDate";
const endDate = "endDate";
const duration = "duration";

//#endregion

//#region Usable Constants

const dateFormat = "YYYY-MM-DD";
const dayCardIdPrefix = "d_";

var monthLongStrings = ['January','February','March','April','May','June','July','August','September','October','November','December'];
var monthShortStrings = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sept','Oct','Nov','Dec'];

//#endregion

//#region REST Requests

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
                //updateLoadedCalendarDisplay(xhr.responseText);
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
                alert(xhr.responseText);
                getCalendarNames();
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
                //updateLoadedCalendarDisplay(xhr.responseText);
            }
        }
    };
    xhr.send(jsonRequest);
}

function getDailySchedule(event) {
    /*
    var dayToView = event.parentElement.parentElement;
    
    var request = apiUrl + calendarsEndpoint;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                //showDailySchedule(xhr.responseText);
                showDailySchedule(mockDailySchedule);
            }
        }
    };
    xhr.send(jsonRequest);
    // Here to prevent multi-line comment overflow */

    showDailySchedule(mockDailySchedule);
}

function postCancelMeeting(){
    alert("Canceling Meeting");
}

//#endregion

// TODO: remove this and replace with updateLoadedCalendarDisplay() in REST-requests
function displayCalendar(data) {
    document.getElementById(loadedCalendarLocation).innerHTML = "<p>New Calendar: </p>" + data;
}

//#region Calendar Creation

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

//#endregion

//#region Calendar Load/Delete Dropdown Menu

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

//#endregion

//#region Show Monthly Calendar View

function updateLoadedCalendarDisplay(calendar){
    var destination = document.getElementById("templateOutput");
    destination.innerHTML = "";

    var template = document.getElementById("fullCalendar");
    var clone = document.importNode(template.content, true);

    var calendarBody = clone.getElementById("calendarBody");

    // Determine where in the actual calendar the data-calendar starts and ends
    // and use this to properly create the HTML elements to display the data
    // properly
    var calendarName = calendar.name;
    var timeslots = calendar.timeslots;
    var firstDay = getUtcMoment(timeslots[0].date);
    var firstWeek = firstDay.week();
    var lastWeek = getUtcMoment(timeslots[timeslots.length-1].date).week();

    for(i = 0; i <= (lastWeek - firstWeek); i++){
        var currentWeekTimeslots = timeslots.filter(function(timeslot){
            var timeslotDay = getUtcMoment(timeslot.date);
            return timeslotDay.week() == (firstWeek+i);
        });
        calendarBody.appendChild(addWeekToCalendar(currentWeekTimeslots));
    }

    var calendarMonth = clone.getElementById("calendarMonth");
    calendarMonth.textContent = monthLongStrings[firstDay.month()];
    var calendarYear = clone.getElementById("calendarYear");
    calendarYear.textContent = firstDay.year();

    destination.appendChild(clone);
}

function addWeekToCalendar(timeslots){
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
            newWeek.appendChild(addNonDayToWeek(nonDay));
        } else {
            newWeek.appendChild(addDayToWeek(timeslotsForDay));
        }
    }

    return clone;
}

function addNonDayToWeek(day){
    var template = document.getElementById("nonCalendarDay");
    var clone = document.importNode(template.content, true);

    var cardDiv = clone.getElementById("newDay");
    cardDiv.id = dayCardIdPrefix + setDateAsMonthAndDay(day.date(), day.month());

    var dayDate = clone.getElementById("dayDate");
    dayDate.textContent = setFirstDateAsMonthAndDay(day.date(), day.month());
    var dayInfo = clone.getElementById("dayInfo");
    dayInfo.textContent = "Not-In-Calendar";

    return clone;
}

function addDayToWeek(timeslots){
    var template = document.getElementById("calendarDay");
    var clone = document.importNode(template.content, true);

    var day = getUtcMoment(timeslots[0].date);

    var cardDiv = clone.getElementById("newDay");
    cardDiv.id = dayCardIdPrefix + setDateAsMonthAndDay(day.date(), day.month());

    var dayDate = clone.getElementById("dayDate");
    dayDate.textContent = setFirstDateAsMonthAndDay(day.date(), day.month());
    var dayInfo = clone.getElementById("dayInfo");
    var meetingCount = 0;
    for(k = 0; k < timeslots.length; k++){
        if(!timeslots[k].isOpen){
            meetingCount++;
        }
    }
    dayInfo.textContent = meetingCount.toString() + " Meetings";

    return clone;
}

function showMonthlySchedule(){
    // TODO: have this work on a loaded calendar name or something
    // updateCalendarTemplate();
    mockLoadSingleCalendar()
}

//#endregion

//#region Show Daily Schedule View

function showDailySchedule(timeslots){
    var destination = document.getElementById("templateOutput");
    destination.innerHTML = "";

    var template = document.getElementById("fullDay");
    var clone = document.importNode(template.content, true);

    var dailySchedule = clone.getElementById("dayTimeslots");

    addDailyScheduleHeader(dailySchedule);
    for(i = 0; i < timeslots.length; i++){
        addTimeslotToDailySchedule(timeslots[i], dailySchedule);
    }

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
    timeslotStartTime.textContent = timeslot.startTime;
    var timeslotIsOpen = clone.getElementById("timeslotIsOpen");
    timeslotIsOpen.textContent = timeslot.isOpen.toString();
    var timeslotAttendee = clone.getElementById("timeslotAttendee");
    timeslotAttendee.textContent = timeslot.attendee;
    var timeslotLocation = clone.getElementById("timeslotLocation");
    timeslotLocation.textContent = timeslot.location;

    // TODO: if is timeslot is a meeting change button text to be cancel meeting
    // otherwise leave it as schedule meeting.
    if(!timeslot.isOpen){
        var meetingActionButton = clone.getElementById("meetingAction");
        meetingActionButton.textContent = "Cancel Meeting"
        meetingActionButton.onclick = postCancelMeeting;
    }

    day.appendChild(clone);
}

function showScheduleMeetingForm(){
    var destination = document.getElementById("templateOutput");
    destination.innerHTML = "";

    var template = document.getElementById("scheduleMeetingForm");
    var clone = document.importNode(template.content, true);

    destination.appendChild(clone);
}

//#endregion

//#region Generalized Helper Functions

function setFirstDateAsMonthAndDay(date, month){
    var newDateString = date;
    if(date == 1){
        newDateString = setDateAsMonthAndDay(date, month);
    }
    return newDateString;
}

function setDateAsMonthAndDay(date, month){
    return monthShortStrings[month] + " " + date;
}

/**
 * Converts a given YYYY-MM-DD date string into a momentjs object
 * following UTC-time, to standardize it.
 *
 * @param {String} dateString A date as a string in the format YYYY-MM-DD
 * @returns A momentjs object representing the time in the dateString.
 */
function getUtcMoment(dateString){
    // We should be using UTC so we can convert between timezones
    return moment(dateString + "Z", dateFormat);
}

//#endregion

//#region Mock/Testing Function Calls

/**
 * A function that wraps updateSelectCalendarDropdown with
 * a mock object for testing.
 *
 */
function mockGetCalendarDropdownOptions(){
    updateSelectCalendarDropdown(mockCalendarList);
}

function mockLoadSingleCalendar(){
    updateLoadedCalendarDisplay(mockCalendarData);
}

//#endregion

//#region Initialize date/time pickers

$(function () {
    $(startDatePicker).datetimepicker({
        locale: 'en',
        format: 'L',
        defaultDate: new moment()
    });
    $(endDatePicker).datetimepicker({
        local: 'en',
        format: 'L',
        defaultDate: new moment()
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

//#endregion

window.onload = getCalendarNames();
