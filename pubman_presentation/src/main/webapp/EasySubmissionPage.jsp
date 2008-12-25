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


<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:tr="http://myfaces.apache.org/trinidad">

	<jsp:output doctype-root-element="html"
	       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" /> 

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
			<f:loadBundle var="genre" basename="#{EasySubmissionSessionBean.genreBundle}"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<jsp:directive.include file="header/ui/StandardImports.jspf" />


			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText value="#{SubmissionPage.beanName}" styleClass="noDisplay" />
			<tr:form usesUpload="true">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<jsp:directive.include file="header/Header.jspf" />

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->

					<jsp:directive.include file="./easySubmission/EasySubmission.jspf"/>

				<!-- end: content section -->
				</div>
			</tr:form>
			<script type="text/javascript">
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
				});
				languageSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/lang/query';
				journalSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/jnar/query';
				journalDetailsBaseURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/jnar/details?id=';
				journalSuggestCommonParentClass = 'itemBlock';
				journalSuggestTrigger = 'JOURNAL';
				bindSuggests();
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>





<!-- 
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:tr="http://myfaces.apache.org/trinidad">

<jsp:output doctype-root-element="html"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
			<html>
				<head>
					<link rel="stylesheet" type="text/css" href="./resources/escidoc-css/css/main.css" />
					<link rel="SHORTCUT ICON" href="./images/escidoc.ico"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					
					<script type="text/javascript" language="JavaScript" src="resources/scripts.js">;</script>
					
					<script type="text/javascript" language="JavaScript" src="../../cone/js/jquery-1.2.6.min.js">;</script>
					<script type="text/javascript" language="JavaScript" src="../../cone/js/jquery.suggest.js">;</script>
					<script type="text/javascript" language="JavaScript" src="../../cone/js/jquery.dimensions.js">;</script>
					<script type="text/javascript" language="JavaScript" src="resources/autoSuggestFunctions.js">;</script>
					
					<link href="../../cone/js/jquery.suggest.css" rel="stylesheet" type="text/css" />

				</head>
				<body lang="#{InternationalizationHelper.locale}">
					<h:outputText id="pageDummy" value="#{EasySubmissionPage.beanName}" style="height: 0px; width: 0px; visibility:hidden; position: absolute" />
					<div id="page_margins">
						<div id="page">
							<tr:form usesUpload="true">
								<div id="header">
									<jsp:directive.include file="../desktop/Header.jspf"/>
									<jsp:directive.include file="../desktop/Login.jspf"/>
									<jsp:directive.include file="../desktop/Search.jspf"/>
								</div>
								<div id="nav">
									<jsp:directive.include file="../desktop/Breadcrumb.jspf"/>
								</div>
								<div id="main">
									<div id="col1">
										<span class="mainMenu">
											<jsp:directive.include file="desktop/Navigation.jspf"/> 
										</span>
									</div>
									<div id="col2">
											&#xa0;
									</div>
									<div id="col3">
										<div class="content">
											<jsp:directive.include file="./easySubmission/EasySubmission.jspf"/>
										</div>
									</div>
								</div>
								<jsp:directive.include file="../desktop/messages.jspf"/>
							</tr:form>
						 </div>
					  </div>
				<script type="text/javascript">
					languageSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/lang/query';
					journalSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/jnar/query';
					journalDetailsBaseURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/jnar/details?id=';
					journalSuggestCommonParentClass = 'easySubmissionBoxBody';
					journalSuggestTrigger = 'JOURNAL';
					bindSuggests();
				</script>
				</body>
			<script type="text/javascript" src="/clickheat/js/clickheat.js"></script><script type="text/javascript">clickHeatPage = 'view_Item';initClickHeat();</script>
			</html>
		
	</f:view>
</jsp:root>
-->