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


 Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" fedoraxsi:schemaLocation="info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-0.xsd" xmlns:fedoraxsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:foxml="info:fedora/fedora-system:def/foxml#">
	<xsl:include href="transform_orgUnit.xsl" />
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:template match="/">
		<xsl:for-each select="foxml:digitalObject">
			<xsl:element name="foxml:digitalObject" namespace="info:fedora/fedora-system:def/foxml#">
				<xsl:for-each select="@*">
					<xsl:copy />
					<xsl:if test="'@fedoraxsi:schemaLocation'">
						<xsl:attribute name="fedoraxsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="'info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd'" />
						<xsl:attribute name="VERSION" select="'1.1'" />
					</xsl:if>
				</xsl:for-each>
				<xsl:for-each select="foxml:objectProperties">
					<xsl:copy-of select="." copy-namespaces="no" />
				</xsl:for-each>

				<xsl:for-each select="foxml:datastream">
					<xsl:choose>
						<xsl:when test="@ID='escidoc'">
							<xsl:element name="foxml:datastream" namespace="info:fedora/fedora-system:def/foxml#">
							<xsl:for-each select="@*">
								<xsl:copy />
							</xsl:for-each>
							<xsl:for-each select="foxml:datastreamVersion">
								<xsl:element name="foxml:datastreamVersion" namespace="info:fedora/fedora-system:def/foxml#">
									<xsl:for-each select="@*">
										<xsl:copy />
									</xsl:for-each>
									<xsl:element name="foxml:xmlContent" namespace="info:fedora/fedora-system:def/foxml#">
										<xsl:for-each select="foxml:xmlContent">
											<xsl:call-template name="orgUnit" />
										</xsl:for-each>
									</xsl:element>
								</xsl:element>
							</xsl:for-each>
							</xsl:element>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="." copy-namespaces="no" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
