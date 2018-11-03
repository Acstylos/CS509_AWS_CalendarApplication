function loadCalendarByName(e) {
    var form = document.loadByNameForm;
    var name = form.calendarNameSelection.value;

    var requestLocation = "https://x325bb0xrc.execute-api.us-east-2.amazonaws.com/Alpha/calendars/";
    var req = requestLocation + name;

    var xhr = new XMLHttpRequest();
    xhr.open("GET", req, true);

    xhr.onloadend = function () {
        alert(xhr.responseText);
        if (xhr.readyState == XMLHttpRequest.Done) {
            alert(xhr.responseText);
        }
    }

    xhr.send(null);
}

function fillOutCalendar() {
    
    
}

/*
function createDayCard(event) {
    var $this = $(this);
    event.preventDefualt();
    document.getElementById(which).innerHTML = 
    $('#'+which).load('test.html');
}

function populateCalendar() {

}*/
