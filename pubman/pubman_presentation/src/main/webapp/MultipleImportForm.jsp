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
				<tr:form>
					<h:outputText value="#{lbl.multipleImport_getMultipleRecords}"/>
					<h:outputText value="#{MultipleImport.uploadedImportFile.filename}"/>,
					<h:outputText value="#{MultipleImport.fileSize}"/>
					<br/>
					<h:outputText value="#{msg.multipleImport_description}"/>
					<br/>
					<h:selectBooleanCheckbox value="#{MultipleImport.rollback}"/>
					<h:outputText value="#{lbl.multipleImport_checkboxRollback}"/>
					<br/>
					<h:selectOneRadio value="#{MultipleImport.duplicateStrategy}">
						<f:selectItem itemValue="1" itemLabel="#{lbl.multipleImport_dont_check_duplicates}"/>
						<f:selectItem itemValue="2" itemLabel="#{lbl.multipleImport_dont_import_duplicates}"/>
						<f:selectItem itemValue="3" itemLabel="#{lbl.multipleImport_dont_import_anything}"/>
					</h:selectOneRadio>
					<h:outputText value="#{lbl.multipleImport_checkboxSomething}"/>
					<br/>
					<h:inputText value="#{MultipleImport.name}"/>
					
					<div class="full_area0 formButtonArea">		
						<h:outputLink value="#{ApplicationBean.appContext}SubmissionPage.jsp"><h:outputText value="#{lbl.EditItem_lnkCancel}" /></h:outputLink>
						<tr:commandLink shortDesc="#{tip.easy_submission_btnImport}" action="#{MultipleImport.startImport}"><h:outputText value="#{lbl.easy_submission_btnImport}" /></tr:commandLink>
					</div>
				</tr:form>
			</body>
		</html>
	</f:view>
</jsp:root>