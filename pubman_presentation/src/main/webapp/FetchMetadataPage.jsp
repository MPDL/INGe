<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{FetchMetadataPage.init}" />
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <f:loadBundle var="genre" basename="#{EasySubmissionSessionBean.genreBundle}" />
        <div class="full wrapper">
            <h:inputHidden id="offset"></h:inputHidden>
            <ui:include src="header/Header.jspf" />
            <h:form id="formFetchMd" onsubmit="fullItemReload();">
                <div class="clear">
                    <div class="headerSection xSmall_marginRExcl">
                        <ui:include src="header/Breadcrumb.jspf" />
                    </div>
                </div>
                <div id="content" class="full_area0 clear">
                    <!-- begin: content section (including elements that visually belong to the header (breadcrumb, headline, subheader and content menu)) -->
                    <ui:include src="./easySubmission/EasySubmission.jspf" />
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>
        
        <ui:include src="footer/Footer.jspf" />
        
        <script type="text/javascript">
            function fullItemReload() {
                document.getElementById('fullItem').style.opacity = '0.4';
                document.getElementById('fullItem').style.bg = 'FFF';
                document.getElementById('ImgFullItemLoad').setAttribute(
                    'class', 'big_imgArea half_marginLIncl smallThrobber');
                $('*').attr('readonly', true);
                $(':input :file').attr('disabled', true);
            }
        </script>
    </f:view>
</body>

</html>