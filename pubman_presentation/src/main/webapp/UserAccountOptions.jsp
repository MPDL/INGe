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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->



	 

	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<ui:include src="header/ui/StandardImports.jspf" />

			</h:head>
			<body lang="${InternationalizationHelper.locale}">
			<h:outputText value="#{UserAccountOptionsPage.beanName}" styleClass="noDisplay" />
			
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<ui:include src="header/Header.jspf" />
				<h:form >
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<ui:include src="header/Breadcrumb.jspf" />
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.UserAccountOptions}"/></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<!-- MessageArea starts here -->
						<h:panelGroup id="messages" styleClass="subHeader">
							<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea small_marginLExcl" rendered="#{UserAccountOptions.hasErrorMessages}">
								<input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
								<h2><h:outputText value="#{lbl.warning_lblMessageHeader}" /></h2>
								<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{UserAccountOptions.hasMessages}"/>
							</h:panelGroup>
							<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea small_marginLExcl" rendered="#{UserAccountOptions.hasMessages and !UserAccountOptions.hasErrorMessages}">
								<input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
								<h2><h:outputText value="#{lbl.info_lblMessageHeader}" /></h2>
								<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{UserAccountOptions.hasMessages}"/>
							</h:panelGroup>
						</h:panelGroup>
						<!-- MessageArea ends here -->
					</div>
					<div class="full_area0">
						<div class="full_area0 fullItem">
							<div class="full_area0 small_marginLExcl">
								<!-- Subheadline starts here -->
								<h3>
									<h:outputText value="#{msg.userAccountOptions_UserInformation}"/>
								</h3>
								<!-- Subheadline ends here -->
							</div>
							<ui:include src="userAccountOptions/Password.jspf" />
						</div>
					</div>	
				</div>
				<!-- end: content section -->
				</h:form>
			</div>
			<ui:include src="footer/Footer.jspf" />
			
			<script type="text/javascript">
				var passArea = $('.passArea'); 
				passArea.find("input[type=password]").keyup(function(keyEvent) {
					var key = keyEvent.keyCode;
					if(key == '13') {
						passArea.find('.activeButton').trigger("click");
					};
				});
				
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
				});
			</script>
			</body>
		</html>
	</f:view>
