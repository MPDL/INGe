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
<jsp:root version="1.2" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:ui="http://www.sun.com/web/ui">
    <jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
    <f:loadBundle var="lbl" basename="#{util$InternationalizationHelper.selectedLableBundle}"/>
	<f:loadBundle var="msg" basename="#{util$InternationalizationHelper.selectedMessagesBundle}"/>
    <f:view>
        <ui:page id="page1">
            <ui:html id="html1">
                <ui:head id="head1">
                    <link rel="stylesheet" type="text/css" href="./resources/escidoc-css/css/main.css" />
                    <link rel="SHORTCUT ICON" href="./images/escidoc.ico"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
                    <!-- FrM: Moved JS sources to external file -->
                    <script type="text/javascript" language="JavaScript" src="resources/scripts.js">;</script>
                    <script type="text/javascript" language="JavaScript" src="/clickheat/js/clickheat.js">;</script>
					<script type="text/javascript">
						clickHeatPage = 'HomePage'; //Identifier der Seite
						initClickHeat();
					</script>
                </ui:head>
                <ui:body id="body1">
                	<div id="page_margins">
						<div id="page">
		                    <ui:form id="form2">
		                        <div id="header">
		                            <jsp:directive.include file="desktop/Header.jspf"/>
		                            <jsp:directive.include file="desktop/Login.jspf"/>
		                            <jsp:directive.include file="desktop/Search.jspf"/>
		                        </div>
		                        <div id="nav">
		                            <jsp:directive.include file="desktop/Breadcrump.jspf"/>
		                        </div>
		                        </ui:form>
		                        <ui:form id="form1">
		                        <div id="main">
			                        <div id="col1">
				    					<span class="mainMenu">
			                            	<jsp:directive.include file="desktop/Navigation.jspf"/> 
			                            </span>
			                        </div>
			                        <div id="col2">
										<div class="contentActions">
											<h1><h:outputText value="#{lbl.actionMenu_Header}"/></h1>
											<ul>
												<li><ui:hyperlink id="lnkHelp" onClick="loadHelp('#{util$InternationalizationHelper.selectedHelpPage}', '#Home');return false"
										            text="#{lbl.mainMenu_lnkHelp}"/></li>
											</ul>
										</div>
									</div>
			                        <div id="col3">
										<div class="content">
				                            <jsp:directive.include file="home/ReleaseNotes.jspf"/>
				                        </div>
				                    </div>
		                        </div>
		                    </ui:form>
		                 </div>
		              </div>
                </ui:body>
            </ui:html>
        </ui:page>
    </f:view>
</jsp:root>
