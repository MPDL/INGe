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


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from EndNote Item to eSciDoc PubItem 
	See http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_Endnote_Mapping and
	http://colab.mpdl.mpg.de/mediawiki/Talk:PubMan_Func_Spec_Endnote_Mapping#revised_mapping
	Author: Vlad Makarenko (initial creation) 
	$Author: mfranke $ (last changed)
	$Revision: 2750 $ 
	$LastChangedDate: 2010-02-05 11:38:47 +0100 (Fri, 05 Feb 2010) $
-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:ei="${xsd.soap.item.item}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:prop="${xsd.core.properties}"
	xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
	xmlns:ec="${xsd.soap.item.components}"
	xmlns:file="${xsd.metadata.file}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:organization="${xsd.metadata.organization}"		
	xmlns:eterms="${xsd.metadata.terms}"   
	xmlns:escidoc="urn:escidoc:functions"
	xmlns:AuthorDecoder="java:de.mpg.escidoc.services.common.util.creators.AuthorDecoder"
	xmlns:Util="java:de.mpg.escidoc.services.transformation.Util"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:esc="http://purl.org/escidoc/metadata/terms/0.1/"
	xmlns:itemlist="${xsd.soap.item.itemlist}"
	xmlns:eprints="http://purl.org/eprint/terms/">
   

	<xsl:import href="../../vocabulary-mappings.xsl"/>
	

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="user" select="'dummy:user'"/>
	<xsl:param name="context" select="'dummy:context'"/>
	<xsl:param name="content-model" select="'dummy:content-model'"/>
	<xsl:param name="root-ou"/>
	<xsl:param name="external-ou"/>
	
	<!-- Configuration parameters -->
	<xsl:param name="Flavor" select="'OTHER'"/>
	<xsl:param name="CoNE" select="'false'"/>

	<xsl:param name="is-item-list" select="true()"/>
	<xsl:param name="source-name" select="''"/>
	
	<xsl:param name="refType" />
	
	<xsl:param name="Organisation" select="''"/>

	<xsl:variable name="vm" select="document('../../ves-mapping.xml')/mappings"/>


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
				<itemlist:item-list>
					<xsl:apply-templates select="//item"/>
				</itemlist:item-list>
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
		</xsl:element>
	</xsl:template>
		
	
	<!-- GENRE -->
	<xsl:template name="itemMetadata">
	
		<xsl:variable name="refType" select="normalize-space(NUM_0)"/>
		
		<xsl:variable name="curGenre" select="$genreMap/m[@key=$refType]" />
		<xsl:variable name="curGenreURI" select="$genre-ves/enum[.=$curGenre]/@uri"/>
		
		<xsl:choose>
			<xsl:when test="$refType=''">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoGenreFound' ), 'Endnote import must have a filled &quot;%0&quot; type to describe the publication genre.')"/>
			</xsl:when>
			<xsl:when test="$curGenre != ''">
				<xsl:call-template name="createEntry">
					<xsl:with-param name="gen" select="$curGenreURI"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NotMappedGenre' ), concat('Endnote genre: ', $refType,' is not mapped.'))"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>
	
	<!-- Create eSciDoc Entry -->
	<xsl:template name="createEntry">
		<xsl:param name="gen"/>
		
		<xsl:variable name="refType" select="normalize-space(NUM_0)"/>
		
		<xsl:variable name="sourceGenre" select="
				if ( B and $refType = ('Book', 'Edited Book', 'Manuscript', 'Report') ) then $genre-ves/enum[.='series']/@uri else
				if ( B and $refType = 'Book Section' ) then $genre-ves/enum[.='book']/@uri else
				if ( B and $refType = ('Electronic Article', 'Newspaper Article', 'Magazine Article') ) then $genre-ves/enum[.='journal']/@uri else
				if ( B and $refType = 'Conference Paper' ) then $genre-ves/enum[.='proceedings']/@uri else
				if ( J and $refType = 'Journal Article' ) then $genre-ves/enum[.='journal']/@uri else
				if ( S and $refType = ('Book Section', 'Conference Proceedings') ) then $genre-ves/enum[.='series']/@uri else
				''
				"/>
				
		<xsl:variable name="secondSourceGenre" select="
				if ( S and $sourceGenre = $genre-ves/enum[.='book']/@uri) then $genre-ves/enum[.='series']/@uri else 
				''
				" />
				
		
		
		<xsl:element name="pub:publication">
		
			<xsl:attribute name="type">
				<xsl:value-of select="$gen"/>
			</xsl:attribute>
			
			
			<!-- CREATORS -->
			<xsl:call-template name="createCreators"/>

						
			<!-- TITLE -->
			<xsl:variable name="vol" select="
				if ($refType = ('Book', 'Edited Book') and N and V) then concat(' vol. ', V) else ''
			"/>
			<xsl:element name="dc:title">
				<xsl:value-of select="concat(T, $vol)"/>
			</xsl:element>
			
			
			<!-- LANGUAGE -->
			<!-- 
