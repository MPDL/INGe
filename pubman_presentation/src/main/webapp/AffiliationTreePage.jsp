<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
    <ui:include src="affiliation/OrganizationTreeFeedLinks.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <h:outputText value="#{AffiliationTreePage.necessaryForBeanInitialisation}" styleClass="noDisplay" />
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
                                    <h:outputText value="#{lbl.AffiliationTree_txtHeadline}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <div class="small_marginLIncl subHeaderSection">
                            <div class="contentMenu">
                                <!-- content menu starts here -->
                                <div class="free_area0 sub">
                                    <!-- content menu lower line starts here -->
                                    &#160;
                                    <!-- content menu lower line ends here -->
                                </div>
                                <!-- content menu ends here -->
                            </div>
                            <div class="subHeader">
                                <!-- Subheadline starts here -->
                                &#160;
                                <!-- Subheadline ends here -->
                            </div>
                        </div>
                    </div>
                    <div class="full_area0">
                        <div class="full_area0 fullItem">
                            <ui:include src="/affiliation/AffiliationTree.jspf" />
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
        </script>
    </f:view>
</body>

</html>