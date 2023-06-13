var autopasteDelimiter = ' ||##|| ';
var autopasteInnerDelimiter = ' @@~~@@ ';

var globalId = '';

var journalDetailsBaseURL = '$1?format=json';
var languageDetailsBaseURL = '';

var commonParentClass = 'suggestAnchor'
var journalSuggestCommonParentClass = 'journalSuggestAnchor';
var journalSuggestTrigger = 'JOURNAL';
var languageSuggestCommonParentClass = 'languageArea';
var personSuggestCommonParentClass = 'suggestAnchor';

function getJournalDetails(details) {
    var parent = $input.parents('.' + journalSuggestCommonParentClass);
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
    var allIDs = '';
    
    if (((altTitle != null) && typeof(altTitle) == 'object')) {
        allAltTitles = 'OTHER' + autopasteInnerDelimiter + altTitle[0];
        for (var i = 1; i < altTitle.length; i++) {
            allAltTitles += autopasteDelimiter + 'OTHER' + autopasteInnerDelimiter + altTitle[i];
        }
    } else if (altTitle != null) {
        allAltTitles = 'OTHER' + autopasteInnerDelimiter + altTitle;
    }

    if (altTitle != null && abbrev != null) {
        allAltTitles += autopasteDelimiter;
    }

    if (((abbrev != null) && typeof(abbrev) == 'object')) {
        allAltTitles += 'ABBREVIATION' + autopasteInnerDelimiter + abbrev[0];
        for (var i = 1; i < abbrev.length; i++) {
            allAltTitles = allAltTitles + autopasteDelimiter + 'ABBREVIATION' + autopasteInnerDelimiter + abbrev[i];
        }
    } else if (abbrev != null) {
        allAltTitles += 'ABBREVIATION' + autopasteInnerDelimiter + abbrev;
    }

    if (identifier != null) {
        if (typeof(identifier) == 'object' && typeof(identifier[0]) != 'undefined') {
            for (var i = 0; i < identifier.length; i++) {
                var identifierType = identifier[i]['http_www_w3_org_2001_XMLSchema_instance_type'];
                var identifierValue = identifier[i]['http_www_w3_org_1999_02_22_rdf_syntax_ns_value'];

                if (i > 0) {
                    allIDs += autopasteDelimiter;
                }
                if (typeof autopasteDelimiter && identifierType != 'undefined') {
                    allIDs += identifierType + '|';
                }
                if (identifierValue.includes('/journals/resource/')) {
                  allIDs += identifierValue.substring(identifierValue.indexOf('/journals/resource/'), identifierValue.length);
                }
                else {
                  allIDs += identifierValue;
                }
            }
        } else {
            var identifierType = identifier['http_www_w3_org_2001_XMLSchema_instance_type'];
            var identifierValue = identifier['http_www_w3_org_1999_02_22_rdf_syntax_ns_value'];

            if (typeof identifierType != null && identifierType != 'undefined') {
                allIDs += identifierType + '|';
            }
            allIDs += identifierValue;
        }
    }

    if (globalId != '') {
        if (allIDs != '' || allIDs != 'undefined') {
            allIDs += autopasteDelimiter;
        }
        if (globalId.includes('/journals/resource/')) {
          allIDs += 'http://purl.org/escidoc/metadata/terms/0.1/CONE|' + globalId.substring(globalId.indexOf('/journals/resource/'), globalId.length);
        }
        else {
          allIDs += 'http://purl.org/escidoc/metadata/terms/0.1/CONE|' + globalId;
        }
    }

    fillField('journalSuggest', title, parent);
    fillField('journalSuggestQuotes', '"' + title + '"', parent);
    fillField('sourceAltTitlePasteField', allAltTitles, parent);
    fillField('publisher', publisher, parent);
    fillField('place', place, parent);
    fillField('sourceIdentifierPasteField', allIDs, parent);
    
    $(parent).find('.hiddenAutosuggestUploadBtn').click();
}

