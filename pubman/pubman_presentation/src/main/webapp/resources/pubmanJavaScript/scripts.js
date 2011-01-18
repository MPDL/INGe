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

/*
* JavaScript functions for pubman_presentation
*/

// TODO: I am not sure withis "redirect" happens.
function deleteItems() 
{
	document.getElementById("form1:btnDeleteItems").click();
}

function changeItemState() 
{
	document.getElementById("form1:content:btnChangeItemState").click();
}

function changeQAWS() 
{
	document.getElementById("form1:content:btnChangeContext").click();
}

function changeImportSource() 
{
	document.getElementById("easySubmission:easySubmissionStep3Import:btnChangeImportSource").click();
}

function changeSortCriteria() 
{
	document.getElementById("form1:content:btnSortItemList").click();
}

function loadAbout(url) 
{
	// use the h:output tag to output the bean property
	openCenteredWindow(url, 1360, 1040, "About"); 
}

function loadBlog(url) 
{
	
	openCenteredWindow(url, 900, 500, "Blog");
}

function loadHelp(url, anchor)
{
	// use the h:output tag to output the bean property
	openCenteredWindow(url + anchor, 600, 400, "Help"); // don't use a windowName containing a blank space! -> http://developer.mozilla.org/en/docs/DOM:window.open
}

function loadDescription(html)
{
	
	//myWindow=window.open('','myWindow','width=600,height=400');
	//myWindow.document.open();
	//myWindow.document.write(html);
	//myWindow.document.close();
	//myWindow.focus();
	openCenteredWindowTest(html, 900, 400, "Description" );
}

function openCenteredWindow(page, width, height, windowName)
{
	var bw, bh, bl, bt, topPos, leftPos, attributes;
	
	bw = window.outerWidth;
	bh = window.outerHeight;
	bl = window.screenX;
	bt = window.screenY;
	
	leftPos = Math.floor((bw-width)/2) + bl;
	topPos = Math.floor((bh-height)/2) + bt;

	attributes = "width=" + width + ", height=" + height + ", top=" + topPos + ", left=" + leftPos + ", resizable=yes, scrollbars=yes";
	newWindow = window.open(page, windowName, attributes); // don't use a windowName containing a blank space! -> http://developer.mozilla.org/en/docs/DOM:window.open
	newWindow.focus();
}

function openCenteredWindowTest(html, width, height, windowName)
{
	var bw, bh, bl, bt, topPos, leftPos, attributes;
	
	bw = window.outerWidth;
	bh = window.outerHeight;
	bl = window.screenX;
	bt = window.screenY;
	
	leftPos = Math.floor((bw-width)/2) + bl;
	topPos = Math.floor((bh-height)/2) + bt;

	
	attributes = "width=" + width + ", height=" + height + ", top=" + topPos + ", left=" + leftPos + ", resizable=yes, scrollbars=yes";
	newWindow = window.open('', windowName, attributes); // don't use a windowName containing a blank space! -> http://developer.mozilla.org/en/docs/DOM:window.open
	newWindow.document.open();
	newWindow.document.write(html);
	
	var scriptElem = document.createElement('script');
        scriptElem.type = 'text/javascript';
        scriptElem.src = 'resources/scripts.js';

    var head = document.getElementsByName('head')[0];
    if( head ) head.appendChild( scriptElem );    

    var scriptElement = document.createElement( "script" );
    scriptElement.setAttribute( "src", "resources/scripts.js" );
    newWindow.document.body.appendChild( scriptElement );
	
	var cssElem = document.createElement('script');
        cssElem.type = 'text/css';
        cssElem.src = './resources/escidoc-css/css/main.css';
	
	 var head = document.getElementsByName('head')[1];
    if( head ) head.appendChild( cssElem );    

    var cssElement = document.createElement( "link" );
    cssElement.setAttribute( "href", "./resources/escidoc-css/css/main.css" );
    cssElement.setAttribute( "type", "text/css" );
    cssElement.setAttribute( "rel", "stylesheet" );
    newWindow.document.body.appendChild( cssElement );
    newWindow.document.close();
	newWindow.focus();
}

