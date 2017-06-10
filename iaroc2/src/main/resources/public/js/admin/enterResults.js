function updateFromRest() {
  //  $.ajax({
   //     type: "GET",
  //      url: "/rest/matchResult/data",
  //      dataType: "json",
  //      success: matchResultJSONParser
//    });
    $("#matchResultsContent").append("<tr> Test </tr>");
   }
   
   $(document).ready(updateFromRest);
   
   function matchResultJSONParser(json) {
    var matches = json.matches;
   
    var numMatchesShown = 0;
    matches.forEach(function(entry) {
        //Only list pending/in-progress matches. Not cancelled or complete.

            var appendContents = "<tr>" + entry.team;

            $("#matchesContent").append(appendContents);
            numMatchesShown++;
        

    });
}