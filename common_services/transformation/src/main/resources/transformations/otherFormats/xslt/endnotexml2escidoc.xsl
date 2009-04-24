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


 Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from EndNote Item to eSciDoc PubItem 
	Author: Vlad Makarenko (initial creation) 
	$Author: $ (last changed)
	$Revision: $ 
	$LastChangedDate: $
-->
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:mdr="http://www.escidoc.de/schemas/metadatarecords/0.4"
   xmlns:mdp="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
   xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
   xmlns:ei="http://www.escidoc.de/schemas/item/0.7"
   xmlns:eidt="http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes"
   xmlns:srel="http://escidoc.de/core/01/structural-relations/"
   xmlns:prop="http://escidoc.de/core/01/properties/"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
   xmlns:ec="http://www.escidoc.de/schemas/components/0.7"
   xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file"
   xmlns:pub="http://escidoc.mpg.de/metadataprofile/schema/0.1/publication"
   xmlns:escidoc="urn:escidoc:functions">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="user" select="'dummy-user'"/>
	<xsl:param name="context" select="'escidoc:dummy-context'"/>
	
	<xsl:param name="genre" select="''"/>
	
	<xsl:template match="/">
		<item-list>
			<xsl:apply-templates select="//item"/>
		</item-list>
	</xsl:template>

	<xsl:template match="//item">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<xsl:element name="srel:context">
					<xsl:attribute name="xlink:href" select="concat('/ir/context/', $context)"/>
				</xsl:element>
				<srel:content-model xlink:href="/cmm/content-model/escidoc:persistent4"/>
				<xsl:element name="prop:content-model-specific"></xsl:element>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="itemMetadata"/>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components"></xsl:element>
		</xsl:element>
	</xsl:template>
	
		
	
	<!-- GENRE -->
	<xsl:template name="itemMetadata">
		<xsl:variable name="genreMap">
			<m name="Book">book</m>
			<m name="Edited Book">book</m>
			<m name="Electronic Book">book</m>
			<m name="Book Section">book-item</m>
			<m name="Conference Paper">conference-paper</m>
			<m name="Conference Proceeding">proceedings</m>
			<m name="Journal Article">article</m>
			<m name="Magazine Article">article</m>
			<m name="Newspaper Article">article</m>
			<m name="Electronic Article">article</m>
			<m name="Report">report</m>
			<m name="Manuscript">manuscript</m>
			<m name="Thesis">thesis</m>
			<m name="Generic">other</m>
		</xsl:variable>
		
		<xsl:variable name="curGenre" select="
			  	if ( NUM_0 = ('Book', 'Edited Book', 'Electronic Book') ) then 'book' else
			  	if ( NUM_0 = ('Book Section') ) then 'book-item' else
			  	if ( NUM_0 = ('Conference Paper') ) then 'conference-paper' else
			  	if ( NUM_0 = ('Conference Proceeding') ) then 'proceedings' else
			  	if ( NUM_0 = ('Journal Article', 'Magazine Article', 'Newspaper Article', 'Electronic Article') ) then 'article' else
			  	if ( NUM_0 = ('Report') ) then 'report' else
			  	if ( NUM_0 = ('Manuscript') ) then 'manuscript' else
			  	if ( NUM_0 = ('Thesis') ) then 'thesis' else
			  	if ( NUM_0 = ('Generic') ) then 'other' else
			  	''
			  	"/>	
		<xsl:if test="$curGenre!=''">
			<xsl:call-template name="createEntry">
				<xsl:with-param name="gen" select="$curGenre"/>
			</xsl:call-template>
		</xsl:if>		

	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:element name="mdp:publication">
			<xsl:attribute name="type">
				<xsl:value-of select="$gen"/>
			</xsl:attribute>
			
			<!-- CREATORS -->
			<xsl:apply-templates select="A"/>
			<xsl:apply-templates select="E"/>
			<xsl:apply-templates select="Y"/>
			<xsl:apply-templates select="QUESTION"/>
						
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="T"/>
			</xsl:element>
			
			<!--ALTTITLE -->
			<xsl:for-each select="
				B[../NUM_0 = ('Generic', 'Electronic Book')]
				|
				F
				|
				J[../NUM_0 = ('Book', 'Book Section', 'Manuscript', 'Edited Book', 'Electronic Article', 'Report')]
				|
				Q
				|
				EXCLAMATION
				|
				S[../NUM_0 = ('Generic')]
				">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			
			
			<!-- IDENTIFIERS -->
			<xsl:for-each select="L">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="M">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">
