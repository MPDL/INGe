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
 * or http://www.escidoc.org/license.
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

/*ADDS MULTIPLE EVENTS TO A EVENTLISTENER*/
function addEvent(obj, evType, fn) {
    if (obj.addEventListener) {
        obj.addEventListener(evType, fn, false);
        return true;
    } else if (obj.attachEvent) {
        var r = obj.attachEvent("on" + evType, fn);
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
    if (typeof installFullItem == 'function') {
        installFullItem();
    }
    /*
    if (typeof installQuickSearchShortcut == 'function') {
    	installQuickSearchShortcut();
    }
	
    if (typeof installDateTextbox == 'function') {
    	installDateTextbox();
    }
    */
    if (typeof installSameHeight == 'function') {
        installSameHeight();
    }
    if (typeof bindSuggests == 'function') {
        bindSuggests();
    }
}

// append a hidden field to preload the throbber image because of load error in webkit engine
$(function() {
    $('body').append('<input type="hidden" class="smallThrobber"/>');
});

function fullItemReloadAjax() {
    var overlayDiv;
    overlayDiv = $('#overlayAjaxRequest');
    if (!overlayDiv || overlayDiv.length == 0) {
        overlayDiv = $('<div id="overlayAjaxRequestParent" ><div id="overlayAjaxRequest" class="overlayAjaxRequestBackground"></div>' +
            '<div class="big_imgArea smallThrobber">&#160;</div></div>');
        $('body').append(overlayDiv);
    }
}

function fullItemReloadStop() {
    $('#overlayAjaxRequestParent').remove();
}

/*This method is called by jsf before every Richfaces Ajax Call */
function beforeAjaxRequest() {
    //console.log("Before Ajax!!");
    if (typeof window.fullItemReloadAjax == 'function') {
        fullItemReloadAjax();
    }
}

/*This method is called by jsf after every Richfaces Ajax Call */
function afterAjaxRequest() {
    //console.log("After Ajax!!");
    //Remove old autosuggest result lists
    $('ul.ac_results').remove();
    if (typeof window.fullItemReloadStop == 'function') {
        fullItemReloadStop();
    }
    install_javascripts();

    resizeSelectbox(431);

    updateSelectionBox(null, true);

    if (typeof window.updatePersonUi == 'function') {
        updatePersonUi();
    }
}


if (!window["busystatus"]) {
    var busystatus = {};
}

busystatus.onStatusChange = function onStatusChange(data) {
    var status = data.status;

    //alert("ajax event triggered")
    if (status === "begin") { // turn on busy indicator
        beforeAjaxRequest();

    } else if (status === "success") {
        // turn off busy indicator, on "success"
        afterAjaxRequest();
    }
};

jsf.ajax.addOnEvent(busystatus.onStatusChange);

/*Stops the enter key, otherwise everytime the enter key is pressed in an textfield, the quicksearchbutton is activated  */
function stopRKey(evt) {
    var evt = (evt) ? evt : ((event) ? event : null);
    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
    if ((evt.keyCode == 13) && (node.type == "text" || node.type == "password")) {
        return false;
    }
}

//Appends the creative commons license chooser to the given div
function appendLicenseBox(divToAppend, currentLicenseUrl) {
    //empty each ccContent
    $.each($('.ccContent'), function(index, value) {
        $(this).empty();
    });
    $(divToAppend).addClass('big_imgArea smallThrobber');

    var hiddenInput = document.createElement('input');
    hiddenInput.type = 'hidden';
    hiddenInput.id = 'cc_js_seed_uri';
    hiddenInput.value = currentLicenseUrl;
    $(divToAppend)[0].appendChild(hiddenInput);

    var locale = $('.header .metaMenu .selectionBox').text();
    if (locale.match(/deu/gi)) {
        locale = 'de_DE';
    } else {
        locale = 'en_US';
    }

    var url = "https://api.creativecommons.org/jswidget/tags/0.96/complete.js?want_a_license=definitely&locale=" + locale;
    var id = $(divToAppend).attr('id');

    var ccScript = document.createElement('script');
    ccScript.type = 'text/javascript';
    ccScript.src = url;
    $(divToAppend)[0].appendChild(ccScript);

    //IE
    /*
	 if ($.browser.msie) {
		 ccScript.onreadystatechange = function () {
			 if (ccScript.readyState == 'loaded') {
				 $(divToAppend).removeClass('big_imgArea smallThrobber');
				 cc_js_pageInit();
			 }
		 }
	 } else { //FF & Co.
		*/
    ccScript.onload = function() {
        cc_js_pageInit();
        $(divToAppend).removeClass('big_imgArea smallThrobber');
    }
    //}

    ccScript.onerror = function() {
        //fullItemReloadStop();
    }
}