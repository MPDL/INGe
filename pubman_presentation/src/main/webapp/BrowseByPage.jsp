<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{BrowseByPage.init}" />
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <div class="full wrapper">
            <h:inputHidden id="offset"></h:inputHidden>
            <ui:include src="header/Header.jspf" />
            <h:form onsubmit="fullItemReload();">
                <div id="content" class="full_area0 clear">
                    <!-- begin: content section (including elements that visually belong to the header (breadcrumb, headline, subheader and content menu)) -->
                    <div class="clear">
                        <div class="headerSection">
                            <ui:include src="header/Breadcrumb.jspf" />
                            <div id="contentSkipLinkAnchor" class="clear headLine">
                                <!-- Headline starts here -->
                                <h1>
                                    <h:outputText value="#{lbl.BrowseByCreator}" rendered="#{BrowseByPage.selectedValue == 'persons'}" />
                                    <h:outputText value="#{lbl.BrowseByYear}" rendered="#{BrowseByPage.selectedValue == 'year'}" />
                                    <h:outputText value="#{lbl.BrowseByPage} #{BrowseByPage.selectedValue}" rendered="#{(BrowseByPage.selectedValue != 'persons') and (BrowseByPage.selectedValue != 'year') }" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                    </div>
                    <div class="full_area0">
                        <div class="full_area0 ">
                            <div id="ImgFullItem">
                                <div id="ImgFullItemLoad" class="noDisplay" style="position: fixed;">&#160;</div>
                            </div>
                            <ui:include src="browseBy/BrowseByPage.jspf" />
                        </div>
                    </div>
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>
        
        <ui:include src="footer/Footer.jspf" />
        
        <script type="text/javascript">
            function fullItemReload() {
                $(document).ready(function() {
                    $("#fullItem a").on('click',function(event) {
                        event.preventDefault();
                    });
                });
                
                document.getElementById('fullItem').style.opacity = '0.4';
                document.getElementById('fullItem').style.bg = 'FFF';
                document.getElementById('ImgFullItemLoad').setAttribute('class', 'big_imgArea half_marginLIncl smallThrobber');
            }
        </script>
    </f:view>
</body>

</html>