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

    //Now go ahead and have delete perform an ajax on click
    $("#delete").prop('disabled', true).click( function(e) {
        event.preventDefault();
        var currentTeamId = $("#teamToModify").val();
        var url =  `/rest/deleteTeam?teamId=${currentTeamId}`;
        $.ajax({
            type: "GET",
            url: url,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data){
                console.log(JSON.stringify(data));
                if(data.status== "failed" && data.hasOwnProperty('reason')) {
                    alert(JSON.stringify(data));
                }else{
                    window.location = '/admin/success.html';
                }},
            failure: function(errMsg) {
                alert(errMsg);
            }
        });
        return false;
    });

    $("#teamToModify").change( function(e) {
        if($(this).val() == -1) {
            $("#delete").prop('disabled', true);
        }
        else {
            $("#delete").prop('disabled', false);
        }
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
                window.location = '/admin/success.html';
            },
            failure: function(errMsg) {
                alert(errMsg);
            }
        });
        return false;
    });
}