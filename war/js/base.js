var ROOT = 'https://genuine-evening-455.appspot.com/_ah/api';

function replaceAll(find, replace, str) {
  return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
};

function escapeRegExp(str) {
  return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
};

function init() {
    gapi.client.load('flagengine', 'v1', function() {
        getShops();
    }, ROOT);
};

function getShops() {
    gapi.client.flagengine.shops.all().execute(function(res) {
        showShops(res);
    });
};

var shops;

function showShops(res) {
    $('#shops').html('');
    shops = res.shops || [];
    getShopHtml(shops);
};

function getShopHtml(shops) {
    $.get('raw/shop.html', function(data) {
        for (var i = 0; i < shops.length; i++) {
            appendShop(data, i, shops[i]);
        }
        
        showShopAdder();
    });
};

function appendShop(data, i, shop) {
    data = replaceAll('${index}', i, data);
    data = data.replace('${logo}', shop.logoUrl);
    data = data.replace('${img}', shop.imageUrl);
    data = data.replace('${name}', shop.name);
    data = data.replace('${desc}', replaceAll('\n', '<br/>', shop.description));
    $('#shops').append(data);
};

function showShopAdder() {
    $.get('raw/add_shop.html', function(data) {
       $('#shops').append(data); 
    });
};

function showShopEditor(i) {
    $.get('raw/edit_shop.html', function(data) {
        makeShopEditor(i, data);
    });
};

function makeShopEditor(i, data) {
    var shop = shops[i];
    data = data.replace('${index}', i);
    data = data.replace('${logo}', shop.logoUrl);
    data = data.replace('${name}', shop.name);
    data = data.replace('${img}', shop.imageUrl);
    data = data.replace('${desc}', shop.description);
    $('#shop_index_' + i).html(data);
};

function hideShopEditor(i) {
    $.get('raw/desc_shop.html', function(data) {
       makeShopDesc(i, data); 
    });
};

function makeShopDesc(i, data) {
    var shop = shops[i];
    data = replaceAll('${index}', i, data);
    data = data.replace('${logo}', shop.logoUrl);
    data = data.replace('${img}', shop.imageUrl);
    data = data.replace('${name}', shop.name);
    data = data.replace('${desc}', replaceAll('\n', '<br/>', shop.description));
    $('#shop_index_' + i).html(data);
};

function showLogoPreview(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
            reader.onload = function (e) {
            $('#shop_logo_preview').attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
};

function showThumnailPreview(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
            reader.onload = function (e) {
            $('#shop_thumbnail_preview').attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
};

function addShop() {
    
};

function editShop() {
    
};

function deleteShop() {
    
};