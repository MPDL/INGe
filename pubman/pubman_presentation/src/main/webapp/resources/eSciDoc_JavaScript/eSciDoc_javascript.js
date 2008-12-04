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
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

var included = false;

 /*INCLUDES EXTERNAL JAVASCRIPT TO PAGE DOM*/
function include_dom(script_filename) {
    var html_doc = document.getElementsByTagName('head').item(0);
    var js = document.createElement('script');
    js.setAttribute('language', 'javascript');
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
	installExtPaginator();
	installItemList();
	installFullItem();
	installQuickSearchShortcut();
	installDateTextbox();
	installSelectReplacement();
	bindSuggests();
}

/*INCLUDES EXTERNAL JAVASCRIPTS*/
function include_javascripts() {
	if(!included){
		include_dom('./resources/eSciDoc_JavaScript/jquery/jquery-1.2.6.min.js');
		include_dom('./resources/eSciDoc_JavaScript/eSciDoc_component_JavaScript/eSciDoc_ext_paginator.js');
		include_dom('./resources/eSciDoc_JavaScript/eSciDoc_component_JavaScript/eSciDoc_item_list.js');
		include_dom('./resources/eSciDoc_JavaScript/eSciDoc_component_JavaScript/eSciDoc_full_item.js');
		include_dom('./resources/eSciDoc_JavaScript/eSciDoc_component_JavaScript/eSciDoc_single_elements.js');
		include_dom('../../cone/js/jquery.suggest.js');
		include_dom('./resources/eSciDoc_JavaScript/autoSuggestFunctions.js');
		/*REITERATION NEEDED TO START ALL INCLUDED JAVASCRIPTS*/
		included = true;
		include_javascripts();
	} else {
			addEvent(window, 'load', install_javascripts);
		}
}

include_javascripts();
