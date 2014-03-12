var ROOT = 'https://genuine-evening-455.appspot.com/_ah/api';

function init() {
    gapi.client.load('flagengine', 'v1', function() {
        getShops();
    }, ROOT);
};

function getShops() {
    gapi.client.flagengine.shops.all().execute(function(res) {
        showShops(res);
    });
}

function showShops(res) {
    res.shops = res.shops || [];
    $('#shops').html('');
    for (var i = 0; i < res.shops.length; i++) {
        var htmlStr = getShopHtml(res.shops[i]);
        $('#shops').append(htmlStr);
    }
}

function getShopHtml(shop) {
    var baseStr = '<div class="shop"><img class="shop_thumbnail" src="${img}"/><div class="shop_name_box">'
                    + '<span class="shop_name">${name}</span></div><div class="shop_desc">${desc}</div></div>';
    
    baseStr = baseStr.replace('${img}', shop.imageUrl);
    baseStr = baseStr.replace('${name}', shop.name);
    baseStr = baseStr.replace('${desc}', shop.description);
    
    return baseStr;
}