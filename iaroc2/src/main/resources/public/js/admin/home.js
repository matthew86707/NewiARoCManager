/**
 * Created by patri_000 on 6/2/2017.
 */

$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "/rest/matches/data",
        dataType: "xml",
        success: matchesXmlParser
    });
});

function matchesXmlParser(xml) {


    $(xml).find("Match").each(function () {
        var unix = parseInt($(this).find("time").text());
        var dt = new Date(unix*1000);
        var current = new Date();
        var timeLabel = "<span class='label label-info'>" + dt.getHours() + ":" + dt.getMinutes() + "</span>";
        if(dt > current){
            timeLabel = "<span class='label label-primary'>LIVE</span>";
            $("#matchesContent").append("<div style='text-color:blue;'> <a href='/index/admin/forms/addMatchResult/'> <div class='well' style='font-size: 20'><b>" + $(this).find("teamA").text() + "</b> &nbsp VS &nbsp <b>" + $(this).find("teamB").text() + "</b> <div class='pull-right'>"+ timeLabel + "</a></div></div></div>");
        }



    });

}