// functions for flag managing

var shopIdForFlags;

function getFlags(shopId) {
    shopIdForFlags = shopId;
    showWaitingDialog();
    gapi.client.flagengine.flags.list.byshop({'shopId': shopId}).execute(function(res) {
        setFlagMenus();
        showFlags(res);
        hideWaitingDialog();
    });
}

function setFlagMenus() {
    showUpIndicator('javascript:backToShopsFromFlags();');
    setAddButton('javascript:showFlagAdderScreen();');
    //$('#flag_adder_screen').on('shown.bs.modal', showFirstFlagAdder);
}

function backToShopsFromFlags() {
    $('#flags').html('');
    getShops();
}

var flags;

function showFlags(res) {
    $('#flags').html('');
    flags = res.flags || [];
    console.log(flags);
    //getFlagHtml(flags);
};