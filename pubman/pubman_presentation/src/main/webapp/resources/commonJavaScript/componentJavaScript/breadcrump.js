/**
 * @author schlender
 */


/* 
 * these fix exist because of double embedded breadcrump in different pages (e.g. simple submission, easy import submission, ...)
 */
function checkBreadcrump() {
	if ($('.clear .breadcrumb')[1]) {
		$('.clear .breadcrumb')[1].innerHTML = $('.clear .breadcrumb')[0].innerHTML;
		var obj = $($('.clear .breadcrumb')[0]);
		
		for (var i =0; i < 20; i++) {
			obj = obj.parent();
			if (obj.hasClass('clear')) {
				$(obj).remove();
				break;
			}
		}		
		 
		$('.headerDistanceFix').removeClass('headerDistanceFix');
	}
}
checkBreadcrump();