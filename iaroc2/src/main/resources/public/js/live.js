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
    var MAX_MATCHES_TO_SHOW = 10;
    var numMatchesShown = 0;
    matches.forEach(function(entry) {
        //Only list pending/in-progress matches. Not cancelled or complete.
        if(entry.status == 0 && numMatchesShown <= MAX_MATCHES_TO_SHOW) {
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
            numMatchesShown++;
        }

    });
}

function teamsJsonParser(json) {
    $("#teamStandings").empty();
    $("#teamStandings").append("<thead class='table table-striped table-hover'>" +
        "<th>Team</th>" +
        "<th>Total</th>" +
        "<th>Drag Race (score : time)</th>" +
    "<th>Maze (score : time)</th>" +
    "<th>Retrieval (score : time)</th>" +
    "<th>Presentation</th>" +
    "</thead> <tbody>");

    json.teamScores.forEach( function(team) {

        var appendContents = "<tr>" +
            "<td><img class='teamImage' src='" + team.icon + "'></td>" +
            "<td>" + team.totalScore + "</td>" +
            "<td>" + team.scoreDragRace + " : " + team.timeDragRace + "</td>" +
            "<td>" + team.scoreMaze + " : " + team.timeMaze + "</td>" +
            "<td>" + team.scoreRetrieval + " : " + team.timeRetrieval + "</td>" +
            "<td>" + team.scorePresentation + "</td>" +
                "</tr>";


        $("#teamStandings").append(appendContents + "</tbody>");
    })
}

function announcementsParser(json) {
    if( json.announcement != "" && json.announcement != $("#announcementsContents").text()) {
        $('#mssgContainer').animate({'opacity': 0}, 1000, function () {
            $("#announcementsContents").text(json.announcement);
        }).animate({'opacity': 1}, 1000);
    }
}
