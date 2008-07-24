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
<!--	<xsl:import href="functions.xsl"/>-->
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
	
<xsl:variable name="entities">

		<replacement>
			<with>\\acute{e}</with>
			<replace>&#xe9;</replace>
		</replacement>
		<replacement>
			<with>\\ast</with>
			<replace>\&#x002a;</replace>
		</replacement>
		<replacement>
			<with>\\star</with>
			<replace>\&#x002a;</replace>
		</replacement>
		<replacement>
			<with>{\\{}</with>
			<replace>\&#x007b;</replace>
		</replacement>
		<replacement>
			<with>\\{</with>
			<replace>\&#x007b;</replace>
		</replacement>
		<replacement>
			<with>{\\}}</with>
			<replace>\&#x007d;</replace>
		</replacement>
		<replacement>
			<with>\\}</with>
			<replace>\&#x007d;</replace>
		</replacement>
		<replacement>
			<with>^{\\underline{\\rm a}}</with>
			<replace>&#x00aa;</replace>
		</replacement>
		<replacement>
			<with>^{\\circ}</with>
			<replace>&#x00b0;</replace>
		</replacement>
		<replacement>
			<with>{\\pm}</with>
			<replace>&#x00b1;</replace>
		</replacement>
		<replacement>
			<with>^{2}</with>
			<replace>&#x00b2;</replace>
		</replacement>
		<replacement>
			<with>^{3}</with>
			<replace>&#x00b3;</replace>
		</replacement>
		<replacement>
			<with>^{1}</with>
			<replace>&#x00b9;</replace>
		</replacement>
		<replacement>
			<with>\\frac{1}{2}</with>
			<replace>&#x00bd;</replace>
		</replacement>
		<replacement>
			<with>{\\times}</with>
			<replace>&#x00d7;</replace>
		</replacement>
		<replacement>
			<with>\\times</with>
			<replace>&#x00d7;</replace>
		</replacement>
		<replacement>
			<with>{\\div}</with>
			<replace>&#x00f7;</replace>
		</replacement>
		<replacement>
			<with>\\div</with>
			<replace>&#x00f7;</replace>
		</replacement>
		<replacement>
			<with>\\dot G</with>
			<replace>&#x0120;</replace>
		</replacement>
		<replacement>
			<with>\\Gamma</with>
			<replace>&#x0393;</replace>
		</replacement>
		<replacement>
			<with>\\Delta</with>
			<replace>&#x0394;</replace>
		</replacement>
		<replacement>
			<with>\\Lambda</with>
			<replace>&#x039b;</replace>
		</replacement>
		<replacement>
			<with>\\Sigma</with>
			<replace>&#x03a3;</replace>
		</replacement>
		<replacement>
			<with>\\Omega</with>
			<replace>&#x03a9;</replace>
		</replacement>
		<replacement>
			<with>\\delta</with>
			<replace>&#x03b4;</replace>
		</replacement>
		<replacement>
			<with>\\alpha</with>
			<replace>&#x03b1;</replace>
		</replacement>
		<replacement>
			<with>\\beta</with>
			<replace>&#x03b2;</replace>
		</replacement>
		<replacement>
			<with>\\gamma</with>
			<replace>&#x03b3;</replace>
		</replacement>
		<replacement>
			<with>\\delta</with>
			<replace>&#x03b4;</replace>
		</replacement>
		<replacement>
			<with>\\epsilon</with>
			<replace>&#x03b5;</replace>
		</replacement>
		<replacement>
			<with>\\zeta</with>
			<replace>&#x03b6;</replace>
		</replacement>
		<replacement>
			<with>\\eta</with>
			<replace>&#x03b7;</replace>
		</replacement>
		<replacement>
			<with>\\theta</with>
			<replace>&#x03b8;</replace>
		</replacement>
		<replacement>
			<with>\\kappa</with>
			<replace>&#x03ba;</replace>
		</replacement>
		<replacement>
			<with>\\lambda</with>
			<replace>&#x03bb;</replace>
		</replacement>
		<replacement>
			<with>\\mu</with>
			<replace>&#x03bc;</replace>
		</replacement>
		<replacement>
			<with>\\nu</with>
			<replace>&#x03bd;</replace>
		</replacement>
		<replacement>
			<with>\\xi</with>
			<replace>&#x03be;</replace>
		</replacement>
		<replacement>
			<with>\\pi</with>
			<replace>&#x03c0;</replace>
		</replacement>
		<replacement>
			<with>\\rho</with>
			<replace>&#x03c1;</replace>
		</replacement>
		<replacement>
			<with>\\sigma</with>
			<replace>&#x03c3;</replace>
		</replacement>
		<replacement>
			<with>\\tau</with>
			<replace>&#x03c4;</replace>
		</replacement>
		<replacement>
			<with>\\phi</with>
			<replace>&#x03c6;</replace>
		</replacement>
		<replacement>
			<with>\\chi</with>
			<replace>&#x03c7;</replace>
		</replacement>
		<replacement>
			<with>\\omega</with>
			<replace>&#x03c9;</replace>
		</replacement>
		<replacement>
			<with>\\ell</with>
			<replace>&#x2113;</replace>
		</replacement>
		<replacement>
			<with>\\rightarrow</with>
			<replace>&#x2192;</replace>
		</replacement>
		<replacement>
			<with>\\to</with>
			<replace>&#x2192;</replace>
		</replacement>
		<replacement>
			<with>\\leftrightarrow</with>
			<replace>&#x2194;</replace>
		</replacement>
		<replacement>
			<with>\\nabla</with>
			<replace>&#x2207;</replace>
		</replacement>
		<replacement>
			<with>\\sim</with>
			<replace>&#x223c;</replace>
		</replacement>
		<replacement>
			<with>\\le</with>
			<replace>&#x2264;</replace>
		</replacement>
		<replacement>
			<with>\\ge</with>
			<replace>&#x2265;</replace>
		</replacement>
		<replacement>
			<with>\\lesssim</with>
			<replace>&#x2272;</replace>
		</replacement>
		<replacement>
			<with>\\gtrsim</with>
			<replace>&#x2273;</replace>
		</replacement>
		<replacement>
			<with>\\odot</with>
			<replace>&#x2299;</replace>
		</replacement>
		<replacement>
			<with>\\infty</with>
			<replace>&#x221e;</replace>
		</replacement>
		<replacement>
			<with>\\circ</with>
			<replace>&#x2218;</replace>
		</replacement>
		<replacement>
			<with>\\cdot</with>
			<replace>&#x22c5;</replace>
		</replacement>
		<replacement>
			<with>\\dot{P}</with>
			<replace>&#x1e56;</replace>
		</replacement>
		<replacement>
			<with>{\\vec B}</with>
			<replace>&#x20d7;</replace>
		</replacement>
		<replacement>
			<with>\\symbol{94}</with>
			<replace>\&#x005e;</replace>
		</replacement>
		<replacement>
			<with>\\symbol{126}</with>
			<replace>&#x007e;</replace>
		</replacement>
		<replacement>
			<with>\\~{}</with>
			<replace>&#x007e;</replace>
		</replacement>
		<replacement>
			<with>\$\\sim\$</with>
			<replace>&#x007e;</replace>
		</replacement>

		<replacement>
			<with>{!`}</with>
			<replace>&#x00a1;</replace>
		</replacement>
		<replacement>
			<with>{\\copyright}</with>
			<replace>&#x00a9;</replace>
		</replacement>
		<replacement>
			<with>{?`}</with>
			<replace>&#x00bf;</replace>
		</replacement>
		<replacement>
			<with>{\\`{A}}</with>
			<replace>&#x00c0;</replace>
		</replacement>
		<replacement>
			<with>{\\`A}</with>
			<replace>&#x00c0;</replace>
		</replacement>
		<replacement>
			<with>\\`{A}</with>
			<replace>&#x00c0;</replace>
		</replacement>
		<replacement>
			<with>\\`A</with>
			<replace>&#x00c0;</replace>
		</replacement>
		<replacement>
			<with>{\\'{A}}</with>
			<replace>&#x00c1;</replace>
		</replacement>
		<replacement>
			<with>{\\'A}</with>
			<replace>&#x00c1;</replace>
		</replacement>
		<replacement>
			<with>\\'{A}</with>
			<replace>&#x00c1;</replace>
		</replacement>
		<replacement>
			<with>\\'A</with>
			<replace>&#x00c1;</replace>
		</replacement>
		<replacement>
			<with>{\\^{A}}</with>
			<replace>&#x00c2;</replace>
		</replacement>
		<replacement>
			<with>{\\^A}</with>
			<replace>&#x00c2;</replace>
		</replacement>
		<replacement>
			<with>\\^{A}</with>
			<replace>&#x00c2;</replace>
		</replacement>
		<replacement>
			<with>\\^A</with>
			<replace>&#x00c2;</replace>
		</replacement>
		<replacement>
			<with>{\\~{A}}</with>
			<replace>&#x00c3;</replace>
		</replacement>
		<replacement>
			<with>{\\~A}</with>
			<replace>&#x00c3;</replace>
		</replacement>
		<replacement>
			<with>\\~{A}</with>
			<replace>&#x00c3;</replace>
		</replacement>
		<replacement>
			<with>\\~A</with>
			<replace>&#x00c3;</replace>
		</replacement>
		<replacement>
			<with>{\\"{A}}</with>
			<replace>&#x00c4;</replace>
		</replacement>
		<replacement>
			<with>{\\"A}</with>
			<replace>&#x00c4;</replace>
		</replacement>
		<replacement>
			<with>\\"{A}</with>
			<replace>&#x00c4;</replace>
		</replacement>
		<replacement>
			<with>\\"A</with>
			<replace>&#x00c4;</replace>
		</replacement>
		<replacement>
			<with>{\\AA}</with>
			<replace>&#x00c5;</replace>
		</replacement>
		<replacement>
			<with>{\\AE}</with>
			<replace>&#x00c6;</replace>
		</replacement>
		<replacement>
			<with>{\\c{C}}</with>
			<replace>&#x00c7;</replace>
		</replacement>
		<replacement>
			<with>\\c{C}</with>
			<replace>&#x00c7;</replace>
		</replacement>
		<replacement>
			<with>{\\`{E}}</with>
			<replace>&#x00c8;</replace>
		</replacement>
		<replacement>
			<with>{\\`E}</with>
			<replace>&#x00c8;</replace>
		</replacement>
		<replacement>
			<with>\\`{E}</with>
			<replace>&#x00c8;</replace>
		</replacement>
		<replacement>
			<with>\\`E</with>
			<replace>&#x00c8;</replace>
		</replacement>
		<replacement>
			<with>{\\'{E}}</with>
			<replace>&#x00c9;</replace>
		</replacement>
		<replacement>
			<with>{\\'E}</with>
			<replace>&#x00c9;</replace>
		</replacement>
		<replacement>
			<with>\\'{E}</with>
			<replace>&#x00c9;</replace>
		</replacement>
		<replacement>
			<with>\\'E</with>
			<replace>&#x00c9;</replace>
		</replacement>
		<replacement>
			<with>{\\^{E}}</with>
			<replace>&#x00ca;</replace>
		</replacement>
		<replacement>
			<with>{\\^E}</with>
			<replace>&#x00ca;</replace>
		</replacement>
		<replacement>
			<with>\\^{E}</with>
			<replace>&#x00ca;</replace>
		</replacement>
		<replacement>
			<with>\\^E</with>
			<replace>&#x00ca;</replace>
		</replacement>
		<replacement>
			<with>{\\"{E}}</with>
			<replace>&#x00cb;</replace>
		</replacement>
		<replacement>
			<with>{\\"E}</with>
			<replace>&#x00cb;</replace>
		</replacement>
		<replacement>
			<with>\\"{E}</with>
			<replace>&#x00cb;</replace>
		</replacement>
		<replacement>
			<with>\\"E</with>
			<replace>&#x00cb;</replace>
		</replacement>
		<replacement>
			<with>{\\`{I}}</with>
			<replace>&#x00cc;</replace>
		</replacement>
		<replacement>
			<with>{\\`I}</with>
			<replace>&#x00cc;</replace>
		</replacement>
		<replacement>
			<with>\\`{I}</with>
			<replace>&#x00cc;</replace>
		</replacement>
		<replacement>
			<with>\\`I</with>
			<replace>&#x00cc;</replace>
		</replacement>
		<replacement>
			<with>{\\'{I}}</with>
			<replace>&#x00cd;</replace>
		</replacement>
		<replacement>
			<with>{\\'I}</with>
			<replace>&#x00cd;</replace>
		</replacement>
		<replacement>
			<with>\\'{I}</with>
			<replace>&#x00cd;</replace>
		</replacement>
		<replacement>
			<with>\\'I</with>
			<replace>&#x00cd;</replace>
		</replacement>
		<replacement>
			<with>{\\^{I}}</with>
			<replace>&#x00ce;</replace>
		</replacement>
		<replacement>
			<with>{\\^I}</with>
			<replace>&#x00ce;</replace>
		</replacement>
		<replacement>
			<with>\\^{I}</with>
			<replace>&#x00ce;</replace>
		</replacement>
		<replacement>
			<with>\\^I</with>
			<replace>&#x00ce;</replace>
		</replacement>
		<replacement>
			<with>{\\"{I}}</with>
			<replace>&#x00cf;</replace>
		</replacement>
		<replacement>
			<with>{\\"I}</with>
			<replace>&#x00cf;</replace>
		</replacement>
		<replacement>
			<with>\\"{I}</with>
			<replace>&#x00cf;</replace>
		</replacement>
		<replacement>
			<with>\\"I</with>
			<replace>&#x00cf;</replace>
		</replacement>
		<replacement>
			<with>{\\~{N}}</with>
			<replace>&#x00d1;</replace>
		</replacement>
		<replacement>
			<with>\\~{N}</with>
			<replace>&#x00d1;</replace>
		</replacement>
		<replacement>
			<with>{\\~N}</with>
			<replace>&#x00d1;</replace>
		</replacement>
		<replacement>
			<with>\\~N</with>
			<replace>&#x00d1;</replace>
		</replacement>
		<replacement>
			<with>{\\`{O}}</with>
			<replace>&#x00d2;</replace>
		</replacement>
		<replacement>
			<with>{\\`O}</with>
			<replace>&#x00d2;</replace>
		</replacement>
		<replacement>
			<with>\\`{O}</with>
			<replace>&#x00d2;</replace>
		</replacement>
		<replacement>
			<with>\\`O</with>
			<replace>&#x00d2;</replace>
		</replacement>
		<replacement>
			<with>{\\'{O}}</with>
			<replace>&#x00d3;</replace>
		</replacement>
		<replacement>
			<with>{\\'O}</with>
			<replace>&#x00d3;</replace>
		</replacement>
		<replacement>
			<with>\\'{O}</with>
			<replace>&#x00d3;</replace>
		</replacement>
		<replacement>
			<with>\\'O</with>
			<replace>&#x00d3;</replace>
		</replacement>
		<replacement>
			<with>{\\^{O}}</with>
			<replace>&#x00d4;</replace>
		</replacement>
		<replacement>
			<with>{\\^O}</with>
			<replace>&#x00d4;</replace>
		</replacement>
		<replacement>
			<with>\\^{O}</with>
			<replace>&#x00d4;</replace>
		</replacement>
		<replacement>
			<with>\\^O</with>
			<replace>&#x00d4;</replace>
		</replacement>
		<replacement>
			<with>{\\~{O}}</with>
			<replace>&#x00d5;</replace>
		</replacement>
		<replacement>
			<with>{\\~O}</with>
			<replace>&#x00d5;</replace>
		</replacement>
		<replacement>
			<with>\\~{O}</with>
			<replace>&#x00d5;</replace>
		</replacement>
		<replacement>
			<with>\\~O</with>
			<replace>&#x00d5;</replace>
		</replacement>
		<replacement>
			<with>{\\"{O}}</with>
			<replace>&#x00d6;</replace>
		</replacement>
		<replacement>
			<with>{\\"O}</with>
			<replace>&#x00d6;</replace>
		</replacement>
		<replacement>
			<with>\\"{O}</with>
			<replace>&#x00d6;</replace>
		</replacement>
		<replacement>
			<with>\\"O</with>
			<replace>&#x00d6;</replace>
		</replacement>
		<replacement>
			<with>{\\O}</with>
			<replace>&#x00d8;</replace>
		</replacement>
		<replacement>
			<with>{\\`{U}}</with>
			<replace>&#x00d9;</replace>
		</replacement>
		<replacement>
			<with>{\\`U}</with>
			<replace>&#x00d9;</replace>
		</replacement>
		<replacement>
			<with>\\`{U}</with>
			<replace>&#x00d9;</replace>
		</replacement>
		<replacement>
			<with>\\`U</with>
			<replace>&#x00d9;</replace>
		</replacement>
		<replacement>
			<with>{\\'{U}}</with>
			<replace>&#x00da;</replace>
		</replacement>
		<replacement>
			<with>{\\'U}</with>
			<replace>&#x00da;</replace>
		</replacement>
		<replacement>
			<with>\\'{U}</with>
			<replace>&#x00da;</replace>
		</replacement>
		<replacement>
			<with>\\'U</with>
			<replace>&#x00da;</replace>
		</replacement>
		<replacement>
			<with>{\\^{U}}</with>
			<replace>&#x00db;</replace>
		</replacement>
		<replacement>
			<with>{\\^U}</with>
			<replace>&#x00db;</replace>
		</replacement>
		<replacement>
			<with>\\^{U}</with>
			<replace>&#x00db;</replace>
		</replacement>
		<replacement>
			<with>\\^U</with>
			<replace>&#x00db;</replace>
		</replacement>
		<replacement>
			<with>{\\"{U}}</with>
			<replace>&#x00dc;</replace>
		</replacement>
		<replacement>
			<with>{\\"U}</with>
			<replace>&#x00dc;</replace>
		</replacement>
		<replacement>
			<with>\\"{U}</with>
			<replace>&#x00dc;</replace>
		</replacement>
		<replacement>
			<with>\\"U</with>
			<replace>&#x00dc;</replace>
		</replacement>
		<replacement>
			<with>{\\'{Y}}</with>
			<replace>&#x00dd;</replace>
		</replacement>
		<replacement>
			<with>{\\'Y}</with>
			<replace>&#x00dd;</replace>
		</replacement>
		<replacement>
			<with>\\'{Y}</with>
			<replace>&#x00dd;</replace>
		</replacement>
		<replacement>
			<with>\\'Y</with>
			<replace>&#x00dd;</replace>
		</replacement>
		<replacement>
			<with>{\\ss}</with>
			<replace>&#x00df;</replace>
		</replacement>
		<replacement>
			<with>{\\`{a}}</with>
			<replace>&#x00e0;</replace>
		</replacement>
		<replacement>
			<with>{\\`a}</with>
			<replace>&#x00e0;</replace>
		</replacement>
		<replacement>
			<with>\\`{a}</with>
			<replace>&#x00e0;</replace>
		</replacement>
		<replacement>
			<with>\\`a</with>
			<replace>&#x00e0;</replace>
		</replacement>
		<replacement>
			<with>{\\'{a}}</with>
			<replace>&#x00e1;</replace>
		</replacement>
		<replacement>
			<with>{\\'a}</with>
			<replace>&#x00e1;</replace>
		</replacement>
		<replacement>
			<with>\\'{a}</with>
			<replace>&#x00e1;</replace>
		</replacement>
		<replacement>
			<with>\\'a</with>
			<replace>&#x00e1;</replace>
		</replacement>
		<replacement>
			<with>{\\^{a}}</with>
			<replace>&#x00e2;</replace>
		</replacement>
		<replacement>
			<with>{\\^a}</with>
			<replace>&#x00e2;</replace>
		</replacement>
		<replacement>
			<with>\\^{a}</with>
			<replace>&#x00e2;</replace>
		</replacement>
		<replacement>
			<with>\\^a</with>
			<replace>&#x00e2;</replace>
		</replacement>
		<replacement>
			<with>{\\~{a}}</with>
			<replace>&#x00e3;</replace>
		</replacement>
		<replacement>
			<with>{\\~a}</with>
			<replace>&#x00e3;</replace>
		</replacement>
		<replacement>
			<with>\\~{a}</with>
			<replace>&#x00e3;</replace>
		</replacement>
		<replacement>
			<with>\\~a</with>
			<replace>&#x00e3;</replace>
		</replacement>
		<replacement>
			<with>{\\"{a}}</with>
			<replace>&#x00e4;</replace>
		</replacement>
		<replacement>
			<with>{\\"a}</with>
			<replace>&#x00e4;</replace>
		</replacement>
		<replacement>
			<with>\\"{a}</with>
			<replace>&#x00e4;</replace>
		</replacement>
		<replacement>
			<with>\\"a</with>
			<replace>&#x00e4;</replace>
		</replacement>
		<replacement>
			<with>{\\aa}</with>
			<replace>&#x00e5;</replace>
		</replacement>
		<replacement>
			<with>{\\ae}</with>
			<replace>&#x00e6;</replace>
		</replacement>
		<replacement>
			<with>{\\c{c}}</with>
			<replace>&#x00e7;</replace>
		</replacement>
		<replacement>
			<with>\\c{c}</with>
			<replace>&#x00e7;</replace>
		</replacement>
		<replacement>
			<with>\\c c</with>
			<replace>&#x00e7;</replace>
		</replacement>
		<replacement>
			<with>{\\`{e}}</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>{\\`e}</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>{\\` e}</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>\\`{e}</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>\\`e</with>
			<replace>&#x00e8;</replace>
		</replacement>
		<replacement>
			<with>{\\'{e}}</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>{\\'e}</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>{\\' e}</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>\\'{e}</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>\\'e</with>
			<replace>&#x00e9;</replace>
		</replacement>
		<replacement>
			<with>{\\^{e}}</with>
			<replace>&#x00ea;</replace>
		</replacement>
		<replacement>
			<with>{\\^e}</with>
			<replace>&#x00ea;</replace>
		</replacement>
		<replacement>
			<with>\\^{e}</with>
			<replace>&#x00ea;</replace>
		</replacement>
		<replacement>
			<with>\\^e</with>
			<replace>&#x00ea;</replace>
		</replacement>
		<replacement>
			<with>{\\"{e}}</with>
			<replace>&#x00eb;</replace>
		</replacement>
		<replacement>
			<with>{\\"e}</with>
			<replace>&#x00eb;</replace>
		</replacement>
		<replacement>
			<with>\\"{e}</with>
			<replace>&#x00eb;</replace>
		</replacement>
		<replacement>
			<with>\\"e</with>
			<replace>&#x00eb;</replace>
		</replacement>
		<replacement>
			<with>{\\`{\\i}}</with>
			<replace>&#x00ec;</replace>
		</replacement>
		<replacement>
			<with>{\\`\\i}</with>
			<replace>&#x00ec;</replace>
		</replacement>
		<replacement>
			<with>\\`{\\i}</with>
			<replace>&#x00ec;</replace>
		</replacement>
		<replacement>
			<with>\\`\\i</with>
			<replace>&#x00ec;</replace>
		</replacement>
		<replacement>
			<with>{\\'{\\i}}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>{\\'\\i}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>\\'{\\i}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>\\'\\i</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>{\\'{i}}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>{\\'i}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>\\'{i}</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>\\'i</with>
			<replace>&#x00ed;</replace>
		</replacement>
		<replacement>
			<with>{\\^{\\i}}</with>
			<replace>&#x00ee;</replace>
		</replacement>
		<replacement>
			<with>{\\^\\i}</with>
			<replace>&#x00ee;</replace>
		</replacement>
		<replacement>
			<with>\\^{\\i}</with>
			<replace>&#x00ee;</replace>
		</replacement>
		<replacement>
			<with>\\^\\i</with>
			<replace>&#x00ee;</replace>
		</replacement>
		<replacement>
			<with>{\\"{\\i}}</with>
			<replace>&#x00ef;</replace>
		</replacement>
		<replacement>
			<with>{\\"\\i}</with>
			<replace>&#x00ef;</replace>
		</replacement>
		<replacement>
			<with>\\"{\\i}</with>
			<replace>&#x00ef;</replace>
		</replacement>
		<replacement>
			<with>\\"\\i</with>
			<replace>&#x00ef;</replace>
		</replacement>
		<replacement>
			<with>{\\~{n}}</with>
			<replace>&#x00f1;</replace>
		</replacement>
		<replacement>
			<with>\\~{n}</with>
			<replace>&#x00f1;</replace>
		</replacement>
		<replacement>
			<with>{\\~n}</with>
			<replace>&#x00f1;</replace>
		</replacement>
		<replacement>
			<with>\\~n</with>
			<replace>&#x00f1;</replace>
		</replacement>
		<replacement>
			<with>{\\`{o}}</with>
			<replace>&#x00f2;</replace>
		</replacement>
		<replacement>
			<with>{\\`o}</with>
			<replace>&#x00f2;</replace>
		</replacement>
		<replacement>
			<with>\\`{o}</with>
			<replace>&#x00f2;</replace>
		</replacement>
		<replacement>
			<with>\\`o</with>
			<replace>&#x00f2;</replace>
		</replacement>
		<replacement>
			<with>{\\'{o}}</with>
			<replace>&#x00f3;</replace>
		</replacement>
		<replacement>
			<with>{\\'o}</with>
			<replace>&#x00f3;</replace>
		</replacement>
		<replacement>
			<with>\\'{o}</with>
			<replace>&#x00f3;</replace>
		</replacement>
		<replacement>
			<with>\\'o</with>
			<replace>&#x00f3;</replace>
		</replacement>
		<replacement>
			<with>{\\^{o}}</with>
			<replace>&#x00f4;</replace>
		</replacement>
		<replacement>
			<with>{\\^o}</with>
			<replace>&#x00f4;</replace>
		</replacement>
		<replacement>
			<with>\\^{o}</with>
			<replace>&#x00f4;</replace>
		</replacement>
		<replacement>
			<with>\\^o</with>
			<replace>&#x00f4;</replace>
		</replacement>
		<replacement>
			<with>{\\~{o}}</with>
			<replace>&#x00f5;</replace>
		</replacement>
		<replacement>
			<with>{\\~o}</with>
			<replace>&#x00f5;</replace>
		</replacement>
		<replacement>
			<with>\\~{o}</with>
			<replace>&#x00f5;</replace>
		</replacement>
		<replacement>
			<with>\\~o</with>
			<replace>&#x00f5;</replace>
		</replacement>
		<replacement>
			<with>{\\"{o}}</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>{\\"o}</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>{\\" o}</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>\\"{o}</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>\\"o</with>
			<replace>&#x00f6;</replace>
		</replacement>
		<replacement>
			<with>{\\o}</with>
			<replace>&#x00f8;</replace>
		</replacement>
		<replacement>
			<with>{\\o }</with>
			<replace>&#x00f8;</replace>
		</replacement>
		<replacement>
			<with>{\\`{u}}</with>
			<replace>&#x00f9;</replace>
		</replacement>
		<replacement>
			<with>{\\`u}</with>
			<replace>&#x00f9;</replace>
		</replacement>
		<replacement>
			<with>\\`{u}</with>
			<replace>&#x00f9;</replace>
		</replacement>
		<replacement>
			<with>\\`u</with>
			<replace>&#x00f9;</replace>
		</replacement>
		<replacement>
			<with>{\\'{u}}</with>
			<replace>&#x00fa;</replace>
		</replacement>
		<replacement>
			<with>{\\'u}</with>
			<replace>&#x00fa;</replace>
		</replacement>
		<replacement>
			<with>\\'{u}</with>
			<replace>&#x00fa;</replace>
		</replacement>
		<replacement>
			<with>\\'u</with>
			<replace>&#x00fa;</replace>
		</replacement>
		<replacement>
			<with>{\\^{u}}</with>
			<replace>&#x00fb;</replace>
		</replacement>
		<replacement>
			<with>{\\^u}</with>
			<replace>&#x00fb;</replace>
		</replacement>
		<replacement>
			<with>\\^{u}</with>
			<replace>&#x00fb;</replace>
		</replacement>
		<replacement>
			<with>\\^u</with>
			<replace>&#x00fb;</replace>
		</replacement>
		<replacement>
			<with>{\\"{u}}</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>{\\"u}</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>{\\" u}</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>\\"{u}</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>\\"u</with>
			<replace>&#x00fc;</replace>
		</replacement>
		<replacement>
			<with>{\\'{y}}</with>
			<replace>&#x00fd;</replace>
		</replacement>
		<replacement>
			<with>{\\'y}</with>
			<replace>&#x00fd;</replace>
		</replacement>
		<replacement>
			<with>\\'{y}</with>
			<replace>&#x00fd;</replace>
		</replacement>
		<replacement>
			<with>\\'y</with>
			<replace>&#x00fd;</replace>
		</replacement>
		<replacement>
			<with>{\\th}</with>
			<replace>&#x00fe;</replace>
		</replacement>
		<replacement>
			<with>{\\"{y}}</with>
			<replace>&#x00ff;</replace>
		</replacement>
		<replacement>
			<with>{\\"y}</with>
			<replace>&#x00ff;</replace>
		</replacement>
		<replacement>
			<with>\\"{y}</with>
			<replace>&#x00ff;</replace>
		</replacement>
		<replacement>
			<with>\\"y</with>
			<replace>&#x00ff;</replace>
		</replacement>
		<replacement>
			<with>{\\'{C}}</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>\\'{C}</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>{\\' C}</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>{\\'C}</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>\\'C</with>
			<replace>&#x0106;</replace>
		</replacement>
		<replacement>
			<with>{\\'{c}}</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>\\'{c}</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>{\\' c}</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>{\\'c}</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>\\'c</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>\\`c</with>
			<replace>&#x0107;</replace>
		</replacement>
		<replacement>
			<with>{\\v{C}}</with>
			<replace>&#x010c;</replace>
		</replacement>
		<replacement>
			<with>\\v{C}</with>
			<replace>&#x010c;</replace>
		</replacement>
		<replacement>
			<with>{\\v C}</with>
			<replace>&#x010c;</replace>
		</replacement>
		<replacement>
			<with>{\\v{c}}</with>
			<replace>&#x010d;</replace>
		</replacement>
		<replacement>
			<with>\\v{c}</with>
			<replace>&#x010d;</replace>
		</replacement>
		<replacement>
			<with>{\\v c}</with>
			<replace>&#x010d;</replace>
		</replacement>
		<replacement>
			<with>{\\u{g}}</with>
			<replace>&#x011f;</replace>
		</replacement>
		<replacement>
			<with>\\u{g}</with>
			<replace>&#x011f;</replace>
		</replacement>
		<replacement>
			<with>{\\u g}</with>
			<replace>&#x011f;</replace>
		</replacement>
		<replacement>
			<with>{\\u{\\i}}</with>
			<replace>&#x012d;</replace>
		</replacement>
		<replacement>
			<with>{\\u\\i}</with>
			<replace>&#x012d;</replace>
		</replacement>
		<replacement>
			<with>\\u{\\i}</with>
			<replace>&#x012d;</replace>
		</replacement>
		<replacement>
			<with>\\u\\i</with>
			<replace>&#x012d;</replace>
		</replacement>
		<replacement>
			<with>{\\i}</with>
			<replace>&#x0131;</replace>
		</replacement>
		<replacement>
			<with>{\\L}</with>
			<replace>&#x0141;</replace>
		</replacement>
		<replacement>
			<with>{\\l}</with>
			<replace>&#x0142;</replace>
		</replacement>
		<replacement>
			<with>\\l{}</with>
			<replace>&#x0142;</replace>
		</replacement>

		<replacement>
			<with>{\\'{N}}</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>\\'{N}</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>{\\' N}</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>{\\'N}</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>\\'N</with>
			<replace>&#x0143;</replace>
		</replacement>
		<replacement>
			<with>{\\'{n}}</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>\\'{n}</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>{\\' n}</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>{\\'n}</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>\\'n</with>
			<replace>&#x0144;</replace>
		</replacement>
		<replacement>
			<with>{\\OE}</with>
			<replace>&#x0152;</replace>
		</replacement>
		<replacement>
			<with>{\\oe}</with>
			<replace>&#x0153;</replace>
		</replacement>
		<replacement>
			<with>{\\v{r}}</with>
			<replace>&#x0159;</replace>
		</replacement>
		<replacement>
			<with>\\v{r}</with>
			<replace>&#x0159;</replace>
		</replacement>
		<replacement>
			<with>{\\v r}</with>
			<replace>&#x0159;</replace>
		</replacement>
		<replacement>
			<with>{\\'{S}}</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>\\'{S}</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>{\\' S}</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>{\\'S}</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>\\'S</with>
			<replace>&#x015a;</replace>
		</replacement>
		<replacement>
			<with>{\\'{s}}</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>\\'{s}</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>{\\' s}</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>{\\'s}</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>\\'s</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>\\'s</with>
			<replace>&#x015b;</replace>
		</replacement>
		<replacement>
			<with>{\\c{S}}</with>
			<replace>&#x015e;</replace>
		</replacement>
		<replacement>
			<with>\\c{S}</with>
			<replace>&#x015e;</replace>
		</replacement>
		<replacement>
			<with>{\\c{s}}</with>
			<replace>&#x015f;</replace>
		</replacement>
		<replacement>
			<with>\\c{s}</with>
			<replace>&#x015f;</replace>
		</replacement>
		<replacement>
			<with>{\\v{S}}</with>
			<replace>&#x0160;</replace>
		</replacement>
		<replacement>
			<with>\\v{S}</with>
			<replace>&#x0160;</replace>
		</replacement>
		<replacement>
			<with>{\\v S}</with>
			<replace>&#x0160;</replace>
		</replacement>
		<replacement>
			<with>{\\u{s}}</with>
			<replace>&#x0161;</replace>
		</replacement>
		<replacement>
			<with>\\u{s}</with>
			<replace>&#x0161;</replace>
		</replacement>
		<replacement>
			<with>{\\v{s}}</with>
			<replace>&#x0161;</replace>
		</replacement>
		<replacement>
			<with>\\v{s}</with>
			<replace>&#x0161;</replace>
		</replacement>
		<replacement>
			<with>{\\'{t}}</with>
			<replace>&#x0165;</replace>
		</replacement>
		<replacement>
			<with>\\'{t}</with>
			<replace>&#x0165;</replace>
		</replacement>
		<replacement>
			<with>{\\'t}</with>
			<replace>&#x0165;</replace>
		</replacement>
		<replacement>
			<with>\\'t</with>
			<replace>&#x0165;</replace>
		</replacement>
		<replacement>
			<with>{\\={u}}</with>
			<replace>&#x016b;</replace>
		</replacement>
		<replacement>
			<with>{\\=u}</with>
			<replace>&#x016b;</replace>
		</replacement>
		<replacement>
			<with>\\={u}</with>
			<replace>&#x016b;</replace>
		</replacement>
		<replacement>
			<with>\\=u</with>
			<replace>&#x016b;</replace>
		</replacement>
		<replacement>
			<with>{\\r{u}}</with>
			<replace>&#x016f;</replace>
		</replacement>
		<replacement>
			<with>\\r{u}</with>
			<replace>&#x016f;</replace>
		</replacement>
		<replacement>
			<with>{\\'{z}}</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>\\'{z}</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>{\\'z}</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>\\'z</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>\\'z</with>
			<replace>&#x017a;</replace>
		</replacement>
		<replacement>
			<with>{\\.{Z}}</with>
			<replace>&#x017b;</replace>
		</replacement>
		<replacement>
			<with>\\.{Z}</with>
			<replace>&#x017b;</replace>
		</replacement>
		<replacement>
			<with>{\\.Z}</with>
			<replace>&#x017b;</replace>
		</replacement>
		<replacement>
			<with>\\.Z</with>
			<replace>&#x017b;</replace>
		</replacement>
		<replacement>
			<with>{\\.{z}}</with>
			<replace>&#x017c;</replace>
		</replacement>
		<replacement>
			<with>\\.{z}</with>
			<replace>&#x017c;</replace>
		</replacement>
		<replacement>
			<with>{\\.z}</with>
			<replace>&#x017c;</replace>
		</replacement>
		<replacement>
			<with>\\.z</with>
			<replace>&#x017c;</replace>
		</replacement>
		<replacement>
			<with>{\\v{Z}}</with>
			<replace>&#x017d;</replace>
		</replacement>
		<replacement>
			<with>\\v{Z}</with>
			<replace>&#x017d;</replace>
		</replacement>
		<replacement>
			<with>{\\v Z}</with>
			<replace>&#x017d;</replace>
		</replacement>
		<replacement>
			<with>{\\v{z}}</with>
			<replace>&#x017e;</replace>
		</replacement>
		<replacement>
			<with>\\v{z}</with>
			<replace>&#x017e;</replace>
		</replacement>
		<replacement>
			<with>{\\v z}</with>
			<replace>&#x017e;</replace>
		</replacement>
		<replacement>
			<with>{\\c{e}}</with>
			<replace>&#x0229;</replace>
		</replacement>
		<replacement>
			<with>\\c{e}</with>
			<replace>&#x0229;</replace>
		</replacement>
		<replacement>
			<with>{\\v{A}}</with>
			<replace>&#x01cd;</replace>
		</replacement>
		<replacement>
			<with>\\v{A}</with>
			<replace>&#x01cd;</replace>
		</replacement>
		<replacement>
			<with>{\\v A}</with>
			<replace>&#x01cd;</replace>
		</replacement>
		<replacement>
			<with>{\\v{a}}</with>
			<replace>&#x01ce;</replace>
		</replacement>
		<replacement>
			<with>\\v{a}</with>
			<replace>&#x01ce;</replace>
		</replacement>
		<replacement>
			<with>{\\v a}</with>
			<replace>&#x01ce;</replace>
		</replacement>

		<!--<replacement><with>{\\_}</with>
			<replace>&#x005f;</replace>
		</replacement>
		<replacement><with>\\_</with>
			<replace>&#x005f;</replace>
		</replacement>
		<replacement>
			<with>\\hspace{0 cm}</with>
			<replace></replace>
		</replacement>
		<replacement>
			<with>\\hspace{0 pt}</with>
			<replace></replace>
		</replacement>
		<replacement><with>{\\#}</with>
					<replace>&#x0023;</replace>
				</replacement>
		<replacement><with>\\#</with>
					<replace>&#x0023;</replace>
				</replacement>
		<replacement><with>{\\$}</with>
					<replace>\&#x0024;</replace>
				</replacement>
		<replacement><with>\\$</with>
					<replace>\&#x0024;</replace>
				</replacement>
		<replacement><with>{\\%}</with>
					<replace>&#x0025;</replace>
				</replacement>
		<replacement><with>\\%</with>
					<replace>&#x0025;</replace>
				</replacement>
		<replacement><with>{\\&amp}</with>
					<replace>&#x0026;</replace>
				</replacement>
		<replacement><with>\\&amp;</with>
			<replace>&#x0026;</replace>
		</replacement>-->
	</xsl:variable>
	
	<xsl:function name="func:texString">
		<xsl:param name="str"/>
		<xsl:param name="counter"/>
		
		<xsl:choose>
			<xsl:when test="$counter &gt; count($entities/*)">
				<xsl:value-of select="$str"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="func:texString(replace($str, $entities/*[$counter]/replace, $entities/*[$counter]/with), $counter + 1)"/>
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>	
	
</xsl:stylesheet>