<!--						TODO: check!!!-->
						<xsl:value-of select="if (starts-with(upper-case(.), 'ISI:' )) then 'eidt:ISI' else 'eidt:OTHER'"/>
					</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="R">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:DOI</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="NUM_6[
					../NUM_0='Manuscript'
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
				   ../NUM_0 = ('Book', 'Book Section', 'Conference Proceeding', 'Edited Book', 'Electronic Book')   
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:ISBN</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
					../NUM_0 = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article')			
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:ISSN</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
				      ../NUM_0 = 'Report'
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<!-- END OF IDENTIFIERS -->
			
			<!-- DATES -->
			<xsl:variable name="year">
				<xsl:if test="D and (
					   NUM_0 = (
						      'Generic'
							, 'Book' 
							, 'Book Section' 
							, 'Conference Paper' 
							, 'Edited Book' 
							, 'Electronic Article' 
							, 'Electronic Book' 
							, 'Journal Article' 
							, 'Magazine Article' 
							, 'Manuscript' 
							, 'Newspaper Article' 
							, 'Report' 
							, 'Thesis'
						)	
					)">
					<xsl:value-of select="D"/>
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="date">
				<xsl:if test="NUM_8 and (
					   NUM_0='Book' 
					or NUM_0='Conference Paper' 
					or NUM_0='Conference Proceeding' 
					or NUM_0='Edited Book' 
					or NUM_0='Journal Article' 
					or NUM_0='Magazine Article' 
					or NUM_0='Manuscript' 
					or NUM_0='Report' 
					or NUM_0='Thesis' 
					)">
					<xsl:for-each select="tokenize(NUM_8, ' ')">
						<xsl:value-of select="
							if (position()=1) then
							  (
							  	if (.='Jan') then '01' else
							  	if (.='Feb') then '02' else
							  	if (.='Mar') then '03' else
							  	if (.='Apr') then '04' else
							  	if (.='May') then '05' else
							  	if (.='Jun') then '06' else
							  	if (.='Jul') then '07' else
							  	if (.='Aug') then '08' else
							  	if (.='Sep') then '09' else
							  	if (.='Oct') then '10' else
							  	if (.='Nov') then '11' else
							  	'12'
							  )	
							else concat('-', .)	
							   "/>
					</xsl:for-each>			
				</xsl:if>
			</xsl:variable>
			<xsl:if test="$year">
				<!--				TODO: check !!!-->
				<dcterms:created xsi:type="dcterms:W3CDTF"><xsl:value-of select="string-join(($year, $date), '-')"/></dcterms:created>				
			</xsl:if>
			<xsl:if test="NUM_7 and (
				   NUM_0 = 'Journal Article' 
				)">
				<pub:published-online xsi:type="dcterms:W3CDTF"><xsl:value-of select="NUM_7"/></pub:published-online>
			</xsl:if>
			<xsl:if test="NUM_8 and (
				   NUM_0 = 'Newspaper Article' 
				)">
				<dcterms:issued xsi:type="dcterms:W3CDTF"><xsl:value-of select="NUM_8"/></dcterms:issued>
			</xsl:if>
			<xsl:if test="EQUAL">
				<dcterms:modified xsi:type="dcterms:W3CDTF"><xsl:value-of select="EQUAL"/></dcterms:modified>
			</xsl:if>
          	<!-- end of DATES -->



			
			<!-- SOURCE -->
			<xsl:variable name="sourceGenre" select="
				if ( B and NUM_0 = ('Book', 'Edited Book', 'Manuscript') ) then 'series' else
				if ( B and NUM_0 = 'Book Section' ) then 'book' else
				if ( B and NUM_0 = ('Electronic Article', 'Newspaper Article', 'Magazine Article') ) then 'journal' else
				if ( J and NUM_0 = 'Journal Article' ) then 'journal' else
				if ( S and NUM_0 = ('Book Section', 'Conference Proceeding') ) then 'series' else
				''
				"/>
			<!--  
				strength version: source will be created only
				if there is type of it 
			-->   
			<xsl:if test="$sourceGenre!=''">
				<xsl:call-template name="createSource">
					<xsl:with-param name="sgen" select="$sourceGenre"/>
				</xsl:call-template>
			</xsl:if>
			
			
			<!-- ABSTRACT -->
			<xsl:call-template name="createAbstract"/>
			<!-- SUBJECT -->
			<xsl:call-template name="createSubject"/>
			<!--end publication-->
		</xsl:element>
	</xsl:template>
	
	
	
	
	<!-- CREATORS -->
	<xsl:template match="A">
		<xsl:choose>
			<xsl:when test="
				../NUM_0 = (
					'Generic', 
					'Book', 
					'Book Section', 
					'Conference Paper', 
					'Conference Proceeding', 
					'Electronic Article', 
					'Electronic Book', 
					'Journal Article', 
					'Magazine Article',	 
					'Newspaper Article',	 
					'Manuscript', 
					'Report', 
					'Thesis'
				)">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">author</xsl:attribute>
					<xsl:call-template name="createPerson"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="../NUM_0='Edited Book'">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">editor</xsl:attribute>
					<xsl:call-template name="createPerson"/>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="E">
		<xsl:choose>
			<xsl:when test="../NUM_0='Generic'">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">author</xsl:attribute>
					<xsl:call-template name="createPerson"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="../NUM_0 = ('Conference Paper', 'Electronic Book') ">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">editor</xsl:attribute>
					<xsl:call-template name="createPerson"/>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="Y">
		<xsl:choose>
			<xsl:when test="../NUM_0='Generic'">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">author</xsl:attribute>
					<xsl:call-template name="createPerson"/>
				</xsl:element>
			</xsl:when>
			<xsl:when test="../NUM_0='Thesis'">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">advisor</xsl:attribute>
					<xsl:call-template name="createPerson"/>
				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="QUESTION">
		<xsl:if test="../NUM_0 = ('Book', 'Book Section', 'Edited Book')">
			<xsl:element name="pub:creator">
				<xsl:attribute name="role">translator</xsl:attribute>
				<xsl:call-template name="createPerson"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="createPerson">
		<xsl:element name="e:person">
			<xsl:element name="e:family-name">
				<xsl:value-of select="substring-before(.,', ')"/>
			</xsl:element>
			<xsl:element name="e:given-name">
				<xsl:value-of select="substring-after(.,', ')"/>
			</xsl:element>
			<xsl:element name="e:complete-name">
				<xsl:value-of select="."/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
