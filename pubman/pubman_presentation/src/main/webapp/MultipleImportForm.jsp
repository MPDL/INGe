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


 Copyright 2006-20110 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:tr="http://myfaces.apache.org/trinidad">

	<jsp:output doctype-root-element="html"
	       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" /> 

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}" >
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<jsp:directive.include file="header/ui/StandardImports.jspf" />

			</head>
			<body lang="#{InternationalizationHelper.locale}">
				<tr:form usesUpload="true" rendered="#{LoginHelper.isModerator and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
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
											<h1><h:outputText value="#{lbl.submission_lnkMultipleImport}"/></h1>
											<!-- Headline ends here -->
										</div>
				                    </div>
									<div class="small_marginLIncl subHeaderSection">
										<div class="contentMenu">
										<!-- content menu starts here -->
											<div class="free_area0 sub">
												<h:commandLink id="lnkEasySubmission" title="#{tip.submission_lnkEasySubmission}" action="#{EasySubmission.newEasySubmission}">
													<h:outputText value="#{lbl.submission_lnkEasySubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
												</h:commandLink>
												<h:outputText styleClass="seperator void" />
												<h:commandLink id="lnkNewSubmission" title="#{tip.submission_lnkNewSubmission}" action="#{CreateItem.newSubmission}" immediate="true">
													<h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
												</h:commandLink>
												<h:outputText styleClass="seperator void" />
												<h:commandLink id="lnkImport" title="#{tip.submission_lnkImport}" action="#{EasySubmission.newImport}">
													<h:outputText value="#{lbl.EasySubmission_lblFetchMetadata}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
												</h:commandLink>
												<h:outputText styleClass="seperator void" />
												<span>
													<h:outputText value="#{lbl.submission_lnkMultipleImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
												</span>
												<h:outputText styleClass="seperator void" />
												<h:outputLink id="lnkImportWorkspace" title="#{tip.submission_lnkImportWorkspace}" value="ImportWorkspace.jsp" rendered="#{LoginHelper.isModerator and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
													<h:outputText value="#{lbl.submission_lnkImportWorkspace}"/>
												</h:outputLink>
											</div>
											<div class="free_area0 sub action">
											<!-- content menu lower line starts here -->
												
											<!-- content menu lower line ends here -->
											</div>
										<!-- content menu ends here -->
										</div>
										<div class="subHeader">
											<!-- Subheadline starts here -->
										 	<h:outputText value="#{lbl.easy_submission_lblCollectionOfItem} #{MultipleImport.context.name}." />
											<!-- Subheadline ends here -->
										</div>
										<div class="subHeader">
											<!-- Subheadline starts here -->
											<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{MultipleImport.hasErrorMessages}">
												<input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
												<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
												<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{MultipleImport.hasMessages}"/>
											</h:panelGroup>
											<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea" rendered="#{MultipleImport.hasMessages and !MultipleImport.hasErrorMessages}">
												<input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
												<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
												<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{MultipleImport.hasMessages}"/>
											</h:panelGroup>
											<!-- Subheadline ends here -->
										</div>
									</div>
				              	</div>
								<div class="full_area0">
									<div class="full_area0 fullItem">
										<div class="full_area0 itemBlock noTopBorder">
											<h3 class="xLarge_area0_p8 endline blockHeader">
												<h:outputText value="#{lbl.multipleImport_getMultipleRecords}"/>
											</h3>
											<h:panelGroup styleClass="seperator"></h:panelGroup>
											<div class="free_area0 itemBlockContent endline">
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine firstLine">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<h:outputText value="#{MultipleImport.uploadedImportFile.filename}, #{MultipleImport.fileSize}"/>
													</span>
												</h:panelGroup>
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine firstLine">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<h:outputText value="#{lbl.multipleImport_description}"/>
													</span>
												</h:panelGroup>
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine firstLine">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<span class="xHuge_checkbox">
															<h:selectBooleanCheckbox id="selRollback" value="#{MultipleImport.rollback}"/>
															<h:outputLabel id="lblCheckBoxRollback" value="#{lbl.multipleImport_checkboxRollback}"/>
														</span>
													</span>
												</h:panelGroup>	
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<h:selectOneRadio id="selDuplicateStrategy" layout="pageDirection" styleClass="xHuge_radioBtn xHuge_area0" value="#{MultipleImport.duplicateStrategy}">
															<f:selectItem id="selDontCheckDuplicates" itemValue="1" itemLabel="#{lbl.multipleImport_dont_check_duplicates}"/>
															<f:selectItem id="selDontImportDuplicates" itemValue="2" itemLabel="#{lbl.multipleImport_dont_import_duplicates}"/>
															<f:selectItem id="selDontImportAnything" itemValue="3" itemLabel="#{lbl.multipleImport_dont_import_anything}"/>
														</h:selectOneRadio>
													</span>
												</h:panelGroup>
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine firstLine">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<span class="large_area0">
															<h:outputLabel id="lblImportTag" styleClass="large_label" value="#{lbl.multipleImport_importTag}"/>
															<h:inputText id="inpMultipleImportName" styleClass="large_txtInput" value="#{MultipleImport.name}"/>
														</span>
													</span>
												</h:panelGroup>
												
												</div>
												</div>
												
										<div class="full_area0 itemBlock">
											<h3 class="xLarge_area0_p8 endline blockHeader">
												<h:outputText value="#{lbl.MultipleImport_configuration}"/>
											</h3>
											<h:panelGroup styleClass="seperator"></h:panelGroup>
											<div class="free_area0 itemBlockContent endline">
												
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine firstLine" rendered="#{not empty MultipleImport.configParameters}">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<span class="huge_area0">
															<tr:iterator var="parameter" value="#{MultipleImport.configParameters}">
																<h:outputLabel styleClass="xLarge_label" value="#{parameter.label} "/>
																<h:inputText id="inpImportParam" styleClass="xLarge_txtInput" value="#{parameter.value}" rendered="#{MultipleImport.parametersValues[parameter.label] == null}"/>
																<h:selectOneMenu  value="#{parameter.value}" rendered="#{MultipleImport.parametersValues[parameter.label] != null}" styleClass="xLarge_select">
																	<f:selectItems value="#{MultipleImport.parametersValues[parameter.label]}"/>
																</h:selectOneMenu>
															</tr:iterator>
														</span>
													</span>
												</h:panelGroup>
											</div>
										</div>
									</div>
									<div class="full_area0 formButtonArea">
										<h:outputLink id="lnkCancel" styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" value="#{ApplicationBean.appContext}SubmissionPage.jsp"><h:outputText value="#{lbl.EditItem_lnkCancel}" /></h:outputLink>
										<tr:commandLink id="lnkStartImport" styleClass="free_area1_p8 activeButton" shortDesc="#{tip.easy_submission_btnImport}" action="#{MultipleImport.startImport}"><h:outputText value="#{lbl.easy_submission_btnImport}" /></tr:commandLink>
									</div>
								</div>
							<!-- end: content section -->
							</div>			
					</div>
				<jsp:directive.include file="footer/Footer.jspf" />
				</tr:form>
			</body>
		</html>
	</f:view>
</jsp:root>