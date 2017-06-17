function updateFromRest() {
     $.ajax({
      type: "GET",
      url: "/rest/getMatchResultsExtended",
      dataType: "json",
      success: matchResultJSONParser
    });
   }
   
   $(document).ready(updateFromRest);
   
   function matchResultJSONParser(json) {
    var matches = json.matchResults;
   
    matches.forEach(function(entry) {

        var time = entry.MATCHTIME;
        var dt = new Date(time * 1000);
        var dateStr = moment(dt).format('ddd hh:mm a');

        var appendContents = "<tr name='matchResult' teamId=" + entry.TEAMID + " matchId=" + entry.MATCHID + " id='match_result_" + entry.MATCHID + entry.TEAMID + "'>";

        appendContents += "<td name='team'>" + entry.TEAMNAME + "</td>";
        appendContents += "<td name='matchType'>" + entry.MATCHTYPE + "</td>";
        appendContents += "<td name='matchTime'>" + dateStr + "</td>";
        var isSelected = "selected='selected'";
        var notSelected = "";
        appendContents += "<td name='isResultAvailble'><select ><option " + (entry.ISFINALRESULT ? isSelected : notSelected) + " value='true'>True</option><option " + (!entry.ISFINALRESULT ? isSelected : notSelected) + " value='false'>False</option></select></td>";
        appendContents += "<td name='completedObjective'><select><option " + (entry.COMPLETEDOBJECTIVE ? isSelected : notSelected) + " value='true'>True</option><option " + (!entry.COMPLETEDOBJECTIVE ? isSelected : notSelected) + " value='false'>False</option></select></td>";
        appendContents += "<td name='scoreTime'><input value=" + entry.SCORETIME + " type='text' class='form-control' placeholder='150000'></td>";
        appendContents += "<td name='bonusPoints'><input value=" + entry.BONUSPOINTS + " name='numBonusPoints' type='text' class='form-control' placeholder='5'></td>";
        appendContents += "</tr>"
        $("#matchResultsContent").append(appendContents);
    });
}

window.onload=function() {
    $('#mainForm').submit(function ( event ) {
        event.preventDefault();

        var contentsToSubmit = [];

        //Go through the table and read the value in each of the fields into a JSON array and submit.
        //We could alternately have gone the route of adding event handlers to each field.
        //But, seemed this was both easier and possibly even more efficient (assuming large numbers of entries are changed)

        $("tr[name='matchResult']").each(function() {
            var teamId = this.attributes['teamId'].value;
            var matchId = this.attributes['matchId'].value;
            var isResultAvailable = this.children.isResultAvailble.children[0].selectedIndex == 0;
            var completedObjective  = this.children.completedObjective.children[0].selectedIndex == 0;
            var scoreTime = this.children.scoreTime.children[0].value;

            var bonusPoints = this.children.bonusPoints.children[0].value;

            var entryToSubmit = {
                'teamId': teamId,
                'matchId' : matchId,
                'bonusPoints' : bonusPoints,
                'time' : scoreTime,
                'completedObjective': completedObjective,
                'isFinalResult' : isResultAvailable
            };
            contentsToSubmit.push(entryToSubmit);
        })

        var toSubmit = { "matchResults" : contentsToSubmit };

        $.ajax({
            type: "POST",
            url: "/rest/addMatchResults",
            data: JSON.stringify( toSubmit ),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data){
                console.log(JSON.stringify(data));
                //if(data.status== "failed" && data.hasOwnProperty('reason')) {
                    alert(JSON.stringify(data));
              //  }
                },
            failure: function(errMsg) {
                alert(errMsg);
            }
        });
        return false;
    });
}