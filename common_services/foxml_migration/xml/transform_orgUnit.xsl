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
	xmlns="http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:ou="http://escidoc.mpg.de/metadataprofile/schema/0.1/organization"
	xmlns:oldkml="http://www.opengis.net/kml/2.2"
	xmlns:kml="http://earth.google.com/kml/2.1"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	exclude-result-prefixes="ou oldkml">
	
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:template name="orgUnit">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy copy-namespaces="no">
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="ou:organization-details">
		<organizational-unit xmlns="http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit"
			xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
			xmlns:kml="http://earth.google.com/kml/2.1"
			xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:dcterms="http://purl.org/dc/terms/">
			
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</organizational-unit>
	</xsl:template>
	
	<xsl:template match="ou:start-date">
		<eterms:start-date>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</eterms:start-date>
	</xsl:template>
	
	<xsl:template match="ou:end-date" priority="999">
		<eterms:end-date>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</eterms:end-date>
	</xsl:template>
	
	<xsl:template match="ou:city" priority="999">
		<eterms:city>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</eterms:city>
	</xsl:template>
	
	<xsl:template match="ou:country" priority="999">
		<eterms:country>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</eterms:country>
	</xsl:template>
	
	<xsl:template match="ou:organization-type" priority="999">
		<eterms:organization-type>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</eterms:organization-type>
	</xsl:template>
	
	<xsl:template match="oldkml:coordinates">
		<kml:coordinates>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</kml:coordinates>
	</xsl:template>
	
</xsl:stylesheet>
