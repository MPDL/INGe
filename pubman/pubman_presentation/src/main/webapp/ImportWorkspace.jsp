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
					<table>
						<thead>
							<tr>
								<th>
									<h:outputText value="#{lbl.import_workspace_status}"/>
								</th>
								<th>
									<h:outputText value="#{lbl.import_workspace_name}"/>
								</th>
								<th>
									<h:outputText value="#{lbl.import_workspace_format}"/>
								</th>
								<th>
									<h:outputText value="#{lbl.import_workspace_startdate}"/>
								</th>
								<th>
									<h:outputText value="#{lbl.import_workspace_enddate}"/>
								</th>
								<th>
									<h:outputText value="#{lbl.import_workspace_details}"/>
								</th>
								<th>
									<h:outputText value="#{lbl.import_workspace_remove}"/>
								</th>
								<th>
									Link
								</th>
							</tr>
						</thead>
						<tbody>
							<tr:iterator var="import" rows="0" value="#{ImportWorkspace.imports}">
								<h:panelGroup>
									<tr>
										<td>
											<h:panelGroup rendered="#{!import.finished}">
												<h:outputText value="#{import.percentage}"/>% - 
											</h:panelGroup>
											<h:outputText value="#{import.status}"/>
											/
											<h:outputText value="#{import.errorLevel}"/>
										</td>
										<td>
											<h:outputText value="#{import.message}"/>
										</td>
										<td>
											<h:outputText value="#{import.format}"/>
										</td>
										<td>
											<h:outputText value="#{import.startDateFormatted}"/>
										</td>
										<td>
											<h:outputText value="#{import.endDateFormatted}"/>
										</td>
										<td>
											<h:outputText value="#{lbl.import_workspace_details}"/>
										</td>
										<td>
											<h:panelGroup rendered="#{import.finished}">
												<tr:commandLink action="#{import.remove}">
													<h:outputText value="#{lbl.import_workspace_remove_import}"/>
												</tr:commandLink>
												/
												<tr:commandLink action="#{import.deleteAll}">
													<h:outputText value="#{lbl.import_workspace_delete_items}"/>
												</tr:commandLink>
												/
												<tr:commandLink action="#{import.submitAll}" rendered="#{!import.simpleWorkflow}">
													<h:outputText value="#{lbl.import_workspace_submit_items}"/>
												</tr:commandLink>
												/
												<tr:commandLink action="#{import.submitAndReleaseAll}">
													<h:outputText value="#{lbl.import_workspace_submit_release_items}"/>
												</tr:commandLink>
											</h:panelGroup>
										</td>
										<td>
											<h:outputLink value="#{import.logLink}">
												Import
											</h:outputLink>
											/
											<h:outputLink value="#{import.itemsLink}">
												Items
											</h:outputLink>
										</td>
									</tr>
								</h:panelGroup>
							</tr:iterator>
						</tbody>
					</table>
				</tr:form>
			</body>
		</html>
	</f:view>
</jsp:root>