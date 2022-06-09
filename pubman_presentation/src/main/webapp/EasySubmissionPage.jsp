<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
    <script src="/cone/js/jquery.suggest.js"></script>
	<h:outputScript name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
    <link rel="stylesheet" href="./resources/cc_license_style.css" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <f:loadBundle var="genre" basename="#{EasySubmissionSessionBean.genreBundle}" />
        <div class="full wrapper">
            <h:inputHidden id="offset"></h:inputHidden>
            <ui:include src="header/Header.jspf" />
            <h:form id="form1">
                <div class="clear">
                    <div class="headerSection xSmall_marginRExcl">
                        <ui:include src="header/Breadcrumb.jspf" />
                    </div>
                </div>
                <div id="content" class="full_area0 clear">
                    <!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
                    <ui:include src="./easySubmission/EasySubmission.jspf" />
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>
        
        <ui:include src="footer/Footer.jspf" />
        
		<script type="text/javascript">
	      var suggestConeUrl = "#{ConeSessionBean.suggestConeUrl}";
	      
	      var organizationSuggestURL = 'OrganizationSuggest.jsp';
	      var personDetailsBaseURL = '$1?format=json&amp;mode=full&amp;lang=$2';
	      var personSuggestURL = suggestConeUrl + 'persons/query?lang=*';
	      
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