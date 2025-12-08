<%--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 f�r wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur F�rderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>

<%@ page import="de.mpg.mpdl.inge.util.PropertyReader"%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

	<title>CoNE - Control of Named Entities</title>

	<link href="/cone/css/main.css" type="text/css" rel="stylesheet"/>
	<link href="/cone/css/themes/skin_MPG/styles/theme.css" type="text/css" rel="stylesheet"/>

	<script type="text/javascript">

		var instanceUrl = '<%= PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) %>';

		function removeLine(element, hasPredicates)
		{
			var form = $(element.form);
			var parent = $(element).parents(".inputField")[0];
			var listSize = $(parent).parent().children(".inputField").length;
			if (listSize > 1 && !hasPredicates)
			{
				$(parent).remove();
			}
			else if (hasPredicates)
			{
				$(parent).find('input[class*="min_imgBtn groupBtn remove"]').remove();
				if ($(parent).find(".itemLine").size() > 0)
				{
					$(parent).find(".itemLine").remove();				
					$(parent).find('input[class*="small_txtInput"]').remove();
					$(parent).find('input[class="noDisplay"]').remove();
				}
				if(listSize>1)
				{
					reorderPredicateInputNames($(parent).parent());
				}
			}
			else
			{
				$(parent).find("input[type='text']").each(function(){ $(this).val('');});
				$(element).remove();
			}
			form.submit();
		}

		function add(element, predicate, hidden, lang, hasPredicates)
		{
			var parentInput =  $(element).parent();
			var newItem = $(parentInput).clone().empty();
				newItem.append('<input name="'+ predicate +'" value="" type="hidden">');
				if (lang)
				{
					newItem.append('<input name="'+ predicate + '_lang'+'" value="" type="hidden">');
				}
			$(parentInput).after(newItem);
			if(hasPredicates)
			{
				reorderPredicateInputNames($(parentInput).parent());
			}
			element.form.submit();
		}

		function reorderPredicateInputNames(parent)
		{
			var count = 0;
			$(parent).children(".inputField").each(
				function (index) {
					if($(this).find("input:text, input:hidden").size())
					{
						$(this).find("input:text").each(function (index) {
							if($(this).attr("name").indexOf("|") >= 0)
							{
								var name = $(this).attr("name").split("|");
								var predicatePos = name.length - 2;
								name[predicatePos] = name[predicatePos].substring(0, name[predicatePos].lastIndexOf('_')) + "_" + count;
								var newName = name.join("|");
								$(this).attr("name", newName);
							}
						});
						count++;
					}
				});
		}

		function bindSuggest(element, model, cutId)
		{
			if (typeof pageLoaded != 'undefined' && pageLoaded)
			{
				element = element.replace('|', '\\|');
				if (typeof cutId != 'undefined' && cutId)
				{
					$('input[name=' + element + ']').suggest("<%= PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) %>" + model + "/query?lang=*&format=json", {onSelect: fillSmallId});
				}
				else
				{
					$('input[name=' + element + ']').suggest("<%= PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) %>" + model + "/query?lang=*&format=json", {onSelect: fillId});
				}
			}
			else
			{
				setTimeout('bindSuggest(\'' + element + '\', \'' + model + '\', ' + (typeof cutId != 'undefined' && cutId) + ')', 100);
			}
		};


		function bindExternalSuggest(element, url)
		{
			if (typeof pageLoaded != 'undefined' && pageLoaded)
			{
				element = element.replace('|', '\\|');
				$('input[name=' + element + ']').suggest(url, {onSelect: fillExternalValue});
			}
			else
			{
				setTimeout('bindExternalSuggest(\'' + element + '\', \'' + url + '\')', 100);
			}
		};

		function fillExternalValue()
		{
			var $input = $(this);
			$input.val(this.resultValue);
			if(this.resultID)
			{
				var inputName = $input.attr('name');
				var lastPos = inputName.lastIndexOf('|');
				var prefix = '';
				if(lastPos!=-1)
				{
					prefix = inputName.substring(0, lastPos) + "|";
				}
				var identifierName = prefix + 'http___purl_org_dc_elements_1_1_identifier';
				var $idInput = $('input[name=' + identifierName.replace('|', '\\|') + ']');
				if($idInput.length)
				{
					$idInput.val(this.resultID);
				}
			}
		}

		function fillSmallId()
		{
			$(this).val(this.resultID.substring(this.resultID.lastIndexOf('/') + 1));
		}
		
		function fillId()
		{
			var id = this.resultID.replace(/^.+\/(.+\/resource\/.+)$/, '$1');
			$(this).val(id);
		}

		function checkId(model, conf)
		{
			var subject;
			if (document.editform["cone_identifier"] != null)
			{
				subject = document.editform["cone_identifier"].value;
			}
			else
			{
				subject = document.editform["uri"].value;
			}
			if (typeof predicate == 'undefined')
			{
				$('#cone_identifier').removeClass('errorMessageArea successMessageArea infoMessageArea endline');
				if (subject != '')
				{
					if (!subject.match(/^[^<>'\"&%]*$/)) {
						$('#cone_identifier').addClass('errorMessageArea endline');
						document.getElementById('idInfo').style.visibility = 'visible';
						document.getElementById('idInfo').className = 'tiny_area0 tiny_marginRExcl inputInfoBox errorMessageArea';
						document.getElementById('idInfo').title = 'The key is invalid. Please do not use special characters or umlauts.';
					} else {
						var subject_prefix = document.editform["cone_subject_prefix"].value;
						$.getJSON(
							instanceUrl + subject_prefix + subject + '?format=json'
							, function(data) {
								if (data.id != null)
								{
									if (conf && confirm('This entry already exists!\nDo you want to edit the existing entry?'))
									{
										location.href = data.id.replace(instanceUrl, instanceUrl + 'edit.jsp?model=' + model + '&uri=');
									}
									else
									{
										$('#cone_identifier').addClass('errorMessageArea endline');
										document.getElementById('idInfo').style.visibility = 'visible';
										document.getElementById('idInfo').className = 'tiny_area0 tiny_marginRExcl inputInfoBox errorMessageArea';
										document.getElementById('idInfo').title = 'This entry already exists!';
									}
								}
								else
								{
									document.getElementById('idInfo').style.visibility = 'visible';
									document.getElementById('idInfo').className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';
									$('#cone_identifier').addClass('infoMessageArea endline');
									document.getElementById('idInfo').title = 'This content is unique';
								}
							}
						);
					}
				}
				else
				{
					document.getElementById('idInfo').style.visibility = 'hidden';
				}
			}
		}

		function checkFields()
		{
			var fields = $.find('.inputInfoBox');
			$(fields).each(function(){
					this.init = false;
					this.onclick();
				}
			);
		}
		
		function checkField(element, model, predicate, formField, counter, popup, shouldBeUnique)
		{
			mx = tempX;
			my = tempY;
			if (typeof popup == 'undefined')
			{
				popup = false;
			}
			var subject = null;
			if (document.editform["cone_identifier"] != null)
			{
				subject = document.editform["cone_identifier"].value;
			}
			else if (document.editform["uri"] != null)
			{
				subject = document.editform["uri"].value;
			}
			var object;
			if (counter != null)
			{
				if(!(typeof document.editform[formField][counter] === "undefined"))
				{
					object = document.editform[formField][counter].value;
				}
				else
				{
					object = document.editform[formField].value;
				}
			}
			else
			{
				object = document.editform[formField].value;
			}
			var input = $(element).parents('.inputField').find('.huge_txtInput, .half_txtArea')[0];
			var info = $(element).parents('.inputField').find('.inputInfoBox')[0];
			$(input).removeClass('errorMessageArea successMessageArea infoMessageArea endline');
			if (object != '')
			{
				var jsonUrl = instanceUrl + model + '/query?' + encodeURIComponent(predicate) + '="' + encodeURIComponent(object) + '"&format=json';
				$.getJSON(
						jsonUrl
						, function(data)
						{
							if (data.length > 0)
							{
								var counter = 0;
								for (var i = 0; i < data.length; i++)
								{
									var entry = data[i];
									if (subject == null || entry.id != instanceUrl + subject)
									{
										counter++;
									}
									else
									{
										// I found myself
									}
								}
								if (counter > 0 && shouldBeUnique)
								{
									$(input).addClass('errorMessageArea endline');
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox errorMessageArea';
									var title;
									if (counter == 1)
									{
										title = 'another entry was';
									}
									else if (counter <=48) 
									{
										title = counter + ' other entries were';
									}
									else
									{
										title = 'many other entries were';
									}
									title = 'This field should usually be unique, but ' + title + ' found with the same content';
									info.title = title;
								}
								else if (counter > 0)
								{
									$(input).addClass('successMessageArea endline');
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox successMessageArea';
									var title;
									if (counter <=48) 
									{
										title = counter;
									}
									else
									{
										title = 'Many';
									}
									title += ' other entries were found with the same content';
									info.title = title;
								}
								else
								{
									$(input).addClass('infoMessageArea endline');
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';
									info.title = 'This content is unique';
								}
								if (counter > 0 && popup && element.init)
								{
									var html = '<ul class="dialog">\n';
									for (var i = 0; i < data.length; i++)
									{
										if (subject == null || data[i].id != instanceUrl + subject)
										{
											html += '<li><a href="' + data[i].id + '" target="_blank">' + data[i].value + '</a>\n';
											html += '<a href="edit.jsp?model=' + model + '&uri=' + data[i].id.replace(instanceUrl, '') + '">[edit]</a>\n';
											html += '<a target="_blank" href="edit.jsp?model=' + model + '&uri=' + data[i].id.replace(instanceUrl, '') + '">[new window]</a></li>\n';
										}
									}
									html += '</ul>\n';
									showDialog(html, element);
								}
								else
								{
									element.init = true;
								}
							}
							else
							{
								$(input).addClass('infoMessageArea endline');
								info.style.visibility = 'visible';
								info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';			
								info.title = jsonUrl + ' - ' + predicate + ' - ' + formField; 
								info.title = 'This content is unique';
							}
						}
				);
			}
			else
			{
				info.style.visibility = 'hidden';
			}
		}

		function removeParent(element)
		{
			element.parentNode.parentNode.removeChild(element.parentNode);
		}

		function addListEntry(element, newTag, text)
		{
			var addEntry = element.parentNode.cloneNode(true);
			var newEntry = document.createElement(newTag);
			newEntry.innerHTML = text;
			var papa = element.parentNode.parentNode;
			removeParent(element);
			papa.appendChild(newEntry);
			papa.appendChild(addEntry);
		}

		function showDialog(html, element)
		{
			$('.messageArea').append(html);
			$('.messageArea').css('position', 'absolute');
			$('.messageArea').css('z-index', '2001');
			$('.messageArea').css('left', mx);
			$('.messageArea').css('top', my);
			$('.messageArea').removeClass('noDisplay');
			document.getElementById('fullItem').style.opacity='0.4';
			document.getElementById('fullItem').style.bg='FFF';
			$('*').attr('readonly', true);
			$(':input :file').attr('disabled', true);
		}

		function closeDialog()
		{
			$('.dialog').remove();
			$('.messageArea').addClass('noDisplay');
			
			document.getElementById('fullItem').style.opacity='1';
			document.getElementById('fullItem').style.bg='FFF';
			$('*').attr('readonly', false);
			$(':input :file').attr('disabled', false);
		}

		function readCslFile (evt) {
			var files = evt.target.files;
			var file = files[0];           
			var reader = new FileReader();
			reader.onload = function() {
				var xmlDoc = $.parseXML(this.result);
				var title = $(xmlDoc).xpath('/csl:style/csl:info/csl:title', xPathNamespace);
				var abbr = $(xmlDoc).xpath('/csl:style/csl:info/csl:title-short', xPathNamespace);
				$("[name='" + evt.data.txtArea + "']").val(this.result);   
				$("[name='http___purl_org_dc_elements_1_1_title']").val(title.text()).change();
				$("[name='http___purl_org_escidoc_metadata_terms_0_1_abbreviation']").val(abbr.text()).change();
			}
			reader.readAsText(file)
		}

		function xPathNamespace(prefix) {
			if (prefix == "csl")
				return "http://purl.org/net/xbiblio/csl";
		}
	</script>
	
	<script language="JavaScript1.2">
		<!--
		
		// Detect if the browser is IE or not.
		// If it is not IE, we assume that the browser is NS.
		var IE = document.all?true:false
		
		// If NS -- that is, !IE -- then set up for mouse capture
		if (!IE) document.captureEvents(Event.MOUSEMOVE)
		
		// Set-up to use getMouseXY function onMouseMove
		document.onmousemove = getMouseXY;
		
		// Temporary variables to hold mouse x-y pos.s
		var tempX = 0
		var tempY = 0
		var mx = 0
		var my = 0
		
		// Main function to retrieve mouse x-y pos.s
		
		function getMouseXY(e) {
		  if (IE) { // grab the x-y pos.s if browser is IE
		    tempX = event.clientX + document.body.scrollLeft
		    tempY = event.clientY + document.body.scrollTop
		  } else {  // grab the x-y pos.s if browser is NS
		    tempX = e.pageX
		    tempY = e.pageY
		  }  
		  // catch possible negative values in NS4
		  if (tempX < 0){tempX = 0}
		  if (tempY < 0){tempY = 0}  
		  // show the position values in the form named Show
		  // in the text fields named MouseX and MouseY
		
		  return true
		}
		
		//-->
	</script>
	
	<!--
	<script type="text/javascript" src="/cone/js/jquery-1.11.1.min.js"></script>
	  -->
	<script type="text/javascript" src="/cone/js/jquery-3.6.0.js"></script>
	<script type="text/javascript" src="/cone/js/jquery-migrate-3.3.2.js"></script>
	<script type="text/javascript" src="/cone/js/jquery.jdialog.min.js"></script>
	<script type="text/javascript" src="/cone/js/jquery.dimensions.js"></script>
	<script type="text/javascript" src="/cone/js/jquery.suggest.js"></script>
	<script type="text/javascript" src="/cone/js/jquery.xpath.min.js"></script>
	<link type="text/css" rel="stylesheet" href="/cone/js/jquery.suggest.css"/>
</head>