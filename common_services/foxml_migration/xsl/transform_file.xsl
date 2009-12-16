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
<xsl:stylesheet xml:base="stylesheet" version="2.0"
	xmlns="http://purl.org/escidoc/metadata/profiles/0.1/file"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	exclude-result-prefixes="xlink file">
	
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:param name="path"/>
	
	<xsl:variable name="vm" select="document( concat( if ($path!='') then concat ($path, '/') else '', 'ves-mapping.xml' ) )/mappings"/>
	
	<xsl:template name="file">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template name="content-category-prop">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy copy-namespaces="no">
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="file:content-category">
		<xsl:variable name="v1" select="."/>
		<xsl:variable name="v2" select="$vm/content-category/v1-to-v2/map[@v1=$v1]"/>
		<!-- review method from the ves -->
		<xsl:element name="eterms:content-category">
			<xsl:value-of select=" if (exists($v2)) then $v2 else error( QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), concat ('No mapping v1.0 to v2.0 for review method: ', $v1 ) ) " />
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="prop:content-category">
		<xsl:variable name="v1" select="."/>
		<xsl:variable name="v2" select="$vm/content-category/v1-to-v2/map[@v1=$v1]"/>
		<!-- review method from the ves -->
		<xsl:element name="prop:content-category">
			<xsl:value-of select=" if (exists($v2)) then $v2 else error( QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), concat ('No mapping v1.0 to v2.0 for review method: ', $v1 ) ) " />
			<xsl:apply-templates select="*/*"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="file:file">
		<file xmlns="http://purl.org/escidoc/metadata/profiles/0.1/file"
			xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
			xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:dcterms="http://purl.org/dc/terms/">
			
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</file>
	</xsl:template>

</xsl:stylesheet>
