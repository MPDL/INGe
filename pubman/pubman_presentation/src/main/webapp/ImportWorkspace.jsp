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
											<h1><h:outputText value="#{lbl.import_workspace_title}"/></h1>
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
										 	
											<!-- Subheadline ends here -->
										</div>
									</div>
				              	</div>
								<div class="full_area0">
									





									<table class="full_area0 itemList loggedIn" rules="none" style="border-collapse: collapse;">
									  <thead class="listHeader" style="text-align: left; vertical-align:top;">
									    <tr class="full_area0">
									      	<th class="tiny_area0">
												&#160;
									      	</th>
									      	<th class="free_area0 endline status statusArea">
									      		<h:panelGroup styleClass="seperator"></h:panelGroup>
												<h:outputText styleClass="free_area0_p8" value="#{lbl.import_workspace_status}"/>	
											</th>
									      	<th class="large_area0">
									      		<h:panelGroup styleClass="seperator"></h:panelGroup>
												<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_name}"/>	
									      	</th>
									      <th class="large_area0">
									     		<h:panelGroup styleClass="seperator"></h:panelGroup>
												<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_format}"/>	
									      </th>
									      <th class="large_area0">
												<h:panelGroup styleClass="seperator"></h:panelGroup>
												<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_startdate}"/>
										  </th>
									      <th class="large_area0">
												<h:panelGroup styleClass="seperator"></h:panelGroup>
												<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_enddate}"/>	
										  </th>
									      <th class="large_area0">
												<h:panelGroup styleClass="seperator"></h:panelGroup>
												<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_details}"/>
										  </th>
										  <th class="large_area0 endline">
												<h:panelGroup styleClass="seperator"></h:panelGroup>
												<h:outputText styleClass="large_area0_p8" value="#{lbl.import_workspace_actions}"/>
										  </th>
									    </tr>
									  </thead>
									  <tbody style="text-align: left; vertical-align:top;">
										<tr:iterator var="import" rows="0" value="#{ImportWorkspace.imports}">
											<h:panelGroup>
											    <tr class="full_area0 listItem">
											      	<td class="free_area0 endline">
											      		<span class="tiny_area0">
															&#160;
											      		</span>
											      	</td>
											      	<td class="free_area0 endline status">
											      		<h:panelGroup styleClass="seperator"></h:panelGroup>
											      		<h:panelGroup styleClass="free_area0_p8 endline statusArea">
															<h:panelGroup styleClass="big_imgArea statusIcon submittedItem" />
															<h:outputLabel styleClass="medium_label endline" title="#{import.errorLevel}">
																<h:panelGroup rendered="#{!import.finished}">
																	<h:outputText value="#{import.percentage}"/>% - 
																</h:panelGroup>
																<h:outputText value="#{import.status}"/>	
															</h:outputLabel>
														</h:panelGroup>
											      	</td>
											      	<td class="free_area0 endline">
											      		<h:panelGroup styleClass="seperator"></h:panelGroup>
											      		<span class="large_area0_p8">
															<h:outputLink value="#{import.itemsLink}">
									 							<b><h:outputText value="#{import.message}"/></b>
										 					</h:outputLink>
											      		</span>
											      	</td>
											      	<td class="free_area0 endline">
											      		<h:panelGroup styleClass="seperator"></h:panelGroup>
											      		<span class="large_area0_p8">
											      			<h:outputText value="#{import.format}"/>&#160;
											      		</span>
											      	</td>
											      	<td class="free_area0 endline">
											      		<h:panelGroup styleClass="seperator"></h:panelGroup>
											      		<span class="large_area0_p8">
												      		<h:outputText value="#{import.startDateFormatted}"/>&#160;
												      	</span>
												    </td>
											      	<td class="free_area0 endline">
											      		<h:panelGroup styleClass="seperator"></h:panelGroup>
											      		<span class="large_area0_p8">
											      			<h:outputText value="#{import.endDateFormatted}"/>&#160;
											      		</span>
											      	</td>
											      	<td class="free_area0 endline">
											      		<h:panelGroup styleClass="seperator"></h:panelGroup>
											      		<span class="large_area0_p8">
											      			<h:outputText value="#{lbl.import_workspace_details}"/>
											      		</span>
											      	</td>
													<td class="free_area0 endline">
											      		<h:panelGroup styleClass="seperator"></h:panelGroup>
											      		<span class="large_area0 endline">
															<h:panelGroup rendered="false" styleClass="large_area0_p8 noPaddingTopBottom endline">
																<h:outputText value="#{import.errorLevel}"/>
															</h:panelGroup>
											      			<h:panelGroup rendered="#{import.finished}">
																<tr:commandLink styleClass="small_area0_p8 noPaddingTopBottom endline" action="#{import.remove}">
																	<h:outputText value="#{lbl.import_workspace_remove_import}"/>
																</tr:commandLink>

																<tr:commandLink styleClass="small_area0_p8 noPaddingTopBottom endline" action="#{import.deleteAll}">
																	<h:outputText value="#{lbl.import_workspace_delete_items}"/>
																</tr:commandLink>

																<tr:commandLink styleClass="small_area0_p8 noPaddingTopBottom endline" action="#{import.submitAll}" rendered="#{!import.simpleWorkflow}">
																	<h:outputText value="#{lbl.import_workspace_submit_items}"/>
																</tr:commandLink>

																<tr:commandLink styleClass="small_area0_p8 noPaddingTopBottom endline" action="#{import.submitAndReleaseAll}">
																	<h:outputText value="#{lbl.import_workspace_submit_release_items}"/>
																</tr:commandLink>
															</h:panelGroup>
											      		</span>
											      	</td>
											    </tr>
											</h:panelGroup>
										</tr:iterator>
									  </tbody>
									</table>





									
								</div>
							<!-- end: content section -->
							</div>			
					</div>



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
									<h:outputText value="#{lbl.import_workspace_actions}"/>
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