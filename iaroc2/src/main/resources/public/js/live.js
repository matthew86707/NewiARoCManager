/**
 * Created by patri_000 on 6/2/2017.
 */

function updateFromRest() {
    $.ajax({
        type: "GET",
        url: "/rest/matches/data",
        dataType: "json",
        success: matchesJSONParser
    });
    $.ajax({
        type: "GET",
        url: "/rest/teams/standings",
        dataType: "json",
        success: teamsJsonParser
    });
    $.ajax({
        type: "GET",
        url: "/rest/live/announcements",
        dataType: "json",
        success: announcementsParser
    });
}

$(document).ready(updateFromRest);

setInterval(updateFromRest, 20000); //5 seconds

function matchesJSONParser(json) {

    var matches = json.matches;
    $("#matchesContent").empty();
    matches.forEach(function(entry) {
        //Only list pending/in-progress matches. Not cancelled or complete.
        if(entry.status == 0) {
            var time = entry.time;
            var dt = new Date(time * 1000);
            var current = new Date();
            var dateStr = moment(dt).format('hh:mm a');
            var timeLabel = "<span class='label label-info'>" + dateStr + "</span>";
            if(current > dt){
                timeLabel = "<span class='label label-primary'>LIVE</span>";
            }
            var appendContents = "<div class='well'>" + entry.type;

            entry.teams.forEach( function(team) {
                appendContents += "<img class='teamImage' src='" + team.icon + "'>";
            });

            appendContents += "<div class='pull-right'>" + timeLabel + "</div></div>";

            $("#matchesContent").append(appendContents);
        }

    });
}

function teamsJsonParser(json) {
    $("#teamStandings").empty();
    json.teamScores.forEach( function(team) {

        var appendContents = "<tr>" +
            "<td><img class='teamImage' src='" + team.icon + "'></td>" +
            "<td>" + team.totalScore + "</td>" +
            "<td>" + team.scoreDragRace + "</td>" +
            "<td>" + team.scoreMaze + "</td>" +
            "<td>" + team.scoreRetrieval + "</td>" +
            "<td>" + team.scorePresentation + "</td>" +
                "</tr>";


        $("#teamStandings").append(appendContents);
    })
}

function announcementsParser(json) {
    if( json.announcement != "" && json.announcement != $("#announcementsContents").text()) {
        $('#mssgContainer').animate({'opacity': 0}, 1000, function () {
            $("#announcementsContents").text(json.announcement);
        }).animate({'opacity': 1}, 1000);
    }
}
