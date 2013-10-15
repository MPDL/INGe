<%--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>


<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>CoNE - Control of Named Entities</title>
	<link href="<%= PropertyReader.getProperty("escidoc.common.presentation.url") %>resources/cssFramework/main.css" type="text/css" rel="stylesheet"/>
	<link href="<%= PropertyReader.getProperty("escidoc.common.stylesheet.contrast.url") %>" id="HighContrast" type="text/css" title="high contrast" rel="alternate stylesheet"/>
	<link href="<%= PropertyReader.getProperty("escidoc.common.stylesheet.classic.url") %>" id="Classic" type="text/css" title="classic" rel="alternate stylesheet"/>	
	<% if ("true".equals(PropertyReader.getProperty("escidoc.common.stylesheet.special.apply"))) { %><link href="<%= PropertyReader.getProperty("escidoc.common.stylesheet.special.url") %>" id="Special" type="text/css" title="special" rel="alternate stylesheet"/><% } %>
	<link href="<%= PropertyReader.getProperty("escidoc.common.stylesheet.standard.url") %>" id="Standard" type="text/css" title="blue" rel="stylesheet"/>

	
	<script language="JavaScript" type="text/javascript">
	
		  function applyCookieStyle() {
				var cookieValue = ""
				var cookie = "layout=";
				var dc = document.cookie;
				if (dc.length > 0) {
					var start = dc.indexOf(cookie);
					if (start != -1) {
						start += cookie.length;
						var stop = dc.indexOf(";", start);
						if (stop == -1) stop = dc.length;
						cookieValue = unescape(dc.substring(start,stop));
					}
				}
				var enableHiddenShemes = false;
				cookie = "enableHiddenSchemes=";
				if (dc.length > 0) {
					var start = dc.indexOf(cookie);
					if (start != -1) {
						start += cookie.length;
						var stop = dc.indexOf(";", start);
						if (stop == -1) stop = dc.length;
						if(unescape(dc.substring(start,stop)) == 'true') {enableHiddenShemes = true;};
					}
				}
				
				var el = null;
				
				if (cookieValue != "" && document.getElementsByTagName && document.getElementById(cookieValue)) {
					el = document.getElementsByTagName("link");
					for (var i = 0; i < el.length; i++ ) {
						if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id") == cookieValue && enableHiddenShemes && (el[i].getAttribute("title") == null || el[i].getAttribute("title") == "" ) ) {
							el[i].setAttribute("title", el[i].getAttribute("id"));
						}
						if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id")) {
							el[i].disabled = true;
							if (el[i].getAttribute("id") == cookieValue) el[i].disabled = false;
						}
					}
				} else if ( (!cookieValue || (cookieValue && !document.getElementById(cookieValue))) && document.getElementsByTagName ) {
					el = document.getElementsByTagName("link"); 
					for (var j = 0; j < el.length; j++ ) {
						if (el[j].id && el[j].rel == 'alternate stylesheet' && el[j].title && el[j].type == "text/css") {
							el[j].disabled = true;
						} else if (el[j].id && el[j].rel == 'stylesheet' && el[j].title && el[j].type == "text/css") {
							el[j].disabled = false;
						}
					}
				} 
				
				setStyleCookie();
			}
		
			function setStyleCookie() {
				var cookieValue = "Standard";
				if(document.getElementsByTagName) {
					var el = document.getElementsByTagName("link");
					for (var i = 0; i < el.length; i++ ) {
						var enabledCounter = 0;
						if (el[i].getAttribute("rel").indexOf("style") != -1 && el[i].getAttribute("id") && el[i].getAttribute("title") && el[i].disabled == false && enabledCounter == 0) {
							cookieValue = el[i].getAttribute("id");
							enabledCounter++;
						}
					}
				}
				var now = new Date();
				var exp = new Date(now.getTime() + (1000*60*60*24*30));
				if(cookieValue != "") {
					document.cookie = "layout=" + escape(cookieValue) + ";" +
										"expires=" + exp.toGMTString() + ";" +
										"path=/";
				}
			}
			applyCookieStyle();
			window.onunload=function(e){setStyleCookie();};
		</script>

	<script type="text/javascript">

		var instanceUrl = '<%= PropertyReader.getProperty("escidoc.cone.service.url") %>';

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
			}
			else
			{
				$(parent).find("input[type='text']").each(function(){ $(this).val('');});
				$(element).remove();
			}
			form.submit();
		}

		function add(element, predicate, hidden, lang)
		{
			var parent = $(element).parents('.itemLine:eq(0)');
			var singleItem = $(parent).find('.singleItem')[0];
			var lastItem = $(parent).find('.singleItem:last');

			var newItem = $(singleItem).clone().empty();
				newItem.append('<input name="'+ predicate +'" value="" type="hidden">');
				if (lang)
				{
					newItem.append('<input name="'+ predicate + '_lang'+'" value="" type="hidden">');
				}
				
			$(lastItem).after(newItem);

			element.form.submit();
			
		}

		function bindSuggest(element, model, cutId)
		{
			if (typeof pageLoaded != 'undefined' && pageLoaded)
			{
				//console.log("Bind suggest: " + element);
				//if(element = 'http://purl.org/eprint/terms/affiliatedInstitution')
				
				element = element.replace('|', '\\|');
				
				if (typeof cutId != 'undefined' && cutId)
				{
					$('input[name=' + element + ']').suggest("<%= PropertyReader.getProperty("escidoc.cone.service.url") %>" + model + "/query?lang=en&format=json", {onSelect: fillSmallId});
				}
				else
				{
					$('input[name=' + element + ']').suggest("<%= PropertyReader.getProperty("escidoc.cone.service.url") %>" + model + "/query?lang=en&format=json", {onSelect: fillId});
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
				//console.log("Bind external suggest: " + element + "url : " + url);
				
				//console.log("Element: " + $('input[name=element]').attr('class'));
				//console.log("http___purl_org_escidoc_metadata_terms_0_1_position_0_http___purl_org_eprint_terms_affiliatedInstitution");
				//if(element = 'http://purl.org/eprint/terms/affiliatedInstitution')
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


			//try to fill identifier, if given
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


			
			/*
			if($input.attr('name').length && /http___purl_org_escidoc_metadata_terms_0_1_position_\d+_http___purl_org_eprint_terms_affiliatedInstitution/.test($input.attr('name')) ) {
				var prefix = $input.attr('name').split('_http___purl_org_eprint_terms_affiliatedInstitution')[0];
				var $idInput = $('input[name=' + prefix + '_http___purl_org_dc_elements_1_1_identifier]');
				$idInput.val(this.resultID);
			}
			*/
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
				if (subject != '')
				{
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
										//xLarge_txtInput errorMessageArea endline
										//document.getElementById('idImage').src = 'img/taken.png';
										document.getElementById('cone_identifier').className = 'xLarge_txtInput errorMessageArea endline';
										document.getElementById('idInfo').style.visibility = 'visible';
										document.getElementById('idInfo').className = 'tiny_area0 tiny_marginRExcl inputInfoBox errorMessageArea';
										document.getElementById('idInfo').title = 'This entry already exists!';
									}
								}
								else
								{
									//xLarge_txtInput infoMessageArea endline
									//document.getElementById('idImage').src = 'img/new.png';
									document.getElementById('idInfo').style.visibility = 'visible';
									document.getElementById('idInfo').className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';
									document.getElementById('cone_identifier').className = 'xLarge_txtInput infoMessageArea endline';
									document.getElementById('idInfo').title = 'This content is unique';

								}
							}
					);
				}
				else
				{
					//document.getElementById('idImage').src = 'img/empty.png';
					document.getElementById('idInfo').style.visibility = 'hidden';
					document.getElementById('cone_identifier').className = 'xLarge_txtInput';
				}
			}
		}

		function checkFields()
		{
			//var fields = $.find('.checkImage');
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
				object = document.editform[formField][counter].value;
			}
			else
			{
				object = document.editform[formField].value;
			}

			//var image = $(element).parents('.inputField').find('.checkImage')[0];
			var input = $(element).parents('.inputField').find('.xLarge_txtInput')[0];
			var info = $(element).parents('.inputField').find('.inputInfoBox')[0];

			if (object != '')
			{

				var jsonUrl = instanceUrl + model + '/query?' + escape(predicate) + '="' + escape(object) + '"&format=json';

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

									

									input.className = 'xLarge_txtInput errorMessageArea endline';
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox errorMessageArea';
								//	image.src = 'img/taken.png';
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
								//	image.title = title;
									info.title = title;
								}
								else if (counter > 0)
								{
									input.className = 'xLarge_txtInput successMessageArea endline';
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox successMessageArea';
								//	image.src = 'img/hits.png';
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
								//	image.title = title;
									info.title = title;
								}
								else
								{
									input.className = 'xLarge_txtInput infoMessageArea endline';
									info.style.visibility = 'visible';
									info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';
									info.title = 'This content is unique';
								//	image.src = 'img/new.png';
								//	image.title = 'This content is unique';
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
								input.className = 'xLarge_txtInput infoMessageArea endline';
								info.style.visibility = 'visible';
								info.className = 'tiny_area0 tiny_marginRExcl inputInfoBox infoMessageArea';			
							//	image.src = 'img/new.png';
							//	image.title = jsonUrl + ' - ' + predicate + ' - ' + formField;
								info.title = jsonUrl + ' - ' + predicate + ' - ' + formField; 
							//	title = 'This field is unique';
								info.title = 'This content is unique';
							}
						}
				);
			}
			else
			{
				input.className ='xLarge_txtInput';
				info.style.visibility = 'hidden';
			//	image.src = 'img/empty.png';
			//	image.title = '';
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
	<script type="text/javascript" src="/cone/js/jquery-1.8.3.min.js">;</script>
	<script type="text/javascript" src="/cone/js/jquery.jdialog.min.js">;</script>
	<script type="text/javascript" src="/cone/js/jquery.dimensions.js">;</script>
	<script type="text/javascript" src="/cone/js/jquery.suggest.js">;</script>
	<link type="text/css" rel="stylesheet" href="/cone/js/jquery.suggest.css"/>
</head>