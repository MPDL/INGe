<!DOCTYPE html>
<!--

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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->



	 

	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
				
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<meta http-equiv="cache-control" content="no-cache" />
				<meta http-equiv="Pragma" content="no-cache" />
				<meta http-equiv="expires" content="0"/>
				<ui:include src="header/ui/StandardImports.jspf" />
				
				
			</h:head>
			<!-- Use onunload here in order to hinder browser to cache the page -->
			<body lang="${InternationalizationHelper.locale}">
			
			<h:outputText value="#{AdvancedSearchPage.beanName}" styleClass="noDisplay" />
			<h:form id="form1">	
			<div class="full wrapper">
			<h:inputHidden id="offset"/>
			
				<ui:include src="header/Header.jspf" />

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
							<ui:include src="header/Breadcrumb.jspf" />
							
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.search_lblAdvancedSearch}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<h:panelGroup layout="block" styleClass="small_marginLIncl subHeaderSection" rendered="#{AdvancedSearchPage.numberOfMessages > 0}">
							<h:panelGroup layout="block" styleClass="contentMenu" rendered="false">
							<!-- content menu starts here -->
								<h:panelGroup layout="block" styleClass="free_area0 sub">
								<!-- content menu lower line starts here -->										
									&#160;
								<!-- content menu lower line ends here -->
								</h:panelGroup>
							<!-- content menu ends here -->
							</h:panelGroup>
							 
							<h:panelGroup layout="block" styleClass="subHeader" rendered="false">
								<!-- Subheadline starts here -->
									&#160;
								<!-- Subheadline ends here -->
							</h:panelGroup>
							<h:panelGroup id="messages" styleClass="subHeader">
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{AdvancedSearchBean.hasErrorMessages}">
									<input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}" /></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{AdvancedSearchBean.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea" rendered="#{AdvancedSearchBean.hasMessages and !AdvancedSearchBean.hasErrorMessages}">
									<input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}" /></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{AdvancedSearchBean.hasMessages}"/>
								</h:panelGroup>
										
							</h:panelGroup> 
							<!-- Subheadline ends here -->
						</h:panelGroup>
					</div>			
					<div class="full_area0">
						<div class="full_area0 fullItem">
							<ui:include src="search/AdvancedSearchEdit.jspf" />
						</div>
					</div>
					<div class="full_area0 formButtonArea">
							<h:commandLink id="lnkAdvancedSearchClearAll" styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" value="#{lbl.adv_search_btClearAll}" action="#{AdvancedSearchBean.clearAndInit}" onclick="fullItemReloadAjax();"/>
							<h:commandLink id="lnkAdvancedSearchStartSearch" styleClass="free_area1_p8 activeButton" value="#{lbl.adv_search_btStart}" action="#{AdvancedSearchBean.startSearch}" onclick="fullItemReloadAjax();"/>
						</div>
				<!-- end: content section -->
				</div>
			</div>
			<ui:include src="footer/Footer.jspf" />
			</h:form>
			<script type="text/javascript">
				function checkUpdatePersonFunction() {
					(typeof updatePersonUi == 'function') ?	updatePersonUi() :	setTimeout("checkUpdatePersonFunction()", 30);
				}
				
				$(document).ready(function () {
					/*
					$("input[id$='offset']").submit(function() {
						$(this).val($(window).scrollTop());
					});
					*/
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});


					
					checkUpdatePersonFunction();
					
					
				});
				languageSuggestURL = '<h:outputText value="#{AdvancedSearchEdit.suggestConeUrl}"/>iso639-3/query?format=json';
				personSuggestURL = '<h:outputText value="#{AdvancedSearchEdit.suggestConeUrl}"/>persons/query?lang=*';
				/*languageDetailsBaseURL = '$1?format=json';*/
				languageDetailsBaseURL = '$1?format=json&amp;lang=$2';
				organizationSuggestURL = 'OrganizationSuggest.jsp';
				personDetailsBaseURL = '$1?format=json&amp;lang=$2';
				subjectSuggestURL = '<h:outputText value="#{AdvancedSearchEdit.suggestConeUrl}"/>$1/query?lang=en';
				journalSuggestURL = '<h:outputText value="#{AdvancedSearchEdit.suggestConeUrl}"/>journals/query';
				journalSuggestTrigger = 'JOURNAL';
				
				
			</script>
			</body>
		</html>
	</f:view>
