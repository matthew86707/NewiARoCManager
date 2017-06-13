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
        var dateStr = moment(dt).format('hh:mm a');

        var appendContents = "<tr id='match_result_" + entry.MATCHID + entry.TEAMID + "'>";

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