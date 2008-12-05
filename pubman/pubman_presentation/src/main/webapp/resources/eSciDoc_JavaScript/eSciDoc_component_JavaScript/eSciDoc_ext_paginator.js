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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

function rebuildRangeSelectorDOM() {
/*	$('.rangeSelector').find('select').each(function(i, ele){
		var classNameString = $(ele).attr("class");
		var lengthValue;
		var possibleLengthValues = classNameString.split(' ');
		var otherClasses = '';
		for(var i=0; i<possibleLengthValues.length; i++) {
			if(possibleLengthValues[i].match('_select')) {
				var wholeLengthValue = possibleLengthValues[i].split('_');
				lengthValue = wholeLengthValue[0];
			} else {
				if(possibleLengthValues[i].match('replace')){}
				else {otherClasses = otherClasses+possibleLengthValues[i]+' '};
			}
		};
		var replacementString = '<span class="large_area0 designedRangeSelector'+otherClasses+'">';
			if($(ele).find('option[selected]').length==0){
				replacementString = replacementString+'<span class="large_area1_p7 replaceLabel">'+$(ele).find('option').text()+' '+$(ele).siblings('.hitsLabel').text()+'</span>';
				replacementString = replacementString+'<input type="hidden" id="'+$(ele).attr('id')+'" name="'+$(ele).attr('name')+'" class="hiddenInput" value="'+$(ele).find('option').val()+'" onchange="'+ele.getAttribute("onchange")+'" />';
			}
			else{
				replacementString = replacementString+'<span class="large_area1_p7 replaceLabel">'+$(ele).find('option[selected]').text()+' '+$(ele).siblings('.hitsLabel').text()+'</span>';
				replacementString = replacementString+'<input type="hidden" id="'+$(ele).attr('id')+'" name="'+$(ele).attr('name')+'" class="hiddenInput" value="'+$(ele).find('option[selected]').val()+'"';
				if(ele.getAttribute("onchange")!=null) replacementString = replacementString+' onchange="'+ele.getAttribute("onchange")+'"';
				replacementString = replacementString+' />';
			}
			replacementString = replacementString+'<span class="large_area0_p8 openArea"><input type="button" class="min_imgBtn open" /></span><span class="large_area1 pulldown">';
			$(ele).find('option').each(function(j, elem){
				replacementString = replacementString+'<a class="large_area0_p7 selectLine';
				if($(elem).text()==$(ele).find('option[selected]').text()) replacementString = replacementString+' actual';
				if(j==0) {replacementString = replacementString+'" name="'+$(elem).val()+'">'+$(elem).text()+' '+$(ele).siblings('.hitsLabel').text()+'</a>';}
				else {replacementString = replacementString+'" name="'+$(elem).val()+'">'+$(elem).text()+'</a>';};
				if(j==0) {
					replacementString = replacementString+'<input type="button" class="min_imgBtn close endline"/>';
				}
			})
		replacementString = replacementString+'</span></span>'
		replacementString = replacementString+'<span class="'+lengthValue+'_area0">&nbsp;</span>';
		$(ele).siblings('.hitsLabel').remove();
*/		$('.paginatorFallbackGoBtn').addClass('noDisplay');
/*		$(ele).replaceWith(replacementString);
	});
*/}

function addPaginatorFunctions() {
/*	$('.rangeSelector').find('.open').each(function(i,ele){$(ele).click(function(){ $(this).parents('.rangeSelector').find('.pulldown').show(); })});
	$('.rangeSelector').find('.close').each(function(i,ele){$(ele).click(function(){ $(this).parents('.rangeSelector').find('.pulldown').hide(); })});
	$('.rangeSelector').find('.selectLine').each(function(i,ele){$(ele).click(function(){ $(this).parents('.rangeSelector').find('input[type=hidden]').val($(this).attr('name')); $(this).parents('.rangeSelector').find('.replaceLabel').text($(this).text()+' '); $(this).parents('.pulldown').find('.actual').removeClass('actual'); $(this).addClass('actual'); $(this).parents('.pulldown').hide(); $(this).parents('.rangeSelector').find('input[type=hidden]').trigger('change'); $('form').submit();  })});
*/	$('.gotoBox').find(':text').keydown(function(event){ switch (event.keyCode) { case 13: $(this).parents('.gotoBox').find('.paginatorFallbackGoBtn').click(); break;   }});
}





