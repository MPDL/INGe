<?xml version="1.0" encoding="UTF-8"?>
	<!--
		CDDL HEADER START The contents of this file are subject to the terms
		of the Common Development and Distribution License, Version 1.0 only
		(the "License"). You may not use this file except in compliance with
		the License. You can obtain a copy of the license at
		license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the
		License for the specific language governing permissions and
		limitations under the License. When distributing Covered Code, include
		this CDDL HEADER in each file and include the License file at
		license/ESCIDOC.LICENSE. If applicable, add the following below this
		CDDL HEADER, with the fields enclosed by brackets "[]" replaced with
		your own identifying information: Portions Copyright [yyyy] [name of
		copyright owner] CDDL HEADER END Copyright 2006-2010
		Fachinformationszentrum Karlsruhe Gesellschaft für
		wissenschaftlich-technische Information mbH and Max-Planck-
		Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved.
		Use is subject to license terms.
	-->
	<!--
		Transformations from eSciDoc PubItem to OAI DC See mapping:
		http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_Dublin_Core_Mapping
		Author: Vlad Makarenko (initial creation) $Author: $ (last changed)
		$Revision: $ $LastChangedDate: $
	-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="${xsd.metadata.dc}" xmlns:dcterms="${xsd.metadata.dcterms}" xmlns:dcmitype="${xsd.metadata.dcmitype}" xmlns:pub="${xsd.metadata.publication}" xmlns:eterms="${xsd.metadata.terms}" xmlns:source="${xsd.metadata.source}" xmlns:event="${xsd.metadata.event}" xmlns:person="${xsd.metadata.person}" xmlns:organization="${xsd.metadata.organization}">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:param name="ID"/>
	
	<xsl:template match="/">
		
		<xsl:for-each select="//pub:publication">
			
			<oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="${xsd.metadata.dc}" xmlns:dcterms="${xsd.metadata.dcterms}" xmlns:dcmitype="${xsd.metadata.dcmitype}">

				<!-- dc:type +	-->
				<dc:type>
					<xsl:value-of select="@type" />
				</dc:type>
				
				<dc:type xsi:type="dcterms:DCMIType">Text</dc:type>
				
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
				<xsl:copy-of select="dc:title"/>

				<!-- dc:language + -->
				<xsl:copy-of select="dc:language" copy-namespaces="no" />

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
				<xsl:copy-of select="eterms:publishing-info/dc:publisher" copy-namespaces="no" />

				<!-- dc:date +-->
				<xsl:variable name="date" select=" if (dcterms:issued != '') then dcterms:issued else if (eterms:published-online!='') then eterms:published-online else if (dcterms:dateAccepted!='') then dcterms:dateAccepted else if (dcterms:dateSubmitted!='') then dcterms:dateSubmitted else if (dcterms:modified!='') then dcterms:modified else if (dcterms:created!='') then dcterms:created else '' " />
				
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
					<dc:date xsi:type="dcterms:W3CDTF">
						<xsl:value-of select="$date" />
					</dc:date>
				</xsl:if>
				
				<xsl:variable name="stype-uri" select="source:source/@type"/>
				<xsl:variable name="stype" select="$genre-ves/enum[@uri=$stype-uri]"/>
				
				<!-- dc:sources +? -->
				<xsl:variable name="source">
					<xsl:value-of select="eterms:publishing-info/eterms:place" />
					<xsl:value-of select="concat(' ', eterms:publishing-info/eterms:edition)" />
					<xsl:value-of select="concat(' ', source:source[1]/dc:title)" />
					<xsl:for-each select="source:source[1][$stype=('book', 'proceedings', 'issue', 'other')]/eterms:creator">
						<xsl:if test="./person:person!=''">
							<xsl:value-of select="concat(' ', string-join((./person:person/eterms:family-name, ./person:person/eterms:given-name), ', ' ))" />
						</xsl:if>
						<xsl:if test="./organization:organization/dc:title!=''">
							<xsl:value-of select="concat(' ', ./organization:organization/dc:title)" />
						</xsl:if>
					</xsl:for-each>
					<xsl:value-of select="concat(' ', source:source[1]/eterms:volume)" />
					<xsl:value-of select="concat(' ', source:source[1]/eterms:issue)" />
					<xsl:for-each select="source:source[1][@type=('journal', 'series')]/eterms:publishing-info">
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
					<xsl:value-of select="concat(string-join((source:source[1]/eterms:start-page, source:source[1]/eterms:end-page), '-'), ' ', source:source[1]/eterms:total-number-of-pages )" />
				</xsl:variable>
				<xsl:variable name="format" select="normalize-space($format)" />
				<xsl:if test="$format!='' and $format!='-'">
					<dc:format>
						<xsl:value-of select="$format" />
					</dc:format>
				</xsl:if>

				<!-- dc:relation -->
				<xsl:if test="event:event!=''">
					<xsl:variable name="event-dates" select="string-join((event:event/eterms:start-date, event:event/eterms:end-date), '&#8212;')" />
					<xsl:variable name="event" select="string-join((event:event/dc:title, event:event/eterms:place, $event-dates), ', ')" />
					<xsl:if test="$event!=''">
						<dc:relation>
							<xsl:value-of select="$event" />
						</dc:relation>
					</xsl:if>
				</xsl:if>

				<!-- dc:description -->
				<xsl:for-each select="dcterms:abstract, dcterms:tableOfContents">
					<xsl:variable name="desc" select="normalize-space(.)"/>
					<xsl:if test="$desc!=''">
						<dc:description>
							<xsl:value-of select="$desc" />
						</dc:description>
					</xsl:if>
				</xsl:for-each>

				<!-- dc:subject -->
				<xsl:copy-of select="dc:subject" copy-namespaces="no" />
			
			</oai_dc:dc>
		</xsl:for-each>
	
	</xsl:template>
	
	<xsl:template match="person:person">
		<xsl:choose>
			<xsl:when test="exists(person:person)">
				<xsl:value-of select="person:person/eterms:family-name"/>
				<xsl:text>, </xsl:text>
				<xsl:value-of select="person:person/eterms:given-name"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="">
		<xsl:value-of select="dc:title" />
	</xsl:template>

</xsl:stylesheet>