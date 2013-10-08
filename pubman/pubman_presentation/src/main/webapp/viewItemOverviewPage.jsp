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


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:rich="http://richfaces.org/rich"
	xmlns:a4j="http://richfaces.org/a4j"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8"
		pageEncoding="UTF-8" />
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label" />
		<f:loadBundle var="msg" basename="Messages" />
		<f:loadBundle var="tip" basename="Tooltip" />
		<html xmlns="http://www.w3.org/1999/xhtml">
	
			<head>
				<title>
					<h:outputText
					value="#{ViewItemFull.pubItem.metadata.title.value} :: #{ApplicationBean.appTitle}"
					converter="HTMLTitleSubSupConverter" />
				</title>
				<link rel="unapi-server" type="application/xml" title="unAPI"
					href="#{ViewItemFull.unapiURLview}" />
				<meta name="description"
					content="#{ViewItemFull.pubItem.descriptionMetaTag}"/>
				<jsp:directive.include file="header/ui/StandardImports.jspf" />
				<style type="text/css">
					/* START NanoScroller */
					.nano {
						position: relative;
						width: 100%;
						height: 100%;
						overflow: hidden;
					}
					.nano .content {
						position: absolute;
						overflow: scroll;
						overflow-x: hidden;
						top: 0;
					/*	right: 0; */
						bottom: 0;
					/*	left: 0; */
						width: 100%;
					}
					.nano.has-scrollbar .content {
						padding-right:20px !important;
					}
					.nano .content:focus {
						outline: thin dotted;
					}
					.nano .content::-webkit-scrollbar {
						visibility: hidden;
					}
					.has-scrollbar .content::-webkit-scrollbar {
						visibility: visible;
					}
					.nano > .pane {
						background: rgba(0,0,0,.25);
						position: absolute;
						width: 10px;
						right: 0;
						padding-right: 0 !important;
						top: 0;
						bottom: 0;
						visibility: hidden\9; /* Target only IE7 and IE8 with this hack */
						opacity: .01; 
						-webkit-transition: .2s;
						-moz-transition: .2s;
						-o-transition: .2s;
						transition: .2s;
						-moz-border-radius: 5px;
						-webkit-border-radius: 5px;	
						border-radius: 5px;
					}
					.nano > .pane > .slider {
						background: #444;
						background: rgba(0,0,0,.5);
						position: relative;
						margin: 0 1px;
						padding-right: 0 !important;
						-moz-border-radius: 3px;
						-webkit-border-radius: 3px;	
						border-radius: 3px;
					}
					.nano:hover > .pane, .pane.active, .pane.flashed {
						visibility: visible\9; /* Target only IE7 and IE8 with this hack */
						opacity: 0.99;
					}
					.tile_category .nano div {
						display: block;
					}
					.tile_category .nano .content div{
						display: inline-block;
					}
					/* END NanoScroller */
					.tile-category {
						content:
					}
					.tile_category {
						margin-top: 1.363636em;
						border-top-color: #FFFFFF;
						border-top-style: solid;
						border-top-width: 9px !important;
						background-color: #CBE0E5;
						border-radius: 6px 6px 6px 6px;
					}
					.colorBackground {
						background-color: #CBE0E5;
						border-radius: 6px 6px 6px 6px;
					}
					.tile_category img[align="left"] {
						clear:left;
					} 
					.tile_category div {
						line-height: 150%;
						display: inline-block;
						margin: 0 0 1em 0; 
						*clear:both; /* IE7 Hack */
					}
					.tile_category div p {
						margin-left: 0;
					}
					.tile_category div .big_imgBtn[align="left"] {
						*margin-bottom: 0.5em; /* IE7 Hack */
					}
					.tile_category div .large_imgBtn[align="right"],
					.tile_category div .big_imgBtn[align="right"] {
						margin-left: 0.545454em;
						display: block;
					}
					.tile_category.borderDarkTurquoise {
						border-top-color: #006f7b;
					}
					.tile_category.borderDarkBlue {
						border-top-color: #163d7a;
					}
					.tile_category.borderDarkGreen {
						border-top-color: #008354;
					}
					.tile_category.borderYellow {
						border-top-color: #cec200;
					}
					.tile_category.borderRed {
						border-top-color: #be0031;
					}
					.tile_category.borderOrange {
						border-top-color: #ff8304;
					}
					.tile_category.borderBrown {
						border-top-color: #6e1e16;
					}
					.tile_category .tile_title {
						font-size: 1.454545em;
						display: block;
						margin: 0.454545em 0;
					}
					.tile_category .tile_title img {
						width: 1.9375em;
						height: 1.9375em;
					}
					.tile_category h4.tile_publication_title {
						font-size: 1.909090909090909em;
						margin: 0.454545em 0;
						line-height: normal;
						text-align: center;
					}
					.tile_category .tile_publication_title img {
						width: 1.476190476190476em;
						height: 1.476190476190476em;
					}
					.tile_category h5.tile_citation_title {
						font-size: 2.181818em;
						margin: 0.454545em 0;
					}
					.tile_category .tile_citation_title img {
						width: 1.291666666666667em;
						height: 1.291666666666667em;
					}
					.tile_category .author_name {
						font-weight: bold;
					}
					.tile_category .author_organization {
						font-style: italic;
					}
					/*TODO add to eSciDoc_core_em.css*/
					.large_imgBtn 
					{
						width: 4.272727em;
						height: 4.272727em;
					}
					.third_area0, .third_area0_p0 
					{
						float:left;
					    width: 47.7272727272em;
					}
					.xxTiny_marginRExcl{
						margin-right: 0.9090909090909091em;
					}
					/* OverviewPage: if third_area0 contains two huge_areas */
					.huge_area0.xTiny_marginRExcl.small_marginLExcl + .third_area0 {
						width: 47.909090em;
					}
					.third_area0_p6 
					{
					    width: 46.636363636363em;
					}
					.third_area0_p6 
					{
					    border-width: 0 !important;
					    display: inline;
					    float: left;
					    margin: 0 0.0909em 0 0;
					    overflow: hidden;
					    padding-bottom: 0.5454em;
					    padding-left: 0.5454em !important;
					    padding-right: 0.5454em !important;
					    padding-top: 0.5454em;
					}
					.filled_area0, .filled_area0_p0 
					{
						float:left;
					    width: 72.27272727em;
					}
					.filled_area0_p6 
					{
					    width: 71em;
					}
					.filled_area0_p6 
					{
					    border-width: 0 !important;
					    display: inline;
					    float: left;
					    margin: 0 0.0909em 0 0;
					    overflow: hidden;
					    padding-bottom: 0.5454em;
					    padding-left: 0.5454em !important;
					    padding-right: 0.5454em !important;
					    padding-top: 0.5454em;
					}
					
					/*BOTTOM MARGINS*/
					.xTiny_marginBExcl 
					{
					    margin-bottom: 1.3636em;
					}
				</style>
			</head>

			<body lang="#{InternationalizationHelper.locale}">
				<h:outputText value="#{ViewItemFullPage.beanName}"
					styleClass="noDisplay" />
				<!-- The unAPI Identifier for this item -->
				<h:outputText
					value="&lt;abbr class='unapi-id' title='#{ViewItemFull.pubItem.version.objectIdAndVersion}'&gt;&lt;/abbr&gt;"
					escape="false"
					rendered="#{ViewItemFull.pubItem != null and ViewItemFull.isStateReleased}" />
			
				<h:form>
					<div class="full wrapper">
						<h:inputHidden id="offset"></h:inputHidden>
			
						<jsp:directive.include file="header/Header.jspf" />
			
						<div id="content" class="full_area0 clear">
							<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
							<div class="clear">
								<div class="headerSection">
									<jsp:directive.include file="header/Breadcrumb.jspf" />
			
									<div id="contentSkipLinkAnchor" class="clear headLine">
										<!-- Headline starts here -->
										<h1>
											<h:outputText value="#{lbl.ViewItemPage}" />
										</h1>
										<!-- Headline ends here -->
									</div>
								</div>
								<div class="small_marginLIncl subHeaderSection">
									
									<!-- content menu starts here -->
									<h:panelGroup layout="block" styleClass="contentMenu"
										rendered="#{ViewItemFull.pubItem != null}">
										
										<!-- content menu upper line starts here -->
										<div class="free_area0 sub">
											<h:outputLink id="lnkLinkForActionsView" styleClass="free_area0"
												value="#{ViewItemFull.linkForActionsView}"
												rendered="#{ViewItemSessionBean.subMenu != 'ACTIONS'}">
												<h:outputText value="#{lbl.ViewItemFull_lblItemActions}" />
											</h:outputLink>
											<h:outputText styleClass="free_area0"
												value="#{lbl.ViewItemFull_lblItemActions}"
												rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}" />
											<h:outputText styleClass="seperator void" />
											<h:outputLink id="lnkLinkForExportView" styleClass="free_area0"
												value="#{ViewItemFull.linkForExportView}"
												rendered="#{ViewItemSessionBean.subMenu != 'EXPORT' and !ViewItemFull.isStateWithdrawn}">
												<h:outputText value="#{lbl.List_lblExportOptions}" />
											</h:outputLink>
											<h:outputText styleClass="free_area0"
												value="#{lbl.List_lblExportOptions}"
												rendered="#{ViewItemSessionBean.subMenu == 'EXPORT' and !ViewItemFull.isStateWithdrawn}" />
										</div>
										<!-- content menu upper line ends here -->
										
										<!-- content menu lower line (actions) starts here -->
										<h:panelGroup layout="block" styleClass="free_area0 sub action"
											rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}">
											<h:commandLink id="lnkEdit" action="#{ViewItemFull.editItem}"
												value="#{lbl.actionMenu_lnkEdit}"
												rendered="#{ViewItemFull.canEdit}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canEdit}" />
											<h:commandLink id="lnkSubmit"
												action="#{ViewItemFull.submitItem}"
												value="#{lbl.actionMenu_lnkSubmit}"
												rendered="#{ViewItemFull.canSubmit}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canSubmit}" />
											<h:commandLink id="lnkRelease"
												action="#{ViewItemFull.submitItem}"
												value="#{lbl.actionMenu_lnkRelease}"
												rendered="#{ViewItemFull.canRelease}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canRelease}" />
											<h:commandLink id="lnkAccept"
												action="#{ViewItemFull.acceptItem}"
												value="#{lbl.actionMenu_lnkAccept}"
												rendered="#{ViewItemFull.canAccept}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canAccept}" />
											<h:commandLink id="lnkRevise"
												action="#{ViewItemFull.reviseItem}"
												value="#{lbl.actionMenu_lnkRevise}"
												rendered="#{ViewItemFull.canRevise}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canRevise}" />
											<h:commandLink id="lnkDelete"
												onclick="if(!confirm('#{msg.deleteMessage}'))return false;"
												value="#{lbl.actionMenu_lnkDelete}"
												action="#{ViewItemFull.deleteItem}"
												rendered="#{ViewItemFull.canDelete}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canDelete}" />
											<h:commandLink id="lnkWithdraw"
												action="#{ViewItemFull.withdrawItem}"
												value="#{lbl.actionMenu_lnkWithdraw}"
												rendered="#{ViewItemFull.canWithdraw}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canWithdraw}" />
											<h:commandLink id="lnkModify"
												action="#{ViewItemFull.modifyItem}"
												value="#{lbl.actionMenu_lnkModify}"
												rendered="#{ViewItemFull.canModify}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canModify}" />
											<h:commandLink id="lnkCreateNewRevision"
												action="#{ViewItemFull.createNewRevision}"
												value="#{lbl.actionMenu_lnkCreateNewRevision}"
												rendered="#{ViewItemFull.canCreateNewRevision}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canCreateNewRevision}" />
											<h:commandLink id="lnkCreateItemFromTemplate"
												action="#{ItemControllerSessionBean.createItemFromTemplate}"
												value="#{lbl.ViewItemFull_lblCreateItemFromTemplate}"
												rendered="#{ViewItemFull.canCreateFromTemplate}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canCreateFromTemplate}" />
											<h:commandLink id="lnkAddToBasket"
												action="#{ViewItemFull.addToBasket}"
												value="#{lbl.ViewItemFull_lblAddToBasket}"
												rendered="#{ViewItemFull.canAddToBasket}" />
											<h:commandLink id="lnkDeleteFromBasket"
												action="#{ViewItemFull.removeFromBasket}"
												value="#{lbl.ViewItemFull_lblRemoveFromBasket}"
												rendered="#{ViewItemFull.canDeleteFromBasket}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.isCandidateOfYearbook}" />
											<h:commandLink id="lnkAddToYearbook" styleClass="free_area0"
												value="#{lbl.Yearbook_addToYearbookViewItem}" type="reset"
												action="#{ViewItemFull.addToYearbookMember}" immediate="true"
												rendered="#{ViewItemFull.isCandidateOfYearbook}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.isMemberOfYearbook}" />
											<h:commandLink id="lnkRemoveFromYearbook"
												styleClass="free_area0"
												value="#{lbl.Yearbook_removeFromYearbookViewItem}"
												action="#{ViewItemFull.removeMemberFromYearbook}"
												rendered="#{ViewItemFull.isMemberOfYearbook}" />
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.ssrnContext and !ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" />
											<h:commandLink id="lnkAddSsrn" styleClass="free_area0"
												title="#{tip.ViewItemFull_lblAddSsrn }"
												action="#{ViewItemFull.addSsrnTag}"
												rendered="#{ViewItemFull.ssrnContext and !ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}">
												<h:panelGroup styleClass="min_imgBtn add" />
												<h:outputText value="#{lbl.ViewItemFull_lblSSRN}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.ssrnContext and ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" />
											<h:commandLink id="lnkRemoveSsrn" styleClass="free_area0"
												title="#{tip.ViewItemFull_lblRemoveSsrn }"
												action="#{ViewItemFull.removeSsrnTag}"
												rendered="#{ViewItemFull.ssrnContext and ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}">
												<h:panelGroup styleClass="min_imgBtn remove" />
												<h:outputText value="#{lbl.ViewItemFull_lblSSRN}" />
											</h:commandLink>
											
										</h:panelGroup>
										<!-- content menu lower line (actions) ends here -->
										
										<!-- content menu lower line (export) starts here -->
										<h:panelGroup layout="block" styleClass="free_area0 sub action"
											rendered="#{ViewItemSessionBean.subMenu == 'EXPORT'}">
											<h:panelGroup layout="block"
												styleClass="xLarge_area1 endline selectContainer">
												<h:panelGroup layout="block" styleClass="xLarge_area0">
													<h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
													<h:panelGroup layout="block"
														styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
												</h:panelGroup>
												<h:selectOneMenu id="selEXPORTFORMAT" styleClass="replace"
													onfocus="updateSelectionBox(this);"
													value="#{ExportItemsSessionBean.exportFormatName}"
													onchange="$pb(this).parents('.sub').find('.exportUpdateButton').click();">
													<f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}" />
												</h:selectOneMenu>
											</h:panelGroup>
											<!-- 
												<h:selectOneMenu id="selEXPORTFORMAT" value="#{ExportItemsSessionBean.exportFormatName}" styleClass="xLarge_select replace" onchange="$pb(this).parents('.sub').find('.exportUpdateButton').click();">
														 <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}"/>
												</h:selectOneMenu>	-->
											<h:commandButton styleClass="noDisplay exportUpdateButton"
												action="#{ExportItems.updateExportFormats}"
												value="updateExportFormats" />
											<h:panelGroup layout="block"
												styleClass="medium_area1 endline selectContainer"
												rendered="#{ExportItemsSessionBean.enableFileFormats}">
												<h:panelGroup layout="block" styleClass="medium_area0">
													<h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
													<h:panelGroup layout="block"
														styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
												</h:panelGroup>
												<h:selectOneMenu id="selFILEFORMAT" styleClass="replace"
													onfocus="updateSelectionBox(this);"
													value="#{ExportItemsSessionBean.fileFormat}"
													onchange="updateSelectionBox(this);">
													<f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}" />
												</h:selectOneMenu>
											</h:panelGroup>
											<!-- <%/*  
			                             <h:selectOneMenu id="selFILEFORMAT" value="#{ExportItemsSessionBean.fileFormat}" styleClass="medium_select replace" rendered="#{ExportItemsSessionBean.enableFileFormats}">
			                             <f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}"/>
			                             </h:selectOneMenu>	*/%> -->
											<!-- <%/* 	</h:panelGroup>
			                             <h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{ViewItemSessionBean.subMenu == 'EXPORT'}">
			                             */%> -->
											<!-- <%/*
			                             <h:commandButton id="btnDisplayItems" styleClass="free_area0" value="#{lbl.export_btDisplay}" action="#{ViewItemFull.exportDisplay}"/>
			                             <h:outputText styleClass="seperator" />
			                             */%> -->
											<h:commandLink id="btnExportDownload" styleClass="free_area0"
												value="#{lbl.export_btDownload}"
												action="#{ViewItemFull.exportDownload}" />
											<h:outputText styleClass="seperator" />
											<h:commandLink id="btnExportEMail" styleClass="free_area0"
												value="#{lbl.export_btEMail}"
												action="#{ViewItemFull.exportEmail}" />
										</h:panelGroup>
										<!-- content menu lower line (export) starts here -->
										
									</h:panelGroup>
									<!-- content menu ends here -->
			
									<!-- Subheadline starts here -->
									<h:panelGroup layout="block" styleClass="subHeader"
										rendered="#{ViewItemFull.isLoggedIn }">
										<h:outputText
											value="#{lbl.EditItem_lblItemVersionID} '#{ViewItemFull.pubItem.version.objectIdAndVersion}'."
											rendered="#{ViewItemFull.pubItem.version.objectIdAndVersion != null}" />
										<br />
										<h:outputText
											value="#{lbl.EditItem_lblCollectionOfItem} '#{ViewItemFull.contextName}', #{lbl.ViewItemFull_lblIsAffiliatedTo}: '#{ViewItemFull.affiliations}'." />
										<br />
										<h:outputText
											value="#{lbl.EditItem_lblItemDepositor} '#{ViewItemFull.owner}'"
											rendered="#{ViewItemFull.owner != null }" />
										<h:outputText value="."
											rendered="#{ViewItemFull.owner != null and ViewItemFull.creationDate == null}" />
										<h:outputText value=" --- #{ViewItemFull.creationDate}"
											rendered="#{ViewItemFull.creationDate != null and ViewItemFull.owner != null }" />
										<h:outputText
											value="#{lbl.EditItem_lblItemlatestChange } #{ViewItemFull.creationDate}"
											rendered="#{ViewItemFull.creationDate != null and ViewItemFull.owner == null }" />
										<br />
										<h:outputText
											value="#{lbl.EditItem_lblItemLatestModifier} '#{ViewItemFull.latestModifier}'"
											rendered="#{ViewItemFull.latestModifier != null}" />
										<h:outputText value="."
											rendered="#{ViewItemFull.latestModifier != null and ViewItemFull.modificationDate == null}" />
										<h:outputText value=" --- #{ViewItemFull.modificationDate}"
											rendered="#{ViewItemFull.modificationDate != null and ViewItemFull.latestModifier != null }" />
										<h:outputText
											value="#{lbl.EditItem_lblItemLatestModification} #{ViewItemFull.modificationDate}"
											rendered="#{ViewItemFull.modificationDate != null and ViewItemFull.latestModifier == null }" />
										<br />
										<h:outputText
											value="#{msg.ViewItemFull_latestMessage} #{ViewItemFull.pubItem.version.lastMessage}"
											rendered="#{ViewItemFull.canShowLastMessage}" />
										<h:outputText
											value="#{msg.ViewItemFull_latestMessage} #{lbl.lbl_noEntry}"
											rendered="#{!ViewItemFull.canShowLastMessage}" />
									</h:panelGroup>
									<!-- Subheadline ends here -->
									
									<!-- JSF messages -->
									<div class="subHeader">
										<h:messages styleClass="singleMessage" errorClass="messageError"
											warnClass="messageWarn" fatalClass="messageFatal"
											infoClass="messageStatus" layout="list" globalOnly="false"
											showDetail="false" showSummary="true"
											rendered="#{ViewItemFull.numberOfMessages == 1}" />
										<h:panelGroup layout="block"
											styleClass="half_area2_p6 messageArea errorMessageArea"
											rendered="#{ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
											<h2>
												<h:outputText value="#{lbl.warning_lblMessageHeader}" />
											</h2>
											<h:messages errorClass="messageError" warnClass="messageWarn"
												fatalClass="messageFatal" infoClass="messageStatus"
												layout="list" globalOnly="false" showDetail="false"
												showSummary="true" rendered="#{ViewItemFull.hasMessages}" />
										</h:panelGroup>
										<h:panelGroup layout="block"
											styleClass="half_area2_p6 messageArea infoMessageArea"
											rendered="#{ViewItemFull.hasMessages and !ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
											<h2>
												<h:outputText value="#{lbl.info_lblMessageHeader}" />
											</h2>
											<h:messages errorClass="messageError" warnClass="messageWarn"
												fatalClass="messageFatal" infoClass="messageStatus"
												layout="list" globalOnly="false" showDetail="false"
												showSummary="true" rendered="#{ViewItemFull.hasMessages}" />
										</h:panelGroup>
										<!-- Special validation messages for yearbook -->
										<h:panelGroup layout="block"
											styleClass="half_area2_p6 messageArea errorMessageArea clear"
											style="padding-top: 0px !important;"
											rendered="#{ViewItemFull.pubItem.validationReport!=null}">
											<h2>
												<h:outputText value="#{lbl.Yearbook_validationMessageHeader}" />
											</h2>
											<ul>
												<a4j:repeat var="valitem"
													value="#{ViewItemFull.pubItem.validationReport.items}">
													<h:panelGroup rendered="#{valitem.restrictive}">
														<li class="messageWarn"><h:outputText
																value="#{msg[valitem.content]}" /></li>
													</h:panelGroup>
													<h:panelGroup rendered="#{!valitem.restrictive}">
														<li class="messageStatus"><h:outputText
																value="#{msg[valitem.content]}" /></li>
													</h:panelGroup>
												</a4j:repeat>
											</ul>
										</h:panelGroup>
										<!-- Survey link -->
										<h:panelGroup layout="block" style="margin-top:1em;"
											rendered="#{not empty HomePage.surveyUrl}">
											<div class="xHuge_area2_p6 messageArea">
												<span class="half_area0">
													<h2>
														<h:outputText value="#{HomePage.surveyTitle}" />
													</h2>
												</span> <span class="huge_area0"> <h:outputText
														value="#{HomePage.surveyText}" />
												</span> <span class="free_area0">
													<div class="medium_area2_p6 small_marginLExcl">
			
														<h:outputLink styleClass="activeButton"
															value="#{HomePage.surveyUrl}" title="User Survey"
															target="_blank">
															<h:outputText value="User Survey" />
														</h:outputLink>
													</div>
												</span>
											</div>
										</h:panelGroup>
									</div>
									<!-- Subheadline ends here -->
									
								</div>
							</div>
							<h:panelGroup layout="block" styleClass="full_area0"
								rendered="#{ViewItemFull.pubItem != null}">
								<div class="full_area0 fullItem">
									
									<!-- Item control information starts here -->
									<div class="full_area0 fullItemControls">
										<span class="full_area0_p5"> <b
											class="free_area0 small_marginLExcl">&#160;
											<h:outputText
													styleClass="messageError"
													value="#{msg.ViewItemFull_withdrawn}"
													rendered="#{ViewItemFull.isStateWithdrawn}" /></b> 
											<h:panelGroup
												styleClass="seperator"
												rendered="#{ViewItemFull.canViewLocalTags}" /> 
											<h:outputLink
												id="lnkViewLocalTagsPage" styleClass="free_area0"
												value="#{ApplicationBean.appContext}ViewLocalTagsPage.jsp"
												rendered="#{ViewItemFull.canViewLocalTags}">
												<h:outputText value="#{lbl.ViewItemFull_lblSubHeaderLocalTags}" />
											</h:outputLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canManageAudience}" />
												<h:commandLink
													id="lnkManageAudience" styleClass="free_area0"
													action="#{AudienceBean.manageAudience}"
													rendered="#{ViewItemFull.canManageAudience}">
													<h:outputText value="#{lbl.AudiencePage}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator" rendered="false" />
											<h:outputLink
												id="lnkCollaboratorPage" styleClass="free_area0"
												value="#{ApplicationBean.appContext}CollaboratorPage.jsp"
												rendered="false">
												<h:outputText value="#{lbl.CollaboratorPage}" />
											</h:outputLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canShowItemLog}" />
											<h:commandLink
												id="lnkViewItemLogPage" styleClass="free_area0"
												action="#{ViewItemFull.showItemLog}"
												rendered="#{ViewItemFull.canShowItemLog}">
												<h:outputText value="#{lbl.ViewItemLogPage}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canShowStatistics}" />
											<h:commandLink
												id="lnkViewItemFull_btnItemStatistics" styleClass="free_area0"
												action="#{ViewItemFull.showStatistics}"
												rendered="#{ViewItemFull.canShowStatistics}">
												<h:outputText value="#{lbl.ViewItemFull_btnItemStatistics}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canShowRevisions}" />
											<h:commandLink
												id="lnkViewItemFull_btnItemRevisions" styleClass="free_area0"
												action="#{ViewItemFull.showRevisions}"
												rendered="#{ViewItemFull.canShowRevisions}">
												<h:outputText value="#{lbl.ViewItemFull_btnItemRevisions}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator"
												rendered="#{ViewItemFull.canShowReleaseHistory}" />
											<h:commandLink
												id="lnkViewItemFull_btnItemVersions" styleClass="free_area0"
												action="#{ViewItemFull.showReleaseHistory}"
												rendered="#{ViewItemFull.canShowReleaseHistory}">
												<h:outputText value="#{lbl.ViewItemFull_btnItemVersions}" />
											</h:commandLink>
											<h:panelGroup styleClass="seperator" />
											<h:outputLink
												id="lnkViewItemPage" styleClass="free_area0"
												value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}viewItemFullPage.jsp?itemId=#{ViewItemFull.pubItem.version.objectIdAndVersion}">
												<h:outputText value="#{lbl.ViewItemFull_btnItemView}" />
											</h:outputLink>
											<h:panelGroup styleClass="seperator" />
											<h:outputLink id="lnkViewItemOverviewPage" 
												styleClass="free_area0 actual" 
												value="#contentSkipLinkAnchor">
												<h:outputText value="#{lbl.ViewItemOverview_lblLinkOverviewPage}" />
											</h:outputLink>
											<h:panelGroup styleClass="seperator" />
										</span>
									</div>
									<!-- Item control information ends here -->

									<!-- Paginator starts here -->
									<h:panelGroup styleClass="full_area0 pageBrowserItem">
										<h:panelGroup styleClass="paginatorPanel">
											<h:commandLink id="btList_lkFirstListItem"
												styleClass="min_imgBtn skipToFirst"
												action="#{PubItemListSessionBean.firstListItem}"
												rendered="#{PubItemListSessionBean.hasPreviousListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">&#160;</h:commandLink>
											<h:commandLink id="btList_lkPreviousListItem"
												styleClass="backward"
												action="#{PubItemListSessionBean.previousListItem}"
												rendered="#{PubItemListSessionBean.hasPreviousListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
												<h:outputText value="#{lbl.List_lkPrevious}" />
											</h:commandLink>
											<h:commandLink id="btList_lkNextListItem" styleClass="forward"
												action="#{PubItemListSessionBean.nextListItem}"
												rendered="#{PubItemListSessionBean.hasNextListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
												<h:outputText value="#{tip.List_lkNext}" />
											</h:commandLink>
											<h:commandLink id="btList_lkLastListItem"
												styleClass="min_imgBtn skipToLast"
												action="#{PubItemListSessionBean.lastListItem}"
												rendered="#{PubItemListSessionBean.hasNextListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">&#160;</h:commandLink>
										</h:panelGroup>
										<h:panelGroup styleClass="gotoBox"
											rendered="#{(PubItemListSessionBean.hasNextListItem or PubItemListSessionBean.hasPreviousListItem) and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
											<h:inputText id="inputItemListPosition"
												styleClass="tiny_txtInput"
												value="#{PubItemListSessionBean.listItemPosition}"
												label="GoTo Box" />
											<h:outputLabel id="lblItemListPosition" styleClass="free_label"
												value="#{lbl.ItemList_of} " />
											<h:outputLabel id="lblChangeItemListPosition"
												styleClass="free_label"
												value="#{PubItemListSessionBean.totalNumberOfElements}" />
											<h:commandButton id="btnGoToItemListPosition"
												styleClass="xTiny_txtBtn pageChangeHiddenBtn"
												value="#{lbl.List_btGo}" title="#{lbl.List_btGo}"
												action="#{PubItemListSessionBean.listItemPosition}" />
										</h:panelGroup>
									</h:panelGroup>
									<!-- Paginator ends here -->
									<!-- ItemView starts here -->
									<h:panelGroup layout="block" styleClass="full_area0" >
										<jsp:directive.include file="viewItemOverview/titleGroup.jspf" />
										<h:panelGroup layout="block" styleClass="huge_area0 xTiny_marginRExcl small_marginLExcl" style="overflow:visible;">
											<jsp:directive.include file="viewItemOverview/authorGroup.jspf" />
											<jsp:directive.include file="viewItemOverview/externalResourceGroup.jspf" />
										</h:panelGroup>
										<h:panelGroup layout="block" styleClass="third_area0">
											<jsp:directive.include file="viewItemOverview/fulltextGroup.jspf" />
											<jsp:directive.include file="viewItemOverview/supplementaryMaterialGroup.jspf" />
											<jsp:directive.include file="viewItemOverview/citationGroup.jspf" />
											<jsp:directive.include file="viewItemOverview/abstractGroup.jspf" />
										</h:panelGroup>
									</h:panelGroup>
									<!-- ItemView ends here -->
								</div>
							</h:panelGroup>
							<!-- end: content section -->
						</div>
					</div>
					<jsp:directive.include file="footer/Footer.jspf" />
				</h:form>
			</body>
			<script language="javascript" type="text/javascript">
				$pb(document).ready(function () {
					startNanoScrollerWhenLoaded();
					$pb('.tile_category a').mouseenter(function (evt) {
					    $pb(this).parent().css({"overflow": "visible"})
					});

					$pb('.tile_category a').mouseleave(function (evt) {
					    $pb(this).parent().css({"overflow": "hidden"})
					});
					replaceAuthorImage();
				});
				// NanoScroller
				var counter = 0;
				var startNanoScrollerTimeout;
				// Add NanoScroller (Scrollbar only visible when hovering the marked div)
				function startNanoScrollerWhenLoaded () {
					clearTimeout(startNanoScrollerTimeout);
					switch (typeof $pb.fn.nanoScroller) {
						case 'function':
							var nanoDiv = $pb(".nano");
							// IE lower 9 will get no NanoScroller attached. Instead a standard scrollbar is added
							if (!$pb.browser.msie || ($pb.browser.msie &amp;&amp; $pb.browser.version &gt; 8)) {
								nanoDiv.nanoScroller();
							} else {
								nanoDiv.css("overflow", "auto");
								nanoDiv.removeClass("nano");
							}
							break;
						default:
							counter++;
							if (counter &lt; 10) {
								startNanoScrollerTimeout = setTimeout(startNanoScrollerWhenLoaded, 100);		
							}
							break;
					}
				}
				// replaces the standard image with the cone image
				function replaceAuthorImage() {
					var url;
					var jsonRequestUrl;
					var imgElement;
					$pb('.mpgAuthorId').each(function (index) {
						url = $pb(this).text();
						jsonRequestUrl = url+'?format=json';
						imgElement = $pb(this).parent().find('img').get(0);
					    updateImage(imgElement, jsonRequestUrl);
						
					});
				}
				
				function updateImage(imgElement, jsonRequestUrl) {	
					$pb.getJSON(jsonRequestUrl, function (result) {
						console.log(result);
						console.log("");
						var pictureUrl =  result.http_xmlns_com_foaf_0_1_depiction;
						if (pictureUrl != undefined &amp;&amp; $pb.trim().pictureUrl != '') {
							$pb(imgElement).attr('src', pictureUrl);
						}
					});
				}
			</script>
		</html>
	</f:view>
</jsp:root>