25: %G English 
62: %G Englisch 
91: %G Language: eng 
120: %G Language: eng 
148: %G eng 
172: %G Language: eng 
202: %G French; Summaries in English. 
245: %G eng 
282: %G Language: eng 
308: %G English 
329: %G English 
377: %G eng 
418: %G Language: eng 
463: %G eng 
499: %G eng 
543: %G de 
579: %G english 
607: %G eng 
639: %G eng 
673: %G eng 
709: %G eng 
743: %G eng 
773: %G Language: eng 
798: %G eng 
826: %G de 	
 -->		
			<xsl:if test="G">
				<xsl:variable name="g" select="G"/>
				<xsl:if test="$vm/language/v1-to-v2/map[$g=.]!=''">
					<xsl:element name="dc:language">
						<xsl:attribute name="xsi:type">dcterms:RFC3066</xsl:attribute>
						<xsl:value-of select="G"/>
					</xsl:element>
				</xsl:if>
			</xsl:if> 

			
			
			<!--ALTTITLE -->
			<xsl:for-each select="
				B[$refType = ('Generic', 'Electronic Book')]
				|
				O[$refType = ('Book', 'Book Section', 'Manuscript', 'Edited Book', 'Electronic Article', 'Report')]
				|
				Q
				|
				EXCLAMATION[../T!=.]
				|
				S[$refType = ('Generic')]
				| 
				STAR[$refType = ('Generic', 'Book Section', 'Journal Article', 'Magazine Article', 'Newspaper Article')]
				">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="if (name(.)='STAR') then concat('Review of: ', .) else ."/>
				</xsl:element>
			</xsl:for-each>
			
			
									
			<!-- IDENTIFIERS -->
			<xsl:for-each select="L">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="M">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type" select="
						if (substring(., 1, 4) = 'ISI:') then 'eterms:ISI' 
						else  'eterms:OTHER'
					"/>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="R">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:DOI</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="NUM_6[
					$refType = 'Manuscript'
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
				   $refType = ('Book', 'Conference Proceedings', 'Edited Book', 'Electronic Book')   
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
					$refType = ('Book Section') and $sourceGenre != $genre-ves/enum[.='book']/@uri	
				]">
				<dc:identifier>
					<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
					<xsl:value-of select="."/>
				</dc:identifier>
			</xsl:for-each>
			<xsl:for-each select="AT[
					$refType = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article')			
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:ISSN</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="AT[
				      $refType = 'Report'
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="U">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:URI</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<!-- END OF IDENTIFIERS -->
			
			<!-- PUBLISHING INFO -->
			<xsl:variable name="publisher" select="
				if (B and I and $refType = 'Thesis') then string-join((B, I), ', ')
				else if (I and $refType = ('Book', 'Conference Proceedings', 'Edited Book', 'Electronic Book', 'Generic', 'Thesis' )) then I
				else if ((I or Y or QUESTION) and $refType = 'Report') then string-join((I, Y, QUESTION), ', ')
				else ''
			"/>
			 
			<xsl:if test="$publisher!=''">
				<xsl:element name="eterms:publishing-info">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="$publisher"/>
					</xsl:element>
					<xsl:variable name="place" select="
						if (C and $refType = ('Book', 'Edited Book', 'Electronic Book', 'Manuscript', 'Report', 'Thesis', 'Magazine Article', 'Generic')) then C
						else ''
					"/>
					<xsl:if test="$place!=''">
						<xsl:element name="eterms:place">
							<xsl:value-of select="$place"/>
						</xsl:element>
					</xsl:if>
					<xsl:variable name="edition" select="
						if (NUM_7 and $sourceGenre='' and $refType = ('Book', 'Conference Proceedings', 'Edited Book', 'Electronic Book', 'Generic', 'Report')) then NUM_7
						else if (ROUND_RIGHT_BRACKET and not(NUM_7)and $refType = ('Book', 'Edited Book', 'Generic')) then ROUND_RIGHT_BRACKET
						else ''
					"/>
					<xsl:if test="$edition!=''">
						<xsl:element name="eterms:edition">
							<xsl:value-of select="$edition"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>			
			<!-- END OF PUBLISHING INFO -->
			
			
			<!-- DATES -->
			<xsl:variable name="dateCreated" select="
				if (D) then escidoc:normalizeDate(D)
				else if (NUM_8) then escidoc:normalizeDate(NUM_8)
				else ''
			"/>
			
			<xsl:if test="$dateCreated!=''">
				<dcterms:issued xsi:type="dcterms:W3CDTF"><xsl:value-of select="$dateCreated"/></dcterms:issued>				
			</xsl:if>
			
			<xsl:variable name="datePublishedOnline" select="
				if (NUM_7 and $refType = 'Journal Article') then escidoc:normalizeDate(NUM_7)
				else ''
			"/>
			<xsl:if test="$datePublishedOnline!=''">
				<eterms:published-online xsi:type="dcterms:W3CDTF"><xsl:value-of select="$datePublishedOnline"/></eterms:published-online>			
			</xsl:if>
			
			<xsl:variable name="dateModified" select="
				if (EQUAL) then escidoc:normalizeDate(EQUAL)
				else ''
			"/>
			<xsl:if test="$dateModified!=''">
				<dcterms:modified xsi:type="dcterms:W3CDTF"><xsl:value-of select="$dateModified"/></dcterms:modified>
			</xsl:if>
          	<!-- end of DATES -->


			<!-- SOURCE -->
			<xsl:if test="$sourceGenre!=''">
				<xsl:call-template name="createSource">
					<xsl:with-param name="sgen" select="$sourceGenre"/>
					<xsl:with-param name="identifier" select="AT" />
				</xsl:call-template>
			</xsl:if>
			
			
			<!-- SECOND SOURCE -->
			<xsl:if test="$secondSourceGenre = $genre-ves/enum[.='series']/@uri">
				<xsl:call-template name="createSecondSource">
					<xsl:with-param name="ssgen" select="$secondSourceGenre"/>
				</xsl:call-template>
			</xsl:if>
			
			
			<!-- TOTAL NUMBER OF PAGES -->
			<xsl:if test="P and $refType = ('Book', 'Edited Book', 'Electronic Book', 'Thesis', 'Generic', 'Conference Proceedings', 'Manuscript', 'Report')">
				<xsl:element name="eterms:total-number-of-pages">
					<xsl:value-of select="P"/>
				</xsl:element>
			</xsl:if>			
			<xsl:if test="AMPERSAND and $refType = 'Book'">
				<xsl:element name="eterms:total-number-of-pages">
					<xsl:value-of select="AMPERSAND"/>
				</xsl:element>
			</xsl:if>			
			
			
			<!-- EVENT -->
			<xsl:if test="B and $refType = ('Conference Paper', 'Conference Proceedings')">
				<xsl:element name="event:event">
					<xsl:element name="dc:title">
						<xsl:value-of select="B"/>
					</xsl:element>
					<xsl:if test="D and $refType = 'Conference Proceedings'">
						<xsl:element name="eterms:start-date">
							<xsl:value-of select="D"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="C">
						<xsl:element name="eterms:place">
							<xsl:value-of select="C"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			
			
			<!-- DEGREE -->
			<!-- ??????? Check! -->
			<xsl:if test="$refType = 'Thesis'">
				<xsl:variable name="dgr" select="escidoc:normalizeDegree(V)"/>
				<xsl:variable name="dgr" select="$degree-ves/enum[$dgr=.]/@uri"/>
				<xsl:if test="$dgr!=''">
					<xsl:element name="eterms:degree">
						<xsl:value-of select="$dgr"/>
					</xsl:element>
				</xsl:if>
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
			
			<!-- tableOfContents-->
