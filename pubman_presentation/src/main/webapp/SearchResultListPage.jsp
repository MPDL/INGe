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
	<ui:include src="search/SearchResultFeedLinks.jspf" />
	<!-- unapi interface for zotero -->
	<link rel="unapi-server" type="application/xml" title="unAPI"
		href="${SearchRetrieverRequestBean.unapiURLview}" />
	<h:outputStylesheet name="commonJavaScript/jquery/css/jquery-ui-1.10.4.min.css" />
	<h:outputScript name="commonJavaScript/jquery/jquery-3.6.0.js" />
	<h:outputScript name="commonJavaScript/jquery/jquery-migrate-3.3.2.js" />
	<!--
	<h:outputScript name="commonJavaScript/jquery/jquery-ui-1.10.4.min.js" />
	  -->
	<script src="/cone/js/jquery.suggest.js"></script>
	<h:outputScript name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
	<f:event type="preRenderView" listener="#{SearchResultListPage.init}" />
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
									<h:outputText value="#{lbl.SearchResultListPage}" />
								</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<h:panelGroup rendered="#{PubItemListSessionBean.partListSize>0}">
								<div class="contentMenu">
									<!-- content menu starts here -->
									<div class="free_area0 sub">
										<!-- content menu upper line starts here -->
										<h:commandLink id="lnkList_lblViewOptions"
											title="#{tip.List_lblViewOptions}" styleClass="free_area0"
											value="#{lbl.List_lblViewOptions}"
											action="#{PubItemListSessionBean.changeSubmenuToView}"
											rendered="#{PubItemListSessionBean.subMenu != 'VIEW'}"
											onclick="fullItemReloadAjax();" />
										<h:outputText styleClass="free_area0"
											value="#{lbl.List_lblViewOptions}"
											rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}" />
										<h:outputText styleClass="seperator void" />
										<h:commandLink id="lnkList_lblSortOptions"
											title="#{tip.List_lblSortOptions}" styleClass="free_area0"
											value="#{lbl.List_lblSortOptions}"
											action="#{PubItemListSessionBean.changeSubmenuToSorting}"
											rendered="#{PubItemListSessionBean.subMenu != 'SORTING'}"
											onclick="fullItemReloadAjax();" />
										<h:outputText styleClass="free_area0"
											value="#{lbl.List_lblSortOptions}"
											rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}" />
										<h:outputText styleClass="seperator void" />
										<h:commandLink id="lnkList_lblExportOptions"
											title="#{tip.List_lblExportOptions}" styleClass="free_area0"
											value="#{lbl.List_lblExportOptions}"
											action="#{PubItemListSessionBean.changeSubmenuToExport}"
											rendered="#{PubItemListSessionBean.subMenu != 'EXPORT'}"
											onclick="fullItemReloadAjax();" />
										<h:outputText styleClass="free_area0"
											value="#{lbl.List_lblExportOptions}"
											rendered="#{PubItemListSessionBean.subMenu == 'EXPORT'}" />
										<h:outputText styleClass="seperator void" />
										<h:commandLink id="lnkList_lblSelected"
											title="#{tip.List_lblActionOptions}" styleClass="free_area0"
											value="#{lbl.List_lblActionOptions}"
											action="#{PubItemListSessionBean.changeSubmenuToActions}"
											rendered="#{PubItemListSessionBean.subMenu != 'ACTIONS'}"
											onclick="fullItemReloadAjax();" />
										<h:outputText styleClass="free_area0"
											value="#{lbl.List_lblActionOptions}"
											rendered="#{PubItemListSessionBean.subMenu == 'ACTIONS'}" />
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
										<h:commandLink id="btnExportDownload" styleClass="free_area0" value="#{lbl.export_btDownload}" action="#{PubItemListSessionBean.exportSelectedDownload}" />
										<h:outputText styleClass="seperator" />
										<h:commandLink id="btnExportEMail" styleClass="free_area0" value="#{lbl.export_btEMail}" action="#{PubItemListSessionBean.exportSelectedEmail}" />
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
		                    	            <h:outputLink styleClass="fa fa-list-ul" value="#{ConeSessionBean.suggestConeUrl}citation-styles/all/format=html" title="#{lbl.searchAndExport_ListCitationStyle}" target="_blank" rel="noreferrer noopener" />
	                                        <h:commandButton id="btnRemoveCslAutoSuggest" value=" " styleClass="xSmall_area0 min_imgBtn closeIcon removeAutoSuggestCsl" style="display:none;" onclick="removeCslAutoSuggest($(this))" title="#{tip.ViewItem_lblRemoveAutosuggestCsl}">
	                                            <f:ajax render="form1:iterCreatorOrganisationAuthors" execute="@form" />
	                                        </h:commandButton>
										</h:panelGroup>
										<!-- content menu lower line ends here -->
									</h:panelGroup>
									<h:panelGroup layout="block" styleClass="free_area0 sub action"
										rendered="#{PubItemListSessionBean.subMenu == 'VIEW'}">
										<!-- content menu lower line starts here -->
										<h:commandLink id="lnkList_lblBibListGRID"
											title="#{tip.List_lblGridList}" styleClass="free_area0"
											rendered="#{PubItemListSessionBean.listType == 'GRID'}"
											action="#{PubItemListSessionBean.changeListTypeToBib}"
											onclick="fullItemReloadAjax();">
											<h:outputText value="#{lbl.List_lblBibList}" />
										</h:commandLink>
										<h:outputText styleClass="free_area0"
											value="#{lbl.List_lblBibList}"
											rendered="#{PubItemListSessionBean.listType == 'BIB'}" />
										<h:outputText styleClass="seperator" />
										<h:commandLink id="lnkList_lblBibListBIB"
											title="#{tip.List_lblBibList}" styleClass="free_area0"
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
									<h:panelGroup layout="block" styleClass="free_area0 sub action"
										rendered="#{PubItemListSessionBean.subMenu == 'SORTING'}">
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
											value="#{lbl.ItemList_SortOrderDescending}"
											id="sortOrderDesc"
											rendered="#{!PubItemListSessionBean.isAscending and PubItemListSessionBean.displaySortOrder}"
											action="#{PubItemListSessionBean.changeSortOrder}"
											onclick="fullItemReloadAjax();" />
										<h:commandButton id="btnChangeSortBy"
											styleClass="noDisplay changeSortBy" value=" "
											action="#{PubItemListSessionBean.changeSortBy}" />
									</h:panelGroup>
									<h:panelGroup layout="block" styleClass="free_area0 sub action"
										rendered="#{PubItemListSessionBean.subMenu == 'ACTIONS'}">
										<!-- content menu lower line starts here -->
										<h:commandLink id="lnkList_lblAddToBasket"
											title="#{tip.List_lblAddToBasket}" styleClass="free_area0"
											value="#{lbl.List_lblAddToBasket}"
											action="#{PubItemListSessionBean.addSelectedToCart}"
											onclick="fullItemReloadAjax();" />
										<h:outputText styleClass="seperator" rendered="#{(LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0) or LoginHelper.isAdmin}"/>
										<h:commandLink id="lnkList_lblAddSelectionToBatch"
											title="#{tip.List_lblAddSelectionToBatch}" styleClass="free_area0"
											value="#{lbl.List_lblAddSelectionToBatch}"
											action="#{PubItemListSessionBean.addSelectedToBatch}"
											onclick="fullItemReloadAjax();" 
											rendered="#{(LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0) or LoginHelper.isAdmin}"/>
										<h:outputText styleClass="seperator" rendered="#{(LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0) or LoginHelper.isAdmin}"/>
										<h:commandLink id="lnkList_lblAddAllToBatch"
											title="#{tip.List_lblAddAllToBatch}" styleClass="free_area0"
											value="#{lbl.List_lblAddAllToBatch}"
											action="#{PubItemListSessionBean.addAllToBatch}"
											onclick="fullItemReloadAjax();" 
											rendered="#{(LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0) or LoginHelper.isAdmin}"/>
											
									</h:panelGroup>
									<!-- content menu lower line ends here -->
									<!-- content menu ends here -->
								</div>
								<div class="subHeader">
									<h:outputText
										value="#{PubItemListSessionBean.totalNumberOfElements} #{lbl.SearchResultList_lblItems} #{lbl.SearchResultList_lblFound}" />
									<h:outputText value=" (" />
									<h:outputText
										value="#{lbl.ENUM_SORTORDER_ASCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}"
										rendered="#{PubItemListSessionBean.isAscending}" />
									<h:outputText
										value="#{lbl.ENUM_SORTORDER_DESCENDING} #{lbl.SearchResultList_lblSortedBy} #{PubItemListSessionBean.selectedSortByLabel}"
										rendered="#{!PubItemListSessionBean.isAscending}" />
									<h:outputText value=")" />
								</div>
							</h:panelGroup>
							<!-- For advanced search, show links for refine, cql query and REST -->
							<h:panelGroup layout="block" styleClass="subHeader"
								rendered="#{SearchRetrieverRequestBean.searchType == 'advanced'}">
								<!-- Subheadline starts here -->
								<h:outputLink id="lnkAdvancedSearchPage"
									styleClass="free_area0 xTiny_marginRIncl"
									value="AdvancedSearchPage.jsp?q=#{SearchRetrieverRequestBean.urlEncodedQueryString}">
									<h:outputText value="#{lbl.SearchResultList_lblAdvancedSearch}" />
								</h:outputLink>
								<a class="free_area0 xTiny_marginRIncl" href="#"
									onclick="$(this).parents('.subHeaderSection').find('.searchQuery').slideToggle('slow'); $(this).hide();">
									<h:outputText value="#{lbl.ShowQuery}" />
								</a>

								<!-- 
                                <h:outputLink id="lnkRestServiceExamplePage" styleClass="free_area0 xTiny_marginRIncl" value="SearchAndExportPage.jsp?esq=#{SearchRetrieverRequestBean.minifiedUrlEncodedElasticSearchQuery}">
                                    <h:outputText value="#{lbl.SearchResultList_lblRestServiceExamplePage}" />
                                </h:outputLink>
                                 -->

								<h:commandLink id="lnkBtnRest"
									styleClass="free_area0 xTiny_marginRIncl"
									value="#{lbl.SearchResultList_lblRestServiceExamplePage}"
									action="#{SearchResultListPage.rest()}">
									<f:param name="query"
										value="#{SearchRetrieverRequestBean.minifiedUrlEncodedElasticSearchQuery}" />
								</h:commandLink>

								<!-- Subheadline ends here -->
							</h:panelGroup>
							<!-- For admin search, show links for refine and cql query -->
							<h:panelGroup layout="block" styleClass="subHeader"
								rendered="#{SearchRetrieverRequestBean.searchType == 'admin'}">
								<!-- Subheadline starts here -->
								<h:outputLink id="lnkAdminAdvancedSearchPage"
									styleClass="free_area0 xTiny_marginRIncl"
									value="AdminAdvancedSearchPage.jsp?q=#{SearchRetrieverRequestBean.urlEncodedQueryString}">
									<h:outputText value="#{lbl.SearchResultList_lblAdvancedSearch}" />
								</h:outputLink>
								<a class="free_area0 xTiny_marginRIncl" href="#"
									onclick="$(this).parents('.subHeaderSection').find('.searchQuery').slideToggle('slow'); $(this).hide();">
									<h:outputText value="#{lbl.ShowQuery}" />
								</a>
								<!-- Subheadline ends here -->
							</h:panelGroup>
							<h:panelGroup layout="block" styleClass="subHeader"
								rendered="#{SearchRetrieverRequestBean.searchType == 'advanced' or SearchRetrieverRequestBean.searchType == 'admin'}">
								<!-- Subheadline starts here -->
								<h:panelGroup layout="block"
									styleClass="full_area0_p6 searchQuery" style="display: none;">
									<h2>
										<h:outputText value="#{msg.searchResultList_QueryString}" />
									</h2>
									<h:inputTextarea styleClass="half_txtArea inputTxtArea"
										rows="30"
										value="#{SearchRetrieverRequestBean.elasticSearchQuery}"
										readonly="true" />
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</h:panelGroup>

							<div style="clear: both; margin-top: 0.6363em;">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError"
									warnClass="messageWarn" fatalClass="messageFatal"
									infoClass="messageStatus" layout="list" globalOnly="true"
									showDetail="false" showSummary="true" escape="false"
									rendered="#{SearchRetrieverRequestBean.numberOfMessages == 1}" />
								<h:panelGroup layout="block"
									styleClass="half_area2_p6 messageArea errorMessageArea"
									rendered="#{SearchRetrieverRequestBean.hasErrorMessages and SearchRetrieverRequestBean.numberOfMessages != 1}">
									<h2>
										<h:outputText value="#{lbl.warning_lblMessageHeader}" />
									</h2>
									<h:messages errorClass="messageError" warnClass="messageWarn"
										fatalClass="messageFatal" infoClass="messageStatus"
										layout="list" globalOnly="true" showDetail="false"
										showSummary="true" escape="false"
										rendered="#{SearchRetrieverRequestBean.hasMessages}" />
								</h:panelGroup>
								<h:panelGroup layout="block"
									styleClass="half_area2_p6 messageArea infoMessageArea"
									rendered="#{SearchRetrieverRequestBean.hasMessages and !SearchRetrieverRequestBean.hasErrorMessages and SearchRetrieverRequestBean.numberOfMessages != 1}">
									<h2>
										<h:outputText value="#{lbl.info_lblMessageHeader}" />
									</h2>
									<h:messages errorClass="messageError" warnClass="messageWarn"
										fatalClass="messageFatal" infoClass="messageStatus"
										layout="list" globalOnly="true" showDetail="false"
										showSummary="true" escape="false"
										rendered="#{SearchRetrieverRequestBean.hasMessages}" />
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<h:panelGroup layout="block" styleClass="full_area0"
						rendered="#{PubItemListSessionBean.listType == 'BIB' and PubItemListSessionBean.partListSize>0}">
						<ui:include src="list/itemList.jspf" />
					</h:panelGroup>
					<h:panelGroup layout="block" styleClass="full_area0"
						rendered="#{PubItemListSessionBean.listType == 'GRID' and PubItemListSessionBean.partListSize>0}">
						<ui:include src="list/gridList.jspf" />
					</h:panelGroup>
					<h:panelGroup styleClass="full_area0"
						rendered="#{PubItemListSessionBean.partListSize==0}">
						<h:outputText styleClass="free_area0 small_marginLExcl"
							value="#{msg.searchResultList_Notification}" />
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