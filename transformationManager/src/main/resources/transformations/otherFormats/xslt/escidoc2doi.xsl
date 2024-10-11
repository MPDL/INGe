<?xml version="1.0" encoding="UTF-8"?>
<!--  CDDL HEADER START  The contents of this file are subject to the terms of the  Common Development and Distribution License, Version 1.0 only  (the "License"). You may not use this file except in compliance  with the License.  You can obtain a copy of the license at license/ESCIDOC.LICENSE  or http://www.escidoc.org/license.  See the License for the specific language governing permissions  and limitations under the License.  When distributing Covered Code, include this CDDL HEADER in each  file and include the License file at license/ESCIDOC.LICENSE.  If applicable, add the following below this CDDL HEADER, with the  fields enclosed by brackets "[]" replaced with your own identifying  information: Portions Copyright [yyyy] [name of copyright owner]  CDDL HEADER END  Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft  für wissenschaftlich-technische Information mbH and Max-Planck-  Gesellschaft zur Förderung der Wissenschaft e.V.  All rights reserved. Use is subject to license terms. -->
<!--   Transformations from eDoc Item to eSciDoc PubItem   Author: walter (initial creation)   $Author: walter $ (last changed)  $Revision: 1 $   $LastChangedDate: $ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
	xmlns="http://datacite.org/schema/kernel-4.5"  exclude-result-prefixes="xsi escidocComponents dc dcterms eterms event escidocMetadataRecords file organization person prop publication source version">
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
	xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"xmlns:version="http://escidoc.de/core/01/properties/version/"  -->
	<xsl:import href="../../vocabulary-mappings.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<!-- <xsl:namespace-alias stylesheet-prefix="doi" result-prefix=""/>-->
	<xsl:template match="/">
		<resource xsi:schemaLocation="http://datacite.org/schema/kernel-4.5 https://schema.datacite.org/meta/kernel-4.5/metadata.xsd">
			<identifier identifierType="DOI"></identifier>
			<xsl:apply-templates />
		</resource>
	</xsl:template>
	<!-- preventing every node to be printed -->
	<xsl:template match="text()" />
	<xsl:template match="publication:publication">
		<xsl:if test="eterms:creator">
			<creators>
				<xsl:choose>
					<xsl:when test="eterms:creator[@role = 'http://www.loc.gov/loc.terms/relators/AUT']">
						<xsl:for-each select="eterms:creator[@role = 'http://www.loc.gov/loc.terms/relators/AUT']/person:person">
							<creator>
								<creatorName>
									<xsl:value-of select="./eterms:family-name"/>, <xsl:value-of select="./eterms:given-name"/>
								</creatorName>
								<givenName>
									<xsl:value-of select="./eterms:given-name"/>
								</givenName>
								<familyName>
									<xsl:value-of select="./eterms:family-name"/>
								</familyName>
								<xsl:for-each select="./organization:organization[dc:title != 'External Organisation']">
									<affiliation>
										<xsl:value-of select="./dc:title"/>
									</affiliation>
								</xsl:for-each>
							</creator>
						</xsl:for-each>
						<xsl:for-each select="eterms:creator[@role = 'http://www.loc.gov/loc.terms/relators/AUT']/organization:organization">
							<creator>
								<creatorName>
									<xsl:value-of select="./dc:title"/>
								</creatorName>
							</creator>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:for-each select="eterms:creator/person:person">
							<creator>
								<creatorName>
									<xsl:value-of select="./eterms:family-name"/>, <xsl:value-of select="./eterms:given-name"/>
								</creatorName>
								<givenName>
									<xsl:value-of select="./eterms:given-name"/>
								</givenName>
								<familyName>
									<xsl:value-of select="./eterms:family-name"/>
								</familyName>
								<xsl:for-each select="./organization:organization[dc:title != 'External Organisation']">
									<affiliation>
										<xsl:value-of select="./dc:title"/>
									</affiliation>
								</xsl:for-each>
							</creator>
						</xsl:for-each>
						<xsl:for-each select="eterms:creator/organization:organization">
							<creator>
								<creatorName>
									<xsl:value-of select="./dc:title"/>
								</creatorName>
							</creator>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</creators>
		</xsl:if>
		<xsl:apply-templates />
		<xsl:if test="dc:subject or dcterms:subject">
			<subjects>
				<xsl:for-each select="dc:subject">
					<subject>
						<xsl:attribute name="subjectScheme">
							<xsl:value-of select="./@xsi:type"/>
						</xsl:attribute>
						<xsl:value-of select="."/>
					</subject>
				</xsl:for-each>
				<xsl:for-each select="dcterms:subject">
					<subject>
						<xsl:value-of select="."/>
					</subject>
				</xsl:for-each>
			</subjects>
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
		<publicationYear>
			<xsl:choose>
				<xsl:when test="contains($yearOfPublication, '-')">
					<xsl:value-of select="substring-before($yearOfPublication, '-')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$yearOfPublication"/>
				</xsl:otherwise>
			</xsl:choose>
		</publicationYear>
		<publisher>
			<xsl:choose>
				<xsl:when test="eterms:publishing-info/dc:publisher">
					<xsl:value-of select="eterms:publishing-info/dc:publisher"/>
				</xsl:when>
				<xsl:when test="source:source/eterms:publishing-info/dc:publisher">
					<xsl:value-of select="source:source/eterms:publishing-info/dc:publisher"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>(:unas)</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</publisher>
		<dates>
			<xsl:if test="dcterms:issued != ''">
				<date>
					<xsl:attribute name="dateType">Issued</xsl:attribute>
					<xsl:value-of select="dcterms:issued"/>
				</date>
			</xsl:if>
			<xsl:if test="eterms:published-online != ''">
				<date>
					<xsl:attribute name="dateType">Available</xsl:attribute>
					<xsl:value-of select="eterms:published-online"/>
				</date>
			</xsl:if>
			<xsl:if test="dcterms:dateAccepted != ''">
				<date>
					<xsl:attribute name="dateType">Accepted</xsl:attribute>
					<xsl:value-of select="dcterms:dateAccepted"/>
				</date>
			</xsl:if>
			<xsl:if test="dcterms:dateSubmitted != ''">
				<date>
					<xsl:attribute name="dateType">Submitted</xsl:attribute>
					<xsl:value-of select="dcterms:dateSubmitted"/>
				</date>
			</xsl:if>
			<xsl:if test="dcterms:modified != ''">
				<date>
					<xsl:attribute name="dateType">Updated</xsl:attribute>
					<xsl:value-of select="dcterms:modified"/>
				</date>
			</xsl:if>
			<xsl:if test="dcterms:created != ''">
				<date>
					<xsl:attribute name="dateType">Created</xsl:attribute>
					<xsl:value-of select="dcterms:created"/>
				</date>
			</xsl:if>
			<xsl:if test="dcterms:issued = '' and event:event/eterms:start-date != ''">
				<date>
					<xsl:attribute name="dateType">Issued</xsl:attribute>
					<xsl:value-of select="event:event/eterms:start-date"/>
				</date>
			</xsl:if>
		</dates>
		<resourceType>
			<xsl:variable name="publicationType" select="./@type"/>
			<xsl:attribute name="resourceTypeGeneral">Text</xsl:attribute>
			<xsl:value-of select="$genre-ves/enum[@uri = $publicationType]"/>
		</resourceType>
		<xsl:if test="dc:identifier">
			<alternateIdentifiers>
				<xsl:for-each select="dc:identifier">
					<alternateIdentifier>
						<xsl:attribute name="alternateIdentifierType">
							<xsl:value-of select="./@xsi:type"/>
						</xsl:attribute>
						<xsl:value-of select="."/>
					</alternateIdentifier>
				</xsl:for-each>
			</alternateIdentifiers>
		</xsl:if>
	</xsl:template>
	<xsl:template match="publication:publication/dc:title">
		<titles>
			<title>
				<xsl:value-of select="."/>
			</title>
			<!-- <xsl:for-each select="../dcterms:alternative"><title><xsl:attribute name="titleType"><xsl:value-of select="./@xsi:type"/></xsl:attribute><xsl:attribute name="titleType">AlternativeTitle</xsl:attribute><xsl:value-of select="."/></title></xsl:for-each>-->
		</titles>
	</xsl:template>
	<xsl:template match="dc:language[1]">
		<language>
			<xsl:value-of select="."/>
		</language>
	</xsl:template>
	<xsl:template match="prop:version/version:number">
		<version>
			<xsl:value-of select="."/>
		</version>
	</xsl:template>
	<xsl:template match="escidocComponents:components">
		<xsl:if test="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:license  or ./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:rights">
			<rightsList>
				<xsl:for-each select="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:license">
					<rights>
						<xsl:attribute name="rightsURI">
							<xsl:value-of select="."/>
						</xsl:attribute>
						<xsl:text>Creative Commons</xsl:text>
					</rights>
				</xsl:for-each>
				<xsl:for-each select="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:rights">
					<rights>
						<xsl:value-of select="."/>
					</rights>
				</xsl:for-each>
			</rightsList>
		</xsl:if>
		<formats>
			<xsl:for-each select="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dc:format">
				<format>
					<xsl:value-of select="."/>
				</format>
			</xsl:for-each>
		</formats>
		<sizes>
			<xsl:for-each select="./escidocComponents:component/escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/file:file/dcterms:extent">
				<size>
					<xsl:value-of select="."/>
				</size>
			</xsl:for-each>
		</sizes>
	</xsl:template>
</xsl:stylesheet>