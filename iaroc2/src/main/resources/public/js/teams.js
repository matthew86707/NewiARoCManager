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

        $(".content").append('<div class="col-xs-4"><div class="panel panel-default"> <div class="panel-body"> <h4><b>' + $(this).find("name").text() +  '</b></h4><br>' + $(this).find("slogan").text() + '</div></div></div>');



    });

    $(".card").fadeIn(1000);

}