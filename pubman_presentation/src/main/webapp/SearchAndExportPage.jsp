<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
    <h:outputStylesheet name="commonJavaScript/jquery/css/jquery-ui-1.10.4.min.css" />
    <h:outputScript name="commonJavaScript/jquery/jquery-ui-1.10.4.min.js" />
    <script src="/cone/js/jquery.suggest.js"></script>
	<h:outputScript name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{SearchAndExportPage.init}" />
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
                    <h:panelGroup layout="block" styleClass="clear">
                        <h:panelGroup layout="block" styleClass="headerSection">
                            <ui:include src="header/Breadcrumb.jspf" />
                            <div id="contentSkipLinkAnchor" class="clear headLine">
                                <!-- Headline starts here -->
                                <h1>
                                    <h:outputText value="#{lbl.searchAndExport_Title}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </h:panelGroup>
                        <h:panelGroup layout="block" styleClass="small_marginLIncl subHeaderSection">
                            <div class="contentMenu">
                                <!-- content menu starts here -->
                                <div class="free_area0 sub">
                                    <!-- content menu lower line starts here -->
                                    <h:outputText styleClass="seperator void" />
                                    <!-- content menu lower line ends here -->
                                </div>
                                <!-- content menu ends here -->
                            </div>
                            <h:panelGroup layout="block" styleClass="subHeader" rendered="false">
                                <!-- Subheadline starts here -->
                                <!-- Subheadline ends here -->
                            </h:panelGroup>
                        </h:panelGroup>
                    </h:panelGroup>
                    
                    <div class="full_area0 fullItem">
                       	<div class="full_area0 itemBlock">
                            <h:commandButton id="btnUpdatePage" styleClass="noDisplay updatePage" action="#{SearchAndExportPage.updatePage}" />
                       		<h3 class="xLarge_area0_p8 endline blockHeader">
								#{lbl.searchAndExport_Form}
							</h3>
							<span class="seperator"></span>
							<div class="free_area0 itemBlockContent endline">
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										#{lbl.searchAndExport_Query}<span class="noDisplay"></span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<h:inputText styleClass="quad_txtInput" id="esQuery" value="#{SearchAndExportPage.esQuery}" required="true" requiredMessage="#{msg.error_required}" validator="#{SearchAndExportPage.validateQuery}" onchange="$(this).parents('.full_area0').find('.updatePage').click();" />
								    	<h:message styleClass="quad_txtInput" for="esQuery" style="color:red"/>
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										#{lbl.searchAndExport_Sorting}<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<span class="xHuge_area0 endline">
											<a href="https://subversion.mpdl.mpg.de/repos/smc/tags/public/PubMan/Material/SortingKeys.xlsx" target="_blank">#{lbl.searchAndExport_ListSortingKey}</a>
										</span>
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="sortKeys">#{lbl.searchAndExport_SortingKey}</label>
											<h:inputText styleClass="double_txtInput" name="sortKeys" value="#{SearchAndExportPage.sortingKey}" onchange="$(this).parents('.full_area0').find('.updatePage').click();" />
										</span>
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="sortOrder">#{lbl.searchAndExport_SortingOrder}</label>
			                                <h:selectOneMenu id="selsortOptions" onfocus="updateSelectionBox(this);" value="#{SearchAndExportPage.sortOrder}" onchange="updateSelectionBox(this);$(this).parents('.full_area0').find('.updatePage').click();" >
			                                    <f:selectItems value="#{SearchAndExportPage.sortOptions}" />
			                                </h:selectOneMenu>
										</span>
									</span>
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										#{lbl.searchAndExport_RecordSpan}<span class="noDisplay">: </span>
									</b>
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<span class="double_area0 xTiny_marginRIncl">
											<label class="double_label" for="offsetId">#{lbl.searchAndExport_Offset}</label>
											<h:inputText styleClass="double_txtInput" id="offsetId" value="#{SearchAndExportPage.offset}" required="true" requiredMessage="#{msg.error_required}" validator="#{SearchAndExportPage.validateOffset}" onchange="$(this).parents('.full_area0').find('.updatePage').click();" />
									    	<h:message styleClass="double_txtInput" for="offsetId" style="color:red"/>
										</span>
										<span class="double_area0 xTiny_marginRIncl">
											<h:outputLabel styleClass="double_label" for="limitId" value="#{lbl.searchAndExport_MaxLimit} #{SearchAndExportPage.maxLimit})" />
											<h:inputText styleClass="double_txtInput" id="limitId" value="#{SearchAndExportPage.limit}" required="true" requiredMessage="#{msg.error_required}" validator="#{SearchAndExportPage.validateLimit}" onchange="$(this).parents('.full_area0').find('.updatePage').click();" />
									    	<h:message styleClass="double_txtInput" for="limitId" style="color:red"/>
										</span>
									</span> 
								</div>
								<div class="free_area0 endline itemLine noTopBorder">
									<b class="xLarge_area0_p8 endline labelLine clear">
										#{lbl.searchAndExport_Options}<span class="noDisplay">: </span>
									</b>								
									<span class="xHuge_area0 xTiny_marginLExcl endline">
				                        <h:panelGroup layout="block" styleClass="xHuge_area0 sub action">
				                            <h:panelGroup layout="block" styleClass="xLarge_area1 endline selectContainer">
				                                <h:panelGroup layout="block" styleClass="xLarge_area0">
				                                    <h:panelGroup styleClass="xLarge_area0 selectionBox">&#160;</h:panelGroup>
				                                    <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
				                                </h:panelGroup>
			 	                                <h:selectOneMenu id="selExportFormatName" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.exportFormatName}" onchange="$(this).parents('.sub').find('.exportUpdateButton').click();">
				                                    <f:selectItems value="#{ExportItems.EXPORTFORMAT_OPTIONS}" />
				                                </h:selectOneMenu>
				                            </h:panelGroup>
				                            <h:commandButton id="btnUpdateExportFormats" styleClass="noDisplay exportUpdateButton" action="#{ExportItems.updateExportFormats}" value="updateExportFormats" />
				                            <h:panelGroup layout="block" styleClass="medium_area1 endline selectContainer" rendered="#{ExportItemsSessionBean.enableFileFormats}">
				                                <h:panelGroup layout="block" styleClass="medium_area0">
				                                    <h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
				                                    <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
				                                </h:panelGroup>
				                                <h:selectOneMenu id="selFileFormat" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.fileFormat}" onchange="updateSelectionBox(this);$(this).parents('.full_area0').find('.updatePage').click();">
				                                    <f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}" />
				                                </h:selectOneMenu>
				                            </h:panelGroup>
				                            <h:panelGroup layout="block" styleClass="free_area0 suggestAnchor endline CSL" rendered="#{ExportItemsSessionBean.enableCslAutosuggest }">
				                                <h:inputText id="inputCitationStyleName" styleClass="huge_txtInput citationStyleSuggest citationStyleName" value="#{ExportItemsSessionBean.citationStyleName}" title="#{ExportItemsSessionBean.citationStyleName}" pt:placeholder="#{lbl.searchAndExport_EnterCitationStyle}" />
				                                <h:inputText id="inputCitationStyleIdentifier" styleClass="noDisplay citationStyleIdentifier" value="#{ExportItemsSessionBean.coneCitationStyleId}" />
				                                <h:outputLink styleClass="fa fa-list-ul" value="#{AdvancedSearchBean.suggestConeUrl}citation-styles/all/format=html" title="#{lbl.searchAndExport_ListCitationStyle}" target="_blank" />
				                                <h:commandButton id="btnRemoveCslAutoSuggest" value=" " styleClass="xSmall_area0 min_imgBtn closeIcon removeAutoSuggestCsl" style="display:none;" onclick="removeCslAutoSuggest($(this))" title="#{lbl.searchAndExport_btnRemoveAutoSuggestCsl}" />
				                            </h:panelGroup>
				                        </h:panelGroup>
									</span>
								</div>
							</div>
                        </div>
                     
                    </div>
                    
					<ui:param name="errorMessages" value="#{facesContext.getMessageList()}" />
					                  
                    <div class="full_area0 formButtonArea">
                        <h:commandLink title="#{tip.searchAndExport_btnDownload}" id="btnExportDownload" styleClass="free_area1_p8 activeButton" value="#{lbl.searchAndExport_btnDownload}" action="#{SearchAndExportPage.searchAndExport}" rendered="#{empty errorMessages}" />
                    </div>
                    
                    <div class="full_area0 formButtonArea">
                        <h:commandLink title="#{tip.searchAndExport_btnCheck}" id="btnCheckInput" styleClass="free_area1_p8 activeButton" value="#{lbl.searchAndExport_btnCheck}" action="#{SearchAndExportPage.updatePage}" rendered="#{not empty errorMessages}" />
                    </div>
                  
	                <h:panelGroup layout="block" styleClass="full_area0 clear" rendered="#{empty errorMessages}">
	                    <div class="full_area0 fullItem">
	                       	<div class="full_area0 itemBlock">
	                       		<h3 class="xLarge_area0_p8 endline blockHeader">
									#{lbl.searchAndExport_CompleteQuery}
								</h3>
								<span class="seperator"></span>
								<div class="free_area0 itemBlockContent endline">
									<span class="third_area0 xTiny_marginLExcl endline">
										<span class="threequarter_area0 endline">
				                             <h:outputText styleClass="threequarter_area0 endline" value="#{SearchAndExportPage.searchString}" />
										</span>
									</span>
			                    </div>
		                    </div>
		                    
	                       	<div class="full_area0 itemBlock">
	                       		<h3 class="xLarge_area0_p8 endline blockHeader">
									#{lbl.searchAndExport_Feed}
								</h3>
								<span class="seperator"></span>
								<div class="free_area0 itemBlockContent endline">
									<span class="xHuge_area0 xTiny_marginLExcl endline">
										<span class="xHuge_area0 endline">
				                            <h:outputLink styleClass="xHuge_area0 endline" value="#{SearchAndExportPage.atomFeedLink}" title="Atom, version 1.0" target="_blank">Atom, version 1.0</h:outputLink>
										</span>
									</span>
			                    </div>
		                    </div>
		                    
	                    </div>
	                </h:panelGroup>
                 
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>
        
        <ui:include src="footer/Footer.jspf" />
        <script type="text/javascript">
            var citationStyleSuggestURL = '<h:outputText value="#{AdvancedSearchBean.suggestConeUrl}"/>citation-styles/query';
            var citationStyleSuggestBaseURL = '$1?format=json';
            $("input[id$='offset']").submit(function() {
                $(this).val($(window).scrollTop());
            });
            $(document).ready(function() {
                $(window).scrollTop($("input[id$='offset']").val());
                $(window).scroll(function() {
                    $("input[id$='offset']").val($(window).scrollTop());
                });
                checkUpdateCslUi();
            });

            function checkUpdateCslUi() {
                (typeof updateCslUi == 'function') ? updateCslUi(): setTimeout("checkUpdateCslUi()", 30);
            }
        </script>
    </f:view>
</body>

</html>