function rebuildRangeSelectorDOMOld() {
	if (!document.getElementsByTagName) return false;
	var possibleRangeSelectors = document.getElementsByTagName("select");
	for(var i=0; i < possibleRangeSelectors.length; i++) {
		if(possibleRangeSelectors[i].parentNode.className.match("rangeSelector")) {
			/*GET LABEL*/
			var label;
			var possibleLabels = document.getElementsByTagName("label");
			for(var j=0; j < possibleLabels.length; j++) {
				if(possibleLabels[j].htmlFor == possibleRangeSelectors[i].id) label = possibleLabels[j];
			}
			/*GENERATE SELECT AREA
			*
			* This code generates the following HTML code
			*
			*<div class="large_area0 headline endline">
			* 	<input type="text" class="xSmall_txtInput numberLabel" value="GIVEN VALUE HERE" onchange="GIVEN EVENT HERE" readOnly="true"/>
			*	GIVEN LABEL HERE
			*	<input type="button" class="min_imgBtn open">
			*</div>
			*
			*/
			var selectArea = document.createElement("div");
				selectArea.className = 'large_area0 headline endline';
					var selectedValueInput = document.createElement("input");
						selectedValueInput.id = possibleRangeSelectors[i].id;
						var onChangeListenerValue = possibleRangeSelectors[i].getAttribute("onchange");
						selectedValueInput.setAttribute('onchange', onChangeListenerValue);
						selectedValueInput.setAttribute('type','text');
						selectedValueInput.setAttribute('readOnly', 'true');
						selectedValueInput.className = 'xSmall_txtInput numberLabel';
						selectedValueInput.value = possibleRangeSelectors[i].options[possibleRangeSelectors[i].selectedIndex].text;
					var selectAreaLabel = label.cloneNode(true);
					var openButton = document.createElement("input");
						openButton.setAttribute('type','button');
						openButton.className = 'min_imgBtn open';
				selectArea.appendChild(selectedValueInput);
				selectArea.appendChild(selectAreaLabel);
				selectArea.appendChild(openButton);
			/*GENERATE MENU AREA
			*
			* This code generates the following HTML code
			*
			*<div class="large_area0 large_negMarginLExcl menu endline noDisplay">
			*	AND FOR EVERY LINE IN THE PULLDOWN:
			*	<div class="large_area1_p3 line">
			*		<input type="radio" name="fallBack" class="noDisplay" />
			*		<label class="tiny_label numberLabel">NUMBER IN THE LINE</label>
			*		AND ONLY FOR THE FIRST LINE IT APPENDS THE GIVEN LABEL AND
			*			<input type="button" class="min_imgBtn close" />
			*	</div>
			*</div>
			*
			*/
			var menuArea = document.createElement("div");
				menuArea.className = 'large_area0 large_negMarginLExcl menu endline noDisplay';
					for(var k=0; k<possibleRangeSelectors[i].options.length; k++) {
						var line = document.createElement("div");
							line.className = 'large_area1_p3 line';
							if(possibleRangeSelectors[i].options[k].text == possibleRangeSelectors[i].options[possibleRangeSelectors[i].selectedIndex].text) line.className += ' actual';
								var fallBackRadio = document.createElement("input");
									fallBackRadio.setAttribute('type','radio');
									fallBackRadio.setAttribute('name','fallBack'+possibleRangeSelectors[i].id);
									if(possibleRangeSelectors[i].options[k].text == possibleRangeSelectors[i].options[possibleRangeSelectors[i].selectedIndex].text) fallBackRadio.setAttribute('checked','true');;
									fallBackRadio.className='noDisplay';
							line.appendChild(fallBackRadio);
								var numberLabel = document.createElement("label");
									numberLabel.className = 'tiny_label numberLabel';
									numberLabel.appendChild(document.createTextNode(possibleRangeSelectors[i].options[k].text));
							line.appendChild(numberLabel);
							if(k==0) {
								line.appendChild(label.cloneNode(true));
								var closeButton = document.createElement("input");
									closeButton.setAttribute('type','button');
									closeButton.className = 'min_imgBtn close';
								line.appendChild(closeButton);
							}
						menuArea.appendChild(line);
					}
					
			/*REPLACE SELECT
			*
			*This replaces the given selectBox by the generated code
			*
			*/
			var selectsParentNode = possibleRangeSelectors[i].parentNode;
			selectsParentNode.replaceChild(selectArea, possibleRangeSelectors[i]);
			selectsParentNode.replaceChild(menuArea, label);
			/*One item is removed, so set back the counter i*/
			i--;
		}
	}
	/*HIDE ALL UNNEEDED FALLBACK GO BUTTONS
	*
	*This removes all fallback go buttons which are only needed if no javascript is supported
	*
	*/
	var possibleRangeSelectorGoButtons = document.getElementsByTagName("input");
	for(var i=0; i < possibleRangeSelectorGoButtons.length; i++) {
		if(possibleRangeSelectorGoButtons[i].className.match("paginatorFallbackGoBtn")) {
			possibleRangeSelectorGoButtons[i].parentNode.removeChild(possibleRangeSelectorGoButtons[i]);
			/*One item is removed, so set back the counter i*/
			i--;
		}
	}
}

