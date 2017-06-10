/**
 * Created by patri_000 on 6/9/2017.
 */

$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "/rest/teams/data",
        dataType: "xml",
        success: xmlTeamParser
    });
});

function xmlTeamParser(xml) {
    $(xml).find("Team").each(function () {

        $('#teamToModify')
        $('#teamToModify').append($("<option/>", {
            value: $(this).attr('id'),
            text: $(this).find("name").text()
        }));
    });
}