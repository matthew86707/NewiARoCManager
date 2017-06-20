/**
 * Created by patri_000 on 6/2/2017.
 */

var currentDivision = 0;

function updateFromRest() {

    currentDivision++;
    if(currentDivision >= 3) {
        currentDivision = 0;
    }

    var fullCurrentDivisionURL = "/rest/teams/standings?division=" + currentDivision;
    $.ajax({
        type: "GET",
        url:"/rest/matches/data",
        dataType: "json",
        success: matchesJSONParser
    });
    $.ajax({
        type: "GET",
        url: fullCurrentDivisionURL,
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

setInterval(updateFromRest, 10000); //10 seconds

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
            var dateStr = moment(dt).format('ddd hh:mm a');
            var timeLabel = "<span class='label label-info'>" + dateStr + "</span>";
            var typeLabel = "<span class='label label-success'>" + entry.type + "</span>";
            if(current > dt){
                timeLabel = "<span class='label label-primary'>LIVE</span>";
            }
            var appendContents = "<div class='well'>";

            entry.teams.forEach( function(team) {
                appendContents += "<img class='teamImage' src='" + team.icon + "'>";
            });

            appendContents += "<div class='pull-right'>" + timeLabel + "<br>" + typeLabel + "</div></div>";

            $("#matchesContent").append(appendContents);
            numMatchesShown++;
        }

    });
}

function teamsJsonParser(json) {
    $('#TeamStandingsContains').animate({'opacity': 0}, 1000, function () {
        $("#teamStandings").empty();
        $("#teamStandings").append("<thead class='table'>" +
            "<th>Team</th>" +
            "<th>Total</th>" +
            "<th>Drag Race</th>" +
            "<th>Maze</th>" +
            "<th>Retrieval</th>" +
            "<th>Presentation</th>" +
            "</thead>");

        json.teamScores.forEach( function(team) {
            var appendContents = "<tr>" +
                "<td><img class='teamImage' src='" + team.icon + "'></td>" +
                "<td>" + team.totalScore + "</td>" +
                "<td>" + team.timeDragRace + " (" + team.scoreDragRace + ")" + "</td>" +
                "<td>" + team.timeMaze + " (" + team.scoreMaze + ")" + "</td>" +
                "<td>" + team.timeRetrieval + " (" + team.scoreRetrieval + ")" + "</td>" +
                "<td>" + "(" + team.scorePresentation + ")</td>" +
                "</tr>";


            $("#teamStandings").append(appendContents);
        })

        //Now go ahead and set it to show which division.
        var labelText = "Current Standings: Level " + currentDivision;
        $("#currentStandingsLabel").text(labelText);
    }).animate({'opacity': 1}, 1000);
}

function announcementsParser(json) {
    if( json.announcement != "" && json.announcement != $("#announcementsContents").text()) {
        $('#mssgContainer').animate({'opacity': 0}, 1000, function () {
            $("#announcementsContents").text(json.announcement);
        }).animate({'opacity': 1}, 1000);
    }
}
