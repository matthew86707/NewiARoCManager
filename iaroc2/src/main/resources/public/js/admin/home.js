function updateFromRest() {
    $.ajax({
        type: "GET",
        url: "/rest/matches/data/upcoming",
        dataType: "json",
        success: matchesJSONParser
    });
   }
   
   $(document).ready(updateFromRest);
   
   setInterval(updateFromRest, 20000); //5 seconds
   
   function matchesJSONParser(json) {
	debugger;
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