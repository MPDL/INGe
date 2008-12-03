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
				<link rel="unapi-server" type="application/xml" title="unAPI" href="#{SearchResultList.unapiURLview}"/>

				<jsp:directive.include file="header/ui/StandardImports.jspf" />

			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText id="pageDummy" value="#{SearchRetrieverRequestBean.beanName}" styleClass="noDisplay" />
			<tr:form usesUpload="true">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<jsp:directive.include file="header/Header.jspf" />

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<jsp:directive.include file="header/Breadcrumb.jspf" />
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.SearchResultListPage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="sub">
								<!-- content menu upper line starts here -->
									<h:commandLink styleClass="free_area0" value="VIEW OPTIONS" action="#{PubItemListSessionBean.changeSubmenuToView}" rendered="#{PubItemListSessionBean.subMenu != 'VIEW'}" />
									<h:outputText styleClass="free_area0" value="VIEW OPTIONS" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}" />
									<h:outputText styleClass="seperator void" />
									<h:commandLink styleClass="free_area0" value="SORTING" action="#{PubItemListSessionBean.changeSubmenuToSorting}" rendered="#{PubItemListSessionBean.subMenu != 'SORTING'}"/>	
									<h:outputText styleClass="free_area0" value="SORTING" rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}" />
									<h:outputText styleClass="seperator void" />
									<h:commandLink styleClass="free_area0" value="EXPORT" action="#{PubItemListSessionBean.changeSubmenuToExport}" rendered="#{PubItemListSessionBean.subMenu != 'EXPORT'}"/>	
									<h:outputText styleClass="free_area0" value="EXPORT" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}" />		
									<h:outputText styleClass="seperator void" />
									<h:commandLink styleClass="free_area0" value="ADD SELECTED TO BASKET" action="#{PubItemListSessionBean.addSelectedToCart}" />		
								<!-- content menu upper line ends here -->
								</div>
								<!-- content menu lower line starts here -->
								<h:panelGroup layout="block" styleClass="sub" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}">
									<h:selectOneMenu value="#{ExportItemsSessionBean.exportFormatType}" styleClass="xLarge_select replace" onchange="$(this).parents('.sub').find('.exportUpdateButton').click();">
											 <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}"/>
									</h:selectOneMenu>
									<h:commandButton styleClass="noDisplay exportUpdateButton" action="#{ExportItems.updateExportFormats}" value="updateExportFormats" />	
									<h:selectOneMenu value="#{ExportItemsSessionBean.fileFormat}" styleClass="medium_select replace" rendered="#{ExportItemsSessionBean.enableLayout}">
										<f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}"/>
									</h:selectOneMenu>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="sub" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}">
									<h:commandButton id="btnDisplayItems" styleClass="free_area0" value="#{lbl.export_btDisplay}" action="#{PubItemListSessionBean.showDisplayExportData}"/>
									<h:outputText styleClass="seperator" />
									<h:commandLink id="btnExportDownload" styleClass="free_area0" value="#{lbl.export_btDownload}" action="#{PubItemListSessionBean.downloadExportFile}" />
									<h:outputText styleClass="seperator" />
									<h:commandLink id="btnExportEMail" styleClass="free_area0" value="#{lbl.export_btEMail}" action="#{PubItemListSessionBean.showExportEmailPage}"/>
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="sub" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}">
								<!-- content menu lower line starts here -->
									<h:commandLink styleClass="free_area0" rendered="#{PubItemListSessionBean.listType == 'GRID'}" action="#{PubItemListSessionBean.changeListTypeToBib}">
										<h:outputText value="Bibliographic list" />
									</h:commandLink>
									<h:outputText styleClass="free_area0" value="Bibliographic list" rendered="#{PubItemListSessionBean.listType == 'BIB'}" />
									<h:outputText styleClass="seperator" />
									<h:commandLink styleClass="free_area0" rendered="#{PubItemListSessionBean.listType == 'BIB'}"  action="#{PubItemListSessionBean.changeListTypeToGrid}">
										<h:outputText value="Grid list" />
									</h:commandLink>
									<h:outputText styleClass="free_area0" value="Grid list" rendered="#{PubItemListSessionBean.listType == 'GRID'}" />
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="sub" rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}">
								<!-- content menu lower line starts here -->
									<h:outputText styleClass="free_area0" value="#{lbl.ItemList_SortBy}: "/>
									<h:selectOneMenu styleClass="xLarge_select replace" id="sortBy" value="#{PubItemListSessionBean.selectedSortBy}" onchange="$(this).parents('div').find('.changeSortBy').click();" >
										<f:selectItems value="#{PubItemListSessionBean.sortBySelectItems}" />
									</h:selectOneMenu>
									<h:commandButton styleClass="noDisplay changeSortBy" value=" "  action="#{PubItemListSessionBean.redirect}"/>
									<h:commandLink styleClass="min_imgArea ascSort" value=" " id="sortOrderAsc" rendered="#{PubItemListSessionBean.isAscending}" action="#{PubItemListSessionBean.changeSortOrder}" />
									<h:commandLink styleClass="min_imgArea desSort" value=" " id="sortOrderDesc" rendered="#{!PubItemListSessionBean.isAscending}" action="#{PubItemListSessionBean.changeSortOrder}" />
								</h:panelGroup>
								<!-- content menu lower line ends here -->
							<!-- content menu ends here -->
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:panelGroup rendered="#{SearchRetrieverRequestBean.searchType == 'advanced'}">
									<h:outputLink styleClass="free_area0 xTiny_marginRIncl" value="AdvancedSearchPage.jsp"><h:outputText value="#{lbl.SearchResultList_lblAdvancedSearch}"/></h:outputLink>
									<a class="free_area0" href="#contentSkipLinkAnchor" onclick="$(this).siblings('.searchQuery').slideToggle('slow'); $(this).hide();"><h:outputText value="Show Query"/></a>
									<h:panelGroup layout="block" styleClass="half_area0_p6 searchQuery" style="display: none;">
										<h2><h:outputText value="#{msg.searchResultList_QueryString}"/></h2>
										<h:outputText value="#{SearchRetrieverRequestBean.cqlQuery}"/>
									</h:panelGroup>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{SearchRetrieverRequestBean.hasErrorMessages}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{SearchRetrieverRequestBean.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{SearchRetrieverRequestBean.hasMessages and !SearchRetrieverRequestBean.hasErrorMessages}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{SearchRetrieverRequestBean.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.listType == 'BIB'}">
						<jsp:directive.include file="list/itemList.jspf" />
					</h:panelGroup>
					<h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.listType == 'GRID'}">
						<jsp:directive.include file="list/gridList.jspf" />
					</h:panelGroup>
				<!-- end: content section -->
				</div>
			
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



