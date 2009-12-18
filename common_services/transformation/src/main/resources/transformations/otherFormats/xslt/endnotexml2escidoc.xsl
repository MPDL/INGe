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


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
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
   xmlns:esc="http://escidoc.mpg.de/">

	<xsl:import href="src/main/resources/transformations/vocabulary-mappings.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="user" select="'dummy:user'"/>
	<xsl:param name="context" select="'dummy:context'"/>
	<xsl:param name="content-model" select="'dummy:content-model'"/>

	<xsl:param name="is-item-list" select="true()"/>
	<xsl:param name="source-name" select="''"/>
	
	<xsl:param name="fulltext-location" select="'http://www.clib-jena.mpg.de/theses/ice/'"/>
	
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
	
	<xsl:variable name="ou-mapping-ice">
		<unit>
			<code>BIBO</code>
			<name_en>Service Group Library</name_en>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>BOL</code>
			<name_en>Department of Bioorganic Chemistry</name_en>
			<edoc_id>13503</edoc_id>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>EDV</code>
			<name_en>Service Group IT</name_en>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>GER</code>
			<name_en>Department of Biochemistry</name_en>
			<edoc_id>13504</edoc_id>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>GH</code>
			<name_en>Service Group Greenhouse</name_en>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>HAN</code>
			<name_en>Department of Neuroethology</name_en>
			<edoc_id>18762</edoc_id>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>HEC</code>
			<name_en>Department of Entomology</name_en>
			<edoc_id>18310</edoc_id>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>ICEDIV</code>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>IMPRS</code>
			<name_en>International Max Planck Research School</name_en>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>ITB</code>
			<name_en>Department of Molecular Ecology</name_en>
			<escidoc_id>escidoc:19040</escidoc_id>
			<edoc_id>13502</edoc_id>
		</unit>
		<unit>
			<code>MS</code>
			<name_en>Research Group Mass Spectrometry</name_en>
			<edoc_id>13507</edoc_id>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>NMR</code>
			<name_en>Research Group Biosynthesis / NMR</name_en>
			<edoc_id>13506</edoc_id>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>TECH</code>
			<name_en>Technical Service</name_en>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>TMO</code>
			<name_en>Group of Genetics and Evolution</name_en>
			<edoc_id>13505</edoc_id>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
		<unit>
			<code>VAD</code>
			<name_en>Administration</name_en>
			<escidoc_id>escidoc:19040</escidoc_id>
		</unit>
	</xsl:variable>
	
	<xsl:function name="escidoc:get-part">
		<xsl:param name="text"/>
		<xsl:param name="delimiter"/>
		<xsl:param name="pos"/>
		
		<xsl:choose>
			<xsl:when test="$pos &gt; 1 and not(contains($text, $delimiter))">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MatchingStringPartNotFound' ), concat('Unable to find part ', $pos, ' in &apos;', $text, '&apos; split by &apos;', $delimiter, '&apos;.'))"/>
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
			<xsl:element name="ec:components">
				<xsl:if test="F and $source-name = 'endnote-ice'">
					<xsl:call-template name="component"/>
				</xsl:if>
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
				if ( B and $refType = ('Book', 'Edited Book', 'Manuscript') ) then $genre-ves/enum[.='series']/@uri else
				if ( B and $refType = 'Book Section' ) then $genre-ves/enum[.='book']/@uri else
				if ( B and $refType = ('Electronic Article', 'Newspaper Article', 'Magazine Article') ) then $genre-ves/enum[.='journal']/@uri else
				if ( J and $refType = 'Journal Article' ) then $genre-ves/enum[.='journal']/@uri else
				if ( S and $refType = ('Book Section', 'Conference Proceedings') ) then $genre-ves/enum[.='series']/@uri else
				''
				"/>		
		
		<xsl:element name="pub:publication">
		
			<xsl:attribute name="type">
				<xsl:value-of select="$genre-ves/enum[.=$gen]/@uri"/>
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
			<xsl:choose>
				<!-- ICE puts filename into %F -->
				<xsl:when test="$source-name = 'endnote-ice'">
					<xsl:for-each select="
						B[$refType = ('Generic', 'Electronic Book')]
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
				</xsl:when>
				<xsl:otherwise>
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
				</xsl:otherwise>
			</xsl:choose>
			
			
									
			<!-- IDENTIFIERS -->
			<xsl:for-each select="L">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="M">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">
						<xsl:value-of select="if (starts-with(upper-case(.), 'ISI:' )) then 'eterms:ISI' else 'eterms:OTHER'"/>
					</xsl:attribute>
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
				   $refType = ('Book', 'Book Section', 'Conference Proceedings', 'Edited Book', 'Electronic Book')   
				]">
				<xsl:element name="dc:identifier">
					<xsl:attribute name="xsi:type">eterms:ISBN</xsl:attribute>
					<xsl:value-of select="."/>
				</xsl:element>
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
			<xsl:if test="$source-name = 'endnote-ice'">
				<xsl:for-each select="DOLLAR">
					<xsl:element name="dc:identifier">
						<xsl:attribute name="xsi:type">eterms:OTHER</xsl:attribute>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:for-each>
			</xsl:if>
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
				<xsl:element name="eterms:publishing-info">
					<xsl:if test="$publisher!=''">
						<xsl:element name="dc:publisher">
							<xsl:value-of select="$publisher"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="$place!=''">
						<xsl:element name="eterms:place">
							<xsl:value-of select="$place"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="$edition!=''">
						<xsl:element name="eterms:edition">
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
						    , 'Conference Proceedings'
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
				<xsl:choose>
					<xsl:when test="not(matches(., '^[A-Z][a-z][a-z]( .+)?')) and 
						   $refType = (
							     'Book'
							   , 'Conference Paper'
							   , 'Conference Proceedings'
							   , 'Edited Book'
							   , 'Journal Article'
							   , 'Magazine Article'
							   , 'Newspaper Article'						   
							   , 'Manuscript'  
							   , 'Report' 
							   , 'Thesis' 
						)"></xsl:when>
					<xsl:when test="NUM_8 and 
						   $refType = (
							     'Book'
							   , 'Conference Paper'
							   , 'Conference Proceedings'
							   , 'Edited Book'
							   , 'Journal Article'
							   , 'Magazine Article'
							   , 'Newspaper Article'						   
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
								  	if (.='Dec') then '12' else
								  	error(QName('http://www.escidoc.de', 'err:MonthNotRecognized' ), concat('Do not know the month ', ., ' in %8'))
								  )	
								else concat('-', if (string-length(.)=1) then concat('0', .) else .  )	
								   "/>
						</xsl:for-each>			
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:if test="$year">
				<dcterms:created xsi:type="dcterms:W3CDTF"><xsl:value-of select="concat($year, if ($year!='' and $date!='') then '-' else '', $date)"/></dcterms:created>				
			</xsl:if>
			<xsl:if test="NUM_7 and (
				   $refType = 'Journal Article' 
				)">
				<eterms:published-online xsi:type="dcterms:W3CDTF"><xsl:value-of select="NUM_7"/></eterms:published-online>
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
			<xsl:if test="V and $refType = 'Thesis'">
				<xsl:element name="eterms:degree">
					<xsl:value-of select="$degree-ves/enum[.=V]/@uri"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="NUM_9 and $refType = 'Thesis'">
				<xsl:element name="eterms:degree" select="$degree-ves/enum[.='diploma']/@uri"/>
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
				<xsl:element name="eterms:location">
					<xsl:value-of select="I"/>
				</xsl:element>
			</xsl:if>
			
			
		</xsl:element>
		
	</xsl:template>

	
	
	<!-- SOURCE -->
	<xsl:template name="createSource">
		<xsl:param name="sgen"/>
		<xsl:variable name="refType" select="normalize-space(NUM_0)"/>
		
		<xsl:element name="source:source">


			<!-- SOURCE GENRE -->
			<xsl:attribute name="type">
				<xsl:value-of select="$genre-ves/enum[.=$sgen]/@uri"/>
			</xsl:attribute>
			

			<!-- SOURCE TITLE -->
			<xsl:element name="dc:title">
				<xsl:choose>
					<xsl:when test="B"><xsl:value-of select="B"/></xsl:when>
					<xsl:when test="J[ 
						$refType = 
							('Journal Article', 'Magazine Article') 
						]"><xsl:value-of select="J"/></xsl:when>
				</xsl:choose>
			</xsl:element>


			<!-- SOURCE ALTTITLE -->
			<xsl:for-each select="J[
				exists(B)
					and
				$refType = 
					('Journal Article', 'Magazine Article') 
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
					<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
					<xsl:with-param name="isSource" select="true()"/>
				</xsl:call-template>
			</xsl:for-each>

			
			<!-- SOURCE VOLUME -->
			<xsl:if test="N and $refType = ('Book', 'Book Section', 'Edited Book')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>	
			<xsl:if test="V and not(N) and $refType = ('Book', 'Book Section', 'Edited Book', 'Report')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			
			<xsl:if test="V and $refType = ('Generic', 'Conference Paper', 'Conference Proceedings', 'Electronic Article', 'Electronic Book', 'Journal Article', 'Magazine Article', 'Newspaper Article', 'Manuscript')">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="V and not(NUM_6) and $refType = 'Report'">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="V"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="NUM_6 and $refType = 'Report'">
				<xsl:element name="eterms:volume">
					<xsl:value-of select="NUM_6"/>
				</xsl:element>
			</xsl:if>

			
			<!-- SOURCE ISSUE -->
			<xsl:if test="N and $refType = ('Electronic Article', 'Journal Article', 'Generic', 'Magazine Article')">
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
			<xsl:if test="N and $refType = 'Report'">
				<xsl:element name="eterms:sequence-number">
					<xsl:value-of select="N"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="M and $refType = 'Manuscript'">
				<xsl:element name="eterms:sequence-number">
					<xsl:value-of select="M"/>
				</xsl:element>
			</xsl:if>
			<xsl:if test="AMPERSAND and $refType = 'Book Section'">
				<xsl:element name="eterms:sequence-number">
					<xsl:value-of select="AMPERSAND"/>
				</xsl:element>
			</xsl:if>
				
					
			<!-- SOURCE PUBLISHINGINFO -->
			<xsl:if test="I and $refType = ('Generic', 'Book', 'Book Section', 'Conference Paper', 'Conference Proceedings', 'Edited Book', 'Electronic Article', 'Electronic Book', 'Magazine Article', 'Newspaper Article')">
				<xsl:element name="eterms:publishing-info">
					<xsl:element name="dc:publisher">
						<xsl:value-of select="I"/>
					</xsl:element>
					<xsl:if test="NUM_7 and $refType = ('Book Section', 'Electronic Article', 'Magazine Article', 'Newspaper Article', 'Report')">
						<xsl:element name="eterms:edition">
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
							<xsl:with-param name="role" select="$creator-ves/enum[.='author']/@uri"/>
						</xsl:call-template>					
					</xsl:when>
					<xsl:when test="$refType = ('Conference Paper', 'Electronic Book') ">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="$creator-ves/enum[.='editor']/@uri"/>
						</xsl:call-template>					
					</xsl:when>
				</xsl:choose>
			</xsl:if>
			
			<xsl:if test="name(.)='Y'">
				<xsl:choose>
					<xsl:when test="$refType='Generic'">
						<xsl:call-template name="createCreator">
							<xsl:with-param name="role" select="$creator-ves/enum[.='author']/@uri"/>
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
			</xsl:if>		
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="createCreator">
		<xsl:param name="role"/>
		<xsl:param name="isSource"/>
		<xsl:param name="pos" select="0"/>
		<xsl:if test="$isSource">
			<xsl:element name="eterms:creator">
				<xsl:attribute name="role"><xsl:value-of select="$creator-ves/enum[.=$role]/@uri"/></xsl:attribute>
				<xsl:call-template name="createPerson">
					<xsl:with-param name="isSource" select="$isSource"/>
				</xsl:call-template>				
			</xsl:element>
		</xsl:if>
		<xsl:if test="not($isSource)">
			<xsl:element name="eterms:creator">
				<xsl:attribute name="role"><xsl:value-of select="$creator-ves/enum[.=$role]/@uri"/></xsl:attribute>
				<xsl:call-template name="createPerson">
					<xsl:with-param name="isSource" select="$isSource"/>
					<xsl:with-param name="pos" select="$pos"/>
				</xsl:call-template>				
			</xsl:element>
		</xsl:if>
	</xsl:template>	
	
	<xsl:template name="createPerson">
		<xsl:param name="isSource"/>
		<xsl:param name="pos" select="0"/>
		<xsl:variable name="person" select="AuthorDecoder:parseAsNode(.)/authors/author[1]"/>
		
		
		<xsl:choose>
			<xsl:when test="$source-name = 'endnote-ice'">
			
				<xsl:variable name="additionalAuthorInformation" select="normalize-space(escidoc:get-part(../NUM_3, ',', $pos))"/>

				<xsl:variable name="iris-id" select="substring-before(substring-after($additionalAuthorInformation, '-'), '-')"/>
				<xsl:variable name="ou-id" select="substring-after(substring-after($additionalAuthorInformation, '-'), '-')"/>
			
				<xsl:comment><xsl:value-of select="(substring-after($additionalAuthorInformation, '-') = '-')"/></xsl:comment>
			
				<xsl:variable name="cone-creator">
					<xsl:if test="not(($pos = 0) or (substring-after($additionalAuthorInformation, '-') = '-'))">
						<xsl:if test="not(starts-with($additionalAuthorInformation, concat($pos, '-')))">
							<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:CustomizedFieldError' ), concat('The customized field %3 has a wrong format: ´', $additionalAuthorInformation, '´. Should start with ´', $pos, '-´'))"/>
						</xsl:if>
						<xsl:comment>Querying CoNE for ´<xsl:value-of select="concat('Chemical Ecology ', $iris-id)"/>´</xsl:comment>
						<xsl:copy-of select="Util:queryCone('persons', concat('Chemical Ecology ', $iris-id))"/>
					</xsl:if>
				</xsl:variable>
				
				<xsl:variable name="multiplePersonsFound" select="exists($cone-creator/cone/rdf:RDF/rdf:Description[@rdf:about != preceding-sibling::attribute/@rdf:about])"/>
		
				<xsl:if test="$multiplePersonsFound">
					<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleCreatorsFound' ), concat('There is more than one CoNE entry matching -', concat($person/familyname, ', ', $person/givenname), '-'))"/>
				</xsl:if>
				
				<xsl:if test="exists($cone-creator/cone) and not(exists($cone-creator/cone/rdf:RDF/rdf:Description))"> 
					<xsl:comment>Iris-ID <xsl:value-of select="$iris-id"/> not found in CoNE service!</xsl:comment>
				</xsl:if>
				
				<xsl:choose>
					<xsl:when test="exists($cone-creator/cone/rdf:RDF/rdf:Description)">
						<person:person>
							<eterms:family-name><xsl:value-of select="$person/familyname"/></eterms:family-name>
							<eterms:given-name><xsl:value-of select="$person/givenname"/></eterms:given-name>
							<xsl:if test="exists($ou-mapping-ice/unit[code = $ou-id])">
								<organization:organization>
									<dc:title><xsl:value-of select="$ou-mapping-ice/unit[code = $ou-id]/name_en"/>, MPI for Chemical Ecology, Max Planck Society</dc:title>
									<dc:identifier><xsl:value-of select="$ou-mapping-ice/unit[code = $ou-id]/escidoc_id"/></dc:identifier>
								</organization:organization>
							</xsl:if>
							<dc:identifier xsi:type="CONE"><xsl:value-of select="$cone-creator/cone/rdf:RDF[1]/rdf:Description/@rdf:about"/></dc:identifier>
						</person:person>
					</xsl:when>
					<xsl:otherwise>
						<person:person>
							<eterms:family-name><xsl:value-of select="$person/familyname"/></eterms:family-name>
							<eterms:given-name><xsl:value-of select="$person/givenname"/></eterms:given-name>
							<xsl:if test="exists($ou-mapping-ice/unit[code = $ou-id])">
								<organization:organization>
									<dc:title><xsl:value-of select="$ou-mapping-ice/unit[code = $ou-id]/name_en"/></dc:title>
									<dc:identifier><xsl:value-of select="$ou-mapping-ice/unit[code = $ou-id]/escidoc_id"/></dc:identifier>
								</organization:organization>
							</xsl:if>
						</person:person>
					</xsl:otherwise>
				</xsl:choose>
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
					<xsl:element name="eterms:complete-name">
						<xsl:value-of select="."/>
					</xsl:element>
					<xsl:choose>
						<xsl:when test="exists($cone-creator/cone/rdf:RDF/rdf:Description/esc:position)">
							<xsl:for-each select="$cone-creator/cone/rdf:RDF[1]/rdf:Description/esc:position">
								<organization:organization>
									<dc:title>
										<xsl:value-of select="rdf:Description/esc:organization"/>
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
								<dc:identifier>${escidoc.pubman.root.organisation.id}</dc:identifier>
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
		<ec:component>
            <ec:properties xmlns:xlink="http://www.w3.org/1999/xlink">               
                <prop:visibility>
					<xsl:choose>
						<xsl:when test="NUM_4 = 'OA'">http://purl.org/escidoc/metadata/ves/access-types/public</xsl:when>
						<xsl:otherwise>http://purl.org/escidoc/metadata/ves/access-types/private</xsl:otherwise>
					</xsl:choose>
				</prop:visibility>
                <prop:content-category>any-fulltext</prop:content-category>
                <prop:file-name><xsl:value-of select="F"/>.pdf</prop:file-name>
                <prop:mime-type>application/pdf</prop:mime-type>
            </ec:properties>
            <ec:content xlink:type="simple" xlink:title="{F}.pdf" xlink:href="{$fulltext-location}{F}.pdf" storage="internal-managed"/>
            <mdr:md-records xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}">
            	<mdr:md-record name="escidoc">
            		<file:file xmlns:file="${xsd.metadata.file}" xmlns:dc="${xsd.metadata.dc}" xmlns:dcterms="${xsd.metadata.dcterms}" xmlns:e="${xsd.metadata.escidocprofile.types}" xmlns:eidt="${xsd.metadata.escidocprofile.idtypes}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
						<dc:title><xsl:value-of select="F"/>.pdf</dc:title>
						<file:content-category>any-fulltext</file:content-category>
						<dc:format xsi:type="dcterms:IMT">application/pdf</dc:format>
						<dcterms:extent><xsl:value-of select="Util:getSize(concat($fulltext-location, F, '.pdf'))"/></dcterms:extent>
					</file:file>
            	</mdr:md-record>
            </mdr:md-records>
        </ec:component>
	</xsl:template>

</xsl:stylesheet>