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
   xmlns:ei="${xsd.soap.item.item}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="${xsd.metadata.escidocprofile.types}"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:prop="${xsd.soap.common.prop}"
   xmlns:pub="${xsd.metadata.publication}">
	<xsl:import href="functions.xsl"/>
	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	
	
	<xsl:template match="/*">			
		<!-- create entry for each item -->		
			<xsl:apply-templates select="ei:item/mdr:md-records/mdr:md-record/mdp:publication"/>				
	</xsl:template>	
	
	<!-- create bibTeX entry -->
	<xsl:template match="ei:item/mdr:md-records/mdr:md-record/mdp:publication">		
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
					<xsl:when test="mdp:degree='master'">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="entryType" select="'masterthesis'"/>
						</xsl:call-template>						
					</xsl:when>
					<xsl:when test="mdp:degree='phd'">
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
		<xsl:variable name="escidocid" select="parent::mdr:md-record/parent::mdr:md-records/parent::ei:item/@objid"/>
		<xsl:value-of select="concat('@', $entryType, '{')"/>
		<xsl:value-of select="func:texString($escidocid,1)"/>
		<xsl:value-of select="','"/>
		
		<xsl:text disable-output-escaping="yes">&#xA;</xsl:text><!-- line break -->
		<!-- TITLE -->
		<xsl:apply-templates select="dc:title"/>		
		<!-- CREATOR -->
		<xsl:apply-templates select="pub:creator[@role='author']"/>		
		<!-- EDITOR -->
		<xsl:apply-templates select="pub:creator[@role='editor']"/>		
		<!-- LANGUAGE -->
		<xsl:apply-templates select="dc:language"/>
		<!-- URI, URN -->
		<xsl:apply-templates select="dc:identifier[contains(@xsi:type, 'URN')]"/>		
		<!-- ISSN -->
		<xsl:choose>
			<xsl:when test="pub:source/dc:identifier[contains(@xsi:type, 'ISSN')]">
				<xsl:apply-templates select="pub:source/dc:identifier[contains(@xsi:type, 'ISSN')]"/>					
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="dc:identifier[contains(@xsi:type, 'ISSN')]"/>				
			</xsl:otherwise>
		</xsl:choose>			
		<!-- ISBN -->
		<xsl:apply-templates select="dc:identifier[contains(@xsi:type, 'ISBN')]"/>		
		<!-- PUBLISHER, ADDRESS -->
		<xsl:choose>
			<xsl:when test="pub:source/pub:publishing-info/dc:publisher=''">
				<xsl:apply-templates select="pub:publishing-info/dc:publisher"/>		
				<xsl:apply-templates select="pub:publishing-info/e:place"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="pub:source/pub:publishing-info/dc:publisher"/>		
				<xsl:apply-templates select="pub:source/pub:publishing-info/e:place"/>	
			</xsl:otherwise>
		</xsl:choose>			
		<!-- EDITION -->
		<xsl:choose>
			<xsl:when test="pub:source/pub:publishing-info/e:edition=''">
				<xsl:apply-templates select="pub:publishing-info/e:edition"/>				
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="pub:source/pub:publishing-info/e:edition"/>	
			</xsl:otherwise>
		</xsl:choose>		
		<!-- YEAR -->
		<xsl:variable name="pubdate" select="if(dcterms:issued!='') then dcterms:issued else if  (pub:published-online!='') then pub:published-online else if (dcterms:dateAccepted!='') then dcterms:dateAccepted else if (dcterms:dateSubmitted!='') then dcterms:dateSubmitted else if (dcterms:modified!='') then dcterms:modified else if (dcterms:created!='') then dcterms:created else ''"/>	
		<xsl:if test="$pubdate!=''">
			<xsl:variable name="year" select="substring($pubdate,1,4)"/>
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'year'"/>
				<xsl:with-param name="xpath" select="$year"/>
			</xsl:call-template>
		</xsl:if>		
		<!-- DATE -->
		<xsl:apply-templates select="dcterms:issued"/>		
		<!-- ABSTRACT -->
		<xsl:apply-templates select="dcterms:abstract"/>		
		<!-- SUBJECT -->
		<xsl:apply-templates select="dc:subject"/>		
		<!-- TABLE OF CONTENTS -->
		<xsl:apply-templates select="dcterms:tableOfContents"/>		
		<!-- SOURCE -->
		<xsl:apply-templates select="pub:source"/>			
		<!-- END OF ENTRY -->		
		<xsl:value-of select="concat('}','')"/>	
		<xsl:text disable-output-escaping="yes">&#xA; &#xA;</xsl:text>
	</xsl:template>
	<!-- END createEntry -->
	
	
	<xsl:template match="dc:title">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'title'"/>
			<xsl:with-param name="xpath" select="concat(normalize-space(.), '')"/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dc:language">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'language'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dc:identifier[contains(@xsi:type, 'URN')]">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'howpublished'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dc:identifier[contains(@xsi:type, 'ISSN')]">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'issn'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dc:identifier[contains(@xsi:type, 'ISBN')]">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'isbn'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="pub:publishing-info/dc:publisher">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'publisher'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="pub:publishing-info/e:place">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'address'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dcterms:abstract">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'abstract'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dc:subject">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'keywords'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="dcterms:tableOfContents">
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
	<xsl:template match="pub:source">		
		<!-- TITLE -->
		<xsl:choose>
			<xsl:when test="@type='series'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'series'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@type='journal'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'journal'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@type='book' or @type='proceedings'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'booktitle'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<!-- SOURCE CREATOR -->
		<xsl:variable name="role" select="pub:creator/@role"/>
		<xsl:if test="exists(pub:creator[@role='author' or @role='editor'])">
			<xsl:text disable-output-escaping="yes">note = "</xsl:text>	
				<xsl:apply-templates select="pub:creator/e:person[parent::*/parent::pub:source]"/>
				<xsl:apply-templates select="pub:creator/e:organization[parent::*/parent::pub:source]"/>
			<xsl:text disable-output-escaping="yes">"; &#xA;</xsl:text>
		</xsl:if>
		
		
		<!-- SOURCE VOLUME -->
		<xsl:if test="e:volume!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'volume'"/>
			<xsl:with-param name="xpath" select="e:volume"/>
		</xsl:call-template>
		</xsl:if>
		
		<!-- SOURCE ISSUE -->
		<xsl:if test="e:issue!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'issue'"/>
			<xsl:with-param name="xpath" select="e:issue"/>
		</xsl:call-template>
		</xsl:if>
		
		<!-- SOURCE PAGES -->
		<xsl:if test="normalize-space(e:start-page)!=''">
			<xsl:if test="e:sequence-number!=''">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'pages'"/>
					<xsl:with-param name="xpath" select="concat(e:start-page, ' - ', e:end-page)"/>
				</xsl:call-template>				
			</xsl:if>
		</xsl:if>		
		<!-- TODO SOURCE HOWPUBLISHED -->		
	</xsl:template>
	
	
	<xsl:template match="pub:publishing-info/e:edition">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'edition'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="dcterms:issued">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'date'"/>
			<xsl:with-param name="xpath" select="substring(., 1, 10)"/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- AUTHOR, EDITOR TEMPLATE -->
	<xsl:template match="pub:creator">					
		<xsl:apply-templates select="e:person"/>			
		<xsl:apply-templates select="e:organization"/>		
	</xsl:template>
	
	<xsl:template name="roleLabel">
		<xsl:variable name="role" select="../@role"/>	
		<xsl:value-of select="$role"/>
		<xsl:text disable-output-escaping="yes"> = "</xsl:text>
	</xsl:template>
	
	<xsl:template match="e:person">			
		<xsl:variable name="role" select="../@role"/>	
		<xsl:choose>		
		<xsl:when test="count(../preceding-sibling::pub:creator[@role=$role])=0">	
			<xsl:call-template name="roleLabel"/>					
		</xsl:when>
		<xsl:when test="count(../preceding-sibling::pub:creator[@role=$role])=0 and count(../parent::pub:source)=1">	
			<xsl:value-of select="concat($role, ' : ')"/>				
		</xsl:when>
		<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="concat(e:family-name, ', ', e:given-name, '')"/>		
		<!-- AND-connection of persons -->		
		<xsl:choose>		
			<xsl:when test="exists(../following-sibling::pub:creator[@role=$role])">
				<xsl:value-of select="' and '"/>
			</xsl:when>
			<xsl:otherwise>				
				<xsl:text disable-output-escaping="yes">"; &#xA;</xsl:text>					
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template match="e:organization">		
		<xsl:variable name="role" select="../@role"/>		
		<xsl:choose>	
		<xsl:when test="count(../preceding-sibling::pub:creator[@role=$role])=0 and count(../parent::pub:source)=1">	
			<xsl:value-of select="concat($role, ' : ')"/>				
		</xsl:when>	
		<xsl:when test="count(../preceding-sibling::pub:creator[@role=$role])=0">	
			<xsl:call-template name="roleLabel"/>					
		</xsl:when>		
		<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="e:organization-name"/>
		<!-- AND-connection of orgas -->		
		<xsl:variable name="role" select="../@role"/>
		<xsl:choose>		
			<xsl:when test="exists(../following-sibling::*:creator[@role=$role])">
				<xsl:value-of select="' and '"/>
			</xsl:when>
			<xsl:otherwise>		
				<xsl:choose>
					<xsl:when test="count(../parent::pub:source)=0">
						<xsl:text disable-output-escaping="yes">", &#xA;</xsl:text>
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
	<xsl:template match="dc:identifier">
		<xsl:if test=".!=''">
		<xsl:value-of select="func:texString(normalize-space(.),1)"/>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
