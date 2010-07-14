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
<!-- 
	Transformations from eSciDoc PubItem Schema to EndNote format 10/11
	Author: Julia Kurt (initial creation) 
	$Author$ (last changed)
	$Revision$  
	$LastChangedDate$
-->
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
	xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file" >

	
	<xsl:import href="vocabulary-mappings.xsl"/>	
	
<xsl:output method="text" encoding="UTF-8" indent="yes"/>

	
	<xsl:template match="/*">			
		<!-- create entry for each item -->
		<xsl:choose>
			<xsl:when test="count(//pub:publication)>0">
				<xsl:apply-templates select="//pub:publication"/>	
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoItemsForTransforamtion' ), 'Empty item list')"/>
			</xsl:otherwise>
		</xsl:choose>				
	</xsl:template>	
	
	<!-- create entry -->
	<xsl:template match="pub:publication">	
		<xsl:variable name="genre-uri" select="@type"/>	
		<xsl:variable name="genre" select="$genre-ves/enum[@uri=$genre-uri]"/>
		
		<!-- detect entry type -->		
		<xsl:choose>
			<xsl:when test="$genre='article'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Journal Article</xsl:with-param>					
				</xsl:call-template>				
			</xsl:when>		
			<xsl:when test="$genre='book'">				
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Book</xsl:with-param>					
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='book-item'">				
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Book Section</xsl:with-param>					
				</xsl:call-template>
			</xsl:when>	
			<xsl:when test="$genre='proceedings'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Conference Proceedings</xsl:with-param>					
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='conference-paper' or $genre='proceedings-paper'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Conference Paper</xsl:with-param>					
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='thesis'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Thesis</xsl:with-param>					
				</xsl:call-template>								
			</xsl:when>
			<xsl:when test="$genre='report'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Report</xsl:with-param>					
				</xsl:call-template>				
			</xsl:when>			
			<xsl:when test="$genre='manuscript'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Manuscript</xsl:with-param>					
				</xsl:call-template>								
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">Generic</xsl:with-param>					
				</xsl:call-template>
			</xsl:otherwise>			
		</xsl:choose>
	</xsl:template>
	
	<!-- create entry -->
	<xsl:template name="createEntry">
		<xsl:param name="entryType"/>
		<xsl:variable name="genre-uri" select="@type"/>
		<xsl:variable name="genre" select="$genre-ves/enum[@uri=$genre-uri]"/>		
		<!-- GENRE -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag" select="'0'"/>
			<xsl:with-param name="value" select="$entryType"/>
		</xsl:call-template>
		
		<!-- AUTHOR -->
		<xsl:apply-templates select="eterms:creator">
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		
		<!-- TITLE -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">T</xsl:with-param>
			<xsl:with-param name="value" select="dc:title"/>
		</xsl:call-template>
		<!-- ALTTITLE -->
		<xsl:if test="$genre='report'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">O</xsl:with-param>
				<xsl:with-param name="value" select="dcterms:alternative"/>
			</xsl:call-template>		
		</xsl:if>
		
		<!-- LANGUAGE -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">G</xsl:with-param>
			<xsl:with-param name="value" select="dc:language"/>
		</xsl:call-template>
		
		<!-- IDENTIFIER -->
		 <xsl:apply-templates select="dc:identifier">
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		
		<!-- PUBLISHER -->
		<xsl:choose>
			<xsl:when test="not($genre='report')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">I</xsl:with-param>
					<xsl:with-param name="value" select="eterms:publishing-info/dc:publisher"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">Y</xsl:with-param>
					<xsl:with-param name="value" select="eterms:publishing-info/dc:publisher"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- PUBLISHING PLACE -->
		<xsl:if test="not($genre='conference-paper' or $genre='proceedings')">
			<xsl:call-template name="print-line">
					<xsl:with-param name="tag">C</xsl:with-param>
					<xsl:with-param name="value" select="eterms:publishing-info/eterms:place"/>
				</xsl:call-template>
		</xsl:if>
		<!-- PUBLISHING EDITION -->
		<xsl:choose>
			<xsl:when test="not($genre='article')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">7</xsl:with-param>
					<xsl:with-param name="value" select="eterms:publishing-info/eterms:edition"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">7</xsl:with-param>
					<xsl:with-param name="value" select="eterms:published-online"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- DATE -->
		<xsl:choose>
			<xsl:when test="dcterms:issued">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">D</xsl:with-param>
					<xsl:with-param name="value" select="dcterms:issued"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="eterms:published-online">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">D</xsl:with-param>
					<xsl:with-param name="value" select="eterms:published-online"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dcterms:dateAccepted">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">D</xsl:with-param>
					<xsl:with-param name="value" select="dcterms:dateAccepted"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dcterms:dateSubmitted">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">D</xsl:with-param>
					<xsl:with-param name="value" select="dcterms:dateSubmitted"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dcterms:modified">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">D</xsl:with-param>
					<xsl:with-param name="value" select="dcterms:modified"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dcterms:created">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">D</xsl:with-param>
					<xsl:with-param name="value" select="dcterms:created"/>
				</xsl:call-template>
			</xsl:when>			
		</xsl:choose>
		
		<!-- REVIEW METHOD -->
		<xsl:variable name="review-method-uri" select="eterms:review-method"/>
		<xsl:variable name="review-method" select="concat('Review method: ',$reviewMethod-ves/enum[@uri=$review-method-uri])"/>
		<xsl:if test="$review-method-uri!=''">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">Z</xsl:with-param>
				<xsl:with-param name="value" select="$review-method"/>
			</xsl:call-template>
		</xsl:if>
		
		<!-- EVENT -->
		<xsl:apply-templates select="event:event">
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		<!-- TOTAL NUMBER OF PAGES -->
		<xsl:if test="$genre='book' or $genre='manuscript' or $genre='proceedings' or $genre='report' or $genre='thesis'">
			<xsl:call-template name="print-line">
					<xsl:with-param name="tag">P</xsl:with-param>
					<xsl:with-param name="value" select="eterms:total-number-of-pages"/>
				</xsl:call-template>
		</xsl:if>
		<!-- DEGREE -->
		<xsl:if test="$genre='thesis'">
			<xsl:call-template name="print-line">
					<xsl:with-param name="tag">V</xsl:with-param>
					<xsl:with-param name="value" select="eterms:degree"/>
				</xsl:call-template>
		</xsl:if>
		<!-- ABSTRACT -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">X</xsl:with-param>
			<xsl:with-param name="value" select="dcterms:abstract"/>
		</xsl:call-template>
		<!-- SUBJECT -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">K</xsl:with-param>
			<xsl:with-param name="value" select="dc:subject"/>
		</xsl:call-template>
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">K</xsl:with-param>
			<xsl:with-param name="value" select="dcterms:subject"/>
		</xsl:call-template>
		<!-- TOC -->
		<xsl:if test="$genre='report'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">(</xsl:with-param>
				<xsl:with-param name="value" select="dcterms:tableOfContents"/>
			</xsl:call-template>
		</xsl:if>
		<!-- ESCIDOC ID -->
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">M</xsl:with-param>
			<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:ESCIDOC']"/>
		</xsl:call-template>
		
		<!-- SOURCE -->
		<xsl:apply-templates select="source:source">
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		
		<!-- new lines at the end of the entry -->
		<xsl:value-of select="'&#13;&#10;'"/>
		<xsl:value-of select="'&#13;&#10;'"/>
	</xsl:template>	





	
	<!-- CREATOR -->
	<xsl:template match="eterms:creator">
		<xsl:param name="genre"/>
		
		<xsl:variable name="role-uri" select="@role"/>
		<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-uri]"/>
		
		<xsl:apply-templates select="person:person">
			<xsl:with-param name="role" select="$role"/>
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="organization:organization">
			<xsl:with-param name="role" select="$role"/>
			<xsl:with-param name="genre" select="$genre"/>
		</xsl:apply-templates>
	</xsl:template>
	<!-- PERSON -->
	<xsl:template match="person:person">
		<xsl:param name="role"/>
		<xsl:param name="genre"/>
		<xsl:variable name="name" select="concat(eterms:family-name, ', ',eterms:given-name)"/>
		<xsl:choose>
			<xsl:when test="$role='author'">
				
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and $genre='book'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and not($genre='book')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'E'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='artist'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='painter'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='photographer'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='illustrator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='commentator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='transcriber'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='contributor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='advisor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'Y'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='translator' and not($genre='proceedings' or $genre='report')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'?'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>	
			<xsl:when test="$role='translator' and ($genre='proceedings' or $genre='report')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'Z'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>			
		</xsl:choose>		
	</xsl:template>
	<!-- ORGANIZATION -->
	<xsl:template match="organization:organization">
		<xsl:param name="role"/>
		<xsl:param name="genre"/>
		<xsl:variable name="name" select="concat(dc:title, ', ',eterms:address)"/>
		<xsl:choose>
			<xsl:when test="$role='author'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and $genre='book'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='editor' and not($genre='book')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'E'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='artist'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='painter'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='photographer'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='illustrator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='commentator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='transcriber'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='contributor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='advisor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($role,': ',$name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$role='translator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'?'"/>
					<xsl:with-param name="value" select="$name"/>
				</xsl:call-template>
			</xsl:when>	
		</xsl:choose>
		
	</xsl:template>


	<!-- IDENTIFIER -->
	<xsl:template match="dc:identifier">
		<xsl:param name="genre"/>
		<xsl:choose>
			<xsl:when test="@xsi:type='eterms:DOI'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">R</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:ISBN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">@</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:ISSN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">@</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:URI'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">U</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:URN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">U</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:EDOC'">
				<xsl:variable name="idstring" select="concat('EDOC: ',.)"/>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">F</xsl:with-param>
					<xsl:with-param name="value" select="$idstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:ISI'">
				<xsl:variable name="idstring" select="concat('ISI: ',.)"/>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">F</xsl:with-param>
					<xsl:with-param name="value" select="$idstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:PND'">
				<xsl:variable name="idstring" select="concat('PND: ',.)"/>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">F</xsl:with-param>
					<xsl:with-param name="value" select="$idstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:OTHER'">
				<xsl:variable name="idstring" select="concat('OTHER: ',.)"/>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">F</xsl:with-param>
					<xsl:with-param name="value" select="$idstring"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@xsi:type='eterms:PMC' and $genre='article'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">2</xsl:with-param>
					<xsl:with-param name="value" select="."/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- EVENT -->
	<xsl:template match="event:event">
		<xsl:param name="genre"/>
		<!-- TITLE -->
		<xsl:choose>
			<xsl:when test="$genre='proceedings-paper' or $genre='conference-paper' or $genre='proceedings'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">B</xsl:with-param>
					<xsl:with-param name="value" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">Z</xsl:with-param>
					<xsl:with-param name="value" select="concat('name of event: ',dc:title)"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- DATE -->
		<xsl:variable name="event-date" select="concat(eterms:start-date,' - ',eterms:end-date)"/>
		<xsl:call-template name="print-line">
			<xsl:with-param name="tag">Z</xsl:with-param>
			<xsl:with-param name="value" select="concat('date od event: ',$event-date)"/>
		</xsl:call-template>
		<xsl:if test="$genre='proceedings'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">D</xsl:with-param>
				<xsl:with-param name="value" select="substring-before(eterms:start-date,'-')"/>
			</xsl:call-template>
		</xsl:if>
		<!-- PLACE -->
		<xsl:choose>
			<xsl:when test="$genre='proceedings' or $genre='proceedings-paper' or $genre='conference-paper'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">C</xsl:with-param>
					<xsl:with-param name="value" select="eterms:place"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">Z</xsl:with-param>
					<xsl:with-param name="value" select="concat('place of event: ',eterms:place)"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>

	<!-- SOURCE -->
	<xsl:template match="source:source">
		<xsl:param name="genre"/>
		<xsl:variable name="sgenre-uri" select="@type"/>
		<xsl:variable name="sgenre" select="$genre-ves/enum[@uri=$sgenre-uri]"/>
		<!-- TITLE -->
		<xsl:choose>
			<xsl:when test="($sgenre='book' or $sgenre='proceedings' or $sgenre='issue' or $sgenre='series') and not($genre='proceedings')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">B</xsl:with-param>
					<xsl:with-param name="value" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='proceedings' and $sgenre='series'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">S</xsl:with-param>
					<xsl:with-param name="value" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$sgenre='journal'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">J</xsl:with-param>
					<xsl:with-param name="value" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		<!-- ALTTITLE -->
		<xsl:if test="$genre='article'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">O</xsl:with-param>
				<xsl:with-param name="value" select="dcterms:alternative"/>
			</xsl:call-template>
		</xsl:if>
		<!-- CREATOR -->
		<xsl:if test="not($genre='article')">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">E</xsl:with-param>
				<xsl:with-param name="value">
					<xsl:for-each select="eterms:creator/person:person">
						<xsl:value-of select="
							string-join(
								(
									eterms:family-name[.!=''], 
									eterms:given-name[.!='']
								), 
								', '
							)
						"/>
						<xsl:value-of select="if (position()!=last()) then '; ' else ''" />
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<!-- VOLUME -->
		<xsl:choose>
			<xsl:when test="$sgenre='series' and not($genre='proceedings')">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">N</xsl:with-param>
					<xsl:with-param name="value" select="eterms:volume"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">V</xsl:with-param>
					<xsl:with-param name="value" select="eterms:volume"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- ISSUE -->
		<xsl:if test="not($sgenre='series')">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">N</xsl:with-param>
				<xsl:with-param name="value" select="eterms:issue"/>
			</xsl:call-template>
		</xsl:if>
		<!-- PAGES -->		
		<xsl:if test="$genre='article' or $genre='manuscript'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag" select="'&amp;'"/>
				<xsl:with-param name="value" select="eterms:start-page"/>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not($genre='book')">
			<xsl:variable name="pages">
				<xsl:value-of select="eterms:start-page"/>
				<xsl:if test="eterms:end-page!=''">					
					<xsl:value-of select="concat(' - ',eterms:end-page)"/>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">P</xsl:with-param>
				<xsl:with-param name="value" select="$pages"/>
			</xsl:call-template>
		</xsl:if>
		<!-- SEQ NO -->
		<xsl:if test="eterms:sequence-number!=''">
			<xsl:choose>
				<xsl:when test="$genre='report' or $genre='manuscript'">
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag">N</xsl:with-param>
						<xsl:with-param name="value" select="eterms:sequence-number"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$genre='book-item'">
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="'&amp;'"/>
						<xsl:with-param name="value" select="eterms:sequence-number"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="seq-no" select="concat('sequence number: ',eterms:sequence-number)"/>
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag">Z</xsl:with-param>
						<xsl:with-param name="value" select="$seq-no"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<!-- PUBLISHER -->
		<xsl:if test="$genre='book-item' or $genre='conference-paper' or $genre='proceedings-paper' or $genre='article'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">I</xsl:with-param>
				<xsl:with-param name="value" select="eterms:publishing-info/dc:publisher"/>
			</xsl:call-template>
		</xsl:if>
		<!-- PLACE -->
		<xsl:if test="$genre='book-item' or $genre='article'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">C</xsl:with-param>
				<xsl:with-param name="value" select="eterms:publishing-info/eterms:place"/>
			</xsl:call-template>
		</xsl:if>
		<!-- EDITION -->
		<xsl:if test="$genre='article'">
			<xsl:call-template name="print-line">
				<xsl:with-param name="tag">7</xsl:with-param>
				<xsl:with-param name="value" select="eterms:publishing-info/eterms:edition"/>
			</xsl:call-template>
		</xsl:if>
		<!-- IDENTIFIER -->
		<xsl:choose>
			<xsl:when test="dc:identifier/@xsi:type='eterms:ISSN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">@</xsl:with-param>
					<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:ISSN']"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dc:identifier/@xsi:type='eterms:ISBN'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">@</xsl:with-param>
					<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:ISBN']"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dc:identifier/@xsi:type='eterms:URI' and not(../dc:identifier[@xsi:type='eterms:URI'or @xsi:type='eterms:URN'])">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">U</xsl:with-param>
					<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:URI' or @xsi:type='eterms:URN']"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="dc:identifier/@xsi:type='eterms:DOI' and not(../dc:identifier[@xsi:type='eterms:DOI'])">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag">R</xsl:with-param>
					<xsl:with-param name="value" select="dc:identifier[@xsi:type='eterms:DOI']"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>





	<!-- TEMPLATES -->
	<!-- Prints result line in EndNote format -->
	<xsl:template name="print-line">
		<xsl:param name="tag"/>
		<xsl:param name="value"/>
		<xsl:variable name="strn" select="$value"/>
		<xsl:if test="$tag!='' and $strn!=''">
			<xsl:value-of select="concat('%', $tag,' ')"/>
			<xsl:value-of select="$strn"/>
			<xsl:value-of select="'&#13;&#10;'"/>
		</xsl:if>
	</xsl:template>
	
	
	<!-- TEMPLATES END-->

</xsl:stylesheet>