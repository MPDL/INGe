    <f:view locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		
		[
			<ui:repeat var="ou" value="#{OrganizationSuggest.results}" varStatus="index">
				<h:panelGroup>
					{
						"id" : "<h:outputText value="#{ou.identifier}" escape="false"/>",
						"value" : "<h:outputText value="#{ou.name}" escape="false"/>",
						"address" : "<h:outputText value="#{ou.address}" escape="false"/>"
					}
					<h:panelGroup rendered="#{index.index lt OrganizationSuggest.results.size()-1}">,</h:panelGroup>
				</h:panelGroup>
			</ui:repeat>
		]
		
	</f:view>