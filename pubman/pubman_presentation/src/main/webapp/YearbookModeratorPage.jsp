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

<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:tr="http://myfaces.apache.org/trinidad" xmlns:ui="http://java.sun.com/jsf/facelets">

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

			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText value="#{YearbookModeratorRetrieverRequestBean.beanName}" styleClass="noDisplay" />
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
								<h1><h:outputText value="#{lbl.YearbookModeratorPage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">

							<h:panelGroup layout="block" styleClass="contentMenu">
							<!-- content menu starts here -->
							

								<div class="free_area0 sub action">
								<!-- content menu upper line starts here -->
									<h:commandLink id="lnkChangeSubmenuToView" title="#{tip.List_lblViewOptions}" styleClass="free_area0" value="#{lbl.List_lblViewOptions}" action="#{PubItemListSessionBean.changeSubmenuToView}" rendered="#{PubItemListSessionBean.subMenu != 'VIEW'}" />
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblViewOptions}" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}" />
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeSubmenuToExport" title="#{tip.List_lblExportOptions}" styleClass="free_area0" value="#{lbl.List_lblExportOptions}" action="#{PubItemListSessionBean.changeSubmenuToExport}" rendered="#{PubItemListSessionBean.subMenu != 'EXPORT'}"/>	
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblExportOptions}" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}" />
									<!-- 
									<h:outputText styleClass="seperator void" />
									<h:outputLink id="lnkChangeToCreateItem" title="#{tip.List_lblViewOptions}" styleClass="free_area0" value="YearbookItemCreatePage.jsp" >
										<h:outputText value="#{lbl.Yearbook_createYearbook}"/>
									</h:outputLink>
									-->
									
									&#160;
								<!-- content menu upper line ends here -->
								</div>
								
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}">
								<!-- content menu lower line starts here -->
									<h:commandLink id="lnkChangeListTypeToBib" title="#{tip.List_lblBibList}" styleClass="free_area0" rendered="#{PubItemListSessionBean.listType == 'GRID'}" action="#{PubItemListSessionBean.changeListTypeToBib}">
										<h:outputText value="#{lbl.List_lblBibList}" />
									</h:commandLink>
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblBibList}" rendered="#{PubItemListSessionBean.listType == 'BIB'}" />
									<h:outputText styleClass="seperator" />
									<h:commandLink id="lnkChangeListTypeToGrid" title="#{tip.List_lblGridList}" styleClass="free_area0" rendered="#{PubItemListSessionBean.listType == 'BIB'}"  action="#{PubItemListSessionBean.changeListTypeToGrid}">
										<h:outputText value="#{lbl.List_lblGridList}" />
									</h:commandLink>
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblGridList}" rendered="#{PubItemListSessionBean.listType == 'GRID'}" />
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}">
									<h:selectOneMenu id="selExportFormatName" value="#{ExportItemsSessionBean.exportFormatName}" styleClass="xLarge_select replace" onchange="$(this).parents('.sub').find('.exportUpdateButton').click();">
											 <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS_EXTENDED}"/>
									</h:selectOneMenu>
									<h:commandButton id="btnUpdateExportFormats" title="#{tip.export_btFormat}" styleClass="noDisplay exportUpdateButton" action="#{ExportItems.updateExportFormats}" value="updateExportFormats" />	
									<h:selectOneMenu id="selFileFormat" value="#{ExportItemsSessionBean.fileFormat}" styleClass="medium_select replace" rendered="#{ExportItemsSessionBean.enableFileFormats}">
										<f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}"/>
									</h:selectOneMenu>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}">
									<!--
									<h:commandButton title="#{tip.export_btDisplay}" id="btnDisplayItems" styleClass="free_area0" value="#{lbl.export_btDisplay}" action="#{PubItemListSessionBean.exportSelectedDisplay}"/>
									<h:outputText styleClass="seperator" />
									 -->
									<h:commandLink title="#{tip.export_btDownload}" id="btnExportDownload" styleClass="free_area0" value="#{lbl.export_btDownload}" action="#{YearbookModeratorRetrieverRequestBean.exportSelectedDownload}" />

								</h:panelGroup>
								
							<!-- content menu ends here -->
							</h:panelGroup>
							<h:panelGroup layout="block" styleClass="subHeader">
								<h:outputText value="#{PubItemListSessionBean.totalNumberOfElements} #{lbl.SearchResultList_lblItems}"/>
								<h:outputText value=" ("/>
 								<h:outputText value="#{lbl.ENUM_SORTORDER_ASCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}" rendered="#{PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}"/>
								<h:outputText value="#{lbl.ENUM_SORTORDER_DESCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}" rendered="#{!PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}"/>
								<h:outputText value=")"/>	
							</h:panelGroup>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookModeratorRetrieverRequestBean.numberOfMessages == 1}"/>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{YearbookModeratorRetrieverRequestBean.hasErrorMessages and YearbookModeratorRetrieverRequestBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookModeratorRetrieverRequestBean.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{YearbookModeratorRetrieverRequestBean.hasMessages and !YearbookModeratorRetrieverRequestBean.hasErrorMessages and YearbookModeratorRetrieverRequestBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookModeratorRetrieverRequestBean.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					
					<h:panelGroup>
						<h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.partListSize>0}">
							<jsp:directive.include file="yearbook/yearbookModeratorList.jspf" />
						</h:panelGroup>
						<h:panelGroup styleClass="full_area0" rendered="#{PubItemListSessionBean.partListSize==0}">
							<h:outputText styleClass="free_area0 small_marginLExcl" value="#{msg.depositorWS_valNoItemsMsg}"/>
						</h:panelGroup>
						
					</h:panelGroup>
				<div id="ImgFullItem">
					<div id="ImgFullItemLoad" class="noDisplay" style="position: fixed;">&#160;</div>
				</div>
				<!-- end: content section -->
				</div>
				
			</div>
			<jsp:directive.include file="footer/Footer.jspf" />
			</tr:form>
			<script type="text/javascript">
			<![CDATA[
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});

					var element = document.getElementById('selSelectedOrgUnit');
					if (element.options != null && element.options.length == 2)
					{
						throb();
						$.getJSON('AffiliationsAsJSON.jsp', loadAffiliations);
					}
				});
				]]>
			</script>
			<script type="text/javascript">
				function fullItemReload()
				{
					document.getElementById('content').style.opacity='0.4';
					document.getElementById('content').style.bg='FFF';
					document.getElementById('ImgFullItemLoad').setAttribute('class','big_imgArea half_marginLIncl smallThrobber');
					$('#fullItem :input :text').attr('readonly', true);
				    $('#fullItem :textarea').attr('readonly', true);
				}
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>