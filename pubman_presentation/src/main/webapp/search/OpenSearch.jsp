	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<h:outputText escape="false" value='&lt;?xml version="1.0" encoding="UTF-8" ?&gt;' />
		<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
			<ShortName><h:outputText value="#{lbl.openSearch_shortDesc} #{Header.type}"/></ShortName>
			<Description><h:outputText value="#{lbl.openSearch_desc} #{lbl.openSearch_shortDesc} #{Header.type}"/>.</Description>
			<Tags><h:outputText value="#{lbl.openSearch_shortDesc}"/></Tags>
			<h:outputText escape="false" value='&lt;Url type="text/html" template="' />
			<h:outputText value='#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}#{Search.openSearchRequest}' />
			<h:outputText escape="false" value='" /&gt;' />
		</OpenSearchDescription>
	</f:view>
