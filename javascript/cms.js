// API-Endpoints
const apiUrl = "https://x325bb0xrc.execute-api.us-east-2.amazonaws.com/Alpha/";
const calendarsEndpoint = "calendars/";
const timeslotsEndpoint = "timeslots/";


function getCalendarByName(event) {
    var form = document.selectCalendar;
    var name = form.calendarNameSelection.value;

    var request = apiUrl + calendarsEndpoint + name;

    var xhr = new XMLHttpRequest();
    xhr.open("GET", request, true);
    xhr.onloadend = displayCalendar(xhr.responseText);
    xhr.send(null);
}

function deleteCalendarByName(event) {
    var form = document.selectCalendar;
    var name = form.calendarNameSelection.value;

    var request = apiUrl + calendarsEndpoint + name;

    var xhr = new XMLHttpRequest();
    xhr.open("DELETE", request, true);
    xhr.onloadend = deleteCalendar(xhr.responseText);
    xhr.send(null);
}

function getCalendarNames(event) {
    var request = apiUrl + calendarsEndpoint;

    var xhr = new XMLHttpRequest();
    xhr.open("GET", request, true);
    xhr.onloadend = updateSelectCalendarDropdown(xhr.responseText);
    xhr.send(null);
}

function postCreateCalendar(event) {
    var form = document.createNewCalendar;
    // get form data
    var json = "{}";
    // JSON-ify the data

    var request = apiUrl + calendarsEndpoint;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", request, true);
    xhr.onloadend = displayCalendar(xhr.responseText);
    xhr.send(json);
}

function displayCalendar(data) {
    alert(data);
}

function updateSelectCalendarDropdown(data) {
    alert(data);
}

function deleteCalendar(data) {
    alert(data);
}
