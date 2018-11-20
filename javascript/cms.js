

//#region API-Endpoints

const apiUrl = "https://x325bb0xrc.execute-api.us-east-2.amazonaws.com/Alpha/";
const calendarsEndpoint = "calendars/";
const timeslotsEndpoint = "timeslots/";
const modifyDayEndpoint = "Day/";
const meetingEndpoint = "meeting"

//#endregion

//#region HTML IDs/Names

// For Select Calendar Dropdown
const calendarNameSelect = "calendarNameSelection";
// For Create Calendar Form
const createCalendarName = "createCalendarName";
const createCalendarStartDate = "createCalendarStartDate";
const createCalendarEndDate = "createCalendarEndDate";
const createCalendarDuration = "createCalendarDuration";
const createCalendarStartTime = "createCalendarStartTime";
const createCalendarEndTime = "createCalendarEndTime";
// For Loaded Calendar Display
const calendarDisplay = "templateOutput";
// Monthly Display
const monthlyDisplayTemplate = "fullCalendar";
const monthlyDisplayBody = "calendarBody";
const monthlyDisplayHeaderMonth = "calendarMonth";
const monthlyDisplayHeaderYear = "calendarYear";
const weekDisplayTemplate = "calendarWeek";
const weekDisplayBody = "newWeek";
const calendarDayTemplate = "calendarDay";
const calendarDayCard = "newDay";
const calendarDayCardDate = "dayDate";
const calendarDayCardBody = "dayBody";
const calendarDayCardInfo = "dayInfo";
const calendarDayCardButton = "dayAction";
// Daily Display
const dailyDisplayTemplate = "fullDay";
const dailyDisplayTimeslotList = "dayTimeslots";
const dailyDisplayHeaderTemplate = "dailyScheduleHeader";
const dailyDisplayTimeslotTemplate = "dayTimeslot";
const timeslotDisplayStartTime = "timeslotStartTime";
const timeslotDisplayIsOpen = "timeslotIsOpen";
const timeslotDisplayAttendee = "timeslotAttendee";
const timeslotDisplayLocation = "timeslotLocation";
const timeslotDisplayButton = "meetingAction";

const loadedCalendarLocation = "loadedCalendar";
// For Add/Remove Day form
const modifyDayTemplate = "modifyCalendarDateForm";
const modifyDayDatePicker = "modifyDatePicker";
const modifyCalendarDateInput = "modifyCalendarDateInput";

var loadedCalendarName = "";

//#endregion

//#region JSON object property names

// newCalendarModel
const calendarName = "calendarName";
const startTime = "startTime";
const endTime = "endTime";
const startDate = "startDate";
const endDate = "endDate";
const duration = "duration";
const modifyCalendarDate = "date";

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
                //displayCalendar(xhr.responseText);
                updateLoadedCalendarDisplay(xhr.responseText);
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
                //displayCalendar(xhr.responseText);
                updateLoadedCalendarDisplay(xhr.responseText);
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

function getMonthlySchedule(){

}

function postCancelMeeting(event){
    var parentRow = event.parentElement;
    // TODO: Where do I get this?
    var timeslotId = 0;

    var formData = {};
    formData["attendee"] = parentRow.children[2].value;
    formData["location"] = parentRow.children[3].value;
    var jsonRequest = JSON.stringify(formData);

    var request = apiUrl + calendarsEndpoint + loadedCalendarName + "/" + timeslotsEndpoint + timeslotId + "/" + meetingEndpoint;
    /*
    var xhr = new XMLHttpRequest();
    xhr.open("POST", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                alert(xhr.responseText);
            }
        }
    };
    //xhr.send(jsonRequest);*/
    alert("Canceling Meeting");
}

function putScheduleMeeting(event){
    var parentRow = event.parentElement;
    var attendee = parentRow.children[2].value;
    var location = parentRow.children[3].value;
    if(attendee == ""){
        alert("No attendee");
        return;
    }
    // TODO: Where do I get this?
    var timeslotId = 0;

    var formData = {};
    formData["attendee"] = attendee;
    formData["location"] = location;
    var jsonRequest = JSON.stringify(formData);

    var request = apiUrl + calendarsEndpoint + loadedCalendarName + "/" + timeslotsEndpoint + timeslotId + "/" + meetingEndpoint;
    /*
    var xhr = new XMLHttpRequest();
    xhr.open("PUT", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                alert(xhr.responseText);
            }
        }
    };
    //xhr.send(jsonRequest);*/
    alert("Scheduling Meeting");
}

function putAddNewDay(event){
    var formData = {};
    formData[modifyCalendarDate] = document.getElementById(modifyCalendarDateInput).value;
    var jsonRequest = JSON.stringify(formData);

    var request = apiUrl + calendarsEndpoint + loadedCalendarName + "/" + modifyDayEndpoint;
    /*
    var xhr = new XMLHttpRequest();
    xhr.open("PUT", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                alert(xhr.responseText);
            }
        }
    };
    //xhr.send(jsonRequest);*/
}

function deleteRemoveDay(event){
    var formData = {};
    formData[modifyCalendarDate] = document.getElementById(modifyCalendarDateInput).value;
    var jsonRequest = JSON.stringify(formData);

    var request = apiUrl + calendarsEndpoint + loadedCalendarName + "/" + modifyDayEndpoint;
    /*
    var xhr = new XMLHttpRequest();
    xhr.open("DELETE", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                alert(xhr.responseText);
            }
        }
    };
    //xhr.send(jsonRequest);*/
}

//#endregion

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

