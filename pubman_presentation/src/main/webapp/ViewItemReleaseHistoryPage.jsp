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

				<title><h:outputText value="#{ApplicationBean.appTitle} #{ViewItemFull.pubItem.metadata.title.value}"/></title>

				<jsp:directive.include file="header/ui/StandardImports.jspf" />


			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText id="pageDummy" value="#{ViewItemReleaseHistoryPage.beanName}" styleClass="noDisplay" />
			<tr:form usesUpload="true">
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
							<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="sub">
								<!-- content menu lower line starts here -->										
									<h:commandLink id="lnkEdit" action="#{ViewItemFull.editItem}" value="#{lbl.actionMenu_lnkEdit}" rendered="#{!ViewItemFull.isStateWithdrawn and ((ViewItemFull.isStatePending || ViewItemFull.isStateInRevision) and ViewItemFull.isLatestVersion and ViewItemFull.isOwner) || (ViewItemFull.isStateSubmitted and ViewItemFull.isLatestVersion and ViewItemFull.isModerator)}"/>
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and ((ViewItemFull.isStatePending || ViewItemFull.isStateInRevision) and ViewItemFull.isLatestVersion and ViewItemFull.isOwner) || (ViewItemFull.isStateSubmitted and ViewItemFull.isLatestVersion and ViewItemFull.isModerator)}" />
									<h:commandLink id="lnkSubmit" action="#{ViewItemFull.submitItem}" value="#{lbl.actionMenu_lnkSubmit}" rendered="#{!ViewItemFull.isStateWithdrawn and  (ViewItemFull.isStatePending || ViewItemFull.isStateInRevision) and ViewItemFull.isLatestVersion and ViewItemFull.isOwner and ViewItemFull.isWorkflowStandard}" />
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and  (ViewItemFull.isStatePending || ViewItemFull.isStateInRevision) and ViewItemFull.isLatestVersion and ViewItemFull.isOwner and ViewItemFull.isWorkflowStandard}" />
									<h:commandLink id="lnkRelease" action="#{ViewItemFull.submitItem}" value="#{lbl.actionMenu_lnkRelease}" rendered="#{!ViewItemFull.isStateWithdrawn and ((ViewItemFull.isStatePending || ViewItemFull.isStateSubmitted) and ViewItemFull.isLatestVersion and ViewItemFull.isOwner and ViewItemFull.isWorkflowSimple) }"/>
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and ((ViewItemFull.isStatePending || ViewItemFull.isStateSubmitted) and ViewItemFull.isLatestVersion and ViewItemFull.isOwner and ViewItemFull.isWorkflowSimple) }" />
									<h:commandLink id="lnkAccept" action="#{ViewItemFull.acceptItem}" value="#{lbl.actionMenu_lnkAccept}" rendered="#{!ViewItemFull.isStateWithdrawn and (ViewItemFull.isStateSubmitted and ViewItemFull.isLatestVersion and ViewItemFull.isModerator and !ViewItemFull.isModifyDisabled) }"/>
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and (ViewItemFull.isStateSubmitted and ViewItemFull.isLatestVersion and ViewItemFull.isModerator and !ViewItemFull.isModifyDisabled) }" />
									<h:commandLink id="lnkRevise" action="#{ViewItemFull.reviseItem}" value="#{lbl.actionMenu_lnkRevise}" rendered="#{!ViewItemFull.isStateWithdrawn and (ViewItemFull.isStateSubmitted and ViewItemFull.isLatestVersion and ViewItemFull.isModerator and !ViewItemFull.isModifyDisabled and ViewItemFull.isWorkflowStandard) }"/>
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and (ViewItemFull.isStateSubmitted and ViewItemFull.isLatestVersion and ViewItemFull.isModerator and !ViewItemFull.isModifyDisabled and ViewItemFull.isWorkflowStandard) }" />
									<h:commandLink id="lnkDelete" onclick="if(!confirmDelete('form1:viewItemFull'))return false;" value="#{lbl.actionMenu_lnkDelete}" action="#{ViewItemFull.deleteItem}" rendered="#{!ViewItemFull.isStateWithdrawn and ViewItemFull.isStatePending and ViewItemFull.isLatestVersion and ViewItemFull.isOwner}"/>
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and ViewItemFull.isStatePending and ViewItemFull.isLatestVersion and ViewItemFull.isOwner}" />
									<h:commandLink id="lnkWithdraw" action="#{ViewItemFull.withdrawItem}" value="#{lbl.actionMenu_lnkWithdraw}" rendered="#{!ViewItemFull.isStateWithdrawn and ViewItemFull.isStateReleased and ViewItemFull.isLatestVersion and ViewItemFull.isOwner}"/>
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and ViewItemFull.isStateReleased and ViewItemFull.isLatestVersion and ViewItemFull.isOwner}" />
									<h:commandLink id="lnkModify" action="#{ViewItemFull.modifyItem}" value="#{lbl.actionMenu_lnkModify}" rendered="#{!ViewItemFull.isStateWithdrawn and ViewItemFull.isStateReleased and ViewItemFull.isLatestVersion and !ViewItemFull.isModifyDisabled and ViewItemFull.isModerator}"/>
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and ViewItemFull.isStateReleased and ViewItemFull.isLatestVersion and !ViewItemFull.isModifyDisabled and ViewItemFull.isModerator}" />
									<h:commandLink id="lnkCreateNewRevision" action="#{ViewItemFull.createNewRevision}" value="#{lbl.actionMenu_lnkCreateNewRevision}" rendered="#{!ViewItemFull.isStateWithdrawn and ViewItemFull.isStateReleased and ViewItemFull.isLatestRelease and !ViewItemFull.isCreateNewRevisionDisabled and ViewItemFull.isDepositor}"/>
									<h:panelGroup styleClass="seperator" rendered="#{!ViewItemFull.isStateWithdrawn and ViewItemFull.isStateReleased and ViewItemFull.isLatestRelease and !ViewItemFull.isCreateNewRevisionDisabled and ViewItemFull.isDepositor}" />
								<!-- content menu lower line ends here -->
								</div>
							<!-- content menu ends here -->
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{ViewItemFull.hasErrorMessages}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{ViewItemFull.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{ViewItemFull.hasMessages and !ViewItemFull.hasErrorMessages}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{ViewItemFull.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>			
					<div class="full_area0">
						<div class="full_area0 fullItem">
							<div class="full_area0 fullItemControls">
								<span class="full_area0_p5">
									<b class="free_area0 small_marginLExcl">&#160;<h:outputText value="#{lbl.ViewItemReleaseHistory_Header}" /></b>
<!-- JIRA AS-583 -->
<!-- start please develop local Tags functionality -->
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.isLatestVersion and !ViewItemFull.isStateWithdrawn and ViewItemFull.isLoggedIn and (ViewItemFull.isDepositor || ViewItemFull.isModerator)}" />
									<h:outputLink styleClass="free_area0 underRework" value="#{ApplicationBean.appContext}LocalTagsPage.jsp" rendered="#{ViewItemFull.isLatestVersion and !ViewItemFull.isStateWithdrawn and ViewItemFull.isLoggedIn and (ViewItemFull.isDepositor || ViewItemFull.isModerator)}">
										<h:outputText value="#{lbl.ViewItemFull_lblSubHeaderLocalTags}" />
									</h:outputLink>
<!-- stop please develop local Tags functionality -->
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.isLatestVersion and !ViewItemFull.isStateWithdrawn and ViewItemFull.isLoggedIn and (ViewItemFull.isDepositor || ViewItemFull.isModerator)}" />
									<h:commandLink styleClass="free_area0" action="#{ViewItemFull.showItemLog}" rendered="#{ViewItemFull.isLatestVersion and !ViewItemFull.isStateWithdrawn and ViewItemFull.isLoggedIn and (ViewItemFull.isDepositor || ViewItemFull.isModerator)}">
										<h:outputText value="#{lbl.ViewItemLogPage}"/>
									</h:commandLink>
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.isLatestRelease and !ViewItemFull.isStateWithdrawn}" />
									<h:commandLink styleClass="free_area0" action="#{ViewItemFull.showStatistics}" rendered="#{ViewItemFull.isLatestRelease and !ViewItemFull.isStateWithdrawn}">
										<h:outputText value="#{lbl.ViewItemFull_btnItemStatistics}"/>
									</h:commandLink>
									<h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.isLatestRelease and !ViewItemFull.isStateWithdrawn}" />
									<h:commandLink styleClass="free_area0" action="#{ViewItemFull.showRevisions}" rendered="#{ViewItemFull.isLatestRelease and !ViewItemFull.isStateWithdrawn}">
										<h:outputText value="#{lbl.ViewItemFull_btnItemRevisions}"/>
									</h:commandLink>
									<h:panelGroup styleClass="seperator" rendered="#{(!ViewItemFull.isStateWithdrawn and ViewItemFull.isLatestRelease) || (ViewItemFull.isStateWithdrawn and ViewItemFull.pubItem.version.versionNumber > 1) }" />
									<h:outputLink styleClass="free_area0 actual" value="#contentSkipLinkAnchor">
										<h:outputText value="#{lbl.ViewItemFull_btnItemVersions}"/>
									</h:outputLink>
									<h:panelGroup styleClass="seperator" />
									<h:outputLink styleClass="free_area0" value="#{ViewItemFull.citationURL}">
										<h:outputText value="#{lbl.ViewItemPage}"/>
									</h:outputLink>
									<h:panelGroup styleClass="seperator" />
								</span>
							</div>
							<div class="full_area0 listItem">
								<div class="free_area0 itemHeader">
									<span class="free_area0_p6 endline itemHeadline">
										<b><h:outputText value="#{ViewItemFull.pubItem.metadata.title.value}"/></b>
									</span>
								</div>
							</div>

							<jsp:directive.include file="releases/ReleaseHistory.jspf"/>

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
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>