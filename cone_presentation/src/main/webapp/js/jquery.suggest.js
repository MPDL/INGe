	
	/*
	 *	jquery.suggest 1.1 - 2007-08-06
	 *	
	 *	Uses code and techniques from following libraries:
	 *	1. http://www.dyve.net/jquery/?autocomplete
	 *	2. http://dev.jquery.com/browser/trunk/plugins/interface/iautocompleter.js	
	 *
	 *	All the new stuff written by Peter Vulgaris (www.vulgarisoip.com)	
	 *	Feel free to do whatever you want with this file
	 *
	 */
	
	(function($) {

		var suggestXhr;
		
		$.suggest = function(input, options) {

			var timeout = false;		// hold timeout ID for suggestion results to appear	
			var prevLength = 0;			// last recorded length of $input.val()
			var cache = [];				// cache MRU list
			var cacheSize = 0;			// size of cache in chars (bytes?)
			var displayResults = true;
			

			var $input = $(input).attr("autocomplete", "off");
			
			var	$results = $("<ul></ul>").addClass(options.resultsClass).appendTo('body');
			
			
			resetPosition();
			$(window)
				.on('load', resetPosition)		// just in case user is changing size of page while loading
				.on('resize', resetPosition);

			// help IE users if possible
			try {
				$results.bgiframe();
			} catch(e) { }


			
			//unbind events on input
			$input.off('keypress');
			$input.off('keydown');
			$input.off('blur');
			
			// I really hate browser detection, but I don't see any other way
			/*
			if ($.browser.mozilla)
				$input.on('keypress',processKey);	// onkeypress repeats arrow keys in Mozilla/Opera
			else
				*/
			$input.on('keydown',processKey);		// onkeydown repeats arrow keys in IE/Safari
			
			
			
			
			//unbind events on result
			$results.off('mouseover');
			$results.off('mouseenter');
			$results.off('mouseleave');
			
			var mouseOverResults = false;
			
			$results.on('mouseover', function(){
				mouseOverResults = true;
			});
			$results.on("mouseenter",function(){
				mouseOverResults = true;
			});
			$results.on("mouseleave",function(){
				mouseOverResults = false;
			});
			$input.on('blur', function(){
				if(!mouseOverResults) {
					displayResults = false;
					$results.hide();
				}
			});

			function resetPosition() {
				// requires jquery.dimension plugin
				var offset = $input.offset();
				$results.css({
					top: (offset.top + input.offsetHeight) + 'px',
					left: offset.left + 'px'
				});
			}
			
			function escapeSelector(str)
			{
				if( str)
				     return str.replace(/([ #;?%&,.+*~\':"!^$[\]()=>|\/@])/g,'\\$1');
				 else
				     return str;
			}
			
			
			function processKey(e) {
				
				// handling up/down/escape requires results to be visible
				// handling enter/tab requires that AND a result to be selected
				if ((/^27$|^38$|^40$/.test(e.keyCode) && $results.is(':visible')) ||
					(/^13$/.test(e.keyCode) && getCurrentResult()) ||
					(/^9$/.test(e.keyCode))) {
					
		            
					if (e.stopPropagation)
		                e.stopPropagation();

					e.cancelBubble = true;
					e.returnValue = false;
				
					switch(e.keyCode) {
	
						case 38: // up
							prevResult();
							if (e.preventDefault)
				                e.preventDefault();
							break;
				
						case 40: // down
							nextResult();
							if (e.preventDefault)
				                e.preventDefault();
							break;
	
						case 9:  // tab
							mouseOverResults = false;
							if (!getCurrentResult()){
								displayResults = false;
								$results.hide();
							}
							else if (getCurrentResult()) {
								selectCurrentResult();
								if (e.preventDefault)
					                e.preventDefault();
							}
							break;
							
						case 13: // return
							selectCurrentResult();
							if (e.preventDefault)
				                e.preventDefault();
							break;
							
						case 27: //	escape
							mouseOverResults = false;
							$results.hide();
							if (e.preventDefault)
				                e.preventDefault();
							break;
	
					}
					
				} else if ($input.val().length != prevLength && e.keyCode != 9) {

					if (timeout) 
						clearTimeout(timeout);
					timeout = setTimeout(suggest, options.delay);
					prevLength = $input.val().length;
					
				}			
					
				
			}
			
			
			function suggest() {
			
				displayResults=true;
				
				var q = $input.val().trim();
				var lang = '';

				if (q.length >= options.minchars) {
					
					$results.html('<li>loading..<span style="text-decoration: blink;">.</span></li>').show();
					
					cached = checkCache(q);
					
					if (cached) {
					
						displayItems(cached['items']);
						
					} else {

						var vocab = null;
						if(options.vocab)
						{
							vocab = $(options.vocab).val().toLowerCase().replace('_', '-');
							vocab = vocab.substring(vocab.lastIndexOf("/"));
						}
						
						var source = options.source;
						var data = '';
						
						// Check if source URL already contains a language
						if (!(source.indexOf('?lang=') >= 0 || source.indexOf('&lang=') >= 0 || source.indexOf('?l=') >= 0 || source.indexOf('&l=') >= 0))
						{
							lang = $('body').attr('lang');
							data = "format=json&lang="+lang+"&q="+encodeURIComponent(q);
						}
						else
						{
							data = "format=json&q="+encodeURIComponent(q);
						}
 
						if (source.indexOf('?') >= 0)
						{
							data = source.substring(source.indexOf('?') + 1) + '&' + data;
							source = source.substring(0, source.indexOf('?'));
						}

						//Cancel old request
						if(typeof suggestXhr !== 'undefined'){
							suggestXhr.abort();
						}
						suggestXhr = $.ajax({
							processData: false,
							type: "GET",
							dataType: "json",
							cache: false,
							url: (options.vocab == null ? source : source.replace('\$1', vocab)),
							data: data,
							success: function(result) {
									$results.hide();
									var items = parseJSON(result, q);
									displayItems(items);
									addToCache(q, items, result.length);
								}
						});	
					}
					
				} else {
				
					$results.hide();
					
				}
					
			}
			
			
			function checkCache(q) {

				for (var i = 0; i < cache.length; i++)
					if (cache[i]['q'] == q) {
						cache.unshift(cache.splice(i, 1)[0]);
						return cache[0];
					}
				
				return false;
			
			}
			
			function addToCache(q, items, size) {

				while (cache.length && (cacheSize + size > options.maxCacheSize)) {
					var cached = cache.pop();
					cacheSize -= cached['size'];
				}
				
				cache.push({
					q: q,
					size: size,
					items: items
					});
					
				cacheSize += size;
			
			}
			
			function displayItems(items) {
				
				if (!items)
					return;
					
				if (!items.length) {
					$results.hide();
					return;
				}
				
				var html = '';
				for (var i = 0; i < items.length; i++) {
					if(items[i][1].type && items[i][1].type == 'main') {
						html += '<li style="background-color:#E0E0E0;"><span style="font-weight:bold;">' + items[i][0] + '</span></li>';
					}
					else if(items[i][1].type && items[i][1].type == 'alt') {
						html += '<li><span style="padding-left:12px;">' + items[i][0] + '</span></li>';
					}
					else {
						html += '<li>' + items[i][0] + '</li>';
					}
				}
				

				resetPosition();
				$results.html(html);
				if(displayResults)
				{
					$results.show();
				}
				else
				{
					$results.hide();
				}
				
				
				$results
					.children('li')
					.on('mouseover', function() {
						$results.children('li').removeClass(options.selectClass);
						$(this).addClass(options.selectClass);
					})
					.on('click',function(e) {
						e.preventDefault(); 
						e.stopPropagation();
						selectCurrentResult();
					})
					;
							
			}
			
			function parseJSON(jsonObject, q) {
				var items = [];

				var queryParts = q.split(' ');

				var expression = "";
				for(var i = 0; i < queryParts.length; i++) {
					if(queryParts[i] != ""){
						expression =  expression + queryParts[i] + "|";
					}
				}

				for (var i = 0; i < jsonObject.length; i++) {
					jsonObject[i].value = jsonObject[i].value.replace(
							new RegExp(expression, 'ig'), 
							function(q) { return '<span class="' + options.matchClass + '">' + q + '</span>' }
							);

					items[items.length] = new Array(jsonObject[i].value, jsonObject[i]);
				}

				return items;
			}
			
			function parseTxt(txt, q) {
				
				var items = [];
				var tokens = txt.split(options.delimiter);
				
				var queryParts = q.split(' ');
				
				var expression = "";
				for(var i = 0; i < queryParts.length; i++) {
					if(queryParts[i] != ""){
						expression =  expression + queryParts[i] + "|";
					}
				}
				
				// parse returned data for non-empty items
				for (var i = 0; i < tokens.length; i++) {
					
					var token = tokens[i].split(options.subdelimiter);
					token[0]= token[0].trim();
					
				/*	var token = tokens[i].trim();*/
					if (token[0]) {
						token[0] = token[0].replace(
							new RegExp(expression, 'ig'), 
							function(q) { return '<span class="' + options.matchClass + '">' + q + '</span>' }
							);
						items[items.length] = token;
					}
				}
				
				return items;
			}
			
			function getCurrentResult() {
			
				if (!$results.is(':visible'))
					return false;
			
				var $currentResult = $results.children('li.' + options.selectClass);
				
				if (!$currentResult.length)
					$currentResult = false;
					
				return $currentResult;

			}
			
			function getResult(result) {
				var id = 'test';
				var span = '<span class="' + options.matchClass + '">';
				for (var j = 0; j < cache.length; j++) {
					for(var i = 0; i < cache[j]['items'].length; i++) {
						var item = cache[j]['items'][i];
						item[0] = item[0].replace(new RegExp(span,'ig'),'');
						item[0] = item[0].replace(new RegExp('</span>','ig'),'');
						item[0] = item[0].trim();
						//alert(item[0] + '==' + result + " : " + (item[0]==result));
						if (item[0] == result.trim()) {
							obj =item[1];
							break;
						}
					}
				}
				return obj;
			}
			
			function selectCurrentResult() {
			
				$currentResult = getCurrentResult();
				
				
			
				if ($currentResult) {
					$input.val($currentResult.text());
					var resultObj = getResult($currentResult.text());
					$input.resultID = resultObj['id'];
					$input.resultLanguage = resultObj['language'];
					$input.resultValue = $currentResult.text();
					$input.result = resultObj;
					$results.hide();
					options.onSelect.apply($input);
				}
			
			}
			
			function nextResult() {
			
				$currentResult = getCurrentResult();
			
				if ($currentResult)
					$currentResult
						.removeClass(options.selectClass)
						.next()
							.addClass(options.selectClass);
				else
					$results.children('li:first-child').addClass(options.selectClass);
			
			}
			
			function prevResult() {
			
				$currentResult = getCurrentResult();
			
				if ($currentResult)
					$currentResult
						.removeClass(options.selectClass)
						.prev()
							.addClass(options.selectClass);
				else
					$results.children('li:last-child').addClass(options.selectClass);
			
			}
	
		}
		
		$.fn.suggest = function(source, options) {
		
			if (!source)
				return;

			
			options = options || {};
			options.source = source;
			options.delay = options.delay || 100;
			options.resultsClass = options.resultsClass || 'ac_results';
			options.selectClass = options.selectClass || 'ac_over';
			options.matchClass = options.matchClass || 'ac_match';
			options.minchars = options.minchars || 2;
			options.delimiter = options.delimiter || '\n';
			options.subdelimiter = options.subdelimiter || '|';
			options.onSelect = options.onSelect || false;
			options.maxCacheSize = options.maxCacheSize || 65536;
			//options.vocab = options.vocab;
	
			this.each(function() {
				new $.suggest(this, options);
			});
	
			return this;
			
		};
		
	})(jQuery);
	
