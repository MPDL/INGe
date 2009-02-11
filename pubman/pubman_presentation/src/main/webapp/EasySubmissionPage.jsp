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
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<jsp:directive.include file="header/Header.jspf" />
				
				<div class="clear">
                    <div class="headerSection">
                            
                        <jsp:directive.include file="header/Breadcrumb.jspf" />
                    </div>
                 </div>       

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->

					<jsp:directive.include file="./easySubmission/EasySubmission.jspf"/>

				<!-- end: content section -->
				</div>
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
				languageSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/languages/query';
				journalSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/journals/query';
				subjectSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/ddc/query';
				personSuggestURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/persons/query';
				journalDetailsBaseURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />jquery/jnar/details?id=';
				personDetailsBaseURL = '<h:outputText value="#{EditItem.suggestConeUrl}"/>jquery/persons/details?id=';
				journalSuggestCommonParentClass = 'itemBlock';
				personSuggestCommonParentClass = 'itemBlock';
				journalSuggestTrigger = 'JOURNAL';
				bindSuggests();
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>
