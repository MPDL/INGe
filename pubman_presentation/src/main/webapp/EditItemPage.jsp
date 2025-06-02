<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="http://xmlns.jcp.org/jsf/core"
	xmlns:h="http://xmlns.jcp.org/jsf/html"
	xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
	xmlns:p="http://primefaces.org/ui"
	xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
	<title><h:outputText value="#{ApplicationBean.appTitle}" /></title>
	<ui:include src="header/ui/StandardImports.jspf" />
	<script src="/cone/js/jquery.suggest.js"></script>
	<h:outputScript name="commonJavaScript/componentJavaScript/autoSuggestFunctions.js" />
	<link rel="stylesheet" href="./resources/cc_license_style.css" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label" />
		<f:loadBundle var="msg" basename="Messages" />
		<f:loadBundle var="tip" basename="Tooltip" />
		<f:loadBundle var="genre" basename="#{EditItem.genreBundle}" />
		<div class="full wrapper">
			<h:inputHidden value="#{EditItemSessionBean.offset}" id="offset"></h:inputHidden>
			<ui:include src="header/Header.jspf" />
			<h:form id="form1">
				<div id="content" class="full_area0 clear">
					<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							<ui:include src="header/Breadcrumb.jspf" />
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1>
									<h:outputText value="#{lbl.EditItemPage}" />
								</h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
								<!-- content menu starts here -->
								<h:panelGroup layout="block" styleClass="free_area0 sub"
									rendered="#{!((EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'IMPORT') )}">
									<!-- content menu lower line starts here -->
                                    &#160;
                                    <!-- content menu lower line ends here -->
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="free_area0 sub"
									rendered="#{((EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION') || (EditItemSessionBean.currentSubmission  == 'IMPORT') )}">
									<h:commandLink title="#{tip.submission_lnkEasySubmission}"
										action="#{EasySubmission.newEasySubmission}"
										rendered="#{EditItemSessionBean.currentSubmission  != 'EASY_SUBMISSION'}"
										onclick="fullItemReloadAjax();">
										<h:outputText value="#{lbl.submission_lnkEasySubmission}"
											rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:commandLink>
									<h:panelGroup styleClass=" "
										rendered="#{EditItemSessionBean.currentSubmission  == 'EASY_SUBMISSION'}">
										<h:outputText value="#{lbl.submission_lnkEasySubmission}"
											rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:panelGroup>
									<h:outputText styleClass="seperator void" />
									<h:commandLink title="#{tip.submission_lnkNewSubmission}"
										action="#{CreateItem.newSubmission}"
										rendered="#{EditItemSessionBean.currentSubmission  != 'FULL_SUBMISSION'}"
										onclick="fullItemReloadAjax();">
										<h:outputText value="#{lbl.submission_lnkNewSubmission}"
											rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:commandLink>
									<h:panelGroup styleClass=" "
										rendered="#{EditItemSessionBean.currentSubmission  == 'FULL_SUBMISSION'}">
										<h:outputText value="#{lbl.submission_lnkNewSubmission}"
											rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:panelGroup>
									<h:outputText styleClass="seperator void" />
									<h:commandLink title="#{tip.submission_lnkImport}"
										action="#{EasySubmission.newImport}"
										rendered="#{EditItemSessionBean.currentSubmission  != 'IMPORT'}"
										onclick="fullItemReloadAjax();">
										<h:outputText value="#{lbl.submission_lnkImport}"
											rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:commandLink>
									<h:panelGroup styleClass=" "
										rendered="#{EditItemSessionBean.currentSubmission  == 'IMPORT'}">
										<h:outputText value="#{lbl.submission_lnkImport}"
											rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:panelGroup>
									<h:outputText styleClass="seperator void"
										rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									<h:commandLink title="#{tip.submission_lnkMultipleImport}"
										action="#{MultipleImport.newImport}"
										rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}"
										onclick="fullItemReloadAjax();">
										<h:outputText value="#{lbl.submission_lnkMultipleImport}"
											rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									</h:commandLink>
								</h:panelGroup>
							</div>
							<!-- content menu ends here -->
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:outputText
									value="#{lbl.EditItem_lblItemVersionID} '#{EditItem.pubItem.objectId}'."
									rendered="#{EditItem.pubItem.objectId != null}" />
								<br />
								<h:outputText
									value="#{lbl.EditItem_lblCollectionOfItem} '#{EditItem.contextName}'." />
								<br />
								<h:outputText
									value="#{lbl.EditItem_lblItemDepositor} '#{EditItem.owner}'"
									rendered="#{EditItem.owner != null}" />
								<h:outputText value="."
									rendered="#{EditItem.owner != null and EditItem.creationDate == null}" />
								<h:outputText value=" --- #{EditItem.creationDate}"
									rendered="#{EditItem.creationDate != null}" />
								<br />
								<h:outputText
									value="#{lbl.EditItem_lblItemLatestModifier} '#{EditItem.lastModifier}'"
									rendered="#{EditItem.lastModifier != null}" />
								<h:outputText value="."
									rendered="#{EditItem.lastModifier != null and EditItem.lastModificationDate == null}" />
								<h:outputText value=" --- #{EditItem.lastModificationDate}"
									rendered="#{EditItem.lastModificationDate != null}" />
							</div>
							<h:panelGroup id="messages" styleClass="subHeader">
								<h:panelGroup layout="block"
									styleClass="half_area2_p6 messageArea errorMessageArea absoluteMessageArea"
									rendered="#{EditItem.hasErrorMessages}">
									<input type="button" class="min_imgBtn fixErrorMessageBlockBtn"
										onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
									<h2>
										<h:outputText value="#{lbl.warning_lblMessageHeader}" />
									</h2>
									<h:messages errorClass="messageError" warnClass="messageWarn"
										fatalClass="messageFatal" infoClass="messageStatus"
										layout="list" globalOnly="true" showDetail="false"  escape="false"
										showSummary="true" rendered="#{EditItem.hasMessages}" />
								</h:panelGroup>
								<h:panelGroup layout="block"
									styleClass="half_area2_p6 messageArea infoMessageArea absoluteMessageArea"
									rendered="#{EditItem.hasMessages and !EditItem.hasErrorMessages}">
									<input type="button"
										class="min_imgBtn fixSuccessMessageBlockBtn"
										onclick="$(this).parents('.messageArea').removeClass('absoluteMessageArea'); $(this).hide();" />
									<h2>
										<h:outputText value="#{lbl.info_lblMessageHeader}" />
									</h2>
									<h:messages errorClass="messageError" warnClass="messageWarn"
										fatalClass="messageFatal" infoClass="messageStatus"
										layout="list" globalOnly="true" showDetail="false"  escape="false"
										showSummary="true" rendered="#{EditItem.hasMessages}" />
								</h:panelGroup>
							</h:panelGroup>
							<!-- Subheadline ends here -->
						</div>
						<div class="free_area0 xTiny_marginLIncl" style="float: right">
							<h:outputText value="#{msg.mandatoryFieldFS2}" />
						</div>
					</div>
					<div class="full_area0">
						<div id="fullItem" class="full_area0 fullItem">

							<div class="full_area0 fullItemControls">
								<span class="full_area0_p5"> <h:panelGroup
										styleClass="seperator"
										rendered="#{!(genre.sources_display == 'false' and genre.sources_form_id == 'full-submission' or  genre.sources_display == 'false' and genre.sources_form_id == 'all')}" />
									<h:outputLink styleClass="free_area0" value="#form1:editSource"
										rendered="#{!(genre.sources_display == 'false' and genre.sources_form_id == 'full-submission' or  genre.sources_display == 'false' and genre.sources_form_id == 'all')}">
										<h:outputText value="#{genre.sources_source_basic_label}"
											converter="GenreLabelConverter" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{!(genre.legal_case_display == 'false' and genre.legal_case_form_id == 'full-submission' or  genre.legal_case_display == 'false' and genre.legal_case_form_id == 'all')}" />
									<h:outputLink styleClass="free_area0"
										value="#form1:editLegalCase"
										rendered="#{!(genre.legal_case_display == 'false' and genre.legal_case_form_id == 'full-submission' or  genre.legal_case_display == 'false' and genre.legal_case_form_id == 'all')}">
										<h:outputText value="#{genre.legal_case_label}"
											converter="GenreLabelConverter" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{!(genre.events_display == 'false' and genre.events_form_id == 'full-submission' or  genre.events_display == 'false' and genre.events_form_id == 'all')}" />
									<h:outputLink styleClass="free_area0" value="#form1:editEvent"
										rendered="#{!(genre.events_display == 'false' and genre.events_form_id == 'full-submission' or  genre.events_display == 'false' and genre.events_form_id == 'all')}">
										<h:outputText value="#{genre.events_label}"
											converter="GenreLabelConverter" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{!(genre.details_display == 'false' and genre.details_form_id == 'full-submission' or  genre.details_display == 'false' and genre.details_form_id == 'all')}" />
									<h:outputLink styleClass="free_area0" value="#form1:editDetail"
										rendered="#{!(genre.details_display == 'false' and genre.details_form_id == 'full-submission' or  genre.details_display == 'false' and genre.details_form_id == 'all')}">
										<h:outputText value="#{genre.details_label}"
											converter="GenreLabelConverter" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{!(genre.content_display == 'false' and genre.content_form_id == 'full-submission' or  genre.content_display == 'false' and genre.content_form_id == 'all')}" />
									<h:outputLink styleClass="free_area0"
										value="#form1:editContent"
										rendered="#{!(genre.content_display == 'false' and genre.content_form_id == 'full-submission' or  genre.content_display == 'false' and genre.content_form_id == 'all')}">
										<h:outputText value="#{genre.content_label}"
											converter="GenreLabelConverter" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{!(genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'full-submission' or  genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'all')}" />
									<h:outputLink styleClass="free_area0" value="#form1:editPerson"
										rendered="#{!(genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'full-submission' or  genre.creator_person_organization_display == 'false' and genre.creator_person_organization_form_id == 'all')}">
										<h:outputText
											value="#{genre.creator_person_organization_label}"
											converter="GenreLabelConverter" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{!(genre.locators_display == 'false' and genre.locators_form_id == 'full-submission' or  genre.locators_display == 'false' and genre.locators_form_id == 'all')}" />
									<h:outputLink styleClass="free_area0"
										value="#form1:editLocator"
										rendered="#{!(genre.locators_display == 'false' and genre.locators_form_id == 'full-submission' or  genre.locators_display == 'false' and genre.locators_form_id == 'all')}">
										<h:outputText value="#{genre.locators_label}"
											converter="GenreLabelConverter" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{!(genre.files_display == 'false' and genre.files_form_id == 'full-submission' or  genre.files_display == 'false' and genre.files_form_id == 'all')}" />
									<h:outputLink styleClass="free_area0" value="#form1:editFile"
										rendered="#{!(genre.files_display == 'false' and genre.files_form_id == 'full-submission' or  genre.files_display == 'false' and genre.files_form_id == 'all')}">
										<h:outputText value="#{genre.files_label}"
											converter="GenreLabelConverter" />
									</h:outputLink> <h:panelGroup styleClass="seperator"></h:panelGroup>
								</span>
							</div>
							<ui:include src="editItem/BasicGroup.jspf" />
							<ui:include src="editItem/FilesGroup.jspf" />
							<ui:include src="editItem/LocatorsGroup.jspf" />
							<ui:include src="editItem/PersOrgGroup.jspf" />
							<ui:include src="editItem/ContentGroup.jspf" />
							<ui:include src="editItem/DetailGroup.jspf" />
							<ui:include src="editItem/EventGroup.jspf" />
							<!--JUS content section -->
							<ui:include src="editItem/LegalCaseGroup.jspf" />
							<ui:include src="editItem/ProjectInfoGroup.jspf" />
							<ui:include src="editItem/SourceGroup.jspf" />
							<p>&#160;</p>

						</div>
						<div id="ImgFullItem">
							<div id="ImgFullItemLoad" class="noDisplay"
								style="position: fixed;">&#160;</div>
						</div>
						<div class="full_area0 formButtonArea">
							<h:commandLink
								styleClass="free_area1_p8 cancelButton xLarge_marginLIncl"
								id="lnkCancel" value="#{lbl.cancel}" action="#{EditItem.cancel}"
								onclick="fullItemReloadAjax();" />
							<h:commandLink styleClass="free_area1_p8 activeButton"
								id="lnkRelease" binding="#{EditItem.lnkRelease}"
								value="#{lbl.actionMenu_lnkRelease}"
								action="#{EditItem.saveAndRelease}"
								onclick="fullItemReloadAjax();" />
							<h:commandLink styleClass="free_area1_p8 activeButton"
								id="lnkSaveAndSubmit" binding="#{EditItem.lnkSaveAndSubmit}"
								value="#{lbl.EditItem_lnkSaveAndSubmit}"
								action="#{EditItem.saveAndSubmit}"
								onclick="fullItemReloadAjax();" />
							<h:commandLink styleClass="free_area1_p8 activeButton"
								id="lnkSave" binding="#{EditItem.lnkSave}" value="#{lbl.save}"
								action="#{EditItem.save}" onclick="fullItemReloadAjax();" />
							<h:commandLink styleClass="free_area1_p8 activeButton"
								id="lnkValidate" value="#{lbl.EditItem_lnkValidate}"
								action="#{EditItem.validate}" onclick="fullItemReloadAjax();" />
						</div>
					</div>
				</div>
				<!-- end: content section -->
			</h:form>
		</div>
		<!--  end: full wrapper -->

		<ui:include src="footer/Footer.jspf" />

		<script type="text/javascript">
	      var suggestConeUrl = "#{ConeSessionBean.suggestConeUrl}";

	      var fundingOrganizationDetailsBaseURL = '$1?format=json';
	      var fundingOrganizationSuggestURL = suggestConeUrl + 'funding-organizations/query';
	      var fundingProgramDetailsBaseURL = '$1?format=json';
	      var fundingProgramSuggestURL = suggestConeUrl + 'funding-programs/query';
	      var identifierSuggestURL = suggestConeUrl + '$1/query?lang=en';
	      var journalDetailsBaseURL = '$1?format=json';
	      var journalSuggestTrigger = 'JOURNAL';
	      var journalSuggestURL = suggestConeUrl + 'journals/query';
	      var languageDetailsBaseURL = '$1?format=json&amp;lang=$2';
	      var languageSuggestURL = suggestConeUrl + 'iso639-3/query';
	      var organizationSuggestURL = 'OrganizationSuggest.jsp';
	      var personDetailsBaseURL = '$1?format=json&amp;mode=full&amp;lang=$2';
	      var personSuggestURL = suggestConeUrl + 'persons/query?lang=*';
	      var subjectSuggestURL = suggestConeUrl + '$1/query?lang=en';

		  $(document).ready(function() {
	        checkUpdatePersonFunction();
	      });

          function checkUpdatePersonFunction() {
	        (typeof updatePersonUi == 'function') ? updatePersonUi(): setTimeout("checkUpdatePersonFunction()", 30);
	      }
	   </script>
	</f:view>
</body>

</html>
