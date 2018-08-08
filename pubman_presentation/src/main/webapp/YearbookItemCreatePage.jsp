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
                                    <h:outputText value="#{lbl.YearbookItemCreatePage}" />
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
                                    <h:panelGroup id="txtMenuYearbookWorkspace" rendered="#{LoginHelper.isYearbookEditor || LoginHelper.isYearbookAdmin}">
                                        <h:outputText styleClass="seperator void" />
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuYearbookWorkspace}" />
                                    </h:panelGroup>
                                    <h:outputText styleClass="seperator void" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}" />
                                    <h:outputLink id="lnkMenuReportWorkspace" title="#{tip.chooseWorkspace_ReportWorkspace}" value="#{ApplicationBean.appContext}ReportWorkspacePage.jsp" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}">
                                        <h:outputText value="#{lbl.chooseWorkspace_optMenuReportWorkspace}" />
                                    </h:outputLink>
                                </h:panelGroup>
                                <!-- content menu ends here -->
                            </h:panelGroup>
                            <div class="subHeader">
                                <!-- Subheadline starts here -->
                                <h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.numberOfMessages == 1}" />
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{YearbookItemCreateBean.hasErrorMessages and YearbookItemCreateBean.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.hasMessages}" />
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{YearbookItemCreateBean.hasMessages and !YearbookItemCreateBean.hasErrorMessages and YearbookItemCreateBean.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.hasMessages}" />
                                </h:panelGroup>
                                <!-- Subheadline ends here -->
                            </div>
                        </div>
                    </div>
                    <div class="full_area0">
                        <div id="fullItem" class="full_area0 fullItem">
                            <ui:include src="yearbook/CreateYearbookItem.jspf" />
                        </div>
                        <div class="full_area0 formButtonArea">
                        	<h:outputLink styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" id="lnkCancel" value="YearbookModeratorPage.jsp" onclick="fullItemReloadAjax();" >
                        		<h:outputText value="Cancel"/>
                        	</h:outputLink>
                            <h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSave" value="Save" action="#{YearbookItemCreateBean.save}" onclick="fullItemReloadAjax();"/>
                        </div>
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
                }); $(document).ready(
                    function() {
                        $(window).scrollTop($("input[id$='offset']").val());
                        $(window).scroll(
                            function() {
                                $("input[id$='offset']").val(
                                    $(window).scrollTop());
                            });
                        var element = document
                            .getElementById('selSelectedOrgUnit');
                        if (element && element.options != null &&
                            element.options.length == 2) {
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