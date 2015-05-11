<?xml version="1.0" encoding="UTF-8"?>
<!--
 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from eDoc Item to eSciDoc PubItem 
	Author: walter (initial creation) 
	$Author: walter $ (last changed)
	$Revision: 1 $ 
	$LastChangedDate:  $
-->
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:dc="${xsd.metadata.dc}" xmlns:dcterms="${xsd.metadata.dcterms}" 
    xmlns:escidocComponents="${xsd.soap.item.components}" 
    xmlns:eterms="${xsd.metadata.terms}" 
    xmlns:event="${xsd.metadata.event}" 
    xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}" 
    xmlns:file="${xsd.metadata.file}" 
    xmlns:organization="${xsd.metadata.organization}" 
    xmlns:person="${xsd.metadata.person}" 
    xmlns:prop="${xsd.soap.common.prop}" 
    xmlns:publication="${xsd.metadata.publication}" 
    xmlns:source="${xsd.metadata.source}" 
    xmlns:version="${xsd.soap.common.version}" 
    xmlns:doi="http://datacite.org/schema/kernel-3"
    exclude-result-prefixes="xsi escidocComponents dc dcterms eterms event escidocMetadataRecords file organization person prop publication source version">
		
		<!--
		xmlns:dc="${xsd.metadata.dc}" 
		xmlns:dcterms="${xsd.metadata.dcterms}"
		xmlns:escidocComponents="${xsd.soap.item.components}"
		xmlns:eterms="${xsd.metadata.terms}"
		xmlns:event="${xsd.metadata.event}"
		xmlns:escidocMetadataRecords="${xsd.soap.common.metadatarecords}"
		xmlns:file="${xsd.metadata.file}"
		xmlns:organization="${xsd.metadata.organization}"
		xmlns:person="${xsd.metadata.person}"
		xmlns:prop="${xsd.soap.common.prop}"
		xmlns:publication="${xsd.metadata.publication}"
		xmlns:source="${xsd.metadata.source}"
		xmlns:version="${xsd.soap.common.version}"
		
		xmlns:dc="http://purl.org/dc/elements/1.1/" 
		xmlns:dcterms="http://purl.org/dc/terms/"
		xmlns:escidocComponents="http://www.escidoc.de/schemas/components/0.9"
		xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
		xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
		xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
		xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file"
		xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
		xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
		xmlns:prop="http://escidoc.de/core/01/properties/"
		xmlns:publication="http://purl.org/escidoc/metadata/profiles/0.1/publication"
		xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
		xmlns:version="http://escidoc.de/core/01/properties/version/"
		-->
	
    <xsl:import href="../../vocabulary-mappings.xsl"/>
	
    <xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<!-- 
	<xsl:namespace-alias stylesheet-prefix="doi" result-prefix=""/>
	-->
    <xsl:template match="/">
        <doi:resource xsi:schemaLocation="http://datacite.org/schema/kernel-3 http://schema.datacite.org/meta/kernel-3/metadata.xsd">
            <doi:identifier identifierType="DOI">dummyDOI</doi:identifier>
            <xsl:apply-templates />
        </doi:resource>
    </xsl:template>
	
	<!-- preventing every node to be printed -->
    <xsl:template match="text()" />
	
    <xsl:template match="publication:publication">
        <xsl:if test="eterms:creator">
            <doi:creators>
                <xsl:for-each select="eterms:creator">
                    <doi:creator>
                        <doi:creatorName>
                            <xsl:value-of select="./person:person/eterms:family-name"/>, <xsl:value-of select="./person:person/eterms:given-name"/>
                        </doi:creatorName>
                        <xsl:for-each select="organization:organization">
                            <doi:affiliation>
                                <xsl:value-of select="./dc:title"/>
                            </doi:affiliation>
                        </xsl:for-each>
                    </doi:creator>
                </xsl:for-each>
            </doi:creators>
        </xsl:if>
	
        <xsl:apply-templates />
	
        <xsl:if test="dc:subject or dcterms:subject">
            <doi:subjects>
                <xsl:for-each select="dc:subject">
                    <doi:subject>
                        <xsl:attribute name="subjectScheme">
                            <xsl:value-of select="./@xsi:type"/>
                        </xsl:attribute>
                        <xsl:value-of select="."/>
                    </doi:subject>
                </xsl:for-each>
                <xsl:for-each select="dcterms:subject">
                    <doi:subject>
                        <xsl:value-of select="."/>
                    </doi:subject>
                </xsl:for-each>
            </doi:subjects>
        </xsl:if>
	
        <xsl:variable name="yearOfPublication">
            <xsl:choose>
                <xsl:when test="dcterms:issued != ''">
                    <xsl:value-of select="dcterms:issued"/>
                </xsl:when>
                <xsl:when test="eterms:published-online != ''">
                    <xsl:value-of select="eterms:published-online"/>
                </xsl:when>
                <xsl:when test="dcterms:dateAccepted != ''">
                    <xsl:value-of select="dcterms:dateAccepted"/>
                </xsl:when>
                <xsl:when test="dcterms:dateSubmitted != ''">
                    <xsl:value-of select="dcterms:dateSubmitted"/>
                </xsl:when>
                <xsl:when test="dcterms:modified != ''">
                    <xsl:value-of select="dcterms:modified"/>
                </xsl:when>
                <xsl:when test="dcterms:created != ''">
                    <xsl:value-of select="dcterms:created"/>
                </xsl:when>
                <xsl:when test="event:event/eterms:start-date != ''">
                    <xsl:value-of select="event:event/eterms:start-date"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
	
        <doi:publicationYear>
            <xsl:choose>
                <xsl:when test="contains($yearOfPublication, '-')">
                    <xsl:value-of select="substring-before($yearOfPublication, '-')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$yearOfPublication"/>
                </xsl:otherwise>
            </xsl:choose>
        </doi:publicationYear>
	
        <doi:publisher>
            <xsl:choose>
                <xsl:when test="eterms:publishing-info/dc:publisher">
                    <xsl:value-of select="."/>
                </xsl:when>
                <xsl:when test="source:source/eterms:publishing-info/dc:publisher">
                    <xsl:value-of select="."/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>(:unas)</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </doi:publisher>
	
        <doi:dates>
            <xsl:if test="dcterms:issued != ''">
                <doi:date>
                    <xsl:attribute name="dateType">Issued</xsl:attribute>
                    <xsl:value-of select="dcterms:issued"/>
                </doi:date>
            </xsl:if>
            <xsl:if test="eterms:published-online != ''">
                <doi:date>
                    <xsl:attribute name="dateType">Available</xsl:attribute>
                    <xsl:value-of select="eterms:published-online"/>
                </doi:date>
            </xsl:if>
            <xsl:if test="dcterms:dateAccepted != ''">
                <doi:date>
                    <xsl:attribute name="dateType">Accepted</xsl:attribute>
                    <xsl:value-of select="dcterms:dateAccepted"/>
                </doi:date>
            </xsl:if>
            <xsl:if test="dcterms:dateSubmitted != ''">
                <doi:date>
                    <xsl:attribute name="dateType">Submitted</xsl:attribute>
                    <xsl:value-of select="dcterms:dateSubmitted"/>
                </doi:date>
            </xsl:if>
            <xsl:if test="dcterms:modified != ''">
                <doi:date>
                    <xsl:attribute name="dateType">Updated</xsl:attribute>
                    <xsl:value-of select="dcterms:modified"/>
                </doi:date>
            </xsl:if>
            <xsl:if test="dcterms:created != ''">
                <doi:date>
                    <xsl:attribute name="dateType">Created</xsl:attribute>
                    <xsl:value-of select="dcterms:created"/>
                </doi:date>
            </xsl:if>
            <xsl:if test="event:event/eterms:start-date != ''">
                <doi:date>
                    <xsl:attribute name="dateType">event start date</xsl:attribute>
                    <xsl:value-of select="event:event/eterms:start-date"/>
                </doi:date>
            </xsl:if>
        </doi:dates>
	
        <doi:resourceType>
            <xsl:variable name="publicationType" select="./@type"/>
            <xsl:attribute name="resourceTypeGeneral">Text</xsl:attribute>
            <xsl:value-of select="$genre-ves/enum[@uri = $publicationType]"/>
        </doi:resourceType>
	
        <xsl:if test="dc:identifier">
            <doi:alternateIdentifiers>
                <xsl:for-each select="dc:identifier">
                    <doi:alternateIdentifier>
                        <xsl:attribute name="alternateIdentifierType">
                            <xsl:value-of select="./@xsi:type"/>
                        </xsl:attribute>
                        <xsl:value-of select="."/>
                    </doi:alternateIdentifier>
                </xsl:for-each>
            </doi:alternateIdentifiers>
        </xsl:if>
    </xsl:template>
	
    <xsl:template match="publication:publication/dc:title">
        <doi:titles>
            <doi:title>
                <xsl:value-of select="."/>
            </doi:title>
            <xsl:for-each select="../dcterms:alternative">
                <doi:title>
                    <!-- <xsl:attribute name="titleType"><xsl:value-of select="./@xsi:type"/></xsl:attribute> -->
                    <xsl:attribute name="titleType">AlternativeTitle</xsl:attribute>
                    <xsl:value-of select="."/>
                </doi:title>
            </xsl:for-each>
        </doi:titles>
    </xsl:template>
	
    <xsl:template match="dc:language">
        <doi:language>
            <xsl:value-of select="."/>
        </doi:language>
    </xsl:template>
	
    <xsl:template match="prop:version/version:number">
        <doi:version>
            <xsl:value-of select="."/>
        </doi:version>
    </xsl:template>
	
    <xsl:template match="escidocComponents:components">
        <xsl:if test="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:license
	                    or ./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:rights">
            <doi:rightsList>
                <xsl:for-each select="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:license">
                    <doi:rights>
                        <xsl:attribute name="rightsURI">
                            <xsl:value-of select="."/>
                        </xsl:attribute>
                        <xsl:text>Creative Commons</xsl:text>
                    </doi:rights>
                </xsl:for-each>
                <xsl:for-each select="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:rights">
                    <doi:rights>
                        <xsl:value-of select="."/>
                    </doi:rights>
                </xsl:for-each>
            </doi:rightsList>
        </xsl:if>
	
        <doi:formats>
            <xsl:for-each select="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:format">
                <doi:format>
                    <xsl:value-of select="."/>
                </doi:format>
            </xsl:for-each>
        </doi:formats>
	
        <doi:sizes>
            <xsl:for-each select="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:extent">
                <doi:size>
                    <xsl:value-of select="."/>
                </doi:size>
            </xsl:for-each>
        </doi:sizes>
    </xsl:template>

</xsl:stylesheet>
