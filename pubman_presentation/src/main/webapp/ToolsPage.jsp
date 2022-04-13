<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{ToolsPage.init}" />
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
                                    <h:outputText value="#{lbl.Tools_lblTools}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <div class="small_marginLIncl subHeaderSection">
                            <div class="contentMenu">
                                <!-- content menu starts here -->
                                <div class="free_area0 sub">
                                    <!-- content menu upper line starts here -->
                                    <h:outputLink id="lnkMenuAdminTool" styleClass="free_area0" value="#{ApplicationBean.pubmanInstanceUrl}/admintool" target="_blank" rel="noreferrer noopener" rendered="#{LoginHelper.isAdmin or LoginHelper.isLocalAdmin}">
                                        <h:outputText value="#{lbl.Tools_lblAdminTool}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="seperator void" rendered="#{LoginHelper.isAdmin or LoginHelper.isLocalAdmin}"/>
                                    <h:outputLink id="lnkMenuCoNE" styleClass="free_area0" value="#{ApplicationBean.pubmanInstanceUrl}/cone/" target="_blank" rel="noreferrer noopener">
                                        <h:outputText value="#{lbl.Tools_lblCoNE}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="seperator void" />
                                    <h:outputLink id="lnkMenuREST" styleClass="free_area0" value="#{ApplicationBean.pubmanInstanceUrl}/pubman/faces/SearchAndExportPage.jsp">
                                        <h:outputText value="#{lbl.Tools_lblREST}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="seperator void" />
                                    <h:outputLink id="lnkMenuCslEditor" styleClass="free_area0" value="#{ApplicationBean.cslEditorInstanceUrl}" target="_blank" rel="noreferrer noopener">
                                        <h:outputText value="#{lbl.Tools_lblCslEditorInstance}" />
                                    </h:outputLink>
                                    <h:outputText styleClass="seperator void" />
                                </div>
                                <!-- content menu upper line ends here -->
                            </div>
                        </div>
                        <div class="full_area0">
                            <div class="full_area0 fullItem">
                                <div class="full_area0 small_marginLExcl">
                                    <!-- Subheadline starts here -->
                                    <h3>
                                        <h:outputText value="#{msg.toolsOverview}" />
                                    </h3>
                                    <!-- Subheadline ends here -->
                                </div>
                                <ui:include src="tools/Tools.jspf" />
                            </div>
                        </div>
                    </div>
                </div>
            </h:form>
            <!-- end: content section -->
        </div>
        <ui:include src="footer/Footer.jspf" />
        <script type="text/javascript">
            $("input[id$='offset']").on('submit',function() {
                $(this).val($(window).scrollTop());
            });
            $(document).ready(function() {
                $(window).scrollTop($("input[id$='offset']").val());
                $(window).on('scroll',function() {
                    $("input[id$='offset']").val($(window).scrollTop());
                });
            });
        </script>
    </f:view>
</body>

</html>