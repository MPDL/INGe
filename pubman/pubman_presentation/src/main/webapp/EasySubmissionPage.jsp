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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->


<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:rich="http://richfaces.org/rich" xmlns:a4j="http://richfaces.org/a4j" >

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
				<script type='text/javascript' src='http://api.creativecommons.org/jswidget/tags/0.96/complete.js?locale=#{PubManSessionBean.locale}&amp;want_a_license=definitely'>;</script>
				<link rel="stylesheet" href="http://labs.creativecommons.org/demos/jswidget/tags/0.97/example_web_app/example-widget-style.css" />

			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText value="#{EasySubmissionPage.beanName}" styleClass="noDisplay" />
			<h:form >
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<jsp:directive.include file="header/Header.jspf" />   
				
				<div class="clear">
                    <div class="headerSection xSmall_marginRExcl">
                        <jsp:directive.include file="header/Breadcrumb.jspf" />
                    </div>
                </div>   
                
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<jsp:directive.include file="./easySubmission/EasySubmission.jspf"/>
				<!-- end: content section -->
				</div>
				
			</div>
			<jsp:directive.include file="footer/Footer.jspf" />
			</h:form>
			<script type="text/javascript">
				$pb("input[id$='offset']").submit(function() {
					$pb(this).val($pb(window).scrollTop());
				});
				$pb(document).ready(function () {
					$pb(window).scrollTop($pb("input[id$='offset']").val());
					$pb(window).scroll(function(){$pb("input[id$='offset']").val($pb(window).scrollTop());});
				});
				languageSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />iso639-3/query?format=json';
				journalSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />journals/query?format=json';
				subjectSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}"/>$1/query?lang=en';
				personSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}"/>persons/query?format=json';
				organizationSuggestURL = 'OrganizationSuggest.jsp';
				journalDetailsBaseURL = '$1?format=json';
				personDetailsBaseURL = '$1?format=json';
				languageDetailsBaseURL = '$1?format=json';
				journalSuggestCommonParentClass = 'itemBlock';
				personSuggestCommonParentClass = 'suggestAnchor';
				journalSuggestTrigger = 'JOURNAL';
			</script>
			<script type="text/javascript">
				function fullItemReload()
				{
					document.getElementById('fullItem').style.opacity='0.4';
					document.getElementById('fullItem').style.bg='FFF';
					document.getElementById('ImgFullItemLoad').setAttribute('class','big_imgArea half_marginLIncl smallThrobber');
				    $pb('*').attr('readonly', true);
				    $pb(':input : file').attr('disabled', true);
				}								
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>
