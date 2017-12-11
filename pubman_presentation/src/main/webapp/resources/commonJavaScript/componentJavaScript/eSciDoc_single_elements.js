function installSameHeight() {
    $('.sameHeightSlave').each(function(i, elem) {
        $(elem).height($('.sameHeightMaster').height());
    });
}

$(function() {
    installSameHeight();
});