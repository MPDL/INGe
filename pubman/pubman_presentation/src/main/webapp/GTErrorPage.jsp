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
        <ui:page id="page1" >
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
						clickHeatPage = 'GTErrorPage'; //Identifier der Seite
						initClickHeat();
					</script>
                </ui:head>
                <ui:body id="body1">
                	<div id="page_margins">
						<div id="page">
		                    <ui:form id="form1">
		                        <div id="main">
		                        	<div id="col3">
										<div class="content">
				                            <h:panelGrid id="panPageAlert" binding="#{ErrorPage.panPageAlert}">
										    </h:panelGrid>
											<h:panelGrid id="panButtons" columns="1" cellspacing="25" cellpadding="0">
												<a href="javascript:window.back()" style="margin-left: 670px; font-weight: bold; height: 50px; width: 150px; font-size: 16px">Back to previous page</a>
											</h:panelGrid>
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
