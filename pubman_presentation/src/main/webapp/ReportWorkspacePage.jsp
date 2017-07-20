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
    <f:event type="preRenderView" listener="#{ReportWorkspacePage.init}" />
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <div class="full wrapper">
            <h:inputHidden id="offset"></h:inputHidden>
            <!-- start: skip link navigation -->
            <h:outputLink styleClass="skipLink" title="skip link" value="#mainMenuSkipLinkAnchor">
                <h:outputText value="Skip to the main menu" />
            </h:outputLink>
            <h:outputLink styleClass="skipLink" title="skip link" value="#contentSkipLinkAnchor">
                <h:outputText value="Skip to the page content" />
            </h:outputLink>
            <h:outputLink styleClass="skipLink" title="skip link" value="#searchMenuSkipLinkAnchor">
                <h:outputText value="Skip to the search menu" />
            </h:outputLink>
            <h:outputLink styleClass="skipLink" title="skip link" value="#metaMenuSkipLinkAnchor">
                <h:outputText value="Skip to the meta menu" />
            </h:outputLink>
            <!-- end: skip link navigation -->
            <ui:include src="header/Header.jspf" />
            <h:form id="formTest">
                <div id="content" class="full_area0 clear">
                    <!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
                    <div class="clear">
                        <div class="headerSection">
                            <ui:include src="header/Breadcrumb.jspf" />
                            <div id="contentSkipLinkAnchor" class="clear headLine">
                                <!-- Headline starts here -->
                                <h1>
                                    <h:outputText value="#{lbl.ReportWorkspacePage}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <div class="small_marginLIncl subHeaderSection">
                            <div class="contentMenu">
                                <!-- content menu starts here -->
                                <h:panelGroup layout="block" styleClass="free_area0 sub">
                                    <h:outputLink id="lnkMenuQAWorkspace" title="#{tip.chooseWorkspace_QAWorkspace}" value="#{ApplicationBean.appContext}QAWSPage.jsp" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}">
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuQAWorkspace}" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="seperator void" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
                                    <h:outputLink id="lnkSubmission_lnkImportWorkspaceMenu" title="#{tip.chooseWorkspace_ImportWorkspace}" value="#{ApplicationBean.appContext}ImportWorkspace.jsp" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuImportWorkspace}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="seperator void" rendered="#{LoginHelper.isYearbookEditor}" />
                                    <h:outputLink id="lnkMenuYearbookWorkspace" title="#{tip.chooseWorkspace_YearbookWorkspace}" value="#{ApplicationBean.appContext}YearbookPage.jsp" rendered="#{LoginHelper.isYearbookEditor}">
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuYearbookWorkspace}" />
                                    </h:outputLink>
                                    <h:panelGroup id="txtMenuReportWorkspace" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}">
                                        <h:outputText styleClass="seperator void" />
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuReportWorkspace}" />
                                    </h:panelGroup>
                                </h:panelGroup>
                                <!-- content menu ends here -->
                            </div>
                            <div class="subHeader">
                                <!-- Subheadline starts here -->
                                <h:outputText value="#{msg.chooseWorkspace}" />
                                <!-- Subheadline ends here -->
                            </div>
                            <div class="subHeader">
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{ReportWorkspaceBean.hasErrorMessages}">
                                    <input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{ReportWorkspaceBean.hasMessages}" />
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea" rendered="#{ReportWorkspaceBean.hasMessages and !ReportWorkspaceBean.hasErrorMessages}">
                                    <input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{ReportWorkspaceBean.hasMessages}" />
                                </h:panelGroup>
                            </div>
                        </div>
                    </div>
                    <div class="full_area0">
                        <div id="reportWorkspace" class="full_area0 fullItem">
                            <ui:include src="workspaces/ReportWorkspace.jspf" />
                        </div>
                        <div id="ImgFullItem">
                            <div id="ImgFullItemLoad" class="noDisplay" style="position: fixed;">&#160;</div>
                        </div>
                        <div class="full_area0 formButtonArea">
                            <h:outputLink id="lnkCancel" styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" value="#{ApplicationBean.appContext}WorkspacesPage.jsp">
                                <h:outputText value="#{lbl.cancel}" />
                            </h:outputLink>
                            <h:commandLink id="lnkGenerateReport" styleClass="free_area1_p8 activeButton" title="#{tip.easy_submission_btnImport}" action="#{ReportWorkspaceBean.generateReport}">
                                <h:outputText value="#{lbl.ReportWorkspace_btnGenereateList}" />
                            </h:commandLink>
                        </div>
                    </div>
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>
        <ui:include src="footer/Footer.jspf" />
        <script type="text/javascript">
            $("input[id$='offset']").submit(function() {
                $(this).val($(window).scrollTop());
            });
            $(document).ready(function() {
                $(window).scrollTop($("input[id$='offset']").val());
                $(window).scroll(function() {
                    $("input[id$='offset']").val($(window).scrollTop());
                });
            });
            organizationSuggestURL = 'OrganizationSuggest.jsp';
        </script>
    </f:view>
</body>

</html>