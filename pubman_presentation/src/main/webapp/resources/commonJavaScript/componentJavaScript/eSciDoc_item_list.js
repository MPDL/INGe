function addItemListFunctions() {
    var slctMenu = $('.selectMenu');
    var itemList = $('.itemList');

    itemList.each(function(i, ele) {
        $(ele).find('.mediumView').each(function(j, elem) {
            $(elem).hide();
        });
        $(ele).find('.collapse').each(function(j, elem) {
            $(elem).hide();
        });
        $(ele).find('.expand').each(function(j, elem) {
            $(elem).show();
        });
        $(ele).find('.collapseTriangle').each(function(j, elem) {
            $(elem).hide();
        });
        $(ele).find('.expandTriangle').each(function(j, elem) {
            $(elem).show();
        });
        $(ele).find('.listItem').on('hover',function() {
            $(this).addClass('listBackground');
        }, function() {
            $(this).removeClass('listBackground');
        });
    });
    // Openration of the select menu for checkboxes 
    // Start with event on document to close the select menu on click elswhere    
    $('html').on('click',function() {
        //        $('.selectMenu').hide();
    });

    slctMenu.on('click',function(evt) {
        evt.preventDefault();
        evt.stopPropagation();
        evt.stopImmediatePropagation();
    });

    function hideElement(element) {
        element.hide(100);
    }

    $('.checkBoxSelectButton').on('click',function(evt) {
        evt.preventDefault();
        evt.stopPropagation();
        evt.stopImmediatePropagation();

        $('body').off("click");
        $('body').off("keydown");

        var cbsButtonPosition = $(this).position();

        var slctMenu = $(this).siblings('.selectMenu');
        $('body').one("click", function(evt) {
            hideElement(slctMenu);
        });
        $('body').one('keydown', function(evt) {
            if (Number(evt.which) === 27) { //check the key-number for number of escape
                hideElement(slctMenu);
            }
        });
        slctMenu.toggle(100, function() {
            if ($(slctMenu).is(':visible')) {
                $(slctMenu).css("left", cbsButtonPosition.left + 10);
                //    			$(slctMenu).css("top", cbsButtonPosition.top - 2);
            }
        });

        //    	$(this).siblings('.selectMenu').toggle(100);
    });

    // Select options    
    var tog = '';
    $('.listHeader').find('.allCheckBox').on('click',function() {
        itemList.find("input[type=checkbox]").prop("checked", !tog);
        tog = !tog;
    });

    $('.listHeader').find('.selectAll').on('click',function() {
        itemList.find('input[type=checkbox]').prop('checked', true);
        hideElement(slctMenu);
    });

    slctMenu.find('.toggleAll').on('click',function() {
        $('.listItem').find('input[type="checkbox"]').click();
        if (itemList.find('.listItem input[type="checkbox"][checked]').length < 1) {
            itemList.find('.allCheckBox').prop('checked', false);
        } else if (itemList.find('.listItem input[type="checkbox"][checked]').length == itemList.find('.listItem input[type="checkbox"]').length) {
            itemList.find('.allCheckBox').prop('checked', true);
        }
        hideElement(slctMenu);
    });

    slctMenu.find('.selectNone').on('click',function() {
        itemList.find("input[type='checkbox']").removeAttr("checked");
        hideElement(slctMenu);
    });

    slctMenu.find('a').each(function(i, elem) {
        $(elem).on('click',function() {
            hideElement(slctMenu);
        });
    });

    $('.headerSwitchView').find('.expandTriangle').on('click',function() {
        $(this).hide();
        $(this).siblings('.collapseTriangle').show();
        itemList.find('.listItem').find('.expandTriangle:visible').each(function(i, elem) {
            $(elem).trigger('click');
        });
    });

    $('.headerSwitchView').find('.collapseTriangle').on('click',function() {
        $(this).hide();
        $(this).siblings('.expandTriangle').show();
        itemList.find('.listItem').find('.collapseTriangle:visible').each(function(i, elem) {
            $(elem).trigger('click');
        });
    });

    $('.shortView').find('.expandTriangle').each(function(i, ele) {
        $(ele).on('click',function() {
            $(this).hide();
            $(this).siblings('.collapseTriangle').show();
            var parentElement = $(this).parents('.listItem');
            $(parentElement).children('.mediumView').slideToggle('normal', function() {
                if (($(parentElement).find('.itemHeader').find('.expandTriangle:visible').length) ==
                    0) {
                    $(parentElement).find('.headerSwitchView').find('.expandTriangle').hide();
                    $(parentElement).find('.headerSwitchView').find('.collapseTriangle').show();
                }
            });
        })
    });

    $('.shortView').find('.collapseTriangle').each(function(i, ele) {
        $(ele).on('click',function() {
            $(this).hide();
            $(this).siblings('.expandTriangle').show();
            var parentElement = $(this).parents('.listItem');
            $(parentElement).children('.mediumView').slideToggle('normal', function() {
                if (($(parentElement).find('.itemHeader').find('.collapseTriangle:visible').length) ==
                    0) {
                    $(parentElement).find('.headerSwitchView').find('.expandTriangle').show();
                    $(parentElement).find('.headerSwitchView').find('.collapseTriangle').hide();
                }
            });
        })
    });

}

function installItemList() {
    /* ADD LISTENERS TO CHANGED DOM */
    addItemListFunctions();
}

$(function() {
    installItemList();
});