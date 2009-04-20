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
<!-- 
	Transformations from eSciDoc PubItem Schema to BibTeX
	Author: Julia Kurt (initial creation) 
	$Author: jkurt $ 
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
xmlns:xs="http://www.w3.org/2001/XMLSchema" 
xmlns:fn="http://www.w3.org/2005/xpath-functions"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
 xmlns:func="urn:my-functions"
 
 xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:prop="http://escidoc.de/core/01/properties/"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"   
   xmlns:ec="http://www.escidoc.de/schemas/components/0.7"
   xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file"
   xmlns:pub="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"  
    xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.4"
   xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
   xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
   xmlns:ei="http://www.escidoc.de/schemas/item/0.7"
   xmlns:eidt="http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes"
   xmlns:srel="http://escidoc.de/core/01/structural-relations/">
	
	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	
	
	<xsl:template match="/*">			
		<!-- create entry for each item -->
			<xsl:apply-templates select="ei:item/mdr:md-records/mdr:md-record/mdp:publication"/>				
	</xsl:template>	
	
	<!-- create ris entry -->
	<xsl:template match="ei:item/mdr:md-records/mdr:md-record/mdp:publication">		
		<xsl:param name="genre" select="@type"/>
		
		<!-- detect ris entry type -->		
		<xsl:choose>
			<xsl:when test="$genre='article'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">JOUR</xsl:with-param>
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='journal'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">JFULL</xsl:with-param>
				</xsl:call-template>								
			</xsl:when>
			<xsl:when test="$genre='proceedings'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">CONF</xsl:with-param>
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='conference-paper'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">CHAP</xsl:with-param>
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='book'">				
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">BOOK</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='book-item'">				
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">CHAP</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='report'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">RPRT</xsl:with-param>
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='thesis'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">THES</xsl:with-param>
				</xsl:call-template>								
			</xsl:when>
			<xsl:when test="$genre='manuscript'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">UNPB</xsl:with-param>
				</xsl:call-template>								
			</xsl:when>
			<xsl:when test="$genre='poster' or $genre='courseware/lecture' or $genre='paper' or $genre='issue' or $genre='other'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">GEN</xsl:with-param>
				</xsl:call-template>								
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!-- create ris entry -->
	<xsl:template name="createEntry">
		<xsl:param name="entryType"/>
		
		<!-- GENRE -->
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'TY'"/>
			<xsl:with-param name="xpath" select="$entryType"/>
		</xsl:call-template>
		
		<!-- IDENTIFIER -->
		<xsl:apply-templates select="dc:identifier[@xsi:type='eidt:ESCIDOC']"/>
		<xsl:apply-templates select="dc:identifier[@xsi:type='eidt:OTHER']"/>
		<!-- TITLE -->
		<xsl:apply-templates select="dc:title"/>
		<!-- CREATOR -->
		
		
		<xsl:if test="count(pub:creator[@role='author']/e:person)!=0 or count(pub:creator[@role='author']/e:organization)!=0">
				<xsl:variable name="creators">
				<xsl:for-each select="pub:creator[@role='author']/e:person">
					<xsl:value-of select="concat(e:complete-name,'; ')"/>
				</xsl:for-each>
				<xsl:for-each select="pub:creator[@role='author']/e:organization">
					<xsl:value-of select="concat(e:organization-name, '; ')"/>
				</xsl:for-each>
				<xsl:for-each select="pub:source/e:creator[@role='author']/e:person">
					<xsl:value-of select="concat(e:complete-name,'; ')"/>
				</xsl:for-each>
				<xsl:for-each select="pub:source/e:creator[@role='author']/e:organization">
					<xsl:value-of select="concat(e:organization-name, '; ')"/>
				</xsl:for-each>
				</xsl:variable>
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'AU'"/>
					<xsl:with-param name="xpath" select="$creators"/>
				</xsl:call-template>
			</xsl:if>		
			<xsl:if test="count(pub:creator[@role!='author']/e:person)!=0 or count(pub:creator[@role!='author']/e:organization)!=0">
				<xsl:variable name="creators">
				<xsl:for-each select="pub:creator[@role!='author']/e:person">
					<xsl:value-of select="concat(e:complete-name,'; ')"/>
				</xsl:for-each>
				<xsl:for-each select="pub:creator[@role!='author']/e:organization">
					<xsl:value-of select="concat(e:organization-name, '; ')"/>
				</xsl:for-each>
				</xsl:variable>
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'ED'"/>
					<xsl:with-param name="xpath" select="$creators"/>
				</xsl:call-template>
					
		</xsl:if>
		
		
		
		<!-- SUBJECT -->
		<xsl:apply-templates select="dcterms:subject"/>
		<!-- PAGES -->
		<xsl:apply-templates select="pub:source/e:start-page"/>
		<xsl:apply-templates select="pub:source/e:end-page"/>
			
		<!-- PUBLISHING PLACE -->
		<xsl:choose>
			<xsl:when test="pub:publishing-info/dc:publisher">
				<xsl:apply-templates select="pub:publishing-info"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="pub:source/e:publishing-info"/>
			</xsl:otherwise>
		</xsl:choose>
		<!-- ABSTRACT -->
		<xsl:apply-templates select="dcterms:abstract"/>		
				
		<!-- DEGREE -->
		<xsl:apply-templates select="pub:degree"/>		
		<!-- CREATOR ORGA -->		
		<xsl:if test="count(pub:creator/e:person/e:organization)!=0 or count(pub:creator/e:organization)!=0">
				<xsl:variable name="creators">
				<xsl:for-each select="pub:creator/e:person/e:organization">
					<xsl:value-of select="concat(e:organization-name)"/>
					<xsl:choose>
					<xsl:when test="e:organization-address">
						<xsl:value-of select="concat(', ', e:organization-address,'; ')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'; '"/>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				<xsl:for-each select="pub:creator/e:organization">
					<xsl:value-of select="concat(e:organization-name)"/>
					<xsl:choose>
					<xsl:when test="e:organization-address">
						<xsl:value-of select="concat(', ', e:organization-address,'; ')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'; '"/>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				<xsl:for-each select="pub:source/e:creator/e:person/e:organization">
					<xsl:value-of select="concat(e:organization-name)"/>
					<xsl:choose>
					<xsl:when test="e:organization-address">
						<xsl:value-of select="concat(', ', e:organization-address,'; ')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'; '"/>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				<xsl:for-each select="pub:source/e:creator/e:organization">
					<xsl:value-of select="concat(e:organization-name)"/>
					<xsl:choose>
					<xsl:when test="e:organization-address">
						<xsl:value-of select="concat(', ', e:organization-address,'; ')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'; '"/>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				</xsl:variable>
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'AD'"/>
					<xsl:with-param name="xpath" select="$creators"/>
				</xsl:call-template>
					
		</xsl:if>
		<!-- VOLUME -->
		<xsl:choose>
			<xsl:when test="pub:source/e:volume">
				<xsl:apply-templates select="pub:source/e:volume"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'VL'"/>
					<xsl:with-param name="xpath" select="pub:publishing-info/e:edition"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>	
		<!-- ISSUE -->
		<xsl:apply-templates select="pub:source/e:issue"/>		
		<!-- DATE -->
		<xsl:apply-templates select="dcterms:created"/>
		
		<!-- SOURCE TITLE -->
		<xsl:apply-templates select="pub:source/dc:title">
			<xsl:with-param name="genre" select="@type"/>
		</xsl:apply-templates>
		<!-- SOURCE CREATOR -->
		<xsl:choose>
			<xsl:when test="@type='book' or @type='proceedings' or @type='thesis' or @type='manuscript' or @type='journal' or @type='series'">
				<xsl:apply-templates select="pub:source/e:creator/e:organization"/>
			</xsl:when>
			<xsl:otherwise>
				
			</xsl:otherwise>
		</xsl:choose>
		<!-- SOURCE IDENTIFIER -->
		<xsl:apply-templates select="pub:source/dc:identifier[@xsi:type='eidt:ISSN' or @xsi:type='eidt:ISBN']"/>
		<!-- N1 -->
		<xsl:variable name="n1">
			<!-- LANGUAGE -->
			<xsl:if test="dc:language">
				<xsl:value-of select="concat('Language: ',dc:language,'; ')"/>
			</xsl:if>
			<!-- REVIEW METHOD -->
			<xsl:if test="pub:review-method">
				<xsl:value-of select="concat('Review Method:',pub:review-method,'; ')"/>
			</xsl:if>
			<!-- SEQ NR -->
			<xsl:if test="pub:source/e:sequence-number">
				<xsl:value-of select="concat('Sequence Number:',pub:source/e:sequence-number,'; ')"/>
			</xsl:if>
			<!-- EVENT -->
			<xsl:if test="pub:event">
				<xsl:value-of select="concat('Event:',pub:event/dc:title)"/>
				<xsl:if test="pub:event/dcterms:alternative">
					<xsl:value-of select="concat(pub:event/dcterms:alternative,', ')"/>
				</xsl:if>
				<xsl:if test="pub:event/e:start-date">
					<xsl:value-of select="pub:event/e:start-date"/>
					<xsl:if test="pub:event/e:end-date">
						<xsl:value-of select="concat('to',pub:event/e:end-date,',')"/>
					</xsl:if>
				</xsl:if>
				<xsl:if test="pub:event/e:place">
					<xsl:value-of select="concat(pub:event/e:place,';')"/>
				</xsl:if>
			</xsl:if>
			<!-- TOC -->
			<xsl:if test="dcterms:tableOfContents">
				<xsl:value-of select="concat('Table of Contents:',dcterms:tableOfContents,'; ')"/>
			</xsl:if>
			<!-- EDITITON -->
			<xsl:if test="not(pub:source/e:volume) and (pub:publishing-info/e:edition or pub:source/e:publishing-info/e:edition)">
				<xsl:if test="pub:publishing-info/e:edition">
					<xsl:value-of select="concat('Edition:',pub:publishing-info/e:edition,'; ')"/>
				</xsl:if>
				<xsl:if test="pub:source/e:publishing-info/e:edition">
					<xsl:value-of select="concat('Source Edition:',pub:source/e:publishing-info/e:edition,'; ')"/>
				</xsl:if>
			</xsl:if>
			<!-- DATES -->
			<xsl:if test="dcterms:modified">
				<xsl:value-of select="concat('Modified:',dcterms:modified,'; ')"/>
			</xsl:if>
			<xsl:if test="dcterms:dateSubmitted">
				<xsl:value-of select="concat('Submitted:', dcterms:dateSubmitted,'; ')"/>
			</xsl:if>
			<xsl:if test="dcterms:dateAccepted">
				<xsl:value-of select="concat('Accepted:', dcterms:dateAccepted,'; ')"/>
			</xsl:if>
			<xsl:if test="pub:published-online">
				<xsl:value-of select="concat('Published Online:',pub:published-online,'; ')"/>
			</xsl:if>
			<xsl:if test="dcterms:issued">
				<xsl:value-of select="concat('Issued:',dcterms:issued,'; ')"/>
			</xsl:if>
			<!-- IDENTIFIER -->
			<xsl:if test="dc:identifier[@xsi:type!='eidt:ISSN' and @xsi:type!='eidt:ISBN' and @xsi:type!='eidt:OTHER']">
				<xsl:value-of select="concat(substring-after(dc:identifier/@xsi:type,':'),':',dc:identifier,'; ')"/>
			</xsl:if>
			<xsl:if test="dc:identifier[@xsi:type='eidt:OTHER']">
				<xsl:value-of select="concat('Other ID:',dc:identifier,'; ')"/>
			</xsl:if>

		</xsl:variable>
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'N1'"/>
			<xsl:with-param name="xpath" select="$n1"/>
		</xsl:call-template>
		<!-- END OF ENTRY -->
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'ER'"/>
			<xsl:with-param name="xpath" select="''"/>
		</xsl:call-template>
		<xsl:text disable-output-escaping="yes">&#xD;&#xA;&#xD;&#xA;</xsl:text>
	</xsl:template>
	<!-- END createEntry -->
	
	<!-- SOURCE CREATOR ORGA -->
	<xsl:template match="pub:source/e:creator/e:organization">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'A3'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- DATE -->
	<xsl:template match="dcterms:created">
		<xsl:variable name="date" select="string(.)"/>
		<xsl:variable name="year" select="substring-before($date, '-')"/>
		<xsl:variable name="month" select="substring-before(substring-after($date, '-'),'-')"/>
		<xsl:variable name="day" select="substring-after(substring-after($date,'-'),'-')"/>
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'PY'"/>
			<xsl:with-param name="xpath" select="concat($year,'/',$month,'/',$day,'/')"/>
		</xsl:call-template>
	</xsl:template> 
	<!-- SOURCE TITLE -->
	<xsl:template match="pub:source/dc:title">
		<xsl:param name="genre"/>
		<xsl:choose>
			<xsl:when test="$genre='book-item'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'T2'"/>
					<xsl:with-param name="xpath" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='article' or $genre='report' or $genre='other'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'JO'"/>
					<xsl:with-param name="xpath" select="."/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='book' or $genre='proceedings' or $genre='thesis' or $genre='manuscript' or $genre='journal' or $genre='series'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'T3'"/>
					<xsl:with-param name="xpath" select="."/>
				</xsl:call-template>
			</xsl:when>
			
			<xsl:otherwise>
				<xsl:if test="../../pub:source[position()=2]">
					<xsl:call-template name="createField">
						<xsl:with-param name="name" select="'T3'"/>
						<xsl:with-param name="xpath" select="../../pub:source[position()=2]"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- SOURCE IDENTIFIER -->
	<xsl:template match="pub:source/dc:identifier[@xsi:type='eidt:ISSN' or @xsi:type='eidt:ISBN']">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'SN'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- ISSUE -->
	<xsl:template match="pub:source/e:issue">
		<xsl:call-template name="createField">	
			<xsl:with-param name="name" select="'IS'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	<!-- VOLUME -->
	<xsl:template name="createVolume">
		<xsl:choose>
			<xsl:when test="pub:source/volume">
				<xsl:apply-templates select="pub:source/e:volume"/>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="pub:source/e:volume">
		
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'VL'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	<!-- DEGREE -->
	<xsl:template match="pub:degree">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'M1'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	<!-- LANGUAGE -->
	<xsl:template match="dc:language">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'N1'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	<!-- ABSTRACT -->
	<xsl:template match="dcterms:abstract">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'N2'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	<!-- PAGES -->
	<xsl:template match="pub:source/e:start-page">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'SP'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pub:source/e:end-page">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'EP'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="pub:total-number-of-pages">
		<xsl:if test="not(../pub:source/e:start-page)">
			<xsl:call-template name="createField">	
				<xsl:with-param name="name" select="'SP'"/>
				<xsl:with-param name="xpath" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- TITLE -->
	<xsl:template match="dc:title">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'TI'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- CREATOR -->
	<!--<xsl:template match="pub:creator">
		<xsl:apply-templates select="e:person"/>
		<xsl:apply-templates select="e:organization"/>
	</xsl:template>
	<xsl:template match="e:person">
		<xsl:choose>
			<xsl:when test="../@role='author'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'AU'"/>
					<xsl:with-param name="xpath" select="e:complete-name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createField">	
					<xsl:with-param name="name" select="'ED'"/>
					<xsl:with-param name="xpath" select="e:complete-name"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="e:organization">
		<xsl:choose>
			<xsl:when test="../@role='author'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'AU'"/>
					<xsl:with-param name="xpath" select="e:organization-name"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createField">	
					<xsl:with-param name="name" select="'ED'"/>
					<xsl:with-param name="xpath" select="e:organization-name"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>-->
	
	<!-- PUBLISHINGINFO -->
	<xsl:template match="pub:publishing-info">
		<xsl:apply-templates select="dc:publisher"/>
		<xsl:apply-templates select="e:place"/>
	</xsl:template>
	<xsl:template match="pub:source/e:publishing-info">
		<xsl:apply-templates select="dc:publisher"/>
		<xsl:apply-templates select="e:place"/>
	</xsl:template>
	<xsl:template match="dc:publisher">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'PB'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="e:place">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'CY'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	
	
	<!-- creates a field for the ris entry -->
	<xsl:template name="createField">
		<xsl:param name="name"/>
		<xsl:param name="xpath"/>
		<xsl:value-of select="$name"/>
		<xsl:text disable-output-escaping="yes"> - </xsl:text>
		<xsl:value-of select="normalize-space($xpath)"/>
		<xsl:text disable-output-escaping="yes">&#xD;&#xA;</xsl:text>
	</xsl:template>
	
	
	
	
	<!-- SUBJECT -->
	<xsl:template match="dcterms:subject">
		<xsl:call-template name="createField">	
			<xsl:with-param name="name" select="'KW'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- IDENTIFIER TEMPLATE -->
	<xsl:template match="dc:identifier[@xsi:type='eidt:ESCIDOC']">
		<xsl:if test=".!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'ID'"/>
				<xsl:with-param name="xpath" select="."/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="dc:identifier[@xsi:type='eidt:OTHER']">	
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'N1'"/>
			<xsl:with-param name="xpath" select="."/>			
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>

