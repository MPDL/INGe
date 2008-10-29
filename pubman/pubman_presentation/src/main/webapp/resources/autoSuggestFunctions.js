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
* f�r wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur F�rderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

/*
* JavaScript functions for pubman_presentation
*/

	var languageSuggestURL = '';
	var journalSuggestURL = '';
	var journalDetailsBaseURL = '';
	var journalSuggestCommonParentClass = 'sourceArea';
	var journalSuggestTrigger = 'JOURNAL';

	function getJournalDetails(details)
	{
		var parent = $input.parents('.'+journalSuggestCommonParentClass);
		var title = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
		var altTitle = (typeof details.http_purl_org_dc_terms_alternative != 'undefined' ?
				(typeof details.http_purl_org_dc_terms_alternative == 'object' ?
						details.http_purl_org_dc_terms_alternative[0] : details.http_purl_org_dc_terms_alternative) : null);
		var publisher = (typeof details.http_purl_org_dc_elements_1_1_publisher != 'undefined' ? details.http_purl_org_dc_elements_1_1_publisher : null);
		var place = (typeof details.http_purl_org_dc_terms_publisher != 'undefined' ? details.http_purl_org_dc_terms_publisher : null);
		
		var identifier = (typeof details.http_purl_org_dc_elements_1_1_identifier != 'undefined' ?
				(typeof details.http_purl_org_dc_elements_1_1_identifier == 'object' ?
						details.http_purl_org_dc_elements_1_1_identifier[0] : details.http_purl_org_dc_elements_1_1_identifier) : null);

		fillField('journalSuggest', title, parent);
		
		$input.content = title;
		
		fillField('publisher', publisher, parent);
		fillField('place', place, parent);
		fillField('identifierValue', identifier, parent);
	}

	function fillField(name, value, commonParent)
	{
		$(commonParent).find('.' + name).val(value);
	}
	
	function fillFields()
	{
		$input = $(this);
		$.getJSON(journalDetailsBaseURL + this.resultID, getJournalDetails);							
	}
	
	function clearFields()
	{
		if ($(this).val() != this.content)
		{
			var parent = $input.parents('.'+journalSuggestCommonParentClass);
			
			fillField('publisher', '', parent);
			fillField('place', '', parent);
			fillField('identifierValue', '', parent);
		}
	}
	
	function bindJournalSuggest()
	{
		$('.journalSuggest').suggest(journalSuggestURL, { onSelect: fillFields});
		$('.journalSuggest').change(clearFields);
	}
	
	function bindSuggests()
	{
		$('.journalPulldown').change(function(){if($(this).val() == journalSuggestTrigger) {  $(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest'); } else
		{
			$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
			$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
			$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown'); $('.autoSuggestsArea').hide();
			$(this).parents('.'+journalSuggestCommonParentClass).find('.publisher').val('');
			$(this).parents('.'+journalSuggestCommonParentClass).find('.place').val('');
			$(this).parents('.'+journalSuggestCommonParentClass).find('.identifierValue').val(''); }; setTimeout('bindJournalSuggest()', 500);});
		bindJournalSuggest();
		$('.languageSuggest').suggest(languageSuggestURL, { onSelect: function() { $(this).siblings('select').val( (this.resultID.split(':'))[2] ); }   });
	};