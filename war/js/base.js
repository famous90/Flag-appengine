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
        gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
            $('#add_shop_logo_form').attr('action', res.url);
        });
        gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
            $('#add_shop_image_form').attr('action', res.url);
        });
    });
};

function showShopEditor(i) {
    $.get('raw/edit_shop.html', function(data) {
        makeShopEditor(i, data);
        gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
            $('#edit_shop_logo_form_' + i).attr('action', res.url);
        });
        gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
            $('#edit_shop_image_form_' + i).attr('action', res.url);
        });
    });
};

function makeShopEditor(i, data) {
    var shop = shops[i];
    data = replaceAll('${index}', i, data);
    data = replaceAll('${logo}', shop.logoUrl, data);
    data = data.replace('${name}', shop.name);
    data = replaceAll('${img}', shop.imageUrl, data);
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

function showLogoPreview(input, i) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
            reader.onload = function (e) {
            $('#shop_logo_preview_' + i).attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
};

function showThumbnailPreview(input, i) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
            reader.onload = function (e) {
            $('#shop_thumbnail_preview_' + i).attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
};

function addShop() {
    $('#add_shop_logo_form').ajaxSubmit(function(resLogo) {
        $('#add_shop_logo_url').val('https://genuine-evening-455.appspot.com/serve?blob-key=' + resLogo.url);
        $('#add_shop_image_form').ajaxSubmit(function(resImage) {
            $('#add_shop_image_url').val('https://genuine-evening-455.appspot.com/serve?blob-key=' + resImage.url);
            sendAddShop();
        });
    });
};

function sendAddShop() {
    var name = $('#add_shop_name').val();
    var logoUrl = $('#add_shop_logo_url').val();
    var imageUrl = $('#add_shop_image_url').val();
    var type = 1;
    var desc = $('#add_shop_desc').val();
    var reward = 0;
    gapi.client.flagengine.shops.insert({
        'name': name,
        'logoUrl': logoUrl,
        'imageUrl': imageUrl,
        'type': type,
        'description': desc,
        'reward': reward
    }).execute(function(res) {
        getShops();
    });
};

function editShop(i) {
    $('#edit_shop_logo_form_' + i).ajaxSubmit(function(resLogo) {
        if (resLogo.url == '')
            $('#edit_shop_logo_url_' + i).val('');
        else
            $('#edit_shop_logo_url_' + i).val('https://genuine-evening-455.appspot.com/serve?blob-key=' + resLogo.url);
        $('#edit_shop_image_form_' + i).ajaxSubmit(function(resImage) {
            if (resImage.url == '')
                $('#edit_shop_image_url_' + i).val('');
            else
                $('#edit_shop_image_url_' + i).val('https://genuine-evening-455.appspot.com/serve?blob-key=' + resImage.url);
            sendEditShop(i);
        });
    });
};

function sendEditShop(i) {
    var id = shops[i].id;
    var name = $('#edit_shop_name_' + i).val();
    var logoUrl = $('#edit_shop_logo_url_' + i).val();
    var imageUrl = $('#edit_shop_image_url_' + i).val();
    var type = shops[i].type;
    var desc = $('#edit_shop_desc_' + i).val();
    var reward = shops[i].reward;
    gapi.client.flagengine.shops.update({
        'id': id,
        'name': name,
        'logoUrl': logoUrl,
        'imageUrl': imageUrl,
        'type': type,
        'description': desc,
        'reward': reward
    }).execute(function(res) {
        shops[i] = res;
        hideShopEditor(i);
    });
}

function deleteShop(i) {
    var res = confirm('Are you sure?');
    if  (res == true) {
        var shop = shops[i];
        gapi.client.flagengine.shops.delete({'shopId': shop.id}).execute(function(res) {
           getShops(); 
        });
    }
};