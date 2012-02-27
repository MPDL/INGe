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
* Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
/**
 * @author Marco Schlender
 * @created 2011-08-19
 */


/*
 * the function search any selectbox on the page and resize them if it's no selectbox in meta menu
 * it activates if the document is ready (after loaded)
 * @required jQuery 1.3.1 or greater
 * @parameter maxWidth = maximum width for the selectboxes
 * @notice if the content is greater than the maximum width it would be cutted
 */
function resizeSelectbox(maxWidth) {
	$('.selectContainer').each(function() {
		//check if the selectbox is on metaMenu
		if ($(this).parent().hasClass("metaMenu")) {
//			console.log('Parent: ' + $(this).parent().attr('class'))
		} else {
			var element = $(this);
			//Define the current width of customized selectBox
			var curSelectWidth = element.find('select').width();
			
			//if the width of selectbox-width is smaller than the maxWidth  AND the selectContainer-width is greater than the selectbox-width -> it's allowed to resize
			if (curSelectWidth < maxWidth && element.width() > curSelectWidth) {
				//set the new width to the selectbox container
				element.find('div:first-child').width(curSelectWidth);
				//set the new width of selectionBox (info area) on the new width
				element.find('.selectionBox').width(curSelectWidth);
				//finally set the new width to the global element
				element.width(curSelectWidth);
			}
		}
	});
}

/* this function search for the parent node */
function searchParentTag(source_obj, searchTagString) {
	for (var i=0; i < $(source_obj).parents().length; i++) {
		if ($($(source_obj).parents()[i]).hasClass(searchTagString)) {
			return $($(source_obj).parents()[i]);
			break;
		}
	}
	return false;
}

/*
 * the function update the text into selectionBox for viewing the selected option in selectbox
 * @parameter box = selectbox object
 * --- only for testing at the moment, maybe it would be neccessary in detail submission
 */
function updateSelectionBox(box, isStart) {
	if (isStart) {	//if start every selectbox will be focused to update the selectionBox
		/*
		$('.selectContainer').each(function() {
			//check if the selectbox is on metaMenu
			if ($(this).parent().hasClass("metaMenu")) {
//				console.log('Parent: ' + $(this).parent().attr('class'))
			} else {
				$(this).find('select').focus();
			}
		});
		
	*/
		$("select").each(function(i){
			var parent = null;
			if (parent = searchParentTag(this, "selectContainer")) {
				var val = $(this).val();
				$(this).find("option").each(function(i){
					if ($(this).val() == val) {
						val = $(this).text();
					}
				});
				$(parent).find(".selectionBox").html(val);
				console.log(val);
			}
		});
	} else {
		var contentText = box.options[box.selectedIndex].text;
		
		$(box).parent().find('.selectionBox').text(contentText);
		
		
		/* at first: exclude all logical operation selectboxes */
		if ($.trim(contentText) != 'AND' && $.trim(contentText) != 'OR' && $.trim(contentText) != 'NOT') {
			
			var parent = null; //define the parent object
			for (var ij = 0; ij < $(box).parents().length; ij++) {
				if ($($(box).parents().get(ij)).hasClass('.itemBlock')) {
					parent = $($(box).parents().get(ij));
					break;
				}
			}
		
			//compare the contentText and decide for remove hidden class
			if (parent) {
				if ($.trim(contentText) != '-' && $.trim(contentText) != '--' && $.trim(contentText) != '') {
					parent.find('.itemBlockContent').removeClass("hideBlockIfVoid");
					parent.find('.itemBlockContent').removeClass("hideAdvSearchComplexBlockIfVoid");
					/* parent.find('.itemBlockContent').removeClass("hideAdvSearchGenreBlockIfVoid"); -- should be expanded for text-input fields*/
				} 
			}
			
		}
	}
	
}


$(document).ready(function(e){
	resizeSelectbox(431);
	updateSelectionBox(null, true)
});

