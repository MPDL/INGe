var BrowserDetect = {
	init: function () {
		this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
		this.version = this.searchVersion(navigator.userAgent)
			|| this.searchVersion(navigator.appVersion)
			|| "an unknown version";
		this.OS = this.searchString(this.dataOS) || "an unknown OS";
	},
	searchString: function (data) {
		for (var i=0;i<data.length;i++)	{
			var dataString = data[i].string;
			var dataProp = data[i].prop;
			this.versionSearchString = data[i].versionSearch || data[i].identity;
			if (dataString) {
				if (dataString.indexOf(data[i].subString) != -1)
					return data[i].identity;
			}
			else if (dataProp)
				return data[i].identity;
		}
	},
	searchVersion: function (dataString) {
		var index = dataString.indexOf(this.versionSearchString);
		if (index == -1) return;
		return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
	},
	dataBrowser: [
		{
			string: navigator.userAgent,
			subString: "Chrome",
			identity: "Chrome"
		},
		{ 	string: navigator.userAgent,
			subString: "OmniWeb",
			versionSearch: "OmniWeb/",
			identity: "OmniWeb"
		},
		{
			string: navigator.vendor,
			subString: "Apple",
			identity: "Safari",
			versionSearch: "Version"
		},
		{
			prop: window.opera,
			identity: "Opera"
		},
		{
			string: navigator.vendor,
			subString: "iCab",
			identity: "iCab"
		},
		{
			string: navigator.vendor,
			subString: "KDE",
			identity: "Konqueror"
		},
		{
			string: navigator.userAgent,
			subString: "Firefox",
			identity: "Firefox"
		},
		{
			string: navigator.vendor,
			subString: "Camino",
			identity: "Camino"
		},
		{		// for newer Netscapes (6+)
			string: navigator.userAgent,
			subString: "Netscape",
			identity: "Netscape"
		},
		{
			string: navigator.userAgent,
			subString: "MSIE",
			identity: "Explorer",
			versionSearch: "MSIE"
		},
		{
			string: navigator.userAgent,
			subString: "Gecko",
			identity: "Mozilla",
			versionSearch: "rv"
		},
		{ 		// for older Netscapes (4-)
			string: navigator.userAgent,
			subString: "Mozilla",
			identity: "Netscape",
			versionSearch: "Mozilla"
		}
	],
	dataOS : [
		{
			string: navigator.platform,
			subString: "Win",
			identity: "Windows"
		},
		{
			string: navigator.platform,
			subString: "Mac",
			identity: "Mac"
		},
		{
			string: navigator.platform,
			subString: "Linux",
			identity: "Linux"
		}
	]

};

BrowserDetect.init();

/*DATE INPUT FIELD*/

function validateDate(inputField) {
	/*DATE VALIDATION ACCORDING TO THE GREGORIAN CALENDAR*/
	var input_empty = "";
	var isValidDate = true;
	var isBC = false;
	var validChars = "0123456789-";
	var validNumbers = "0123456789";
	var bcString = "BC";
	var possibleDate = inputField.value;
	if((inputField.value=="")||(inputField.value==input_empty)) {
		$(inputField).val(input_empty).addClass("blankInput");
	}
	if(!(inputField.value == input_empty)) {
		/*REMOVE SPACES*/
		while(inputField.value.match(' -')) inputField.value = inputField.value.replace(/ -/, '-');
		while(inputField.value.match('- ')) inputField.value = inputField.value.replace(/- /, '-');
		/*REMOVE LEADING SPACES*/
		while(inputField.value.indexOf(' ')==0) inputField.value = inputField.value.substring(1,inputField.value.length);
		/*REMOVE SPACES AT THE END*/
		while(inputField.value.lastIndexOf(' ')==inputField.value.length-1) inputField.value = inputField.value.substring(0,inputField.value.length-1);
		while(inputField.value.match(' '+bcString)) inputField.value = inputField.value.replace(/ BC/, bcString);
		inputField.value = inputField.value.replace(bcString, ' '+bcString);
		possibleDate = inputField.value;
		/*CHECK FOR BC*/
		if( (possibleDate.indexOf(bcString))== (possibleDate.length-2)  ) {
			possibleDate = possibleDate.replace(' '+bcString, '');
			isBC= true;
		}
		/*VALIDATE DATE*/
		for (j = 0; j < possibleDate.length && isValidDate == true; j++) {
			Char = possibleDate.charAt(j); 
    		if (!(validChars.indexOf(Char) == -1)) 
    	     {
    	     	var subType = possibleDate.split(/-/);
    	     	if((subType.length < 4) && (subType.length > 0) && (possibleDate.lastIndexOf('-')<possibleDate.length-1) && (possibleDate.indexOf('-')!=0) ) {
    	     		for(var k=0; k < subType.length; k++) {
    	     			switch(k) {
    	     				/*FIRST NUMBER HAS NOT FOUR DIGITS*/
    	     				case 0:	if(subType[k].length!=4) isValidDate = false;
    	     						break;
    	     				/*SECOND NUMBER HAS NOT TWO DIGITS AND/OR IS LESS THAN 1 OR BIGGER THAN 12*/
    	     				case 1: if((subType[k].length!=2) || (subType[k]>12) || (subType[k]<1)) isValidDate = false;
        	 						break;
        	 				/*THIRD NUMBER HAS NOT TWO DIGITS AND/OR IS LESS THAN 1 OR BIGGER THAN 31*/
        	 				case 2: if((subType[k].length!=2) || (subType[k]>31) || (subType[k]<1)) isValidDate = false;
        	 						else {
        	 								/*APRIL, JUNE, SEPTEMBER AND NOVEMBER HAVE MORE THAN 30 DAYS*/
        	 								if(((subType[k-1]=='04') || (subType[k-1]=='06') || (subType[k-1]=='09') || (subType[k-1]=='11')) && (subType[k]>30)) isValidDate = false;
        	 								/*
        	 								*FEBRUARY HAS MORE THAN 28 DAYS IN REGULAR YEARS (YEAR mod 4 is bigger than 0 OR year mod 100=0 AND year mod 400 is bigger than 0)
        	 								*FEBRUARY HAS MORE THAN 29 DAYS IN LEAP YEARS (all others)
        	 								*/
        	 								if((subType[k-1]=='02') && ( ((subType[k]>29) && ((subType[k-2]%4) == 0)) || (   ((subType[k]>28) && ((subType[k-2]%4)>0)) || (((subType[k-2]%100)==0) && ((subType[k-2]%400)>0) && (subType[k]>28)  )   )  ) ) isValidDate = false;
        	  							}
        	 						break;
        	 			}
        	 		}
        	 	}
        	 	else isValidDate = false;
       		}
       		else isValidDate = false;
		}

		if(!(isValidDate)) {
			$(inputField).addClass("falseValue");
		} else $(inputField).removeClass("falseValue");
	}
}

