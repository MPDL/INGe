/*QUICK SEARCH INITIALISATION*/

function addQuickSearchFunction(){
	$('.quickSearchTextInput').keyup(function(keyEvent){
		if(keyEvent.keyCode == '13'){
			$(this).parents('.searchMenu').find('.quickSearchBtn').click();
		};
	});
};


function installQuickSearchShortcut() {
	addQuickSearchFunction();
}


function installSameHeight() {
	$('.sameHeightSlave').each(function(i,elem){$(elem).height($('.sameHeightMaster').height());});
}

$(function(){
	installQuickSearchShortcut();
	installSameHeight();
});