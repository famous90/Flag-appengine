// common functions

//$(document).ready(showItemAdder);

function replaceAll(find, replace, str) {
    if (find != null && replace != null && str != null)
        return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
    else
        return str;
};

function escapeRegExp(str) {
  return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
};

function lineBreaks(str) {
    return replaceAll('\n', '<br/>', str)
}

function showWaitingDialog() {
    $('#waiting_dialog').modal({
        backdrop: 'static',
        keyboard: false
    });
};

function hideWaitingDialog() {
    $('#waiting_dialog').modal('hide');
};

function showPreview(input, where) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            $('#' + where).attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
};

// gapi initialize

var ROOT = 'https://genuine-evening-455.appspot.com/_ah/api';
var provider;// = {id: 5721646983806976};

function init() {
    gapi.client.load('flagengine', 'v1', function() {
        checkProvider();
    }, ROOT);
}

function checkProvider() {
    if (provider && provider.id)
        loadShops();
    else
        demandSignIn();
}

function demandSignIn() {
    $('#sign_in_form').modal({
        backdrop: 'static',
        keyboard: false
    });
}

function signIn() {
    $('#sign_in_wait').modal({
        backdrop: 'static',
        keyboard: false
    });
    
    var emailValue = $('#sign_in_email').val();
    var passwordValue = $('#sign_in_password').val();
    
    gapi.client.flagengine.providers.get({
        email: emailValue,
        password: passwordValue
    }).execute(function(res) {
        $('#sign_in_wait').modal('hide');
        if (res && res.id) {
            $('#sign_in_email').val('');
            $('#sign_in_password').val('');
            $('#sign_in_form').modal('hide');
            $('#sign_in_warning').text('');
            provider = res;
            loadShops();
        } else
            $('#sign_in_warning').text('wrong email or password');     
    });
}

function signOut() {
    provider = null;
    $('#button_sign_out').css('display', 'none');
    $('#contents_background').css('display', 'none');
    $('#shops').html('');
    $('#shop_contents').css('display', 'none');
    
    init();
}

var shops;
var hqShops;
var brShops;

function loadShops() {
    $('#button_sign_out').css('display', 'block');
    $('#contents_background').css('display', 'block');
    showWaitingDialog();
    gapi.client.flagengine.shops.list.provider({providerId: provider.id}).execute(function(res) {
        hideWaitingDialog();
        processShops(res);
    });
}

function processShops(res) {
    $('#shops').html('');
    shops = res.shops || [];
    hqShops = [];
    brShops = [];
    
    for (var i = 0; i < shops.length; i++)
        if (shops[i].type == 1)
            hqShops[hqShops.length] = shops[i];
        else if (shops[i].type == 2)
            brShops[brShops.length] = shops[i];
    
    getShopHtml();
}

function getShopHtml() {
    $.get('raw/shop.html', function(data) {
        for (var i = 0; i < hqShops.length; i++)
            showShop(data, hqShops[i], true, i);
        
        for (var j = 0; j < brShops.length; j++)
            showShop(data, brShops[j], false, j);
        
        $.get('raw/shop_add.html', function(data_add) {
            $('#shops').append(data_add);
        });
    });
}

function showShop(data, shop, isHQ, i) {
    data = replaceAll('${index}', i, data);
    data = replaceAll('${isHQ}', isHQ, data);
    data = data.replace('${shop_logo_url}', shop.logoUrl + '&width=0&height=0');
    data = data.replace('${shop_name}', shop.name);
    $('#shops').append(data);
}

function showShopAdder() {
    $('#shop_adder').modal({
        backdrop: 'static',
        keyboard: false
    });
    
    gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
        $('#shop_contents_logo_add_form').attr('action', res.url);
    });
    $('#shop_contents_logo_add').attr('src', '');
    $('#shop_contents_name_add').val('');
}

