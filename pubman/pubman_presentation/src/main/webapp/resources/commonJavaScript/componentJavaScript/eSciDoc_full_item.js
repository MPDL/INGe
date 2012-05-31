/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

function addFullItemFunctions() {
	$pb('.itemBlock').each( function(i,ele){
		$pb(ele).find('.collapse').each(function(j,elem){
			$pb(elem).show();
		});
		$pb(ele).find('.expand').each(function(j,elem){
			$pb(elem).hide();
		});  
		$pb(ele).not('.visibility').find('.blockHeader').each(function(j,elem){
			if($pb(elem).siblings('.itemBlockContent').length==0)$pb(elem).addClass('voidBlock');
		});
	});
	$pb('.fullItem').find('.visibility').find('.collapse').click(function(){
		$pb(this).hide(); 
		$pb(this).parents('.itemBlock').find('.expand').show(); 
		$pb(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.collapse:visible').trigger('click');
	});
	$pb('.fullItem').find('.visibility').find('.expand').click(function(){
		$pb(this).hide(); 
		$pb(this).parents('.itemBlock').find('.collapse').show(); 
		$pb(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.expand:visible').trigger('click');
	}); 
	$pb('.itemBlock:not(.visibility)').find('.expand').each(function(i,ele){
		$pb(ele).click(function(){
			$pb(this).hide(); 
			$pb(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').hide(); 
			$pb(this).parents('.itemBlock').children('.itemBlockContent').slideToggle('normal', function(){
				$pb(this).parents('.itemBlock').find('.collapse').show();  
				$pb(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').show(); 
				if(($pb(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.expand:visible').length)==0) { 
					$pb(this).parents('.fullItem').find('.visibility').find('.collapse').show(); 
					$pb(this).parents('.fullItem').find('.visibility').find('.expand').hide();
				} 
			});
		})
	});
	$pb('.itemBlock:not(.visibility)').find('.collapse').each(function(i,ele){
		$pb(ele).click(function(){
			$pb(this).hide(); 
			$pb(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').hide(); 
			$pb(this).parents('.itemBlock').children('.itemBlockContent').slideToggle('normal', function(){
				$pb(this).parents('.itemBlock').find('.expand').show(); 
				if(($pb(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.collapse:visible').length)==0) { 
					$pb(this).parents('.fullItem').find('.visibility').find('.collapse').hide(); 
					$pb(this).parents('.fullItem').find('.visibility').find('.expand').show();
				} 
			});
		})
	});
	$pb('.hideBlockIfVoid').each(function(i,elem){	//function is in use for advanced search 
		if( allInputsBelowVoid(elem) && ($pb(elem).find('.itemLine').length < 3 ) ) { 
			$pb(elem).siblings('.expand').show(); 
			$pb(elem).find('.collapse').hide(); 
			$pb(elem).hide();  
		} 
	});
	$pb('.hideAdvSearchGenreBlockIfVoid').each(function(i,elem){ //function is in use for advanced search
		if( allInputsBelowVoid(elem) && ($pb(elem).find('.itemLine').length < 5 ) ) { 
			$pb(elem).siblings('.expand').show(); 
			$pb(elem).find('.collapse').hide(); 
			$pb(elem).hide();  
		};
	});
	$pb('.hideAdvSearchComplexBlockIfVoid').each(function(i,elem){ //function is in use for advanced search
		if( allInputsBelowVoid(elem) && ($pb(elem).find('.itemLine').length < 11 ) ) { 
			$pb(elem).siblings('.expand').show(); 
			$pb(elem).find('.collapse').hide(); 
			$pb(elem).hide();  
		};   
	});
	
	$pb('.creator').each(function(i,ele){
		$pb(ele).hover(function(){
			$pb(this).addClass('affHover');
			var numbers = $pb(this).children('sup').text().split(',');
			for(var z=0; z<numbers.length; z++) {
				$pb(this).parents('.itemBlockContent').find('.affiliation').each(function(j, elem){
					if(jQuery.trim($pb(elem).prev().text())==jQuery.trim(numbers[z])){
						$pb(elem).addClass('affHover');
					}
				});
			}
		}, function(){
			$pb(this).removeClass('affHover');
			var numbers = $pb(this).children('sup').text().split(',');
			for(var z=0; z<numbers.length; z++) {
				$pb(this).parents('.itemBlockContent').find('.affiliation').each(function(j, elem){if(jQuery.trim($pb(elem).prev().text())==jQuery.trim(numbers[z])){$pb(elem).removeClass('affHover');}});
			}
		})
	});
	$pb('.affiliation').each(function(i,ele){$pb(ele).hover(function(){
		$pb(this).addClass('affHover');
		var number = $pb(this).prev().text();
		$pb(this).parents('.itemBlockContent').find('.creator').each(function(j, elem){
				var numbers = $pb(elem).children('sup').text().split(',');
				for(var z=0; z<numbers.length; z++) {
					if(jQuery.trim(number)==jQuery.trim(numbers[z])){
						$pb(elem).addClass('affHover');
					}
				}
			});
	}, function(){
		$pb(this).removeClass('affHover');
		var number = $pb(this).prev().text();
		$pb(this).parents('.itemBlockContent').find('.creator').each(function(j, elem){
				var numbers = $pb(elem).children('sup').text().split(',');
				for(var z=0; z<numbers.length; z++) {
					if(jQuery.trim(number)==jQuery.trim(numbers[z])){
						$pb(elem).removeClass('affHover');
					}
				}
			});
	} )});
	
	$pb('.fullItem').find('.shortView').each(function(i,ele){$pb(ele).hide();});
	$pb('.fullItem').find('.itemInfoSwitch').each(function(i,ele){$pb(ele).click(function(){$pb(this).parents('.listItem').find('.shortView').slideToggle('normal'); });});

//	$pb('.fileUploadBtn').each(function(i, elem){ if($pb(elem).parents('.fileSection').find('.fileInput').val() == ''){ $pb(elem).parents('.fileSection').find('.fileUploadBtn').attr('disabled','disabled');}; });

	$pb('.showMultipleAuthors').click(function(){
		$pb(this).parents('.itemBlock').find('.multipleAuthors').slideDown('normal');
		$pb(this).parents('.itemBlock').find('.firstCreator').removeClass('noTopBorder');
		$pb(this).parents('.itemBlock').find('.multipleAuthors').find(':hidden').val('showPermanent');
		$pb(this).hide();
	});
	$pb('.multipleAuthors').hide();
	$pb('.showMultipleAuthors').each(function(i,elem){ 
		if($pb(elem).parents('.itemBlock').find("input[type='hidden'][value='showPermanent']").length > 0) {
			$pb(elem).hide(); $pb(elem).parents('.itemBlock').find('.multipleAuthors').show(); 
			$pb(elem).parents('.itemBlock').find('.firstCreator').removeClass('noTopBorder'); 
		}; 
	});
	
	$pb('.checkAll').click(function() { // function is used in logged out status for advanced search
		$pb(this).parents('.itemLine').find('.checkboxDoubleGroup').find(':checkbox').attr('checked','true'); 
		$pb(this).parents('.itemLine').find('.checkboxDoubleGroup').find('span:hidden').show(); 
		$pb(this).parents('.itemLine').find('.checkboxDoubleGroup').find('.showMoreCheckboxes').hide(); 
	});
	$pb('.showMoreCheckboxes').click(function() { 
		$pb(this).hide();
		var cont = $pb(this).parent().find('.checkboxDoubleContainer');
		//if a container for checkboxDoubleGroup given the children gets visible status 
		if (cont.length > 0) {
			cont.show();
			cont.children().show();
		} else { // otherwise all following node will be set visible
			$pb(this).siblings().show();
		} 
	});
	$pb('.checkboxDoubleGroup').each(function(i,elem){
		if($pb(elem).find('.large_checkbox:gt(0)').find(':checked').length == 0) {
			$pb(elem).find('.large_checkbox:gt(0)').hide();
		} else {
//			$pb(elem).find('.showMoreCheckboxes').hide();
		};
	});
	
	$pb('.showMoreDates').click(function(){ $pb(this).hide(); $pb(this).siblings().show(); });
	/* not sure if large_area0 is needed anymore - should be deprecated with xLarge_area0*/
	$pb('.datesGroup').each(function(i,elem){if($pb(elem).find('span.large_area0:gt(0)').find(":text[value!='']").length == 0) {$pb(elem).find('span.large_area0:gt(0)').hide();} else {$pb(elem).find('.showMoreDates').hide();};});
	$pb('.datesGroup').each(function(i,elem){if($pb(elem).find('span.xLarge_area0:gt(0)').find(":text[value!='']").length == 0) {$pb(elem).find('span.xLarge_area0:gt(0)').hide();} else {$pb(elem).find('.showMoreDates').hide();};});
	
	$pb('.showMoreAuthors').click(function(){ $pb(this).hide(); $pb(this).siblings().show(); });
	$pb('.authorsGroup').each(function(i,elem){if($pb(elem).find('span.creatorHidden').find(":text[value!='']").length == 0) {$pb(elem).find('span.creatorHidden').hide();} else {$pb(elem).find('.showMoreAuthors').hide();};});

}

function allInputsBelowVoid(topLevelElement) {
	return ( ($pb(topLevelElement).find(':checkbox:checked').length == 0) && 
			 ($pb(topLevelElement).find("textarea[value!=''], :text[value!='']").length == 0) && 
			 ($pb(topLevelElement).find('.languageSuggest').siblings("select[value!='']").length == 0) &&
			 ($pb(topLevelElement).find('.languageSuggest').siblings('span.replace').find("input:hidden[value!='']").length == 0)
			)
}

function installFullItem() {
	/*ADD LISTENERS TO CHANGED DOM*/
	addFullItemFunctions();
}