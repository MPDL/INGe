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
function addItemListFunctions(){
    $pb('.itemList').each(function(i, ele){
        $pb(ele).find('.mediumView').each(function(j, elem){
            $pb(elem).hide();
        });
        $pb(ele).find('.collapse').each(function(j, elem){
            $pb(elem).hide();
        });
        $pb(ele).find('.expand').each(function(j, elem){
            $pb(elem).show();
        });
        $pb(ele).find('.collapseTriangle').each(function(j, elem){
            $pb(elem).hide();
        });
        $pb(ele).find('.expandTriangle').each(function(j, elem){
            $pb(elem).show();
        });
        $pb(ele).find('.listItem').hover(function(){
            $pb(this).addClass('listBackground');
        }, function(){
            $pb(this).removeClass('listBackground');
        });
    });
// Openration of the select menu for checkboxes 
// Start with event on document to close the select menu on click elswhere    
    $pb('html').click(function(){
//        $pb('.selectMenu').hide();
    });
    
    $pb('.selectMenu').click(function(evt){
    	evt.preventDefault();
    	evt.stopPropagation();
    	evt.stopImmediatePropagation();
    });
    
    function hideElement(element) {
    	element.hide(100);
    }
    
    $pb('.checkBoxSelectButton').click(function(evt){
    	evt.preventDefault();
    	evt.stopPropagation();
    	evt.stopImmediatePropagation();
    	
    	$pb('body').unbind("click");
    	$pb('body').unbind("keydown");
    	
    	var cbsButtonPosition = $pb(this).position();
    	
    	var slctMenu = $pb(this).siblings('.selectMenu');
    	$pb('body').one("click", function(evt) {
    		hideElement(slctMenu);
    	});
    	$pb('body').one('keydown', function(evt){
			if (Number(evt.which) === 27) {	//check the key-number for number of escape
				hideElement(slctMenu);
			}
		});
    	slctMenu.toggle(100, function(){
    		if ($pb(slctMenu).is(':visible')) {
    			$pb(slctMenu).css("left", cbsButtonPosition.left + 10);
//    			$pb(slctMenu).css("top", cbsButtonPosition.top - 2);
        	}
    	});
    	
//    	$pb(this).siblings('.selectMenu').toggle(100);
    });
    
// Select options    
    var tog = '';
    $pb('.listHeader').find('.allCheckBox').click(function(){
    	$pb('.itemList').find("input[type=checkbox]").attr("checked", !tog);
    	tog = !tog;
    });
//	$pb(this).parents('.selectMenu').hide();
    hideElement($pb(this).parents('.selectMenu'));

    $pb('.listHeader').find('.selectAll').click(function(){
        $pb('.itemList').find('input[type=checkbox]').attr('checked', true);
    });
//	$pb(this).parents('.selectMenu').hide();
    hideElement($pb(this).parents('.selectMenu'));

    $pb('.selectMenu').find('.toggleAll').click(function(){
        $pb('.listItem').find('input[type=checkbox]').click();
    });
//	$pb(this).parents('.selectMenu').hide();
    hideElement($pb(this).parents('.selectMenu'));

    $pb('.selectMenu').find('.selectNone').click(function(){
        $pb(this).parents('.itemList').find('.itemCheckBox').attr('checked', false);
    });
//	$pb(this).parents('.selectMenu').hide();
    hideElement($pb(this).parents('.selectMenu'));
    
    $pb('.selectMenu').find('a').each(function(i, elem){
        $pb(elem).click(function(){
//            $pb(this).parents('.selectMenu').hide()
        	hideElement($pb(this).parents('.selectMenu'));
        });
    });
    
    $pb('.headerSwitchView').find('.expandTriangle').click(function(){
        $pb(this).hide();
        $pb(this).siblings('.collapseTriangle').show();
        $pb(this).parents('.itemList').find('.listItem').find('.expandTriangle:visible').each(function(i, elem){
            $pb(elem).trigger('click');
        });
    });
    
    $pb('.headerSwitchView').find('.collapseTriangle').click(function(){
        $pb(this).hide();
        $pb(this).siblings('.expandTriangle').show();
        $pb(this).parents('.itemList').find('.listItem').find('.collapseTriangle:visible').each(function(i, elem){
            $pb(elem).trigger('click');
        });
    });
    
    $pb('.shortView').find('.expandTriangle').each(function(i, ele){
        $pb(ele).click(function(){
            $pb(this).hide();
            $pb(this).siblings('.collapseTriangle').show();
            var parentElement = $pb(this).parents('.listItem');
            $pb(parentElement).children('.mediumView').slideToggle('normal', function(){
                if (($pb(parentElement).find('.itemHeader').find('.expandTriangle:visible').length) ==
                0) {
                    $pb(parentElement).find('.headerSwitchView').find('.expandTriangle').hide();
                    $pb(parentElement).find('.headerSwitchView').find('.collapseTriangle').show();
                }
            });
        })
    });
    
    $pb('.shortView').find('.collapseTriangle').each(function(i, ele){
        $pb(ele).click(function(){
            $pb(this).hide();
            $pb(this).siblings('.expandTriangle').show();
            var parentElement = $pb(this).parents('.listItem');
            $pb(parentElement).children('.mediumView').slideToggle('normal', function(){
                if (($pb(parentElement).find('.itemHeader').find('.collapseTriangle:visible').length) ==
                0) {
                    $pb(parentElement).find('.headerSwitchView').find('.expandTriangle').show();
                    $pb(parentElement).find('.headerSwitchView').find('.collapseTriangle').hide();
                }
            });
        })
    });
    
}

function installItemList(){
    /* ADD LISTENERS TO CHANGED DOM */
    addItemListFunctions();
}












