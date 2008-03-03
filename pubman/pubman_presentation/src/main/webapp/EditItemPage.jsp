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
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:tr="http://myfaces.apache.org/trinidad">
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
<!-- FrM: Moved JS sources to external file -->
<script type="text/javascript" language="JavaScript" src="resources/scripts.js">;</script>
</head>
<body>
	<h:outputText id="pageDummy" value="#{EditItemPage.beanName}" style="height: 0px; width: 0px; visibility:hidden; position: absolute" />
	<div id="page_margins">
						<div id="page">
		<tr:form usesUpload="true">
		<div id="header">
		<jsp:directive.include file="desktop/Header.jspf"/>
		<jsp:directive.include file="desktop/Login.jspf"/>
		<jsp:directive.include file="desktop/Search.jspf"/>
		</div>
		<div id="nav">
			<jsp:directive.include file="desktop/Breadcrumb.jspf"/>
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
												<li><h:commandLink id="lnkHelp" onclick="loadHelp('#{InternationalizationHelper.selectedHelpPage}', '#EditItem');return false" value="#{lbl.mainMenu_lnkHelp}"/></li>
												<li><h:commandLink id="lnkValidate"  value="#{lbl.EditItem_lnkValidate}" action="#{EditItem.validate}"/></li>
										<li><h:commandLink id="lnkSave" binding ="#{EditItem.lnkSave}"  value="#{lbl.EditItem_lnkSave}" action="#{EditItem.save}"/></li>
										<li><h:commandLink id="lnkSaveAndSubmit" binding ="#{EditItem.lnkSaveAndSubmit}"  value="#{lbl.EditItem_lnkSaveAndSubmit}" action="#{EditItem.saveAndSubmit}"/></li>
										<li><h:commandLink id="lnkDelete" binding ="#{EditItem.lnkDelete}" immediate="true"  value="#{lbl.EditItem_lnkDelete}" onmousedown="if(!confirmDelete('form1:EditItem'))return false;" action="#{EditItem.delete}"/></li>
										<li><h:commandLink id="lnkCancel" immediate="true"  value="#{lbl.EditItem_lnkCancel}" action="#{EditItem.cancel}"/></li>
										<li><h:commandLink id="lnkAccept" binding ="#{EditItem.lnkAccept}"  value="#{lbl.EditItem_lnkAccept}" action="#{EditItem.saveAndAccept}"/></li>
											</ul>
										</div>
									</div>
									<div id="col3">
										<div class="content">
				<jsp:directive.include file="editItem/EditItem.jspf"/>
				</div>
				</div>
		</div>
		</tr:form>
		 </div>
		  </div>
</body>
</html>

</f:view>
</jsp:root>
