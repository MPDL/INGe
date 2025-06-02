<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
    <script src="/cone/js/jquery.suggest.js"></script>
	<h:outputScript name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{AdvancedSearchPage.init}" />
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <div class="full wrapper">
            <h:inputHidden id="offset" />
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
                                    <h:outputText value="#{lbl.search_lblAdvancedSearch}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <h:panelGroup layout="block" styleClass="small_marginLIncl subHeaderSection" rendered="#{AdvancedSearchPage.numberOfMessages > 0}">
                            <h:panelGroup layout="block" styleClass="contentMenu" rendered="false">
                                <!-- content menu starts here -->
                                <h:panelGroup layout="block" styleClass="free_area0 sub">
                                    <!-- content menu lower line starts here -->
                                    &#160;
                                    <!-- content menu lower line ends here -->
                                </h:panelGroup>
                                <!-- content menu ends here -->
                            </h:panelGroup>
                            <h:panelGroup layout="block" styleClass="subHeader" rendered="false">
                                <!-- Subheadline starts here -->
                                &#160;
                                <!-- Subheadline ends here -->
                            </h:panelGroup>
                            <h:panelGroup id="messages" styleClass="subHeader">
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{AdvancedSearchBean.hasErrorMessages}">
                                    <input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" escape="false" rendered="#{AdvancedSearchBean.hasMessages}" />
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea" rendered="#{AdvancedSearchBean.hasMessages and !AdvancedSearchBean.hasErrorMessages}">
                                    <input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" escape="false" rendered="#{AdvancedSearchBean.hasMessages}" />
                                </h:panelGroup>
                            </h:panelGroup>
                            <!-- Subheadline ends here -->
                        </h:panelGroup>
                    </div>
                    <!--  hidden button to enable return key for starting search. If only visiblity:hidden is used, the button does not work in IE -->
                    <h:commandButton id="lnkAdvancedSearchStartSearchTop" style="height:0; width:0; padding:0; margin:0; border:none;" value="#{lbl.adv_search_btStart}" action="#{AdvancedSearchBean.startSearch}" onclick="fullItemReloadAjax();" />
                    <div class="full_area0">
                        <div class="full_area0 fullItem">
                            <ui:include src="search/AdvancedSearchEdit.jspf" />
                        </div>
                    </div>
                    <div class="full_area0 formButtonArea">
                        <h:commandButton id="lnkAdvancedSearchClearAll" styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" value="#{lbl.adv_search_btClearAll}" action="#{AdvancedSearchBean.clearAndInit}" onclick="fullItemReloadAjax();" />
                        <h:commandButton id="lnkAdvancedSearchStartSearch" styleClass="free_area1_p8 activeButton" value="#{lbl.adv_search_btStart}" action="#{AdvancedSearchBean.startSearch}" onclick="fullItemReloadAjax();" />
                    </div>
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>

        <ui:include src="footer/Footer.jspf" />

        <script type="text/javascript">
			var suggestConeUrl = "#{ConeSessionBean.suggestConeUrl}";

            var identifierSuggestURL = suggestConeUrl + '$1/query?lang=en';
            var journalSuggestTrigger = 'JOURNAL';
	  	    var journalSuggestURL = suggestConeUrl + 'journals/query';
            var languageDetailsBaseURL = '$1?format=json&amp;lang=$2';
			var languageSuggestURL = suggestConeUrl + 'iso639-3/query';
            var organizationSuggestURL = 'OrganizationSuggest.jsp';
            var personDetailsBaseURL = '$1?format=json&amp;lang=$2';
			var personSuggestURL = suggestConeUrl + 'persons/query?lang=*';
			var subjectSuggestURL = suggestConeUrl + '$1/query?lang=en';

            $(document).ready(function() {
                checkUpdatePersonFunction();
            });

            function checkUpdatePersonFunction() {
                (typeof updatePersonUi == 'function') ? updatePersonUi(): setTimeout("checkUpdatePersonFunction()", 30);
            }
        </script>
    </f:view>
</body>

</html>
