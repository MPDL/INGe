<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ViewItemFull.pubItem.metadata.title} :: #{ApplicationBean.appTitle}" converter="HTMLTitleSubSupConverter" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
    <link rel="unapi-server" type="application/xml" title="unAPI" href="${ViewItemFull.unapiURLview}" />
    <ui:fragment rendered="#{ViewItemFull.pubItem == null or ViewItemFull.isStateWithdrawn}">
		<meta name="robots" content="noindex" />
	</ui:fragment>
    <h:outputText value="#{ViewItemFull.htmlMetaTags}" escape="false" rendered="#{ViewItemFull.pubItem != null and ViewItemFull.isStateReleased}" />
    <meta name="description" content="${ViewItemFull.pubItem.descriptionMetaTag}" />
    <h:outputStylesheet name="commonJavaScript/jquery/css/jquery-ui-1.10.4.min.css" />
	<h:outputScript name="commonJavaScript/jquery/jquery-ui-1.10.4.min.js" />
	<!-- ACHTUNG: Mit untenstehenden Bibliotheken funktioniert der nanoScroller nicht !!!
	              Daher kann auch eSciDoc_full_item.js nicht auf die neue Syntax umgestellt werden!!!
	<h:outputScript name="commonJavaScript/jquery/jquery-3.6.0.js" />
	<h:outputScript name="commonJavaScript/jquery/jquery-migrate-3.3.2.js" />
	  -->
    <script src="/cone/js/jquery.suggest.js"></script>
	<h:outputScript name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
    <style type="text/css">
        .dialogNoTitleBar .ui-dialog-titlebar {
            display: none;
        }

        .ui-dialog {
            background: #eee
        }
    </style>
    <script type="text/javascript">
        var currentDialog;
        var text = '${msg.ViewItem_doiDialog}';

        function showDialog() {
            currentDialog = $("<p>" + text + "</p>").dialog({
                dialogClass: "dialogNoTitleBar",
                modal: true,
                width: "auto",
                resizable: false,
                draggable: false,
                width: 500,
                buttons: [{
                        text: "#{lbl.cancel}",
                        click: function() {
                            $(this).dialog("close");
                        }
                    },
                    {
                        text: "#{lbl.ViewItemFull_lblDoi}",
                        click: function() {
                            $(".hiddenLnkExecuteAddDoi").click();
                            $(this).dialog("close");
                        }
                    }
                ],
                close: function(event, ui) {
                    $(this).dialog("destroy");
                }
            });
        }
    </script>
</h:head>

