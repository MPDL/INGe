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
			<html>
				<head>
					<link rel="stylesheet" type="text/css" href="./resources/escidoc-css/css/main.css" />
					<link rel="SHORTCUT ICON" href="./images/escidoc.ico" />
					<meta http-equiv="pragma" content="no-cache" />
					<meta http-equiv="cache-control" content="no-cache" />
					<meta http-equiv="expires" content="0" />
					<!-- FrM: Moved JS sources to external file -->
					<script type="text/javascript" language="JavaScript" src="resources/scripts.js">;</script>
				</head>
				<body>
					<h:outputText id="pageDummy" value="#{AffiliationDetailPage.beanName}" style="height: 0px; width: 0px; visibility:hidden; position: absolute" />
					<div id="page_margins">
						<div id="page">
							<h:form id="form1">
								<div class="affDetails">
									<h1><h:outputText value="#{lbl.AffiliationTree_txtHeadlineDetails}"/></h1>
									<h:messages errorClass="messageError" infoClass="messageStatus" layout="table" globalOnly="true" showDetail="false" showSummary="true" rendered="#{AffiliationDetailPage.hasMessages}"/>
						
						            <!-- title -->
									<tr:outputText id="detailsTitle" value="#{AffiliationDetailPage.affiliation.defaultMetadata.name}"/>  
									
									<br/>
									
									<!-- alternative titles -->
									<tr:iterator id="detailsAltTitles" var="alternative" value="#{AffiliationDetailPage.affiliation.defaultMetadata.alternativeNames}">
                                        <tr:outputText value="#{alternative} "/>    
                                    </tr:iterator>
                                    
                                    <br/>
                                    
                                    <!-- descriptions -->
                                    <tr:iterator id="detailsDescription" var="description" value="#{AffiliationDetailPage.affiliation.defaultMetadata.descriptions}">
                                        <tr:outputText value="#{description} "/>    
                                    </tr:iterator>
                                    
                                    <br/>
                                    
                                    <!-- city -->
                                    <tr:outputText id="detailsCity" value="#{AffiliationDetailPage.affiliation.defaultMetadata.city}, "/>
                                    
                                    <!-- country -->
                                    <tr:outputText id="detailsCountry" value="#{AffiliationDetailPage.affiliation.defaultMetadata.countryCode}"/>
								</div>
							</h:form>
						</div>
					</div>
				</body>
			</html>
		
	</f:view>
</jsp:root>
