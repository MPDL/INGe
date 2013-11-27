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


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">

<jsp:output doctype-root-element="html"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" />
	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		omit-xml-declaration="true" />
	<f:view xmlns:e="http://www.escidoc.de/jsf">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<jsp:directive.include file="header/ui/StandardImports.jspf" />
				<style>
					.mainMenu a:hover, .mainMenu a:focus {
						background-image:url("../images/BG_MainNavi_hover_bold.png");
					}
				</style>
			</head>
			<body>
				<h:form id="form1">
					<div class="full wrapper">
					<h:inputHidden id="offset"></h:inputHidden>
						<span class="full_area0 metaMenu">
							<h:panelGroup styleClass="seperator"></h:panelGroup>
						</span>
						
						<div class="LogoNSearch">
							<h:outputLink id="lnkStartPage" title="#{tip.navigation_lblStartpage}" value="#{ApplicationBean.appContext}HomePage.jsp">
								<span class="tiny_marginLExcl quad_area0 headerLogo">
									<h:panelGroup styleClass="quad_area0 themePark #{Header.serverLogo}"></h:panelGroup>
								</span>
							</h:outputLink>
						</div>
						<div id="mainMenuSkipLinkAnchor" class="full_area0 mainMenu">
							<h:outputLink id="lnkHome" styleClass="free_area0" style="margin-top:0.5em" title="#{tip.mainMenu_lnkHome}" value="#{ApplicationBean.appContext}HomePage.jsp">
								<h:outputText value="#{lbl.mainMenu_lnkHome}" />
							</h:outputLink>
						</div>
						<div id="content" class="full_area0 clear" style="margin-top:4em; ">
							<span style="font-size: 140%;line-height:140%"> 
								<p style="margin-left:4em; margin-right:4em;">
									This publication has been assigned a persistent identifier (PID), but the identifier has not yet been registered in the worldwide Handle system. This may take some time. If you think the registration is overdue, please refer to the PubMan support team.
								</p>
								<p style="margin-left:4em; margin-right:4em;padding-top:1em;border-top-color: #B0B1B1;border-top-style: solid;border-top-width: 0.09em">
									Dieser Publikation wurde ein persistenter Identifikator (PID) zugewiesen, der Identifikator wurde aber noch nicht im weltweiten Handle-System registriert. Dies kann einige Zeit dauern. Falls Sie der Meinung sind, die Registrierung sei überfällig, wenden Sie sich bitte an das PubMan-Support-Team.
								</p>
							</span>		
						</div>
					</div>
					<jsp:directive.include file="footer/Footer.jspf" />
				</h:form>
				<script type="text/javascript">
				$pb("input[id$='offset']").submit(function() {
					$pb(this).val($pb(window).scrollTop());
				});
				$pb(document).ready(function () {
					$pb(window).scrollTop($pb("input[id$='offset']").val());
					$pb(window).scroll(function(){$pb("input[id$='offset']").val($pb(window).scrollTop())});
				});
				</script>
			</body>
		</html>
	</f:view>
</jsp:root>