<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">
<h:head>
    <title>
        <h:outputText value="#{ViewItemFull.pubItem.metadata.title} :: #{ApplicationBean.appTitle}" converter="HTMLTitleSubSupConverter" />
    </title>
    <ui:include src="header/ui/StandardImports.jspf" />
</h:head>

<body lang="${InternationalizationHelper.locale}">
    <f:event type="preRenderView" listener="#{LocalTagsPage.init}" />
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
                                <h1>
                                    <h:outputText value="#{lbl.ViewItemPage}" />
                                </h1>
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
                                &#160;
                                <!-- Subheadline ends here -->
                            </div>
                        </div>
                    </div>
                    <div class="full_area0">
                        <div class="full_area0 fullItem">
                            <div class="full_area0 fullItemControls">
                                <span class="full_area0_p5 underRework"> <b
									class="free_area0 small_marginLExcl">&#160;<h:outputText
											styleClass="messageError"
											value="#{msg.ViewItemFull_withdrawn}"
											rendered="#{ViewItemFull.isStateWithdrawn}" /></b> <h:panelGroup
										styleClass="seperator" /> <h:outputLink
										id="lnkContentSkipLinkAnchor" styleClass="free_area0 actual"
										value="#contentSkipLinkAnchor">
										<h:outputText
											value="#{lbl.ViewItemFull_lblSubHeaderLocalTags}" />
									</h:outputLink> <h:panelGroup styleClass="seperator"
										rendered="#{ViewItemFull.isLatestVersion and !ViewItemFull.isStateWithdrawn and ViewItemFull.isLoggedIn and (ViewItemFull.isDepositor || ViewItemFull.isModerator)}" />
									<h:commandLink id="lnkShowItemLog" styleClass="free_area0"
										action="#{ViewItemFull.showItemLog}"
										rendered="#{ViewItemFull.isLatestVersion and !ViewItemFull.isStateWithdrawn and ViewItemFull.isLoggedIn and (ViewItemFull.isDepositor || ViewItemFull.isModerator)}">
										<h:outputText value="#{lbl.ViewItemLogPage}" />
									</h:commandLink> <h:panelGroup styleClass="seperator"
										rendered="#{ViewItemFull.isLatestRelease and !ViewItemFull.isStateWithdrawn}" />
									<h:panelGroup styleClass="seperator"
										rendered="#{(!ViewItemFull.isStateWithdrawn and ViewItemFull.isLatestRelease) || (ViewItemFull.isStateWithdrawn and ViewItemFull.pubItem.versionNumber > 1) }" />
									<h:commandLink id="lnkShowReleaseHistory"
										styleClass="free_area0"
										action="#{ViewItemFull.showReleaseHistory}"
										rendered="#{(!ViewItemFull.isStateWithdrawn and ViewItemFull.isLatestRelease) || (ViewItemFull.isStateWithdrawn and ViewItemFull.pubItem.versionNumber > 1) }">
										<h:outputText value="#{lbl.ViewItemFull_btnItemVersions}" />
									</h:commandLink> <h:panelGroup styleClass="seperator" /> <h:outputLink
										id="lnkViewItemPage" styleClass="free_area0"
										value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}ViewItemFullPage.jsp?itemId=#{ViewItemFull.pubItem.objectIdAndVersion}">
										<h:outputText value="#{lbl.ViewItemFull_btnItemView}" />
									</h:outputLink> <h:panelGroup styleClass="seperator" /> <h:outputLink
										id="lnkViewItemOverviewPage" styleClass="free_area0"
										value="#{ApplicationBean.pubmanInstanceUrl}#{ApplicationBean.appContext}ViewItemOverviewPage.jsp?itemId=#{ViewItemFull.pubItem.objectIdAndVersion}">
										<h:outputText
											value="#{lbl.ViewItemOverview_lblLinkOverviewPage}" />
									</h:outputLink> <h:panelGroup styleClass="seperator" />
								</span>
                            </div>
                            <div class="full_area0 itemHeader">
                                <h:panelGroup styleClass="xLarge_area0 endline">
                                    &#160;
                                </h:panelGroup>
                                <h:panelGroup styleClass="seperator" />
                                <h:panelGroup styleClass="free_area0_p8 endline itemHeadline">
                                    <b><h:outputText
											value="#{ViewItemFull.pubItem.metadata.title}"
											converter="HTMLSubSupConverter" escape="false" /></b>
                                </h:panelGroup>
                                <h:panelGroup layout="block" styleClass="medium_area0_p4 statusArea">
                                    <h:panelGroup styleClass="big_imgArea xSmall_marginLExcl withdrawnItem" rendered="#{ViewItemFull.isStateWithdrawn}" />
                                    <h:panelGroup styleClass="big_imgArea xSmall_marginLExcl pendingItem" rendered="#{ViewItemFull.isStatePending}" />
                                    <h:panelGroup styleClass="big_imgArea xSmall_marginLExcl submittedItem" rendered="#{ViewItemFull.isStateSubmitted}" />
                                    <h:panelGroup styleClass="big_imgArea xSmall_marginLExcl releasedItem" rendered="#{ViewItemFull.isStateReleased and !ViewItemFull.isStateWithdrawn}" />
                                    <h:panelGroup styleClass="big_imgArea xSmall_marginLExcl inRevisionItem" rendered="#{ViewItemFull.isStateInRevision}" />
                                    <h:outputText styleClass="noDisplay" value="Item is " />
                                    <h:outputLabel id="lblItemPublicState" styleClass="medium_label endline" style="text-align: center;" rendered="#{ViewItemFull.isStateWithdrawn}">
                                        <h:outputText value="#{ViewItemFull.itemPublicState}" />
                                    </h:outputLabel>
                                    <h:outputLabel id="lblItemState" styleClass="medium_label endline" style="text-align: center;" rendered="#{!ViewItemFull.isStateWithdrawn}">
                                        <h:outputText value="#{ViewItemFull.itemState}" />
                                    </h:outputLabel>
                                </h:panelGroup>
                            </div>
                            <ui:include src="localTags/LocalTags.jspf" />
                        </div>
                        <div class="full_area0 formButtonArea">
                            <h:commandLink styleClass="free_area1_p8 cancelButton xLarge_marginLIncl" id="lnkCancel" value="#{lbl.cancel}" action="#{EditItem.cancel}" onclick="fullItemReloadAjax();" />
                            <h:commandLink styleClass="free_area1_p8 activeButton" id="lnkAccept" value="#{lbl.save}" action="#{EditItem.acceptLocalTags}" onclick="fullItemReloadAjax();" />
                        </div>
                    </div>
                    <!-- end: content section -->
                </div>
            </h:form>
        </div>
        <ui:include src="footer/Footer.jspf" />
        <script type="text/javascript">
            $("input[id$='offset']").on('submit',function() {
                $(this).val($(window).scrollTop());
            });
            $(document).ready(function() {
                $(window).scrollTop($("input[id$='offset']").val());
                $(window).on('scroll',function() {
                    $("input[id$='offset']").val($(window).scrollTop());
                });
            });
        </script>
    </f:view>
</body>

</html>