function addPaginatorFunctionsOld() {
	if (!document.getElementsByTagName) return false;
	var possibleOpenButtons = document.getElementsByTagName("input");
	for (var i=0; i < possibleOpenButtons.length; i++) {
		/*FIND open BUTTONS AND ASSIGN OPEN MENU FUNCTION TO ITS ONCLICK*/
		if( (possibleOpenButtons[i].className.match("open")) && (possibleOpenButtons[i].parentNode.parentNode.className.match("rangeSelector")) ) {
			possibleOpenButtons[i].onclick = function(event) {
				/*GET MENU TO SHOW*/
				var possibleMenus = this.parentNode.parentNode.childNodes;
				for(var j=0; j<possibleMenus.length; j++)
				{
					if(possibleMenus[j].nodeType == 1) {
						/*SHOW MENU*/
						if(possibleMenus[j].className.match("menu")) possibleMenus[j].className = possibleMenus[j].className.replace(/noDisplay/,'');
					}
				}
			}
		}
		/*FIND close BUTTONS AND ASSIGN CLOSE MENU FUNCTION TO ITS ONCLICK*/
		if( (possibleOpenButtons[i].className.match("close")) && (possibleOpenButtons[i].parentNode.parentNode.parentNode.className.match("rangeSelector")) ) {
			possibleOpenButtons[i].onclick = function(event) {
				/*HIDE MENU*/
				this.parentNode.parentNode.className += ' noDisplay';
				/*STOP EVENT BUBBLING*/
				var evt = event || window.event;
				if(evt.stopPropagation) evt.stopPropagation();
				evt.cancelBubble = true;
			}
		}
	}
	var possibleMenuLines = document.getElementsByTagName("div");
	for (var i=0; i < possibleMenuLines.length; i++) {
		/*FIND ALL line ELEMENTS IN THE menu AND ASSIGN SELECT VALUE TO ITS ONCLICK*/
		if( (possibleMenuLines[i].className.match("line")) && (possibleMenuLines[i].parentNode.parentNode.className.match("rangeSelector")) ) {
			possibleMenuLines[i].onclick = function(event) {
				/*REMOVE actual-SELECTION FROM ALL MENU LINES*/
				var allLinesToDeleteActual = this.parentNode.childNodes;
				for(var j=0; j<allLinesToDeleteActual.length; j++)
				{
					if(allLinesToDeleteActual[j].nodeType == 1) {
						if(allLinesToDeleteActual[j].className.match("line")) allLinesToDeleteActual[j].className = allLinesToDeleteActual[j].className.replace(/actual/,'');
					}
				}
				var possibleRangeSelectorDefault = this.parentNode.parentNode.childNodes;
				for(var j=0; j<possibleRangeSelectorDefault.length; j++)
				{
					/*FIND THE TEXTINPUT FIELD WITH ACTUALLY SELECTED VALUE*/
					if(possibleRangeSelectorDefault[j].nodeType == 1) {
						if(!(possibleRangeSelectorDefault[j].className.match("menu"))) {
							var possibleRangeSelectionInput = possibleRangeSelectorDefault[j].childNodes;
							for(var k=0; k<possibleRangeSelectionInput.length; k++)
							{
								if(possibleRangeSelectionInput[k].nodeType == 1) {
									if((possibleRangeSelectionInput[k].className.match("numberLabel")) && (possibleRangeSelectionInput[k].nodeName == 'INPUT')) {
										var possibleOwnRange = this.childNodes;
										for(var l=0; l<possibleOwnRange.length; l++)
										{
											/*FIND VALUE OF THE SELECTED RANGE*/
											if(possibleOwnRange[l].nodeType == 1) {
												if(possibleOwnRange[l].className.match("numberLabel")) {
													/*ASSIGN VALUE OF SELECTED RANGE TO TEXTINPUT FIELD WITH ACTUALLY SELECTED VALUE*/
													possibleRangeSelectionInput[k].value = possibleOwnRange[l].firstChild.nodeValue;
													/*FIRE ONCHANGE EVENT (NOT FIRED BY JS-CHANGE)*/
													possibleRangeSelectionInput[k].onchange();
													/*SET line OF SELECTED VALUE AS ACTUAL*/
													this.className += ' actual';
													/*HIDE MENU*/
													this.parentNode.className += ' noDisplay';
												}
											}
										}
									}  
								}
							}
						}
					}
				}
			}
		}
	}
}

function installExtPaginator() {
	/*CHANGE DOM*/
	rebuildRangeSelectorDOM();
	/*ADD LISTENERS TO CHANGED DOM*/
	addPaginatorFunctions();
}