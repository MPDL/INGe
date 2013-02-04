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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:rich="http://richfaces.org/rich" xmlns:a4j="http://richfaces.org/a4j" >

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
				<!-- <% /*
				<link rel="unapi-server" type="application/xml" title="unAPI" href="#{MyTasksRetrieverRequestBean.unapiURLview}"/>
				*/ %> -->
				<jsp:directive.include file="header/ui/StandardImports.jspf" />

			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText value="#{UserAccountOptionsPage.beanName}" styleClass="noDisplay" />
			<h:form >
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
								<h1><h:outputText value="#{lbl.UserAccountOptions}"/></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<!-- MessageArea starts here -->
						<h:panelGroup id="messages" styleClass="subHeader">
							<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{UserAccountOptions.hasErrorMessages}">
								<input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$pb(this).parents('.messageArea').removeClass('absoluteMessageArea'); $pb(this).hide();" />
								<h2><h:outputText value="#{lbl.warning_lblMessageHeader}" /></h2>
								<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{UserAccountOptions.hasMessages}"/>
							</h:panelGroup>
							<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea" rendered="#{UserAccountOptions.hasMessages and !UserAccountOptions.hasErrorMessages}">
								<input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$pb(this).parents('.messageArea').removeClass('absoluteMessageArea'); $pb(this).hide();" />
								<h2><h:outputText value="#{lbl.info_lblMessageHeader}" /></h2>
								<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{UserAccountOptions.hasMessages}"/>
							</h:panelGroup>
						</h:panelGroup>
						<!-- MessageArea ends here -->
						<div class="full_area0">
							<div class="full_area0 fullItem">
								<div class="full_area0 small_marginLExcl">
									<!-- Subheadline starts here -->
									<h3>
										<h:outputText value="#{msg.userAccountOptions_UserInformation}"/>
									</h3>
									<!-- Subheadline ends here -->
								</div>
								<jsp:directive.include file="userAccountOptions/Password.jspf"/>
							</div>
						</div>	
					</div>
				</div>
				<!-- end: content section -->
			</div>
			<jsp:directive.include file="footer/Footer.jspf" />
			</h:form>
			<script type="text/javascript">
				$pb("input[id$='offset']").submit(function() {
					$pb(this).val($pb(window).scrollTop());
				});
				$pb(document).ready(function () {
					$pb(window).scrollTop($pb("input[id$='offset']").val());
					$pb(window).scroll(function(){$pb("input[id$='offset']").val($pb(window).scrollTop());});
				});
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>