function updateLoadedCalendarDisplay(calendarString){
    var destination = document.getElementById(calendarDisplay);
    destination.innerHTML = "";

    var template = document.getElementById(monthlyDisplayTemplate);
    var clone = document.importNode(template.content, true);

    var calendarBody = clone.getElementById(monthlyDisplayBody);

    var calendar = JSON.parse(calendarString);
    // Determine where in the actual calendar the data-calendar starts and ends
    // and use this to properly create the HTML elements to display the data
    // properly
    var calendarName = calendar.name;
    loadedCalendarName = calendarName;
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

    var calendarLoadedInfo = document.getElementById(loadedCalendarLocation);
    calendarLoadedInfo.textContent = "Loaded Calendar: " + loadedCalendarName;
    var calendarMonth = clone.getElementById(monthlyDisplayHeaderMonth);
    calendarMonth.textContent = monthLongStrings[firstDay.month()];
    var calendarYear = clone.getElementById(monthlyDisplayHeaderYear);
    calendarYear.textContent = firstDay.year();

    destination.appendChild(clone);
}

function addWeekToCalendar(timeslots){
    var template = document.getElementById(weekDisplayTemplate);
    var clone = document.importNode(template.content, true);

    var newWeek = clone.getElementById(weekDisplayBody);

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
    var template = document.getElementById(calendarDayTemplate);
    var clone = document.importNode(template.content, true);

    var cardDiv = clone.getElementById(calendarDayCard);
    cardDiv.id = dayCardIdPrefix + setDateAsMonthAndDay(day.date(), day.month());

    var dayDate = clone.getElementById(calendarDayCardDate);
    dayDate.textContent = setFirstDateAsMonthAndDay(day.date(), day.month());
    var dayInfo = clone.getElementById(calendarDayCardInfo);
    dayInfo.textContent = "Not-In-Calendar";

    var dayActionButton = clone.getElementById(calendarDayCardButton);
    dayActionButton.style.display = "none"; // comment out when implemented
    dayActionButton.textContent = "Add to Calendar";
    dayActionButton.setAttribute("onclick", "postAddNewDay(this);");

    var dayBody = clone.getElementById(calendarDayCardBody);
    dayBody.className = "card-body non-calendar";

    return clone;
}

function addDayToWeek(timeslots){
    var template = document.getElementById(calendarDayTemplate);
    var clone = document.importNode(template.content, true);

    var day = getUtcMoment(timeslots[0].date);

    var cardDiv = clone.getElementById(calendarDayCard);
    cardDiv.id = dayCardIdPrefix + setDateAsMonthAndDay(day.date(), day.month());

    var dayDate = clone.getElementById(calendarDayCardDate);
    dayDate.textContent = setFirstDateAsMonthAndDay(day.date(), day.month());
    var dayInfo = clone.getElementById(calendarDayCardInfo);
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
    var request = apiUrl + calendarsEndpoint + loadedCalendarName;

    var xhr = new XMLHttpRequest();
    xhr.open("GET", request, true);
    xhr.onloadend = function () {
        if(xhr.readyState === xhr.DONE) {
            if(xhr.status === 200){
                updateLoadedCalendarDisplay(xhr.responseText);
            }
        }
    };
    xhr.send(null);
}

//#endregion

//#region Show Daily Schedule View

function showDailySchedule(timeslots){
    var destination = document.getElementById(calendarDisplay);
    destination.innerHTML = "";

    var template = document.getElementById(dailyDisplayTemplate);
    var clone = document.importNode(template.content, true);

    var dailySchedule = clone.getElementById(dailyDisplayTimeslotList);

    addDailyScheduleHeader(dailySchedule);
    for(i = 0; i < timeslots.length; i++){
        addTimeslotToDailySchedule(timeslots[i], dailySchedule);
    }

    destination.appendChild(clone);
}

function addDailyScheduleHeader(schedule){
    // TODO: force this to stay at the top of the list group?
    var template = document.getElementById(dailyDisplayHeaderTemplate);
    var clone = document.importNode(template.content, true);

    schedule.appendChild(clone);
}

function addTimeslotToDailySchedule(timeslot, day){
    var template = document.getElementById(dailyDisplayTimeslotTemplate);
    var clone = document.importNode(template.content, true);

    var timeslotStartTime = clone.getElementById(timeslotDisplayStartTime);
    timeslotStartTime.textContent = timeslot.startTime;
    var timeslotIsOpen = clone.getElementById(timeslotDisplayIsOpen);
    timeslotIsOpen.textContent = timeslot.isOpen.toString();
    var timeslotAttendee = clone.getElementById(timeslotDisplayAttendee);
    var timeslotLocation = clone.getElementById(timeslotDisplayLocation);

    if(!timeslot.isOpen){

        timeslotAttendee.value = timeslot.attendee;
        timeslotAttendee.placeholder = "";
        timeslotAttendee.disabled = true;
        timeslotLocation.value = timeslot.location;
        timeslotLocation.placeholder = "";
        timeslotLocation.disabled = true;

        var meetingActionButton = clone.getElementById(timeslotDisplayButton);
        meetingActionButton.textContent = "Cancel Meeting"
        meetingActionButton.setAttribute("onclick", "postCancelMeeting(this);");
    }

    day.appendChild(clone);
}

//#endregion

//#endregion

//#region Modify Calendar Day

function showModifyCalendarForm(){
    var destination = document.getElementById(calendarDisplay);
    destination.innerHTML = "";

    var template = document.getElementById(modifyDayTemplate);
    var clone = document.importNode(template.content, true);

    var modifyDayPicker = clone.getElementById(modifyDayDatePicker);

    $(function () {
        $(modifyDayPicker).datetimepicker({
            local: 'en',
            format: 'L',
            defaultDate: new moment()
        });
    });

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
