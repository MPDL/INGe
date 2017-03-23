<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">

<body lang="${InternationalizationHelper.locale}">
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <ui:repeat var="ou" value="#{AffiliationTree.affiliationSelectItems}">
            <h:panelGroup>
                { "id" : "
                <h:outputText value="#{ou.value}" escape="false" />", "value" : "
                <h:outputText value="#{ou.label}" escape="false" />", },
            </h:panelGroup>
        </ui:repeat>
    </f:view>
</body>

</html>