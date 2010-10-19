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


 Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
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
				<script src="./resources/eSciDoc_JavaScript/jquery/jquery.jdialog.min.js" language="JavaScript" type="text/javascript">;</script>

				<script type='text/javascript' src='http://api.creativecommons.org/jswidget/tags/0.96/complete.js?locale=#{PubManSessionBean.locale}&amp;want_a_license=definitely'>;</script>
				<link rel="stylesheet" href="http://labs.creativecommons.org/demos/jswidget/tags/0.97/example_web_app/example-widget-style.css" />

			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText value="#{YearbookItemCreateBean.beanName}" styleClass="noDisplay" />
			<tr:form usesUpload="true">
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
								<h1><h:outputText value="Create Yearbook Item" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">

							<h:panelGroup layout="block" styleClass="contentMenu">
							<!-- content menu starts here -->
							
								eee

							<!-- content menu ends here -->
							</h:panelGroup>
							
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.numberOfMessages == 1}"/>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{YearbookItemCreateBean.hasErrorMessages and YearbookItemCreateBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{YearbookItemCreateBean.hasMessages and !YearbookItemCreateBean.hasErrorMessages and YearbookItemCreateBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div id="fullItem" class="full_area0">
						
						<h:panelGroup layout="block" styleClass="full_area0 itemBlock">
							<h3 class="xLarge_area0_p8 endline blockHeader">
								<h:outputText value="Yearbook Information"/>
							</h3>
							<span class="seperator"></span>
							<span class="free_area0 itemBlockContent endline">
							
								<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<h:outputText styleClass="mandatory" title="#{msg.mandatoryField}" value="*"/>
										<h:outputText value="Title"/>
										<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<h:inputTextarea id="inputTitleText" styleClass="quad_txtArea inputTxtArea" value="#{YearbookItemCreateBean.title}" cols="15" rows="3" ></h:inputTextarea>
									</span>
									<!-- 
									<span class="large_area0_p8 lineToolSection">
										<h:commandButton id="btnAddTitle" title="#{tip.EditItem_addTitle}" styleClass="min_imgBtn groupTool add" value=" " action="#{EditItem.titleCollection.addTitle}" rendered="#{genre.item_basic_item_title_alternative_repeatable != 'false'}"/>
										<h:commandButton id="btnRemoveTitle" title="#{tip.EditItem_removeTitle}" styleClass="min_imgBtn groupTool remove" value=" " disabled="#{EditItem.titleCollection.alternativeTitleManager.size == 0}" action="#{EditItem.titleCollection.removeTitle}" rendered="#{genre.item_basic_item_title_alternative_repeatable != 'false'}"/>
									</span>
									-->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<h:outputText styleClass="mandatory" title="#{msg.mandatoryField}" value="*"/>
										<h:outputText value="Organization Id"/>
										<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<h:inputText id="inputOrgId" styleClass="large_txtInput" value="#{YearbookItemCreateBean.orgId}" />
									</span>
									<!-- 
									<span class="large_area0_p8 lineToolSection">
										<h:commandButton id="btnAddTitle" title="#{tip.EditItem_addTitle}" styleClass="min_imgBtn groupTool add" value=" " action="#{EditItem.titleCollection.addTitle}" rendered="#{genre.item_basic_item_title_alternative_repeatable != 'false'}"/>
										<h:commandButton id="btnRemoveTitle" title="#{tip.EditItem_removeTitle}" styleClass="min_imgBtn groupTool remove" value=" " disabled="#{EditItem.titleCollection.alternativeTitleManager.size == 0}" action="#{EditItem.titleCollection.removeTitle}" rendered="#{genre.item_basic_item_title_alternative_repeatable != 'false'}"/>
									</span>
									-->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<h:outputText styleClass="mandatory" title="#{msg.mandatoryField}" value="*"/>
										<h:outputText value="Context Ids (comma seperated)"/>
										<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<h:inputText id="inputContextId" styleClass="xHuge_txtInput" value="#{YearbookItemCreateBean.contextIds}" />
									</span>
									<!-- 
									<span class="large_area0_p8 lineToolSection">
										<h:commandButton id="btnAddTitle" title="#{tip.EditItem_addTitle}" styleClass="min_imgBtn groupTool add" value=" " action="#{EditItem.titleCollection.addTitle}" rendered="#{genre.item_basic_item_title_alternative_repeatable != 'false'}"/>
										<h:commandButton id="btnRemoveTitle" title="#{tip.EditItem_removeTitle}" styleClass="min_imgBtn groupTool remove" value=" " disabled="#{EditItem.titleCollection.alternativeTitleManager.size == 0}" action="#{EditItem.titleCollection.removeTitle}" rendered="#{genre.item_basic_item_title_alternative_repeatable != 'false'}"/>
									</span>
									-->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<h:outputText styleClass="mandatory" title="#{msg.mandatoryField}" value="*"/>
										<h:outputText value="Date"/>
										<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
									
									<h:panelGroup styleClass="large_area0 tiny_marginRIncl">
										<h:outputLabel styleClass="large_label" for="txtDateFrom" value="from"/>
										<h:inputText styleClass="large_txtInput dateJSInput" id="txtDateFrom" value="#{YearbookItemCreateBean.dateFrom}" />
									</h:panelGroup>
									<h:panelGroup styleClass="large_area0 tiny_marginRIncl">
										<h:outputLabel styleClass="large_label" for="txtDateTo" value="to"/>
										<h:inputText styleClass="large_txtInput dateJSInput" id="txtDateTo" value="#{YearbookItemCreateBean.dateTo}" />
									</h:panelGroup>
										
									</span>
									<!-- 
									<span class="large_area0_p8 lineToolSection">
										<h:commandButton id="btnAddTitle" title="#{tip.EditItem_addTitle}" styleClass="min_imgBtn groupTool add" value=" " action="#{EditItem.titleCollection.addTitle}" rendered="#{genre.item_basic_item_title_alternative_repeatable != 'false'}"/>
										<h:commandButton id="btnRemoveTitle" title="#{tip.EditItem_removeTitle}" styleClass="min_imgBtn groupTool remove" value=" " disabled="#{EditItem.titleCollection.alternativeTitleManager.size == 0}" action="#{EditItem.titleCollection.removeTitle}" rendered="#{genre.item_basic_item_title_alternative_repeatable != 'false'}"/>
									</span>
									-->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										&#160;<h:outputText styleClass="mandatory" title="#{msg.mandatoryField}" value="*"/>
										<h:outputText value="Yearbook User Id (Collaborator Rights are granted)"/>
										<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<h:inputText id="inputUserId" styleClass="xHuge_txtInput" value="#{YearbookItemCreateBean.collaboratorUserIds}" />
									</span>
								</h:panelGroup>
							</span>
						</h:panelGroup>
						</div>
						
						<div class="full_area0 formButtonArea">
							<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSave" value="Save" action="#{YearbookItemCreateBean.save}"/>
							
							
						</div>
						
						
					</div>
				<!-- end: content section -->
				</div>
			</div>
			<jsp:directive.include file="footer/Footer.jspf" />
			</tr:form>
			<script type="text/javascript">
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
</jsp:root>