<!--	END OF CREATORS-->

	 
	 
	
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="sgen"/>
		<xsl:element name="pub:source">
			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:value-of select="$sgen"/>
			</xsl:attribute>
			
			<!-- SOURCE TITLE -->
			<xsl:element name="dc:title">
				<xsl:choose>
					<xsl:when test="B"><xsl:value-of select="B"/></xsl:when>
					<xsl:when test="J"><xsl:value-of select="J"/></xsl:when>
				</xsl:choose>
			</xsl:element>

			<!-- SOURCE ALTTITLE -->
			<xsl:for-each select="J[ 
				../NUM_0 = 
				('Book', 'Book Section', 'Manuscript', 'Edited Book', 'Report', 'Journal Article', 'Magazin Article') 
				]">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			
			
			<!-- SOURCE CREATORS -->
			<!-- Persons -->
			<xsl:for-each select="
				E[
					../NUM_0 =   
					('Book', 'Edited Book', 'Report', 'Book Section', 'Conference Proceeding')
				]|
				Y[
					../NUM_0 = 'Conference Proceeding'
				]
				">
				<xsl:element name="pub:creator">
					<xsl:attribute name="role">editor</xsl:attribute>
					<xsl:element name="e:person">
						<xsl:element name="e:complete-name">
							<xsl:value-of select="."/>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
			<!-- Organizations -->
			<xsl:for-each select="E[ 
				../NUM_0 = 'Patent'  
				]">
				<xsl:element name="pub:creator">
<!--					TODO: check -->
					<xsl:attribute name="role">editor????</xsl:attribute>
					<xsl:element name="e:organization">
						<xsl:element name="e:organization-name">
							<xsl:value-of select="."/>
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>

			
			<!-- SOURCE VOLUME -->
			<xsl:if test="N and NUM_0 = ('Book', 'Book Section', 'Edited Book')">
				<xsl:element name="e:volume">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>	
			<xsl:if test="V and not(N) and NUM_0 = ('Book', 'Book Section', 'Edited Book', 'Report')">
				<xsl:element name="e:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and not(NUM_6) and NUM_0 = 'Report'">
				<xsl:element name="e:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="NUM_6 and NUM_0 = 'Report'">
				<xsl:element name="e:volume">
					<xsl:value-of select="NUM_6"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and NUM_0 = ('Generic', ' Conference Paper', ' Conference Proceeding', ' Electronic Article', ' Electronic Book', ' Journal Article', ' Magazine Article', ' Newspaper Article', 'Manuscript')">
				<xsl:element name="e:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>


