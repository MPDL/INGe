<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://xmlns.jcp.org/jsf/core"
	xmlns:h="http://xmlns.jcp.org/jsf/html"
	xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
	xmlns:p="http://primefaces.org/ui"
	xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
	<title><h:outputText value="#{ApplicationBean.appTitle}" /></title>
	<ui:include src="header/ui/StandardImports.jspf" />
	<!-- unapi interface for zotero -->
	<link rel="unapi-server" type="application/xml" title="unAPI"
		href="${BatchItemsRetrieverRequestBean.unapiURLview}" />
	<h:outputStylesheet	name="commonJavaScript/jquery/css/jquery-ui-1.10.4.min.css" />
	<h:outputScript name="commonJavaScript/jquery/jquery-3.6.0.js" />
	<h:outputScript name="commonJavaScript/jquery/jquery-migrate-3.3.2.js" />
	<!--
	<h:outputScript name="commonJavaScript/jquery/jquery-ui-1.10.4.min.js" />
	  -->
	<script src="/cone/js/jquery.suggest.js"></script>
	<h:outputScript
		name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
	<f:event type="preRenderView" listener="#{BatchWorkspacePage.init}" />
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label" />
		<f:loadBundle var="msg" basename="Messages" />
		<f:loadBundle var="tip" basename="Tooltip" />
		<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			<ui:include src="header/Header.jspf" />
			<h:form id="form1">
				<div id="content" class="full_area0 clear">
					<!-- begin: content section (including elements that visually belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							<ui:include src="header/Breadcrumb.jspf" />
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1>
									<h:outputText value="#{lbl.BatchWorkspacePage}" />
								</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
								<!-- content menu starts here -->
								<div class="free_area0 sub">
									<!-- content menu upper line starts here -->
									<h:commandLink id="lnkList_lblViewOptions"
										title="#{tip.List_lblViewOptions}" styleClass="free_area0"
										value="#{lbl.List_lblViewOptions}"
										action="#{PubItemListSessionBean.changeSubmenuToView}"
										rendered="#{PubItemListSessionBean.subMenu != 'VIEW' and PubItemListSessionBean.totalNumberOfElements>0}"
										onclick="fullItemReloadAjax();" />
									<h:outputText styleClass="free_area0"
										value="#{lbl.List_lblViewOptions}"
										rendered="#{PubItemListSessionBean.subMenu == 'VIEW' and PubItemListSessionBean.totalNumberOfElements>0}" />
									<h:outputText styleClass="seperator void"
										rendered="#{PubItemListSessionBean.totalNumberOfElements>0}" />
									<!-- #{PubItemListSessionBean.subMenu != 'SORTING' and PubItemListSessionBean.totalNumberOfElements>0} -->
									<h:commandLink id="lnkList_lblSortOptions"
										title="#{tip.List_lblSortOptions}" styleClass="free_area0"
										value="#{lbl.List_lblSortOptions}"
										action="#{PubItemListSessionBean.changeSubmenuToSorting}"
										rendered="#{PubItemListSessionBean.subMenu != 'SORTING' and PubItemListSessionBean.totalNumberOfElements>0}"
										onclick="fullItemReloadAjax();" />
									<h:outputText styleClass="free_area0"
										value="#{lbl.List_lblSortOptions}"
										rendered="#{PubItemListSessionBean.subMenu == 'SORTING' and PubItemListSessionBean.totalNumberOfElements>0}" />
									<h:outputText styleClass="seperator void"
										rendered="#{PubItemListSessionBean.totalNumberOfElements>0}" />
									<!-- #{PubItemListSessionBean.subMenu != 'EXPORT' and PubItemListSessionBean.totalNumberOfElements>0} -->
									<h:commandLink id="lnkList_lblExportAllOptions"
										title="#{tip.List_lblExportOptions}" styleClass="free_area0"
										value="#{lbl.List_lblExportOptions}"
										action="#{PubItemListSessionBean.changeSubmenuToExport}"
										rendered="#{PubItemListSessionBean.subMenu != 'EXPORT' and PubItemListSessionBean.totalNumberOfElements>0}"
										onclick="fullItemReloadAjax();" />
									<h:outputText styleClass="free_area0"
										value="#{lbl.List_lblExportOptions}"
										rendered="#{PubItemListSessionBean.subMenu == 'EXPORT' and PubItemListSessionBean.totalNumberOfElements>0}" />
									<h:outputText styleClass="seperator void"
										rendered="#{PubItemListSessionBean.totalNumberOfElements>0 and PubItemListSessionBean.subMenu != 'ACTIONS' and PubItemListSessionBean.subMenu != 'PROCESS_LOG'}" />
									<h:commandLink id="lnkBatch_lblRemoveSelected"
										title="#{tip.List_lblRemoveFromBatch}" styleClass="free_area0"
										value="#{lbl.Batch_lblRemoveSelectedUpperCase}"
										action="#{BatchItemsRetrieverRequestBean.deleteSelected}"
										rendered="#{PubItemListSessionBean.totalNumberOfElements>0 and PubItemListSessionBean.subMenu != 'ACTIONS' and PubItemListSessionBean.subMenu != 'PROCESS_LOG'}"
										onclick="fullItemReloadAjax();" />
									<h:outputText styleClass="seperator void"
										rendered="#{PubItemListSessionBean.totalNumberOfElements>0 }" />
									<h:commandLink id="lnkList_lblActions"
										title="#{tip.List_lblActionOptions}" styleClass="free_area0"
										value="#{lbl.List_lblActionOptions}"
										action="#{PubItemListSessionBean.changeSubmenuToActions}"
										rendered="#{PubItemListSessionBean.subMenu != 'ACTIONS' and PubItemListSessionBean.totalNumberOfElements>0}"
										onclick="fullItemReloadAjax();" />
									<h:outputText styleClass="free_area0"
										value="#{lbl.List_lblActionOptions}"
										rendered="#{PubItemListSessionBean.subMenu == 'ACTIONS' and PubItemListSessionBean.totalNumberOfElements>0}" />
									<h:outputText styleClass="seperator void"/>
									<h:commandLink id="lnkList_lblProcessLog"
										title="#{tip.List_lblBatchProcessLog}" styleClass="free_area0"
										value="#{lbl.List_lblBatchProcessLog}"
										action="#{PubItemListSessionBean.changeSubmenuToProcessLog}"
										rendered="#{PubItemListSessionBean.subMenu != 'PROCESS_LOG'}"
										onclick="fullItemReloadAjax();" />
									<h:outputText styleClass="free_area0"
										value="#{lbl.List_lblBatchProcessLog}"
										rendered="#{PubItemListSessionBean.subMenu == 'PROCESS_LOG'}" />
									<!-- content menu upper line ends here -->
								</div>
								<!-- content menu lower line starts here -->
								<h:panelGroup id="export" layout="block"
									styleClass="free_area0 sub action"
									rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}">
									<h:panelGroup layout="block"
										styleClass="xLarge_area1 endline selectContainer">
										<h:panelGroup layout="block" styleClass="xLarge_area0">
											<h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block"
												styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="selEXPORTFORMAT" styleClass="replace"
											onfocus="updateSelectionBox(this);"
											value="#{ExportItemsSessionBean.exportFormatName}">
											<f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}" />
											<f:ajax render="form1:export" execute="form1:export"
												listener="#{ExportItems.updateExportFormats}" />
										</h:selectOneMenu>
									</h:panelGroup>
									<h:panelGroup layout="block"
										styleClass="medium_area1 endline selectContainer"
										rendered="#{ExportItemsSessionBean.enableFileFormats}">
										<h:panelGroup layout="block" styleClass="medium_area0">
											<h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block"
												styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="selFILEFORMAT" styleClass="replace"
											onfocus="updateSelectionBox(this);"
											value="#{ExportItemsSessionBean.fileFormat}"
											onchange="updateSelectionBox(this);">
											<f:selectItems value="#{ExportItems.CITATION_OPTIONS}" />
											<f:ajax render="form1:export" execute="form1:export"
												listener="#{ExportItems.updateExportFormats}" />
										</h:selectOneMenu>
									</h:panelGroup>
									<h:commandLink id="btnExportDownload"
										title="#{tip.export_btDownload}" styleClass="free_area0"
										value="#{lbl.export_btDownload}"
										action="#{PubItemListSessionBean.exportAllDownload}" />
									<h:outputText styleClass="seperator" />
									<h:commandLink id="btnExportEMail"
										title="#{tip.export_btEMail}" styleClass="free_area0"
										value="#{lbl.export_btEMail}"
										action="#{PubItemListSessionBean.exportAllEmail}" />
									<h:panelGroup layout="block"
										styleClass="free_area0 suggestAnchor endline CSL"
										rendered="#{ExportItemsSessionBean.enableCslAutosuggest }">
										<h:inputText id="inputCitationStyleName"
											styleClass="huge_txtInput citationStyleSuggest citationStyleName"
											value="#{ExportItemsSessionBean.citationStyleName}"
											title="#{ExportItemsSessionBean.citationStyleName}"
											pt:placeholder="Zitierstil eingeben" />
										<h:inputText id="inputCitationStyleIdentifier"
											styleClass="noDisplay citationStyleIdentifier"
											value="#{ExportItemsSessionBean.coneCitationStyleId}" />
										<h:outputLink class="fa fa-list-ul"
											value="#{AdvancedSearchBean.suggestConeUrl}citation-styles/all/format=html"
											title="Liste aller Zitierstile" target="_blank" rel="noreferrer noopener" />
										<h:commandButton id="btnRemoveCslAutoSuggest" value=" "
											styleClass="xSmall_area0 min_imgBtn closeIcon removeAutoSuggestCsl"
											style="display:none;" onclick="removeCslAutoSuggest($(this))"
											title="#{tip.ViewItem_lblRemoveAutosuggestCsl}">
											<f:ajax render="form1:iterCreatorOrganisationAuthors"
												execute="@form" />
										</h:commandButton>
									</h:panelGroup>
									<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub action"
									rendered="#{PubItemListSessionBean.subMenu == 'VIEW' and PubItemListSessionBean.totalNumberOfElements>0}">
									<!-- content menu lower line starts here -->
									<h:commandLink id="lnkChangeListTypeToBib"
										title="#{tip.List_lblBibList}" styleClass="free_area0"
										rendered="#{PubItemListSessionBean.listType == 'GRID'}"
										action="#{PubItemListSessionBean.changeListTypeToBib}"
										onclick="fullItemReloadAjax();">
										<h:outputText value="#{lbl.List_lblBibList}" />
									</h:commandLink>
									<h:outputText styleClass="free_area0"
										value="#{lbl.List_lblBibList}"
										rendered="#{PubItemListSessionBean.listType == 'BIB'}" />
									<h:outputText styleClass="seperator" />
									<h:commandLink id="lnkChangeListTypeToGrid"
										title="#{tip.List_lblGridList}" styleClass="free_area0"
										rendered="#{PubItemListSessionBean.listType == 'BIB'}"
										action="#{PubItemListSessionBean.changeListTypeToGrid}"
										onclick="fullItemReloadAjax();">
										<h:outputText value="#{lbl.List_lblGridList}" />
									</h:commandLink>
									<h:outputText styleClass="free_area0"
										value="#{lbl.List_lblGridList}"
										rendered="#{PubItemListSessionBean.listType == 'GRID'}" />
									<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="xHuge_area0 sub action"
									rendered="#{PubItemListSessionBean.subMenu == 'SORTING' and PubItemListSessionBean.totalNumberOfElements>0}">
									<!-- content menu lower line starts here -->
									<h:outputText styleClass="free_area0"
										value="#{lbl.ItemList_SortBy} " />
									<h:panelGroup layout="block"
										styleClass="xLarge_area1 endline selectContainer">
										<h:panelGroup layout="block" styleClass="xLarge_area0">
											<h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
											<h:panelGroup layout="block"
												styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
										</h:panelGroup>
										<h:selectOneMenu id="sortBy" styleClass="replace"
											onfocus="updateSelectionBox(this);"
											value="#{PubItemListSessionBean.selectedSortBy}"
											onchange="$(this).parents('div').find('.changeSortBy').click();">
											<f:selectItems
												value="#{PubItemListSessionBean.sortBySelectItems}" />
										</h:selectOneMenu>
									</h:panelGroup>
									<h:commandLink title="#{tip.list_ascending}"
										styleClass="ascSort"
										value="#{lbl.ItemList_SortOrderAscending}" id="sortOrderAsc"
										rendered="#{PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}"
										action="#{PubItemListSessionBean.changeSortOrder}"
										onclick="fullItemReloadAjax();" />
									<h:commandLink title="#{tip.list_descending}"
										styleClass="desSort"
										value="#{lbl.ItemList_SortOrderDescending}" id="sortOrderDesc"
										rendered="#{!PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}"
										action="#{PubItemListSessionBean.changeSortOrder}"
										onclick="fullItemReloadAjax();" />
									<h:commandButton id="btnChangeSortBy"
										styleClass="noDisplay changeSortBy" value=" "
										action="#{PubItemListSessionBean.changeSortBy}" />
									<!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="xHuge_area0 sub action"
									rendered="#{PubItemListSessionBean.subMenu == 'PROCESS_LOG'}">
										<!-- Submenu starts here -->
										<h:commandLink id="btnRefillBatchLog"
											title="#{tip.List_lblBatchProcessLog_refillWithLog}" styleClass="free_area0"
											value="#{lbl.BatchWorkspace_lblProcessLog_refillWithLog}"
											action="#{PubItemListSessionBean.refillBatchWithLog}" />
											<h:outputText styleClass="seperator" />
										<h:commandLink id="btnRefillBatchError"
											title="#{tip.List_lblBatchProcessLog_refillWithErrors}" styleClass="free_area0"
											value="#{lbl.BatchWorkspace_lblProcessLog_refillWithErrors}"
											action="#{PubItemListSessionBean.refillBatchWithErrorsOnly}" />
										<!-- Submenu ends here -->
								</h:panelGroup>
								<!-- content menu ends here -->
							</div>
							<div class="subHeader">
								<h:panelGroup rendered="#{PubItemListSessionBean.subMenu != 'ACTIONS' and PubItemListSessionBean.subMenu != 'PROCESS_LOG'}">
									<h:outputText
										value="#{PubItemListSessionBean.totalNumberOfElements} #{lbl.SearchResultList_lblItems}"
										rendered="#{PubItemListSessionBean.totalNumberOfElements > 0}" />
									<h:outputText value=" ("
										rendered="#{PubItemListSessionBean.totalNumberOfElements > 0}" />
									<h:outputText
										value="#{lbl.ENUM_SORTORDER_ASCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}"
										rendered="#{PubItemListSessionBean.isAscending and PubItemListSessionBean.totalNumberOfElements > 0}" />
									<h:outputText
										value="#{lbl.ENUM_SORTORDER_DESCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}"
										rendered="#{!PubItemListSessionBean.isAscending and PubItemListSessionBean.totalNumberOfElements > 0}" />
									<h:outputText value=")"
										rendered="#{PubItemListSessionBean.totalNumberOfElements > 0}" />
								</h:panelGroup>
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError"
									warnClass="messageWarn" fatalClass="messageFatal"
									infoClass="messageStatus" layout="list" globalOnly="true"
									showDetail="false" showSummary="true"  escape="false"
									rendered="#{ExportItemsSessionBean.numberOfMessages == 1}" />
								<h:panelGroup layout="block"
									styleClass="half_area2_p6 messageArea errorMessageArea"
									rendered="#{ExportItemsSessionBean.hasErrorMessages and ExportItemsSessionBean.numberOfMessages != 1}">
									<h2>
										<h:outputText value="#{lbl.warning_lblMessageHeader}" />
									</h2>
									<h:messages errorClass="messageError" warnClass="messageWarn"
										fatalClass="messageFatal" infoClass="messageStatus"
										layout="list" globalOnly="true" showDetail="false"
										showSummary="true"  escape="false"
										rendered="#{ExportItemsSessionBean.hasMessages}" />
								</h:panelGroup>
								<h:panelGroup layout="block"
									styleClass="half_area2_p6 messageArea infoMessageArea"
									rendered="#{ExportItemsSessionBean.hasMessages and !ExportItemsSessionBean.hasErrorMessages and ExportItemsSessionBean.numberOfMessages != 1}">
									<h2>
										<h:outputText value="#{lbl.info_lblMessageHeader}" />
									</h2>
									<h:messages errorClass="messageError" warnClass="messageWarn"
										fatalClass="messageFatal" infoClass="messageStatus"
										layout="list" globalOnly="true" showDetail="false"
										showSummary="true"  escape="false"
										rendered="#{ExportItemsSessionBean.hasMessages}" />
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<h:panelGroup layout="block" styleClass="full_area0"
						rendered="#{PubItemListSessionBean.subMenu == 'ACTIONS'}">
						<ui:include src="batch/batchActions.jspf" />
					</h:panelGroup>
					<h:panelGroup layout="block" styleClass="full_area0"
						rendered="#{PubItemListSessionBean.subMenu == 'PROCESS_LOG'}">
						<ui:include src="batch/batchProcessLog.jspf" />
					</h:panelGroup>
					<h:panelGroup layout="block" styleClass="full_area0"
						rendered="#{PubItemListSessionBean.listType == 'BIB' and PubItemListSessionBean.partListSize>0 and PubItemListSessionBean.subMenu != 'ACTIONS' and PubItemListSessionBean.subMenu != 'PROCESS_LOG'}">
						<ui:include src="list/itemList.jspf" />
					</h:panelGroup>
					<h:panelGroup layout="block" styleClass="full_area0"
						rendered="#{PubItemListSessionBean.listType == 'GRID' and PubItemListSessionBean.partListSize>0 and PubItemListSessionBean.subMenu != 'ACTIONS' and PubItemListSessionBean.subMenu != 'PROCESS_LOG'}">
						<ui:include src="list/gridList.jspf" />
					</h:panelGroup>
					<h:panelGroup styleClass="full_area0"
						rendered="#{PubItemListSessionBean.partListSize==0}">
						<h:outputText styleClass="free_area0 small_marginLExcl"
							value="#{msg.batch_NoItems}" />
					</h:panelGroup>
					<!-- end: content section -->
				</div>
			</h:form>
		</div>
		<ui:include src="footer/Footer.jspf" />
		<script type="text/javascript">
      var citationStyleSuggestURL = '<h:outputText value="#{AdvancedSearchBean.suggestConeUrl}"/>citation-styles/query';
      var citationStyleSuggestBaseURL = '$1?format=json';
      $("input[id$='offset']").on('submit',function() {
        $(this).val($(window).scrollTop());
      });
      $(document).ready(function() {
        $(window).scrollTop($("input[id$='offset']").val());
        $(window).on('scroll',function() {
          $("input[id$='offset']").val($(window).scrollTop());
        });
        checkUpdateCslUi();
      });

      function checkUpdateCslUi() {
        (typeof updateCslUi == 'function') ? updateCslUi() : setTimeout(
                "checkUpdateCslUi()", 30);
      }
    </script>
	</f:view>
</body>

</html>