<!--			<xsl:if test="ROUND_LEFT_BRACKET and $refType='Report'">-->
<!--				<xsl:element name="dcterms:tableOfContents">-->
<!--					<xsl:value-of select="ROUND_LEFT_BRACKET"/>-->
<!--				</xsl:element>-->
<!--			</xsl:if>-->
			<xsl:if test="SLASH and $refType='Report'">
				<xsl:element name="dcterms:tableOfContents">
					<xsl:value-of select="SLASH"/>
				</xsl:element>
			</xsl:if>
			
			<!-- LOCATION -->
<!--			<xsl:if test="I and $refType = 'Manuscript'">-->
<!--				<xsl:element name="eterms:location">-->
<!--					<xsl:value-of select="I"/>-->
<!--				</xsl:element>-->
<!--			</xsl:if>-->
			
		</xsl:element>
		
	</xsl:template>
	
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="sgen"/>
		<xsl:param name="identifier"/>
		
		<xsl:variable name="refType" select="normalize-space(NUM_0)"/>
		
		<xsl:element name="source:source">

			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:value-of select="$sgen"/>
			</xsl:attribute>

			<!-- SOURCE TITLE -->
			<xsl:element name="dc:title">
				<xsl:choose>
					<xsl:when test="B">
						<xsl:value-of select="B"/>
					</xsl:when>
					<xsl:when test="J and $refType = ('Journal Article', 'Magazine Article')">
						<xsl:value-of select="J"/>
					</xsl:when>
					<xsl:when test="S and $refType = ('Conference Proceedings')">
						<xsl:value-of select="S"/>
					</xsl:when>
				</xsl:choose>
			</xsl:element>


			<!-- SOURCE ALTTITLE -->
			<xsl:for-each select="
				J[
					exists(../B)
						and
					$refType = ('Journal Article', 'Magazine Article') 
				]
				|
				O[
					$refType = ('Journal Article', 'Magazine Article')				
				]
				">
				<xsl:element name="dcterms:alternative">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			
			
			<!-- SOURCE CREATORS -->
			<xsl:for-each select="
				E[
					$refType =   
					('Book', 'Edited Book', 'Report', 'Conference Proceedings', 'Book Section')
				]
				|
				Y[
					$refType = ('Conference Proceedings')
				]
				">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
					<xsl:with-param name="isSource" select="true()"/>
				</xsl:call-template>
			</xsl:for-each>

			
			<!-- SOURCE VOLUME -->
			<xsl:if test="N and $refType = ('Book', 'Edited Book')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>	
			<xsl:if test="V and not(N) and $refType = ('Book', 'Edited Book')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			
			<xsl:if test="V and $refType = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and B and $refType = ('Manuscript')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and S and $refType = ('Conference Proceedings')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			
			<xsl:if test="V and not(N) and B and $refType = 'Report'">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and not(NUM_6 or N) and B and $refType = 'Report'">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>

			
			<!-- SOURCE ISSUE -->  
			<xsl:if test="N and $refType = ('Magazine Article', 'Electronic Article', 'Journal Article')">
				<xsl:element name="eterms:issue">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>
			
			
			<!-- SOURCE PAGES -->
			<xsl:if test="P and $refType = ('Electronic Article', 'Journal Article', 'Magazine Article', 'Newspaper Article', 'Book Section', 'Conference Paper' )">
				<xsl:variable name="pages" select="tokenize(normalize-space(P), '[-–]+')"/>
				<xsl:if test="count($pages)>=1 and $pages[1]!=''">
					<xsl:element name="eterms:start-page">
						<xsl:value-of select="$pages[1]"/>								
					</xsl:element>						
				</xsl:if>
				<xsl:if test="count($pages)=2 and $pages[2]!=''">
					<xsl:element name="eterms:end-page">
						<xsl:value-of select="$pages[2]"/>								
					</xsl:element>						
				</xsl:if>
			</xsl:if>			
			<xsl:if test="N and not(P) and $refType = 'Newspaper Article'">
				<xsl:element name="eterms:start-page">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="AMPERSAND and not(P) and $refType = ('Journal Article', 'Magazine Article', 'Manuscript')">
				<xsl:element name="eterms:start-page">
					<xsl:value-of select="AMPERSAND"/>
				</xsl:element>
			</xsl:if>

			
			<!-- SOURCE SEQUENCE NUMBER -->
			<xsl:if test="N and B and $refType = ('Report', 'Manuscript')">
				<xsl:element name="eterms:sequence-number">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="AMPERSAND and $refType = 'Book Section'">
				<xsl:element name="eterms:sequence-number">
					<xsl:value-of select="AMPERSAND"/>
				</xsl:element>
			</xsl:if>
				
					
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:variable name="publisher" select="
				if (I and $refType = ('Book Section', 'Conference Paper', 'Electronic Article', 'Magazine Article', 'Newspaper Article', 'Journal Article')) then I else ''
			"/>
			<xsl:if test="$publisher!=''">
				<xsl:element name="eterms:publishing-info">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="$publisher"/>
					</xsl:element>
					<xsl:variable name="place" select="
						if (C and $refType = ('Book Section', 'Newspaper Article')) then C else ''
					"/>
					<xsl:if test="$place!=''">
						<xsl:element name="eterms:place">
							<xsl:value-of select="$place"/>
						</xsl:element>
					</xsl:if>
					<xsl:variable name="edition" select="
						if (NUM_7 and $refType = ('Book Section', 'Electronic Article', 'Magazine Article', 'Newspaper Article', 'Report')) then NUM_7
						else if (ROUND_RIGHT_BRACKET and not(NUM_7) and $refType = ('Book Section', 'Magazine Article')) then ROUND_RIGHT_BRACKET 
						else ''
					"/>
					<xsl:if test="$edition!=''">
						<xsl:element name="eterms:edition">
							<xsl:value-of select="$edition"/>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:if>
			
			<!--  SOURCE IDENTIFIER -->
			<xsl:if test="$identifier and $refType = ('Book Section') and $sgen = $genre-ves/enum[.='book']/@uri">
				<dc:identifier>
					<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
					<xsl:value-of select="$identifier"/>
				</dc:identifier>
			</xsl:if>
			
		</xsl:element>	
			
	</xsl:template>
	<!-- END OF SOURCE -->
	
	<!-- SECOND SOURCE -->
	<xsl:template name="createSecondSource">
		<xsl:param name="ssgen"/>
		<xsl:variable name="refType" select="normalize-space(NUM_0)"/>
		
		<source:source>


			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:value-of select="$ssgen"/>
			</xsl:attribute>
			

			<!-- SOURCE TITLE -->
			<dc:title>
				<xsl:choose>
					<xsl:when test="S">
						<xsl:value-of select="S"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSeriesTitle' ), concat('There is more than one CoNE entry matching -', $ssgen))"/>
						</xsl:otherwise>
				</xsl:choose>
			</dc:title>
			<xsl:for-each select="
				Y[
					$refType = ('Book Section')
				]
				">
				<xsl:call-template name="createCreator">
					<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
					<xsl:with-param name="isSource" select="true()"/>
				</xsl:call-template>
			</xsl:for-each>
		</source:source>
	</xsl:template>
	<!-- END OF SECOND SOURCE -->
	
	
	<!-- CREATORS -->
	<xsl:template name="createCreators">
		<xsl:variable name="refType" select="normalize-space(NUM_0)"/>
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
						<xsl:variable name="currentAuthorPosition" select="position()"/>
						<xsl:comment>--Author --</xsl:comment>
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="$creator-ves/enum[.='author']/@uri"/>
							<xsl:with-param name="pos" select="count(../A[position() &lt; $currentAuthorPosition]) + 1"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$refType='Edited Book'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			
			<xsl:if test="name(.)='E'">
				<xsl:choose>
					<xsl:when test="$refType='Generic'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="$creator-ves/enum[.='contributor']/@uri"/>
						</xsl:call-template>					
					</xsl:when>
					<xsl:when test="$refType = ('Conference Proceedings', 'Conference Paper', 'Electronic Book')">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
						</xsl:call-template>					
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			<xsl:if test="name(.)='Y'">
				<xsl:choose>
					<xsl:when test="$refType='Generic' or ($refType='Conference Proceedings' and S)">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="$creator-ves/enum[.='contributor']/@uri"/>
						</xsl:call-template>					
					</xsl:when>
					<xsl:when test="$refType='Thesis'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="$creator-ves/enum[.='advisor']/@uri"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			
			<xsl:if test="name(.)='QUESTION'">
				<xsl:if test="$refType = ('Book', 'Book Section', 'Edited Book')">
					<xsl:call-template name="createCreator">
						<xsl:with-param name="role" select="$creator-ves/enum[.='translator']/@uri"/>
					</xsl:call-template>					
				</xsl:if>
				<xsl:if test="$refType = ('Generic')">
					<xsl:call-template name="createCreator">
						<xsl:with-param name="role" select="$creator-ves/enum[.='contributor']/@uri"/>
					</xsl:call-template>					
				</xsl:if>
			</xsl:if>		
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="createCreator">
		<xsl:param name="role"/>
		<xsl:param name="isSource"/>
		<xsl:param name="pos" select="0"/>
		
		<xsl:choose>
			<xsl:when test="$isSource">
				<xsl:element name="eterms:creator">
					<xsl:attribute name="role"><xsl:value-of select="$role"/></xsl:attribute>
					<xsl:call-template name="createPerson">
						<xsl:with-param name="isSource" select="$isSource"/>
					</xsl:call-template>				
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="eterms:creator">
					<xsl:attribute name="role"><xsl:value-of select="$role"/></xsl:attribute>
					<xsl:call-template name="createPerson">
						<xsl:with-param name="isSource" select="$isSource"/>
						<xsl:with-param name="pos" select="$pos"/>
					</xsl:call-template>				
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	
	
	<xsl:template name="createPerson">
		<xsl:param name="isSource"/>
		<xsl:param name="pos" select="0"/>
		
		<xsl:variable name="person" select="AuthorDecoder:parseAsNode(.)/authors/author[1]"/>
		<xsl:choose>
			<xsl:when test="$CoNE = 'false'">
				<xsl:element name="person:person">
					<xsl:element name="eterms:family-name">
						<xsl:value-of select="$person/familyname"/>
					</xsl:element>
					<xsl:element name="eterms:given-name">
						<xsl:value-of select="$person/givenname"/>
					</xsl:element>
					<xsl:choose>
						<xsl:when test="not($isSource)">
							<organization:organization>
								<dc:title>Max Planck Society</dc:title>
								<dc:identifier><xsl:value-of select="$root-ou"/></dc:identifier>
							</organization:organization>
						</xsl:when>
					</xsl:choose>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="cone-creator" select="Util:queryCone('persons', concat($person/familyname, ', ', $person/givenname))"/>

				<xsl:variable name="multiplePersonsFound" select="exists($cone-creator/cone/rdf:RDF/rdf:Description[@rdf:about != preceding-sibling::attribute/@rdf:about])"/>
			
				<xsl:if test="$multiplePersonsFound">
					<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleCreatorsFound' ), concat('There is more than one CoNE entry matching -', concat($person/familyname, ', ', $person/givenname), '-'))"/>
				</xsl:if>
				<xsl:element name="person:person">
					<xsl:element name="eterms:family-name">
						<xsl:value-of select="$person/familyname"/>
					</xsl:element>
					<xsl:element name="eterms:given-name">
						<xsl:value-of select="$person/givenname"/>
					</xsl:element>
					<xsl:choose>
						<xsl:when test="exists($cone-creator/cone/rdf:RDF/rdf:Description/esc:position)">
							<xsl:for-each select="$cone-creator/cone/rdf:RDF[1]/rdf:Description/esc:position">
								<organization:organization>
									<dc:title>
										<xsl:value-of select="rdf:Description/eprints:affiliatedInstitution"/>
									</dc:title>
									<dc:identifier>
										<xsl:value-of select="rdf:Description/dc:identifier"/>
									</dc:identifier>
								</organization:organization>
							</xsl:for-each>
						</xsl:when>
						<xsl:when test="not($isSource)">
							<organization:organization>
								<dc:title>Max Planck Society</dc:title>
								<dc:identifier><xsl:value-of select="$root-ou"/></dc:identifier>
							</organization:organization>
						</xsl:when>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="exists($cone-creator/cone/rdf:RDF/rdf:Description)">
							<dc:identifier xsi:type="CONE"><xsl:value-of select="$cone-creator/cone/rdf:RDF[1]/rdf:Description[1]/@rdf:about"/></dc:identifier>
						</xsl:when>
					</xsl:choose>
					
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
<!--	END OF CREATORS-->	
	
	<xsl:template name="component">
		<xsl:param name="oa" select="false()"/>
		
