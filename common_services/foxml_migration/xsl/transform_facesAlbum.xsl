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
	xmlns="http://purl.org/escidoc/metadata/profiles/0.1/publication"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
	xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
	xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
	xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:metadata="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"
	xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
	exclude-result-prefixes="xlink metadata escidoc">
	
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:template name="facesAlbum">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:copy copy-namespaces="no">
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="faces-album">
		<publication xmlns="http://purl.org/escidoc/metadata/profiles/0.1/publication"
			xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
			xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
			xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
			xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
			xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
			xmlns:dc="http://purl.org/dc/elements/1.1/"
			xmlns:dcterms="http://purl.org/dc/terms/">
			
			<xsl:attribute name="type">
				<xsl:text>other</xsl:text>
			</xsl:attribute>
			
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			
			<xsl:for-each select="./metadata:creator">
				<eterms:creator>
					<xsl:copy-of select="@*" copy-namespaces="no"/>
					<!--
					<xsl:for-each select="./escidoc:person">
						<xsl:call-template name="person"/>
					</xsl:for-each>
					<xsl:for-each select="./escidoc:organization">
						<xsl:call-template name="organization"/>
					</xsl:for-each>
					-->
					<xsl:apply-templates/>
				</eterms:creator>
			</xsl:for-each>
			
			<dc:title>
				<xsl:copy-of select="@*" copy-namespaces="no"/>
				<xsl:value-of select="./dc:title"/>
			</dc:title>
			
			<dcterms:alternative>
				<xsl:copy-of select="@*" copy-namespaces="no"/>
				<xsl:value-of select="./name"/>
			</dcterms:alternative>
			
			<dcterms:abstract>
				<xsl:copy-of select="@*" copy-namespaces="no"/>
				<xsl:value-of select="./description"/>
			</dcterms:abstract>
		</publication>
	</xsl:template>
	
	<xsl:template match="*[namespace-uri()='http://escidoc.mpg.de/metadataprofile/schema/0.1/types']" priority="1">
		<xsl:element name="eterms:{local-name()}">
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="escidoc:person" priority="999">
		<person:person>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:call-template name="complete-name"/>
			<!--
			<xsl:for-each select="./escidoc:organization">
				<xsl:call-template name="organization"/>
			</xsl:for-each>
			<xsl:for-each select="./escidoc:person/escidoc:identifier">
				<xsl:call-template name="identifier"/>
			</xsl:for-each>
			-->
			<xsl:apply-templates/>
		</person:person>
	</xsl:template>
	
	<xsl:template match="escidoc:organization" priority="999">
		<organization:organization>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<!--
			<xsl:for-each select="./escidoc:organization/escidoc:organization-name">
				<xsl:call-template name="organization-name"/>
			</xsl:for-each>
			<xsl:for-each select="./escidoc:organization/escidoc:identifier">
				<xsl:call-template name="identifier"/>
			</xsl:for-each>
			-->
			<xsl:apply-templates/>
		</organization:organization>
	</xsl:template>
	
	<xsl:template match="escidoc:organization/escidoc:organization-name" priority="999">
		<dc:title>
			<xsl:copy-of select="@*" copy-namespaces="no"/>
			<xsl:apply-templates/>
		</dc:title>
	</xsl:template>
	
	<xsl:template match="escidoc:organization/escidoc:identifier" priority="999"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		exclude-result-prefixes="xsi xs">
		<dc:identifier>
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:variable name="value" as="xs:string" select="string(local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
				<xsl:namespace name="idtype" select="'http://purl.org/escidoc/metadata/terms/0.1/'"/>
				<xsl:attribute name="xsi:type" select="concat('idtype:', $value)"/>
			</xsl:if>
			<xsl:apply-templates/>
		</dc:identifier>
	</xsl:template>

	<xsl:template match="escidoc:person/escidoc:identifier" priority="999"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		exclude-result-prefixes="xsi xs">
		<dc:identifier>
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:variable name="value" as="xs:string" select="string(local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
				<xsl:namespace name="idtype" select="'http://purl.org/escidoc/metadata/terms/0.1/'"/>
				<xsl:attribute name="xsi:type" select="concat('idtype:', $value)"/>
			</xsl:if>
			<xsl:apply-templates/>
		</dc:identifier>
	</xsl:template>
	 
	<xsl:template match="dc:identifier"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		exclude-result-prefixes="xsi xs">
		
		<xsl:element name="dc:identifier">
			<xsl:copy-of select="@*[name() != 'xsi:type']"/>
			<xsl:if test="@*[name() = 'xsi:type']">
				<xsl:variable name="value" as="xs:string" select="string(local-name-from-QName(resolve-QName(@xsi:type, .)))"/>
				<xsl:namespace name="idtype" select="'http://purl.org/escidoc/metadata/terms/0.1/'"/>
				<xsl:attribute name="xsi:type" select="concat('idtype:', $value)"/>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="complete-name">
		<eterms:complete-name>
		</eterms:complete-name>
	</xsl:template>

</xsl:stylesheet>
