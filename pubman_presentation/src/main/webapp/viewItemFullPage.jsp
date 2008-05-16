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


 Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page">

<jsp:output doctype-root-element="html"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
			<html>
				<head>
					<link rel="stylesheet" type="text/css" href="../resources/escidoc-css/css/main.css" />
					<link rel="SHORTCUT ICON" href="./images/escidoc.ico"/>
					<meta http-equiv="pragma" content="no-cache"/>
					<meta http-equiv="cache-control" content="no-cache"/>
					<meta http-equiv="expires" content="0"/>
					<!-- FrM: Moved JS sources to external file -->
					<script type="text/javascript" language="JavaScript" src="resources/scripts.js">;</script>
				</head>
				<body>
					<h:outputText id="pageDummy" value="#{ViewItemFullPage.beanName}" style="height: 0px; width: 0px; visibility:hidden; position: absolute" />
					<div id="page_margins">
						<div id="page">
							<h:form id="form1">
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
										<div class="contentActions">
											<h1><h:outputText value="#{lbl.actionMenu_Header}"/></h1>
											<ul>
												<li><h:commandLink id="lnkHelp" onclick="loadHelp('#{InternationalizationHelper.selectedHelpPage}', '#ViewItem');return false" value="#{lbl.mainMenu_lnkHelp}"/></li>
												
												<li><h:commandLink id="lnkEdit" action="#{ViewItemFull.editItem}"
													 value="#{lbl.actionMenu_lnkEdit}" rendered="#{ViewItemFull.isStatePending and ViewItemFull.isLatestVersion and ViewItemFull.isOwner}"/></li>
												
												<li><h:commandLink id="lnkSubmit" action="#{ViewItemFull.submitItem}" 
													 value="#{lbl.actionMenu_lnkSubmit}" rendered="#{ViewItemFull.isStatePending and ViewItemFull.isLatestVersion and ViewItemFull.isOwner}"/></li>

												<li><h:commandLink id="lnkDelete" onclick="if(!confirmDelete('form1:viewItemFull'))return false;"
													 value="#{lbl.actionMenu_lnkDelete}" action="#{ViewItemFull.deleteItem}" rendered="#{ViewItemFull.isStatePending and ViewItemFull.isLatestVersion and ViewItemFull.isOwner}"/></li>
													 
												<li><h:commandLink id="lnkWithdraw" action="#{ViewItemFull.withdrawItem}"
													 value="#{lbl.actionMenu_lnkWithdraw}" rendered="#{ViewItemFull.isStateReleased and ViewItemFull.isLatestVersion and ViewItemFull.isOwner}"/></li>
													 
												<li><h:commandLink id="lnkModify" action="#{ViewItemFull.modifyItem}"
													 value="#{lbl.actionMenu_lnkModify}" rendered="#{(ViewItemFull.isStateReleased || ViewItemFull.isStateSubmitted) and ViewItemFull.isLatestVersion and !ViewItemFull.isModifyDisabled and ViewItemFull.isModerator}"/></li>
													 
												<li><h:commandLink id="lnkCreateNewRevision" action="#{ViewItemFull.createNewRevision}"
													 value="#{lbl.actionMenu_lnkCreateNewRevision}" rendered="#{ViewItemFull.isStateReleased and ViewItemFull.isLatestRelease and !ViewItemFull.isCreateNewRevisionDisabled and ViewItemFull.isDepositor}"/></li>
													 
												<h:panelGroup rendered="#{ViewItemFull.isDepositor and !ViewItemFull.isStateWithdrawn}">
													<li><h:commandLink binding="#{ViewItemSessionBean.lnkCreateItemFromTemplate}" id="lnkCreateItemFromTemplate" action="#{ItemControllerSessionBean.createItemFromTemplate}"
													 	value="#{lbl.actionMenu_lnkCreateItemFromTemplate}"/></li>
												</h:panelGroup>
												<h:commandButton action="#{viewItemViewItem.viewItemFull}" id="btnDeleteItem" style="visibility:hidden;" />
											</ul>
										</div>
									</div>
									<div id="col3">
										<div class="content">
											<jsp:directive.include file="./viewItem/viewItemFull.jspf"/>
										</div>
									</div>
								</div>
								<jsp:directive.include file="../desktop/messages.jspf"/>
							</h:form>
						 </div>
					  </div>
				</body>
			<script type="text/javascript" src="/clickheat/js/clickheat.js"></script><script type="text/javascript">clickHeatPage = 'view_Item';initClickHeat();</script>
			</html>
		
	</f:view>
</jsp:root>
