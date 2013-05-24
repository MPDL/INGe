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

if(typeof cookieVersion=='undefined') {
	var cookieVersion = "1.1";
}
if(typeof jsURL=='undefined') {
	var jsURL = './resources/commonJavaScript/';
}
if(typeof coneURL=='undefined') {
	var coneURL = '../../cone/';
}
var hiddenThemesEnabled = false;

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
			if(unescape(dc.substring(start,stop)) == 'true') {enableHiddenShemes = true; hiddenThemesEnabled = true;};
		}
	}
	var isCorrectCookieVersion = false;
	cookie = "cVersion=";
	if (dc.length > 0) {
		var start = dc.indexOf(cookie);
		if (start != -1) {
			start += cookie.length;
			var stop = dc.indexOf(";", start);
			if (stop == -1) stop = dc.length;
			if(unescape(dc.substring(start,stop)) == cookieVersion) {isCorrectCookieVersion = true;};
		}
	}
	
	var el = null;
	
	if (cookieValue != "" && isCorrectCookieVersion && document.getElementsByTagName && document.getElementById(cookieValue)) {
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
	var cookieValue = "";
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
		if(hiddenThemesEnabled) {
			document.cookie = "layout=" + escape(cookieValue) + ";" +
								"cVersion=" + cookieVersion + ";" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
			document.cookie = "cVersion=" + cookieVersion + ";" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
			document.cookie = "enableHiddenSchemes=true;" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
		} else {
			document.cookie = "layout=" + escape(cookieValue) + ";" +
								"cVersion=" + cookieVersion + ";" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
			document.cookie = "cVersion=" + cookieVersion + ";" +
								"expires=" + exp.toGMTString() + ";" +
								"path=/";
		}
	}
}

var included = false;

 /*INCLUDES EXTERNAL JAVASCRIPT TO PAGE DOM*/
function include_dom(script_filename) {
    var html_doc = document.getElementsByTagName('head').item(0);
    var js = document.createElement('script');
    js.setAttribute('language', 'javascript');
    js.setAttribute('charset', 'UTF-8');
    js.setAttribute('type', 'text/javascript');
    js.setAttribute('src', script_filename);
    html_doc.appendChild(js);
    return false;
}

/*ADDS MULTIPLE EVENTS TO A EVENTLISTENER*/
function addEvent(obj, evType, fn){
 if (obj.addEventListener){
   obj.addEventListener(evType, fn, false);
   return true;
 } else if (obj.attachEvent){
   var r = obj.attachEvent("on"+evType, fn);
   return r;
 } else {
   return false;
 }
}

/*START ALL EXTERNAL JAVASCRIPTS*/
function install_javascripts() {
	if (typeof installExtPaginator == 'function') {
		installExtPaginator();
	}
	if (typeof installItemList == 'function') {
		installItemList();
	}
	if (typeof installItemList == 'function') {
		installFullItem();
	}
	if (typeof installQuickSearchShortcut == 'function') {
		installQuickSearchShortcut();
	}
	if (typeof installDateTextbox == 'function') {
		installDateTextbox();
	}
	if (typeof installSameHeight == 'function') {
		installSameHeight();
	}
	if (typeof bindSuggests == 'function') {
		bindSuggests();
	}
}

/*INCLUDES EXTERNAL JAVASCRIPTS*/

function include_javascripts() {
	
		if(!included){
			//console.log("inclide js $ " + $().jquery)
			//console.log("inclide js jquery " + jQuery.fn.jquery)
			include_dom(jsURL + 'componentJavaScript/eSciDoc_ext_paginator.js');
			include_dom(jsURL + 'componentJavaScript/eSciDoc_selectbox.js');
			include_dom(jsURL + 'componentJavaScript/eSciDoc_item_list.js');
			include_dom(jsURL + 'componentJavaScript/eSciDoc_full_item.js');
			include_dom(jsURL + 'componentJavaScript/eSciDoc_single_elements.js');
			include_dom(coneURL + 'js/jquery.suggest.js')
			include_dom(jsURL + 'componentJavaScript/autoSuggestFunctions.js');
			include_dom(jsURL + 'componentJavaScript/breadcrump.js');
			/*REITERATION NEEDED TO START ALL INCLUDED JAVASCRIPTS*/
			included = true;
			include_javascripts();
		} else {
			addEvent(window, 'load', function(){window.setTimeout('install_javascripts()', 1);});
		}
}




