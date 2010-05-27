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
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:param name="path"/>
	<xsl:variable name="vm" select="document(
		concat(
			if ($path!='') then concat ($path, '/') else '', 
			'ves-mapping.xml'
		)
	)/mappings"/>

	<xsl:template name="admin-descriptor">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="node() | @*">
		<xsl:copy>
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="allowed-genre">
		<xsl:variable name="v1" select="."/> 
		<xsl:variable name="v2" select="$vm/publication-type/v1-to-v2/map[@v1=$v1]"/>
		<xsl:element name="{name(.)}">
			<xsl:value-of select="
				if (exists($v2))  
				then $v2
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping v1.0 to v2.0 allowed genre: ', $v1 )
					)
			" />
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>

		
</xsl:stylesheet>
