// functions for responsive web
function responsiveReady() {
    $(window).resize(alignItems);
}

function alignItems() {
    var width = $('#items').width();
    var num = parseInt(width / 300);
    var marginTotal = width - num * 300;
    var margin = marginTotal / (num * 2);
    $('.item').css('margin', margin + 'px');
}