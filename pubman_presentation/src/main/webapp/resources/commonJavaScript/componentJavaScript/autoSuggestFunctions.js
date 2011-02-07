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

/*
* JavaScript functions for pubman_presentation
*/

	var languageSuggestURL = '';
	var journalSuggestURL = '';
	var subjectSuggestURL = '';
	var personSuggestURL = '';
	var organizationSuggestURL = '';
	var journalDetailsBaseURL = '';
	var languageDetailsBaseURL = '';
	var autopasteDelimiter = ' ||##|| ';
	var autopasteInnerDelimiter = ' @@~~@@ ';
	var journalSuggestCommonParentClass = 'sourceArea';
	var journalSuggestTrigger = 'JOURNAL';
	var subjectSuggestCommonParentClass = 'parentArea';
	var languageSuggestCommonParentClass = 'languageArea';
	var personSuggestCommonParentClass = 'personArea';
	var commonParentClass = 'suggestAnchor'
	
	var globalId = '';

	function getJournalDetails(details)
	{
		
		var parent = $input.parents('.'+journalSuggestCommonParentClass);
		var title = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
		var altTitle = (typeof details.http_purl_org_dc_terms_alternative != 'undefined' ?
				(typeof details.http_purl_org_dc_terms_alternative == 'object' ?
						details.http_purl_org_dc_terms_alternative[0] : details.http_purl_org_dc_terms_alternative) : null);
		var abbrev = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_abbreviation != 'undefined' ?
				(typeof details.http_purl_org_escidoc_metadata_terms_0_1_abbreviation == 'object' ?
						details.http_purl_org_escidoc_metadata_terms_0_1_abbreviation[0] : details.http_purl_org_escidoc_metadata_terms_0_1_abbreviation) : null);
		var publisher = (typeof details.http_purl_org_dc_elements_1_1_publisher != 'undefined' ? details.http_purl_org_dc_elements_1_1_publisher : null);
		var place = (typeof details.http_purl_org_dc_terms_publisher != 'undefined' ? details.http_purl_org_dc_terms_publisher : null);
		
		var identifier = (typeof details.http_purl_org_dc_elements_1_1_identifier != 'undefined' ? 
				(typeof details.http_purl_org_dc_elements_1_1_identifier == 'object' ?
						details.http_purl_org_dc_elements_1_1_identifier : details.http_purl_org_dc_elements_1_1_identifier) : null);
				
	
		var allAltTitles = '';
		if(((altTitle != null) && typeof(altTitle) == 'object'))
		{
			allAltTitles = 'OTHER' + autopasteInnerDelimiter + altTitle[0];
			for(var i=1; i<altTitle.length; i++) {
					allAltTitles += autopasteDelimiter + 'OTHER' + autopasteInnerDelimiter + altTitle[i];
			}
		}
		else if (altTitle != null)
		{
			allAltTitles = 'OTHER' + autopasteInnerDelimiter + altTitle;
		}
		
		if(((abbrev != null) && typeof(abbrev) == 'object'))
		{
			allAltTitles += 'ABBREVIATION' + autopasteInnerDelimiter + abbrev[0];
			for(var i=1; i<abbrev.length; i++) {
					allAltTitles = allAltTitles + autopasteDelimiter + 'ABBREVIATION' + autopasteInnerDelimiter + abbrev[i];
			}
		}
		else if (abbrev != null)
		{
			allAltTitles += 'ABBREVIATION' + autopasteInnerDelimiter + abbrev;
		}
		
		var allIDs = '';
		
		
		if(identifier != null){
			if(typeof(identifier)=='object' && typeof(identifier[0])!='undefined'){
				for(var i = 0; i<identifier.length; i++) {
					
					var identifierType = identifier[i]['http_www_w3_org_2001_XMLSchema_instance_type'];
					var identifierValue = identifier[i]['http_www_w3_org_1999_02_22_rdf_syntax_ns_value'];
					
					if (i > 0)
					{
						allIDs += autopasteDelimiter;
					}
					if (typeof autopasteDelimiter && identifierType != 'undefined')
					{
						allIDs += identifierType + '|';
					}
					allIDs += identifierValue;
				}
			} else{
				
				var identifierType = identifier['http_www_w3_org_2001_XMLSchema_instance_type'];
				var identifierValue = identifier['http_www_w3_org_1999_02_22_rdf_syntax_ns_value'];
			
				if (typeof identifierType != null && identifierType != 'undefined')
				{
					allIDs += identifierType + '|';
				}
				allIDs += identifierValue;
			}
		}
		
		if (globalId != '')
		{
			if (allIDs != '' || allIDs !='undefined')
			{
				allIDs += autopasteDelimiter;
			}
			allIDs += 'http://purl.org/escidoc/metadata/terms/0.1/CONE|' + globalId;
		}
		
		
		fillField('journalSuggest', title, parent);
		fillField('sourceAltTitlePasteField', allAltTitles, parent);
		fillField('publisher', publisher, parent);
		fillField('place', place, parent);
		fillField('sourceIdentifierPasteField', allIDs, parent);
		$(parent).find('.hiddenAutosuggestUploadBtn').click();
	}

	function getPersonDetails(details)
	{
		var parent = $input.parents('.' + personSuggestCommonParentClass);
		
		var completeName = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
		
		var chosenName = $input.resultValue;
		var orgName = null;
		var orgId = null;
		
		if (chosenName.indexOf('(') >= 0)
		{
			orgName = chosenName.substring(chosenName.indexOf('(') + 1, chosenName.indexOf(')')).replace(/^\s*(.*\S)\s*$/, '$1');
			chosenName = chosenName.substring(0, chosenName.indexOf('(')).replace(/^\s*(.*\S)\s*$/, '$1');
		}
		var familyName = '';
		var givenName = '';
		if (chosenName.indexOf(',') >= 0)
		{
			familyName = chosenName.split(',')[0].replace(/^\s*(.*\S)\s*$/, '$1');
			givenName = chosenName.split(',')[1].replace(/^\s*(.*\S)\s*$/, '$1');
		}
		else if (chosenName.indexOf(' ') >= 0)
		{
			var firstIndex = chosenName.indexOf(' ');
			familyName = chosenName.substring(0, firstIndex).replace(/^\s*(.*\S)\s*$/, '$1');
			givenName = chosenName.substring(firstIndex + 1).replace(/^\s*(.*\S)\s*$/, '$1');
		}
		else
		{
			familyName = chosenName;
		}
		if (orgName != null)
		{
			if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined'
				&& typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.length != 'undefined')
			{
				for (var i = 0; i < details.http_purl_org_escidoc_metadata_terms_0_1_position.length; i++)
				{
					if (details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_eprint_terms_affiliatedInstitution == orgName
						&& typeof details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_dc_elements_1_1_identifier != 'undefined')
					{
						orgId = details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_dc_elements_1_1_identifier;
						break;
					}
				}
			}
			else if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined'
				&& typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution != 'undefined'
				&& typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier != 'undefined'
				&& details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution == orgName)
			{
				orgId = details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier;
			}
		}
		else
		{
			if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined'
				&& typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.length != 'undefined')
			{
				orgName = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position[0].http_purl_org_eprint_terms_affiliatedInstitution != 'undefined' ? details.http_purl_org_escidoc_metadata_terms_0_1_position[0].http_purl_org_eprint_terms_affiliatedInstitution : null);
				orgId = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position[0].http_purl_org_dc_elements_1_1_identifier != 'undefined' ? details.http_purl_org_escidoc_metadata_terms_0_1_position[0].http_purl_org_dc_elements_1_1_identifier : null);
			}
			else if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined')
			{
				orgName = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution != 'undefined' ? details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution : null);
				orgId = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier != 'undefined' ? details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier : null);
			}
		}
		var personId = $input.resultID;

		fillField('familyName', familyName, parent, true);
		fillField('givenName', givenName, parent, true);
