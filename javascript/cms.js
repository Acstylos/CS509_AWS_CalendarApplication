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

var mockCalendarList = [
    {value: "Personal", text: "Personal"},
    {value: "Professional", text: "Professional"},
    {value: "Extra", text: "Extra"}
]

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
 * @param {*} listOfCalendars JSON array of calendar objects.
 */
function updateSelectCalendarDropdown(listOfCalendars){
    var select = document.getElementById(calendarNameSelect);
    listOfCalendars.forEach(calendar => addCalendarOptionToSelect(calendar, select));
}

/**
 * Adds calendar information as an option to a given select element
 *
 * @param {*} calendar A JSON calendar object
 * @param {*} select The select element to add the option to
 */
function addCalendarOptionToSelect(calendar, select){
    var option = document.createElement("option");
    // TODO actually pull the right json elements out
    option.value = calendar.value;
    option.appendChild(document.createTextNode(calendar.text));

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
function mockLoadCalendars(){
    updateSelectCalendarDropdown(mockCalendarList);
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


window.onload = mockLoadCalendars();
