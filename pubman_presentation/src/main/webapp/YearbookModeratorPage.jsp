<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core"
	xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui"
	xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
	<title><h:outputText value="#{ApplicationBean.appTitle}" /></title>
	<ui:include src="header/ui/StandardImports.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
	<f:event type="preRenderView" listener="#{YearbookModeratorPage.init}" />
	<f:event type="preRenderView" listener="#{YearbookModeratorRetrieverRequestBean.init}" />
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label" />
		<f:loadBundle var="msg" basename="Messages" />
		<f:loadBundle var="tip" basename="Tooltip" />
		<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			<ui:include src="header/Header.jspf" />
			<h:form id="form1">
				<div id="content" class="full_area0 clear">
					<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							<ui:include src="header/Breadcrumb.jspf" />
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1>
									<h:outputText value="#{lbl.YearbookModeratorPage}" />
								</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<h:panelGroup layout="block" styleClass="contentMenu">
								<!-- content menu starts here -->
								<h:panelGroup layout="block" styleClass="free_area0 sub">
									<h:outputLink id="lnkMenuQAWorkspace" title="#{tip.chooseWorkspace_QAWorkspace}"
										value="#{ApplicationBean.appContext}QAWSPage.jsp"
										rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuQAWorkspace}"
											rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}" />
									</h:outputLink>
									<h:outputText styleClass="seperator void"
										rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									<h:outputLink id="lnkSubmission_lnkImportWorkspaceMenu" title="#{tip.chooseWorkspace_ImportWorkspace}"
										value="#{ApplicationBean.appContext}ImportWorkspace.jsp"
										rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuImportWorkspace}" />
									</h:outputLink>
									<h:panelGroup id="txtMenuYearbookWorkspace" rendered="#{LoginHelper.isYearbookEditor}">
										<h:outputText styleClass="seperator void" />
										<h:outputText value="#{lbl.chooseWorkspace_optMenuYearbookWorkspace}" />
									</h:panelGroup>
									<h:outputText styleClass="seperator void"
										rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}" />
									<h:outputLink id="lnkMenuReportWorkspace" title="#{tip.chooseWorkspace_ReportWorkspace}"
										value="#{ApplicationBean.appContext}ReportWorkspacePage.jsp"
										rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuReportWorkspace}" />
									</h:outputLink>
								</h:panelGroup>
								<div class="free_area0 sub action">
									<!-- content menu upper line starts here -->
									
                                    <h:commandLink id="lnkChangeSubmenuToFilter" title="#{tip.List_lblFilterOptions}" styleClass="free_area0" value="#{lbl.List_lblFilterOptions}" action="#{YearbookModeratorListSessionBean.changeSubmenuToFilter}" rendered="#{YearbookModeratorListSessionBean.subMenu != 'FILTER'}" onclick="fullItemReloadAjax();" />
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblFilterOptions}" rendered="#{YearbookModeratorListSessionBean.subMenu == 'FILTER'}" />
                                    <h:outputText styleClass="seperator void" />
                                    
                                    <h:commandLink id="lnkChangeSubmenuToSorting" title="#{tip.List_lblSortOptions}" styleClass="free_area0" value="#{lbl.List_lblSortOptions}" action="#{YearbookModeratorListSessionBean.changeSubmenuToSorting}" rendered="#{YearbookModeratorListSessionBean.subMenu != 'SORTING'}" onclick="fullItemReloadAjax();" />
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblSortOptions}" rendered="#{YearbookModeratorListSessionBean.subMenu == 'SORTING'}" />
                                    <h:outputText styleClass="seperator void" />
									
									<h:commandLink id="lnkChangeSubmenuToExport" title="#{tip.List_lblExportOptions}" styleClass="free_area0"
										value="#{lbl.List_lblExportOptions}" action="#{YearbookModeratorListSessionBean.changeSubmenuToExport}"
										rendered="#{YearbookModeratorListSessionBean.subMenu != 'EXPORT'}" onclick="fullItemReloadAjax();" />
									<h:outputText styleClass="free_area0" value="#{lbl.List_lblExportOptions}"
										rendered="#{YearbookModeratorListSessionBean.subMenu == 'EXPORT'}" />
									
									<h:panelGroup rendered="#{LoginHelper.isYearbookEditor}">
										<h:outputText styleClass="seperator void" />
	                                    <h:outputLink styleClass="free_area0"  id="lnkCreateYearbook" value="#{ApplicationBean.appContext}YearbookItemCreatePage.jsp" >
										<h:outputText value="#{lbl.Yearbook_createYearbook}" />
										</h:outputLink>
									</h:panelGroup>
                                   
									
									<h:panelGroup rendered="#{LoginHelper.isYearbookAdmin}">
										<h:outputText styleClass="seperator void" />
										<h:commandLink id="lnkSendBackForReworkYearbook" styleClass="free_area0"
											value="#{lbl.Yearbook_sendBackForReworkYearbook}"
											action="#{YearbookModeratorRetrieverRequestBean.sendBackForRework}" onclick="fullItemReloadAjax();"  />
										<h:outputText styleClass="seperator void" />
										<h:commandLink id="lnkReleaseYearbook" styleClass="free_area0" value="#{lbl.Yearbook_releaseYearbook}"
											action="#{YearbookModeratorRetrieverRequestBean.releaseSelectedYearbooks}" onclick="fullItemReloadAjax();"/>
									</h:panelGroup>
									&#160;
									<!-- content menu upper line ends here -->
								</div>
								<h:panelGroup layout="block" styleClass="quad_area0 sub action" rendered="#{YearbookModeratorListSessionBean.subMenu == 'FILTER'}">
                                    <!-- content menu lower line starts here -->
                                    <h:outputText styleClass="medium_label" value="#{lbl.ENUM_CRITERIA_STATE}" />
                                    <h:panelGroup layout="block" styleClass="xDouble_area1 endline selectContainer">
                                        <h:panelGroup layout="block" styleClass="xDouble_area0">
                                            <h:panelGroup styleClass="xDouble_area0 selectionBox">&#160;</h:panelGroup>
                                            <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
                                        </h:panelGroup>
                                        <h:selectOneMenu id="cboItemstate" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{YearbookModeratorRetrieverRequestBean.selectedItemState}" onchange="$(this).parents('div').find('.changeState').click();">
                                            <f:selectItems id="selectItems" value="#{YearbookModeratorRetrieverRequestBean.itemStateSelectItems}" />
                                        </h:selectOneMenu>
                                    </h:panelGroup>
                                    <h:commandButton id="btnChangeItemState" title="#{tip.list_btChangeState}" styleClass="noDisplay changeState" value=" " action="#{YearbookModeratorRetrieverRequestBean.changeItemState}" />
                                </h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action"
									rendered="#{YearbookModeratorListSessionBean.subMenu == 'SORTING'}">
									<!-- content menu lower line starts here -->
									<h:outputText styleClass="free_area0" value="#{lbl.ItemList_SortBy} " />
									<h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
										<h:panelGroup layout="block" styleClass="xLarge_area0">
											<h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="sortBy" styleClass="replace" onfocus="updateSelectionBox(this);"
											value="#{YearbookModeratorListSessionBean.selectedSortBy}"
											onchange="$(this).parents('div').find('.changeSortBy').click();">
											<f:selectItems value="#{YearbookModeratorListSessionBean.sortBySelectItems}" />
										</h:selectOneMenu>
									</h:panelGroup>
									<h:commandLink title="#{tip.list_ascending}" styleClass="ascSort" value="#{lbl.ItemList_SortOrderAscending}"
										id="sortOrderAsc"
										rendered="#{YearbookModeratorListSessionBean.isAscending and YearbookModeratorListSessionBean.displaySortOrder}"
										action="#{YearbookModeratorListSessionBean.changeSortOrder}" onclick="fullItemReloadAjax();" />
									<h:commandLink title="#{tip.list_descending}" styleClass="desSort" value="#{lbl.ItemList_SortOrderDescending}"
										id="sortOrderDesc"
										rendered="#{!YearbookModeratorListSessionBean.isAscending and YearbookModeratorListSessionBean.displaySortOrder}"
										action="#{YearbookModeratorListSessionBean.changeSortOrder}" onclick="fullItemReloadAjax();" />
									<h:commandButton id="btnChangeSortBy" styleClass="noDisplay changeSortBy" value=" "
										action="#{YearbookModeratorListSessionBean.changeSortBy}" />
								</h:panelGroup>
                                <h:panelGroup id="export" layout="block" styleClass="free_area0 sub action" rendered="#{YearbookModeratorListSessionBean.subMenu == 'EXPORT'}">
                                    <h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
                                        <h:panelGroup layout="block" styleClass="xLarge_area0">
                                            <h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
                                            <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
                                        </h:panelGroup>
                                        <h:selectOneMenu id="selEXPORTFORMAT" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.exportFormatName}">
                                            <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS_EXTENDED}" />
                                            <f:ajax render="form1:export" execute="form1:export" listener="#{ExportItems.updateExportFormats}"/>
                                        </h:selectOneMenu>
                                    </h:panelGroup>                           
                                    <h:panelGroup layout="block" styleClass="medium_area1 endline selectContainer" rendered="#{ExportItemsSessionBean.enableFileFormats}">
                                        <h:panelGroup layout="block" styleClass="medium_area0">
                                            <h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
                                            <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
                                        </h:panelGroup>
                                        <h:selectOneMenu id="selFILEFORMAT" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.fileFormat}" onchange="updateSelectionBox(this);">
                                            <f:selectItems value="#{ExportItems.CITATION_OPTIONS}" />
                                            <f:ajax render="form1:export" execute="form1:export" listener="#{ExportItems.updateExportFormats}"/>
                                        </h:selectOneMenu>
                                    </h:panelGroup>
                                    <h:commandLink title="#{tip.export_btDownload}" id="btnExportDownload" styleClass="free_area0"
										value="#{lbl.export_btDownload}" action="#{YearbookModeratorRetrieverRequestBean.exportSelectedDownload}" />
                                    <h:panelGroup layout="block" styleClass="free_area0 suggestAnchor endline CSL" rendered="#{ExportItemsSessionBean.enableCslAutosuggest }">
                                        <h:inputText id="inputCitationStyleName" styleClass="huge_txtInput citationStyleSuggest citationStyleName" value="#{ExportItemsSessionBean.citationStyleName}" title="#{ExportItemsSessionBean.citationStyleName}" pt:placeholder="Zitierstil eingeben" />
                                        <h:inputText id="inputCitationStyleIdentifier" styleClass="noDisplay citationStyleIdentifier" value="#{ExportItemsSessionBean.coneCitationStyleId}" />
                                        <h:outputLink class="fa fa-list-ul" value="#{AdvancedSearchBean.suggestConeUrl}citation-styles/all/format=html" title="Liste aller Zitierstile" target="_blank" />
                                        <h:commandButton id="btnRemoveCslAutoSuggest" value=" " styleClass="xSmall_area0 min_imgBtn closeIcon removeAutoSuggestCsl" style="display:none;" onclick="removeCslAutoSuggest($(this))" title="#{tip.ViewItem_lblRemoveAutosuggestCsl}">
                                            <f:ajax render="form1:iterCreatorOrganisationAuthors" execute="@form" />
                                        </h:commandButton>
                                    </h:panelGroup>
                                    <!-- content menu lower line ends here -->
                                </h:panelGroup>
								<!-- content menu ends here -->
							</h:panelGroup>
							<h:panelGroup layout="block" styleClass="subHeader">
								<h:outputText value="#{YearbookModeratorListSessionBean.totalNumberOfElements} #{lbl.SearchResultList_lblItems}" />
								<h:outputText value=" (" />
								<h:outputText
									value="#{lbl.ENUM_SORTORDER_ASCENDING} #{lbl.SearchResultList_lblSortedBy} #{YearbookModeratorListSessionBean.selectedSortByLabel}"
									rendered="#{YearbookModeratorListSessionBean.isAscending and YearbookModeratorListSessionBean.displaySortOrder}" />
								<h:outputText
									value="#{lbl.ENUM_SORTORDER_DESCENDING} #{lbl.SearchResultList_lblSortedBy} #{YearbookModeratorListSessionBean.selectedSortByLabel}"
									rendered="#{!YearbookModeratorListSessionBean.isAscending and YearbookModeratorListSessionBean.displaySortOrder}" />
								<h:outputText value=")" />
							</h:panelGroup>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn"
									fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false"
									showSummary="true" rendered="#{YearbookModeratorRetrieverRequestBean.numberOfMessages == 1}" />
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea"
									rendered="#{YearbookModeratorRetrieverRequestBean.hasErrorMessages and YearbookModeratorRetrieverRequestBean.numberOfMessages != 1}">
									<h2>
										<h:outputText value="#{lbl.warning_lblMessageHeader}" />
									</h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal"
										infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true"
										rendered="#{YearbookModeratorRetrieverRequestBean.hasMessages}" />
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea"
									rendered="#{YearbookModeratorRetrieverRequestBean.hasMessages and !YearbookModeratorRetrieverRequestBean.hasErrorMessages and YearbookModeratorRetrieverRequestBean.numberOfMessages != 1}">
									<h2>
										<h:outputText value="#{lbl.info_lblMessageHeader}" />
									</h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal"
										infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true"
										rendered="#{YearbookModeratorRetrieverRequestBean.hasMessages}" />
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<h:panelGroup>
						<h:panelGroup layout="block" styleClass="full_area0" rendered="#{YearbookModeratorListSessionBean.partListSize>0}">
							<ui:include src="yearbook/yearbookModeratorList.jspf" />
						</h:panelGroup>
						<h:panelGroup styleClass="full_area0" rendered="#{YearbookModeratorListSessionBean.partListSize==0}">
							<h:outputText styleClass="free_area0 small_marginLExcl" value="#{msg.depositorWS_valNoItemsMsg}" />
						</h:panelGroup>
					</h:panelGroup>
					<div id="ImgFullItem">
						<div id="ImgFullItemLoad" class="noDisplay" style="position: fixed;">&#160;</div>
					</div>
				</div>
				<!-- end: content section -->
			</h:form>
		</div>
		<ui:include src="footer/Footer.jspf" />
		<script type="text/javascript">
			<![CDATA[
			$("input[id$='offset']").submit(function() {
				$(this).val($(window).scrollTop());
			});
			$(document).ready(
					function() {
						$(window).scrollTop($("input[id$='offset']").val());
						$(window).scroll(
								function() {
									$("input[id$='offset']").val(
											$(window).scrollTop());
								});
						var element = document
								.getElementById('selSelectedOrgUnit');
						if (element && element.options != null
								&& element.options.length == 2) {
							throb();
							$.getJSON('AffiliationsAsJSON.jsp',
									loadAffiliations);
						}
					});
			]]>
		</script>
	</f:view>
</body>

</html>