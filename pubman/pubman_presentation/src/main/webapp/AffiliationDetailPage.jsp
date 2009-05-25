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


 Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" 
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:tr="http://myfaces.apache.org/trinidad">


<jsp:output doctype-root-element="html"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" />
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
			<html xmlns="http://www.w3.org/1999/xhtml">
				<head>

					<title><h:outputText value="#{AffiliationDetailPage.affiliation.defaultMetadata.name}"/></title>
					<jsp:directive.include file="header/ui/StandardImports.jspf" />
					
					<jsp:directive.include file="affiliation/OrganizationDetailFeedLinks.jspf" />
	
				</head>
				<body lang="#{InternationalizationHelper.locale}">
					<h:outputText id="pageDummy" value="#{AffiliationDetailPage.beanName}" styleClass="noDisplay" />
					<h:form id="form1">
					<div class="full wrapper">
						<div id="content" class="full_area0 clear">
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<h1><h:outputText value="#{lbl.AffiliationTree_txtHeadlineDetails}" /></h1>
							</div>
							<h:messages errorClass="messageError" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{AffiliationDetailPage.hasMessages}"/>
							<div class="full_area0 fullItem">
								<!-- title -->
								<div class="full_area0 itemHeader noTopBorder">
									<h:panelGroup styleClass="xLarge_area0 endline blockHeader" >
										&#160;	
									</h:panelGroup>
										<h:panelGroup styleClass="seperator" />
									<h:panelGroup styleClass="free_area0_p8 endline itemHeadline">
										<b><tr:outputText id="detailsTitle" value="#{AffiliationDetailPage.affiliation.defaultMetadata.name}"/></b>
									</h:panelGroup>
								</div>
								<h:panelGroup layout="block" styleClass="full_area0 itemBlock">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										<h:outputText value="#{lbl.AffiliationDetailDetails}" />
									</h3>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<div class="free_area0 itemBlockContent endline">
										<!-- alternative titles -->
										<div class="free_area0 endline itemLine  noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												<h:outputText value="#{lbl.AffiliationDetailAlternativeTitle}" /><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 endline">
												<tr:iterator id="detailsAltTitles" var="alternative" value="#{AffiliationDetailPage.affiliation.defaultMetadata.alternativeNames}">
			                                        <tr:outputText styleClass="xHuge_area0 endline" value="#{alternative} "/>    
			                                    </tr:iterator>
											</span>
										</div>
										<!-- city & country -->
										<div class="free_area0 endline itemLine  noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												<h:outputText value="#{lbl.AffiliationDetailLocation}" /><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 endline">
												<tr:outputText id="detailsCity" value="#{AffiliationDetailPage.affiliation.defaultMetadata.city}, "/>
	                                    		<tr:outputText id="detailsCountry" value="#{AffiliationDetailPage.affiliation.defaultMetadata.countryCode}"/>
											</span>
										</div>
										<!-- descriptions -->
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												<h:outputText value="#{lbl.AffiliationDetailDescription}" /><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 endline">
												 <tr:iterator id="detailsDescription" var="description" value="#{AffiliationDetailPage.affiliation.defaultMetadata.descriptions}">
			                                        <tr:outputText styleClass="xHuge_area0 endline" value="#{description} "/>    
			                                    </tr:iterator>
											</span>
										</div>
										<!-- identifiers -->
                                        <div class="free_area0 endline itemLine noTopBorder">
                                            <b class="xLarge_area0_p8 endline labelLine clear">
                                                <h:outputText value="#{lbl.AffiliationDetailIdentifier}" /><span class="noDisplay">: </span>
                                            </b>
                                            <span class="xHuge_area0 endline">
                                                 <tr:iterator id="detailsIdentifier" var="identifier" value="#{AffiliationDetailPage.affiliation.defaultMetadata.identifiers}">
                                                    <tr:outputText styleClass="xHuge_area0 endline" value="#{identifier.id} "/>    
                                                </tr:iterator>
                                            </span>
                                        </div>
									</div>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="full_area0 itemBlock" rendered="#{AffiliationDetailPage.affiliation.hasSuccessors}">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										<h:outputText value="#{lbl.AffiliationDetailSuccessors}"/>
									</h3>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<div class="free_area0 itemBlockContent endline">
										<!-- any field -->
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												<h:outputText value="label" /><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 endline">
			                                    <h:outputText styleClass="xHuge_area0 endline" value="value"/>    
											</span>
										</div>
									</div>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="full_area0 itemBlock" rendered="#{AffiliationDetailPage.affiliation.hasPredecessors}">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										<h:outputText value="#{lbl.AffiliationDetailPredecessors}"/>
									</h3>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
									<tr:iterator id="predecessorsDescription" var="predecessor" value="#{AffiliationDetailPage.affiliation.predecessors}">
									   <div class="free_area0 itemBlockContent endline">
										  <!-- any field -->
										  <div class="free_area0 endline itemLine noTopBorder">
											 <b class="xLarge_area0_p8 endline labelLine clear">
												    <h:outputText value="label" /><span class="noDisplay">: </span>
											 </b>
											 <span class="xHuge_area0 endline">
			                                     <h:outputText styleClass="xHuge_area0 endline" value="#{predecessor.defaultMetadata.name}"/>    
											 </span>
										</div>
									   </div>
									</tr:iterator>
								</h:panelGroup>
							</div>
						</div>
					</div>
					</h:form>
				</body>
			</html>
	</f:view>
</jsp:root>
