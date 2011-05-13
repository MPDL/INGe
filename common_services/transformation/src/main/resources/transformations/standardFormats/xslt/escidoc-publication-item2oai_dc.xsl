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
<xsl:stylesheet version="2.0"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}" 
	xmlns:dcmitype="${xsd.metadata.dcmitype}"

	xmlns:pub="${xsd.metadata.publication}"
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:organization="${xsd.metadata.organization}"
	>

	<xsl:import href="../../vocabulary-mappings.xsl"/>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes" />


	<xsl:template match="/*">
		<xsl:call-template name="createItem" />
	</xsl:template>


	<xsl:template name="createItem">

		<xsl:for-each select="//pub:publication">
		
			<xsl:element name="oai_dc:dc" 
				namespace="http://www.openarchives.org/OAI/2.0/oai_dc/" 
				>
				<xsl:namespace name="xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:namespace>
				<xsl:namespace name="dc">${xsd.metadata.dc}</xsl:namespace>
				<xsl:namespace name="dcterms">${xsd.metadata.dcterms}</xsl:namespace>
				<xsl:namespace name="dcmitype">${xsd.metadata.dcmitype}</xsl:namespace>

				<!-- dc:type +	-->
				<xsl:element name="dc:type">
					<xsl:value-of select="@type" />
				</xsl:element>
				
				<xsl:element name="dc:type">
					<xsl:attribute name="xsi:type">dcterms:DCMIType</xsl:attribute>
					<xsl:text>Text</xsl:text>
				</xsl:element>
				
				<xsl:if test="eterms:degree!=''">
					<xsl:element name="dc:type">
						<xsl:value-of select="eterms:degree" />
					</xsl:element>
				</xsl:if>
				
				<!-- CREATORS -->
				<xsl:for-each select="eterms:creator">
					<xsl:variable name="author_uri" select="$creator-ves/enum[.='author']/@uri" />
					<xsl:variable name="role-uri" select="@role" />
					<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-uri]" />
					
					<xsl:variable name="creatorType" select="
						if ($role='author') then 'dc:creator' 
						else if ($role = ('advisor', 'contributor', 'transcriber', translator, 'honoree')) then 'dc:contributor'
						else if (empty(following-sibling::*[@role=$author_uri]|preceding-sibling::*[@role=$author_uri])) then 'dc:creator'
						else 'dc:contributor'
					"/>

					<xsl:if test="person:person!=''">
						<xsl:element name="{$creatorType}">
							<xsl:value-of
								select="string-join((person:person/eterms:family-name, person:person/eterms:given-name), ', ' ) " />
						</xsl:element>
						<xsl:if test="person:person/organization:organization!=''">
							<xsl:element name="dc:contributor">
								<xsl:value-of select="person:person/organization:organization/dc:title" />
							</xsl:element>
						</xsl:if>
					</xsl:if>

					<xsl:if test="organization:organization!=''">
						<xsl:element name="{$creatorType}">
							<xsl:value-of select="organization:organization/dc:title" />
						</xsl:element>
					</xsl:if>

				</xsl:for-each>

				<!-- dc:title -->
				<xsl:copy-of select="dc:title" copy-namespaces="no" />

				<!-- dc:language + -->
				<xsl:copy-of select="dc:language" copy-namespaces="no" />

				<!-- dcterms:alternative -> dc:title + -->
				<xsl:for-each select="dcterms:alternative">
					<xsl:element name="dc:title">
						<xsl:value-of select="." />
					</xsl:element>
				</xsl:for-each>

				<!-- dc:identifiers +-->
				<xsl:for-each select="dc:identifier">
					<xsl:element name="dc:identifier">
						<xsl:if test="@xsi:type!=''">
							<xsl:value-of select="concat(@xsi:type, ': ')" />
						</xsl:if>
						<xsl:value-of select="." />
					</xsl:element>
				</xsl:for-each>

				<!-- dc:publisher +-->
				<xsl:copy-of select="eterms:publishing-info/dc:publisher"
					copy-namespaces="no" />

				<!-- dc:date +-->
				<xsl:variable name="date" select="
					if (dcterms:issued!='') then dcterms:issued
					else if (eterms:published-online!='') then eterms:published-online 
					else if (dcterms:dateAccepted!='') then dcterms:dateAccepted 
					else if (dcterms:dateSubmitted!='') then dcterms:dateSubmitted 
					else if (dcterms:modified!='') then dcterms:modified 
					else if (dcterms:created!='') then dcterms:created
					else '' 
				" />
				<xsl:if test="$date!=''">
					<xsl:element name="dc:date">
						<xsl:attribute name="xsi:type">dcterms:W3CDTF</xsl:attribute>
						<xsl:value-of select="$date" />
					</xsl:element>
				</xsl:if>
				
				<xsl:variable name="stype-uri" select="source:source/@type"/>
				<xsl:variable name="stype" select="$genre-ves/enum[@uri=$stype-uri]"/>
				
				<!-- dc:sources +? -->
				<xsl:variable name="source">
					<xsl:value-of select="eterms:publishing-info/eterms:place" />
					<xsl:value-of
						select="concat(' ', eterms:publishing-info/eterms:edition)" />
					<xsl:value-of select="concat(' ', source:source[1]/dc:title)" />
					<xsl:for-each
						select="source:source[1][$stype=('book', 'proceedings', 'issue', 'other')]/eterms:creator">
						<xsl:if test="./person:person!=''">
							<xsl:value-of select="concat(' ', string-join((./person:person/eterms:family-name, ./person:person/eterms:given-name), ', ' ))" />
						</xsl:if>
						<xsl:if test="./organization:organization/dc:title!=''">
							<xsl:value-of select="concat(' ', ./organization:organization/dc:title)" />
						</xsl:if>
					</xsl:for-each>
					<xsl:value-of select="concat(' ', source:source[1]/eterms:volume)" />
					<xsl:value-of select="concat(' ', source:source[1]/eterms:issue)" />
					<xsl:for-each
						select="source:source[1][@type=('journal', 'series')]/eterms:publishing-info">
						<xsl:value-of select="concat(' ', ./dc:publisher, ' ', ./eterms:place, ' ', ./eterms:edition)" />
					</xsl:for-each>
				</xsl:variable>
				<xsl:variable name="source" select="normalize-space($source)" />
				<xsl:if test="$source!=''">
					<xsl:element name="dc:source">
						<xsl:value-of select="$source" />
					</xsl:element>
				</xsl:if>

				<!-- dc:format + -->
				<xsl:variable name="format">
					<xsl:value-of
						select="concat(string-join((source:source[1]/eterms:start-page, source:source[1]/eterms:end-page), '-'), ' ', source:source[1]/eterms:total-number-of-pages )" />
				</xsl:variable>
				<xsl:variable name="format" select="normalize-space($format)" />
				<xsl:if test="$format!='' and $format!='-'">
					<xsl:element name="dc:format">
						<xsl:value-of select="$format" />
					</xsl:element>
				</xsl:if>


				<!-- dc:relation -->
				<xsl:if test="event:event!=''">
					<xsl:variable name="event-dates"
						select="string-join((event:event/eterms:start-date, event:event/eterms:end-date), '&#8212;')" />
					<xsl:variable name="event"
						select="string-join((event:event/dc:title, event:event/eterms:place, $event-dates), ', ')" />
					<xsl:if test="$event!=''">
						<xsl:element name="dc:relation">
							<xsl:value-of select="$event" />
						</xsl:element>
					</xsl:if>
				</xsl:if>

				<!-- dc:description -->
				<xsl:for-each select="dcterms:abstract, dcterms:tableOfContents">
					<xsl:variable name="desc" select="normalize-space(.)"	/>
					<xsl:if test="$desc!=''">
						<xsl:element name="dc:description">
							<xsl:value-of select="$desc" />
						</xsl:element>
					</xsl:if>
				</xsl:for-each>

				<!-- dc:subject -->
				<xsl:copy-of select="dc:subject" copy-namespaces="no" />

			</xsl:element>

		</xsl:for-each>

	</xsl:template>


</xsl:stylesheet>