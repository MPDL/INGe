<!DOCTYPE html>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->




	 

	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
			<f:loadBundle var="genre" basename="#{EasySubmissionSessionBean.genreBundle}"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<ui:include src="header/ui/StandardImports.jspf" />

			</h:head>
			<body lang="${InternationalizationHelper.locale}">
			<h:outputText value="#{FetchMetadataPage.beanName}" styleClass="noDisplay" />
			<h:form id="formFetchMd" onsubmit="fullItemReload();">
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<ui:include src="header/Header.jspf" />   
				
				<div class="clear">
                    <div class="headerSection xSmall_marginRExcl">
                        <ui:include src="header/Breadcrumb.jspf" />
                    </div>
                </div>   
                
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visually belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<ui:include src="./easySubmission/EasySubmission.jspf" />
				<!-- end: content section -->
				</div>
				
			</div>
			<ui:include src="footer/Footer.jspf" />
			</h:form>
			<script type="text/javascript">
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
				});
				languageSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />iso639-1/query?format=json';
				journalSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}" />journals/query?format=json';
				subjectSuggestURL = '<h:outputText value="#{EasySubmission.suggestConeUrl}"/>$1/query';
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
				    $('*').attr('readonly', true);
				    $(':input : file').attr('disabled', true);
				}								
			</script>
			</body>
		</html>
	</f:view>

