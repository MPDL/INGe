function addPaginatorFunctions() {
    $('.gotoBox').find(':text').keypress(function(event) {

        switch (event.keyCode) {
            case 13:
                {
                    event.preventDefault();
                    $(this).parents('.gotoBox').find('.paginatorFallbackGoBtn').click();
                    break;
                }

        }
    });
}

function addPaginatorItemFunctions() {
    $('.gotoBox').find(':text').keypress(function(event) {
        switch (event.keyCode) {
            case 13:
                {
                    event.preventDefault();
                    $(this).parents('.gotoBox').find('.pageChangeHiddenBtn').click();
                    break;
                }
        }
    });
}

function installExtPaginator() {
    /*ADD LISTENERS TO DOM*/
    addPaginatorFunctions();
    addPaginatorItemFunctions();
}

$(function() {
    installExtPaginator();
});