<!-- 
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">

<jsp:output doctype-root-element="html"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
			<html>
				<head>
					<link rel="stylesheet" type="text/css" href="./resources/escidoc-css/css/main.css" />
					<link rel="SHORTCUT ICON" href="./images/escidoc.ico"/>
					<link rel="unapi-server" type="application/xml" title="unAPI" href="#{SearchResultList.unapiURLview}"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>				
					
					<script type="text/javascript" language="JavaScript" src="resources/scripts.js">;</script>
				</head>
				<body>
					<h:outputText id="pageDummy" value="#{SearchResultListPage.beanName}" style="height: 0px; width: 0px; visibility:hidden; position: absolute" />
					<div id="page_margins">
						<div id="page">
							<h:form id="form1">
								<div id="header">
									<jsp:directive.include file="desktop/Header.jspf"/>
									<jsp:directive.include file="desktop/Login.jspf"/>
									<jsp:directive.include file="desktop/Search.jspf"/>
								</div>
								<div id="nav">
									<jsp:directive.include file="desktop/Breadcrumb.jspf"/>
								</div>
								<div id="main">
									<div id="col1">
										<span class="mainMenu">
											 <jsp:directive.include file="desktop/Navigation.jspf"/>
										</span>
										<div class="export">
											<jsp:directive.include file="export/Export.jspf"/>
										</div>
									</div>
									<div id="col2">
										&#xa0;
									</div>
									<div id="col3">
										<div class="content">
											<jsp:directive.include file="search/SearchResultList.jspf"/>
										</div>
									</div>
								</div>
							</h:form>
						 </div>
					  </div>
				</body>
			</html>
		
	</f:view>
</jsp:root>
-->