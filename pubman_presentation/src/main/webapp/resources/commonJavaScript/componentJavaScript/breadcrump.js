/**
 * @author schlender
 */


/* 
 * these fix exist because of double embedded breadcrump in different pages (e.g. simple submission, easy import submission, ...)
 */
function checkBreadcrump() {
	
	if ($pb('.clear .breadcrumb')[1]) {
		$pb('.clear .breadcrumb')[1].innerHTML = $pb('.clear .breadcrumb')[0].innerHTML;
		var obj = $pb($pb('.clear .breadcrumb')[0]);
		
		for (var i =0; i < 20; i++) {
			obj = obj.parent();
			if (obj.hasClass('clear')) {
				$pb(obj).remove();
				break;
			}
		}
		
		$pb('.headerDistanceFix').removeClass('headerDistanceFix');
	}
}

$pb('#content').ready(function(e){
	checkBreadcrump();
});