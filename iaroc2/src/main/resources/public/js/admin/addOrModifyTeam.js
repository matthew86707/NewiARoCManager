/**
 * Created by patri_000 on 6/9/2017.
 */

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

        $('#teamToModify')
        $('#teamToModify').append($("<option/>", {
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
            'id':0,
            'name':"",
            'icon':"",
            'division':0
        };

        toSubmit.id = $('#teamToModify')[0].value;

        toSubmit.name = $('#inputName')[0].value;

        toSubmit.icon = $('#inputIconURL')[0].value;

        toSubmit.division = $('#inputDivision')[0].value;


        $.ajax({
            type: "POST",
            url: "/rest/addOrModifyTeam",
            data: JSON.stringify( toSubmit ),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data){
                console.log(JSON.stringify(data));
                alert(JSON.stringify(data));
            },
            failure: function(errMsg) {
                alert(errMsg);
            }
        });
        return false;
    });
}