<body lang="${InternationalizationHelper.locale}">
	<f:event type="preRenderView" listener="#{ViewItemFullPage.init}" />
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <!-- The unAPI Identifier for this item -->
        <h:outputText value="&lt;abbr class='unapi-id' title='#{ViewItemFull.pubItem.objectIdAndVersion}'&gt;&lt;/abbr&gt;" escape="false" rendered="#{ViewItemFull.pubItem != null and ViewItemFull.isStateReleased}" />
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
                                    <h:outputText value="#{lbl.ViewItemPage}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <div class="small_marginLIncl subHeaderSection">
                            <!-- content menu starts here -->
                            <h:panelGroup layout="block" styleClass="contentMenu" rendered="#{ViewItemFull.pubItem != null}">
                                <!-- content menu upper line starts here -->
                                <div class="free_area0 sub">
                                    <h:outputLink id="lnkLinkForActionsView" styleClass="free_area0" value="#{ViewItemFull.linkForActionsView}" rendered="#{ViewItemSessionBean.subMenu != 'ACTIONS'}">
                                        <h:outputText value="#{lbl.ViewItemFull_lblItemActions}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="free_area0" value="#{lbl.ViewItemFull_lblItemActions}" rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}" />
                                    <h:outputText styleClass="seperator void" />
                                    <h:outputLink id="lnkLinkForExportView" styleClass="free_area0" value="#{ViewItemFull.linkForExportView}" rendered="#{ViewItemSessionBean.subMenu != 'EXPORT' and !ViewItemFull.isStateWithdrawn}">
                                        <h:outputText value="#{lbl.List_lblExportOptions}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblExportOptions}" rendered="#{ViewItemSessionBean.subMenu == 'EXPORT' and !ViewItemFull.isStateWithdrawn}" />
                                </div>
                                <!-- content menu upper line ends here -->
                                <!-- content menu lower line (actions) starts here -->
                                <h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}">
                                    <h:commandLink id="lnkEdit" action="#{ViewItemFull.editItem}" value="#{lbl.actionMenu_lnkEdit}" rendered="#{ViewItemFull.canEdit and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canEdit and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkSubmit" action="#{ViewItemFull.submitItem}" value="#{lbl.actionMenu_lnkSubmit}" rendered="#{ViewItemFull.canSubmit and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canSubmit and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkRelease" action="#{ViewItemFull.releaseItem}" value="#{lbl.actionMenu_lnkRelease}" rendered="#{ViewItemFull.canRelease and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canRelease and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkRevise" action="#{ViewItemFull.reviseItem}" value="#{lbl.actionMenu_lnkRevise}" rendered="#{ViewItemFull.canRevise and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canRevise and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkDelete" onclick="if(!confirm('#{msg.deleteMessage}'))return false;" value="#{lbl.actionMenu_lnkDelete}" action="#{ViewItemFull.deleteItem}" rendered="#{ViewItemFull.canDelete and ViewItemFull.isLatestVersion}" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canDelete and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkWithdraw" action="#{ViewItemFull.withdrawItem}" value="#{lbl.actionMenu_lnkWithdraw}" rendered="#{ViewItemFull.canWithdraw and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canWithdraw and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkModify" action="#{ViewItemFull.modifyItem}" value="#{lbl.actionMenu_lnkModify}" rendered="#{ViewItemFull.canModify and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canModify and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkSendOAMail" action="#{GFZSendOAMailPage.sendOAMailPage}" value="#{lbl.actionMenu_sendOAMail}" rendered="#{ViewItemFull.isModerator and ViewItemFull.isStateReleased and ViewItemFull.canSendOAMail}" onclick="fullItemReloadAjax();"/>
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.isModerator and ViewItemFull.isStateReleased and ViewItemFull.canSendOAMail}" />
                                    <h:commandLink id="lnkCreateItemFromTemplate" action="#{ItemControllerSessionBean.createItemFromTemplate}" value="#{lbl.ViewItemFull_lblCreateItemFromTemplate}" rendered="#{ViewItemFull.canCreateFromTemplate}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.canCreateFromTemplate}" />
                                    <h:commandLink id="lnkAddToBasket" action="#{ViewItemFull.addToBasket}" value="#{lbl.ViewItemFull_lblAddToBasket}" rendered="#{ViewItemFull.canAddToBasket}" onclick="fullItemReloadAjax();" />
                                    <h:commandLink id="lnkDeleteFromBasket" action="#{ViewItemFull.removeFromBasket}" value="#{lbl.ViewItemFull_lblRemoveFromBasket}" rendered="#{ViewItemFull.canDeleteFromBasket}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{(ViewItemFull.canAddToBatch and ((LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0) or LoginHelper.isAdmin)) or ViewItemFull.canDeleteFromBatch}" />
                                    <h:commandLink id="lnkAddToBatch" action="#{ViewItemFull.addToBatch}" value="#{lbl.ViewItemFull_lblAddToBatch}" rendered="#{ViewItemFull.canAddToBatch and ((LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0) or LoginHelper.isAdmin)}" onclick="fullItemReloadAjax();" />
                                    <h:commandLink id="lnkDeleteFromBatch" action="#{ViewItemFull.removeFromBatch}" value="#{lbl.ViewItemFull_lblRemoveFromBatch}" rendered="#{ViewItemFull.canDeleteFromBatch}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.ssrnContext and !ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" />
                                    <h:commandLink id="lnkAddSsrn" styleClass="free_area0" title="#{tip.ViewItemFull_lblAddSsrn }" action="#{ViewItemFull.addSsrnTag}" rendered="#{ViewItemFull.ssrnContext and !ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" onclick="fullItemReloadAjax();">
                                        <h:panelGroup styleClass="min_imgBtn add" />
                                        <h:outputText value="#{lbl.ViewItemFull_lblSSRN}" />
                                    </h:commandLink>
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.ssrnContext and ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" />
                                    <h:commandLink id="lnkRemoveSsrn" styleClass="free_area0" title="#{tip.ViewItemFull_lblRemoveSsrn }" action="#{ViewItemFull.removeSsrnTag}" rendered="#{ViewItemFull.ssrnContext and ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" onclick="fullItemReloadAjax();">
                                        <h:panelGroup styleClass="min_imgBtn remove" />
                                        <h:outputText value="#{lbl.ViewItemFull_lblSSRN}" />
                                    </h:commandLink>
                                    <h:panelGroup styleClass="seperator" rendered="#{ViewItemFull.doiCappable and ViewItemFull.canEdit}" />
                                    <h:outputLink id="lnkAddDoi" styleClass="free_area0" value="#" title="#{tip.ViewItemFull_lblAddDoi}" rendered="#{ViewItemFull.doiCappable and (ViewItemFull.canEdit or ViewItemFull.canModify)}" onclick="showDialog();">
                                        <h:outputText value="#{lbl.ViewItemFull_lblDoi}" />
                                    </h:outputLink>
                                    <!-- hidden Button for executing the addDoi command, after the jquery dialog has been confirmed -->
                                    <h:commandLink id="lnkExecuteAddDoi" styleClass="hiddenLnkExecuteAddDoi" style="display:none;" value="#" action="#{ViewItemFull.addDoi}" onclick="fullItemReloadAjax();" />
                                </h:panelGroup>
                                <!-- content menu lower line (actions) ends here -->
                                <!-- content menu lower line (export) starts here -->
                                                                <h:panelGroup id="export" layout="block" styleClass="free_area0 sub action" rendered="#{ViewItemSessionBean.subMenu == 'EXPORT'}">
                                    <h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
                                        <h:panelGroup layout="block" styleClass="xLarge_area0">
                                            <h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
                                            <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
                                        </h:panelGroup>
                                        <h:selectOneMenu id="selEXPORTFORMAT" styleClass="replace" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.exportFormatName}">
                                            <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}" />
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
                                    <h:commandLink id="btnExportDownload" styleClass="free_area0" value="#{lbl.export_btDownload}" action="#{ViewItemFull.exportDownload}" />
                                    <h:outputText styleClass="seperator" />
                                    <h:commandLink id="btnExportEMail" styleClass="free_area0" value="#{lbl.export_btEMail}" action="#{ViewItemFull.exportEmail}" />
                                    <h:panelGroup layout="block" styleClass="free_area0 suggestAnchor endline CSL" rendered="#{ExportItemsSessionBean.enableCslAutosuggest }">
                                        <h:inputText id="inputCitationStyleName" styleClass="huge_txtInput citationStyleSuggest citationStyleName" value="#{ExportItemsSessionBean.citationStyleName}" title="#{ExportItemsSessionBean.citationStyleName}" pt:placeholder="Zitierstil eingeben" />
                                        <h:inputText id="inputCitationStyleIdentifier" styleClass="noDisplay citationStyleIdentifier" value="#{ExportItemsSessionBean.coneCitationStyleId}" />
		                                <h:outputLink styleClass="fa fa-list-ul" value="#{ConeSessionBean.suggestConeUrl}citation-styles/all/format=html" title="#{lbl.searchAndExport_ListCitationStyle}" target="_blank" rel="noreferrer noopener" />
                                        <h:commandButton id="btnRemoveCslAutoSuggest" value=" " styleClass="xSmall_area0 min_imgBtn closeIcon removeAutoSuggestCsl" style="display:none;" onclick="removeCslAutoSuggest($(this))" title="#{tip.ViewItem_lblRemoveAutosuggestCsl}">
                                            <f:ajax render="form1:iterCreatorOrganisationAuthors" execute="@form" />
                                        </h:commandButton>
                                    </h:panelGroup>
                                    <!-- content menu lower line ends here -->
                                </h:panelGroup>
                             </h:panelGroup>
                            <!-- content menu ends here -->
                            <!-- Subheadline starts here -->
                            <h:panelGroup layout="block" styleClass="subHeader" rendered="#{ViewItemFull.isLoggedIn }">
                                <h:outputText value="#{lbl.EditItem_lblItemVersionID} '#{ViewItemFull.pubItem.objectIdAndVersion}'." rendered="#{ViewItemFull.pubItem.objectIdAndVersion != null}" />
                                <br />
                                <h:outputText value="#{lbl.EditItem_lblCollectionOfItem} '#{ViewItemFull.contextName}', #{lbl.ViewItemFull_lblIsAffiliatedTo}: '#{ViewItemFull.affiliations}'." />
                                <br />
                                <h:outputText value="#{lbl.EditItem_lblItemDepositor} '#{ViewItemFull.owner.name}'" rendered="#{ViewItemFull.owner != null }" />
                                <h:outputText value="." rendered="#{ViewItemFull.owner != null and ViewItemFull.creationDate == null}" />
                                <h:outputText value=" --- #{ViewItemFull.creationDate}" rendered="#{ViewItemFull.creationDate != null and ViewItemFull.owner != null }" />
                                <h:outputText value="#{lbl.EditItem_lblItemlatestChange } #{ViewItemFull.creationDate}" rendered="#{ViewItemFull.creationDate != null and ViewItemFull.owner == null }" />
                                <br />
                                <h:outputText value="#{lbl.EditItem_lblItemLatestModifier} '#{ViewItemFull.latestModifier.name}'" rendered="#{ViewItemFull.latestModifier != null}" />
                                <h:outputText value="." rendered="#{ViewItemFull.latestModifier != null and ViewItemFull.modificationDate == null}" />
                                <h:outputText value=" --- #{ViewItemFull.modificationDate}" rendered="#{ViewItemFull.modificationDate != null and ViewItemFull.latestModifier != null }" />
                                <h:outputText value="#{lbl.EditItem_lblItemLatestModification} #{ViewItemFull.modificationDate}" rendered="#{ViewItemFull.modificationDate != null and ViewItemFull.latestModifier == null }" />
                                <br />
                                <h:outputText value="#{msg.ViewItemFull_latestMessage} #{ViewItemFull.pubItem.message}" rendered="#{ViewItemFull.canShowLastMessage}" />
                                <h:outputText value="#{msg.ViewItemFull_latestMessage} #{lbl.lbl_noEntry}" rendered="#{!ViewItemFull.canShowLastMessage}" />
                            </h:panelGroup>
                            <!-- Subheadline ends here -->
                            <!-- JSF messages -->
                            <div class="subHeader">
                                <h:messages id="msg_wrapper" styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="false" showDetail="false" showSummary="true" rendered="#{ViewItemFull.numberOfMessages == 1}" escape="false"/>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages id="msg_error" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="false" showDetail="false" showSummary="true" rendered="#{ViewItemFull.hasMessages}" escape="false"/>
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{ViewItemFull.hasMessages and !ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages id="msg_info" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="false" showDetail="false" showSummary="true" rendered="#{ViewItemFull.hasMessages}" escape="false"/>
                                </h:panelGroup>
                            </div>
                            <!-- Subheadline ends here -->
                        </div>
                    </div>
                    <h:panelGroup layout="block" styleClass="full_area0" rendered="#{ViewItemFull.pubItem != null}">
                        <div class="full_area0 fullItem">
                            <!-- Item control information starts here -->
                            <div class="full_area0 fullItemControls">
                                <span class="full_area0_p5"> <b
									class="free_area0 small_marginLExcl">&#160; <h:outputText
											styleClass="messageError"
											value="#{msg.ViewItemFull_withdrawn}"
											rendered="#{ViewItemFull.isStateWithdrawn}" /></b> <h:panelGroup
										styleClass="seperator"
										rendered="#{ViewItemFull.canViewLocalTags}" /> <h:outputLink
										id="lnkViewLocalTagsPage" styleClass="free_area0"
										value="#{ApplicationBean.appContext}ViewLocalTagsPage.jsp"
										rendered="#{ViewItemFull.canViewLocalTags}">
										<h:outputText
											value="#{lbl.ViewItemFull_lblSubHeaderLocalTags}" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{ViewItemFull.canShowItemLog}" /> <h:commandLink
										id="lnkViewItemLogPage" styleClass="free_area0"
										action="#{ViewItemFull.showItemLog}"
										rendered="#{ViewItemFull.canShowItemLog}">
										<h:outputText value="#{lbl.ViewItemLogPage}" />
									</h:commandLink> <h:panelGroup styleClass="seperator"
										rendered="#{ViewItemFull.canShowReleaseHistory}" /> <h:commandLink
										id="lnkViewItemFull_btnItemVersions" styleClass="free_area0"
										action="#{ViewItemFull.showReleaseHistory}"
										rendered="#{ViewItemFull.canShowReleaseHistory}">
										<h:outputText value="#{lbl.ViewItemFull_btnItemVersions}" />
									</h:commandLink> <h:panelGroup styleClass="seperator" /> <h:outputLink
										id="lnkViewItemPage" styleClass="free_area0"
										value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}ViewItemFullPage.jsp?itemId=#{ViewItemFull.pubItem.objectIdAndVersion}">
										<h:outputText value="#{lbl.ViewItemFull_btnItemView}" />
									</h:outputLink> <h:panelGroup styleClass="seperator" /> <h:outputLink
										id="lnkViewItemOverviewPage" styleClass="free_area0 actual"
										value="#contentSkipLinkAnchor">
										<h:outputText
											value="#{lbl.ViewItemOverview_lblLinkOverviewPage}" />
									</h:outputLink> <h:panelGroup styleClass="seperator" />
								</span>
                            </div>
                            <!-- Item control information ends here -->
                            <!-- Paginator starts here -->
                            <h:panelGroup styleClass="full_area0 pageBrowserItem">
                                <h:panelGroup styleClass="paginatorPanel">
                                    <h:commandLink id="btList_lkFirstListItem" styleClass="min_imgBtn skipToFirst" action="#{PubItemListSessionBean.firstListItem}" rendered="#{PubItemListSessionBean.hasPreviousListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">&#160;</h:commandLink>
                                    <h:commandLink id="btList_lkPreviousListItem" styleClass="backward" action="#{PubItemListSessionBean.previousListItem}" rendered="#{PubItemListSessionBean.hasPreviousListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
                                        <h:outputText value="#{lbl.List_lkPrevious}" />
                                    </h:commandLink>
                                    <h:commandLink id="btList_lkNextListItem" styleClass="forward" action="#{PubItemListSessionBean.nextListItem}" rendered="#{PubItemListSessionBean.hasNextListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
                                        <h:outputText value="#{tip.List_lkNext}" />
                                    </h:commandLink>
                                    <h:commandLink id="btList_lkLastListItem" styleClass="min_imgBtn skipToLast" action="#{PubItemListSessionBean.lastListItem}" rendered="#{PubItemListSessionBean.hasNextListItem and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">&#160;</h:commandLink>
                                </h:panelGroup>
                                <h:panelGroup styleClass="gotoBox" rendered="#{(PubItemListSessionBean.hasNextListItem or PubItemListSessionBean.hasPreviousListItem) and BreadcrumbItemHistorySessionBean.previousPageIsListPage}">
                                    <h:inputText id="inputItemListPosition" styleClass="pag_txtInput" value="#{PubItemListSessionBean.listItemPosition}" validatorMessage="#{FacesBean.getMessage('listError_goTo')}" requiredMessage="#{FacesBean.getMessage('listError_goTo')}" converterMessage="#{FacesBean.getMessage('listError_goTo')}" label="GoToBox" />
                                    <h:outputLabel id="lblItemListPosition" styleClass="free_label" value="#{lbl.ItemList_of} " />
                                    <h:outputLabel id="lblChangeItemListPosition" styleClass="free_label" value="#{PubItemListSessionBean.totalNumberOfElements}" />
                                    <h:commandButton id="btnGoToItemListPosition" styleClass="xTiny_txtBtn pageChangeHiddenBtn" value="#{lbl.List_btGo}" title="#{lbl.List_btGo}" action="#{PubItemListSessionBean.doListItemPosition}" />
                                </h:panelGroup>
                            </h:panelGroup>
                            <!-- Paginator ends here -->
                            <!-- ItemView starts here -->
                            <h:panelGroup layout="block" styleClass="full_area0">
                                <ui:include src="viewItemOverview/titleGroup.jspf" />
                                <h:panelGroup layout="block" styleClass="huge_area0 xTiny_marginRExcl small_marginLExcl" style="overflow:visible;">
                                    <ui:include src="viewItemOverview/authorGroup.jspf" />
                                    <ui:include src="viewItemOverview/externalResourceGroup.jspf" />
                                    <ui:include src="viewItemOverview/restrictedFilesGroup.jspf" />
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="third_area0">
                                    <ui:include src="viewItemOverview/fulltextGroup.jspf" />
                                    <ui:include src="viewItemOverview/supplementaryMaterialGroup.jspf" />
                                    <ui:include src="viewItemOverview/citationGroup.jspf" />
                                    <ui:include src="viewItemOverview/abstractGroup.jspf" />
                                </h:panelGroup>
                            </h:panelGroup>
                            <!-- ItemView ends here -->
                        </div>
                    </h:panelGroup>
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>

        <ui:include src="footer/Footer.jspf" />

		<script type="text/javascript">
	        var suggestConeUrl = "#{ConeSessionBean.suggestConeUrl}";

		    var citationStyleSuggestBaseURL = '$1?format=json';
    		var citationStyleSuggestURL = suggestConeUrl + 'citation-styles/query';

			$(document).ready(function() {
				startNanoScrollerWhenLoaded();

				// Try to replace standard author images with CoNE-images
				replaceAuthorImage();
				checkUpdateCslUi();
			});

			// NanoScroller
			var counter = 0;
			var startNanoScrollerTimeout;

			// Add NanoScroller (Scrollbar only visible when hovering the marked div)
			function startNanoScrollerWhenLoaded() {
				clearTimeout(startNanoScrollerTimeout);
				switch (typeof $.fn.nanoScroller) {
					case 'function':
						var nanoDiv = $(".nano");
						nanoDiv.nanoScroller();
						break;
					default:
						counter++;
						if (counter &lt; 10) {
							startNanoScrollerTimeout = setTimeout(startNanoScrollerWhenLoaded, 100);
						}
						break;
				}
			}

			// tries to replace the standard author image with the cone image.
			function replaceAuthorImage() {
				var url;
				var jsonRequestUrl;
				var imgElement;
				$('.mpgAuthorId').each(function(index) {
					url = $(this).text();
					jsonRequestUrl = '#{ConeSessionBean.coneServiceUrl}' + url + '?format=json';
					imgElement = $(this).parent().find('img').get(0);
					updateImage(imgElement, jsonRequestUrl);
				});
			}

			// JSon request to CoNE (works only if CoNE is on the same server as PubMan [Cross-site-scripting])
			// !DOES NOT WORK LOCALLY! (Cross-site-scripting)
			function updateImage(imgElement, jsonRequestUrl) {
				$.getJSON(jsonRequestUrl, function(result) {
					var pictureUrl = result.http_xmlns_com_foaf_0_1_depiction;
					if (pictureUrl != undefined &amp;&amp; pictureUrl.trim() != '') {
						$(imgElement).attr('src', pictureUrl);
					}
				});
			}

			function checkUpdateCslUi() {
				(typeof updateCslUi == 'function') ? updateCslUi(): setTimeout("checkUpdateCslUi()", 30);
			}
		</script>
    </f:view>
</body>

</html>
