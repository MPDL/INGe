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
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<jsp:directive.include file="header/ui/StandardImports.jspf" />


			</head>
			<body lang="#{InternationalizationHelper.locale}">
				<h:outputText value="#{MultipleImport.beanName}" styleClass="noDisplay" />
				<tr:form>
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
												<h:commandLink title="#{tip.submission_lnkEasySubmission}" action="#{EasySubmission.newEasySubmission}">
													<h:outputText value="#{lbl.submission_lnkEasySubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
												</h:commandLink>
												<h:outputText styleClass="seperator void" />
												<h:commandLink title="#{tip.submission_lnkNewSubmission}" action="#{CreateItem.newSubmission}" immediate="true">
													<h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
												</h:commandLink>
												<h:outputText styleClass="seperator void" />
												<h:commandLink title="#{tip.submission_lnkImport}" action="#{EasySubmission.newImport}">
													<h:outputText value="#{lbl.submission_lnkImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
												</h:commandLink>
												<h:outputText styleClass="seperator void" />
												<span>
													<h:outputText value="#{lbl.submission_lnkMultipleImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"/>
												</span>
											</div>
											<div class="free_area0 sub action">
											<!-- content menu lower line starts here -->
												
											<!-- content menu lower line ends here -->
											</div>
										<!-- content menu ends here -->
										</div>
										<div class="subHeader">
											<!-- Subheadline starts here -->
										 	<h:outputText value="#{lbl.easy_submission_lblCollectionOfImportedItem1} MultipleImport.contextName#{lbl.easy_submission_lblCollectionOfImportedItem2}." />
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
														<h:outputText value="#{msg.multipleImport_description}"/>
													</span>
												</h:panelGroup>
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine firstLine">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<span class="xLarge_checkbox">
															<h:selectBooleanCheckbox value="#{MultipleImport.rollback}"/>
															<h:outputLabel value="#{lbl.multipleImport_checkboxRollback}"/>
														</span>
													</span>
												</h:panelGroup>	
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<h:selectOneRadio styleClass="large_radioBtn xHuge_area0" value="#{MultipleImport.duplicateStrategy}">
															<f:selectItem itemValue="1" itemLabel="#{lbl.multipleImport_dont_check_duplicates}"/>
															<f:selectItem itemValue="2" itemLabel="#{lbl.multipleImport_dont_import_duplicates}"/>
															<f:selectItem itemValue="3" itemLabel="#{lbl.multipleImport_dont_import_anything}"/>
														</h:selectOneRadio>
													</span>
												</h:panelGroup>
												<h:panelGroup layout="block" styleClass="free_area0 endline itemLine firstLine">
													<b class="xLarge_area0 endline labelLine">
														&#160;<span class="noDisplay">: </span>
													</b>
													<span class="xHuge_area0 xTiny_marginLExcl endline">
														<span class="large_area0">
															<h:outputLabel styleClass="large_label" value="#{lbl.multipleImport_importTag}"/>
															<h:inputText styleClass="large_txtInput" value="#{MultipleImport.name}"/>
														</span>
													</span>
												</h:panelGroup>
											</div>
										</div>
									</div>
									<div class="full_area0 formButtonArea">
										<h:outputLink styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" value="#{ApplicationBean.appContext}SubmissionPage.jsp"><h:outputText value="#{lbl.EditItem_lnkCancel}" /></h:outputLink>
										<tr:commandLink styleClass="free_area1_p8 activeButton" shortDesc="#{tip.easy_submission_btnImport}" action="#{MultipleImport.startImport}"><h:outputText value="#{lbl.easy_submission_btnImport}" /></tr:commandLink>
									</div>
								</div>
							<!-- end: content section -->
							</div>			
					</div>
				</tr:form>
			</body>
		</html>
	</f:view>
</jsp:root>