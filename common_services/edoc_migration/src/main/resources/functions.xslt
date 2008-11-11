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


 Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0" xmlns:escidoc="urn:escidoc:functions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">

	<xsl:function name="escidoc:ou-name">
		<xsl:param name="name"/>

		<xsl:value-of select="$organizational-units//ou[@name = $name or @alias = $name]/@name"/>

		<xsl:if test="$organizational-units//ou[@name = $name or @alias = $name]/../@name != $name">
			<xsl:text>, </xsl:text>
			<xsl:value-of select="escidoc:ou-name($organizational-units//ou[@name = $name or @alias = $name]/../@name)"/>
		</xsl:if>
		
	</xsl:function>
	
	<xsl:function name="escidoc:ou-id">
		<xsl:param name="name"/>
		
		<xsl:choose>
			<xsl:when test="$organizational-units//ou[@name = $name or @alias = $name]">
				<xsl:value-of select="$organizational-units//ou[@name = $name or @alias = $name]/@id"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$organizational-units//ou[@name = 'external']/@id"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>
	
</xsl:stylesheet>