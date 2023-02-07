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
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{ViewItemFullPage.init}" />
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <f:loadBundle var="genre" basename="Genre_#{ViewItemFull.pubItem!=null ? ViewItemFull.pubItem.metadata.genre : 'ARTICLE' }" />
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
                                <h1><h:outputText value="#{lbl.ViewItemPage}" /></h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <div class="small_marginLIncl subHeaderSection">
                            <h:panelGroup layout="block" styleClass="contentMenu" rendered="#{ViewItemFull.pubItem != null}">
                                <!-- content menu starts here -->
                                <div class="free_area0 sub">
                                    <!-- content menu upper line starts here -->
                                    <h:outputLink id="lnkLinkForActionsView" styleClass="free_area0" value="#{ViewItemFull.linkForActionsView}" rendered="#{ViewItemSessionBean.subMenu != 'ACTIONS'}">
                                        <h:outputText value="#{lbl.ViewItemFull_lblItemActions}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="free_area0" value="#{lbl.ViewItemFull_lblItemActions}" rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}" />
                                    <h:outputText styleClass="seperator void" />
                                    <h:outputLink id="lnkLinkForExportView" styleClass="free_area0" value="#{ViewItemFull.linkForExportView}" rendered="#{ViewItemSessionBean.subMenu != 'EXPORT' and !ViewItemFull.isStateWithdrawn}">
                                        <h:outputText value="#{lbl.List_lblExportOptions}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="free_area0" value="#{lbl.List_lblExportOptions}" rendered="#{ViewItemSessionBean.subMenu == 'EXPORT' and !ViewItemFull.isStateWithdrawn}" />
                                    <!-- content menu upper line ends here -->
                                </div>
                                <h:panelGroup layout="block" styleClass="free_area0 sub action" rendered="#{ViewItemSessionBean.subMenu == 'ACTIONS'}">
                                    <!-- content menu lower line starts here -->
                                    <h:commandLink id="lnkEdit" action="#{ViewItemFull.editItem}" value="#{lbl.actionMenu_lnkEdit}" rendered="#{ViewItemFull.canEdit and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup id="lnkEditSeperator" styleClass="seperator" rendered="#{ViewItemFull.canEdit and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkSubmit" action="#{ViewItemFull.submitItem}" value="#{lbl.actionMenu_lnkSubmit}" rendered="#{ViewItemFull.canSubmit and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup id="lnkSubmitSeperator" styleClass="seperator" rendered="#{ViewItemFull.canSubmit and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkRelease" action="#{ViewItemFull.releaseItem}" value="#{lbl.actionMenu_lnkRelease}" rendered="#{ViewItemFull.canRelease and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup id="lnkReleaseSeperator" styleClass="seperator" rendered="#{ViewItemFull.canRelease and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkRevise" action="#{ViewItemFull.reviseItem}" value="#{lbl.actionMenu_lnkRevise}" rendered="#{ViewItemFull.canRevise and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup id="lnkReviseSeperator" styleClass="seperator" rendered="#{ViewItemFull.canRevise and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkDelete" onclick="if(!confirm('#{msg.deleteMessage}'))return false;" value="#{lbl.actionMenu_lnkDelete}" action="#{ViewItemFull.deleteItem}" rendered="#{ViewItemFull.canDelete and ViewItemFull.isLatestVersion}" />
                                    <h:panelGroup id="lnkDeleteSeperator" styleClass="seperator" rendered="#{ViewItemFull.canDelete and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkWithdraw" action="#{ViewItemFull.withdrawItem}" value="#{lbl.actionMenu_lnkWithdraw}" rendered="#{ViewItemFull.canWithdraw and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup id="lnkWithdrawSeperator" styleClass="seperator" rendered="#{ViewItemFull.canWithdraw and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkModify" action="#{ViewItemFull.modifyItem}" value="#{lbl.actionMenu_lnkModify}" rendered="#{ViewItemFull.canModify and ViewItemFull.isLatestVersion}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup id="lnkModifySeperator" styleClass="seperator" rendered="#{ViewItemFull.canModify and ViewItemFull.isLatestVersion}" />
                                    <h:commandLink id="lnkCreateItemFromTemplate" action="#{ItemControllerSessionBean.createItemFromTemplate}" value="#{lbl.ViewItemFull_lblCreateItemFromTemplate}" rendered="#{ViewItemFull.canCreateFromTemplate}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup id="lnkCreateItemFromTemplateSeperator" styleClass="seperator" rendered="#{ViewItemFull.canCreateFromTemplate}" />
                                    <h:commandLink id="lnkAddToBasket" action="#{ViewItemFull.addToBasket}" value="#{lbl.ViewItemFull_lblAddToBasket}" rendered="#{ViewItemFull.canAddToBasket}" onclick="fullItemReloadAjax();" />
                                    <h:commandLink id="lnkDeleteFromBasket" action="#{ViewItemFull.removeFromBasket}" value="#{lbl.ViewItemFull_lblRemoveFromBasket}" rendered="#{ViewItemFull.canDeleteFromBasket}" onclick="fullItemReloadAjax();" />
                                    <h:panelGroup id="lnkAddDeleteBasketSeperator" styleClass="seperator" rendered="#{(ViewItemFull.canAddToBatch and ((LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0) or LoginHelper.isAdmin)) or ViewItemFull.canDeleteFromBatch}" />
                                    <h:commandLink id="lnkAddToBatch" action="#{ViewItemFull.addToBatch}" value="#{lbl.ViewItemFull_lblAddToBatch}" rendered="#{ViewItemFull.canAddToBatch and ((LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0) or LoginHelper.isAdmin)}" onclick="fullItemReloadAjax();" />
                                    <h:commandLink id="lnkDeleteFromBatch" action="#{ViewItemFull.removeFromBatch}" value="#{lbl.ViewItemFull_lblRemoveFromBatch}" rendered="#{ViewItemFull.canDeleteFromBatch}" onclick="fullItemReloadAjax();" />
                                    <h:commandLink id="lnkAddSsrn" styleClass="free_area0" title="#{tip.ViewItemFull_lblAddSsrn }" action="#{ViewItemFull.addSsrnTag}" rendered="#{ViewItemFull.ssrnContext and !ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" onclick="fullItemReloadAjax();">
                                        <h:panelGroup styleClass="min_imgBtn add" />
                                        <h:outputText value="#{lbl.ViewItemFull_lblSSRN}" />
                                    </h:commandLink>
                                    <h:panelGroup id="lnkSsrnSeperator" styleClass="seperator" rendered="#{ViewItemFull.ssrnContext and ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" />
                                    <h:commandLink id="lnkRemoveSsrn" styleClass="free_area0" title="#{tip.ViewItemFull_lblRemoveSsrn }" action="#{ViewItemFull.removeSsrnTag}" rendered="#{ViewItemFull.ssrnContext and ViewItemFull.ssrnTagged and (ViewItemFull.canEdit or ViewItemFull.canModify)}" onclick="fullItemReloadAjax();">
                                        <h:panelGroup styleClass="min_imgBtn remove" />
                                        <h:outputText value="#{lbl.ViewItemFull_lblSSRN}" />
                                    </h:commandLink>
                                    <h:panelGroup rendered="#{ViewItemFull.doiCappable and ViewItemFull.canEdit}">
                                        <h:panelGroup id="lnkAddDoiSeperator" styleClass="seperator" />
                                        <h:outputLink id="lnkAddDoi" styleClass="free_area0" value="#" title="#{tip.ViewItemFull_lblAddDoi}" onclick="showDialog();">
                                            <h:outputText value="#{lbl.ViewItemFull_lblDoi}" />
                                        </h:outputLink>
                                        <script type="text/javascript">
                                            var currentDialog;
                                            var text = '${msg.ViewItem_doiDialog}';

                                            function showDialog() {
                                                currentDialog = $(
                                                        "<p>" + text + "</p>")
                                                    .dialog({
                                                        dialogClass: "dialogNoTitleBar",
                                                        modal: true,
                                                        width: "auto",
                                                        resizable: false,
                                                        draggable: false,
                                                        width: 500,
                                                        buttons: [{
                                                                text: "#{lbl.cancel}",
                                                                click: function() {
                                                                    $(
                                                                            this)
                                                                        .dialog(
                                                                            "close");
                                                                }
                                                            },
                                                            {
                                                                text: "#{lbl.ViewItemFull_lblDoi}",
                                                                click: function() {
                                                                    $(
                                                                            ".hiddenLnkExecuteAddDoi")
                                                                        .click();
                                                                    $(
                                                                            this)
                                                                        .dialog(
                                                                            "close");
                                                                }
                                                            }
                                                        ],
                                                        close: function(
                                                            event,
                                                            ui) {
                                                            $(this)
                                                                .dialog(
                                                                    "destroy");
                                                        }
                                                    });
                                            }
                                        </script>
                                        <!-- hidden Button for executing the addDoi command, after the jquery dialog has been confirmed -->
                                        <h:commandLink id="lnkExecuteAddDoi" styleClass="hiddenLnkExecuteAddDoi" style="display:none;" value="#" action="#{ViewItemFull.addDoi}" onclick="fullItemReloadAjax();" />
                                    </h:panelGroup>
                                    <!-- content menu lower line ends here -->
                                </h:panelGroup>
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
                                <!-- content menu ends here -->
                            </h:panelGroup>
                            <h:panelGroup layout="block" styleClass="subHeader" rendered="#{ViewItemFull.isLoggedIn }">
                                <!-- Subheadline starts here -->
                                <h:outputText value="#{lbl.EditItem_lblItemVersionID} '#{ViewItemFull.pubItem.objectIdAndVersion}'." rendered="#{ViewItemFull.pubItem.objectIdAndVersion != null}" />
                                <br />
                                <h:outputText value="#{lbl.EditItem_lblCollectionOfItem} '#{ViewItemFull.contextName}', #{lbl.ViewItemFull_lblIsAffiliatedTo}: '#{ViewItemFull.affiliations}'." />
                                <br />
                                <h:outputText value="#{lbl.EditItem_lblItemDepositor} '#{ViewItemFull.owner.name}'" rendered="#{ViewItemFull.owner != null }" />
                                <h:outputText value="." rendered="#{ViewItemFull.owner != null and ViewItemFull.creationDate == null}" />
                                <h:outputText value=" --- #{ViewItemFull.creationDate}" rendered="#{ViewItemFull.creationDate != null and ViewItemFull.owner != null }" />
                                <h:outputText value="#{lbl.EditItem_lblItemlatestChange } #{ViewItemFull.creationDate}" rendered="#{ViewItemFull.creationDate != null and ViewItemFull.owner == null }" />
                                <br />
                                <h:outputText value="#{lbl.EditItem_lblItemLatestModifier} '#{ViewItemFull.modifier.name}'" rendered="#{ViewItemFull.modifier != null}" />
                                <h:outputText value="." rendered="#{ViewItemFull.modifier != null and ViewItemFull.modificationDate == null}" />
                                <h:outputText value=" --- #{ViewItemFull.modificationDate}" rendered="#{ViewItemFull.modificationDate != null and ViewItemFull.modifier != null }" />
                                <h:outputText value="#{lbl.EditItem_lblItemLatestModification} #{ViewItemFull.modificationDate}" rendered="#{ViewItemFull.modificationDate != null and ViewItemFull.modifier == null }" />
                                <br />
                                <h:outputText value="#{msg.ViewItemFull_latestMessage} #{ViewItemFull.pubItem.message}" rendered="#{ViewItemFull.canShowLastMessage}" />
                                <h:outputText value="#{msg.ViewItemFull_latestMessage} #{lbl.lbl_noEntry}" rendered="#{!ViewItemFull.canShowLastMessage}" />
                            </h:panelGroup>
                            <div class="subHeader">
                                <!-- JSF messages -->
                                <h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="false" showDetail="false" showSummary="true" rendered="#{ViewItemFull.numberOfMessages == 1}" escape="false"/>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="false" showDetail="false" showSummary="true" rendered="#{ViewItemFull.hasMessages}" escape="false"/>
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{ViewItemFull.hasMessages and !ViewItemFull.hasErrorMessages and ViewItemFull.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="false" showDetail="false" showSummary="true" rendered="#{ViewItemFull.hasMessages}" escape="false"/>
                                </h:panelGroup>
                                <!-- Subheadline ends here -->
                            </div>
                        </div>
                    </div>
                    <h:panelGroup layout="block" styleClass="full_area0 clear" rendered="#{ViewItemFull.pubItem != null}">
                        <div class="full_area0 fullItem">
                            <div class="full_area0 fullItemControls">
                                <span class="full_area0_p5"> <b
									class="free_area0 small_marginLExcl">&#160;<h:outputText
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
										id="lnkViewItemPage" styleClass="free_area0 actual"
										value="#contentSkipLinkAnchor">
										<h:outputText value="#{lbl.ViewItemFull_btnItemView}" />
									</h:outputLink> <h:panelGroup styleClass="seperator" /> <h:outputLink
										id="lnkViewItemOverviewPage" styleClass="free_area0"
										value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}ViewItemOverviewPage.jsp?itemId=#{ViewItemFull.pubItem.objectIdAndVersion}">
										<h:outputText
											value="#{lbl.ViewItemOverview_lblLinkOverviewPage}" />
									</h:outputLink> <h:panelGroup styleClass="seperator" />
								</span>
                            </div>
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
                            <div class="full_area0 itemHeader">
                                <h:panelGroup styleClass="xLarge_area0 endline blockHeader">
                                    &#160;
                                </h:panelGroup>
                                <h:panelGroup styleClass="seperator" />
                                <h:panelGroup styleClass="free_area0_p8 endline itemHeadline">
                                    <b><h:outputText
											value="#{ViewItemFull.pubItem.metadata.title}"
											converter="HTMLSubSupConverter" escape="false" /></b>
                                    <h:outputText value="#{ViewItemFull.citationHtml}" escape="false" />
                                </h:panelGroup>
                                <h:panelGroup styleClass="free_area0 status statusArea">
                                    <h:panelGroup layout="block" styleClass="big_imgArea statusIcon withdrawnItem" rendered="#{ViewItemFull.isStateWithdrawn}" />
                                    <h:panelGroup layout="block" styleClass="big_imgArea statusIcon pendingItem" rendered="#{ViewItemFull.isStatePending}" />
                                    <h:panelGroup layout="block" styleClass="big_imgArea statusIcon submittedItem" rendered="#{ViewItemFull.isStateSubmitted}" />
                                    <h:panelGroup layout="block" styleClass="big_imgArea statusIcon releasedItem" rendered="#{ViewItemFull.isStateReleased}" />
                                    <h:panelGroup layout="block" styleClass="big_imgArea statusIcon inRevisionItem" rendered="#{ViewItemFull.isStateInRevision}" />
                                    <h:outputText styleClass="noDisplay" value="Item is " />
                                    <h:outputLabel styleClass="medium_label statusLabel free_area0_p3" rendered="#{ViewItemFull.isStateWithdrawn}">
                                        <h:outputText value="#{ViewItemFull.itemPublicState}" />
                                    </h:outputLabel>
                                    <h:outputLabel styleClass="medium_label statusLabel free_area0_p3" rendered="#{!ViewItemFull.isStateWithdrawn}">
                                        <h:outputText value="#{ViewItemFull.itemState}" />
                                    </h:outputLabel>
                                </h:panelGroup>
                            </div>
                            <h:panelGroup layout="block" styleClass="full_area0 itemBlock noTopBorder" rendered="#{ViewItemFull.isLoggedIn}">
                                <h3 class="xLarge_area0_p8 endline blockHeader">&#160;</h3>
                                <div class="free_area0 itemBlockContent endline">
                                    <b class="xLarge_area0_p8 endline labelLine clear"> <h:outputText
											value="#{lbl.ViewItem_lblModeratorContact}" /><span
										class="noDisplay">: </span>
									</b> <span class="xHuge_area0 xTiny_marginLExcl endline"> <h:outputLink
											id="lnkModeratorContactEmail"
											value="mailto:#{ViewItemFull.moderatorContactEmail}?subject=#{ViewItemFull.pubItem.objectIdAndVersion}"
											rendered="#{ViewItemFull.isLoggedIn}">
											<h:outputText value="#{lbl.ViewItem_lnkModeratorEmail}" />
										</h:outputLink>
									</span>
                                </div>
                            </h:panelGroup>
                            <h:panelGroup layout="block" styleClass="full_area0 itemBlock visibility" rendered="#{!ViewItemFull.isStateWithdrawn}">
                                <h3 class="xLarge_area0_p8 endline blockHeader">&#160;</h3>
                                <h:panelGroup styleClass="seperator" />
                                <a class="free_area0 expand">
                                    <h:outputText value="#{lbl.ViewItemFull_lblShowGroup} #{lbl.ViewItemFull_lblAll}" />
                                </a>
                                <a class="free_area0 collapse">
                                    <h:outputText value="#{lbl.ViewItemFull_lblHideGroup} #{lbl.ViewItemFull_lblAll}" />
                                </a>
                            </h:panelGroup>
                            <ui:include src="viewItem/BasicGroup.jspf" />
                            <ui:include src="viewItem/FilesGroup.jspf" />
                            <ui:include src="viewItem/LocatorsGroup.jspf" />
                            <ui:include src="viewItem/PersOrgGroup.jspf" />
                            <ui:include src="viewItem/ContentGroup.jspf" />
                            <ui:include src="viewItem/DetailGroup.jspf" />
                            <ui:include src="viewItem/EventGroup.jspf" />
                            <!--JUS content section -->
                            <ui:include src="viewItem/LegalCaseGroup.jspf" />
                            <ui:include src="viewItem/ProjectInfoGroup.jspf" />
                            <ui:include src="viewItem/SourceGroup.jspf" />
                            <ui:include src="viewItem/WithdrawnGroup.jspf" />
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
				checkUpdateCslUi();
			});

			function checkUpdateCslUi() {
				(typeof updateCslUi == 'function') ? updateCslUi(): setTimeout("checkUpdateCslUi()", 30);
			}
		</script>
    </f:view>
</body>

</html>