if (!(location.pathname.match(/faces\/help\//))) {
	include_javascripts();
}
applyCookieStyle();
window.onunload=function(e){setStyleCookie();};





function fullItemReloadAjax()
{
	var fi = $pb('#fullItem');
	var fil = $pb('#ImgFullItemLoad');
	if ((fi && fi.length > 0) && (fil && fil.length > 0))
	{
		if (!$pb.browser.msie) {
			fi.css('opacity','0.4');
		}
		fil.attr('class','big_imgArea half_marginLIncl smallThrobber');
		if ($pb.browser.msie && $pb.browser.version == 7) {
			fil.css("left", "25%");
		}
		fi.find("input[type='text'], textarea").attr("readonly", "true");
	}
}
function fullItemReloadStop()
{
	var fi = $pb('#fullItem');
	var fil = $pb('#ImgFullItemLoad');
	if ((fi && fi.length > 0) && (fil && fil.length > 0))
	{
		if (!$pb.browser.msie) {
			fi.css('opacity','1.0');
		}
		fil.attr('class','noDisplay');
		fi.find("input[type='text'], textarea").removeAttr("readonly");
	}
}

/*This method is called by the a4j:status element in Header.jspf before every Richfaces Ajax Call */
function beforeAjaxRequest()
{
	
	if(typeof window.fullItemReloadAjax == 'function')
	{ 
		
		fullItemReloadAjax();
	}
}

/*This method is called by the a4j:status element in Header.jspf after every Richfaces Ajax Call */
function afterAjaxRequest()
{
	
	if(typeof window.fullItemReloadStop == 'function')
	{ 
		fullItemReloadStop();
	}
	install_javascripts();
	
	resizeSelectbox(431);
	updateSelectionBox(null, true);
	
	if(typeof window.updatePersonUi == 'function')
	{ 
		updatePersonUi();
	}
}

/*Stops the enter key, otherwise everytime the enter key is pressed in an textfield, the quicksearchbutton is activated  */
function stopRKey(evt) {
	var evt = (evt) ? evt : ((event) ? event : null);
	var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
	if ((evt.keyCode == 13) && (node.type=="text" || node.type=="password"))  {return false;}
}

document.onkeypress = stopRKey;


//Appends the creative commons license chooser to the given div
function appendLicenseBox(divToAppend, currentLicenseUrl)
{
	 //empty each ccContent
	 $pb.each($pb('.ccContent'), function(index, value) {$pb(this).empty();});
	 $pb(divToAppend).addClass('big_imgArea smallThrobber');
	 
	 var hiddenInput = document.createElement( 'input' );
	 hiddenInput.type = 'hidden';
	 hiddenInput.id = 'cc_js_seed_uri';
	 hiddenInput.value = currentLicenseUrl;
	 $pb(divToAppend)[0].appendChild(hiddenInput);
	 
	 var locale = $pb('.header .metaMenu .selectionBox').text();
	 if (locale.match(/deu/gi)) {
		 locale = 'de_DE';
	 } else {
		 locale = 'en_US';
	 }
	 
	 var url= "http://api.creativecommons.org/jswidget/tags/0.96/complete.js?want_a_license=definitely&locale=" + locale;
	 var id = $(divToAppend).attr('id');			     
	 
	 var ccScript = document.createElement( 'script' );
	 ccScript.type = 'text/javascript';
	 ccScript.src = url;
	 $pb(divToAppend)[0].appendChild(ccScript);
	 
	 //IE
	 if ($pb.browser.msie) {
		 ccScript.onreadystatechange = function () {
			 if (ccScript.readyState == 'loaded') {
				 //init does not work in IE...
				 //cc_js_pageInit();
				 $pb(divToAppend).removeClass('big_imgArea smallThrobber');
				 cc_js_pageInit();
			 }
		 }
	 } else { //FF & Co.
		 ccScript.onload = function () {
			 cc_js_pageInit();
			 $pb(divToAppend).removeClass('big_imgArea smallThrobber');
		 }
	 }
	 
	 ccScript.onerror = function () {
		 //fullItemReloadStop();
	 }
}