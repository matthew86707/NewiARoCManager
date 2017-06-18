$(document).ready(function () {
    $.ajax({
        type: "GET",
        url: "rest/teams/data",
        dataType: "xml",
        success: xmlParser
    });
});

function xmlParser(xml) {

    $('#loading').fadeOut();

    $(xml).find("Team").each(function () {

        var sectionIdentifier = "";
        if($(this).find("division").text() == 0) {
            sectionIdentifier = "#division0Content";
        }
        else if($(this).find("division").text() == 1) {
            sectionIdentifier = "#division1Content";
        }
        else if($(this).find("division").text() == 2) {
            sectionIdentifier = "#division2Content";
        }

        if(sectionIdentifier != "") {
            $(sectionIdentifier).append('<div class="col-xs-4">' +
                '<div class="panel panel-default"> <div class="panel-body"> <h4><b>'
                + $(this).find("name").text() +  '</b></h4>' +
                '</br><img class="teamImage" src="' + $(this).find("iconUrl").text() + '" >' +
                '</div></div></div>');
        }
    });

    $(".card").fadeIn(1000);

}