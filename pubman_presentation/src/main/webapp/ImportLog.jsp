<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://xmlns.jcp.org/jsf/core" xmlns:h="http://xmlns.jcp.org/jsf/html" xmlns:ui="http://xmlns.jcp.org/jsf/facelets" xmlns:p="http://primefaces.org/ui" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">

<body lang="${InternationalizationHelper.locale}">
    <f:view locale="#{InternationalizationHelper.userLocale}">
        <f:loadBundle var="lbl" basename="Label" />
        <f:loadBundle var="msg" basename="Messages" />
        <f:loadBundle var="tip" basename="Tooltip" />
        <table>
            <tbody>
                <tr class="full_area0 listItem">
                    <td class="free_area0 endline">
                    	<span class="tiny_area0">&#160; </span>
                    </td>
                    <td class="free_area0 endline status">
                        <h:panelGroup styleClass="seperator"></h:panelGroup>
                        <h:panelGroup styleClass="free_area0 endline statusArea">
                            <h:panelGroup layout="block" styleClass="big_imgArea statusIcon ajaxedImport #{ImportLogBean.import.status} import#{ImportLogBean.import.status}#{ImportLogBean.import.errorLevel}" />
                            <h:outputLabel id="lblErrorLevel" styleClass="free_area0_p3 medium_label endline" title="#{ImportLogBean.import.errorLevel}">
                                <h:panelGroup rendered="#{!ImportLogBean.import.finished}">
                                    <h:outputText value="#{ImportLogBean.import.percentage}" />% -
                                </h:panelGroup>
                                <h:outputText value="#{ImportLogBean.import.status}" />
                            </h:outputLabel>
                            <h:inputHidden id="inpImportLogLink" value="#{ImportLogBean.import.logLink}" />
                        </h:panelGroup>
                    </td>
                    <td class="free_area0 endline">
                        <h:panelGroup styleClass="seperator"></h:panelGroup>
                        <span class="large_area0_p8">
                        	<h:outputLink id="lnkImportMyItems"
								value="#{ImportLogBean.import.myItemsLink}"
								rendered="#{ImportLogBean.import.importedItems}">
								<h:outputText value="#{ImportLogBean.import.message}" />
							</h:outputLink>
							<h:outputText value="#{ImportLogBean.import.message}" rendered="#{!ImportLogBean.import.importedItems}" />
						</span>
					</td>
                    <td class="free_area0 endline">
                        <h:panelGroup styleClass="seperator"></h:panelGroup>
                        <span class="large_area0_p8">
                        	<h:outputText value="#{ImportWorkspace.getFormatLabel(ImportLogBean.import)}" />&#160;
						</span>
					</td>
                    <td class="free_area0 endline">
                        <h:panelGroup styleClass="seperator"></h:panelGroup>
                        <span class="large_area0_p8">
                        	<h:outputText value="#{ImportLogBean.import.startDateFormatted}" />&#160;
					    </span>
					</td>
                    <td class="free_area0 endline">
                        <h:panelGroup styleClass="seperator"></h:panelGroup>
                        <span class="large_area0_p8">
                        	<h:outputText value="#{ImportLogBean.import.endDateFormatted}" />&#160;
						</span>
					</td>
                    <td class="free_area0 endline">
                        <h:panelGroup styleClass="seperator"></h:panelGroup>
                        <span class="large_area0_p8 detailsLinkArea">
                        	<h:inputHidden id="inpImportItemsLink" value="#{ImportLogBean.import.itemsLink}" />
							<a onclick="if(!$(this).parents('tr').next('tr').hasClass('importDetails')) {$(this).parents('tr').after(detailsAwaiting); $(this).parents('tr').next('.importDetails').find('td').load($(this).siblings('input').val())} else {$(this).parents('tr').next('.importDetails').remove();}">
								<b><h:outputText value="#{lbl.import_workspace_detailsView}" /></b>
							</a>
						</span>
					</td>
                    <td class="free_area0 endline">
                        <h:panelGroup styleClass="seperator">
                        </h:panelGroup>
                        <span class="large_area0 endline">
                        	<h:panelGroup rendered="false"
								styleClass="large_area0_p8 noPaddingTopBottom endline">
								<h:outputText value="#{ImportLogBean.import.errorLevel}" />
							</h:panelGroup>
							<h:panelGroup rendered="#{ImportLogBean.import.finished}">
								<h:outputLink
									styleClass="small_area0_p8 noPaddingTopBottom endline"
									title="#{tip.import_workspace_remove_import}"
									value="ImportWorkspaceRemove.jsp?id=#{ImportLogBean.importId}">
									<h:outputText value="#{lbl.import_workspace_remove_import}" />
								</h:outputLink>
								<h:outputLink
									styleClass="small_area0_p8 noPaddingTopBottom endline"
									title="#{tip.import_workspace_delete_items}"
									value="ImportWorkspaceDelete.jsp?id=#{ImportLogBean.importId}"
									rendered="#{ImportLogBean.import.importedItems}">
									<h:outputText value="#{lbl.import_workspace_delete_items}" />
								</h:outputLink>
								<h:outputLink
									styleClass="large_area0_p8 noPaddingTopBottom endline"
									title="#{tip.import_workspace_submit_items}"
									value="ImportWorkspaceSubmit.jsp?id=#{ImportLogBean.importId}"
									rendered="#{ImportLogBean.import.importedItems and !ImportLogBean.simpleWorkflow and !LoginHelper.isModerator}">
									<h:outputText value="#{lbl.import_workspace_submit_items}" />
								</h:outputLink>
								<h:outputLink
									styleClass="large_area0_p8 noPaddingTopBottom endline"
									title="#{tip.import_workspace_submit_release_items}"
									value="ImportWorkspaceRelease.jsp?id=#{ImportLogBean.importId}"
									rendered="#{ImportLogBean.import.importedItems and (LoginHelper.isModerator or ImportLogBean.simpleWorkflow)}">
									<h:outputText value="#{lbl.import_workspace_submit_release_items}" />
								</h:outputLink>
							</h:panelGroup>
						</span>
					</td>
                </tr>
            </tbody>
        </table>
    </f:view>
</body>

</html>