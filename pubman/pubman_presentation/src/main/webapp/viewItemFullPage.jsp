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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:rich="http://richfaces.org/rich" xmlns:a4j="http://richfaces.org/a4j"   xmlns:fn="http://java.sun.com/jsp/jstl/functions">

	<jsp:output doctype-root-element="html"
	       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" /> 

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
	<f:loadBundle var="lbl" basename="Label"/>
	<f:loadBundle var="msg" basename="Messages"/>
	<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>

				<title><h:outputText value="#{ViewItemFull.pubItem.metadata.title.value} :: #{ApplicationBean.appTitle}" converter="HTMLTitleSubSupConverter" /></title>
				<link rel="unapi-server" type="application/xml" title="unAPI" href="#{ViewItemFull.unapiURLview}"/>
				<meta name="description" content="#{ViewItemFull.pubItem.descriptionMetaTag}"></meta>
				<jsp:directive.include file="header/ui/StandardImports.jspf" />

			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText value="#{ViewItemFullPage.beanName}" styleClass="noDisplay" />
			<!-- The unAPI Identifier for this item -->
			<h:outputText value="&lt;abbr class='unapi-id' title='#{ViewItemFull.pubItem.version.objectIdAndVersion}'&gt;&lt;/abbr&gt;" escape="false" rendered="#{ViewItemFull.pubItem != null and ViewItemFull.isStateReleased}"/>

			<h:form >
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
								<h1><h:outputText value="#{lbl.ViewItemPage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<h:panelGroup layout="block" styleClass="contentMenu" rendered="#{ViewItemFull.pubItem != null}">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu upper line starts here -->
									<h:outputLink id="lnkLinkForActionsView" styleClass="free_area0" value="#{ViewItemFull.linkForActionsView}" rendered="#{ViewItemSessionBean.subMenu != 'ACTIONS'}" ><h:outputText value="#{lbl.ViewItemFull_lblItemActions}"/></h:outputLink>
									<h:outputText styleClass="free_area0" value="#{lbl.ViewItemFull_lblItemActions}" rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}" />
									<h:outputText styleClass="seperator void" />
									<h:outputLink id="lnkLinkForExportView" styleClass="free_area0" value="#{ViewItemFull.linkForExportView}" rendered="#{ViewItemSessionBean.subMenu != 'EXPORT' and !ViewItemFull.isStateWithdrawn}"><h:outputText value="#{lbl.List_lblExportOptions}"/></h:outputLink>
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblExportOptions}" rendered="#{ViewItemSessionBean.subMenu == 'EXPORT' and !ViewItemFull.isStateWithdrawn}" />
								<!-- content menu upper line ends here -->
								</div>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}">
								<!-- content menu lower line starts here -->										
									<h:commandLink id="lnkEdit" action="#{ViewItemFull.editItem}" value="#{lbl.actionMenu_lnkEdit}" rendered="#{ViewItemFull.canEdit}"/>
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canEdit}"/>
									
									<h:commandLink id="lnkSubmit" action="#{ViewItemFull.submitItem}" value="#{lbl.actionMenu_lnkSubmit}" rendered="#{ViewItemFull.canSubmit}"/>
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canSubmit}"/>

									<h:commandLink id="lnkRelease" action="#{ViewItemFull.submitItem}" value="#{lbl.actionMenu_lnkRelease}" rendered="#{ViewItemFull.canRelease}"/>
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canRelease}"/>

									<h:commandLink id="lnkAccept" action="#{ViewItemFull.acceptItem}" value="#{lbl.actionMenu_lnkAccept}" rendered="#{ViewItemFull.canAccept}"/>
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canAccept}"/>

									<h:commandLink id="lnkRevise" action="#{ViewItemFull.reviseItem}" value="#{lbl.actionMenu_lnkRevise}" rendered="#{ViewItemFull.canRevise}"/>
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canRevise}"/>

									<h:commandLink id="lnkDelete" onclick="if(!confirm('#{msg.deleteMessage}'))return false;" value="#{lbl.actionMenu_lnkDelete}" action="#{ViewItemFull.deleteItem}" rendered="#{ViewItemFull.canDelete}" />
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canDelete}" />

									<h:commandLink id="lnkWithdraw" action="#{ViewItemFull.withdrawItem}" value="#{lbl.actionMenu_lnkWithdraw}" rendered="#{ViewItemFull.canWithdraw}" />
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canWithdraw}" />

									<h:commandLink id="lnkModify" action="#{ViewItemFull.modifyItem}" value="#{lbl.actionMenu_lnkModify}" rendered="#{ViewItemFull.canModify}" />
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canModify}" />

									<h:commandLink id="lnkCreateNewRevision" action="#{ViewItemFull.createNewRevision}" value="#{lbl.actionMenu_lnkCreateNewRevision}" rendered="#{ViewItemFull.canCreateNewRevision}" />
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canCreateNewRevision}" />

									<h:commandLink id="lnkCreateItemFromTemplate" action="#{ItemControllerSessionBean.createItemFromTemplate}" value="#{lbl.ViewItemFull_lblCreateItemFromTemplate}" rendered="#{ViewItemFull.canCreateFromTemplate}" />
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canCreateFromTemplate}" />

									<h:commandLink id="lnkAddToBasket" action="#{ViewItemFull.addToBasket}" value="#{lbl.ViewItemFull_lblAddToBasket}" rendered="#{ViewItemFull.canAddToBasket}" />
									<h:commandLink id="lnkDeleteFromBasket" action="#{ViewItemFull.removeFromBasket}" value="#{lbl.ViewItemFull_lblRemoveFromBasket}" rendered="#{ViewItemFull.canDeleteFromBasket}" />

									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.isCandidateOfYearbook}" />
									<h:commandLink id="lnkAddToYearbook" styleClass="free_area0" value="#{lbl.Yearbook_addToYearbookViewItem}" type="reset" action="#{ViewItemFull.addToYearbookMember}" immediate="true" rendered="#{ViewItemFull.isCandidateOfYearbook}" />

									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.isMemberOfYearbook}" />
									<h:commandLink id="lnkRemoveFromYearbook" styleClass="free_area0" value="#{lbl.Yearbook_removeFromYearbookViewItem}" action="#{ViewItemFull.removeMemberFromYearbook}" rendered="#{ViewItemFull.isMemberOfYearbook}" />


								<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{ViewItemSessionBean.subMenu == 'EXPORT'}">
									
									<h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
										<h:panelGroup layout="block" styleClass="xLarge_area0">
											<h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="selEXPORTFORMAT" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.exportFormatName}" onchange="$pb(this).parents('.sub').find('.exportUpdateButton').click();">
											<f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}" />
										</h:selectOneMenu>
									</h:panelGroup>
								<!-- 
									<h:selectOneMenu id="selEXPORTFORMAT" value="#{ExportItemsSessionBean.exportFormatName}" styleClass="xLarge_select replace" onchange="$pb(this).parents('.sub').find('.exportUpdateButton').click();">
											 <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}"/>
									</h:selectOneMenu>	-->
									
									<h:commandButton styleClass="noDisplay exportUpdateButton" action="#{ExportItems.updateExportFormats}" value="updateExportFormats" />	
									
									<h:panelGroup layout="block" styleClass="medium_area1 endline selectContainer" rendered="#{ExportItemsSessionBean.enableFileFormats}">
										<h:panelGroup layout="block" styleClass="medium_area0">
											<h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="selFILEFORMAT" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.fileFormat}" onchange="updateSelectionBox(this);">
											<f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}" />
										</h:selectOneMenu>
									</h:panelGroup>
								<!-- <% /*  
									<h:selectOneMenu id="selFILEFORMAT" value="#{ExportItemsSessionBean.fileFormat}" styleClass="medium_select replace" rendered="#{ExportItemsSessionBean.enableFileFormats}">
										<f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}"/>
									</h:selectOneMenu>	*/ %> -->
					<!-- <% /* 	</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{ViewItemSessionBean.subMenu == 'EXPORT'}">
								 */ %> -->
									<!-- <% /*
									<h:commandButton id="btnDisplayItems" styleClass="free_area0" value="#{lbl.export_btDisplay}" action="#{ViewItemFull.exportDisplay}"/>
									<h:outputText styleClass="seperator" />
									*/ %> -->
									<h:commandLink id="btnExportDownload" styleClass="free_area0" value="#{lbl.export_btDownload}" action="#{ViewItemFull.exportDownload}" />
									<h:outputText styleClass="seperator" />
									<h:commandLink id="btnExportEMail" styleClass="free_area0" value="#{lbl.export_btEMail}" action="#{ViewItemFull.exportEmail}"/>
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								
							<!-- content menu ends here -->
							</h:panelGroup>
						
						<h:panelGroup layout="block" styleClass="subHeader" rendered="#{ViewItemFull.isLoggedIn }">
							<!-- Subheadline starts here -->
							<h:outputText value="#{lbl.EditItem_lblItemVersionID} '#{ViewItemFull.pubItem.version.objectIdAndVersion}'." rendered="#{ViewItemFull.pubItem.version.objectIdAndVersion != null}"/><br/>
							<h:outputText value="#{lbl.EditItem_lblCollectionOfItem} '#{ViewItemFull.contextName}', #{lbl.ViewItemFull_lblIsAffiliatedTo}: '#{ViewItemFull.affiliations}'." /><br/>
							<h:outputText value="#{lbl.EditItem_lblItemDepositor} '#{ViewItemFull.owner}'" rendered="#{ViewItemFull.owner != null }"/>
							<h:outputText value="." rendered="#{ViewItemFull.owner != null and ViewItemFull.creationDate == null}"/>
							<h:outputText value=" --- #{ViewItemFull.creationDate}" rendered="#{ViewItemFull.creationDate != null}"/><br/>
							<h:outputText value="#{lbl.EditItem_lblItemLastModifier} '#{ViewItemFull.lastModifier}'" rendered="#{ViewItemFull.lastModifier != null}"/>
							<h:outputText value="." rendered="#{ViewItemFull.lastModifier != null and ViewItemFull.modificationDate == null}"/>
							<h:outputText value=" --- #{ViewItemFull.modificationDate}" rendered="#{ViewItemFull.modificationDate != null}"/>
						</h:panelGroup>	
						<h:panelGroup layout="block" styleClass="subHeader" rendered="#{ViewItemFull.isStateInRevision}">
							<h:outputText value="#{msg.ViewItemFull_inRevision} #{ViewItemFull.pubItem.version.lastMessage}" rendered="#{ViewItemFull.canShowLastMessage}" />
							<h:outputText value="#{msg.ViewItemFull_inRevision} #{lbl.lbl_noEntry}" rendered="#{!ViewItemFull.canShowLastMessage}" />
						</h:panelGroup>
						<h:panelGroup layout="block" styleClass="subHeader" rendered="#{ViewItemFull.isStateSubmitted}">
							<h:outputText value="#{msg.ViewItemFull_submitted} #{ViewItemFull.pubItem.version.lastMessage}" rendered="#{ViewItemFull.canShowLastMessage}" />
							<h:outputText value="#{msg.ViewItemFull_submitted} #{lbl.lbl_noEntry}" rendered="#{!ViewItemFull.canShowLastMessage}" />
						</h:panelGroup>
						<div class="subHeader">
							<!-- JSF messages -->
							<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{ViewItemFull.numberOfMessages == 1}"/>
							<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
								<h2><h:outputText  value="#{lbl.warning_lblMessageHeader}"/></h2>
								<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{ViewItemFull.hasMessages}"/>
							</h:panelGroup>
							<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{ViewItemFull.hasMessages and !ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
								<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
								<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{ViewItemFull.hasMessages}"/>
							</h:panelGroup>
							<!-- Special validation messages for yearbook -->
							<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea clear" style="padding-top: 0px !important;" rendered="#{ViewItemFull.pubItem.validationReport!=null}">
								<h2><h:outputText value="#{lbl.Yearbook_validationMessageHeader}"/></h2>
								<ul>
								<a4j:repeat var="valitem" value="#{ViewItemFull.pubItem.validationReport.items}">
									<h:panelGroup rendered="#{valitem.restrictive}">
										<li class="messageWarn">
										<h:outputText value="#{msg[valitem.content]}"/>
										</li>
									</h:panelGroup>
									<h:panelGroup rendered="#{!valitem.restrictive}">
										<li class="messageStatus">
										<h:outputText value="#{msg[valitem.content]}"/>
										</li>
									</h:panelGroup>
								</a4j:repeat>
								</ul>	
					   		</h:panelGroup>
						   	<!-- Survey link -->
						   	<h:panelGroup layout="block" style="margin-top:1em;" rendered="#{not empty HomePage.surveyUrl}">
								<div class="xHuge_area2_p6 messageArea">
									<span class="half_area0">
										<h2><h:outputText value="#{HomePage.surveyTitle}"/></h2>
									</span>
									<span class="huge_area0"> 
										<h:outputText value="#{HomePage.surveyText}"/>
									</span> 
									<span class="free_area0">
									<div class="medium_area2_p6 small_marginLExcl">
									
									<h:outputLink  styleClass="activeButton" value="#{HomePage.surveyUrl}" title="User Survey" target="_blank">
										<h:outputText value="User Survey"/>
									</h:outputLink>
										</div>
									</span>
								</div>
							</h:panelGroup>
							<!-- Subheadline ends here -->
						</div>
					</div>
				</div>			
				<h:panelGroup layout="block" styleClass="full_area0" rendered="#{ViewItemFull.pubItem != null}">
					<div class="full_area0 fullItem">
						<div class="full_area0 fullItemControls">
							<span class="full_area0_p5">
								<b class="free_area0 small_marginLExcl">&#160;<h:outputText styleClass="messageError" value="#{msg.ViewItemFull_withdrawn}" rendered="#{ViewItemFull.isStateWithdrawn}" /></b>
								<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canViewLocalTags}" />
								<h:outputLink id="lnkViewLocalTagsPage" styleClass="free_area0" value="#{ApplicationBean.appContext}ViewLocalTagsPage.jsp" rendered="#{ViewItemFull.canViewLocalTags}">
									<h:outputText value="#{lbl.ViewItemFull_lblSubHeaderLocalTags}" />
								</h:outputLink>
								<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canManageAudience}" />
								<h:commandLink id="lnkManageAudience" styleClass="free_area0" action="#{AudienceBean.manageAudience}" rendered="#{ViewItemFull.canManageAudience}">
									<h:outputText value="#{lbl.AudiencePage}" />
								</h:commandLink>
								<h:panelGroup styleClass="seperator" rendered="false" />
								<h:outputLink id="lnkCollaboratorPage" styleClass="free_area0" value="#{ApplicationBean.appContext}CollaboratorPage.jsp" rendered="false">
									<h:outputText value="#{lbl.CollaboratorPage}" />
								</h:outputLink>
								<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canShowItemLog}" />
								<h:commandLink id="lnkViewItemLogPage" styleClass="free_area0" action="#{ViewItemFull.showItemLog}" rendered="#{ViewItemFull.canShowItemLog}">
									<h:outputText value="#{lbl.ViewItemLogPage}" />
								</h:commandLink>
								<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canShowStatistics}" />
								<h:commandLink id="lnkViewItemFull_btnItemStatistics" styleClass="free_area0" action="#{ViewItemFull.showStatistics}" rendered="#{ViewItemFull.canShowStatistics}">
									<h:outputText value="#{lbl.ViewItemFull_btnItemStatistics}" />
								</h:commandLink>
								<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canShowRevisions}" />
								<h:commandLink id="lnkViewItemFull_btnItemRevisions" styleClass="free_area0" action="#{ViewItemFull.showRevisions}" rendered="#{ViewItemFull.canShowRevisions}">
									<h:outputText value="#{lbl.ViewItemFull_btnItemRevisions}" />
								</h:commandLink>
								<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canShowReleaseHistory}" />
								<h:commandLink id="lnkViewItemFull_btnItemVersions" styleClass="free_area0" action="#{ViewItemFull.showReleaseHistory}" rendered="#{ViewItemFull.canShowReleaseHistory}">
									<h:outputText value="#{lbl.ViewItemFull_btnItemVersions}" />
								</h:commandLink>
								<h:panelGroup styleClass="seperator" />
								<h:outputLink id="lnkViewItemPage" styleClass="free_area0 actual" value="#contentSkipLinkAnchor">
									<h:outputText value="#{lbl.ViewItemFull_btnItemView}" />
								</h:outputLink>
								<h:panelGroup styleClass="seperator" />
							</span>
						</div>
						<h:panelGroup styleClass="full_area0 pageBrowser">
							<h:commandLink  id="btList_lkPreviousBottom" styleClass="backward" action="#{PubItemListSessionBean.previousItem}" rendered="#{BreadcrumbItemHistorySessionBean.previousItemName == 'SearchResultListPage'}"  >
								<h:outputText value="#{lbl.List_lkPrevious}"/>
					 		</h:commandLink>
							<h:commandLink  id="btList_lkNextBottom" styleClass="forward" style="float:right;" action="#{PubItemListSessionBean.nextItem}" rendered="#{BreadcrumbItemHistorySessionBean.previousItemName == 'SearchResultListPage'}" >
								<h:outputText value="#{tip.List_lkNext}"/>
							</h:commandLink>
						</h:panelGroup>
						<div class="full_area0 itemHeader">
							
							<h:panelGroup styleClass="xLarge_area0 endline blockHeader">
								&#160;	
							</h:panelGroup>
							<h:panelGroup styleClass="seperator" />
							<h:panelGroup styleClass="free_area0_p8 endline itemHeadline">
								<b><h:outputText value="#{ViewItemFull.pubItem.metadata.title.value}" converter="HTMLSubSupConverter" escape="false"/></b>
								<h:outputText value="#{ViewItemFull.citationHtml}" escape="false"/>
							</h:panelGroup>
							<h:panelGroup layout="block" styleClass="medium_area0_p4 statusArea" >
								<h:panelGroup styleClass="big_imgArea xSmall_marginLExcl withdrawnItem" rendered="#{ViewItemFull.isStateWithdrawn}" />
								<h:panelGroup styleClass="big_imgArea xSmall_marginLExcl pendingItem" rendered="#{ViewItemFull.isStatePending}" />
								<h:panelGroup styleClass="big_imgArea xSmall_marginLExcl submittedItem" rendered="#{ViewItemFull.isStateSubmitted}" />
								<h:panelGroup styleClass="big_imgArea xSmall_marginLExcl releasedItem" rendered="#{ViewItemFull.isStateReleased}" />
								<h:panelGroup styleClass="big_imgArea xSmall_marginLExcl inRevisionItem" rendered="#{ViewItemFull.isStateInRevision}" />
								<h:outputText styleClass="noDisplay" value="Item is " />
								<h:outputLabel styleClass="medium_label endline" style="text-align: center;" rendered="#{ViewItemFull.isStateWithdrawn}">
									<h:outputText value="#{ViewItemFull.itemPublicState}" />
								</h:outputLabel>
								<h:outputLabel styleClass="medium_label endline" style="text-align: center;" rendered="#{!ViewItemFull.isStateWithdrawn}">
									<h:outputText value="#{ViewItemFull.itemState}" />
								</h:outputLabel>
							</h:panelGroup>
						</div>
						
						<h:panelGroup layout="block" styleClass="full_area0 itemBlock noTopBorder" rendered="#{ViewItemFull.isLoggedIn}">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								&#160;
							</h3>
							<div class="free_area0 itemBlockContent endline">
								<b class="xLarge_area0_p8 endline labelLine clear">
									<h:outputText value="#{lbl.ViewItem_lblModeratorContact}" /><span class="noDisplay">: </span>
								</b>
								<span class="xHuge_area0 xTiny_marginLExcl endline">
									<h:outputLink id="lnkModeratorContactEmail" value="mailto:#{ViewItemFull.moderatorContactEmail}?subject=#{ViewItemFull.pubItem.version.objectIdAndVersion}" rendered="#{ViewItemFull.isLoggedIn}"><h:outputText value="#{lbl.ViewItem_lnkModeratorEmail}" /></h:outputLink>
								</span>
							</div>
						</h:panelGroup>
						<h:panelGroup layout="block" styleClass="full_area0 itemBlock visibility" rendered="#{!ViewItemFull.isStateWithdrawn}">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								&#160;
							</h3>
							<h:panelGroup styleClass="seperator" />
							<a class="free_area0 expand"><h:outputText value="#{lbl.ViewItemFull_lblShowGroup} #{lbl.ViewItemFull_lblAll}" /></a>
							<a class="free_area0 collapse"><h:outputText value="#{lbl.ViewItemFull_lblHideGroup} #{lbl.ViewItemFull_lblAll}" /></a>
						</h:panelGroup> 

							<jsp:directive.include file="viewItem/BasicGroup.jspf" />
							<jsp:directive.include file="viewItem/FilesGroup.jspf" />
							<jsp:directive.include file="viewItem/LocatorsGroup.jspf" />
							<jsp:directive.include file="viewItem/PersOrgGroup.jspf" />
							<jsp:directive.include file="viewItem/ContentGroup.jspf" />
							<jsp:directive.include file="viewItem/DetailGroup.jspf" />
							<jsp:directive.include file="viewItem/EventGroup.jspf" />
							<!--JUS content section -->
							<jsp:directive.include file="viewItem/LegalCaseGroup.jspf" />
							<jsp:directive.include file="viewItem/SourceGroup.jspf" />
							<jsp:directive.include file="viewItem/WithdrawnGroup.jspf" />

						</div>
					</h:panelGroup>
				<!-- end: content section -->
				</div>
			</div>
			<jsp:directive.include file="footer/Footer.jspf" />
			</h:form>
			
			</body>
		</html>
	</f:view>
</jsp:root>
