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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:output method="html" encoding="UTF-8" media-type="text/html"/>

	<xsl:template match="/">
	
		<html>
			<head>
				<title><xsl:value-of select="/explain/name"/>4<xsl:value-of select="/explain/interface"/></title>
			</head>
			<body>
				<h1><xsl:value-of select="/explain/name"/>4<xsl:value-of select="/explain/interface"/></h1>
				<p>
					<xsl:value-of select="/explain/description"/>
				</p>
				<h2>Services</h2>
				<p>
					<a href="../explain/models.xml">Here</a> you can find a machine readable description of the available models.
				</p>
				<ul>
					<xsl:for-each select="/explain/services/service">
						<li>
							<h3>
								<a href="{link}">
									<xsl:value-of select="name"/>
								</a>
							</h3>
							<p>
								<xsl:value-of select="description"/>
							</p>
							Methods:
							<ul>
								<xsl:for-each select="methods/method">
									<li>
										<b><xsl:value-of select="name"/></b>
										<br/>
										<xsl:if test="parameters/parameter">
											Parameters:
											<br/>
											<xsl:for-each select="parameters/parameter">
												<b><xsl:value-of select="name"/></b>
												<xsl:choose>
													<xsl:when test="@required = 'true'"> (required)</xsl:when>
													<xsl:otherwise> (optional)</xsl:otherwise>
												</xsl:choose>:
												<xsl:value-of select="value"/>
												<br/>
											</xsl:for-each>
										</xsl:if>
										<br/>
									</li>
								</xsl:for-each>
							</ul>
							Sample links:
							<ul>
								<xsl:for-each select="samples/sample">
									<li>
										<a href="{.}">
											<xsl:value-of select="."/>
										</a>
										
										<xsl:if test="@description">
											:
											<xsl:value-of select="@description"/>
										</xsl:if>
									</li>
								</xsl:for-each>
							</ul>
						</li>
					</xsl:for-each>
				</ul>
			</body>
		</html>

	</xsl:template>

</xsl:stylesheet>