function addShop() {
    $('#add_shop_wait').modal({
        backdrop: 'static',
        keyboard: false
    });
    
    $('#shop_contents_logo_add_form').ajaxSubmit(function(res) {
        gapi.client.flagengine.shops.insert({
            logoUrl: res.url,
            name: $('#shop_contents_name_add').val(),
            imageUrl: '',
            description: '',
            providerId: provider.id,
            type: 1,
            reward: 0
        }).execute(function(res) {
            $('#add_shop_wait').modal('hide');
            $('#shop_adder').modal('hide');
            hqShops[hqShops.length] = res;
            showShopContents(true, hqShops.length - 1);
        });
    });
}

var targetShop;

function toShopsFromContents() {
    loadShops();
    $('#shop_contents').css('display', 'none');
}

function deleteShop() {
    if (confirm('are you sure?'))
        gapi.client.flagengine.shops.delete({'shopId': targetShop.id}).execute(function(res) {
            toShopsFromContents();
        });
}

function showShopContents(isHQ, index) {
    $('#shops').html('');
    
    var shop;
    if (isHQ)
        targetShop = hqShops[index];
    else
        targetShop = brShops[index];
    
    $('#shop_contents').css('display', 'block');
    writeShopInfos();
    setMap();
    getItems();
}

function writeShopInfos() {
    $('#shop_contents_logo').attr('src', targetShop.logoUrl + '&width=0&height=0');
    $('#shop_contents_name').text(targetShop.name);
    $('#shop_contents_poster').attr('src', targetShop.imageUrl + '&width=0&height=0');
    $('#shop_contents_poster').load(adjustDescHeight);
    $('#shop_contents_desc').html(lineBreaks(targetShop.description));
}

function adjustDescHeight() {
    var h = 520 - $('#shop_contents_poster').height();
    $('#shop_contents_desc').css('height', h + 'px');
}

var map;
var flags;
var markers;

function setMap() {
    var mapOptions = {
        center: new google.maps.LatLng(37.397, 127.644),
        zoom: 7
    };
    map = new google.maps.Map(document.getElementById('shop_contents_flag_box'), mapOptions);
    
    
    getFlags();
}

function getFlags() {
    gapi.client.flagengine.flags.list.byshop({shopId: targetShop.id}).execute(function(res) {
        flags = res.flags || [];
        showFlags();
    });
}

function showFlags() {
    markers = [];
    for (var i = 0; i < flags.length; i++) {
        appendMarker(i, flags[i]);
    }
}

function appendMarker(i, flag) {
    var latLng = new google.maps.LatLng(flag.lat, flag.lon);
    var marker = new google.maps.Marker({
        position: latLng,
        title: flag.shopName
    });
    
    marker.setMap(map);
    markers[i] = marker;
}

var items;
var hiddenItems;

function getItems() {
    $('#shop_contents_items').html('');
    $('.button_item_add').css('display', 'block');
// if (targetShop.type == 1)
// $('.button_item_add').css('display', 'block');
// else if (targetShop.type == 2)
// $('.button_item_add').css('display', 'none');
    
    gapi.client.flagengine.items.list({shopId: targetShop.id, userId: 0}).execute(function(res) {
        items = res.items || [];
        hiddenItems = res.hiddenItems || [];
        for (var i = 0; i < hiddenItems.length; i++)
            items[items.length] = hiddenItems[i];
        
        getItemHtml();
    });
}

function getItemHtml() {
    var viewPath;
    viewPath = 'raw/item.html';
// if (targetShop.type == 1)
// viewPath = 'raw/item.html';
// else if (targetShop.type == 2)
// viewPath = 'raw/item_br.html'
    
    $.get(viewPath, function(data) {
        $.get('raw/item_edit.html', function(data_edit) {
            for (var i = 0; i < items.length; i++) {
                showItem(data, items[i], i);
// if (targetShop.type == 1)
                appendItemEditor(data_edit, i);
                if (items[i].rewardable)
                    $('#button_item_scan_' + i).css('opacity', 1);
            }
            
            for (var i = 0; i < hiddenItems.length; i++) {
                $('#item_box_' + (items.length - hiddenItems.length + i)).css('opacity', '0.3');
                $('#button_item_expose_' + (items.length - hiddenItems.length + i)).css('display', 'block');
                $('#button_item_scan_' + (items.length - hiddenItems.length + i)).attr('disabled', true);
            }
        });
    });
}

