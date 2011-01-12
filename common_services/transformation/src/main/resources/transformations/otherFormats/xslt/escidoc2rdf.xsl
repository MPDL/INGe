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


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from eSciDoc PubItem Schema to BibTeX
	Author: Julia Kurt (initial creation) 
	$Author: kleinfercher $ 
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:ei="http://www.escidoc.de/schemas/item/0.9"
	xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.5"
   
	xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:ec="http://www.escidoc.de/schemas/components/0.9"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	xmlns:pub="http://purl.org/escidoc/metadata/profiles/0.1/publication"
	xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source"
	xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event"
	xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person"
	xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization"
   
	xmlns:escidocContext="http://www.escidoc.de/schemas/context/0.7" 
	xmlns:escidocContextList="http://www.escidoc.de/schemas/contextlist/0.7" 


	xmlns:escidocItemList="http://www.escidoc.de/schemas/itemlist/0.9" 
	xmlns:escidocRelations="http://www.escidoc.de/schemas/relations/0.3" 
	xmlns:escidocSearchResult="http://www.escidoc.de/schemas/searchresult/0.8" 
	xmlns:srel="http://escidoc.de/core/01/structural-relations/" 
	xmlns:version="http://escidoc.de/core/01/properties/version/" 
	xmlns:release="http://escidoc.de/core/01/properties/release/" 
	xmlns:member-list="http://www.escidoc.de/schemas/memberlist/0.9" 
	xmlns:container="http://www.escidoc.de/schemas/container/0.8" 
	xmlns:container-list="http://www.escidoc.de/schemas/containerlist/0.8" 
	xmlns:struct-map="http://www.escidoc.de/schemas/structmap/0.4" 
	xmlns:mods-md="http://www.loc.gov/mods/v3" 
	xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file" 
	
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:dcam="http://purl.org/dc/dcam/">
	
	<xsl:import href="../../vocabulary-mappings.xsl"/>
	<xsl:import href="../../term_URI-mappings.xsl"/>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:variable name="profile-base-uri">http://purl.org/escidoc/metadata/profiles/0.1/</xsl:variable>
	<xsl:template match="/*">
		<xsl:element name="rdf:RDF">
			<!-- namespaces -->
			<xsl:attribute name="xml:rdf">http://www.w3.org/1999/02/22-rdf-syntax-ns#</xsl:attribute>
			<xsl:attribute name="xml:base">
				<xsl:value-of select="$profiles/profile[.='publication']/@uri"/>
			</xsl:attribute>
			<!-- create entry for each item -->
			<xsl:apply-templates select="ei:item/mdr:md-records/mdr:md-record/pub:publication"/>
		</xsl:element>					
	</xsl:template>	
	
		
	<!-- create publication -->
	<xsl:template match="ei:item/mdr:md-records/mdr:md-record/pub:publication">	
		
		<!-- PROFILE -->
		<xsl:variable name="escidoc-id" select="../../../@objid"/>		
		<xsl:variable name="publication-type" select="@type"/>
		
		<xsl:element name="rdf:Description">			
			<xsl:attribute name="rdf:about">
				<xsl:value-of select="$escidoc-id"/>
			</xsl:attribute>
			<!-- properties -->
			<!-- genre -->
			<xsl:element name="eterms:publication-type">
				<xsl:value-of select="$publication-type"/>
			</xsl:element>
			<!-- creator -->
			<xsl:apply-templates select="eterms:creator"/>
			<!-- title -->
			<xsl:element name="dc:title">
				<xsl:value-of select="dc:title"/>
			</xsl:element>
			<!-- LANGUAGE -->
			<xsl:apply-templates select="dc:language"/>
			<!-- alttitle -->
			<xsl:apply-templates select="dcterms:alternative"/>
			<!-- IDENTIFIER -->		
			<xsl:apply-templates select="dc:identifier"/>
			<!-- PUBLISHER --><!-- PUBLISHING PLACE --><!-- PUBLISHING EDITION -->
			<xsl:apply-templates select="eterms:publishing-info"/>
			<!-- DATE -->
			<xsl:apply-templates select="dcterms:created"/>
			<xsl:apply-templates select="dcterms:modified"/>
			<xsl:apply-templates select="dcterms:dateSubmitted"/>
			<xsl:apply-templates select="dcterms:dateAccepted"/>
			<xsl:apply-templates select="eterms:published-online"/>
			<xsl:apply-templates select="dcterms:issued"/>
			<!-- REVIEW METHOD -->
			<xsl:apply-templates select="eterms:review-method"/>
			<!-- COURT -->
			<xsl:apply-templates select="eterms:court"/>
			<!-- SOURCE -->
			<xsl:apply-templates select="source:source"/>
			<!-- EVENT -->
			<xsl:apply-templates select="event:event"/>
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:apply-templates select="event:total-number-of-pages"/>
			<!-- DEGREE -->
			<xsl:apply-templates select="event:degree"/>
			<!-- ABSTRACT -->
			<xsl:apply-templates select="dcterms:abstract"/>		
			<!-- SUBJECT -->
			<xsl:apply-templates select="dcterms:subject"/>
			<!-- TOC -->
			<xsl:apply-templates select="dcterms:tableOfContents"/>			
		</xsl:element>				
	</xsl:template>
	<!-- COURT -->
	<xsl:template match="eterms:court">
		<xsl:element name="eterms:court">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- REVIEW -->
	<xsl:template match="eterms:review-method">
		<xsl:element name="eterms:review-method">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- DATES -->
	<xsl:template match="dcterms:created">
		<xsl:element name="dcterms:created">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="dcterms:modified">
		<xsl:element name="dcterms:modified">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="dcterms:dateSubmitted">
		<xsl:element name="dcterms:dateSubmitted">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="dcterms:dateAccepted">
		<xsl:element name="dcterms:dateAccepted">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eterms:published-online">
		<xsl:element name="eterms:published-online">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="dcterms:issued">
		<xsl:element name="dcterms:issued">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- PUBLISHING-INFO -->
	<xsl:template match="eterms:publishing-info">
		<xsl:element name="eterms:publishing-info">
			<xsl:element name="rdf:Description">				
				<xsl:apply-templates select="dc:publisher"/>
				<xsl:apply-templates select="eterms:place"/>
				<xsl:apply-templates select="eterms:edition"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="dc:publisher">
		<xsl:element name="dc:publisher">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eterms:place">
		<xsl:element name="eterms:place">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eterms:edition">
		<xsl:element name="eterms:edition">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- ALT TITLE -->
	<xsl:template match="dcterms:alternative">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- CREATOR -->
	<xsl:template match="eterms:creator">
		<xsl:element name="eterms:creator">
			<xsl:apply-templates select="person:person"/>
			<xsl:apply-templates select="organization:organization"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="person:person">
		<xsl:element name="rdf:Description">
			<xsl:attribute name="rdf:about">
				<xsl:value-of select="$profiles/profile[.='person']/@uri"/>
			</xsl:attribute>
			<!-- name -->
			<xsl:apply-templates select="eterms:complete-name"/>
			<xsl:apply-templates select="eterms:given-name"/>
			<xsl:apply-templates select="eterms:family-name"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="organization:organization">
		<xsl:element name="rdf:Description">
			<xsl:attribute name="rdf:about">
				<xsl:value-of select="$profiles/profile[.='organization']/@uri"/>
			</xsl:attribute>
			<!-- name -->
			<xsl:apply-templates select="dc:title"/>
			<xsl:apply-templates select="eterms:address"/>
			<xsl:apply-templates select="dc:identifier"/>
		</xsl:element>
	</xsl:template>
	
	<!-- ORGANIZATION -->
	<xsl:template match="dc:title">
		<xsl:element name="dc:title">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eterms:address">
		<xsl:element name="eterms:address">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- PERSON NAMES -->
	<xsl:template match="eterms:complete-name">
		<xsl:element name="eterms:complete-name">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eterms:given-name">
		<xsl:element name="eterms:given-name">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eterms:family-name">
		<xsl:element name="eterms:family-name">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- LANGUAGE -->
	<xsl:template match="dc:language">
		<xsl:element name="dc:language">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- IDENTIFIER -->
	<xsl:template match="dc:identifier">							
		<xsl:element name="dc:identifier">
			<xsl:choose>
			<xsl:when test="@xsi:type">
			<xsl:element name="rdf:Description">
				<xsl:element name="dcam:memberOf">
					<xsl:attribute name="rdf:resource">
						<xsl:value-of select="@xsi:type"/>
					</xsl:attribute>
				</xsl:element>
				<xsl:element name="rdf:value">    			
    				<xsl:value-of select="."/>    			
    			</xsl:element>	
			</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="rdf:Description">
				<xsl:element name="dcam:memberOf">
					<xsl:attribute name="rdf:resource">
						<xsl:value-of>eterms:OTHER</xsl:value-of>
					</xsl:attribute>
				</xsl:element>
				<xsl:element name="rdf:value">    			
    				<xsl:value-of select="."/>    			
    			</xsl:element>	
			</xsl:element>
			</xsl:otherwise>
			</xsl:choose>
		</xsl:element>
	</xsl:template>
	<!-- EVENT -->
	<xsl:template match="event">
		<xsl:element name="eterms:event">
			<xsl:element name="rdf:Description">
				<xsl:apply-templates select="dc:title"/>
				<xsl:apply-templates select="dcterms:alternative"/>
				<xsl:apply-templates select="eterms:start-date"/>
				<xsl:apply-templates select="eterms:end-date"/>
				<xsl:apply-templates select="eterms:place"/>
				<xsl:apply-templates select="eterms:invitation-status"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eterms:start-date">
		<xsl:element name="eterms:start-date">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="eterms:end-date">
		<xsl:element name="eterms:end-date">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="eterms:invitation-status">
		<xsl:element name="eterms:invitation-status">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- TOTAL NUMBER OF PAGES -->
	<xsl:template match="eterms:total-number-of-pages">
		<xsl:element name="eterms:total-number-of-pages">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- DEGREE -->
	<xsl:template match="eterms:degree">
		<xsl:element name="eterms:degree">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- ABSTRACT -->
	<xsl:template match="dcterms:abstract">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>		
	<!-- SUBJECT -->
	<xsl:template match="dcterms:subject">
		<xsl:element name="dcterms:subject">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- TOC -->
	<xsl:template match="dcterms:tableOfContents">
		<xsl:element name="dcterms:tableOfContents">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SOURCE -->
	<xsl:template match="source:source">
		<xsl:element name="eterms:source">
			<xsl:element name="rdf:Description">
				<!-- genre -->
				<xsl:element name="eterms:publication-type">
					<xsl:value-of select="@type"/>
				</xsl:element>
				<xsl:apply-templates select="dc:title"/>
				<xsl:apply-templates select="dcterms:alternative"/>
				<xsl:apply-templates select="eterms:creator"/>
				<xsl:apply-templates select="eterms:volume"/>
				<xsl:apply-templates select="eterms:issue"/>
				<xsl:apply-templates select="dcterms:issued"/>
				<xsl:apply-templates select="eterms:start-page"/>
				<xsl:apply-templates select="eterms:end-page"/>
				<xsl:apply-templates select="eterms:sequence-number"/>
				<xsl:apply-templates select="eterms:total-number-of-pages"/>			
				<xsl:apply-templates select="eterms:publishing-info"/>
				<xsl:apply-templates select="dc:identifier"/>
				<xsl:apply-templates select="source:source"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- VOL -->
	<xsl:template match="eterms:volume">
		<xsl:element name="eterms:volume">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- ISSUE -->
	<xsl:template match="eterms:issue">
		<xsl:element name="eterms:issue">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SP -->
	<xsl:template match="eterms:start-page">
		<xsl:element name="eterms:start-page">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- EP -->
	<xsl:template match="eterms:end-page">
		<xsl:element name="eterms:end-page">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	<!-- SEQ NO -->
	<xsl:template match="eterms:sequence-number">
		<xsl:element name="eterms:sequence-number">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
		
</xsl:stylesheet>

