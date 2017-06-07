/**
 * Created by patri_000 on 6/2/2017.
 */

$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "/rest/matches/data",
        dataType: "json",
        success: matchesJSONParser
    });
});

function matchesJSONParser(json) {

    var matches = json.matches;

    matches.forEach(function(entry) {
        var time = entry.time;
        var dt = new Date(time * 1000);
        var current = new Date();
        var dateStr = moment().format('h:mm a');
        var timeLabel = "<span class='label label-info'>" + dateStr + "</span>";
        if(dt > current){
            timeLabel = "<span class='label label-primary'>LIVE</span>";
        }
        var appendContents = "<div class='well'>" + entry.type;

        entry.teams.forEach( function(team) {
            appendContents += "<img class='teamImage' src='" + team.icon + "'>";
        });

        appendContents += "<div class='pull-right'>" + timeLabel + "</div></div>";

        $("#matchesContent").append(appendContents);
    });
}

$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "/rest/teams/standings",
        dataType: "json",
        success: teamsJsonParser
    });
});

function teamsJsonParser(json) {
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

setInterval(function() {
    $.ajax({
        type: "GET",
        url: "/rest/live/info",
        dataType: "xml",
        success: mssgXmlParser
    });
}, 6000); //5 seconds

function mssgXmlParser(xml) {


    $(xml).find("mssg").each(function () {
        var current = $(this);
        $('#mssgContainer').animate({'opacity': 0}, 1000, function () {
            $(".messages").text((current).text());
        }).animate({'opacity': 1}, 1000);

        // $(".messages").text(($(this).text()));

    });

}
