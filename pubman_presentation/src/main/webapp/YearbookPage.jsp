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
				<link rel="unapi-server" type="application/xml" title="unAPI" href="#{YearbookCandidatesRetrieverRequestBean.unapiURLview}"/>

				<jsp:directive.include file="header/ui/StandardImports.jspf" />

			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText value="#{YearbookCandidatesRetrieverRequestBean.beanName}" styleClass="noDisplay" />
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
								<h1><h:outputText value="#{lbl.YearbookPage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">

							<h:panelGroup layout="block" styleClass="contentMenu" rendered="#{YearbookItemSessionBean.yearbookItem!=null}">
							<!-- content menu starts here -->
							
								<div class="free_area0 sub">
									<h:commandLink id="lnkChangeToCandidates" styleClass="free_area0" action="#{YearbookItemSessionBean.changeToCandidates}" rendered="#{YearbookItemSessionBean.selectedWorkspace!='CANDIDATES'}">
										<h:outputText value="#{lbl.YearbookCandidatesPage}"/>
									</h:commandLink>
									<h:outputText id="txtChangeToCandidates" styleClass="free_area0" value="#{lbl.YearbookCandidatesPage}" rendered="#{YearbookItemSessionBean.selectedWorkspace=='CANDIDATES'}"/>
									
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeToMembers" styleClass="free_area0" action="#{YearbookItemSessionBean.changeToMembers}" rendered="#{YearbookItemSessionBean.selectedWorkspace!='MEMBERS'}">
										<h:outputText value="#{lbl.YearbookMembersPage} (#{YearbookItemSessionBean.numberOfMembers})"/>
									</h:commandLink>
									<h:outputText id="txtChangeToMembers" styleClass="free_area0" value="#{lbl.YearbookMembersPage} (#{YearbookItemSessionBean.numberOfMembers})" rendered="#{YearbookItemSessionBean.selectedWorkspace=='MEMBERS'}"/>
									
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeToInvalidItems" styleClass="free_area0" action="#{YearbookItemSessionBean.changeToInvalidItems}" rendered="#{YearbookItemSessionBean.selectedWorkspace!='INVALID'}">
										<h:outputText value="#{lbl.YearbookInvalidItemsPage}"/>
									</h:commandLink>
									<h:outputText id="txtChangeToInvalidItems" styleClass="free_area0" value="#{lbl.YearbookInvalidItemsPage}" rendered="#{YearbookItemSessionBean.selectedWorkspace=='INVALID'}"/>
									
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeToNonCandidates" styleClass="free_area0" action="#{YearbookItemSessionBean.changeToNonCandidates}" rendered="#{YearbookItemSessionBean.selectedWorkspace!='NON_CANDIDATES'}">
										<h:outputText value="#{lbl.YearbookNonCandidatesPage}"/>
									</h:commandLink>
									<h:outputText id="txtChangeToNonCandidates" styleClass="free_area0" value="#{lbl.YearbookNonCandidatesPage}" rendered="#{YearbookItemSessionBean.selectedWorkspace=='NON_CANDIDATES'}"/>
									
								</div>
							
								<div class="free_area0 sub action">
								<!-- content menu upper line starts here -->
									<h:commandLink id="lnkChangeSubmenuToView" title="#{tip.List_lblViewOptions}" styleClass="free_area0" value="#{lbl.List_lblViewOptions}" action="#{PubItemListSessionBean.changeSubmenuToView}" rendered="#{PubItemListSessionBean.subMenu != 'VIEW'}" />
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblViewOptions}" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}" />
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeSubmenuToFilter" title="#{tip.List_lblFilterOptions}" styleClass="free_area0" value="#{lbl.List_lblFilterOptions}" action="#{PubItemListSessionBean.changeSubmenuToFilter}" rendered="#{PubItemListSessionBean.subMenu != 'FILTER'}"/>
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblFilterOptions}" rendered="#{PubItemListSessionBean.subMenu == 'FILTER'}" />
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkChangeSubmenuToSorting" title="#{tip.List_lblSortOptions}" styleClass="free_area0" value="#{lbl.List_lblSortOptions}" action="#{PubItemListSessionBean.changeSubmenuToSorting}" rendered="#{PubItemListSessionBean.subMenu != 'SORTING'}"/>	
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblSortOptions}" rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}" />
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkAddToYearbook" styleClass="free_area0" value="#{lbl.Yearbook_addToYearbook}" action="#{YearbookCandidatesRetrieverRequestBean.addSelectedToYearbook}" rendered="#{YearbookItemSessionBean.selectedWorkspace=='CANDIDATES' and YearbookItemSessionBean.yearbookItem.version.state=='PENDING'}"/>
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkRemoveFromYearbook" styleClass="free_area0" value="#{lbl.Yearbook_removeFromYearbook}" action="#{YearbookCandidatesRetrieverRequestBean.removeSelectedFromYearbook}" rendered="#{YearbookItemSessionBean.selectedWorkspace=='MEMBERS' and YearbookItemSessionBean.yearbookItem.version.state=='PENDING'}"/>
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkValidate" styleClass="free_area0" value="#{lbl.Yearbook_validate}" action="#{YearbookItemSessionBean.validateYearbook}" onclick="fullItemReload();" rendered="#{YearbookItemSessionBean.selectedWorkspace=='MEMBERS' || YearbookItemSessionBean.selectedWorkspace=='INVALID'}"/>
									<h:outputText styleClass="seperator void" />
									<h:commandLink id="lnkReleaseYearbook" styleClass="free_area0" value="#{lbl.Yearbook_releaseYearbook}" action="#{YearbookItemSessionBean.releaseYearbook}" rendered="#{YearbookItemSessionBean.selectedWorkspace=='MEMBERS' and YearbookItemSessionBean.yearbookItem.version.state=='PENDING' and ContextListSessionBean.yearbookContextListSize>0}"/>
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
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'FILTER'}">
								<!-- content menu lower line starts here -->

									<h:outputText styleClass="free_area0 clearLeft" value="#{lbl.qaws_lblOrgUnitSelection} "/>
									<h:selectOneMenu id="selSelectedOrgUnit" styleClass="xDouble_select replace" value="#{YearbookCandidatesRetrieverRequestBean.selectedOrgUnit}" onchange="$(this).parents('div').find('.changeOrgUnit').click();">
										<f:selectItems value="#{YearbookCandidatesRetrieverRequestBean.orgUnitSelectItems}"/>
									</h:selectOneMenu>
									<h:commandButton id="btChangeOrgUnit" styleClass="noDisplay changeOrgUnit" action="#{YearbookCandidatesRetrieverRequestBean.changeOrgUnit}" value="change org unit"/>
								<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}">
								<!-- content menu lower line starts here -->
									<h:outputText styleClass="free_area0" value="#{lbl.ItemList_SortBy}: "/>
									<h:selectOneMenu styleClass="xLarge_select replace" id="sortBy" value="#{PubItemListSessionBean.selectedSortBy}" onchange="$(this).parents('div').find('.changeSortBy').click();" >
										<f:selectItems value="#{PubItemListSessionBean.sortBySelectItems}" />
									</h:selectOneMenu>
									<h:commandLink title="#{tip.list_ascending}" styleClass="ascSort" value="#{lbl.ItemList_SortOrderAscending}" id="sortOrderAsc" rendered="#{PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}" action="#{PubItemListSessionBean.changeSortOrder}" />
									<h:commandLink title="#{tip.list_descending}" styleClass="desSort" value="#{lbl.ItemList_SortOrderDescending}" id="sortOrderDesc" rendered="#{!PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}" action="#{PubItemListSessionBean.changeSortOrder}" />
									<h:commandButton id="btnChangeSortBy" title="#{tip.list_btSortBy}" styleClass="noDisplay changeSortBy" value=" "  action="#{PubItemListSessionBean.changeSortBy}"/>
								<!-- content menu lower line ends here -->
								</h:panelGroup>

							<!-- content menu ends here -->
							</h:panelGroup>
							<h:panelGroup layout="block" styleClass="subHeader" rendered="#{YearbookItemSessionBean.yearbookItem!=null}">
								<h:outputText value="#{PubItemListSessionBean.totalNumberOfElements} #{lbl.SearchResultList_lblItems}"/>
								<h:outputText value=" ("/>
 								<h:outputText value="#{lbl.ENUM_SORTORDER_ASCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}" rendered="#{PubItemListSessionBean.isAscending}"/>
								<h:outputText value="#{lbl.ENUM_SORTORDER_DESCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}" rendered="#{!PubItemListSessionBean.isAscending}"/>
								<h:outputText value=")"/>	
							</h:panelGroup>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookCandidatesRetrieverRequestBean.numberOfMessages == 1}"/>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{YearbookCandidatesRetrieverRequestBean.hasErrorMessages and YearbookCandidatesRetrieverRequestBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookCandidatesRetrieverRequestBean.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{YearbookCandidatesRetrieverRequestBean.hasMessages and !YearbookCandidatesRetrieverRequestBean.hasErrorMessages and YearbookCandidatesRetrieverRequestBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookCandidatesRetrieverRequestBean.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					
					<h:panelGroup rendered="#{YearbookItemSessionBean.yearbookItem!=null}">
						<h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.listType == 'BIB' and PubItemListSessionBean.partListSize>0}">
							<jsp:directive.include file="list/itemList.jspf" />
						</h:panelGroup>
						<h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.listType == 'GRID' and PubItemListSessionBean.partListSize>0}">
							<jsp:directive.include file="list/gridList.jspf" />
						</h:panelGroup>
						<h:panelGroup styleClass="full_area0" rendered="#{PubItemListSessionBean.partListSize==0}">
							<h:outputText styleClass="free_area0 small_marginLExcl" value="#{msg.depositorWS_valNoItemsMsg}"/>
						</h:panelGroup>
						
					</h:panelGroup>
					<h:panelGroup rendered="#{YearbookItemSessionBean.yearbookItem==null}">
						<h:panelGroup styleClass="full_area0">
							<h:outputText styleClass="free_area0 small_marginLExcl" value="#{msg.Yearbook_noYearbookFound}"/>
							<h:outputLink styleClass="free_area0 small_marginLExcl" value="YearbookItemCreatePage.jsp" rendered="#{ContextListSessionBean.yearbookContextListSize>0}">
								<h:outputText value="#{lbl.Yearbook_createYearbook}"/>
							</h:outputLink>
						</h:panelGroup>
					</h:panelGroup>
					
					
					
				<div id="ImgFullItem">
					<div id="ImgFullItemLoad" class="noDisplay" style="position: fixed;"></div>
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