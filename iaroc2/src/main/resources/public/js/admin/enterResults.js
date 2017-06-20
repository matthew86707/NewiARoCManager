var divisionFilter = -1;
var isResultAvailableFilter = -1;
var teamsFilter = -1;
var dayFilter = -1;
var data = null;



function updateFromRest() {
     $.ajax({
      type: "GET",
      url: "/rest/getMatchResultsExtended",
      dataType: "json",
      success: provideResults
    });
   }
   
   $(document).ready(updateFromRest);

function provideResults(json) {
    data = json;

    var teamsAlreadyLogged = [];
    data.matchResults.forEach( function(entry) {
        var teamId = entry.TEAMID;
        var teamName = entry.TEAMNAME;
        if(teamsAlreadyLogged.indexOf(teamId) == -1) {
            $("#teamsFilter").append("<option value='" + teamId + "'>" + teamName + "</option>");
            teamsAlreadyLogged.push(teamId);
        }

    });

    refreshResults(data);
}
   
function refreshResults() {
    var matches = data.matchResults;

    $("#matchResultsContent").empty();
   
    matches.forEach(function(entry) {

        var time = entry.MATCHTIME;
        var dt = new Date(time * 1000);
        var dateStr = moment(dt).format('ddd hh:mm a');

        var dayOfMonth = moment(dt).format('Do');

        if(divisionFilter != -1 && entry.DIVISION != divisionFilter) {
            return;
        }

        if(isResultAvailableFilter != -1 && entry.ISFINALRESULT != isResultAvailableFilter) {
            return;
        }

        if(teamsFilter != -1 && entry.TEAMID != teamsFilter) {
            return;
        }

        if(dayFilter != -1 && dayOfMonth != dayFilter) {
            return;
        }

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

function submitResults() {
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
            if(data.status== "failed" && data.hasOwnProperty('reason')) {
            alert(JSON.stringify(data));
            }else{
            window.location = '/admin/success.html';
            }
        },
        failure: function(errMsg) {
            alert(errMsg);
        }
    });
}

window.onload=function() {
    $('#mainForm').submit(function ( event ) {
        event.preventDefault();

        submitResults();

        return false;
    });

    $("#divisionSelect").change(function(eventData) {
        divisionFilter = eventData.target.value;
        refreshResults();
    });

    $("#isResultAvailableSelect").change(function(eventData) {
        isResultAvailableFilter = eventData.target.value;
        refreshResults();
    });
    $("#teamsFilter").change(function(eventData) {
        teamsFilter = eventData.target.value;
        refreshResults();
    });
    $("#dayFilter").change(function(eventData) {
        dayFilter = eventData.target.value;
        refreshResults();
    })
}