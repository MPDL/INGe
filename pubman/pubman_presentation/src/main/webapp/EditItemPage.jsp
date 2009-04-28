<?xml version="1.0" encoding="UTF-8"?>
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


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:tr="http://myfaces.apache.org/trinidad">

	<jsp:output doctype-root-element="html"
	       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" /> 

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
			<f:loadBundle var="genre" basename="#{EditItem.genreBundle}"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<jsp:directive.include file="header/ui/StandardImports.jspf" />


			</head>
			<body lang="#{InternationalizationHelper.locale}">

			<h:outputText id="pageDummy" value="#{EditItemPage.beanName}" styleClass="noDisplay" />
			<tr:form usesUpload="true">
			<div class="full wrapper">
			<h:inputHidden value="#{EditItemSessionBean.offset}" id="offset"></h:inputHidden>
			
				<jsp:directive.include file="header/Header.jspf" />

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<jsp:directive.include file="header/Breadcrumb.jspf" />
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.EditItemPage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
							<!-- content menu starts here -->
								<h:panelGroup layout="block" styleClass="free_area0 sub" rendered="#{!( (EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'IMPORT') )}">
								<!-- content menu lower line starts here -->										
									&#160;
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								
								<h:panelGroup layout="block" styleClass="free_area0 sub" rendered="#{( (EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'IMPORT') )}" >
									<h:commandLink title="#{tip.submission_lnkEasySubmission}" action="#{EasySubmission.newEasySubmission}" rendered="#{EditItemSessionBean.currentSubmission  != 'EASY_SUBMISSION'}">
										<h:outputText value="#{lbl.submission_lnkEasySubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
									</h:commandLink>
									<h:panelGroup styleClass=" " rendered="#{EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION'}">
										<h:outputText value="#{lbl.submission_lnkEasySubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
									</h:panelGroup>
									<h:outputText styleClass="seperator void" />
									<h:commandLink title="#{tip.submission_lnkNewSubmission}" action="#{CreateItem.newSubmission}" immediate="true" rendered="#{EditItemSessionBean.currentSubmission  != 'FULL_SUBMISSION'}">
										<h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:commandLink>
										<h:panelGroup styleClass=" " rendered="#{EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION'}">
										<h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:panelGroup>
									<h:outputText styleClass="seperator void" />
									<h:commandLink title="#{tip.submission_lnkImport}" action="#{EasySubmission.newImport}" rendered="#{EditItemSessionBean.currentSubmission  != 'IMPORT'}">
										<h:outputText value="#{lbl.submission_lnkImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
									</h:commandLink>
									<h:panelGroup styleClass=" " rendered="#{EditItemSessionBean.currentSubmission  == 'IMPORT'}">
										<h:outputText value="#{lbl.submission_lnkImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
									</h:panelGroup>
									<h:outputText styleClass="seperator void" />
									<h:commandLink title="#{tip.submission_lnkMultipleImport}" action="#{MultipleImport.newImport}">
										<h:outputText value="#{lbl.submission_lnkMultipleImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
									</h:commandLink>
									<h:outputText styleClass="seperator void" />
									<h:outputLink title="#{tip.submission_lnkImportWorkspace}" value="ImportWorkspace.jsp" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
										<h:outputText value="#{lbl.submission_lnkImportWorkspace}"/>
									</h:outputLink>
								</h:panelGroup>
							<!-- content menu ends here -->
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:outputText value="#{lbl.EditItem_lblCollectionOfItem} #{EditItem.contextName}." />
								<!-- Subheadline ends here -->
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{EditItem.hasErrorMessages}">
									<input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{EditItem.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea" rendered="#{EditItem.hasMessages and !EditItem.hasErrorMessages}">
									<input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{EditItem.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div class="full_area0 fullItem">
							<div class="full_area0 fullItemControls">
								<span class="full_area0_p5">
									<h:panelGroup styleClass="seperator" rendered="#{!(genre.sources_display == 'false' and genre.sources_form_id == 'full-submission' or  genre.sources_display == 'false' and genre.sources_form_id == 'all')}"/>
									<a class="free_area0" href="#editSource"><h:outputText value="#{genre.sources_source_basic_label}" converter="GenreLabelConverter" rendered="#{!(genre.sources_display == 'false' and genre.sources_form_id == 'full-submission' or  genre.sources_display == 'false' and genre.sources_form_id == 'all')}"/></a>
									<h:panelGroup styleClass="seperator" rendered="#{!(genre.events_display == 'false' and genre.events_form_id == 'full-submission' or  genre.events_display == 'false' and genre.events_form_id == 'all')}"/>
									<a class="free_area0" href="#editEvent"><h:outputText value="#{genre.events_label}" rendered="#{!(genre.events_display == 'false' and genre.events_form_id == 'full-submission' or  genre.events_display == 'false' and genre.events_form_id == 'all')}" converter="GenreLabelConverter"/></a>
									<h:panelGroup styleClass="seperator" rendered="#{!(genre.details_display == 'false' and genre.details_form_id == 'full-submission' or  genre.details_display == 'false' and genre.details_form_id == 'all')}"/>
									<a class="free_area0" href="#editDetail"><h:outputText value="#{genre.details_label}" rendered="#{!(genre.details_display == 'false' and genre.details_form_id == 'full-submission' or  genre.details_display == 'false' and genre.details_form_id == 'all')}" converter="GenreLabelConverter"/></a>
									<h:panelGroup styleClass="seperator" rendered="#{!(genre.content_display == 'false' and genre.content_form_id == 'full-submission' or  genre.content_display == 'false' and genre.content_form_id == 'all')}"/>
									<a class="free_area0" href="#editContent"><h:outputText value="#{genre.content_label}"  rendered="#{!(genre.content_display == 'false' and genre.content_form_id == 'full-submission' or  genre.content_display == 'false' and genre.content_form_id == 'all')}" converter="GenreLabelConverter"/></a>
									<h:panelGroup styleClass="seperator" rendered="#{!(genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'full-submission' or  genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'all')}"/>
									<a class="free_area0" href="#editPerson"><h:outputText value="#{genre.creator_person_organization_label}" rendered="#{!(genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'full-submission' or  genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'all')}" converter="GenreLabelConverter"/></a>
									<h:panelGroup styleClass="seperator" rendered="#{!(genre.locators_display == 'false' and genre.locators_form_id == 'full-submission' or  genre.locators_display == 'false' and genre.locators_form_id == 'all')}"/>
									<a class="free_area0" href="#editLocator"><h:outputText value="#{genre.locators_label}" converter="GenreLabelConverter" rendered="#{!(genre.locators_display == 'false' and genre.locators_form_id == 'full-submission' or  genre.locators_display == 'false' and genre.locators_form_id == 'all')}"/></a>
									<h:panelGroup styleClass="seperator" rendered="#{!(genre.files_display == 'false' and genre.files_form_id == 'full-submission' or  genre.files_display == 'false' and genre.files_form_id == 'all')}"/>
									<a class="free_area0" href="#editFile"><h:outputText value="#{genre.files_label}" converter="GenreLabelConverter" rendered="#{!(genre.files_display == 'false' and genre.files_form_id == 'full-submission' or  genre.files_display == 'false' and genre.files_form_id == 'all')}"/></a>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
								</span>
							</div>

						 	<jsp:directive.include file="editItem/BasicGroup.jspf" />
							<jsp:directive.include file="editItem/FilesGroup.jspf" />
							<jsp:directive.include file="editItem/LocatorsGroup.jspf" />
							<jsp:directive.include file="editItem/PersOrgGroup.jspf" />
							<jsp:directive.include file="editItem/ContentGroup.jspf" />
							<jsp:directive.include file="editItem/DetailGroup.jspf" />
							<jsp:directive.include file="editItem/EventGroup.jspf" />
							<jsp:directive.include file="editItem/SourceGroup.jspf" />

							<div class="free_area0 xTiny_marginLIncl"><h:outputText value="* #{msg.mandatoryField}" styleClass="mandatory"/></div>
						 
						</div>
						<div class="full_area0 formButtonArea">
							<h:commandLink styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" id="lnkCancel" value="#{lbl.EditItem_lnkCancel}" action="#{EditItem.cancel}"/>
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkDelete" binding ="#{EditItem.lnkDelete}" immediate="true"  value="#{lbl.EditItem_lnkDelete}" onmousedown="if(!confirmDelete('form1:EditItem'))return false;" action="#{EditItem.delete}"/>
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkRelease" binding ="#{EditItem.lnkRelease}"  value="#{lbl.actionMenu_lnkRelease}" action="#{EditItem.saveAndSubmit}"/>
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkAccept" binding ="#{EditItem.lnkAccept}"  value="#{lbl.EditItem_lnkAccept}" action="#{EditItem.saveAndAccept}"/>
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSaveAndSubmit" binding ="#{EditItem.lnkSaveAndSubmit}"  value="#{lbl.EditItem_lnkSaveAndSubmit}" action="#{EditItem.saveAndSubmit}"/>
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSave" binding ="#{EditItem.lnkSave}"  value="#{lbl.EditItem_lnkSave}" action="#{EditItem.save}"/>							
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkValidate"  value="#{lbl.EditItem_lnkValidate}" action="#{EditItem.validate}"/>
						</div>
					</div>
				<!-- end: content section -->
				</div>
			
			</div>
			<jsp:directive.include file="footer/Footer.jspf" />
			</tr:form>
			<script type="text/javascript">
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
				});
			
				languageSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/languages/query';
				journalSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/journals/query';
				subjectSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/ddc/query';
				personSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/persons/query';
				journalDetailsBaseURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/journals/details?id=';
				personDetailsBaseURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/persons/details?id=';
				journalSuggestCommonParentClass = 'itemBlock';
				personSuggestCommonParentClass = 'suggestAnchor';
				journalSuggestTrigger = 'JOURNAL';
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>