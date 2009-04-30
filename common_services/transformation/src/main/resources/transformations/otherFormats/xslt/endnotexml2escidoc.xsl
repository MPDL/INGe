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
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="${xsd.metadata.escidocprofile.types}"
   xmlns:ei="${xsd.soap.item.item}"
   xmlns:eidt="${xsd.metadata.escidocprofile}idtypes"
   xmlns:srel="${xsd.soap.common.srel}"
   xmlns:prop="${xsd.core.properties}"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:file="${xsd.metadata.file}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:escidoc="urn:escidoc:functions">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="user" select="'dummy:user'"/>
	<xsl:param name="context" select="'dummy:context'"/>
	<xsl:param name="content-model" select="'dummy:content-model'"/>

	<xsl:param name="is-item-list" select="true()"/>
	
	<xsl:param name="refType" />

	<xsl:variable name="genreMap">
			<m key="Book">book</m>
			<m key="Edited Book">book</m>
			<m key="Electronic Book">book</m>
			<m key="Book Section">book-item</m>
			<m key="Conference Paper">conference-paper</m>
			<m key="Conference Proceedings">proceedings</m>
			<m key="Journal Article">article</m>
			<m key="Magazine Article">article</m>
			<m key="Newspaper Article">article</m>
			<m key="Electronic Article">article</m>
			<m key="Report">report</m>
			<m key="Manuscript">manuscript</m>
			<m key="Thesis">thesis</m>
			<m key="Generic">other</m>
	</xsl:variable>
	
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<item-list>
					<xsl:apply-templates select="//item"/>
				</item-list>
			</xsl:when>
			<xsl:when test="count(//item) = 1">
				<xsl:apply-templates select="//item"/>
			</xsl:when>
			<xsl:when test="count(//item) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="//item">
		<xsl:element name="ei:item">
			<xsl:element name="ei:properties">
				<srel:context objid="{$context}" />
				<srel:content-model objid="{$content-model}" />
				<xsl:element name="prop:content-model-specific"/>
			</xsl:element>
			<xsl:element name="mdr:md-records">
				<mdr:md-record name="escidoc">
					<xsl:call-template name="itemMetadata"/>
				</mdr:md-record>
			</xsl:element>
			<xsl:element name="ec:components"/>
		</xsl:element>
	</xsl:template>
		
	
	<!-- GENRE -->
	<xsl:template name="itemMetadata">
	
		<xsl:variable name="refType" select="NUM_0"/>
		
		<xsl:variable name="curGenre" select="$genreMap/m[@key=$refType]" /> 
		<xsl:if test="$curGenre!=''">
			<xsl:call-template name="createEntry">
				<xsl:with-param name="gen" select="$curGenre"/>
			</xsl:call-template>
		</xsl:if>		

	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:variable name="refType" select="NUM_0"/>
		
		<xsl:variable name="sourceGenre" select="
				if ( B and $refType = ('Book', 'Edited Book', 'Manuscript') ) then 'series' else
				if ( B and $refType = 'Book Section' ) then 'book' else
				if ( B and $refType = ('Electronic Article', 'Newspaper Article', 'Magazine Article') ) then 'journal' else
				if ( J and $refType = 'Journal Article' ) then 'journal' else
				if ( S and $refType = ('Book Section', 'Conference Proceedings') ) then 'series' else
				''
				"/>		
		
		<xsl:element name="mdp:publication">
		
			<xsl:attribute name="type">
				<xsl:value-of select="$gen"/>
			</xsl:attribute>
			
			
			<!-- CREATORS -->
			<xsl:call-template name="createCreators"/>

						
			<!-- TITLE -->
			<xsl:element name="dc:title">
				<xsl:value-of select="T"/>
			</xsl:element>
			
			
			<!-- LANGUAGE -->
			<xsl:if test="G">
				<xsl:element name="dc:language">
					<xsl:attribute name="xsi:type">dcterms:RFC3066</xsl:attribute>
					<xsl:value-of select="G"/>
				</xsl:element>
			</xsl:if> 
			
			
			<!--ALTTITLE -->
			<xsl:for-each select="
				B[$refType = ('Generic', 'Electronic Book')]
				|
				F
				|
				J[$refType = ('Book', 'Book Section', 'Manuscript', 'Edited Book', 'Electronic Article', 'Report')]
				|
				Q
				|
				EXCLAMATION
				|
				S[$refType = ('Generic')]
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
					$refType = 'Manuscript'
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
				   $refType = ('Book', 'Book Section', 'Conference Proceedings', 'Edited Book', 'Electronic Book')   
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:ISBN</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
					$refType = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article')			
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:ISSN</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
				      $refType = 'Report'
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="U">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eidt:URI</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<!-- END OF IDENTIFIERS -->
			
			
			<!-- PUBLISHING INFO -->
			<xsl:variable name="publisher">
				<xsl:if test="(B or I) and $refType = 'Thesis'">
					<xsl:value-of select="string-join((B, I), ', ')" />
				</xsl:if>
				<xsl:if test="(I or Y or QUESTION) and $refType = 'Report'">
					<xsl:value-of select="string-join((I, Y, QUESTION), ', ')" />
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="place">
				<xsl:if test="C and $refType = ('Book', 'Book Section', 'Edited Book', 'Electronic Article', 'Electronic Book', 'Manuscript', 'Newspaper Article', 'Report', 'Thesis', 'Magazine Article')">
					<xsl:value-of select="C" />
				</xsl:if>
			</xsl:variable>
			<xsl:variable name="edition">
				<xsl:if test="NUM_7 and $sourceGenre=''">
					<xsl:value-of select="NUM_7" />
				</xsl:if>
			</xsl:variable>
			
			<xsl:if test="concat($publisher, $place, $edition)!=''">
				<xsl:element name="pub:publishing-info">
					<xsl:if test="$publisher!=''">
						<xsl:element name="dc:publisher">
							<xsl:value-of select="$publisher"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="$place!=''">
						<xsl:element name="e:place">
							<xsl:value-of select="$place"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="$edition!=''">
						<xsl:element name="e:edition">
							<xsl:value-of select="$edition"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>			
			<!-- END OF PUBLISHING INFO -->
			
			
			<!-- DATES -->
			<xsl:variable name="year">
				<xsl:if test="D and (
					   $refType = (
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
				<xsl:if test="NUM_8 and 
					   $refType = (
						     'Book'
						   , 'Conference Paper'
						   , 'Conference Proceedings'
						   , 'Edited Book'
						   , 'Journal Article'
						   , 'Magazine Article'
						   , 'Manuscript'  
						   , 'Report' 
						   , 'Thesis' 
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
							else concat('-', if (string-length(.)=1) then concat('0', .) else .  )	
							   "/>
					</xsl:for-each>			
				</xsl:if>
			</xsl:variable>
			<xsl:if test="$year">
				<dcterms:created xsi:type="dcterms:W3CDTF"><xsl:value-of select="concat($year, if ($year!='' and $date!='') then '-' else '', $date)"/></dcterms:created>				
			</xsl:if>
			<xsl:if test="NUM_7 and (
				   $refType = 'Journal Article' 
				)">
				<pub:published-online xsi:type="dcterms:W3CDTF"><xsl:value-of select="NUM_7"/></pub:published-online>
			</xsl:if>
			<xsl:if test="NUM_8 and (
				   $refType = 'Newspaper Article' 
				)">
				<dcterms:issued xsi:type="dcterms:W3CDTF"><xsl:value-of select="NUM_8"/></dcterms:issued>
			</xsl:if>
			<xsl:if test="EQUAL">
				<dcterms:modified xsi:type="dcterms:W3CDTF"><xsl:value-of select="EQUAL"/></dcterms:modified>
			</xsl:if>
          	<!-- end of DATES -->


			<!-- SOURCE -->
			<xsl:if test="$sourceGenre!=''">
				<xsl:call-template name="createSource">
					<xsl:with-param name="sgen" select="$sourceGenre"/>
				</xsl:call-template>
			</xsl:if>
			
			
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:if test="P and $refType = ('Book', 'Edited Book', 'Electronic Book', 'Thesis', 'Generic', 'Conference Proceeding', 'Manuscript', 'Report')">
				<xsl:element name="pub:total-number-of-pages">
					<xsl:value-of select="P"/>
				</xsl:element>
			</xsl:if>			
			<xsl:if test="AMPERSAND and $refType = 'Book'">
				<xsl:element name="pub:total-number-of-pages">
					<xsl:value-of select="AMPERSAND"/>
				</xsl:element>
			</xsl:if>			
			
			
			<!-- EVENT -->
			<xsl:if test="B and $refType = ('Conference Paper', 'Conference Proceedings')">
				<xsl:element name="pub:event">
					<xsl:element name="dc:title">
						<xsl:value-of select="B"/>
					</xsl:element>
					<xsl:if test="D and $refType = 'Conference Proceedings'">
						<xsl:element name="e:start-date">
							<xsl:value-of select="D"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="C">
						<xsl:element name="e:place">
							<xsl:value-of select="C"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			
			
			<!-- DEGREE -->
			<xsl:if test="V and $refType = 'Thesis'">
				<xsl:element name="pub:degree">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="NUM_9 and $refType = 'Thesis'">
				<xsl:element name="pub:degree">diploma</xsl:element>
			</xsl:if>
			
			
			<!-- ABSTRACT -->
			<xsl:if test="X">
				<xsl:element name="dcterms:abstract">
					<xsl:value-of select="X"/>
				</xsl:element>
			</xsl:if>
			
			
			<!-- SUBJECT -->
			<xsl:if test="K">
				<xsl:element name="dcterms:subject">
					<xsl:value-of select="K"/>
				</xsl:element>
			</xsl:if>
			
			
			<!-- LOCATION -->
			<xsl:if test="I and $refType = 'Manuscript'">
				<xsl:element name="pub:location">
					<xsl:value-of select="I"/>
				</xsl:element>
			</xsl:if>
			
			
		</xsl:element>
		
	</xsl:template>
	
	
	
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="sgen"/>
		<xsl:variable name="refType" select="NUM_0"/>
		
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
				$refType = 
					('Journal Article', 'Magazin Article') 
				]">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			
			
			<!-- SOURCE CREATORS -->
			<xsl:for-each select="
				E[
					$refType =   
					('Book', 'Edited Book', 'Report', 'Book Section', 'Conference Proceedings')
				]
				|
				Y[
					$refType = ('Conference Proceedings', 'Book Section')
				]
				">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="role" select="'editor'"/>
					<xsl:with-param name="isSource" select="true()"/>
				</xsl:call-template>
			</xsl:for-each>

			
			<!-- SOURCE VOLUME -->
			<xsl:if test="N and $refType = ('Book', 'Book Section', 'Edited Book')">
				<xsl:element name="e:volume">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>	
			<xsl:if test="V and not(N) and $refType = ('Book', 'Book Section', 'Edited Book', 'Report')">
				<xsl:element name="e:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and $refType = ('Generic', ' Conference Paper', ' Conference Proceedings', ' Electronic Article', ' Electronic Book', ' Journal Article', ' Magazine Article', ' Newspaper Article', 'Manuscript')">
				<xsl:element name="e:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and not(NUM_6) and $refType = 'Report'">
				<xsl:element name="e:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="NUM_6 and $refType = 'Report'">
				<xsl:element name="e:volume">
					<xsl:value-of select="NUM_6"/>
				</xsl:element>
			</xsl:if>

			
			<!-- SOURCE ISSUE -->
			<xsl:if test="N and $refType = ('Electronic Article', 'Journal Article', 'Generic', 'Magazine Article')">
				<xsl:element name="e:issue">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>
			
			
			<!-- SOURCE PAGES -->
			<xsl:if test="P and $refType = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article', 'Book Section', 'Conference Paper' )">
				<xsl:variable name="pages" select="tokenize(normalize-space(P), '[-–]+')"/>
				<xsl:if test="count($pages)>=1 and $pages[1]!=''">
					<xsl:element name="e:start-page">
						<xsl:value-of select="$pages[1]"/>								
					</xsl:element>						
				</xsl:if>
				<xsl:if test="count($pages)=2 and $pages[2]!=''">
					<xsl:element name="e:end-page">
						<xsl:value-of select="$pages[2]"/>								
					</xsl:element>						
				</xsl:if>
			</xsl:if>			
			<xsl:if test="N and not(P) and $refType = 'Newspaper Article'">
				<xsl:element name="e:start-page">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="AMPERSAND and not(P) and $refType = ('Journal Article', 'Magazine Article', 'Manuscript')">
				<xsl:element name="e:start-page">
					<xsl:value-of select="AMPERSAND"/>
				</xsl:element>
			</xsl:if>

			
			<!-- SOURCE SEQUENCE NUMBER -->
			<xsl:if test="N and $refType = 'Report'">
				<xsl:element name="e:sequence-number">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="M and $refType = 'Manuscript'">
				<xsl:element name="e:sequence-number">
					<xsl:value-of select="M"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="AMPERSAND and $refType = 'Book Section'">
				<xsl:element name="e:sequence-number">
					<xsl:value-of select="AMPERSAND"/>
				</xsl:element>
			</xsl:if>
				
					
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:if test="I and $refType = ('Generic', 'Book', 'Book Section', 'Conference Paper', 'Conference Proceedings', 'Edited Book', 'Electronic Article', 'Electronic Book', 'Magazine Article', 'Newspaper Article')">
				<xsl:element name="e:publishing-info">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="I"/>
					</xsl:element>
					<xsl:if test="NUM_7 and $refType = ('Book Section', 'Electronic Article', 'Magazine Article', 'Newspaper Article', 'Report')">
						<xsl:element name="e:edition">
							<xsl:value-of select="NUM_7"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
		</xsl:element>
		
	</xsl:template>
	<!-- END OF SOURCE -->
	
	
	<!-- CREATORS -->
	<xsl:template name="createCreators">
		<xsl:variable name="refType" select="NUM_0"/>
		<xsl:for-each select="A|E|Y|QUESTION">
			<xsl:if test="name(.)='A'">
				<xsl:choose>
					<xsl:when test="
						$refType = (
							'Generic', 
							'Book', 
							'Book Section', 
							'Conference Paper', 
							'Conference Proceedings', 
							'Electronic Article', 
							'Electronic Book', 
							'Journal Article', 
							'Magazine Article',	 
							'Newspaper Article',	 
							'Manuscript', 
							'Report', 
							'Thesis'
						)">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="'author'"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$refType='Edited Book'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="'editor'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			
			<xsl:if test="name(.)='E'">
				<xsl:choose>
					<xsl:when test="$refType='Generic'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="'author'"/>
						</xsl:call-template>					
					</xsl:when>
					<xsl:when test="$refType = ('Conference Paper', 'Electronic Book') ">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="'editor'"/>
						</xsl:call-template>					
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			
			<xsl:if test="name(.)='Y'">
				<xsl:choose>
					<xsl:when test="$refType='Generic'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="'author'"/>
						</xsl:call-template>					
					</xsl:when>
					<xsl:when test="$refType='Thesis'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="'advisor'"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			
			<xsl:if test="name(.)='QUESTION'">
				<xsl:if test="$refType = ('Book', 'Book Section', 'Edited Book')">
					<xsl:call-template name="createCreator">
						<xsl:with-param name="role" select="'translator'"/>
					</xsl:call-template>					
				</xsl:if>
			</xsl:if>		
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="createCreator">
		<xsl:param name="role"/>
		<xsl:param name="isSource"/>
		<xsl:if test="$isSource">
			<xsl:element name="e:creator">
				<xsl:attribute name="role"><xsl:value-of select="$role"/></xsl:attribute>
				<xsl:call-template name="createPerson">
					<xsl:with-param name="isSource" select="$isSource"/>
				</xsl:call-template>				
			</xsl:element>
		</xsl:if>
		<xsl:if test="not($isSource)">
			<xsl:element name="pub:creator">
				<xsl:attribute name="role"><xsl:value-of select="$role"/></xsl:attribute>
				<xsl:call-template name="createPerson">
					<xsl:with-param name="isSource" select="$isSource"/>
				</xsl:call-template>				
			</xsl:element>
		</xsl:if>
	</xsl:template>	
	
	<xsl:template name="createPerson">
		<xsl:param name="isSource"/>
		<xsl:element name="e:person">
			<xsl:element name="e:family-name">
				<xsl:value-of select="substring-before( ., ', ' )"/>
			</xsl:element>
			<xsl:element name="e:given-name">
				<xsl:value-of select="substring-after( ., ', ' )"/>
			</xsl:element>
			<xsl:element name="e:complete-name">
				<xsl:value-of select="."/>
			</xsl:element>
			<xsl:if test="not($isSource) and position()=1">
				<e:organization>
					<e:organization-name>External Organizations</e:organization-name>
					<e:identifier>${escidoc.pubman.external.organisation.id}</e:identifier>
				</e:organization>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	
	
<!--	END OF CREATORS-->	
	

</xsl:stylesheet>