function showItem(data, item, i) {
    data = replaceAll('${index}', i, data);
    data = data.replace('${item_img}', item.thumbnailUrl);
    data = data.replace('${item_name}', item.name);
    data = data.replace('${item_barcodeId}', item.barcodeId);
    data = data.replace('${item_desc}', lineBreaks(item.description));
    data = data.replace('${item_reward}', item.reward);
    data = data.replace('${item_price}', item.price);
    
    
    if (item.oldPrice) {
        data = data.replace('${item_oldPrice}', item.oldPrice);
        data = data.replace('${item_sale}', item.sale + '%');
        $('#item_price').css('color', 'red');
    } else {
        data = data.replace('${item_oldPrice}', '');
        data = data.replace('${item_sale}', '');
    }
    
    $('#shop_contents_items').append(data);
}

function hideItem(index) {
    var item = items[index];
    gapi.client.flagengine.items.branch.hide({
        shopId: targetShop.id,
        itemId: item.id
    }).execute(function(res){
    });
    
    $('#item_box_' + index).fadeTo('300', '0.3');
    $('#button_item_expose_' + index).css('display', 'block');
    $('#button_item_scan_' + index).attr('disabled', true);
}

function exposeItem(index) {
    var item = items[index];
    gapi.client.flagengine.items.branch.expose({
        branchShopId: targetShop.id,
        itemId: item.id,
        rewardable: false
    }).execute(function(res){
    });
    
    $('#button_item_expose_' + index).css('display', 'none');
    $('#item_box_' + index).fadeTo('300', '1');
    $('#button_item_scan_' + index).attr('disabled', false);
}

function toggleScanItem(index) {
    var item = items[index];
    gapi.client.flagengine.items.branch.reward({
        branchShopId: targetShop.id,
        itemId: item.id
    }).execute(function(res){
    });
    
    if (item.rewardable) {
        $('#button_item_scan_' + index).fadeTo('300', 0.2);
        item.rewardable = false;
    } else {
        $('#button_item_scan_' + index).fadeTo('300', 1);
        item.rewardable = true;
    }
}

function appendItemEditor(data_edit, i) {
    data_edit = replaceAll('${index}', i, data_edit);
    $('#shop_contents_items').append(data_edit);
}

function editModeShopHead() {
    $('#shop_contents_head_box').css('display', 'none');
    $('#shop_contents_head_box_edit').css('display', 'block');
    
    gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
        $('#shop_contents_logo_edit_form').attr('action', res.url);
    });
    $('#shop_contents_logo_edit').attr('src', targetShop.logoUrl + '&width=0&height=0');
    $('#shop_contents_name_edit').val(targetShop.name);
}

function cancelEditShopHead() {
    $('#shop_contents_head_box_edit').css('display', 'none');
    $('#shop_contents_head_box').css('display', 'block');
}

function editShopHead() {
    var newName = $('#shop_contents_name_edit').val();
    
    if ($('#shop_contents_logo_edit_file').val())
        $('#shop_contents_logo_edit_form').ajaxSubmit(function(res) {
            gapi.client.flagengine.shops.update({
                id: targetShop.id,
                logoUrl: res.url,
                name: newName
            }).execute(function(res) {
                targetShop = res;
            });

            $('#shop_contents_head_box_edit').css('display', 'none');
            $('#shop_contents_head_box').css('display', 'block');
            $('#shop_contents_logo').attr('src', res.url);
            $('#shop_contents_name').text(newName);
        });
    else {
        gapi.client.flagengine.shops.update({
            id: targetShop.id,
            name: newName
        }).execute(function(res) {
            targetShop = res;
        });
        
        $('#shop_contents_head_box_edit').css('display', 'none');
        $('#shop_contents_head_box').css('display', 'block');
        $('#shop_contents_name').text(newName);
    }
}

