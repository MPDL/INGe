    <f:view locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		
		[
			<ui:repeat var="ua" value="#{UserAccountSuggest.userAccountList}" varStatus="index">
				<h:panelGroup>
					{
						"id" : "<h:outputText value="#{ua.objectId}" escape="false"/>",
						"value" : "<h:outputText value="#{ua.name}" escape="false"/>"
					}
					<h:panelGroup rendered="#{index.index lt UserAccountSuggest.userAccountListSize - 1}">,</h:panelGroup>
				</h:panelGroup>
			</ui:repeat>
		]
		
	</f:view>