//		fillField('orgName', orgName, parent);
//		fillField('orgIdentifier', orgId, parent, true);
		$input.blur();
		$input.focus();
		fillField('personIdentifier', personId, parent, true);
		
		if (personId != null && personId != '')
		{
			$(parent).find('.authorLink').replaceWith('<a href="' + personId + '" class="small_area0 authorCard authorLink" target="_blank">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
			
		}

		var counter = 1;
		var found = false;
		var empty = true;
		
		var orgIdString = (orgId == null ? '' : orgId);
		
		
		$.each($input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationName'),
			function ()
			{
				var otherOrgName = $(this).val();
				var otherOrgId = $(this).siblings('.organizationIdentifier').val();
				
				if (orgName == otherOrgName && orgIdString == otherOrgId)
				{
					fillField('ouNumber', counter + '', parent);
					found = true;
				}
				else if (otherOrgName != '')
				{
					empty = false;
				}
					
				counter++;
			}
		);

		if (!found)
		{

			if (counter == 2 && empty)
			{
				
				$input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationName').val(orgName == null ? "" : orgName);
				$input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationIdentifier').val(orgIdString == null ? "" : orgIdString);

				if (orgIdString != null && orgIdString != '')
				{
					$input.parents('.itemBlockContent').find('.ouLink').replaceWith('<a href="#" onclick="openCenteredWindow(\'AffiliationDetailPage.jsp?id=' + orgIdString + '\', 980, 400, \'Details\');return false" class="small_area0 ouCard ouLink">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
					
				}
				fillField('ouNumber', '1', parent);
			}
			else
			{
				$input.siblings('.organizationPasteField').val(orgIdString + autopasteInnerDelimiter + (orgName == null ? "" : orgName));
				fillField('ouNumber', '' + counter, parent);
				$input.siblings('.hiddenButtonPasteOrganizations').click();
			}
		}
		
	}
	
	function removeConeId(element)
	{
		var $input = $(element);
		var parent = $input.parents('.' + personSuggestCommonParentClass);
		if ($(parent).find('.personIdentifier').val() != '')
		{
			fillField('personIdentifier', '', parent);
		}
	}
	
	function fillField(name, value, commonParent, readonly)
	{
		var field = $(commonParent).find('.' + name)
		field.val(value);
		if (typeof readonly != 'undefined')
		{
			$(field).attr('readonly', true);
		}
	}
	
	function fillFields()
	{
		$input = $(this);
		globalId = this.resultID;
		$.getJSON(journalDetailsBaseURL.replace('$1', this.resultID), getJournalDetails);
	}
	
	function fillPersonFields()
	{
		$input = $(this);
		$input.resultValue = this.resultValue;
		$input.resultID = this.resultID;
		$input.resultLanguage = this.resultLanguage;
		if (typeof this.resultLanguage != 'undefined')
		{
			$.getJSON(personDetailsBaseURL.replace('$1', this.resultID).replace('$2', this.resultLanguage), getPersonDetails);
		}
		else
		{
			$.getJSON(personDetailsBaseURL.replace('$1', this.resultID).replace('$1', this.resultID).replace('$2', '*'), getPersonDetails);
		}
	}
	
	function fillOrganizationFields()
	{
		$input = $(this);
		var parent = $input.parents('.' + commonParentClass);
		fillField('organizationName', this.resultValue, parent);
		fillField('organizationIdentifier', this.resultID, parent);
		fillField('organizationAddress', this.result.address, parent);
		
		if (this.resultID != null && this.resultID != '')
		{
			$(parent).find('.ouLink').replaceWith('<a href="#" onclick="openCenteredWindow(\'/pubman/faces/AffiliationDetailPage.jsp?id=' + this.resultID + '\', 980, 400, \'Details\');return false" class="small_area0 ouCard ouLink" target="_blank">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
			
		}
		
	}
	
	function bindJournalSuggest()
	{
		$('.journalSuggest').suggest(journalSuggestURL, { onSelect: fillFields});
	}
	
	function bindSuggests()
	{
		$('select.journalPulldown[value="'+journalSuggestTrigger+'"]').parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
		$('span.journalPulldown').find('input[type=hidden][value="'+journalSuggestTrigger+'"]').parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
		
		$('select.journalPulldown').change(
				function(){
					if($(this).val() == journalSuggestTrigger) {
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
					} else { 
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown');
						$('.autoSuggestsArea').hide();
					};
					var t = window.setTimeout('bindJournalSuggest()', 500);
				});

		$('span.journalPulldown').find('input[type=hidden]').change(
				function(){
					if($(this).val() == journalSuggestTrigger) {
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
					} else {
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
						$(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown');
						$('.autoSuggestsArea').hide();
					};
					var t = window.setTimeout('bindJournalSuggest()', 500);
				});

		bindJournalSuggest();
		
		$('.languageSuggest').suggest(languageSuggestURL, { onSelect: selectLanguage});
		$('.subjectSuggest').each(
			function(i,ele){
				$(ele).suggest(subjectSuggestURL, { vocab: $(ele).parents('.subjectArea').find('.vocabulary'), onSelect: function() {$(this).val(this.currentResult)}});
			});
		$('.personSuggest').suggest(personSuggestURL, { onSelect: fillPersonFields });
		
		$('.organizationSuggest').suggest(organizationSuggestURL, { onSelect: fillOrganizationFields });

	};
	
	function selectLanguage()
	{
		$input = $(this);
		var lang = document.getElementsByTagName('body')[0].lang;
		$.getJSON(languageDetailsBaseURL.replace('$1', this.resultID).replace('$2', lang), selectLanguageDetails);
	}
	
	function selectLanguageDetails(details)
	{
		var identifier = (typeof details.http_purl_org_dc_elements_1_1_relation.http_purl_org_dc_elements_1_1_identifier != 'undefined' ?
				details.http_purl_org_dc_elements_1_1_relation.http_purl_org_dc_elements_1_1_identifier : null);
		var name = (typeof details.http_purl_org_dc_elements_1_1_relation.http_purl_org_dc_elements_1_1_identifier != 'undefined' ?
				details.http_purl_org_dc_elements_1_1_relation.http_purl_org_dc_elements_1_1_title : null);
		var id3;
		if (identifier != null && !(typeof identifier.splice === 'function') && identifier.length == 3)
		{
			id3 = identifier;
		}
		else if (identifier != null)
		{
			for (var i=0; i < identifier.length; i++)
			{
				if (identifier[i].length == 3)
				{
					id3 = identifier[i];
				}
			}
		}
		
		$input.val(id3);
		$input.parents('.'+languageSuggestCommonParentClass).find('.languageText').val(name);
	}
	