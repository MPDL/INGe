    <f:view locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		[
			<ui:repeat var="identifier" value="#{IdentifierSuggest.results}" varStatus="index">
				<h:panelGroup>
					{
						"value" : "<h:outputText value="#{identifier}" escape="false"/>"
					}
					<h:panelGroup rendered="#{index.index lt IdentifierSuggest.results.size()-1}">,</h:panelGroup>
				</h:panelGroup>
			</ui:repeat>
		]
	</f:view>