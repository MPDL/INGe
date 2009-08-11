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
 fÃ¼r wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur FÃ¶rderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Use the following GET parameter:
	- id: The id of the current import (required)
	- page: The current page of items (optional, default 0)
	- itemsPerPage: The number of items that should be displayed (optional, default 0, show all)
 -->
<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:tr="http://myfaces.apache.org/trinidad">

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="Label"/>
		<f:loadBundle var="msg" basename="Messages"/>
		<f:loadBundle var="tip" basename="Tooltip"/>
		
		<html>
		<body>
			<tr:iterator var="item" value="#{ImportItems.import.items}" first="#{ImportItems.page * ImportItems.itemsPerPage}" rows="#{ImportItems.itemsPerPage}">
				<h:panelGroup>
					<div class="full_area0" style="margin-bottom: 0.19em;">
						<div class="medium_area0_p8 state noPaddingTopBottom" style="margin-left: 2.28em;">					
							<h:outputText value="#{item.status}"/>
						</div>
						<div class="huge_area0_p8 noPaddingTopBottom" style="margin-left: 0.36em; margin-right: 0.19em">
							<h:outputText value="#{item.localizedMessage}" rendered="#{item.itemId == null}"/>
							<h:outputLink value="#{item.link}" rendered="#{item.itemId != null}">
								<h:outputText value="#{item.localizedMessage}"/>&#160;
							</h:outputLink>
						</div>
						<div class="large_area0_p8 noPaddingTopBottom">
							<h:outputText value="#{item.startDateFormatted}"/>&#160;
						</div>
						<div class="large_area0_p8 noPaddingTopBottom">
							<h:outputText value="#{item.endDateFormatted}"/>&#160;
						</div>
						<div class="large_area0_p8 noPaddingTopBottom">
							<h:inputHidden value="#{item.detailsLink}"/>
							<a onmouseover="$(this).createDialog({addr: $(this).siblings('input').val(), bg: '#FFF',opacity: 0.5});">
								Details
							</a>
						</div>
						<div class="large_area0_p8 endline noPaddingTopBottom">
							<h:outputText value="#{item.errorLevel}"/>&#160;
						</div>
					</div>
				</h:panelGroup>
			</tr:iterator>
		</body>
		</html>
	</f:view>
</jsp:root>