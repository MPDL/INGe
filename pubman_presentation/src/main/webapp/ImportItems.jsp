<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">

<body lang="${InternationalizationHelper.locale}">
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <ui:repeat var="item" value="#{ImportItems.import.items}" varStatus="status">
            <h:panelGroup>
                <div class="full_area0" style="margin-bottom: 0.19em;">
                    <div class="medium_area0_p8 state noPaddingTopBottom" style="margin-left: 2.28em;">
                        <h:outputText value="#{item.status}" />
                    </div>
                    <div class="huge_area0_p8 noPaddingTopBottom" style="margin-left: 0.36em; margin-right: 0.19em">
                        <h:outputText value="#{item.localizedMessage}" rendered="#{item.itemId == null}" />
                        <h:outputLink id="lnkItem" value="#{item.link}" rendered="#{item.itemId != null}">
                            <h:outputText value="#{item.localizedMessage}" />&#160;
                        </h:outputLink>
                    </div>
                    <div class="large_area0_p8 noPaddingTopBottom">
                        <h:outputText value="#{item.startDateFormatted}" /> &#160;
                    </div>
                    <div class="large_area0_p8 noPaddingTopBottom">
                        <h:outputText value="#{item.endDateFormatted}" /> &#160;
                    </div>
                    <div class="large_area0_p8 noPaddingTopBottom">
                        <h:inputHidden id="inpItemDetailsLink" value="#{item.detailsLink}" />
                        <a href="#" onclick="showDialog($(this).siblings('input').val());return false;">
							Details </a>
                    </div>
                    <div class="large_area0_p8 endline noPaddingTopBottom">
                        <h:outputText value="#{item.errorLevel}" /> &#160;
                    </div>
                </div>
            </h:panelGroup>
        </ui:repeat>
    </f:view>
</body>

</html>