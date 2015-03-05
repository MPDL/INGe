<!DOCTYPE html>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
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



	 

	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<ui:include src="header/ui/StandardImports.jspf" />
			</h:head>
			<body lang="${InternationalizationHelper.locale}">
			<h:outputText value="#{YearbookArchiveBean.beanName}" styleClass="noDisplay" />
			<h:outputText value="#{YearbookArchivePage.beanName}" styleClass="noDisplay" />

			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
				<ui:include src="header/Header.jspf" />
				<h:form >
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
						<ui:include src="header/Breadcrumb.jspf" />
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.YearbookArchivePage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<h:panelGroup layout="block" styleClass="contentMenu">
							<!-- content menu starts here -->
								<h:panelGroup layout="block" styleClass="free_area0 sub">
									<h:outputLink id="lnkMenuQAWorkspace" title="#{tip.chooseWorkspace_QAWorkspace}"  value="#{ApplicationBean.appContext}QAWSPage.jsp" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuQAWorkspace}" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}"/>
									</h:outputLink>
									
									<h:outputText styleClass="seperator void" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									<h:outputLink id="lnkSubmission_lnkImportWorkspaceMenu" title="#{tip.chooseWorkspace_ImportWorkspace}" value="#{ApplicationBean.appContext}ImportWorkspace.jsp" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuImportWorkspace}"/>
									</h:outputLink>  
									
									<h:panelGroup id="txtMenuYearbookWorkspace" rendered="#{LoginHelper.isYearbookEditor}">
										<h:outputText styleClass="seperator void" />
										<h:outputText value="#{lbl.chooseWorkspace_optMenuYearbookWorkspace}"/>
									</h:panelGroup>
									
									
									<h:outputText styleClass="seperator void" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}" />
									<h:outputLink id="lnkMenuReportWorkspace" title="#{tip.chooseWorkspace_ReportWorkspace}" value="#{ApplicationBean.appContext}ReportWorkspacePage.jsp" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuReportWorkspace}"/>
									</h:outputLink>
								</h:panelGroup>
							<!-- content menu ends here -->
							</h:panelGroup>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookArchiveBean.numberOfMessages == 1}"/>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{YearbookArchiveBean.hasErrorMessages and YearbookArchiveBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{YearbookArchiveBean.hasMessages and !YearbookArchiveBean.hasErrorMessages and YearbookArchiveBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookArchiveBean.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div id="editItem" class="full_area0 fullItem">	
							<ui:include src="yearbook/yearbookArchiveList.jspf" />
						</div>
					</div>
				<!-- end: content section -->
				</div>
				</h:form>
			</div>
			<ui:include src="footer/Footer.jspf" />
			
			</body>
		</html>
	</f:view>