/** Ask the user if s/he really wants to delete an item.
*   @author franke
*/
/*
function confirmDelete(prefix)
{
	var answer = true;
	var message = document.getElementById(prefix + ':deleteMessage_1').innerHTML + ' ' + document.getElementById(prefix + ':deleteMessage_2_a').innerHTML + ' ' + document.getElementById(prefix + ':deleteMessage_3_a').innerHTML + '?';

	answer = confirm(message);

	return answer;

}
*/
/** Ask the user if s/he really wants to delete items from a list.
*   @author franke
*/
/*
function confirmListDelete(form)
{
	var answer = true;
	var checkedItems = document.getElementById('form1:' + form + ':noso').value;
	
	if (checkedItems != null && checkedItems > 0)
	{
		var message;
		if (checkedItems == 1)
		{
			message = document.getElementById('form1:' + form + ':deleteMessage_1').innerHTML + ' ' + document.getElementById('form1:' + form + ':deleteMessage_2_a').innerHTML + ' ' + document.getElementById('form1:' + form + ':deleteMessage_3_a').innerHTML + '?';
		}
		else
		{
			message = document.getElementById('form1:' + form + ':deleteMessage_1').innerHTML + ' ' + document.getElementById('form1:' + form + ':deleteMessage_2_b').innerHTML + ' ' + checkedItems + ' ' + document.getElementById('form1:' + form + ':deleteMessage_3_b').innerHTML + '?';
		}

		answer = confirm(message);
	}
	if (answer)
	{
		deleteItems();
	}

//	return answer;

}
*/
/** Triggered when an item is checked/unchecked to increase/decrease the count of checked items.
*   @author franke
*/
/*
function computeCheckedItems(element)
{
	var checked = element.checked;
	if (self.checkedItems == null)
	{
		self.checkedItems = 0;
	}
	if (checked)
	{
		checkedItems++;
	}
	else
	{
		checkedItems--;
	}

}
*/
/*
function showCollectionDescription()
{
	openCenteredWindow("editItem/contextDescription.jsp", 400, 200, "Description"); // // don't use a windowName containing a blank space! -> http://developer.mozilla.org/en/docs/DOM:window.open
}					
*/
/**
 * Allow only numbers, "-" and "v" and "c" (for copy and pasting with strg-c and strg-v) for dte fields.
 * @author Thomas Dieb�cker
 */
/*
function restrictDateEntry(e)
{							
	var key;
	var keychar;

	if (window.event)
	   key = window.event.keyCode;
	else if (e)
	   key = e.which;
	else
	   return true;
	keychar = String.fromCharCode(key);
	keychar = keychar.toLowerCase();
	
	// control keys
	if ((key==null) || (key==0) || (key==8) || 
		(key==9) || (key==13) || (key==27) )
	   return true;
	
	// alphas and numbers
	else if ((("-0123456789vc").indexOf(keychar) > -1))
	   return true;
	else
	   return false;
}
*/
function exportDataPopUp(w,h,site) 
{
	x=screen.availWidth/2-w/2;
	y=screen.availHeight/2-h/2;
	attributes = "width="+w+",height="+h+",left="+x+",top="+y+",screenX="+x+",screenY="+y+",resizable=yes, scrollbars=yes"	
	var popupWindow=window.open('','',attributes);
	popupWindow.document.write(site);
	popupWindow.document.close();	
	
}											
function updateAnyFieldMask()
{							
	document.getElementById("form1:AdvancedSearchEdit:btnAnyFieldUpdate").click();
}											
function updateExportFormats() 
{							
document.getElementById("form1:Export:btnUpdateExportFormats").click();
}											
function sortItemList () 
{
  document.getElementById("form1:SearchResultList:btnSortItemList").click();
}
/*
function downloadFile(FileID) 
{
  document.getElementById("form1:SearchResultList:"+FileID).click();
}
*/

