<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://xmlns.jcp.org/jsf/core"
	xmlns:h="http://xmlns.jcp.org/jsf/html"
	xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
	xmlns:p="http://primefaces.org/ui"
	xmlns:pt="http://xmlns.jcp.org/jsf/passthrough"
	xmlns:os="http://a9.com/-/spec/opensearch/1.1/">

<body lang="${InternationalizationHelper.locale}">

	<f:view locale="#{InternationalizationHelper.userLocale}">

		<f:loadBundle var="lbl" basename="Label" />
		<f:loadBundle var="msg" basename="Messages" />
		<f:loadBundle var="tip" basename="Tooltip" />

		<h:outputText escape="false"
			value='&lt;?xml version="1.0" encoding="UTF-8" ?&gt;' />

		<os:OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
			<os:ShortName>
				<h:outputText value="#{lbl.openSearch_shortDesc} #{Header.type}" />
			</os:ShortName>
			<os:Description>
				<h:outputText value="#{lbl.openSearch_desc} #{lbl.openSearch_shortDesc} #{Header.type}" />.</os:Description>
			<os:Tags>
				<h:outputText value="#{lbl.openSearch_shortDesc}" />
			</os:Tags>
			<os:Contact>escidoc-dev-ext@gwdg.de</os:Contact>
			<h:outputText escape="false"
				value='&lt;Url type="text/html" template="' />
			<h:outputText
				value='#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}#{Search.openSearchRequest}' />
			<h:outputText escape="false" value='" /&gt;' />
			<h:Image height="32" width="32" type="image/png">
				<h:outputText
					value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}javax.faces.resources/pubman_favicon_32_32.png" />
			</h:Image>
		</os:OpenSearchDescription>
	</f:view>

</body>

</html>