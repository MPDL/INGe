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

/*
* JavaScript functions for pubman_presentation
*/

//	var languageSuggestURL = '';
//	var journalSuggestURL = '';
//	var subjectSuggestURL = '';
//	var personSuggestURL = '';
//	var organizationSuggestURL = '';
	var journalDetailsBaseURL = '$1?format=json';
	var languageDetailsBaseURL = '';
	var autopasteDelimiter = ' ||##|| ';
	var autopasteInnerDelimiter = ' @@~~@@ ';
	var journalSuggestCommonParentClass = 'itemBlock';
	var journalSuggestTrigger = 'JOURNAL';
	var subjectSuggestCommonParentClass = 'parentArea';
	var languageSuggestCommonParentClass = 'languageArea';
	var personSuggestCommonParentClass = 'suggestAnchor';
	var commonParentClass = 'suggestAnchor'
	
	var globalId = '';

	function getJournalDetails(details)
	{
		
		var parent = $input.parents('.'+journalSuggestCommonParentClass);
		var title = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
		var altTitle = (typeof details.http_purl_org_dc_terms_alternative != 'undefined' ?
				(typeof details.http_purl_org_dc_terms_alternative == 'object' ?
						details.http_purl_org_dc_terms_alternative : details.http_purl_org_dc_terms_alternative) : null);
		var abbrev = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_abbreviation != 'undefined' ?
				(typeof details.http_purl_org_escidoc_metadata_terms_0_1_abbreviation == 'object' ?
						details.http_purl_org_escidoc_metadata_terms_0_1_abbreviation : details.http_purl_org_escidoc_metadata_terms_0_1_abbreviation) : null);
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
		
		
		if(altTitle != null && abbrev != null)
		{
			allAltTitles += autopasteDelimiter;
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
		
		//Used for search
		fillField('journalSuggestQuotes', '"' + title + '"', parent);
		
		fillField('sourceAltTitlePasteField', allAltTitles, parent);
		fillField('publisher', publisher, parent);
		fillField('place', place, parent);
		fillField('sourceIdentifierPasteField', allIDs, parent);
		$pb(parent).find('.hiddenAutosuggestUploadBtn').click();
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
			orgName = chosenName.substring(chosenName.indexOf('(') + 1, chosenName.lastIndexOf(')')).replace(/^\s*(.*\S)\s*$/, '$1');
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
					if (details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_eprint_terms_affiliatedInstitution.replace(/^\s*(.*\S)\s*$/, '$1') == orgName
						&& typeof details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_dc_elements_1_1_identifier != 'undefined')
					{
						orgId = $pb.trim(details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_dc_elements_1_1_identifier);
						break;
					}
				}
			}
			else if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined'
				&& typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution != 'undefined'
				&& typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier != 'undefined'
				&& details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution.replace(/^\s*(.*\S)\s*$/, '$1') == orgName)
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
		$input.blur();
		$input.focus();
		fillField('personIdentifier', personId, parent, true);
		$pb(parent).find('.removeAutoSuggestPerson').css('display', 'inline');
		$pb(parent).find('.givenName').attr('class', 'medium_txtInput givenName');

		if (personId != null && personId != '')
		{
			$pb(parent).find('.authorLink').replaceWith('<a href="' + personId + '" class="small_area0 authorCard authorLink" target="_blank">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
			
		}

		// Try to disable input field
		$pb.each($pb(parent).find('.disableAfter'),
				function ()
				{
					$pb(this).attr('readonly', 'readonly');
				}
		);
		
		var counter = 1;
		var found = false;
		var empty = true;
		
		var orgIdString = (orgId == null ? '' : orgId);
		
		
		$pb.each($input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationName'),
			function ()
			{
				var otherOrgName = $pb(this).val();
				var otherOrgId = $pb(this).siblings('.organizationIdentifier').val();
				
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
				$input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationName').attr('title', (orgName == null ? "" : orgName));
				$input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationIdentifier').val(orgIdString == null ? "" : orgIdString);
				$input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationIdentifier').attr('title', (orgIdString == null ? "" : orgIdString));

				if (orgIdString != null && orgIdString != '')
				{
					$input.parents('.itemBlockContent').find('.ouLink').replaceWith('<a href="#" onclick="openCenteredWindow(\'AffiliationDetailPage.jsp?id=' + orgIdString + '\', 980, 400, \'Details\');return false" class="small_area0 ouCard ouLink">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
					
				}
				fillField('ouNumber', '1', parent);
			}
			else
			{
				$input.siblings('.organizationPasteField').val(orgIdString + autopasteInnerDelimiter + (orgName == null ? "" : orgName));
				$input.siblings('.organizationPasteField').attr('title', (orgIdString + autopasteInnerDelimiter + (orgName == null ? "" : orgName)));
				fillField('ouNumber', '' + counter, parent);
				$input.siblings('.hiddenButtonPasteOrganizations').click();
			}
		}
		
	}
	
	function removeConeId(element)
	{
		var $input = $pb(element);
		var parent = $input.parents('.' + personSuggestCommonParentClass);
		if ($pb(parent).find('.personIdentifier').val() != '')
		{
			fillField('personIdentifier', '', parent);
		}
	}
	
	// removes 'readonly' attributes and resets fields for autosuggest
	function removeAuthorAutoSuggest(element)
	{
		var $input = $pb(element);
		var parent = $input.parent();
		var field = null;
		if ($pb(parent).find('.personIdentifier').val() != '')
		{
			field = $pb(parent).find('.personIdentifier');
			field.removeAttr('readonly');
			fillField('personIdentifier', '', parent);
		}
		if ($pb(parent).find('.familyName').val() != '')
		{	
			field = $pb(parent).find('.familyName');
			field.removeAttr('readonly');
			fillField('familyName', '', parent);
		}
		if ($pb(parent).find('.givenName').val() != '')
		{
			field = $pb(parent).find('.givenName');
			field.removeAttr('readonly');
			fillField('givenName', '', parent);
		}
		$input.css('display', 'none');
		$input.parent().find('.givenName').attr('class', 'large_txtInput givenName');
		bindSuggests();
		return;
	}
	
	function updatePersonUi()
	{
		// maintain attributes for autosuggest filled persons
		if($pb('.personIdentifier' != null))
		{
			$pb('.personIdentifier').each(function(ind){
				if (this.value) {
					$pb(this).parents('.' + personSuggestCommonParentClass).find('.familyName').attr('readonly', 'readonly');
					$pb(this).parents('.' + personSuggestCommonParentClass).find('.givenName').attr('readonly', 'readonly');
					$pb(this).parents('.' + personSuggestCommonParentClass).find('.removeAutoSuggestPerson').css('display', 'inline');
					$pb(this).parents('.' + personSuggestCommonParentClass).find('.givenName').attr('class', 'medium_txtInput givenName');
				}
			})
		}
		
	}
	
	function fillField(name, value, commonParent, readonly)
	{
		var field = $pb(commonParent).find('.' + name);
		
		if(field.length)
		{
			
			if(value!=null)
			{
				field.val(value);
			}
			else
			{
				field.val('');
			}
			
			
			
			field.attr('title', value);
			
			
			if (typeof readonly != 'undefined')
			{
				field.unbind('keydown');
				field.unbind('keypress');
				field.attr('readonly', 'readonly');
			}
		}
		
	}
	
	function fillFields()
	{
		$input = $pb(this);
		globalId = this.resultID;
		$pb.getJSON(journalDetailsBaseURL.replace('$1', this.resultID), getJournalDetails);
	}
	
	function fillPersonFields()
	{
		$input = $pb(this);
		$input.resultValue = this.resultValue;
		$input.resultID = this.resultID;
		$input.resultLanguage = this.resultLanguage;
		if (typeof this.resultLanguage != 'undefined')
		{
			$pb.getJSON(personDetailsBaseURL.replace('$1', this.resultID).replace('$2', this.resultLanguage), getPersonDetails);
		}
		else
		{
			$pb.getJSON(personDetailsBaseURL.replace('$1', this.resultID).replace('$1', this.resultID).replace('$2', '*'), getPersonDetails);
		}
		$input.unbind('keydown');
		$input.unbind('keypress');
	}
	
	function fillOrganizationFields()
	{
		$input = $pb(this);
		var parent = $input.parents('.' + commonParentClass);
		fillField('organizationName', this.resultValue, parent);
		fillField('organizationIdentifier', this.resultID, parent);
		fillField('organizationAddress', this.result.address, parent);
		
		if (this.resultID != null && this.resultID != '')
		{
			$pb(parent).find('.ouLink').replaceWith('<a href="#" onclick="openCenteredWindow(\'/pubman/faces/AffiliationDetailPage.jsp?id=' + this.resultID + '\', 980, 400, \'Details\');return false" class="small_area0 ouCard ouLink" target="_blank">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
			
		}

		// Try to disable input field
		$pb.each($pb(parent).find('.disableAfter'),
				function ()
				{
					$pb(this).attr('readonly', 'readonly');
				}
		);
		
	}
	
	function bindJournalSuggest()
	{
		if(typeof journalSuggestURL != 'undefined')
		{
			//journalsuggestQuotes is used for advanced search, adds quotes around title
			$pb('.journalSuggest, .journalSuggestQuotes').suggest(journalSuggestURL, { onSelect: fillFields});
		}
	}
	
	function bindSuggests()
	{
		$pb('select.journalPulldown[value="'+journalSuggestTrigger+'"]').parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
		$pb('span.journalPulldown').find('input[type=hidden][value="'+journalSuggestTrigger+'"]').parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
		
		$pb('select.journalPulldown').change(
				function(){
					if($pb(this).val() == journalSuggestTrigger) {
						$pb(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
					} else { 
						$pb(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
						$pb(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
						$pb(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown');
						$pb('.autoSuggestsArea').hide();
					};
					var t = window.setTimeout('bindJournalSuggest()', 500);
				});

		$pb('span.journalPulldown').find('input[type=hidden]').change(
				function(){
					if($pb(this).val() == journalSuggestTrigger) {
						$pb(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
					} else {
						$pb(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
						$pb(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
						$pb(this).parents('.'+journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown');
						$pb('.autoSuggestsArea').hide();
					};
					var t = window.setTimeout('bindJournalSuggest()', 500);
				});

		bindJournalSuggest();
		if(typeof languageSuggestURL != 'undefined')
		{
			$pb('.languageSuggest').suggest(languageSuggestURL, { onSelect: selectLanguage});
		}
		$pb('.subjectSuggest').each(
			function(i,ele){
				if(typeof subjectSuggestURL != 'undefined')
				{
					$pb(ele).suggest(subjectSuggestURL, { vocab: $pb(ele).parents('.subjectArea').find('.vocabulary'), onSelect: function() {$pb(this).val(this.currentResult)}});
				}
			});
		if(typeof personSuggestURL != 'undefined')
		{
			$pb('.personSuggest').each (
				function(i,ele){
					if($pb(ele).parent().find('.personIdentifier').val() == null || $pb(ele).parent().find('.personIdentifier').val() == '')
					{
						$pb(ele).suggest(personSuggestURL, { onSelect: fillPersonFields });
					}
				}
			);
			
		}
		if(typeof organizationSuggestURL != 'undefined')
		{
			$pb('.organizationSuggest').suggest(organizationSuggestURL, { onSelect: fillOrganizationFields });
		}
	};
	
	function selectLanguage()
	{
		$input = $pb(this);
		if (($pb.trim(this.resultValue)).indexOf(' ') !== -1){
			var langShortHand = $pb.trim(($pb.trim(this.resultValue)).substr(0, ($pb.trim(this.resultValue)).indexOf(' ')));
			if (langShortHand != ''){
				$input.val(langShortHand); 
				$input.attr('title', langShortHand);
			}
			var lang = $pb.trim(($pb.trim(this.resultValue)).substr(($pb.trim(this.resultValue)).lastIndexOf(' ') + 1));
			if (lang != ''){
				$input.parents('.'+languageSuggestCommonParentClass).find('.languageText').val(lang);
				$input.parents('.'+languageSuggestCommonParentClass).find('.languageText').attr('title', lang);
			}
		}
	}