function addDateJSLabels() {
	/*
	*This function adds the following HTML code
	*
	*<div class="dateJSBox *LENGTH VALUE HERE*_area0">
	*	GIVEN INPUT FIELD HERE
	*	<label class="dateJSLabel *LENGTH VALUE HERE*_negMarginLIncl noDisplay" for="*INPUT FIELD ID*"></label>
	*</div>
	*
	*/
	$(".dateJSInput").each(function(){
		var classNameString = $(this).attr("class");
		var lengthValue;
		var possibleLengthValues = classNameString.split(' ');
		for(var i=0; i<possibleLengthValues.length; i++) {
			if(possibleLengthValues[i].match('_txtInput')) {
				var wholeLengthValue = possibleLengthValues[i].split('_');
				lengthValue = wholeLengthValue[0];
			}
		}
		$(this).wrap('<div class="dateJSBox '+lengthValue+'_area0"></div>');
		$(this).after('<label class="dateJSLabel '+lengthValue+'_label '+lengthValue+'_negMarginLIncl noDisplay" for="'+$(this).attr("id")+'"></label>');
	});
}

function addDateJSFunctions() {
	$(".dateJSInput").each(function(){
		$(this).focus(function() {
			var input_empty = "", empty_string = "";
			
			$(this).removeClass("falseValue");
			
			if($(this).val() === input_empty)
			{
				$(this).val(empty_string);
				$(this).removeClass("blankInput");
			}
	
			if($(this).val() != "")
			{
				var date = null;
				date = Date.parse($(this).val());
				if(date!=null)
				{
					$(".dateJSLabel[for='"+$(this).attr("id")+"']").removeClass("noDisplay").text(date.toString("yyyy-MM-dd"));
				}
			}
	        return false;    
		});
		$(this).blur(function(){
			var input_empty = "", empty_string = "";
		
			$(".dateJSLabel[for='"+$(this).attr("id")+"']").addClass("noDisplay").text("");
			
			if($(this).val() === empty_string)
			{
				$(this).val(input_empty).addClass("blankInput");
			}
			validateDate(this);
		});
		$(this).keyup(function(event){
			var message = "";
			var input_empty = "", empty_string = "";
			var date = null;

			$(".dateJSLabel[for='"+$(this).attr("id")+"']").text("");
			if($(this).val() != "")
			{
				date = Date.parse($(this).val());
				
				if(date != null)
				{
					$(".dateJSLabel[for='"+$(this).attr("id")+"']").removeClass("noDisplay").text(date.toString("yyyy-MM-dd"));
					var oEvent = event || window.event;
					if(oEvent.keyCode == 13)
					{
						$(this).val(date.toString("yyyy-MM-dd"));
						$(".dateJSLabel[for='"+$(this).attr("id")+"']").addClass("noDisplay").text("");
					};
				} else
					{
						$(".dateJSLabel[for='"+$(this).attr("id")+"']").addClass("noDisplay").text(message);
					}
			}
			else
				{
					$(".dateJSLabel[for='"+$(this).attr("id")+"']").addClass("noDisplay").text("");
				}			
	      	var evt = event || window.event;
	      	if(evt.stopPropagation) evt.stopPropagation();
		 	evt.cancelBubble = true;
		});
		validateDate(this);
	});
}