/**
 * Clicks download file button (JSF BUG WORKAROUND) 
 * @author Tobias Schraut
 */
function downloadFileViewItem(element) 
{
	var button = element.previousSibling;
	button.click();
}

function selectExternalService(element) 
{
	document.getElementById(element).click();
}

function bookmark(url, title) {

	alert('URL:' + url + '\nTitle:' + title);

	if (window.sidebar) { // Mozilla Firefox
	  window.sidebar.addPanel(title, url, "");
	}
	else if (window.external) { // IE
	  window.external.AddFavorite( url, title);
	}
}

function orgInformationPopUp(w,h,site) 
{
	x=screen.availWidth/2-w/2;
	y=screen.availHeight/2-h/2;
	var popupWindow=window.open('','','width='+w+',height='+h+',left='+x+',top='+y+',screenX='+x+',screenY='+y);
	popupWindow.document.write(site);
	
	// FrM: Inserted to avoid Firefox trouble.
	popupWindow.document.close();
}
/*
function goBack() 
{
  document.getElementById("form1:btGoBack").click();
}

function startQuicksearch(e)
{
	var allInputs = document.getElementsByTagName('input');
	var buttonID = "";
	var fieldID = "";
	
	for(var i = 0; i < allInputs.length - 1; i++)
	{
		var inputElement = allInputs[i];
		var len = inputElement.id.length;
		var positionFID = eval(len - 9);
		var positionBID = eval(len - 8);
		if ( inputElement.id.substr( positionFID, len ) == "txtSearch" )
		{
			fieldID = inputElement.id;
		}
		if ( inputElement.id.substr( positionBID, len ) == "btSearch" )
		{
			buttonID = inputElement.id;
		}
	}

		if((e.keyCode == 13) && (document.getElementById(fieldID).value != "") && (buttonID != ""))
		{
				document.getElementById(buttonID).click();
				return false;
		}
}

function toggleErrorMessage()
{
	var button = document.getElementById('error-button');

	if (button.value == '+')
	{
		document.getElementById('error-message-div').style.height = 'auto';
		button.value = '-';
	}
	else
	{
		document.getElementById('error-message-div').style.height = '30px';
		button.value = '+';
	}
}
*/

function throb()
{
	var text = document.getElementById('selSelectedOrgUnit').options[1].text;
	if (text.indexOf('>--->--->--->--->--->--->--->---') >= 0)
	{
		document.getElementById('selSelectedOrgUnit').options[1].text = text.replace('>--->--->--->--->--->--->--->---', '->--->--->--->--->--->--->--->--');
	}
	else if (text.indexOf('->--->--->--->--->--->--->--->--') >= 0)
	{
		document.getElementById('selSelectedOrgUnit').options[1].text = text.replace('->--->--->--->--->--->--->--->--', '-->--->--->--->--->--->--->--->-');
	}
	else if (text.indexOf('-->--->--->--->--->--->--->--->-') >= 0)
	{
		document.getElementById('selSelectedOrgUnit').options[1].text = text.replace('-->--->--->--->--->--->--->--->-', '--->--->--->--->--->--->--->--->');
	}
	else if (text.indexOf('--->--->--->--->--->--->--->--->') >= 0)
	{
		document.getElementById('selSelectedOrgUnit').options[1].text = text.replace('--->--->--->--->--->--->--->--->', '>--->--->--->--->--->--->--->---');
	}
	else
	{
		return
	}
	setTimeout('throb()', 100);
}

function loadAffiliations(list)
{
	var element = document.getElementById('selSelectedOrgUnit');

	for (var i = 0; i < list.length - 1; i++)
	{
		element.options[i] = new Option(list[i].value, list[i].id);
	}
}
