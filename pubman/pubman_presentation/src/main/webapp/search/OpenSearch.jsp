<?xml version="1.0" encoding="UTF-8"?>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">

	<jsp:directive.page contentType="text/xml;charset=UTF-8" pageEncoding="UTF-8" />
	<f:view locale="#{InternationalizationHelper.userLocale}" xmlns:e="http://www.escidoc.de/jsf">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		<h:outputText escape="false" value='&lt;?xml version="1.0" encoding="UTF-8" ?&gt;' />
		<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
			<ShortName><h:outputText value="#{lbl.openSearch_shortDesc} #{Header.type}"/></ShortName>
			<Description><h:outputText value="#{lbl.openSearch_desc} #{lbl.openSearch_shortDesc} #{Header.type}"/>.</Description>
			<Tags><h:outputText value="#{lbl.openSearch_shortDesc}"/></Tags>
			<Contact>escidoc-dev-ext@gwdg.de</Contact>
				<h:outputText escape="false" value='&lt;Url type="text/html" template="' />
				<h:outputText value='#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}' />
				<h:outputText escape="false" value='SearchResultListPage.jsp?cql=%28+%28+escidoc.metadata%3D%22{searchTerms}%22+%29++or++%28+escidoc.any-identifier%3D%22{searchTerms}%22+%29++not++%28+escidoc.context.objid%3D%22{searchTerms}%22+%29++not++%28+escidoc.created-by.objid%3D%22{searchTerms}%22+%29++and++%28+escidoc.objecttype%3D%22item%22+%29+%29+and++%28+escidoc.content-model.objid%3D%22escidoc%3Apersistent4%22+%29+" /&gt;' />
			<Image height="16" width="16" type="image/vnd.microsoft.icon"><h:outputText value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}resources/pubman_favicon.ico" /></Image>
		</OpenSearchDescription>
	</f:view>
</jsp:root>
