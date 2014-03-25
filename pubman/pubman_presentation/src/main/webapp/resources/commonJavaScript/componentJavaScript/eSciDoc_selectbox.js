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
	$pb('.selectContainer').each(function(i, box) {
		//check if the selectbox is on metaMenu
		if ($pb(box).parent().hasClass("metaMenu")) {
//			console.log('Parent: ' + $pb(box).parent().attr('class'))
		} else {
			var element = $pb(box);
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


/*
 * the function update the text into selectionBox for viewing the selected option in selectbox
 * @parameter box = selectbox object
 * --- only for testing at the moment, maybe it would be neccessary in detail submission
 */
function updateSelectionBox(box, isStart) {
	/* the start param is different in workflow for selectionBox values 
	 * if it's true: all selectboxes will be read and update their selectionBox
	 * if it's false: only the current selectbox will be read and update their selectionBox
	 */
	var tooltip = null;
	var selText = null;
	var selCont = null;
	var selVal = null;
	
	if (isStart) 
	{	//if start or ajax request every selectbox will be focused to update the selectionBox
		var sb = $pb('select');
		sb.each(function(i, slb)
		{
			selCont = null;
			selText = null;
			tooltip = null;
			selVal = null;
			
			selCont = $pb(slb).parents(".selectContainer");
			if (selCont && selCont.length > 0) {
				selVal = slb.options[slb.selectedIndex].text;
				$pb(slb).find("option").each(function(i, opt)
				{
					if ($pb(opt).val() == selVal) 
					{
						selVal = $pb(opt).text();
					}
				});
				
				selText = selCont.find(".selectionBox");
				if (selText && selText.length > 0)
				{
					selText.html(selVal);
				}
				
				tooltip = selCont.find(".tooltip");
				if (tooltip && tooltip.length > 0)
				{
					tooltip.html(selVal);
					tooltip.css("min-width", selText.width()-10);
				}
			}
		});
	} else {
		var contentText = box.options[box.selectedIndex].text;
		selCont = $pb(box).parent();
		selCont.find('.selectionBox').text(contentText);
		tooltip = selCont.find(".tooltip");
		if (tooltip && tooltip.length > 0)
		{
			tooltip.text(contentText);
			tooltip.css("min-width", selCont.find(".selectionBox").width()-10);
		}
		tooltip = null;
		
		/* at first: exclude all logical operation selectboxes */
		if ($pb.trim(contentText) != 'AND' && $pb.trim(contentText) != 'OR' && $pb.trim(contentText) != 'NOT') 
		{
			var parent = null; //define the parent object
			for (var ij = 0; ij < $pb(box).parents().length; ij++) 
			{
				if ($pb($pb(box).parents().get(ij)).hasClass('.itemBlock')) 
				{
					parent = $pb($pb(box).parents().get(ij));
					break;
				}
			}
			
			//compare the contentText and decide for remove hidden class
			if (parent) 
			{
				if ($pb.trim(contentText) != '-' && $pb.trim(contentText) != '--' && $pb.trim(contentText) != '') 
				{
					parent.find('.itemBlockContent').removeClass("hideBlockIfVoid");
					parent.find('.itemBlockContent').removeClass("hideAdvSearchComplexBlockIfVoid");
					/* parent.find('.itemBlockContent').removeClass("hideAdvSearchGenreBlockIfVoid"); -- should be expanded for text-input fields*/
				} 
			}
		}
	}
}


$pb(document).ready(function(e){
	resizeSelectbox(431);
	updateSelectionBox(null, true);
});

