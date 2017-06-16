$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "/rest/teams/data",
        dataType: "xml",
        success: xmlTeamParser
    });
    $.ajax({
        type: "GET",
        url: "/rest/matches/data",
        dataType: "json",
        success: jsonMatchesParser
    });
});

function jsonMatchesParser(json) {
    json.matches.forEach( function(entry) {
        var dt = new Date(entry.time * 1000);
        var dateStr = moment(dt).format('hh:mm a');
        var teams = [];
        entry.teams.forEach( function(teamEntry) {
            teams.push(teamEntry.name);
        })

        $('#matchToModify').append("<option value='" + entry.id + "'>" + entry.type + " : " + dateStr + " : [" + teams + "]" +
        "</option>");
    });
}

function xmlTeamParser(xml) {
    $(xml).find("Team").each(function () {

        $('#teamSelection')
        $('#teamSelection').append($("<option/>", {
            value: $(this).attr('id'),
            text: $(this).find("name").text()
        }));
    });
}

window.onload=function() {
    $('#mainForm').submit(function ( event ) {
        //Read information from the various form elements and submit.
        event.preventDefault();
        var toSubmit = {
            'teams':[],
            'time':0,
            'date':0,
            'type':'Undefined'
        };
        $('#teamSelection > option').each( function() {
            if(this.selected) {
                toSubmit.teams.push(this.value);
            }
        } );

        toSubmit.time = $('#inputTime')[0].value;

        toSubmit.matchToModify = $('#matchToModify')[0].value;

        toSubmit.date = $('#inputDate')[0].value;

        toSubmit.type = $('#inputType').find(':selected')[0].value;

        $.ajax({
            type: "POST",
            url: "/rest/addOrModifyMatch",
            data: JSON.stringify( toSubmit ),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data){
            	console.log(JSON.stringify(data));
                if(data.status== "failed" && data.hasOwnProperty('reason')) {
                    alert(JSON.stringify(data));
                }
            	},
            failure: function(errMsg) {
                alert(errMsg);
            }
        });

        return false;
    });
}