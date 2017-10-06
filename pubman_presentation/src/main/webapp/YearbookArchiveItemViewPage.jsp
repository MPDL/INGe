<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <div class="full wrapper">
            <h:inputHidden id="offset"></h:inputHidden>
            <ui:include src="header/Header.jspf" />
            <h:form>
                <div id="content" class="full_area0 clear">
                    <!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
                    <div class="clear">
                        <div class="headerSection">
                            <ui:include src="header/Breadcrumb.jspf" />
                            <div id="contentSkipLinkAnchor" class="clear headLine">
                                <!-- Headline starts here -->
                                <h1>
                                    <h:outputText value="#{lbl.YearbookArchivePage} #{YearbookItemSessionBean.yearbookForView.year}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <div class="small_marginLIncl subHeaderSection">
                            <h:panelGroup layout="block" styleClass="contentMenu">
                                <!-- content menu starts here -->
                                <h:panelGroup layout="block" styleClass="free_area0 sub">
                                    <h:outputLink id="lnkMenuQAWorkspace" title="#{tip.chooseWorkspace_QAWorkspace}" value="#{ApplicationBean.appContext}QAWSPage.jsp" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}">
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuQAWorkspace}" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="seperator void" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
                                    <h:outputLink id="lnkSubmission_lnkImportWorkspaceMenu" title="#{tip.chooseWorkspace_ImportWorkspace}" value="#{ApplicationBean.appContext}ImportWorkspace.jsp" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuImportWorkspace}" />
                                    </h:outputLink>
                                    <h:panelGroup id="txtMenuYearbookWorkspace" rendered="#{LoginHelper.isYearbookEditor}">
                                        <h:outputText styleClass="seperator void" />
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuYearbookWorkspace}" />
                                    </h:panelGroup>
                                    <h:outputText styleClass="seperator void" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}" />
                                    <h:outputLink id="lnkMenuReportWorkspace" title="#{tip.chooseWorkspace_ReportWorkspace}" value="#{ApplicationBean.appContext}ReportWorkspacePage.jsp" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}">
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuReportWorkspace}" />
                                    </h:outputLink>
                                </h:panelGroup>
                                <div class="free_area0 sub action">
                                    <!-- content menu upper line starts here -->
                                    <h:commandLink id="lnkChangeSubmenuToView" title="#{tip.List_lblViewOptions}" styleClass="free_area0" value="#{lbl.List_lblViewOptions}" action="#{PubItemListSessionBean.changeSubmenuToView}" rendered="#{PubItemListSessionBean.subMenu != 'VIEW'}" onclick="fullItemReloadAjax();" />
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblViewOptions}" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}" />
                                    <h:outputText styleClass="seperator void" />
                                    <h:commandLink id="lnkChangeSubmenuToFilter" title="#{tip.List_lblFilterOptions}" styleClass="free_area0" value="#{lbl.List_lblFilterOptions}" action="#{PubItemListSessionBean.changeSubmenuToFilter}" rendered="#{PubItemListSessionBean.subMenu != 'FILTER'}" onclick="fullItemReloadAjax();" />
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblFilterOptions}" rendered="#{PubItemListSessionBean.subMenu == 'FILTER'}" />
                                    <h:outputText styleClass="seperator void" />
                                    <h:commandLink id="lnkChangeSubmenuToSorting" title="#{tip.List_lblSortOptions}" styleClass="free_area0" value="#{lbl.List_lblSortOptions}" action="#{PubItemListSessionBean.changeSubmenuToSorting}" rendered="#{PubItemListSessionBean.subMenu != 'SORTING'}" onclick="fullItemReloadAjax();" />
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblSortOptions}" rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}" />
                                    <h:outputText styleClass="seperator void" />
                                    <h:commandLink id="lnkChangeSubmenuToExport" title="#{tip.List_lblExportOptions}" styleClass="free_area0" value="#{lbl.List_lblExportOptions}" action="#{PubItemListSessionBean.changeSubmenuToExport}" rendered="#{PubItemListSessionBean.subMenu != 'EXPORT'}" onclick="fullItemReloadAjax();" />
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblExportOptions}" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}" /> &#160;
                                    <!-- content menu upper line ends here -->
                                </div>
                                <h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}">
                                    <!-- content menu lower line starts here -->
                                    <h:commandLink id="lnkChangeListTypeToBib" title="#{tip.List_lblBibList}" styleClass="free_area0" rendered="#{PubItemListSessionBean.listType == 'GRID'}" action="#{PubItemListSessionBean.changeListTypeToBib}" onclick="fullItemReloadAjax();">
                                        <h:outputText value="#{lbl.List_lblBibList}" />
                                    </h:commandLink>
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblBibList}" rendered="#{PubItemListSessionBean.listType == 'BIB'}" />
                                    <h:outputText styleClass="seperator" />
                                    <h:commandLink id="lnkChangeListTypeToGrid" title="#{tip.List_lblGridList}" styleClass="free_area0" rendered="#{PubItemListSessionBean.listType == 'BIB'}" action="#{PubItemListSessionBean.changeListTypeToGrid}" onclick="fullItemReloadAjax();">
                                        <h:outputText value="#{lbl.List_lblGridList}" />
                                    </h:commandLink>
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblGridList}" rendered="#{PubItemListSessionBean.listType == 'GRID'}" />
                                    <!-- content menu lower line ends here -->
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'FILTER'}">
                                    <!-- content menu lower line starts here -->
                                    <h:outputText styleClass="free_area0 clearLeft" value="#{lbl.qaws_lblOrgUnitSelection} " />
                                    <h:panelGroup layout="block" styleClass="xDouble_area1 endline selectContainer">
                                        <h:panelGroup layout="block" styleClass="xDouble_area0">
                                            <h:panelGroup styleClass="xDouble_area0 selectionBox">&#160;</h:panelGroup>
                                            <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
                                        </h:panelGroup>
                                        <h:selectOneMenu id="selSelectedOrgUnit" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{YearbookArchiveRetrieverRequestBean.selectedOrgUnit}" onchange="$(this).parents('div').find('.changeOrgUnit').click();">
                                            <f:selectItems value="#{YearbookArchiveRetrieverRequestBean.orgUnitSelectItems}" />
                                        </h:selectOneMenu>
                                    </h:panelGroup>
                                    <h:commandButton id="btChangeOrgUnit" styleClass="noDisplay changeOrgUnit" action="#{YearbookArchiveRetrieverRequestBean.changeOrgUnit}" value="change org unit" />
                                    <!-- content menu lower line ends here -->
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}">
                                    <!-- content menu lower line starts here -->
                                    <h:outputText styleClass="free_area0" value="#{lbl.ItemList_SortBy}: " />
                                    <h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
                                        <h:panelGroup layout="block" styleClass="xLarge_area0">
                                            <h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
                                            <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
                                        </h:panelGroup>
                                        <h:selectOneMenu id="sortBy" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{PubItemListSessionBean.selectedSortBy}" onchange="$(this).parents('div').find('.changeSortBy').click();">
                                            <f:selectItems value="#{PubItemListSessionBean.sortBySelectItems}" />
                                        </h:selectOneMenu>
                                    </h:panelGroup>
                                    <h:commandLink title="#{tip.list_ascending}" styleClass="ascSort" value="#{lbl.ItemList_SortOrderAscending}" id="sortOrderAsc" rendered="#{PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}" action="#{PubItemListSessionBean.changeSortOrder}" onclick="fullItemReloadAjax();" />
                                    <h:commandLink title="#{tip.list_descending}" styleClass="desSort" value="#{lbl.ItemList_SortOrderDescending}" id="sortOrderDesc" rendered="#{!PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}" action="#{PubItemListSessionBean.changeSortOrder}" onclick="fullItemReloadAjax();" />
                                    <h:commandButton id="btnChangeSortBy" title="#{tip.list_btSortBy}" styleClass="noDisplay changeSortBy" value=" " action="#{PubItemListSessionBean.changeSortBy}" />
                                    <!-- content menu lower line ends here -->
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}">
                                    <!-- content menu lower line starts here -->
                                    <h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
                                        <h:panelGroup layout="block" styleClass="xLarge_area0">
                                            <h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
                                            <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
                                        </h:panelGroup>
                                        <h:selectOneMenu id="selExportFormatName" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.exportFormatName}" onchange="$(this).parents('.sub').find('.exportUpdateButton').click();">
                                            <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS_EXTENDED}" />
                                        </h:selectOneMenu>
                                    </h:panelGroup>
                                    <h:commandButton id="btnUpdateExportFormats" title="#{tip.export_btFormat}" styleClass="noDisplay exportUpdateButton" action="#{ExportItems.updateExportFormats}" value="updateExportFormats" />
                                    <h:panelGroup layout="block" styleClass="medium_area1 endline selectContainer" rendered="#{ExportItemsSessionBean.enableFileFormats}">
                                        <h:panelGroup layout="block" styleClass="medium_area0">
                                            <h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
                                            <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
                                        </h:panelGroup>
                                        <h:selectOneMenu id="selFileFormat" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.fileFormat}" onchange="updateSelectionBox(this);">
                                            <f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}" />
                                        </h:selectOneMenu>
                                    </h:panelGroup>
                                    <h:commandLink title="#{tip.export_btDownload}" id="btnExportDownload" styleClass="free_area0" value="#{lbl.export_lblExport}" action="#{YearbookArchiveRetrieverRequestBean.exportSelectedDownload}" />
                                    <!-- content menu lower line ends here -->
                                </h:panelGroup>
                                <!-- content menu ends here -->
                            </h:panelGroup>
                            <div class="subHeader">
                                <!-- Subheadline starts here -->
                                <h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookArchiveBean.numberOfMessages == 1}" />
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{YearbookArchiveBean.hasErrorMessages and YearbookArchiveBean.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.hasMessages}" />
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{YearbookArchiveBean.hasMessages and !YearbookArchiveBean.hasErrorMessages and YearbookArchiveBean.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookArchiveBean.hasMessages}" />
                                </h:panelGroup>
                                <!-- Subheadline ends here -->
                            </div>
                        </div>
                    </div>
                    <div class="full_area0">
                        <h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.listType == 'BIB' and PubItemListSessionBean.partListSize>0}">
                            <ui:include src="list/itemList.jspf" />
                        </h:panelGroup>
                        <h:panelGroup layout="block" styleClass="full_area0" rendered="#{PubItemListSessionBean.listType == 'GRID' and PubItemListSessionBean.partListSize>0}">
                            <ui:include src="list/gridList.jspf" />
                        </h:panelGroup>
                        <h:panelGroup styleClass="full_area0" rendered="#{PubItemListSessionBean.partListSize==0}">
                            <h:outputText styleClass="free_area0 small_marginLExcl" value="#{msg.depositorWS_valNoItemsMsg}" />
                        </h:panelGroup>
                    </div>
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>
        <ui:include src="footer/Footer.jspf" />
    </f:view>
</body>

</html>