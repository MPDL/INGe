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


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
--%>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>CoNE - Control of Named Entities</title>
	<link href="/pubman/resources/eSciDoc_CSS_v2/main.css" type="text/css" rel="stylesheet"/>
	<link href="/pubman/resources/eSciDoc_CSS_v2/themes/skin_PubMan/styles/theme.css" id="PubManTheme" type="text/css" title="PubMan" rel="stylesheet"/>
	<script type="text/javascript">

			function remove(element)
			{
				var parent = $(element).parents(".singleItem");
				var listSize = $(parent).parent().find(".singleItem").length;
				if (listSize > 1)
				{
					$(parent).remove();
				}
				else
				{
					$(parent).find("input[type='text']").each(function(){ $(this).val('');});
					$(element).remove();
				}
			}

			function add(element, predicate, hidden, lang)
			{
				var parent = $(element).parents('.itemLine');
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
				if (typeof cutId != 'undefined' && cutId)
				{
					$('.' + element).suggest("/cone/json/" + model + "/query?lang=en", {onSelect: fillSmallId});
				}
				else
				{
					$('.' + element).suggest("/cone/json/" + model + "/query?lang=en", {onSelect: fillId});
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