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
	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
		<f:loadBundle var="lbl" basename="de.mpg.escidoc.pubman.bundle.Label"/>
		<f:loadBundle var="msg" basename="de.mpg.escidoc.pubman.bundle.Messages"/>
			<html>
				<head>
					
					<script type="text/javascript">
						function FensterOeffnen () {
						  window.resizeTo(400, 200);
						}
					</script>
				</head>
				<body onload="FensterOeffnen();" class="page">
					<h:outputText id="pageDummy" value="#{FacesBean.beanName}" style="height: 0px; width: 0px; visibility:hidden; position: absolute" />
					<h:form id="form1">
						<div style="position: absolute; left: 0px; top: 0px; width: 400px; height: 200px">
							<jsp:directive.include file="viewItemOrganizationInformation.jspf"/>
						</div>
					</h:form>
				</body>
			</html>
		
	</f:view>
</jsp:root>