<!--tuta ostanovilsja!-->
			
			
			<!-- SOURCE ISSUE -->
			<xsl:choose>
				<xsl:when test="IS">
					<xsl:element name="e:issue">
						<xsl:value-of select="IS"/>
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="e:issue">
						<xsl:value-of select="CP"/>
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
			<!-- SOURCE PAGES -->
			<xsl:if test="EP">
				<xsl:element name="e:start-page">
					<xsl:value-of select="SP"/>
				</xsl:element>
				<xsl:element name="e:end-page">
					<xsl:value-of select="EP"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE TOTAL NUMBER OF PAGES -->
			<xsl:if test="not(EP) and SP">
				<xsl:element name="e:total-number-of-pages">
					<xsl:value-of select="SP"/>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:if test="($genre='article' or $genre='paper' or $genre='issue' or $genre='other' or $genre='conference-paper' or $genre='book-item') and (PB or CY)">
				<xsl:element name="e:publishing-info">
					<xsl:element name="e:publisher">
						<xsl:value-of select="PB"/>
					</xsl:element>
					<xsl:element name="e:place">
						<xsl:value-of select="CY"/>
					</xsl:element>
					<xsl:if test="ET and ($genre='book' or $genre='thesis' or $genre='proceedings' or $genre='report')">
						<xsl:element name="e:edition">
							<xsl:value-of select="ET"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			<!-- SOURCE IDENTIFIER -->
			<xsl:if test="SN and not($genre='journal' or $genre='series' or $genre='book' or $genre='thesis' or $genre='proceedings' or $genre='report')">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">
						<xsl:choose>
							<xsl:when test="$genre='series' or $genre='journal'">ISSN</xsl:when>
							<xsl:otherwise>ISBN</xsl:otherwise>
						</xsl:choose>						
					</xsl:attribute>
					<xsl:value-of select="SN"/>
				</xsl:element>
			</xsl:if>			
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="JF">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="JO">
		<xsl:element name="dcterms:alternative">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>	
	<!-- DATES -->
	<xsl:template name="createDate">
		<xsl:choose>
			<xsl:when test="Y1">
				<xsl:apply-templates select="Y1"/>
			</xsl:when>
			<xsl:when test="PY">
				<xsl:apply-templates select="PY"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="Y2"/>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	<xsl:template match="Y1">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
		
	</xsl:template>
	<xsl:template match="PY">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
		
	</xsl:template>
	<xsl:template match="Y2">
		<xsl:element name="dcterms:issued">
			<xsl:call-template name="parseDate"/>
		</xsl:element>
		
	</xsl:template>
	<xsl:template name="parseDate">
		<xsl:variable name="year" select="substring-before(.,'/')"/>
		<xsl:variable name="string-md" select="substring-after(.,'/')"/>
		<xsl:variable name="month" select="substring-before($string-md, '/')"/>
		<xsl:variable name="day" select="substring-after($string-md, '/')"/>
		<xsl:variable name="date">
			<xsl:if test="not($year='')">
				<xsl:value-of select="$year"/>
				<xsl:if test="not($month='')">
					<xsl:value-of select="concat('-',$month)"/>
				</xsl:if>
				<xsl:if test="not($day='')">
					<xsl:value-of select="concat('-',$day)"/>
				</xsl:if>
			</xsl:if>				
		</xsl:variable>
		<xsl:value-of select="$date"/>
	</xsl:template>
	
	<!-- ABSTRACT -->
	<xsl:template name="createAbstract">
		<xsl:choose>
			<xsl:when test="N2">				
				<xsl:element name="dcterms:abstract">
					<xsl:value-of select="N2"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="AB"/>
			</xsl:otherwise>			
		</xsl:choose>		
	</xsl:template>
	<xsl:template match="AB">
		<xsl:element name="dcterms:abstract">
			<xsl:value-of select="."/>
		</xsl:element>
	</xsl:template>
	
	<!-- SUBJECT -->
	<xsl:template name="createSubject">
		<xsl:if test="KW">
			<xsl:element name="dcterms:subject">
				<xsl:value-of select="KW[position()=1]"/>
				<xsl:for-each select="KW[position()>1]">
					<xsl:text>, </xsl:text>
					<xsl:value-of select="."/>
				</xsl:for-each>
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<!-- PUBLISHINGINFO -->
	<xsl:template name="createEdition">
		<xsl:element name="pub:publishing-info">
			<xsl:element name="e:edition">
				<xsl:value-of select="ET"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>


</xsl:stylesheet>