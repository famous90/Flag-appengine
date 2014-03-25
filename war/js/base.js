// common functions

function replaceAll(find, replace, str) {
  return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
};

function escapeRegExp(str) {
  return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
};

function showWaitingDialog() {
    $('#waiting_dialog').modal({
        backdrop: 'static',
        keyboard: false
    });
};

function hideWaitingDialog() {
    $('#waiting_dialog').modal('hide');
};

function showUpIndicator(callBack) {
    $('#up_indicator').css('display', 'block');
    $('#up_indicator').attr('href', callBack);
}

function hideUpIndicator() {
    $('#up_indicator').css('display', 'none');
}

function setAddButton(callBack) {
    $('#add_button').attr('href', callBack);
}

function showPreview(input, where, i) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
            reader.onload = function (e) {
            $('#' + where + '_preview_' + i).attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
};

// gapi initialize

var ROOT = 'https://genuine-evening-455.appspot.com/_ah/api';

function init() {
    gapi.client.load('flagengine', 'v1', function() {
        responsiveReady();
        getShops();
    }, ROOT);
};