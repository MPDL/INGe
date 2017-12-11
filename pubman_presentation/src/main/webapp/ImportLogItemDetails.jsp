<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">

<body lang="${InternationalizationHelper.locale}">
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <div class="xHuge_area2_p8 messageArea" style="height: 28.37em; overflow-y: auto;">
            <input type="button" id="btnClose" onclick="currentDialog.dialog('close')" value=" " class="min_imgBtn quad_marginLIncl fixMessageBlockBtn" />
            <h2>
                <h:outputText value="#{lbl.import_workspace_details}" />
            </h2>
            <h:panelGroup styleClass="free_area0" style="margin-bottom: 0.56em;" rendered="#{ImportLogItemDetailBean.length == 0}">
                <span class="small_area0">
                	<h:outputText value="#{msg.multiple_import_no_details}" />
				</span>
            </h:panelGroup>
            <ui:repeat var="detail" value="#{ImportLogItemDetailBean.details}">
                <h:panelGroup styleClass="quad_area0" style="margin-bottom: 0.56em;">
                    <span class="small_area0 endline"> <h:outputText
							value="#{detail.status}" />&#160;
					</span>
                    <span class="small_area0 endline"> <h:outputText
							value="#{detail.errorLevel}" />&#160;
					</span>
                    <span class="medium_area0 endline"> <h:outputText
							value="#{detail.startDateFormatted}" />&#160;
					</span>
                    <span class="double_area0 endline"> <h:outputText
							value="#{detail.localizedMessage}"
							converter="HTMLEscapeConverter" escape="false"
							rendered="#{detail.itemId == null}" /> <h:outputLink
							id="lnkDetail" value="#{detail.link}"
							rendered="#{detail.itemId != null}">
							<h:outputText value="#{detail.localizedMessage}"
								converter="HTMLEscapeConverter" escape="false" />&#160;
						</h:outputLink>&#160;
					</span>
                </h:panelGroup>
            </ui:repeat>
        </div>
    </f:view>
</body>

</html>