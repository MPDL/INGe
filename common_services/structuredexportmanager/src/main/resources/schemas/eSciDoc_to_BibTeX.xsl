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
 xmlns:func="urn:my-functions">
	<xsl:import href="functions.xsl"/>
	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	
	
	<xsl:template match="/*">			
		<!-- create entry for each item -->		
			<xsl:apply-templates select="*:item//*:md-record/*:publication"/>				
	</xsl:template>	
	
	<!-- create bibTeX entry -->
	<xsl:template match="*:item//*:md-record/*:publication">		
		<xsl:param name="genre" select="@type"/>
		
		<!-- detect bibtex entry type -->		
		<xsl:choose>
			<xsl:when test="$genre='article'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">article</xsl:with-param>
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='proceedings'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">proceedings</xsl:with-param>
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='conference-paper'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType">inproceedings</xsl:with-param>
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='book'">				
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'book'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='book-item'">				
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'incollection'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$genre='report'">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'techreport'"/>
				</xsl:call-template>				
			</xsl:when>
			<xsl:when test="$genre='thesis'">
				<xsl:choose>
					<xsl:when test="*:degree='master'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="entryType" select="'masterthesis'"/>
						</xsl:call-template>						
					</xsl:when>
					<xsl:when test="*:degree='phd'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="entryType" select="'phdthesis'"/>
						</xsl:call-template>						
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="createEntry">
							<xsl:with-param name="entryType" select="'misc'"/>
						</xsl:call-template>	
					</xsl:otherwise>
				</xsl:choose>				
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="createEntry">
					<xsl:with-param name="entryType" select="'misc'"/>
				</xsl:call-template>				
			</xsl:otherwise>
		</xsl:choose>			
	</xsl:template>
	
	<!-- create bibTeX entry -->
	<xsl:template name="createEntry">
		
		<xsl:param name="entryType"/>
		<xsl:variable name="escidocid" select="parent::*:md-record/parent::*:md-records/parent::*:item/@objid"/>
		<xsl:value-of select="concat('@', $entryType, '{')"/>
		<xsl:value-of select="func:texString($escidocid,1)"/>
		<xsl:value-of select="','"/>
		
		<xsl:text disable-output-escaping="yes">&#xA;</xsl:text><!-- line break -->
		<!-- TITLE -->
		<xsl:apply-templates select="*:title"/>		
		<!-- CREATOR -->
		<xsl:apply-templates select="*:creator[@role='author']"/>		
		<!-- EDITOR -->
		<xsl:apply-templates select="*:creator[@role='editor']"/>		
		<!-- LANGUAGE -->
		<xsl:apply-templates select="*:language"/>
		<!-- URI, URN -->
		<xsl:apply-templates select="*:identifier[contains(@xsi:type, 'URN')]"/>		
		<!-- ISSN -->
		<xsl:choose>
			<xsl:when test="*:source/*:identifier[contains(@xsi:type, 'ISSN')]">
				<xsl:apply-templates select="*:source/*:identifier[contains(@xsi:type, 'ISSN')]"/>					
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="*:identifier[contains(@xsi:type, 'ISSN')]"/>				
			</xsl:otherwise>
		</xsl:choose>			
		<!-- ISBN -->
		<xsl:apply-templates select="*:identifier[contains(@xsi:type, 'ISBN')]"/>		
		<!-- PUBLISHER, ADDRESS -->
		<xsl:choose>
			<xsl:when test="*:source/*:publishing-info/*:publisher=''">
				<xsl:apply-templates select="*:publishing-info/*:publisher"/>		
				<xsl:apply-templates select="*:publishing-info/*:place"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="*:source/*:publishing-info/*:publisher"/>		
				<xsl:apply-templates select="*:source/*:publishing-info/*:place"/>	
			</xsl:otherwise>
		</xsl:choose>			
		<!-- EDITION -->
		<xsl:choose>
			<xsl:when test="*:source/*:publishing-info/*:edition=''">
				<xsl:apply-templates select="*:publishing-info/*:edition"/>				
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="*:source/*:publishing-info/*:edition"/>	
			</xsl:otherwise>
		</xsl:choose>		
		<!-- YEAR -->
		<xsl:variable name="pubdate" select="if(*:issued!='') then *:issued else if  (*:published-online!='') then *:published-online else if (*:dateAccepted!='') then *:dateAccepted else if (*:dateSubmitted!='') then *:dateSubmitted else if (*:modified!='') then *:modified else if (*:created!='') then *:created else ''"/>	
		<xsl:if test="$pubdate!=''">
			<xsl:variable name="year" select="substring($pubdate,1,4)"/>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'year'"/>
				<xsl:with-param name="xpath" select="$year"/>
			</xsl:call-template>
		</xsl:if>		
		<!-- DATE -->
		<xsl:apply-templates select="*:issued"/>		
		<!-- ABSTRACT -->
		<xsl:apply-templates select="*:abstract"/>		
		<!-- SUBJECT -->
		<xsl:apply-templates select="*:subject"/>		
		<!-- TABLE OF CONTENTS -->
		<xsl:apply-templates select="*:tableOfContents"/>		
		<!-- SOURCE -->
		<xsl:apply-templates select="*:source"/>			
		<!-- END OF ENTRY -->		
		<xsl:value-of select="concat('}','')"/>	
		<xsl:text disable-output-escaping="yes">&#xA; &#xA;</xsl:text>
	</xsl:template>
	<!-- END createEntry -->
	
	
	<xsl:template match="*:title">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'title'"/>
			<xsl:with-param name="xpath" select="concat(normalize-space(.), '')"/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:language">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'language'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:identifier[contains(@xsi:type, 'URN')]">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'howpublished'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:identifier[contains(@xsi:type, 'ISSN')]">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'issn'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:identifier[contains(@xsi:type, 'ISBN')]">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'isbn'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:publishing-info/*:publisher">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'publisher'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:publishing-info/*:place">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'address'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:abstract">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'abstract'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:subject">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'keywords'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="*:tableOfContents">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'contents'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- creates a field for the bibtex entry -->
	<xsl:template name="createField">
		<xsl:param name="name"/>
		<xsl:param name="xpath"/>
		<xsl:value-of select="func:texString($name,1)"/>
		<xsl:text disable-output-escaping="yes"> = "</xsl:text>
		<xsl:value-of select="func:texString(normalize-space($xpath),1)"/>
		<xsl:text disable-output-escaping="yes">"; &#xA;</xsl:text>
	</xsl:template>
	
	<!-- SOURCE -->
	<xsl:template match="*:source">		
		<!-- TITLE -->
		<xsl:choose>
			<xsl:when test="@type='series'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'series'"/>
					<xsl:with-param name="xpath" select="*:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@type='journal'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'journal'"/>
					<xsl:with-param name="xpath" select="*:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@type='book' or @type='proceedings'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'booktitle'"/>
					<xsl:with-param name="xpath" select="*:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<!-- SOURCE CREATOR -->
		<xsl:variable name="role" select="*:creator/@role"/>
		<xsl:if test="exists(*:creator[@role='author' or @role='editor'])">
			<xsl:text disable-output-escaping="yes">note = "</xsl:text>	
				<xsl:apply-templates select="*:creator/*:person[parent::*/parent::*:source]"/>
				<xsl:apply-templates select="*:creator/*:organization[parent::*/parent::*:source]"/>
			<xsl:text disable-output-escaping="yes">"; &#xA;</xsl:text>
		</xsl:if>
		
		
		<!-- SOURCE VOLUME -->
		<xsl:if test="*:volume!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'volume'"/>
			<xsl:with-param name="xpath" select="*:volume"/>
		</xsl:call-template>
		</xsl:if>
		
		<!-- SOURCE ISSUE -->
		<xsl:if test="*:issue!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'issue'"/>
			<xsl:with-param name="xpath" select="*:issue"/>
		</xsl:call-template>
		</xsl:if>
		
		<!-- SOURCE PAGES -->
		<xsl:if test="normalize-space(*:start-page)!=''">
			<xsl:if test="*:sequence-number!=''">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'pages'"/>
					<xsl:with-param name="xpath" select="concat(*:start-page, ' - ', *:end-page)"/>
				</xsl:call-template>				
			</xsl:if>
		</xsl:if>		
		<!-- TODO SOURCE HOWPUBLISHED -->		
	</xsl:template>
	
	
	<xsl:template match="*:publishing-info/*:edition">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'edition'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="*:issued">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'date'"/>
			<xsl:with-param name="xpath" select="substring(., 1, 10)"/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- AUTHOR, EDITOR TEMPLATE -->
	<xsl:template match="*:creator">					
		<xsl:apply-templates select="*:person"/>			
		<xsl:apply-templates select="*:organization"/>		
	</xsl:template>
	
	<xsl:template name="roleLabel">
		<xsl:variable name="role" select="../@role"/>	
		<xsl:value-of select="$role"/>
		<xsl:text disable-output-escaping="yes"> = "</xsl:text>
	</xsl:template>
	
	<xsl:template match="*:person">			
		<xsl:variable name="role" select="../@role"/>	
		<xsl:choose>		
		<xsl:when test="count(../preceding-sibling::*:creator[@role=$role])=0">	
			<xsl:call-template name="roleLabel"/>					
		</xsl:when>
		<xsl:when test="count(../preceding-sibling::*:creator[@role=$role])=0 and count(../parent::*:source)=1">	
			<xsl:value-of select="concat($role, ' : ')"/>				
		</xsl:when>
		<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="concat(*:family-name, ', ', *:given-name, '')"/>		
		<!-- AND-connection of persons -->		
		<xsl:choose>		
			<xsl:when test="exists(../following-sibling::*:creator[@role=$role])">
				<xsl:value-of select="' and '"/>
			</xsl:when>
			<xsl:otherwise>				
				<xsl:text disable-output-escaping="yes">"; &#xA;</xsl:text>					
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template match="*:organization">		
		<xsl:variable name="role" select="../@role"/>		
		<xsl:choose>	
		<xsl:when test="count(../preceding-sibling::*:creator[@role=$role])=0 and count(../parent::*:source)=1">	
			<xsl:value-of select="concat($role, ' : ')"/>				
		</xsl:when>	
		<xsl:when test="count(../preceding-sibling::*:creator[@role=$role])=0">	
			<xsl:call-template name="roleLabel"/>					
		</xsl:when>		
		<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="*:organization-name"/>
		<!-- AND-connection of orgas -->		
		<xsl:variable name="role" select="../@role"/>
		<xsl:choose>		
			<xsl:when test="exists(../following-sibling::*:creator[@role=$role])">
				<xsl:value-of select="' and '"/>
			</xsl:when>
			<xsl:otherwise>		
				<xsl:choose>
					<xsl:when test="count(../parent::*:source)=0">
						<xsl:text disable-output-escaping="yes">"; &#xA;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text disable-output-escaping="yes">  </xsl:text>
					</xsl:otherwise>	
				</xsl:choose>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- IDENTIFIER TEMPLATE -->
	<!-- TODO id.type= uri -->
	<xsl:template match="*:identifier">
		<xsl:if test=".!=''">
		<xsl:value-of select="func:texString(normalize-space(.),1)"/>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
