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
<xsl:stylesheet xml:base="stylesheet" version="2.0"
	xmlns="http://purl.org/escidoc/metadata/profiles/0.1/virrelement"
	xmlns:virr="http://escidoc.mpg.de/metadataprofile/schema/0.1/virrelement"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	exclude-result-prefixes="virr">
	
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:template name="virrElement">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy copy-namespaces="no">
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="virr:virrelement">
		<virrelement xmlns="http://purl.org/escidoc/metadata/profiles/0.1/virrelement">
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</virrelement>
	</xsl:template>
	
</xsl:stylesheet>
