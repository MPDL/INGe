<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ApplicationBean.appTitle}" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{GFZSendOAMailPage.init}" />
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
		<div class="full wrapper">
            <h:inputHidden id="offset"></h:inputHidden>
			<ui:include src="header/Header.jspf" />
			<h:form>
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							<ui:include src="header/Breadcrumb.jspf" />
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.actionMenu_sendOAMail}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">
							<div class="contentMenu">
							<!-- content menu starts here -->
								<div class="free_area0 sub">
								<!-- content menu lower line starts here -->
									&#160;
								<!-- content menu lower line ends here -->
								</div>
							<!-- content menu ends here -->
							</div>
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{GFZSendOAMailPage.numberOfMessages == 1}"/>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{GFZSendOAMailPage.hasErrorMessages and GFZSendOAMailPage.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{GFZSendOAMailPage.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{GFZSendOAMailPage.hasMessages and !GFZSendOAMailPage.hasErrorMessages and GFZSendOAMailPage.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{GFZSendOAMailPage.hasMessages}"/>
								</h:panelGroup>	
								&#160;
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					
					<div class="full_area0">
						<div class="full_area0 fullItem">
							
							<div class="full_area0 itemBlock noTopBorder">
								<h3 class="xLarge_area0_p8 endline blockHeader">
									&#160;
								</h3>
								<span class="seperator">&#160;</span>
								<span class="free_area0_p8 endline itemHeadline">
									&#160;
								</span>
								<h3 class="xLarge_area0_p8 endline blockHeader">
									&#160;
								</h3>

								<h:panelGroup rendered="#{GFZSendOAMailPage.hasGFZAuthor}" styleClass="free_area0 itemBlockContent endline">
									<div class="free_area0 endline itemLine noTopBorder">
										<span class="quad_area0 xTiny_marginLExcl endline">
											<h:outputText styleClass="quad_txtInput" value="#{lbl.oaMail_message}" />
										</span>
									</div>
									<div class="free_area0 endline itemLine noTopBorder">
										<span class="quad_area0 xTiny_marginLExcl endline">
											<h:outputLabel id="lblToRecipient" styleClass="quad_label" value="#{lbl.ExportEmail_lblToRecipient}" />
											<h:inputText id="inpEmailRecipients" styleClass="quad_txtInput" style="background-color:#BAD2E4;" value="#{GFZSendOAMailPage.firstGFZAuthorMailAdress}"/>
										</span>
									</div>
									<div class="free_area0 endline itemLine noTopBorder">
										<span class="quad_area0 xTiny_marginLExcl endline">
											<h:outputLabel id="lblSubject" styleClass="quad_label" value="#{lbl.ExportEmail_lblSubject}" />
											<h:outputText styleClass="quad_area0" style="background-color:#BAD2E4;" value="#{GFZSendOAMailPage.emailSubject}" />
										</span>
									</div>
									<div class="free_area0 endline itemLine noTopBorder">
										<span class="quad_area0 xTiny_marginLExcl endline">
											<h:outputLabel id="lblText" styleClass="quad_label" value="#{lbl.oaMail_TextLabel}" />
											<h:outputText styleClass="quad_area0" style="background-color:#BAD2E4;" escape="false" value="#{GFZSendOAMailPage.emailText}" />
										</span>
									</div>
									<div class="free_area0 endline itemLine noTopBorder">
										<span class="quad_area0 xTiny_marginLExcl endline">
											<h:outputLabel id="lblReturnToRecipient" styleClass="quad_label" value="#{lbl.ExportEmail_lblReturnToRecipient}" />
											<h:outputText id="inpExportEmailReplyToAddr" style="background-color:#BAD2E4;" styleClass="quad_txtInput" value="#{GFZSendOAMailPage.replyToAddr}" />
										</span>
									</div>
									<div class="free_area0 endline itemLine noTopBorder">
										&#160;<br />&#160;
									</div>
								</h:panelGroup>
								
								<h:panelGroup rendered="#{!GFZSendOAMailPage.hasGFZAuthor}" styleClass="free_area0 itemBlockContent endline">
									<h:outputText value="#{msg.oaMail_noGFZAuthors}"/>
								</h:panelGroup>
							</div>
						</div>
					</div>

					<div class="full_area0 formButtonArea">
						<h:outputLink styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" id="lnkCancel" value="#{ApplicationBean.appContext}#{GFZSendOAMailPage.previousPageURI}"><h:outputText value="#{lbl.ExportEmail_lblBack}"/></h:outputLink>
						<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSave" value="#{lbl.ExportEmail_lblSend}" action="#{GFZSendOAMailPage.sendEMail}" onclick="fullItemReloadAjax();" rendered="#{GFZSendOAMailPage.hasGFZAuthor}"/>
					</div>
				<!-- end: content section -->
				</div>
			</h:form>
        </div>
        <ui:include src="footer/Footer.jspf" />
        <script type="text/javascript">
            $("input[id$='offset']").submit(function() {
                $(this).val($(window).scrollTop());
            });
            $(document).ready(function() {
                $(window).scrollTop($("input[id$='offset']").val());
                $(window).scroll(function() {
                    $("input[id$='offset']").val($(window).scrollTop());
                });
            });
        </script>
    </f:view>
</body>

</html>