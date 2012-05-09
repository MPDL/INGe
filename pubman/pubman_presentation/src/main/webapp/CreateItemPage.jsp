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

<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:rich="http://richfaces.org/rich" xmlns:a4j="http://richfaces.org/a4j" >

	<jsp:output doctype-root-element="html" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" /> 

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" />
	<f:view locale="#{InternationalizationHelper.userLocale}">
			<f:loadBundle var="lbl" basename="Label" />
			<f:loadBundle var="msg" basename="Messages" />
			<f:loadBundle var="tip" basename="Tooltip" />
				
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<jsp:directive.include file="header/ui/StandardImports.jspf" />
			</head>
			<body lang="#{InternationalizationHelper.locale}">
				<h:outputText value="#{CreateItemPage.beanName}" styleClass="noDisplay" />
				<h:form >
					<div class="full wrapper">
						<h:inputHidden id="offset"></h:inputHidden>
						<!-- start: skip link navigation -->
						<h:outputLink styleClass="skipLink" title="skip link" value="#mainMenuSkipLinkAnchor">
							<h:outputText value="Skip to the main menu" />
						</h:outputLink>
						<h:outputLink styleClass="skipLink" title="skip link" value="#contentSkipLinkAnchor">
							<h:outputText value="Skip to the page content" />
						</h:outputLink>
						<h:outputLink styleClass="skipLink" title="skip link" value="#searchMenuSkipLinkAnchor">
							<h:outputText value="Skip to the search menu" />
						</h:outputLink>
						<h:outputLink styleClass="skipLink" title="skip link" value="#metaMenuSkipLinkAnchor">
							<h:outputText value="Skip to the meta menu" />
						</h:outputLink>
						<!-- end: skip link navigation -->
					
						<jsp:directive.include file="header/Header.jspf" />
		
						<div id="content" class="full_area0 clear"> <!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
							<div class="clear">
								<div class="headerSection">
									<jsp:directive.include file="header/Breadcrumb.jspf" />
									<div id="contentSkipLinkAnchor" class="clear headLine">
										<!-- Headline starts here -->
										<h1><h:outputText value="#{lbl.CreateItemPage}"/></h1>
										<!-- Headline ends here -->
									</div>
								</div>
								<div class="small_marginLIncl subHeaderSection">
									<div class="contentMenu"> <!-- content menu starts here -->
										<div class="free_area0 sub"> <!-- content menu lower line starts here -->
											<h:commandLink id="lnkNewEasySubmission" title="#{tip.submission_lnkEasySubmission}" action="#{EasySubmission.newEasySubmission}">
												<h:outputText value="#{lbl.submission_lnkEasySubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
											</h:commandLink>
											<h:outputText styleClass="seperator void" />
											<h:commandLink id="lnkNewSubmission" title="#{tip.submission_lnkNewSubmission}" rendered="#{CreateItem.multiple}" action="#{CreateItem.newSubmission}" immediate="true">
												<h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
											</h:commandLink>
											<span>
												<h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{!CreateItem.multiple and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
											</span>
											<h:outputText styleClass="seperator void" />
											<h:commandLink id="lnkNewImport" title="#{tip.submission_lnkImport}" action="#{EasySubmission.newImport}">
												<h:outputText value="#{lbl.EasySubmission_lblFetchMetadata}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
											</h:commandLink>
											<h:outputText styleClass="seperator void" />
											<h:commandLink id="lnkNewMultipleImport" title="#{tip.submission_lnkMultipleImport}" rendered="#{!CreateItem.multiple}" action="#{MultipleImport.newImport}">
												<h:outputText value="#{lbl.submission_lnkMultipleImport}" rendered="#{LoginHelper.isModerator and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
											</h:commandLink>
											<span>
												<h:outputText value="#{lbl.submission_lnkMultipleImport}" rendered="#{CreateItem.multiple and LoginHelper.isModerator and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
											</span>
										</div> <!-- content menu lower line ends here -->
									</div> <!-- content menu ends here -->
								</div> <!-- end of subHeaderSection -->
							</div> <!-- end of clear div -->		
							<div class="full_area0">
								<div class="full_area0 fullItem" id="fullItem">
									<div class="full_area0 small_marginLExcl">
										<!-- Subheadline starts here -->
										<h3><h:outputText value="#{msg.create_Item_Select_Collection}" rendered="#{ContextListSessionBean.openContextsAvailable}"/></h3>
										<h3><h:outputText value="#{msg.depositorWE_noOpenContextsAvailable}" rendered="#{!ContextListSessionBean.openContextsAvailable}"/></h3>
										<!-- Subheadline ends here -->
									</div>
									<jsp:directive.include file="createItem/CreateItem.jspf"/>
								</div>
								<div class="full_area0 formButtonArea">		
									<h:outputLink id="lnkBack" styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" value="#{ApplicationBean.appContext}SubmissionPage.jsp"><h:outputText value="#{lbl.easy_submission_btnBack}" /></h:outputLink>
								</div>
							</div>
						</div> <!-- end: content section -->
					</div> <!-- end of full wrapper -->
					<jsp:directive.include file="footer/Footer.jspf" />
				</h:form>
				<script type="text/javascript">
					
					$pb(document).ready(function () {
						/*
						$pb("input[id$='offset']").submit(function() {
							$pb(this).val($pb(window).scrollTop());
						});
						*/
						$pb(window).scrollTop($pb("input[id$='offset']").val());
						$pb(window).scroll(function(){$pb("input[id$='offset']").val($pb(window).scrollTop());});
					});
				</script>
			</body>
		</html>
	</f:view>
</jsp:root>