function editModeShopDetail() {
    $('#shop_contents_intro_box').css('display', 'none');
    $('#shop_contents_intro_box_edit').css('display', 'block');
    
    gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
        $('#shop_contents_poster_form').attr('action', res.url);
    });
    $('#shop_contents_poster_edit').attr('src', targetShop.imageUrl);
    $('#shop_contents_desc_edit').val(targetShop.description);
}

function cancelEditShopDetail() {
    $('#shop_contents_intro_box_edit').css('display', 'none');
    $('#shop_contents_intro_box').css('display', 'block');
}

function editShopDetail() {
    var newDesc = $('#shop_contents_desc_edit').val();
    
    if ($('#shop_contents_poster_file').val())
        $('#shop_contents_poster_form').ajaxSubmit(function(res) {
            gapi.client.flagengine.shops.update({
                id: targetShop.id,
                imageUrl: res.url,
                description: newDesc
            }).execute(function(res) {
                targetShop = res;
            });
            
            $('#shop_contents_intro_box_edit').css('display', 'none');
            $('#shop_contents_intro_box').css('display', 'block');
            $('#shop_contents_poster').attr('src', res.url);
            $('#shop_contents_desc').text(newDesc);
        });
    else {
        gapi.client.flagengine.shops.update({
            id: targetShop.id,
            description: newDesc
        }).execute(function(res) {
            targetShop = res;
        });
        
        $('#shop_contents_intro_box_edit').css('display', 'none');
        $('#shop_contents_intro_box').css('display', 'block');
        $('#shop_contents_desc').text(newDesc);
    }
}

function editModeItem(index) {
    var targetItem = items[index];
    $('#item_' + index).css('display', 'none');
    $('#item_edit_' + index).css('display', 'block');
    
    gapi.client.flagengine.images.uploadUrl.get().execute(function(res) {
        $('#item_img_edit_form_' + index).attr('action', res.url);
    });
    $('#item_img_edit_' + index).attr('src', targetItem.thumbnailUrl);
    $('#item_name_edit_' + index).val(targetItem.name);
    $('#item_barcode_edit_' + index).val(targetItem.barcodeId);
    $('#item_desc_edit_' + index).val(targetItem.description);
    $('#item_reward_edit_' + index).val(targetItem.reward);
    $('#item_price_edit_' + index).val(targetItem.price);
    
    if (targetItem.oldPrice) {
        $('#item_old_price_edit_' + index).css('display', 'block');
        $('#item_old_price_edit_' + index).val(targetItem.oldPrice);
        $('#item_sale_edit_' + index).prop('checked', true);
    }
}

function onItemSaleToggle(input, index) {
    if (input.checked)
        $('#item_old_price_edit_' + index).css('display', 'block');
    else {
        $('#item_old_price_edit_' + index).val('');
        $('#item_old_price_edit_' + index).css('display', 'none');
    }
}

function cancelEditItem(index) {
    $('#item_edit_' + index).css('display', 'none');
    $('#item_' + index).css('display', 'block');
}