<!--		<xsl:variable name="suffix">-->
<!--			<xsl:choose>-->
<!--				<xsl:when test="contains(., '.')">-->
<!--					<xsl:value-of select="substring-after(., '.')"/>-->
<!--				</xsl:when>-->
<!--				<xsl:otherwise>pdf</xsl:otherwise>-->
<!--			</xsl:choose>-->
<!--		</xsl:variable>-->
		
<!--		<xsl:variable name="filename">-->
<!--			<xsl:choose>-->
<!--				<xsl:when test="contains(., '.')">-->
<!--					<xsl:value-of select="."/>-->
<!--				</xsl:when>-->
<!--				<xsl:otherwise>-->
<!--					<xsl:value-of select="."/>.<xsl:value-of select="$suffix"/>-->
<!--				</xsl:otherwise>-->
<!--			</xsl:choose>-->
<!--		</xsl:variable>-->
		
<!--		<xsl:variable name="mimetype">-->
<!--			<xsl:value-of select="Util:getMimetype($suffix)"/>-->
<!--		</xsl:variable>-->
	
		<!-- ec:component>
            <ec:properties xmlns:xlink="http://www.w3.org/1999/xlink">               
                <prop:visibility>
					<xsl:choose>
						<xsl:when test="$oa">public</xsl:when>
						<xsl:otherwise>private</xsl:otherwise>
					</xsl:choose>
				</prop:visibility>
                <prop:content-category>
                	<xsl:choose>
	                	<xsl:when test="contains(., 's')">supplementary-material</xsl:when>
	                	<xsl:otherwise>any-fulltext</xsl:otherwise>
					</xsl:choose>
                </prop:content-category>
                <prop:file-name><xsl:value-of select="$filename"/></prop:file-name>
                <prop:mime-type><xsl:value-of select="$mimetype"/></prop:mime-type>
            </ec:properties>
            <ec:content xlink:type="simple" xlink:title="{.}.{$suffix}" xlink:href="{$fulltext-location}{$filename}" storage="internal-managed"/>
            <mdr:md-records xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}">
            	<mdr:md-record name="escidoc">
            		<file:file xmlns:file="${xsd.metadata.file}" xmlns:dc="${xsd.metadata.dc}" xmlns:dcterms="${xsd.metadata.dcterms}" xmlns:e="${xsd.metadata.escidocprofile.types}" xmlns:eidt="${xsd.metadata.escidocprofile.idtypes}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
						<dc:title><xsl:value-of select="$filename"/></dc:title>
						<file:content-category>
							<xsl:choose>
			                	<xsl:when test="contains(., 's')">supplementary-material</xsl:when>
			                	<xsl:otherwise>any-fulltext</xsl:otherwise>
							</xsl:choose>
						</file:content-category>
						<dc:format xsi:type="dcterms:IMT"><xsl:value-of select="$mimetype"/></dc:format>
						<dcterms:extent><xsl:value-of select="Util:getSize(concat($fulltext-location, $filename))"/></dcterms:extent>
					</file:file>
            	</mdr:md-record>
            </mdr:md-records>
        </ec:component -->
        
	</xsl:template>

	<!-- FUNCTIONS-->
	<xsl:function name="escidoc:get-part">
		<xsl:param name="text"/>
		<xsl:param name="delimiter"/>
		<xsl:param name="pos"/>
		
		<xsl:choose>
			<xsl:when test="$pos &gt; 1 and not(contains($text, $delimiter))">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MatchingStringPartNotFound' ), concat('Unable to find part ', $pos, ' in ~', $text, '~ split by ~', $delimiter, '~.'))"/>
			</xsl:when>
			<xsl:when test="$pos &gt; 1"> 
				<xsl:value-of select="escidoc:get-part(substring-after($text, $delimiter), $delimiter, $pos - 1)"/>
			</xsl:when>
			<xsl:when test="contains($text, $delimiter)"> 
				<xsl:value-of select="substring-before($text, $delimiter)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<!--
		see http://colab.mpdl.mpg.de/mediawiki/Talk:PubMan_Func_Spec_Endnote_Mapping#Date_proper_formats 		
	
	 --> 		

	<xsl:function name="escidoc:normalizeDate">
		<xsl:param name="d" />
		<xsl:variable name="d" select="replace(replace(normalize-space($d), '^\s+', ''), '\s+$', '')"/>
		<xsl:variable name="nd" as="item()*">
			<xsl:choose>
				<xsl:when test="matches($d,'^\d{1,2}[-.]\d{1,2}[-.]\d{4}$')">
					<xsl:variable name="dmy" select="tokenize($d, '[-.]')"/>
					<xsl:copy-of select="$dmy[3], escidoc:add0($dmy[2]), escidoc:add0($dmy[1])" />
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}[-.]\d{1,2}[-.]\d{1,2}$')">
					<xsl:variable name="dmy" select="tokenize($d, '[-.]')"/>
					<xsl:copy-of select="$dmy[1], escidoc:add0($dmy[2]), escidoc:add0($dmy[3])"/>
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}[-.]\d{1,2}$')">
					<xsl:variable name="dmy" select="tokenize($d, '[-.]')"/>
					<xsl:copy-of select="$dmy[1], escidoc:add0($dmy[2])"/>
				</xsl:when>
				<xsl:when test="matches($d,'^\d{4}$')">
					<xsl:copy-of select="$d"/>
				</xsl:when>
				<xsl:when test="matches($d,'^\w{3,}\s+\d{1,2}\s*,\s*\d{4}$')">
					<xsl:analyze-string regex="(\w{{3,}})\s+(\d{{1,2}})\s*,\s*(\d{{4}})" select="$d">
						<xsl:matching-substring>
							<xsl:variable name="m" select="escidoc:getMonthNum(regex-group(1))"/>
							<xsl:copy-of select="
								if ($m!='') then 
								(
									regex-group(3),
									$m,
									escidoc:add0(regex-group(2))
								)
								else ()	 
							"/>
							
						</xsl:matching-substring>
					</xsl:analyze-string>
				</xsl:when>
				<xsl:when test="matches($d,'^\w{3,}\s+\d{4}$')">
					<xsl:analyze-string regex="(\w{{3,}})\s+(\d{{4}})" select="$d">
						<xsl:matching-substring>
							<xsl:variable name="m" select="escidoc:getMonthNum(regex-group(1))"/>
							<xsl:copy-of select="
								if ($m!='') then 
								(
									regex-group(2),
									$m
								)
								else ()	 
							"/>
							
						</xsl:matching-substring>
					</xsl:analyze-string>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
