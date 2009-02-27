
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>CoNE - Control of Named Entities</title>
	<link href="/cone/resources/eSciDoc_CSS_v2/main.css" type="text/css" rel="stylesheet"/>
	<link href="/cone/resources/eSciDoc_CSS_v2/themes/blue/styles/theme.css" id="blueTheme" type="text/css" title="blue" rel="stylesheet"/>
	<script type="text/javascript">

			function remove(element)
			{
				var parent = element.parentNode;
				var listSize = parent.parentNode.getElementsByTagName('li').length;
				if (listSize > 1)
				{
					parent.parentNode.removeChild(parent);
				}
				else
				{
					parent.getElementsByTagName('input')[0].value = '';
					parent.getElementsByTagName('input')[1].value = '';
					parent.removeChild(element);
				}
			}

			function add(element, predicate, hidden, lang)
			{
				var parent = element.parentNode;
				var ul = parent.getElementsByTagName('ul')[0];

				if (ul.getElementsByTagName('li').length == 1 && ul.getElementsByTagName('li')[0].getElementsByTagName('input').length == 1)
				{
					var newButton = document.createElement('input');
					newButton.value = 'delete';
					newButton.type = 'button';
					newButton.onclick = new Function('remove(this)');
					ul.getElementsByTagName('li')[0].appendChild(newButton);
				}
				
				var li = document.createElement("li");
				ul.appendChild(li);
				
				var input = document.createElement('input');
				input.name = predicate;
				input.type = 'hidden';
				input.value = '';
				li.appendChild(input);

				if (lang)
				{
					var inputLang = document.createElement('input');
					inputLang.name = predicate + '_lang';
					inputLang.type = 'hidden';
					inputLang.size = '3';
					inputLang.value = '';
					li.appendChild(inputLang);
				}
				
//				var button = document.createElement('input');
//				button.value = 'delete';
//				button.type = 'button';
//				button.onclick = new Function('remove(this)');
//				li.appendChild(button);

				element.form.submit();
				
			}

			function bindSuggest(element, model, cutId)
			{
				if (typeof cutId != 'undefined' && cutId)
				{
					$('.' + element).suggest("/cone/jquery/" + model + "/query?lang=en", {onSelect: fillSmallId});
				}
				else
				{
					$('.' + element).suggest("/cone/jquery/" + model + "/query?lang=en", {onSelect: fillId});
				}
			};

			function fillSmallId()
			{
				$(this).val(this.resultID.substring(this.resultID.lastIndexOf(':') + 1));
			}
			
			function fillId()
			{
				$(this).val(this.resultID);
			}
			
	</script>
	<script type="text/javascript" src="/cone/js/jquery-1.2.6.min.js">;</script>
	<script type="text/javascript" src="/cone/js/jquery.dimensions.js">;</script>
	<script type="text/javascript" src="/cone/js/jquery.suggest.js">;</script>
	<link type="text/css" rel="stylesheet" href="/cone/js/jquery.suggest.css"/>
</head>