function getPersonDetails(details) {
    var parent = $input.parents('.' + personSuggestCommonParentClass);
    var completeName = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
    var chosenName = $input.resultValue;
    var orgName = null;
    var orgId = null;
    var familyName = '';
    var givenName = '';

    if (chosenName.indexOf('(') >= 0) {
        orgName = chosenName.substring(chosenName.indexOf('(') + 1, chosenName.lastIndexOf(')')).replace(/^\s*(.*\S)\s*$/, '$1');
        chosenName = chosenName.substring(0, chosenName.indexOf('(')).replace(/^\s*(.*\S)\s*$/, '$1');
    }
    
    if (chosenName.indexOf(',') >= 0) {
        familyName = chosenName.split(',')[0].replace(/^\s*(.*\S)\s*$/, '$1');
        givenName = chosenName.split(',')[1].replace(/^\s*(.*\S)\s*$/, '$1');
    } else if (chosenName.indexOf(' ') >= 0) {
        var firstIndex = chosenName.indexOf(' ');
        familyName = chosenName.substring(0, firstIndex).replace(/^\s*(.*\S)\s*$/, '$1');
        givenName = chosenName.substring(firstIndex + 1).replace(/^\s*(.*\S)\s*$/, '$1');
    } else {
        familyName = chosenName;
    }
    
    if (orgName != null) {
        if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined' &&
            typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.length != 'undefined') {
            for (var i = 0; i < details.http_purl_org_escidoc_metadata_terms_0_1_position.length; i++) {
                if (details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_eprint_terms_affiliatedInstitution.replace(/^\s*(.*\S)\s*$/, '$1') == orgName &&
                    typeof details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_dc_elements_1_1_identifier != 'undefined') {
                    orgId = $.trim(details.http_purl_org_escidoc_metadata_terms_0_1_position[i].http_purl_org_dc_elements_1_1_identifier);
                    break;
                }
            }
        } else if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined' &&
            typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution != 'undefined' &&
            typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier != 'undefined' &&
            details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution.replace(/^\s*(.*\S)\s*$/, '$1') == orgName) {
            orgId = details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier;
        }
    } else {
        if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined' &&
            typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.length != 'undefined') {
            orgName = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position[0].http_purl_org_eprint_terms_affiliatedInstitution != 'undefined' ? details.http_purl_org_escidoc_metadata_terms_0_1_position[0].http_purl_org_eprint_terms_affiliatedInstitution : null);
            orgId = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position[0].http_purl_org_dc_elements_1_1_identifier != 'undefined' ? details.http_purl_org_escidoc_metadata_terms_0_1_position[0].http_purl_org_dc_elements_1_1_identifier : null);
        } else if (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position != 'undefined') {
            orgName = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution != 'undefined' ? details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_eprint_terms_affiliatedInstitution : null);
            orgId = (typeof details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier != 'undefined' ? details.http_purl_org_escidoc_metadata_terms_0_1_position.http_purl_org_dc_elements_1_1_identifier : null);
        }
    }
    
    
    var personId = $input.resultID;
    if (personId != null && personId != '') {
    	personId = personId.substring(personId.indexOf('/persons/resource/'), personId.length);
    }

    fillField('familyName', familyName, parent, true);
    fillField('givenName', givenName, parent, true);
    $input.blur();
    $input.focus();
    fillField('personIdentifier', personId, parent, true);
    $(parent).find('.removeAutoSuggestPerson').css('display', 'inline');
    $(parent).find('.givenName').attr('class', 'medium_txtInput givenName');

    if ($input.resultID != null && $input.resultID != '') {
        $(parent).find('.authorLink').replaceWith('<a href="' + $input.resultID + '" class="small_area0 authorCard authorLink xTiny_marginRExcl" target="_blank">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
    }

    // Try to disable input field
    $.each($(parent).find('.disableAfter'),
        function() {
            $(this).attr('readonly', 'readonly');
        }
    );

    var counter = 1;
    var found = false;
    var empty = true;
    var orgIdString = (orgId == null ? '' : orgId);

    $.each($input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationName'),
        function() {
            var otherOrgName = $(this).val();
            var otherOrgId = $(this).siblings('.organizationIdentifier').val();

            if (orgName == otherOrgName && orgIdString == otherOrgId) {
                fillField('ouNumber', counter + '', parent);
                found = true;
            } else if (otherOrgName != '') {
                empty = false;
            }
            counter++;
        }
    );

    if (!found) {
        if (counter == 2 && empty) {
            $input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationName').val(orgName == null ? "" : orgName);
            $input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationName').attr('title', (orgName == null ? "" : orgName));
            $input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationIdentifier').val(orgIdString == null ? "" : orgIdString);
            $input.parents('.itemBlockContent').find('.personOrganizations').find('.organizationIdentifier').attr('title', (orgIdString == null ? "" : orgIdString));

            if (orgIdString != null && orgIdString != '') {
                $input.parents('.itemBlockContent').find('.personOrganizations').find('.ouLink').replaceWith('<a href="#" onclick="openCenteredWindow(\'AffiliationDetailPage.jsp?id=' + orgIdString + '\', 980, 400, \'Details\');return false" class="small_area0 ouCard ouLink xTiny_marginRExcl">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
                updatePersonUi();
            }
            fillField('ouNumber', '1', parent);
        } else {
            $input.siblings('.organizationPasteField').val(orgIdString + autopasteInnerDelimiter + (orgName == null ? "" : orgName));
            $input.siblings('.organizationPasteField').attr('title', (orgIdString + autopasteInnerDelimiter + (orgName == null ? "" : orgName)));
            fillField('ouNumber', '' + counter, parent);
            $input.siblings('.hiddenButtonPasteOrganizations').click();
        }
    }
}

// removes 'readonly' attributes and resets fields for autosuggest
function removeAuthorAutoSuggest(element) {
    var $input = $(element);
    var parent = $input.parent();
    var field = null;
    if ($(parent).find('.personIdentifier').val() != '') {
        field = $(parent).find('.personIdentifier');
        field.removeAttr('readonly');
        fillField('personIdentifier', '', parent);
    }
    if ($(parent).find('.familyName').val() != '') {
        field = $(parent).find('.familyName');
        field.removeAttr('readonly');
        fillField('familyName', '', parent);
    }
    if ($(parent).find('.givenName').val() != '') {
        field = $(parent).find('.givenName');
        field.removeAttr('readonly');
        fillField('givenName', '', parent);
    }

    //Hide remove button
    $input.css('display', 'none');

    //Remove link to researcher portfolio with empty space
    $input.parent().find('.authorLink').replaceWith('<span class="xSmall_area0 authorLink xTiny_marginRExcl">&nbsp;</span>');
    //Enlarge givenName Field, because it was smaller before due to the remove button
    $input.parent().find('.givenName').attr('class', 'large_txtInput givenName');
    
    bindSuggests();
}

// removes 'readonly' attributes and resets fields for autosuggest
function removeOrganizationAutoSuggest(element) {
    var $input = $(element);
    var parent = $input.parent();
    var field = null;
    if ($(parent).find('.organizationIdentifier').val() != '') {
        field = $(parent).find('.organizationIdentifier');
        field.removeAttr('readonly');
        fillField('organizationIdentifier', '', parent);
    }
    if ($(parent).find('.organizationName').val() != '') {
        field = $(parent).find('.organizationName');
        field.removeAttr('readonly');
        fillField('organizationName', '', parent);
    }
    if ($(parent).find('.organizationAddress').val() != '') {
        field = $(parent).find('.organizationAddress');
        field.removeAttr('readonly');
        fillField('organizationAddress', '', parent);
    }

    $input.css('display', 'none');
    $input.parent().find('.organizationAddress').attr('class', 'xLarge_txtInput organizationAddress');
    $input.parent().find('.ouLink').replaceWith('<span class="xSmall_area0 ouLink xTiny_marginRExcl">&nbsp;</span>');
    
    bindSuggests();
}

// removes 'readonly' attributes and resets fields for autosuggest
function removeUserAccountAutoSuggest(element) {
    var $input = $(element);
    var parent = $input.parent();
    var field = null;
    if ($(parent).find('.userAccountIdentifier').val() != '') {
        field = $(parent).find('.userAccountIdentifier');
        field.removeAttr('readonly');
        fillField('userAccountIdentifier', '', parent);
    }
    if ($(parent).find('.userAccountName').val() != '') {
        field = $(parent).find('.userAccountName');
        field.removeAttr('readonly');
        fillField('userAccountName', '', parent);
    }

    $input.css('display', 'none');
    $input.parent().find('.userAccountName').attr('class', 'double_txtInput userAccountSuggest userAccountName disableAfter');

    bindSuggests();
}

function updatePersonUi() {
    // maintain attributes for autosuggest filled persons
    if ($('.personIdentifier' != null)) {
        $('.personIdentifier').each(function(ind) {
            if (this.value) {
                $(this).parents('.' + personSuggestCommonParentClass).find('.familyName').attr('readonly', 'readonly');
                $(this).parents('.' + personSuggestCommonParentClass).find('.givenName').attr('readonly', 'readonly');
                $(this).parents('.' + personSuggestCommonParentClass).find('.removeAutoSuggestPerson').css('display', 'inline');
                $(this).parents('.' + personSuggestCommonParentClass).find('.givenName').attr('class', 'medium_txtInput givenName');
            }
        });
    }

    // maintain attributes for autosuggest filled organizations
    if ($('.organizationIdentifier' != null)) {
        $('.organizationIdentifier').each(function(ind) {
            if (this.value) {
                $(this).parents('.' + personSuggestCommonParentClass).find('.organizationName').attr('readonly', 'readonly');
                $(this).parents('.' + personSuggestCommonParentClass).find('.organizationAddress').attr('readonly', 'readonly');
                $(this).parents('.' + personSuggestCommonParentClass).find('.removeAutoSuggestOrganization').css('display', 'inline');
                //$(this).parents('.' + personSuggestCommonParentClass).find('.removeAutoSuggestPerson').css('display', 'inline');
                $(this).parents('.' + personSuggestCommonParentClass).find('.organizationAddress').attr('class', 'large_txtInput organizationAddress');
            }
        });
    }

    if ($('.userAccountIdentifier' != null)) {
        $('.userAccountIdentifier').each(function(ind) {
            if (this.value) {
                $(this).parents('.' + personSuggestCommonParentClass).find('.userAccountName').attr('readonly', 'readonly');
                $(this).parents('.' + personSuggestCommonParentClass).find('.removeAutoSuggestUserAccount').css('display', 'inline');
                $(this).parents('.' + personSuggestCommonParentClass).find('.userAccountName').attr('class', 'large_txtInput userAccountName');
            }
        });
    }
}

function fillField(name, value, commonParent, readonly) {
    var field = $(commonParent).find('.' + name);

    if (field.length) {
        if (value != null) {
            field.val(value);
        } else {
            field.val('');
        }

        field.attr('title', value);

        if (typeof readonly != 'undefined') {
            field.unbind('keydown');
            field.unbind('keypress');
            field.attr('readonly', 'readonly');
        }
    }
}

function fillFields() {
    $input = $(this);
    globalId = this.resultID;
    $.getJSON(journalDetailsBaseURL.replace('$1', this.resultID), getJournalDetails);
}

function fillPersonFields() {
    $input = $(this);
    $input.resultValue = this.resultValue;
    $input.resultID = this.resultID;
    $input.resultLanguage = this.resultLanguage;
    if (typeof this.resultLanguage != 'undefined') {
        $.getJSON(personDetailsBaseURL.replace('$1', this.resultID).replace('$2', this.resultLanguage), getPersonDetails);
    } else {
        $.getJSON(personDetailsBaseURL.replace('$1', this.resultID).replace('$1', this.resultID).replace('$2', '*'), getPersonDetails);
    }
    $input.unbind('keydown');
    $input.unbind('keypress');
}

function fillOrganizationFields() {
    $input = $(this);
    var parent = $input.parents('.' + commonParentClass);
    fillField('organizationName', this.resultValue, parent);
    fillField('organizationIdentifier', this.resultID, parent);
    fillField('organizationAddress', this.result.address, parent);

    if (this.resultID != null && this.resultID != '') {
        $(parent).find('.ouLink').replaceWith('<a href="#" onclick="openCenteredWindow(\'/pubman/faces/AffiliationDetailPage.jsp?id=' + this.resultID + '\', 980, 400, \'Details\');return false" class="small_area0 ouCard ouLink xTiny_marginRExcl" target="_blank">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
    }

    // Try to disable input field
    $.each($(parent).find('.disableAfter'),
        function() {
            $(this).attr('readonly', 'readonly');
        }
    );

    updatePersonUi();
}

function fillUserAccountFields() {
    $input = $(this);
    var parent = $input.parents('.' + commonParentClass);
    fillField('userAccountName', this.resultValue, parent);
    fillField('userAccountIdentifier', this.resultID, parent);
    //fillField('organizationAddress', this.result.address, parent);

    /*
    if (this.resultID != null && this.resultID != '')
    {
    	$(parent).find('.ouLink').replaceWith('<a href="#" onclick="openCenteredWindow(\'/pubman/faces/AffiliationDetailPage.jsp?id=' + this.resultID + '\', 980, 400, \'Details\');return false" class="small_area0 ouCard ouLink xTiny_marginRExcl" target="_blank">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>');
    	
    }
    */
    // Try to disable input field
    $.each($(parent).find('.disableAfter'),
        function() {
            $(this).attr('readonly', 'readonly');
        }
    );

    updatePersonUi();
}

function fillFundingProgramFields() {
    $input = $(this);
    $input.resultValue = this.resultValue;
    $input.resultID = this.resultID;
    $.getJSON(fundingProgramDetailsBaseURL.replace('$1', this.resultID).replace('$1', this.resultID), getFundingProgramDetails);

    //$input.unbind('keydown');
    //$input.unbind('keypress');
}

function getFundingProgramDetails(details) {
    var parent = $input.parents('.' + commonParentClass);
    var programTitle = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
    var programIdentifier = (typeof details.http_purl_org_dc_elements_1_1_identifier != 'undefined' ? details.http_purl_org_dc_elements_1_1_identifier : null);
    var organizationTitle = (typeof details.http_purl_org_dc_elements_1_1_relation.http_purl_org_dc_elements_1_1_title != 'undefined' ?
        details.http_purl_org_dc_elements_1_1_relation.http_purl_org_dc_elements_1_1_title : null);
    var organizationIdentifier = (typeof details.http_purl_org_dc_elements_1_1_relation.http_purl_org_dc_elements_1_1_identifier != 'undefined' ?
        details.http_purl_org_dc_elements_1_1_relation.http_purl_org_dc_elements_1_1_identifier : null);

    fillField('fundingProgramTitle', programTitle, parent);
    fillField('fundingProgramIdentifier', programIdentifier, parent);
    fillField('fundingOrganizationTitle', organizationTitle, parent);
    fillField('fundingOrganizationIdentifier', organizationIdentifier, parent);
}

function fillFundingOrganizationFields() {
    $input = $(this);
    $input.resultValue = this.resultValue;
    $input.resultID = this.resultID;
    $.getJSON(fundingOrganizationDetailsBaseURL.replace('$1', this.resultID).replace('$1', this.resultID), getFundingOrganizationDetails);
}


function getFundingOrganizationDetails(details) {
    var parent = $input.parents('.' + commonParentClass);
    var organizationTitle = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ?
        details.http_purl_org_dc_elements_1_1_title : null);
    var organizationIdentifier = (typeof details.http_purl_org_dc_elements_1_1_identifier != 'undefined' ?
        details.http_purl_org_dc_elements_1_1_identifier : null);

    fillField('fundingOrganizationTitle', organizationTitle, parent);
    fillField('fundingOrganizationIdentifier', organizationIdentifier, parent);
}

function fillCitationStyleFields() {
    $input = $(this);
    $input.resultValue = this.resultValue;
    $input.resultID = this.resultID;

    $.getJSON(citationStyleSuggestBaseURL.replace('$1', this.resultID), getCitationStyleDetails);
}

function getCitationStyleDetails(details) {
    var parent = $input.parents('.' + commonParentClass);
    var citationTitle = (typeof details.http_purl_org_dc_elements_1_1_title != 'undefined' ? details.http_purl_org_dc_elements_1_1_title : null);
    //var citationValue = (typeof details.http_www_w3_org_1999_02_22_rdf_syntax_ns_value != 'undefined' ? details.http_www_w3_org_1999_02_22_rdf_syntax_ns_value : null);

    fillField('citationStyleName', citationTitle, parent);
    fillField('citationStyleIdentifier', $input.resultID, parent);

    $(parent).find('.removeAutoSuggestCsl').css('display', 'inline');
    $input.attr('readonly', 'readonly');
}

// removes 'readonly' attributes and resets fields for autosuggest
function removeCslAutoSuggest(element) {
    var $input = $(element);
    var parent = $input.parent();
    var field = null;
    
    if ($(parent).find('.citationStyleIdentifier').val() != '') {
        field = $(parent).find('.citationStyleIdentifier');
        field.removeAttr('readonly');
        fillField('citationStyleIdentifier', '', parent);
    }
    if ($(parent).find('.citationStyleName').val() != '') {
        field = $(parent).find('.citationStyleName');
        field.removeAttr('readonly');
        fillField('citationStyleName', '', parent);
    }

    //Hide remove button
    $input.css('display', 'none');

    bindSuggests();
}

function updateCslUi() {
    var cslIdentifier = $('.citationStyleIdentifier');
    // maintain attributes for autosuggest filled persons
    if (cslIdentifier && cslIdentifier.val()) {
        $('.citationStyleName').attr('readonly', 'readonly');
        $('.removeAutoSuggestCsl').css('display', 'inline');
    }
}

function bindJournalSuggest() {
    if (typeof journalSuggestURL != 'undefined') {
        //journalsuggestQuotes is used for advanced search, adds quotes around title
        $('.journalSuggest, .journalSuggestQuotes').suggest(journalSuggestURL, {
            onSelect: fillFields
        });
    }
}

function bindSuggests() {
    $('select.journalPulldown[value="' + journalSuggestTrigger + '"]').parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
    $('span.journalPulldown').find('input[type=hidden][value="' + journalSuggestTrigger + '"]').parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');

    $('select.journalPulldown').change(
        function() {
            if ($(this).val() == journalSuggestTrigger) {
                $(this).parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
            } else {
                $(this).parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
                $(this).parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
                $(this).parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown');
                $('.autoSuggestsArea').hide();
            };
            var t = window.setTimeout('bindJournalSuggest()', 500);
        });

    $('span.journalPulldown').find('input[type=hidden]').change(
        function() {
            if ($(this).val() == journalSuggestTrigger) {
                $(this).parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').addClass('journalSuggest');
            } else {
                $(this).parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').removeClass('journalSuggest');
                $(this).parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').unbind('keypress');
                $(this).parents('.' + journalSuggestCommonParentClass).find('.sourceTitle').unbind('keydown');
                $('.autoSuggestsArea').hide();
            };
            var t = window.setTimeout('bindJournalSuggest()', 500);
        });

    bindJournalSuggest();
    
    if (typeof languageSuggestURL != 'undefined') {
        $('.languageSuggest').suggest(languageSuggestURL, {
            onSelect: selectLanguage
        });
    }
    
    $('.subjectSuggest').each(
        function(i, ele) {
            if (typeof subjectSuggestURL != 'undefined') {
                $(ele).suggest(subjectSuggestURL, {
                    vocab: $(ele).parents('.subjectArea').find('.vocabulary'),
                    onSelect: function() {
                        $(this).val(this.resultValue);
                    }
                });
            }
        });

    $('.identifierSuggest').each(
            function(i, ele) {
                if (typeof identifierSuggestURL != 'undefined') {
                    $(ele).suggest(identifierSuggestURL, {
                        vocab: $(ele).parents('.identifierArea').find('.vocabulary'),
                        onSelect: function() {
							if (this.resultID.indexOf('rifsproject') > 0)
		                        $(this).val(this.resultID);
	                        else
	    	                    $(this).val(this.resultValue);
	                        }
                    });
                }
            });

    //for search, adds result in quotes
    $('.subjectSuggestQuotes').each(
        function(i, ele) {
            if (typeof subjectSuggestURL != 'undefined') {
                $(ele).suggest(subjectSuggestURL, {
                    vocab: $(ele).parents('.subjectArea').find('.vocabulary'),
                    onSelect: function() {
                        $(this).val('"' + this.resultValue + '"');
                    }
                });
            }
        });
    
    //for search, adds result in quotes
    $('.identifierSuggestQuotes').each(
        function(i, ele) {
            if (typeof identifierSuggestURL != 'undefined') {
                $(ele).suggest(identifierSuggestURL, {
                    vocab: $(ele).parents('.identifierArea').find('.vocabulary'),
                    onSelect: function() {
							if (this.resultID.indexOf('rifsproject') > 0)
						    $(this).val('"' + this.resultID + '"');
                        else
    	                    $(this).val('"' + this.resultValue + '"');
                    }
                });
            }
        });
    
    if (typeof personSuggestURL != 'undefined') {
        $('.personSuggest').each(
            function(i, ele) {
                if ($(ele).parent().find('.personIdentifier').val() == null || $(ele).parent().find('.personIdentifier').val() == '') {
                    $(ele).suggest(personSuggestURL, {
                        onSelect: fillPersonFields
                    });
                }
            }
        );
    }
    
    if (typeof organizationSuggestURL != 'undefined') {
        $('.organizationSuggest').suggest(organizationSuggestURL, {
            onSelect: fillOrganizationFields
        });
    }

    if (typeof userAccountSuggestURL != 'undefined') {
        $('.userAccountSuggest').suggest(userAccountSuggestURL, {
            onSelect: fillUserAccountFields
        });
    }

    if (typeof fundingProgramSuggestURL != 'undefined') {
        $('.fundingProgramSuggest').suggest(fundingProgramSuggestURL, {
            onSelect: fillFundingProgramFields
        });
    }

    if (typeof fundingOrganizationSuggestURL != 'undefined') {
        $('.fundingOrganizationSuggest').suggest(fundingOrganizationSuggestURL, {
            onSelect: fillFundingOrganizationFields
        });
    }

    if (typeof citationStyleSuggestURL != 'undefined') {
        $('.citationStyleSuggest').suggest(citationStyleSuggestURL, {
            onSelect: fillCitationStyleFields
        });
    }
};

function selectLanguage() {
    $input = $(this);
    if (($.trim(this.resultValue)).indexOf(' ') !== -1) {
        var langShortHand = $.trim(($.trim(this.resultValue)).substr(0, ($.trim(this.resultValue)).indexOf(' ')));
        if (langShortHand != '') {
            $input.val(langShortHand);
            $input.attr('title', langShortHand);
        }
        var lang = $.trim(($.trim(this.resultValue)).substr(($.trim(this.resultValue)).lastIndexOf(' ') + 1));
        if (lang != '') {
            $input.parents('.' + languageSuggestCommonParentClass).find('.languageText').val(lang);
            $input.parents('.' + languageSuggestCommonParentClass).find('.languageText').attr('title', lang);
        }
    }
}

$(function() {
    bindSuggests();
});