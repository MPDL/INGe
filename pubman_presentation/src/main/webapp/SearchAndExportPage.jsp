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
    <style>
        .dialogNoTitleBar .ui-dialog-titlebar {
            display: none;
        }

        .dialogNoTitleBar {
            background: none;
            border: none;
        }
    </style>
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
                                    <h:outputText value="#{lbl.searchAndExport_title}" />
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
                    <div class="full_area0">
                        <div class="full_area0 fullItem">
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
	                            <h:commandButton id="btnUpdateExportFormats" title="#{tip.export_btFormat}" styleClass="noDisplay exportUpdateButton" action="#{ExportItems.updateExportFormats}" value="updateExportFormats" />
	                            <h:panelGroup layout="block" styleClass="medium_area1 endline selectContainer" rendered="#{ExportItemsSessionBean.enableFileFormats}">
	                                <h:panelGroup layout="block" styleClass="medium_area0">
	                                    <h:panelGroup styleClass="medium_area0 selectionBox">&#160;</h:panelGroup>
	                                    <h:panelGroup layout="block" styleClass="min_imgArea selectboxIcon">&#160;</h:panelGroup>
	                                </h:panelGroup>
	                                <h:selectOneMenu id="selFileFormat" onfocus="updateSelectionBox(this);" value="#{ExportItemsSessionBean.fileFormat}" onchange="updateSelectionBox(this);">
	                                    <f:selectItems value="#{ExportItems.FILEFORMAT_OPTIONS}" />
	                                </h:selectOneMenu>
	                            </h:panelGroup>
	                            <h:commandLink title="#{tip.export_btDownload}" id="btnExportDownload" styleClass="free_area0 xTiny_marginLExcl" value="#{lbl.export_btDownload}" action="#{SearchAndExportPage.searchAndExport}" />
	                            <h:panelGroup layout="block" styleClass="free_area0 suggestAnchor endline CSL" rendered="#{ExportItemsSessionBean.enableCslAutosuggest }">
	                                <h:inputText id="inputCitationStyleName" styleClass="huge_txtInput citationStyleSuggest citationStyleName" value="#{ExportItemsSessionBean.citationStyleName}" title="#{ExportItemsSessionBean.citationStyleName}" pt:placeholder="Zitierstil eingeben" />
	                                <h:inputText id="inputCitationStyleIdentifier" styleClass="noDisplay citationStyleIdentifier" value="#{ExportItemsSessionBean.coneCitationStyleId}" />
	                                <h:outputLink class="fa fa-list-ul" value="#{AdvancedSearchBean.suggestConeUrl}citation-styles/all/format=html" title="Liste aller Zitierstile" target="_blank" />
	                                <h:commandButton id="btnRemoveCslAutoSuggest" value=" " styleClass="xSmall_area0 min_imgBtn closeIcon removeAutoSuggestCsl" style="display:none;" onclick="removeCslAutoSuggest($(this))" title="#{tip.ViewItem_lblRemoveAutosuggestCsl}" />
	                            </h:panelGroup>
	                            <!-- content menu lower line ends here -->
	                        </h:panelGroup>
                        </div>
                    </div>
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