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
                    <!-- begin: content section (including elements that visually belong to the header (breadcrumb, headline, subheader and content menu)) -->
                    <div class="clear">
                        <div class="headerSection">
                            <ui:include src="header/Breadcrumb.jspf" />
                            <div id="contentSkipLinkAnchor" class="clear headLine">
                                <!-- Headline starts here -->
                                <h1>
                                    <h:outputText value="#{lbl.WithdrawItemPage}" />
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
                            <h:panelGroup id="messages" styleClass="subHeader">
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea" rendered="#{WithdrawItemPage.hasErrorMessages}">
                                    <input type="button" class="min_imgBtn fixErrorMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" escape="false" rendered="#{WithdrawItemPage.hasMessages}" />
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea" rendered="#{WithdrawItemPage.hasMessages and !WithdrawItemPage.hasErrorMessages}">
                                    <input type="button" class="min_imgBtn fixSuccessMessageBlockBtn" onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" escape="false" rendered="#{WithdrawItemPage.hasMessages}" />
                                </h:panelGroup>
                            </h:panelGroup>
                            <div class="subHeader">
                                <!-- Subheadline starts here -->
                                <h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" escape="false" rendered="#{WithdrawItem.numberOfMessages == 1}" />
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{WithdrawItem.hasErrorMessages and WithdrawItem.numberOfMessages > 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.warning_lblMessageHeader}" />
                                    </h2>
                                    <h:messages id="txtWithdrawItemPageWarn" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" escape="false" rendered="#{WithdrawItem.hasMessages}" />
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{WithdrawItem.hasMessages and !WithdrawItem.hasErrorMessages and WithdrawItem.numberOfMessages > 1}">
                                    <h2>
                                        <h:outputText value="#{lbl.info_lblMessageHeader}" />
                                    </h2>
                                    <h:messages id="txtWithdrawItemPageInfo" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" escape="false" rendered="#{WithdrawItem.hasMessages}" />
                                </h:panelGroup>
                                &#160;
                                <!-- Subheadline ends here -->
                            </div>
                        </div>
                    </div>
                    <div class="full_area0">
                        <div class="full_area0 fullItem">
                            <ui:include src="withdrawItem/WithdrawItem.jspf" />
                        </div>
                        <div class="full_area0 formButtonArea">
                            <h:commandLink styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" id="lnkCancel" value="#{lbl.cancel}" action="#{WithdrawItem.cancel}" onclick="fullItemReloadAjax();" />
                            <h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSave" value="#{lbl.WithdrawItem_lnkWithdraw}" action="#{WithdrawItem.withdraw}" onclick="fullItemReloadAjax();" />
                        </div>
                    </div>
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>
        
        <ui:include src="footer/Footer.jspf" />
    </f:view>
</body>

</html>