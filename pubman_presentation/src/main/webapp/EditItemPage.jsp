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
				
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<link href="../../cone/js/jquery.suggest.css" rel="stylesheet" type="text/css" />

				<link href="./resources/eSciDoc_CSS_v2/main.css" type="text/css" rel="stylesheet"/>
				<link rel="SHORTCUT ICON" href="./images/escidoc.ico"/>
				<meta http-equiv="pragma" content="no-cache"/>
				<meta http-equiv="cache-control" content="no-cache"/>
				<meta http-equiv="expires" content="0"/>
				<script type="text/javascript" language="JavaScript" src="resources/scripts.js">;</script>
				
				<script src="./resources/eSciDoc_JavaScript/eSciDoc_javascript.js" language="JavaScript" type="text/javascript">;</script>


			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<tr:form usesUpload="true">
			<h:inputHidden id="offset"></h:inputHidden>
			<!-- start: skip link navigation -->
				<h:outputLink styleClass="skipLink" title="skip link" value="#mainMenuSkipLinkAnchor">
					<h:outputText value="Skip to the main menu"/>
				</h:outputLink>
				<h:outputLink styleClass="skipLink" title="skip link" value="#contentSkipLinkAnchor">
					<h:outputText value="Skip to the page content"/>
				</h:outputLink>
				<h:outputLink styleClass="skipLink" title="skip link" value="#searchMenuSkipLinkAnchor">
					<h:outputText value="Skip to the search menu"/>
				</h:outputLink>
				<h:outputLink styleClass="skipLink" title="skip link" value="#metaMenuSkipLinkAnchor">
					<h:outputText value="Skip to the meta menu"/>
				</h:outputLink>
			<!-- end: skip link navigation -->
			
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
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{EditItem.hasErrorMessages}">
									<input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{EditItem.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{EditItem.hasMessages and !EditItem.hasErrorMessages}">
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
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<a class="free_area0" href="#editSource"><h:outputText value="#{lbl.EditItem_lblSource}" /></a>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<a class="free_area0" href="#editEvent"><h:outputText value="#{lbl.EditItem_lblEvent}"/></a>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<a class="free_area0" href="#editDetail"><h:outputText value="#{lbl.EditItem_lblDetails}" /></a>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<a class="free_area0" href="#editContent"><h:outputText value="#{lbl.EditItem_lblContent}" /></a>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<a class="free_area0" href="#editPerson"><h:outputText value="#{lbl.EditItem_lblPersonsAndOrganizations}" /></a>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<a class="free_area0" href="#editLocator"><h:outputText value="#{lbl.EditItem_lblFileLoc}" /></a>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<a class="free_area0" href="#editFile"><h:outputText value="#{lbl.EditItem_lblFile}" /></a>
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
			
			</tr:form>
			<script type="text/javascript">
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
				});
			
				languageSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/lang/query';
				journalSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/jnar/query';
				journalDetailsBaseURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/jnar/details?id=';
				journalSuggestCommonParentClass = 'itemBlock';
				journalSuggestTrigger = 'JOURNAL';
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>