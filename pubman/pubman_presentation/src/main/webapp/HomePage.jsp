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
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">

<jsp:output doctype-root-element="html"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" />
	<f:view locale="#{InternationalizationHelper.userLocale}" xmlns:e="http://www.escidoc.de/jsf">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<link rel="stylesheet" type="text/css" href="./resources/escidoc-css/css/main.css" />
				<link rel="SHORTCUT ICON" href="./images/escidoc.ico"/>
				<meta http-equiv="pragma" content="no-cache"/>
				<meta http-equiv="cache-control" content="no-cache"/>
				<meta http-equiv="expires" content="0"/>
				<script type="text/javascript" language="JavaScript" src="resources/scripts.js">;</script>
				<title>PubMan Homepage</title>
			</head>
			<body><h:form id="form1">
				<h:outputText value="#{HomePage.beanName}" style="height: 0px; width: 0px; visibility:hidden; position: absolute;" />
				<div id="page_margins">
					<div id="page">
						
							<div id="header">
								<jsp:directive.include file="desktop/Header.jspf" />
								<div class="header">
									<jsp:directive.include file="desktop/Login.jspf" />
									<jsp:directive.include file="desktop/Search.jspf" />
								</div>
							</div>
							<div id="nav">
									<jsp:directive.include file="desktop/Breadcrumb.jspf" />
							</div>
							<div id="main">
								<div id="col1">
									<span class="mainMenu">
										<jsp:directive.include file="desktop/Navigation.jspf" />
									</span>
								</div>
								<div id="col2">
									&#xa0;
								</div>
								<div id="col3">
									<div class="content">
										<jsp:directive.include file="home/ReleaseNotes.jspf" />
									</div>
								</div>
							</div>
						</div>
					</div>
				</h:form>
			</body>
		</html>
	</f:view>
</jsp:root>