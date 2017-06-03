$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "/rest/teams/data",
        dataType: "xml",
        success: xmlTeamParser
    });
});

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
            'type':'Undefined'
        };
        $('#teamSelection > option').each( function() {
            if(this.selected) {
                toSubmit.teams.push(this.value);
            }
        } );

        toSubmit.time = $('#inputTime')[0].value;

        toSubmit.type = $('#inputType').find(':selected')[0].value;

        $.ajax({
            type: "POST",
            url: "/rest/addMatch",
            data: JSON.stringify( toSubmit ),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data){alert(data);},
            failure: function(errMsg) {
                alert(errMsg);
            }
        });

        return false;
    });
}