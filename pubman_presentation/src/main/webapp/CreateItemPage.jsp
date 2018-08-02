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
                                    <h:outputText value="#{lbl.CreateItemPage}" />
                                </h1>
                                <!-- Headline ends here -->
                            </div>
                        </div>
                        <div class="small_marginLIncl subHeaderSection">
                            <div class="contentMenu">
                                <!-- content menu starts here -->
                                <div class="free_area0 sub">
                                    <!-- content menu lower line starts here -->
                                    <h:commandLink id="lnkNewEasySubmission" title="#{tip.submission_lnkEasySubmission}" action="#{EasySubmission.newEasySubmission}" onclick="fullItemReloadAjax();">
                                        <h:outputText value="#{lbl.submission_lnkEasySubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
                                    </h:commandLink>
                                    <h:outputText styleClass="seperator void" />
                                    <h:commandLink id="lnkNewSubmission" title="#{tip.submission_lnkNewSubmission}" rendered="#{CreateItem.multiple}" action="#{CreateItem.newSubmission}" immediate="true" onclick="fullItemReloadAjax();">
                                        <h:outputText value="#{lbl.submission_lnkNewSubmission}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
                                    </h:commandLink>
                                    <span> <h:outputText
											value="#{lbl.submission_lnkNewSubmission}"
											rendered="#{!CreateItem.multiple and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</span>
                                    <h:outputText styleClass="seperator void" />
                                    <h:commandLink id="lnkNewImport" title="#{tip.submission_lnkImport}" action="#{EasySubmission.newImport}" onclick="fullItemReloadAjax();">
                                        <h:outputText value="#{lbl.submission_lnkImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
                                    </h:commandLink>
                                    <h:outputText styleClass="seperator void" />
                                    <h:commandLink id="lnkNewMultipleImport" title="#{tip.submission_lnkMultipleImport}" rendered="#{!CreateItem.multiple}" action="#{MultipleImport.newImport}" onclick="fullItemReloadAjax();">
                                        <h:outputText value="#{lbl.submission_lnkMultipleImport}" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
                                    </h:commandLink>
                                    <span> <h:outputText
											value="#{lbl.submission_lnkMultipleImport}"
											rendered="#{CreateItem.multiple and DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</span>
                                </div>
                                <!-- content menu lower line ends here -->
                            </div>
                            <!-- content menu ends here -->
                        </div>
                        <!-- end of subHeaderSection -->
                    </div>
                    <!-- end of clear div -->
                    <div class="full_area0">
                        <div class="full_area0 fullItem" id="fullItem">
                            <div class="full_area0 small_marginLExcl">
                                <!-- Subheadline starts here -->
                                <h3>
                                    <h:outputText value="#{msg.create_Item_Select_Collection}" rendered="#{ContextListSessionBean.openContextsAvailable}" />
                                </h3>
                                <h3>
                                    <h:outputText value="#{msg.depositorWE_noOpenContextsAvailable}" rendered="#{!ContextListSessionBean.openContextsAvailable}" />
                                </h3>
                                <!-- Subheadline ends here -->
                            </div>
                            <ui:include src="createItem/CreateItem.jspf" />
                        </div>
                        <div class="full_area0 formButtonArea">
                            <h:outputLink id="lnkBack" styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" value="#{ApplicationBean.appContext}SubmissionPage.jsp">
                                <h:outputText value="#{lbl.easy_submission_btnBack}" />
                            </h:outputLink>
                        </div>
                    </div>
                </div>
                <!-- end: content section -->
            </h:form>
        </div>
        <!-- end of full wrapper -->
        <ui:include src="footer/Footer.jspf" />
        <script type="text/javascript">
            $(document).ready(function() {
                /*
                $("input[id$='offset']").submit(function() {
                	$(this).val($(window).scrollTop());
                });
                 */
                $(window).scrollTop($("input[id$='offset']").val());
                $(window).scroll(function() {
                    $("input[id$='offset']").val($(window).scrollTop());
                });
            });
        </script>
    </f:view>
</body>

</html>