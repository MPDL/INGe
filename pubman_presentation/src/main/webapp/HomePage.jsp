<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <link rel="sword" type="application/xml" title="Sword Servicedocument Location" href="${ApplicationBean.pubmanInstanceUrl}/pubman/faces/sword-app/servicedocument" />
    <meta name="description" content="${lbl.Pubman_descriptionMetaTag}"></meta>
    <ui:include src="header/ui/StandardImports.jspf" />
    <ui:include src="home/HomePageFeedLinks.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{HomePage.init}" />
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <div class="full wrapper">
            <h:inputHidden id="offset"></h:inputHidden>
            <!-- import header -->
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
                                    <h:outputText value="#{lbl.HomePage}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <div class="small_marginLIncl subHeaderSection">
                            <div class="contentMenu">
                                <!-- content menu starts here -->
                                <div class="free_area0 sub">
                                    <!-- content menu upper line starts here -->
                                    &#160;
                                    <!-- content menu upper line ends here -->
                                </div>
                                <!-- content menu ends here -->
                            </div>
                            <div class="subHeader">
                                <!-- Subheadline starts here -->
                                <h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.numberOfMessages == 1}" />
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{HomePage.hasErrorMessages and HomePage.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.hasMessages}" />
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{HomePage.hasMessages and !HomePage.hasErrorMessages and HomePage.numberOfMessages != 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{HomePage.hasMessages}" />
                                </h:panelGroup>
                                <h:outputText value="&#160;" rendered="#{!HomePage.hasErrorMessages}" />
                                <!-- Subheadline ends here -->
                            </div>
                        </div>
                    </div>
                    <div class="full_area0">
                        <div class="full_area0 infoPage">
                            <!-- Main Content -->
                            <h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{!PubManSessionBean.loggedIn and InternationalizationHelper.homeContent!=null}">
                                <h:outputText value="#{InternationalizationHelper.homeContent}" escape="false" />
                            </h:panelGroup>
                            <h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{!PubManSessionBean.loggedIn and InternationalizationHelper.homeContent==null}">
                                <ui:include src="home/StartPageLoggedOut.jspf" />
                            </h:panelGroup>
                            <h:panelGroup styleClass="half_area0_p8 mainSection" rendered="#{PubManSessionBean.loggedIn}">
                                <ui:include src="home/StartPageLoggedIn.jspf" />
                            </h:panelGroup>
                            <!-- Side Panels -->
                            <h:panelGroup styleClass="sideSectionArea">
                                <h:panelGroup styleClass="free_area0_p8 sideSection">
                                    <ui:include src="home/LastReleased.jspf" />
                                    <h:panelGroup rendered="#{ApplicationBean.pubmanBlogFeedUrl != ''}">
                                        <ui:include src="home/BlogIntegration.jspf" />
                                    </h:panelGroup>
                                    <h:panelGroup>
                                        <div id="searchCloudDiv">&#160;</div>
                                    </h:panelGroup>
                                </h:panelGroup>
                            </h:panelGroup>
                        </div>
                    </div>
                </div>
            </h:form>
            <!-- end: content section -->
            <ui:include src="footer/Footer.jspf" />
            <script type="text/javascript">
                $("input[id$='offset']").submit(function() {
                    $(this).val($(window).scrollTop());
                });
                $(document).ready(function() {
                    $(window).scrollTop($("input[id$='offset']").val());
                    $(window).scroll(function() {
                        $("input[id$='offset']").val($(window).scrollTop())
                    });
                });
            </script>
        </div>
        <!-- end: full wrapper -->
    </f:view>
</body>

</html>