/*SELECT REPLACEMENT*/

function rebuildSelectDOM() {
	$('select.replace').each(function(i, ele){
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
		var replacementString = '<span class="'+lengthValue+'_area0 replace '+otherClasses+'">';
			
			if((BrowserDetect.browser == 'Firefox')&& (BrowserDetect.version < 3 )) {
				replacementString = replacementString+'<span class="'+lengthValue+'_area0">&nbsp;</span>';
				};
		
			if($(ele).find('option[selected]').length==0){
				replacementString = replacementString+'<span class="'+lengthValue+'_area1_p7 replaceLabel">'+$(ele).find('option').text()+'</span>';
				replacementString = replacementString+'<input type="hidden" id="'+$(ele).attr('id')+'" name="'+$(ele).attr('name')+'" class="hiddenInput" value="'+$(ele).find('option').val()+'" onchange="'+ele.getAttribute("onchange")+'" />';
			}
			else{
				replacementString = replacementString+'<span class="'+lengthValue+'_area1_p7 replaceLabel">'+$(ele).find('option[selected]').text()+'</span>';
				replacementString = replacementString+'<input type="hidden" id="'+$(ele).attr('id')+'" name="'+$(ele).attr('name')+'" class="hiddenInput" value="'+$(ele).find('option[selected]').val()+'"';
				if(ele.getAttribute("onchange")!=null) replacementString = replacementString+' onchange="'+ele.getAttribute("onchange")+'"';
				replacementString = replacementString+' />';
			}
			replacementString = replacementString+'<span class="'+lengthValue+'_area0_p8 openArea"><input type="button" class="min_imgBtn open" /></span><span class="'+lengthValue+'_area1 pulldown">';
			$(ele).find('option').each(function(j, elem){
				replacementString = replacementString+'<a class="'+lengthValue+'_area0_p7 selectLine';
				if($(elem).text()==$(ele).find('option[selected]').text()) replacementString = replacementString+' actual';
				replacementString = replacementString+'" name="'+$(elem).val()+'">'+$(elem).text()+'</a>';
				if(j==0) {
					replacementString = replacementString+'<input type="button" class="min_imgBtn close endline"/>';
				}
			})
		replacementString = replacementString+'</span></span>'
		if(!((BrowserDetect.browser == 'Firefox')&&(BrowserDetect.version < 3 ))) {
			if(BrowserDetect.browser != 'Explorer'){
				replacementString = replacementString+'<span class="'+lengthValue+'_area0">&nbsp;</span>';
				}
			else {
				replacementString = replacementString+'<span class="free_area0" style="width: 0em;">&nbsp;</span>';
				}
			};
		//replacementString = replacementString+'<span class="'+lengthValue+'_area0">&nbsp;</span>';
		$(ele).replaceWith(replacementString);
	});
}

function addReplacementFunctions() {
	$('span.replace').find('.open').each(function(i,ele){$(ele).click(function(){ $(this).parents('.replace').find('.pulldown').show(); })});
	$('span.replace').find('.close').each(function(i,ele){$(ele).click(function(){ $(this).parents('.replace').find('.pulldown').hide(); })});
	$('span.replace').find('.selectLine').each(function(i,ele){$(ele).click(function(){ $(this).parents('.replace').find('input:hidden').val($(this).attr('name')); $(this).parents('.replace').find('.replaceLabel').text($(this).text()); $(this).parents('.pulldown').find('.actual').removeClass('actual'); $(this).addClass('actual');  $(this).parents('.pulldown').hide(); $(this).parents('span.replace').find('input:hidden').trigger('change');  })});
}

jQuery.fn.replaceValue = function(value) {
	if($(this).find('input[type=hidden]').length > 0 ){
		$(this).find('input[type=hidden]').val(value);
		$(this).find('.replaceLabel').text($(this).find('.selectLine[name='+value+']:first').text());
		$(this).find('.actual').removeClass('actual');
		$(this).find('.selectLine[name='+value+']').addClass('actual');
		$(this).find('input[type=hidden]').trigger('change'); 
	}
};

jQuery.fn.getValue = function() {
	if($(this).find('input[type=hidden]').length > 0 ){
		return $(this).find('input[type=hidden]').val();
	}
};

function installDateTextbox() {
	/*GET LANGUAGE*/
	var language = '';
	language = document.body.lang;
	if(language != '') language = '-'+language;
	/*INCLUDE RIGHT LANGUAGE HERE*/
	include_dom('./resources/eSciDoc_JavaScript/eSciDoc_component_JavaScript/DateJS/date'+language+'.js');
	addDateJSLabels();
	addDateJSFunctions();
}

function installSelectReplacement() {
	rebuildSelectDOM();
	addReplacementFunctions();
}