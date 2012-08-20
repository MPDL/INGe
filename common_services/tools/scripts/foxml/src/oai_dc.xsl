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


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<!--
	Transformations from eSciDoc PubItem to OAI DC See mapping:
	http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_Dublin_Core_Mapping
	Author: Vlad Makarenko (initial creation) $Author: $ (last changed)
	$Revision: $ $LastChangedDate: $
-->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:dcmitype="http://purl.org/dc/dcmitype/"
	xmlns:pub="http://purl.org/escidoc/metadata/profiles/0.1/publication"
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
	xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
	xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
	xmlns:publication="http://purl.org/escidoc/metadata/profiles/0.1/publication"
	xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
	xmlns:foxml="info:fedora/fedora-system:def/foxml#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:param name="ID"/>
	
	<xsl:param name="year"/>
	<xsl:param name="month"/>
	<xsl:param name="day"/>
	<xsl:param name="hour"/>
	<xsl:param name="minute"/>
	<xsl:param name="second"/>
	<xsl:param name="millis"/>
	
	<xsl:template match="*">
		<xsl:choose>
			<xsl:when test="/foxml:digitalObject/foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/rdf:type/@rdf:resource = 'http://escidoc.de/core/01/resources/Item'">
				<xsl:copy>
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="/"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	
	<xsl:template match="foxml:datastream[@ID = 'DC']">
		
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
	
			<xsl:variable name="dc">
				<xsl:for-each select="/foxml:digitalObject/foxml:datastream[@ID = 'escidoc']/foxml:datastreamVersion[last()]/foxml:xmlContent/publication:publication">
					
					<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dcmitype="http://purl.org/dc/dcmitype/">
		
						<!-- dc:type +	-->
						<dc:type>
							<xsl:value-of select="@type" />
						</dc:type>
						
						<dc:type>Text</dc:type>
						
						<xsl:if test="eterms:degree!=''">
							<dc:type>
								<xsl:value-of select="eterms:degree" />
							</dc:type>
						</xsl:if>
						
						<!-- CREATORS -->
						<xsl:for-each select="eterms:creator">
		
							<xsl:choose>
								<xsl:when test="@role = 'http://www.loc.gov/loc.terms/relators/AUT'">
									<dc:creator>
										<xsl:apply-templates/>
									</dc:creator>
									<xsl:if test="person:person/organization:organization!=''">
										<dc:contributor>
											<xsl:apply-templates select="person:person/organization:organization"/>
										</dc:contributor>
									</xsl:if>
								</xsl:when>
								<xsl:when test="@role = 'http://www.loc.gov/loc.terms/relators/SAD' or @role = 'http://www.loc.gov/loc.terms/relators/CTB' or @role = 'http://www.loc.gov/loc.terms/relators/TRC' or @role = 'http://www.loc.gov/loc.terms/relators/TRL' or @role = 'http://www.loc.gov/loc.terms/relators/HNR'">
									<dc:contributor>
										<xsl:apply-templates/>
									</dc:contributor>
									<xsl:if test="person:person/organization:organization!=''">
										<dc:contributor>
											<xsl:apply-templates select="person:person/organization:organization"/>
										</dc:contributor>
									</xsl:if>
								</xsl:when>
							</xsl:choose>
						
						</xsl:for-each>
		
						<!-- dc:title -->
						<xsl:variable name="title" select="normalize-space(dc:title)"/>
						<xsl:if test="$title != ''">
							<dc:title>
								<xsl:value-of select="$title"/>
							</dc:title>
						</xsl:if>
		
						<!-- dc:language + -->
						<xsl:variable name="language" select="normalize-space(dc:language)"/>
						<xsl:if test="$language != ''">
							<dc:language>
								<xsl:value-of select="$language"/>
							</dc:language>
						</xsl:if>
		
						<!-- dcterms:alternative -> dc:title + -->
						<xsl:for-each select="dcterms:alternative">
							<dc:title>
								<xsl:value-of select="." />
							</dc:title>
						</xsl:for-each>
		
						<!-- dc:identifiers +-->
						<dc:identifier>
							<xsl:value-of select="$ID"/>
						</dc:identifier>
						<xsl:for-each select="dc:identifier">
							<dc:identifier>
								<xsl:if test="@xsi:type != ''">
									<xsl:value-of select="@xsi:type" />
									<xsl:text>: </xsl:text>
								</xsl:if>
								<xsl:value-of select="." />
							</dc:identifier>
						</xsl:for-each>
		
						<!-- dc:publisher +-->
						<xsl:variable name="publisher" select="normalize-space(eterms:publishing-info/dc:publisher)"/>
						<xsl:if test="$publisher != ''">
							<dc:publisher>
								<xsl:value-of select="$publisher"/>
							</dc:publisher>
						</xsl:if>
		
						<!-- dc:date +-->				
						<xsl:variable name="date">
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
							</xsl:choose>
						</xsl:variable>
						
						<xsl:if test="$date != ''">
							<dc:date>
								<xsl:value-of select="$date" />
							</dc:date>
						</xsl:if>
						
						<xsl:variable name="stype-uri" select="source:source/@type"/>
						
						<!-- dc:sources +? -->
						<xsl:variable name="source">
							<xsl:value-of select="eterms:publishing-info/eterms:place" />
							<xsl:value-of select="concat(' ', eterms:publishing-info/eterms:edition)" />
							<xsl:value-of select="concat(' ', source:source[1]/dc:title)" />
							<xsl:for-each select="source:source[1][$stype-uri = 'http://purl.org/eprint/type/Book' or $stype-uri = 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings' or $stype-uri = 'http://purl.org/escidoc/metadata/ves/publication-types/issue' or $stype-uri = 'http://purl.org/escidoc/metadata/ves/publication-types/other']/eterms:creator">
								<xsl:if test="./person:person!=''">
									<xsl:value-of select="concat(' ', ./person:person/eterms:family-name, ', ', ./person:person/eterms:given-name)" />
								</xsl:if>
								<xsl:if test="./organization:organization/dc:title!=''">
									<xsl:value-of select="concat(' ', ./organization:organization/dc:title)" />
								</xsl:if>
							</xsl:for-each>
							<xsl:value-of select="concat(' ', source:source[1]/eterms:volume)" />
							<xsl:value-of select="concat(' ', source:source[1]/eterms:issue)" />
							<xsl:for-each select="source:source[1][@type = 'journal' or @type = 'series']/eterms:publishing-info">
								<xsl:value-of select="concat(' ', ./dc:publisher, ' ', ./eterms:place, ' ', ./eterms:edition)" />
							</xsl:for-each>
						</xsl:variable>
						<xsl:variable name="source" select="normalize-space($source)" />
						<xsl:if test="$source!=''">
							<dc:source>
								<xsl:value-of select="$source" />
							</dc:source>
						</xsl:if>
		
						<!-- dc:format + -->
						<xsl:variable name="format">
							<xsl:value-of select="concat(source:source[1]/eterms:start-page, '-', source:source[1]/eterms:end-page, ' ', source:source[1]/eterms:total-number-of-pages )" />
						</xsl:variable>
						
						<xsl:variable name="format" select="normalize-space($format)" />
						
						<xsl:if test="$format != '' and $format != '-'">
							<dc:format>
								<xsl:value-of select="$format" />
							</dc:format>
						</xsl:if>
		
						<!-- dc:relation -->
						<xsl:if test="event:event!=''">
							<xsl:variable name="event-dates" select="concat(event:event/eterms:start-date, '&#8212;', event:event/eterms:end-date)" />
							<xsl:variable name="event" select="concat(event:event/dc:title, ', ', event:event/eterms:place, $event-dates)" />
							<xsl:if test="$event!=''">
								<dc:relation>
									<xsl:value-of select="$event" />
								</dc:relation>
							</xsl:if>
						</xsl:if>
		
						<!-- dc:description -->
						<xsl:variable name="desc" select="normalize-space(dcterms:abstract)"/>
						<xsl:if test="$desc!=''">
							<dc:description>
								<xsl:value-of select="$desc" />
							</dc:description>
						</xsl:if>
						<xsl:variable name="desc" select="normalize-space(dcterms:tableOfContents)"/>
						<xsl:if test="$desc!=''">
							<dc:description>
								<xsl:value-of select="$desc" />
							</dc:description>
						</xsl:if>
		
						<!-- dc:subject -->
						<xsl:variable name="subject" select="normalize-space(dc:subject)"/>
						<xsl:if test="$subject != ''">
							<dc:subject>
								<xsl:value-of select="$subject" />
							</dc:subject>
						</xsl:if>
					
					</oai_dc:dc>
				
				</xsl:for-each>
			</xsl:variable>
			
			<xsl:variable name="dc-length" select="string-length($dc)"/>
			
			<xsl:variable name="dc-stream-version" select="number(substring-after(foxml:datastreamVersion[last()]/@ID, '.')) + 1"/>

			<foxml:datastreamVersion ID="DC.{$dc-stream-version}" LABEL="" CREATED="{$year + 1900}-{$month}-{$day}T{$hour}:{$minute}:{$second}.{$millis}Z" MIMETYPE="text/xml" SIZE="XXXXX-SIZE-TOKEN-YYYYY">
				<foxml:xmlContent>
					<xsl:copy-of select="$dc"/>
				</foxml:xmlContent>
			</foxml:datastreamVersion>
		</xsl:copy>
		
	</xsl:template>

	<xsl:template match="person:person" mode="dc">
		<xsl:value-of select="eterms:family-name"/><xsl:text>, </xsl:text><xsl:value-of select="eterms:given-name"/>
	</xsl:template>

	<xsl:template match="organization:organization" mode="dc">
		<xsl:value-of select="dc:title"/>
	</xsl:template>

</xsl:stylesheet>