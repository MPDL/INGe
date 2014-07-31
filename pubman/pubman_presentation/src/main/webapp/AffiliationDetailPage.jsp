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
			<html xmlns="http://www.w3.org/1999/xhtml">
				<h:head>

					<title><h:outputText value="#{AffiliationDetailPage.affiliation.defaultMetadata.name}"/></title>
					<ui:include src="header/ui/StandardImports.jspf" />
					
					<ui:include src="affiliation/OrganizationDetailFeedLinks.jspf" />
	
				</h:head>
				<body lang="${InternationalizationHelper.locale}">
					<h:outputText value="#{AffiliationDetailPage.beanName}" styleClass="noDisplay" />
					<h:form id="form1">
					<div class="full wrapper withoutPageHeader">
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
										<b><h:outputText id="detailsTitle" value="#{AffiliationDetailPage.affiliation.defaultMetadata.name}"/></b>
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
												<ui:repeat id="detailsAltTitles" var="alternative" value="#{AffiliationDetailPage.affiliation.defaultMetadata.alternativeNames}">
			                                        <h:outputText id="txtAffiliationDetailAlternativeTitle" styleClass="xHuge_area0 endline" value="#{alternative} "/>    
			                                    </ui:repeat>
											</span>
										</div>
										<!-- city & country -->
										<div class="free_area0 endline itemLine  noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												<h:outputText value="#{lbl.AffiliationDetailLocation}" /><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 endline">
												<h:outputText id="detailsCity" value="#{AffiliationDetailPage.affiliation.defaultMetadata.city}, "/>
	                                    		<h:outputText id="detailsCountry" value="#{AffiliationDetailPage.affiliation.defaultMetadata.countryCode}"/>
											</span>
										</div>
										<!-- descriptions -->
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												<h:outputText value="#{lbl.AffiliationDetailDescription}" /><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 endline">
												 <ui:repeat id="detailsDescription" var="description" value="#{AffiliationDetailPage.affiliation.defaultMetadata.descriptions}">
			                                        <h:outputText id="txtAffiliationDetailDescription" styleClass="xHuge_area0 endline" value="#{description} "/>    
			                                    </ui:repeat>
											</span>
										</div>
										<!-- identifiers -->
                                        <div class="free_area0 endline itemLine noTopBorder">
                                            <b class="xLarge_area0_p8 endline labelLine clear">
                                                <h:outputText value="#{lbl.AffiliationDetailIdentifier}" /><span class="noDisplay">: </span>
                                            </b>
                                            <span class="xHuge_area0 endline">
                                                 <ui:repeat id="detailsIdentifier" var="identifier" value="#{AffiliationDetailPage.affiliation.defaultMetadata.identifiers}">
                                                    <h:outputText id="txtAffiliationDetailIdentifier" styleClass="xHuge_area0 endline" value="#{identifier.id} "/>    
                                                </ui:repeat>
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
										<ui:repeat id="successorsDescription" var="successors" value="#{AffiliationDetailPage.affiliation.successors}">
										<div class="free_area0 endline itemLine noTopBorder">
											<b class="xLarge_area0_p8 endline labelLine clear">
												<h:outputText value="#{lbl.AffiliationDetailName}" /><span class="noDisplay">: </span>
											</b>
											<span class="xHuge_area0 endline">
                                             <h:outputText styleClass="xHuge_area0 endline" value="#{successors.defaultMetadata.name}"/>    
                                         </span>
										</div>
										</ui:repeat>
									</div>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="full_area0 itemBlock" rendered="#{AffiliationDetailPage.affiliation.hasPredecessors}">
									<h3 class="xLarge_area0_p8 endline blockHeader">
										<h:outputText value="#{lbl.AffiliationDetailPredecessors}"/>
									</h3>
									<h:panelGroup styleClass="seperator"></h:panelGroup>
								   <div class="free_area0 itemBlockContent endline">
									<!-- any field -->
									<ui:repeat id="predecessorsDescription" var="predecessor" value="#{AffiliationDetailPage.affiliation.predecessors}">
									  <div class="free_area0 endline itemLine noTopBorder">
										 <b class="xLarge_area0_p8 endline labelLine clear">
											    <h:outputText value="#{lbl.AffiliationDetailName}" /><span class="noDisplay">: </span>
										 </b>
										 <span class="xHuge_area0 endline">
		                                     <h:outputText styleClass="xHuge_area0 endline" value="#{predecessor.defaultMetadata.name}"/>    
										 </span>
									  </div>
									</ui:repeat>
								   </div>
								</h:panelGroup>
							</div>
						</div>
					</div>
					</h:form>
				</body>
			</html>
	</f:view>