function editItem(index) {
    var targetItem = items[index];
    var newName = $('#item_name_edit_' + index).val();
    var newBarcode = $('#item_barcode_edit_' + index).val();
    var newDesc = $('#item_desc_edit_' + index).val();
    var newReward = $('#item_reward_edit_' + index).val();
    var newPrice = $('#item_price_edit_' + index).val();
    var newOldPrice = $('#item_old_price_edit_' + index).val();
    
    if ($('#item_img_edit_file_' + index).val())
        $('#item_img_edit_form_' + index).ajaxSubmit(function(res) {
            gapi.client.flagengine.items.update({
                id: targetItem.id,
                thumbnailUrl: res.url,
                name: newName,
                barcodeId: newBarcode,
                description: newDesc,
                reward: newReward,
                price: newPrice,
                oldPrice: newOldPrice
            }).execute(function(res) {
                items[index] = res;
                $('#item_sale_' + index).text(res.sale + '%');
            });
            
            $('#item_edit_' + index).css('display', 'none');
            $('#item_' + index).css('display', 'block');
            
            $('#item_img_' + index).attr('src', res.url);
            $('#item_name_' + index).text(newName);
            $('#item_barcode_' + index).text(newBarcode);
            $('#item_desc_' + index).text(newDesc);
            $('#item_reward_' + index).text(newReward);
            $('#item_price_' + index).text(newPrice);
            
            if (newOldPrice && newOldPrice.length > 0) {
                $('#item_old_price_' + index).text(newOldPrice);
                $('#item_price_' + index).css('color', 'red');
            } else {
                $('#item_old_price_' + index).text('');
                $('#item_sale_' + index).text('');
            }
        });
    else {
        gapi.client.flagengine.items.update({
                id: targetItem.id,
                name: newName,
                barcodeId: newBarcode,
                description: newDesc,
                reward: newReward,
                price: newPrice,
                oldPrice: newOldPrice
            }).execute(function(res) {
                items[index] = res;
                $('#item_sale_' + index).text(res.sale + '%');
            });
            
            $('#item_edit_' + index).css('display', 'none');
            $('#item_' + index).css('display', 'block');
            
            $('#item_name_' + index).text(newName);
            $('#item_barcode_' + index).text(newBarcode);
            $('#item_desc_' + index).text(newDesc);
            $('#item_reward_' + index).text(newReward);
            $('#item_price_' + index).text(newPrice);
            
            if (newOldPrice) {
                $('#item_old_price_' + index).text(newOldPrice);
                $('#item_price').css('color', 'red');
            } else {
                $('#item_old_price_' + index).text('');
                $('#item_sale_' + index).text('');
            }
    }
}

function deleteItem(index) {
    var targetItem = items[index];
    if (confirm('are you sure?')) {
        gapi.client.flagengine.items.delete({itemId: targetItem.id}).execute(function(res){
        });
        
        $('#item_' + index).css('display', 'none');
        $('#item_edit_' + index).css('display', 'none');
    }
}

function showItemAdder() {
    $('#item_adder').modal({
        backdrop: 'static',
        keyboard: false
    });
    
    initDropForm();
}

function hideItemAdder() {
    $('#item_adder').modal('hide');
}

var itemAddPrgressTotal;
var itemAddPrgressCount;

function uploadItemSheet() {
    $('#add_item_shop_id').val(targetShop.id);
    if ($('#add_item_sheet_file').val()) {
        $('#add_item_wait').modal({
            backdrop: 'static',
            keyboard: false
        });
        
        itemAddProgress(0);
        itemAddPrgressTotal = fileIndexes.length + 1;
        itemAddPrgressCount = 0;
        
        $('#add_item_sheet_form').ajaxSubmit(function(res) {
            if (res == 'success') {
                itemAddProgress(1 / itemAddPrgressTotal);
                uploadDropItems(targetShop.id, function(res) {
                    itemAddPrgressCount++;
                    itemAddProgress(itemAddPrgressCount / itemAddPrgressTotal);
                }, function() {
                    $('#add_item_wait').modal('hide');
                    hideItemAdder();
                    getItems();
                });
            } else {
                $('#add_item_wait').modal('hide');
                $('#add_item_warning').text(res);
            }
        });
    }
}

function itemAddProgress(per) {
    $('#add_item_progress').css('width', (per * 100) + '%');
}