<!--		<xsl:message select="-->
<!--			concat('I am here, inp:', $d, ', out:', if (exists($nd)) then string-join( $nd[.!=''], '-' ) else '' )-->
<!--		"/>-->
		<xsl:value-of select="
			if (exists($nd)) then string-join( $nd[.!=''], '-' ) else ''
		"/>

	</xsl:function>

	<xsl:function name="escidoc:getMonthNum">
		<xsl:param name="m" />
		<xsl:variable name="m" select="lower-case(substring($m, 1, 3))"/>
		<xsl:value-of select="
		  	if ($m='jan') then '01' else
		  	if ($m='feb') then '02' else
		  	if ($m='mar') then '03' else
		  	if ($m='apr') then '04' else
		  	if ($m='may') then '05' else
		  	if ($m='jun') then '06' else
		  	if ($m='jul') then '07' else
		  	if ($m='aug') then '08' else
		  	if ($m='sep') then '09' else
		  	if ($m='oct') then '10' else
		  	if ($m='nov') then '11' else
		  	if ($m='dec') then '12' else
			''
		" />
	</xsl:function>
	
	<xsl:function name="escidoc:add0">
		<xsl:param name="d" />
		<xsl:value-of select="if (string-length($d)=1) then concat('0', $d) else $d"/>
	</xsl:function>

	
	<xsl:function name="escidoc:normalizeDegree">
		<xsl:param name="d" />
		<xsl:value-of select="lower-case(replace($d, '[.\s]+', ''))"/>
	</xsl:function>


</xsl:stylesheet>
