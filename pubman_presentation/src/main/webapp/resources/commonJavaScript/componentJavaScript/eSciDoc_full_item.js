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

function addFullItemFunctions() {
	$pb('.itemBlock').each( function(i,ele){
		if ($pb(this).find('.itemBlockContent:visible').length > 0) {
			$pb(this).find('.collapse').each(function(j,elem){
				$pb(elem).show();
			});
			$pb(this).find('.expand').each(function(j,elem){
				$pb(elem).hide();
			});
		} else {
			$pb(this).find('.collapse').each(function(j,elem){
				$pb(elem).hide();
			});
			$pb(this).find('.expand').each(function(j,elem){
				$pb(elem).show();
			});
		}
		$pb(ele).not('.visibility').find('.blockHeader').each(function(j,elem){
			if($pb(elem).siblings('.itemBlockContent').length==0)$pb(elem).addClass('voidBlock');
		});
		
	});
	
	$pb('.fullItem').find('.visibility').find('.collapse').unbind("click");
	$pb('.fullItem').find('.visibility').find('.collapse').click(function(){
		$pb(this).hide(); 
		$pb(this).parents('.itemBlock').find('.expand').show(); 
		$pb(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.collapse:visible').trigger('click');
	});
	
	$pb('.fullItem').find('.visibility').find('.expand').unbind("click");
	$pb('.fullItem').find('.visibility').find('.expand').click(function(){
		$pb(this).hide(); 
		$pb(this).parents('.itemBlock').find('.collapse').show(); 
		$pb(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.expand:visible').trigger('click');
	}); 
	$pb('.itemBlock:not(.visibility)').find('.expand').each(function(i,ele){
		$pb(ele).unbind("click");
		$pb(ele).click(function(){
			$pb(this).hide(); 
			$pb(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').hide(); 
			$pb(this).parents('.itemBlock').children('.itemBlockContent').slideToggle('normal', function(){
				$pb(this).parents('.itemBlock').find('.collapse').show();  
				$pb(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').show(); 
				if(($pb(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.expand:visible').length)==0) { 
					$pb(this).parents('.fullItem').find('.visibility').find('.collapse').show(); 
					$pb(this).parents('.fullItem').find('.visibility').find('.expand').hide();
				} 
			});
		})
	});
	$pb('.itemBlock:not(.visibility)').find('.collapse').each(function(i,ele){
		$pb(ele).unbind("click");
		$pb(ele).click(function(){
			$pb(this).hide(); 
			$pb(this).parents('.itemBlock').children('.itemBlockContent').children('.lineToolSection').hide(); 
			$pb(this).parents('.itemBlock').children('.itemBlockContent').slideToggle('normal', function(){
				$pb(this).parents('.itemBlock').find('.expand').show(); 
				if(($pb(this).parents('.fullItem').find('.itemBlock:not(.visibility)').find('.collapse:visible').length)==0) { 
					$pb(this).parents('.fullItem').find('.visibility').find('.collapse').hide(); 
					$pb(this).parents('.fullItem').find('.visibility').find('.expand').show();
				} 
			});
		})
	});
	$pb('.hideBlockIfVoid').each(function(i,elem){	//function is in use for advanced search 
		if( allInputsBelowVoid(elem) && ($pb(elem).find('.itemLine').length < 3 ) ) { 
			$pb(elem).siblings('.expand').show(); 
			$pb(elem).find('.collapse').hide(); 
			$pb(elem).hide();  
		} 
	});
	$pb('.hideAdvSearchGenreBlockIfVoid').each(function(i,elem){ //function is in use for advanced search
		if( allInputsBelowVoid(elem) && ($pb(elem).find('.itemLine').length < 5 ) ) { 
			$pb(elem).siblings('.expand').show(); 
			$pb(elem).find('.collapse').hide(); 
			$pb(elem).hide();  
		};
	});
	$pb('.hideAdvSearchComplexBlockIfVoid').each(function(i,elem){ //function is in use for advanced search
		if( allInputsBelowVoid(elem) && ($pb(elem).find('.itemLine').length < 11 ) ) { 
			$pb(elem).siblings('.expand').show(); 
			$pb(elem).find('.collapse').hide(); 
			$pb(elem).hide();  
		};   
	});
	
	$pb('.creator').each(function(i,ele){
		$pb(ele).hover(function(){
			$pb(this).addClass('affHover');
			var numbers = $pb(this).children('sup').text().split(',');
			for(var z=0; z<numbers.length; z++) {
				$pb(this).parents('.itemBlockContent').find('.affiliation').each(function(j, elem){
					if(jQuery.trim($pb(elem).prev().text())==jQuery.trim(numbers[z])){
						$pb(elem).addClass('affHover');
					}
				});
			}
		}, function(){
			$pb(this).removeClass('affHover');
			var numbers = $pb(this).children('sup').text().split(',');
			for(var z=0; z<numbers.length; z++) {
				$pb(this).parents('.itemBlockContent').find('.affiliation').each(function(j, elem){if(jQuery.trim($pb(elem).prev().text())==jQuery.trim(numbers[z])){$pb(elem).removeClass('affHover');}});
			}
		})
	});
	$pb('.affiliation').each(function(i,ele){$pb(ele).hover(function(){
		$pb(this).addClass('affHover');
		var number = $pb(this).prev().text();
		$pb(this).parents('.itemBlockContent').find('.creator').each(function(j, elem){
				var numbers = $pb(elem).children('sup').text().split(',');
				for(var z=0; z<numbers.length; z++) {
					if(jQuery.trim(number)==jQuery.trim(numbers[z])){
						$pb(elem).addClass('affHover');
					}
				}
			});
	}, function(){
		$pb(this).removeClass('affHover');
		var number = $pb(this).prev().text();
		$pb(this).parents('.itemBlockContent').find('.creator').each(function(j, elem){
				var numbers = $pb(elem).children('sup').text().split(',');
				for(var z=0; z<numbers.length; z++) {
					if(jQuery.trim(number)==jQuery.trim(numbers[z])){
						$pb(elem).removeClass('affHover');
					}
				}
			});
	} )});
	
	$pb('.fullItem').find('.shortView').each(function(i,ele){$pb(ele).hide();});
	$pb('.fullItem').find('.itemInfoSwitch').each(function(i,ele){$pb(ele).unbind("click"); $pb(ele).click(function(){$pb(this).parents('.listItem').find('.shortView').slideToggle('normal'); });});

//	$pb('.fileUploadBtn').each(function(i, elem){ if($pb(elem).parents('.fileSection').find('.fileInput').val() == ''){ $pb(elem).parents('.fileSection').find('.fileUploadBtn').attr('disabled','disabled');}; });
	$pb('.showMultipleAuthors').unbind("click");
	$pb('.showMultipleAuthors').click(function(){
		$pb(this).parents('.itemBlock').find('.multipleAuthors').slideDown('normal');
		$pb(this).parents('.itemBlock').find('.firstCreator').removeClass('noTopBorder');
		$pb(this).parents('.itemBlock').find('.multipleAuthors').find(':hidden').val('showPermanent');
		$pb(this).hide();
	});
	
	$pb('.multipleAuthors').hide();
	$pb('.showMultipleAuthors').each(function(i,elem){ 
		if($pb(elem).parents('.itemBlock').find("input[type='hidden'][value='showPermanent']").length > 0) {
			$pb(elem).hide(); $pb(elem).parents('.itemBlock').find('.multipleAuthors').show(); 
			$pb(elem).parents('.itemBlock').find('.firstCreator').removeClass('noTopBorder'); 
		}; 
	});
	
	$pb('.checkAll').unbind("click");
	$pb('.checkAll').click(function() { // function is used in logged out status for advanced search
		$pb(this).parents('.itemLine').find('.checkboxDoubleGroup').find(':checkbox').attr('checked','true'); 
		$pb(this).parents('.itemLine').find('.checkboxDoubleGroup').find('span:hidden').show(); 
		$pb(this).parents('.itemLine').find('.checkboxDoubleGroup').find('.showMoreCheckboxes').hide(); 
	});
	
	$pb('.showMoreCheckboxes').unbind("click");
	$pb('.showMoreCheckboxes').click(function() { 
		$pb(this).hide();
		var cont = $pb(this).parent().find('.checkboxDoubleContainer');
		//if a container for checkboxDoubleGroup given the children gets visible status 
		if (cont.length > 0) {
			cont.show();
			cont.children().show();
		} else { // otherwise all following node will be set visible
			$pb(this).siblings().show();
		} 
	});
	
	$pb('.checkboxDoubleGroup').each(function(i,elem){
		if($pb(elem).find('.large_checkbox:gt(0)').find(':checked').length == 0) {
			$pb(elem).find('.large_checkbox:gt(0)').hide();
		} else {
//			$pb(elem).find('.showMoreCheckboxes').hide();
		};
	});
	
	$pb('.showMoreDates').unbind("click");
	$pb('.showMoreDates').click(function(){ $pb(this).hide(); $pb(this).siblings().show(); });
	/* not sure if large_area0 is needed anymore - should be deprecated with xLarge_area0*/
	$pb('.datesGroup').each(function(i,elem){if($pb(elem).find('span.large_area0:gt(0)').find(":text[value!='']").length == 0) {$pb(elem).find('span.large_area0:gt(0)').hide();} else {$pb(elem).find('.showMoreDates').hide();};});
	$pb('.datesGroup').each(function(i,elem){if($pb(elem).find('span.xLarge_area0:gt(0)').find(":text[value!='']").length == 0) {$pb(elem).find('span.xLarge_area0:gt(0)').hide();} else {$pb(elem).find('.showMoreDates').hide();};});
	
	$pb('.showMoreAuthors').unbind("click");
	$pb('.showMoreAuthors').click(function(){ $pb(this).hide(); $pb(this).siblings().show(); });
	$pb('.authorsGroup').each(function(i,elem){if($pb(elem).find('span.creatorHidden').find(":text[value!='']").length == 0) {$pb(elem).find('span.creatorHidden').hide();} else {$pb(elem).find('.showMoreAuthors').hide();};});

}

function allInputsBelowVoid(topLevelElement) {
	return ( ($pb(topLevelElement).find(':checkbox:checked').length == 0) && 
			 ($pb(topLevelElement).find("textarea[value!=''], :text[value!='']").length == 0) && 
			 ($pb(topLevelElement).find('.languageSuggest').siblings("select[value!='']").length == 0) &&
			 ($pb(topLevelElement).find('.languageSuggest').siblings('span.replace').find("input:hidden[value!='']").length == 0)
			)
}

function installFullItem() {
	/*ADD LISTENERS TO CHANGED DOM*/
	addFullItemFunctions();
}

/*! nanoScrollerJS - v0.7.3 - (c) 2013 James Florentino; Licensed MIT */
!function(a,b,c){"use strict";var d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x;w={paneClass:"pane",sliderClass:"slider",contentClass:"content",iOSNativeScrolling:!1,preventPageScrolling:!1,disableResize:!1,alwaysVisible:!1,flashDelay:1500,sliderMinHeight:20,sliderMaxHeight:null,documentContext:null,windowContext:null},s="scrollbar",r="scroll",k="mousedown",l="mousemove",n="mousewheel",m="mouseup",q="resize",h="drag",u="up",p="panedown",f="DOMMouseScroll",g="down",v="wheel",i="keydown",j="keyup",t="touchmove",d="Microsoft Internet Explorer"===b.navigator.appName&&/msie 7./i.test(b.navigator.appVersion)&&b.ActiveXObject,e=null,x=function(){var a,b,d;return a=c.createElement("div"),b=a.style,b.position="absolute",b.width="100px",b.height="100px",b.overflow=r,b.top="-9999px",c.body.appendChild(a),d=a.offsetWidth-a.clientWidth,c.body.removeChild(a),d},o=function(){function i(d,f){this.el=d,this.options=f,e||(e=x()),this.$el=a(this.el),this.doc=a(this.options.documentContext||c),this.win=a(this.options.windowContext||b),this.$content=this.$el.children("."+f.contentClass),this.$content.attr("tabindex",this.options.tabIndex||0),this.content=this.$content[0],this.options.iOSNativeScrolling&&null!=this.el.style.WebkitOverflowScrolling?this.nativeScrolling():this.generate(),this.createEvents(),this.addEvents(),this.reset()}return i.prototype.preventScrolling=function(a,b){if(this.isActive)if(a.type===f)(b===g&&a.originalEvent.detail>0||b===u&&a.originalEvent.detail<0)&&a.preventDefault();else if(a.type===n){if(!a.originalEvent||!a.originalEvent.wheelDelta)return;(b===g&&a.originalEvent.wheelDelta<0||b===u&&a.originalEvent.wheelDelta>0)&&a.preventDefault()}},i.prototype.nativeScrolling=function(){this.$content.css({WebkitOverflowScrolling:"touch"}),this.iOSNativeScrolling=!0,this.isActive=!0},i.prototype.updateScrollValues=function(){var a;a=this.content,this.maxScrollTop=a.scrollHeight-a.clientHeight,this.prevScrollTop=this.contentScrollTop||0,this.contentScrollTop=a.scrollTop,this.iOSNativeScrolling||(this.maxSliderTop=this.paneHeight-this.sliderHeight,this.sliderTop=0===this.maxScrollTop?0:this.contentScrollTop*this.maxSliderTop/this.maxScrollTop)},i.prototype.createEvents=function(){var a=this;this.events={down:function(b){return a.isBeingDragged=!0,a.offsetY=b.pageY-a.slider.offset().top,a.pane.addClass("active"),a.doc.bind(l,a.events[h]).bind(m,a.events[u]),!1},drag:function(b){return a.sliderY=b.pageY-a.$el.offset().top-a.offsetY,a.scroll(),a.updateScrollValues(),a.contentScrollTop>=a.maxScrollTop&&a.prevScrollTop!==a.maxScrollTop?a.$el.trigger("scrollend"):0===a.contentScrollTop&&0!==a.prevScrollTop&&a.$el.trigger("scrolltop"),!1},up:function(){return a.isBeingDragged=!1,a.pane.removeClass("active"),a.doc.unbind(l,a.events[h]).unbind(m,a.events[u]),!1},resize:function(){a.reset()},panedown:function(b){return a.sliderY=(b.offsetY||b.originalEvent.layerY)-.5*a.sliderHeight,a.scroll(),a.events.down(b),!1},scroll:function(b){a.isBeingDragged||(a.updateScrollValues(),a.iOSNativeScrolling||(a.sliderY=a.sliderTop,a.slider.css({top:a.sliderTop})),null!=b&&(a.contentScrollTop>=a.maxScrollTop?(a.options.preventPageScrolling&&a.preventScrolling(b,g),a.prevScrollTop!==a.maxScrollTop&&a.$el.trigger("scrollend")):0===a.contentScrollTop&&(a.options.preventPageScrolling&&a.preventScrolling(b,u),0!==a.prevScrollTop&&a.$el.trigger("scrolltop"))))},wheel:function(b){var c;if(null!=b)return c=b.delta||b.wheelDelta||b.originalEvent&&b.originalEvent.wheelDelta||-b.detail||b.originalEvent&&-b.originalEvent.detail,c&&(a.sliderY+=-c/3),a.scroll(),!1}}},i.prototype.addEvents=function(){var a;this.removeEvents(),a=this.events,this.options.disableResize||this.win.bind(q,a[q]),this.iOSNativeScrolling||(this.slider.bind(k,a[g]),this.pane.bind(k,a[p]).bind(""+n+" "+f,a[v])),this.$content.bind(""+r+" "+n+" "+f+" "+t,a[r])},i.prototype.removeEvents=function(){var a;a=this.events,this.win.unbind(q,a[q]),this.iOSNativeScrolling||(this.slider.unbind(),this.pane.unbind()),this.$content.unbind(""+r+" "+n+" "+f+" "+t,a[r])},i.prototype.generate=function(){var a,b,c,d,f;return c=this.options,d=c.paneClass,f=c.sliderClass,a=c.contentClass,this.$el.find(""+d).length||this.$el.find(""+f).length||this.$el.append('<div class="'+d+'"><div class="'+f+'" /></div>'),this.pane=this.$el.children("."+d),this.slider=this.pane.find("."+f),e&&(b={paddingRight:(e)},this.$el.addClass("has-scrollbar")),null!=b&&this.$content.css(b),this},i.prototype.restore=function(){this.stopped=!1,this.pane.show(),this.addEvents()},i.prototype.reset=function(){var a,b,c,f,g,h,i,j,k,l;return this.iOSNativeScrolling?(this.contentHeight=this.content.scrollHeight,void 0):(this.$el.find("."+this.options.paneClass).length||this.generate().stop(),this.stopped&&this.restore(),a=this.content,c=a.style,f=c.overflowY,d&&this.$content.css({height:this.$content.height()}),b=a.scrollHeight+e,k=parseInt(this.$el.css("max-height"),10),k>0&&(this.$el.height(""),this.$el.height(a.scrollHeight>k?k:a.scrollHeight)),h=this.pane.outerHeight(!1),j=parseInt(this.pane.css("top"),10),g=parseInt(this.pane.css("bottom"),10),i=h+j+g,l=Math.round(i/b*i),l<this.options.sliderMinHeight?l=this.options.sliderMinHeight:null!=this.options.sliderMaxHeight&&l>this.options.sliderMaxHeight&&(l=this.options.sliderMaxHeight),f===r&&c.overflowX!==r&&(l+=e),this.maxSliderTop=i-l,this.contentHeight=b,this.paneHeight=h,this.paneOuterHeight=i,this.sliderHeight=l,this.slider.height(l),this.events.scroll(),this.pane.show(),this.isActive=!0,a.scrollHeight===a.clientHeight||this.pane.outerHeight(!0)>=a.scrollHeight&&f!==r?(this.pane.hide(),this.isActive=!1):this.el.clientHeight===a.scrollHeight&&f===r?this.slider.hide():this.slider.show(),this.pane.css({opacity:this.options.alwaysVisible?1:"",visibility:this.options.alwaysVisible?"visible":""}),this)},i.prototype.scroll=function(){return this.isActive?(this.sliderY=Math.max(0,this.sliderY),this.sliderY=Math.min(this.maxSliderTop,this.sliderY),this.$content.scrollTop(-1*((this.paneHeight-this.contentHeight+e)*this.sliderY/this.maxSliderTop)),this.iOSNativeScrolling||this.slider.css({top:this.sliderY}),this):void 0},i.prototype.scrollBottom=function(a){return this.isActive?(this.reset(),this.$content.scrollTop(this.contentHeight-this.$content.height()-a).trigger(n),this):void 0},i.prototype.scrollTop=function(a){return this.isActive?(this.reset(),this.$content.scrollTop(+a).trigger(n),this):void 0},i.prototype.scrollTo=function(b){return this.isActive?(this.reset(),this.scrollTop(a(b).get(0).offsetTop),this):void 0},i.prototype.stop=function(){return this.stopped=!0,this.removeEvents(),this.pane.hide(),this},i.prototype.destroy=function(){return this.stopped||this.stop(),this.pane.length&&this.pane.remove(),d&&this.$content.height(""),this.$content.removeAttr("tabindex"),this.$el.hasClass("has-scrollbar")&&(this.$el.removeClass("has-scrollbar"),this.$content.css({paddingRight:""})),this},i.prototype.flash=function(){var a=this;if(this.isActive)return this.reset(),this.pane.addClass("flashed"),setTimeout(function(){a.pane.removeClass("flashed")},this.options.flashDelay),this},i}(),a.fn.nanoScroller=function(b){return this.each(function(){var c,d;if((d=this.nanoscroller)||(c=a.extend({},w,b),this.nanoscroller=d=new o(this,c)),b&&"object"==typeof b){if(a.extend(d.options,b),b.scrollBottom)return d.scrollBottom(b.scrollBottom);if(b.scrollTop)return d.scrollTop(b.scrollTop);if(b.scrollTo)return d.scrollTo(b.scrollTo);if("bottom"===b.scroll)return d.scrollBottom(0);if("top"===b.scroll)return d.scrollTop(0);if(b.scroll&&b.scroll instanceof a)return d.scrollTo(b.scroll);if(b.stop)return d.stop();if(b.destroy)return d.destroy();if(b.flash)return d.flash()}return d.reset()})}}(jQuery,window,document);
/*
//@ sourceMappingURL=jquery.nanoscroller.min.js.map
*/