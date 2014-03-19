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
        getShops();
    }, ROOT);
};

// functions for shop managing

function getShops() {
    showWaitingDialog();
    gapi.client.flagengine.shops.all().execute(function(res) {
        setShopMenus();
        showShops(res);
        hideWaitingDialog();
    });
};

function setShopMenus() {
    hideUpIndicator();
    setAddButton('#');
}

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
    var desc = $('#edit_shop_desc_' + i).val();
    var reward = shops[i].reward;
    gapi.client.flagengine.shops.update({
        'id': id,
        'name': name,
        'logoUrl': logoUrl,
        'imageUrl': imageUrl,
        'description': desc,
        'reward': reward
    }).execute(function(res) {
        shops[i] = res;
        hideShopEditor(i);
    });
}

function deleteShop(i) {
    if (confirm('Are you sure?')) {
            var shop = shops[i];
            gapi.client.flagengine.shops.delete({'shopId': shop.id}).execute(function(res) {
               getShops(); 
        });
    }
};

function loadItems(i) {
    var shop = shops[i];
    $('#shops').html('');
    getItems(shop.id);
}

// functions for item managing

var shopIdForItems = 0;

function getItems(shopId) {
    shopIdForItems = shopId;
    showWaitingDialog();
    gapi.client.flagengine.items.list({'shopId': shopId, 'userId': 0}).execute(function(res) {
        setItemMenus();
        showItems(res);
        hideWaitingDialog();
    });
}

function setItemMenus() {
    showUpIndicator('javascript:backToShops();');
    setAddButton('javascript:showItemAdderScreen();');
    $('#item_adder_screen').on('shown.bs.modal', showFirstItemAdder);
}

function backToShops() {
    $('#items').html('');
    getShops();
}

var items;

function showItems(res) {
    $('#items').html('');
    items = res.items || [];
    getItemHtml(items);
};

function getItemHtml(items) {
    $.get('raw/item.html', function(data) {
        for (var i = 0; i < items.length; i++) {
            appendItem(data, i, items[i]);
        }
    });
};

function appendItem(data, i, item) {
    data = replaceAll('${index}', i, data);
    data = data.replace('${img}', item.thumbnailUrl);
    data = data.replace('${name}', item.name);
    data = data.replace('${desc}', item.description);
    data = data.replace('${sale}', item.sale);
    data = data.replace('${price}', item.price);
    if (item.oldPrice)
        data = data.replace('${oldPrice}', item.oldPrice);
    else
        data = data.replace('${oldPrice}', '');
    data = data.replace('${reward}', item.reward);
    data = data.replace('${barcodeId}', item.barcodeId);
    $('#items').append(data);
};

function showItemAdderScreen() {
    $('#item_adder_screen').modal({
        backdrop: 'static',
        keyboard: false
    });
};

function hideItemAdderScreen() {
    $('#item_adder_screen').modal('hide');
}

var ia_first = true;
var indexArray = [0];

function showFirstItemAdder() {
    if (ia_first) {
        $.get('raw/add_item.html', function(data) {
            data = replaceAll('${index}', 0, data);
            $('#items_to_be_added').append(data);
        
            gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
                $('#add_item_thumbnail_form_0').attr('action', res.url);
            });
            
            showItemAdderButton();
        });
        
        ia_first = false;
    }
}

function addItemAdder() {
    hideItemAdderButton();
    $.get('raw/add_item.html', function(data) {
        var index = addAndGetIndex();
        data = replaceAll('${index}', index, data);
        $('#items_to_be_added').append(data);
        
        gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
            $('#add_item_thumbnail_form_' + index).attr('action', res.url);
        });
        
        showItemAdderButton();
    });
}

function addAndGetIndex() {
    indexArray[indexArray.length] = indexArray[indexArray.length - 1] + 1;
    return indexArray[indexArray.length - 1];
}

function removeItemAdder(i) {
    $('#add_item_' + i).remove();
    removeIndex(i);
}

function removeIndex(i) {
    var tempArray = indexArray;
    indexArray = [];
    for (var j = 0; j < tempArray.length; j++) {
        if (tempArray[j] != i)
            indexArray.push(tempArray[j]);
    }
}

function showItemAdderButton() {
    var adder = '<div class="add_item adder" onclick="addItemAdder();"></div>';
    $('#items_to_be_added').append(adder);
}

function hideItemAdderButton() {
    $('.add_item.adder').remove();
}

var itemUploadTotal = 0;
var itemUploadCount = 0;

function addItems() {
    $('#uploading_items_dialog').modal({
        backdrop: 'static',
        keyboard: false
    });
    
    itemUploadTotal = indexArray.length;
    for (var i = 0; i < itemUploadTotal; i++) {
        addItem(indexArray[i]);
    }
}

function addItem(i) {
    $('#add_item_thumbnail_form_' + i).ajaxSubmit(function(res) {
        $('#add_item_thumbnail_url_' + i).val('https://genuine-evening-455.appspot.com/serve?blob-key=' + res.url);
        sendAddItem(i);
    });
}

function sendAddItem(i) {
    var name = $('#add_item_name_' + i).val();
    var description = $('#add_item_desc_' + i).val();
    var thumbnailUrl = $('#add_item_thumbnail_url_' + i).val();
    var sale = $('#add_item_sale_' + i).val();
    var oldPrice = $('#add_item_old_price_' + i).val();
    var price = $('#add_item_price_' + i).val();
    var reward = $('#add_item_reward_' + i).val();
    var barcode = $('#add_item_barcode_' + i).val();
    var shopId = shopIdForItems;
    gapi.client.flagengine.items.insert({
        'name': name,
        'description': description,
        'thumbnailUrl': thumbnailUrl,
        'sale': sale,
        'oldPrice': oldPrice,
        'price': price,
        'reward': reward,
        'barcodeId': barcode,
        'shopId': shopId
    }).execute(function(res) {
        $('#add_item_' + i).remove();
        itemUploadCount++;
        if (itemUploadTotal == itemUploadCount)
            finishItemUpload();
    });
}

function finishItemUpload() {
    $('#uploading_items_dialog').modal('hide');
    hideItemAdderScreen();
    getItems(shopIdForItems);
}

function deleteItem(i) {
    if (confirm('are you sure?')) {
        $('#item_index_' + i).remove();
        
        var item = items[i];
        gapi.client.flagengine.items.delete({'itemId': item.id}).execute(function(res) {
           // nothing to do
        });
    }
}
