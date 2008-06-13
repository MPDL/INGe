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
	Transformations from eSciDoc PubItem Schema to EndNote format 6.0
	Author: Vlad Makarenko (initial creation) 
	$Author: vdm $ (last changed)
	$Revision: 64 $ 
	$LastChangedDate: 2007-11-13 15:40:58 +0100 (Tue, 13 Nov 2007) $
-->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:xlink="http://www.w3.org/1999/xlink" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:dc="http://purl.org/dc/elements/1.1/" 
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:mdr="${xsd.soap.common.metadatarecords}" 
	xmlns:mdp="${xsd.metadata.escidocprofile}"
	xmlns:e="${xsd.metadata.escidocprofile.types}" 
	xmlns:ec="${xsd.soap.item.components}"
	xmlns:prop="${xsd.core.properties}"
		
>

	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	<!--
  DC XML  Header
-->
	<xsl:template match="/">
	
		<xsl:for-each select="//mdr:md-record">
	
			<!-- if md-record is not in publication profile pur the message -->
			<xsl:if test="name(mdp:publication)=''">
				<xsl:value-of select="concat(
					'&#10;'
					,'%C Cannot export to the EndNote for the metadata record: '
					,@xlink:href  
					,'. Element: &lt;'
					, name(child::*[1]), '&gt;'
					,', @md-type=&quot;'
					,@md-type, '&quot;'
					)"/>
			</xsl:if>
			
			<!-- md-record has md in publication profile -->
			<xsl:if test="name(mdp:publication)!=''">
				<xsl:for-each select="mdp:publication">
				
					<!-- Put new line for new doc  -->
					<xsl:if test="position()!=1">
						<xsl:value-of select="'&#10;'"/>
					</xsl:if>
		
					<!-- GENRES -->
					<xsl:variable name="gen" select="@type"/>
					<xsl:choose>
						<!-- ### book ### -->
						<xsl:when test="$gen='book'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<!-- at least one author! -->
								<xsl:with-param name="value">
									<xsl:choose>
										<xsl:when test="count(*:creator[@role='author'])!=0">Book</xsl:when>
										<xsl:otherwise>Edited Book</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- ### conference-paper ### -->
						<xsl:when test="$gen='conference-paper'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value">
									<xsl:choose>
										<xsl:when test="*:source/@type='journal'">Journal Article</xsl:when>
										<xsl:when test="*:source/@type='book' or *:source/@type='proceedings'">Book Section</xsl:when>
										<xsl:otherwise>Generic</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- TODO: mapping of DegreeEnum  to EndNote  is needed  -->
						<xsl:when test="$gen='thesis'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Thesis'"/>
							</xsl:call-template>
							<xsl:if test="*:degree='phd' or *:degree='habilitation'"> -->
								<xsl:call-template name="print-line">
									<xsl:with-param name="tag" select="'9'"/>
									<xsl:with-param name="value" select="if (*:degree='habilitation') then 'Habilitation' else if (*:degree='phd') then 'PhD-Thesis' else ''"/>
								</xsl:call-template>
							</xsl:if>
						</xsl:when>
						<!-- ### article ### -->
						<xsl:when test="$gen='article'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Journal Article'"/>
							</xsl:call-template>
						</xsl:when>
						<!-- ### proceedings, conference-report  ### -->
						<xsl:when test="$gen='proceedings' or $gen='conference-report'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Conference Proceedings'"/>
							</xsl:call-template>
						</xsl:when>
						<!-- ### talk-at-event, poster, courseware-lecture, paper, journal, issue,  series, others, etc  ### -->
						<xsl:otherwise>
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="Generic"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
					<!-- GENRES END -->
					
					<!-- AUTHORS -->
					<xsl:for-each select="*:creator">
						<xsl:variable name="creator-string">
							<xsl:call-template name="get-creator-str">
								<xsl:with-param name="creator" select="."/>
							</xsl:call-template>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="$creator-string=''">
								<xsl:message>
								Error: The creator string is empty.
							</xsl:message>
							</xsl:when>
							<xsl:when test="@role='author' or (@role='editor' and count(../*:creator[@role='author'])=0 and contains( 'bookproceedingsjournalseries', $gen))">
								<xsl:call-template name="print-line">
									<xsl:with-param name="tag" select="'A'"/>
									<xsl:with-param name="value" select="$creator-string"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="print-line">
									<xsl:with-param name="tag" select="'Z'"/>
									<xsl:with-param name="value" select="concat(@role, ': ', $creator-string)"/>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
					<!-- AUTHORS END -->
					
					<!-- YEAR -->
					<xsl:variable name="months">
						<m num="01">Jan</m>
						<m num="02">Feb</m>
						<m num="03">Mar</m>
						<m num="04">Apr</m>
						<m num="05">May</m>
						<m num="06">Jun</m>
						<m num="07">Jul</m>
						<m num="08">Aug</m>
						<m num="09">Sep</m>
						<m num="10">Oct</m>
						<m num="11">Nov</m>
						<m num="12">Dec</m>
					</xsl:variable>
					<xsl:variable name="pubdate" select="if ((dcterms:issued)!='') then dcterms:issued else if  (*:published-online!='') then *:published-online else if ((dcterms:accepted)!='') then dcterms:accepted else if ((dcterms:submitted)!='') then dcterms:submitted else if ((dcterms:modified)!='') then dcterms:modified else if ((dcterms:created)!='') then dcterms:created else ''"/>			
					<xsl:variable name="year" select="substring($pubdate,1,4)"/>
					<xsl:variable name="month" select="substring($pubdate,6,2)"/>
					<xsl:variable name="day" select="substring($pubdate,9,2)"/>
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="'D'"/>
						<xsl:with-param name="value">
							<xsl:value-of select="$year"/>
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="'8'"/>
						<xsl:with-param name="value" select="concat($months/m[@num=$month], ' ', $day)"/>
					</xsl:call-template>
					<!-- YEAR END -->
					
					<!-- Abstract  -->
					<xsl:for-each select="dcterms:abstract">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'X'"/>
							<xsl:with-param name="value" select="."/>
						</xsl:call-template>
					</xsl:for-each>	
					
					<!-- Artnum - Sequence Number of Article  -->
					<xsl:variable name="sequence-number" select="normalize-space(*:source/e:sequence-number)"/>
					<xsl:choose>
						<xsl:when test="($gen='article' or $gen='conference-paper') and normalize-space(*:source/e:start-page)=''">
							<xsl:if test="$sequence-number!=''">
								<xsl:call-template name="print-line">
									<xsl:with-param name="tag" select="'P'"/>
									<xsl:with-param name="value">
										<xsl:value-of select="$sequence-number"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<xsl:if test="$sequence-number!=''">
								<xsl:call-template name="print-line">
									<xsl:with-param name="tag" select="'Z'"/>
									<xsl:with-param name="value">
										<xsl:value-of select="$sequence-number"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
					
					<!-- Book Contributor Fullname or Book Creator Fullname-->
					<xsl:for-each select="*:source[@type='book' or @type='proceedings' or @type='book-item' or @type='conference-paper']/*:creator[@role='author' or @role='editor']">
						<xsl:variable name="fn" select="normalize-space(e:person/e:family-name)"/>
						<xsl:if test="$fn!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="if (../@type='book-item' or ../@type='conference-paper') then 'E' else if (e:person/@role='author')  then 'A' else 'E'"/>
								<xsl:with-param name="value">
									<xsl:value-of select="$fn"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					<!-- Titles -->
					<!-- 
						dc:title -> %T
						dcterms:alternative -> %T
					-->
					<xsl:for-each select="dc:title|dcterms:alternative">
						<xsl:variable name="t" select="normalize-space(.)"/>
						<xsl:if test="$t!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'T'"/>
								<xsl:with-param name="value" select="$t"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					<!-- 
						*:source[@type='journal']/dc:title -> %J
						*:source[@type='journal']/dcterms:alternative -> %J
						*:source[@type!='journal']/dc:title -> %B
						*:source[@type!='journal']/dcterms:alternative -> %B
					-->
					<xsl:for-each select="*:source/(dc:title|dcterms:alternative)">
						<xsl:variable name="jb" select="normalize-space(.)"/>
						<xsl:if test="$jb!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="if (../@type='journal') then 'J' else 'B'"/>
								<xsl:with-param name="value" select="$jb"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
		
					<!-- 
						*:source/e:esource/dc:title -> %S
						*:source/e:esource/dcterms:alternative -> %S
					-->
					<xsl:for-each select="*:source/e:esource/(dc:title|dcterms:alternative)">
						<xsl:variable name="s" select="normalize-space(.)"/>
						<xsl:if test="$s!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'S'"/>
								<xsl:with-param name="value" select="$s"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					<!-- 
						[@type='proceedings' or @type='conference-paper' or @type='conference-report']/e:event/dc:title -> %B
						[@type='proceedings' or @type='conference-paper' or @type='conference-report']/e:event/dcterms:alternative -> %B 
					-->
					<xsl:if test="$gen='proceedings' or $gen='conference-paper' or $gen='conference-report'">
						<xsl:for-each select="*:event/(dc:title|dcterms:alternative)">
							<xsl:variable name="b" select="normalize-space(.)"/>
							<xsl:if test="$b!=''">
								<xsl:call-template name="print-line">
									<xsl:with-param name="tag" select="'B'"/>
									<xsl:with-param name="value" select="$b"/>
								</xsl:call-template>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
		
					<!-- Titles END -->
		
					<!-- ContentType -->
					<xsl:for-each select="../../../ec:components/ec:component/ec:properties/prop:content-category">
						<xsl:variable name="tc" select="normalize-space(.)"/>
						<xsl:if test="$tc!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'Z'"/>
								<xsl:with-param name="value" select="concat('Type of Content: ', upper-case(substring($tc, 1, 1)), substring($tc, 2))"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
		
					<!-- dcterms:modified -->
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="'Z'"/>
						<xsl:with-param name="value" select="concat('Last Change of the Resource: ', dcterms:modified)"/>
					</xsl:call-template>
					
					<!-- Date of event -->
					<xsl:variable name="sd" select="substring(normalize-space(*:event/e:start-date),1,10)"/>
					<xsl:if test="$sd!=''">
						<xsl:variable name="ed" select="substring(normalize-space(*:event/e:end-date),1,10)"/>
						<xsl:variable name="dates" select="concat($sd, if ($ed!='') then concat(' - ', $ed) else '')"/>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'Z'"/>
							<xsl:with-param name="value" select="
							if ($gen='conference-report' or $gen='poster' or $gen='conference-paper' or $gen='proceedings') then 
								concat('Date of Conference: ', $dates)	
							else if ($gen='courseware-lecture') then 
								concat('Date of Lecture: ', $dates)
							else 
								concat('Date of Event: ', $dates)
							"/>
						</xsl:call-template>
					</xsl:if>
					
					<!-- AFFILIATIONS -->
					<xsl:for-each select="*:creator[@role='author']/e:organization/e:organization-name">
						<xsl:variable name="aff" select="normalize-space(.)"/>
						<xsl:if test="$aff!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'+'"/>
								<xsl:with-param name="value" select="$aff"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
		
					<!-- dc:subject, keywords -->
					<xsl:for-each select="dc:subject">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'K'"/>
							<xsl:with-param name="value" select="."/>
						</xsl:call-template>
					</xsl:for-each>
					
					<!-- Edition Description  -->
					<xsl:for-each select="*:publishing-info/e:edition">
						<xsl:variable name="ed" select="normalize-space(.)"/>
						<xsl:if test="$ed!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'7'"/>
								<xsl:with-param name="value" select="$ed"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="*:degree">
						<xsl:variable name="ed" select="normalize-space(.)"/>
						<xsl:if test="$ed!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'9'"/>
								<xsl:with-param name="value" select="$ed"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
		
					<!-- Start Page - End Page -->
					<xsl:for-each select="*:source">
						<xsl:variable name="sp" select="normalize-space(e:start-page)"/>
						<xsl:if test="$sp!=''">
							<xsl:variable name="ep" select="normalize-space(e:end-page)"/>
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'P'"/>
								<xsl:with-param name="value" select="string-join(($sp, $ep), '-')"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					<!-- Genre -->
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="'Z'"/>
						<xsl:with-param name="value" select="concat('eSciDoc Reference Type: ', $gen)"/>
					</xsl:call-template>
				
					<!-- IDENTIFIERS -->
					<xsl:for-each select="dc:identifier">
						<xsl:variable name="ident" select="."/>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="
							if (@xsi:type='eidt:ISSN' or @xsi:type='eidt:ISBN') then '@'
							else if (@xsi:type='dcterms:URI') then 'U' 
							else 'Z'"/>
							<xsl:with-param name="value" select="
							if (@xsi:type='eidt:ISSN' or @xsi:type='eidt:ISBN' or @xsi:type='dcterms:URI') then $ident
							else concat(@xsi:type, ' identifier: ', $ident)"/>
						</xsl:call-template>
					</xsl:for-each>
		
					<!-- 	Issue -->
					<xsl:for-each select="*:source">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'N'"/>
							<xsl:with-param name="value" select="e:issue"/>
						</xsl:call-template>
					</xsl:for-each>
		
					<!-- 	Issue Editors -->
					<xsl:for-each select="*:source[@type='issue']/*:creator[@role='editor']">
						<xsl:variable name="n" select="normalize-space(e:person/e:family-name | e:organization/e:organization-name)"/>
						<xsl:if test="$n!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'Z'"/>
								<xsl:with-param name="value" select="concat('Issue-Editor(s): ', $n)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					
					<!-- Language -->
					<xsl:for-each select="dc:language">	
						<xsl:variable name="lang" select="normalize-space(.)"/>
						<xsl:if test="$lang!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'Z'"/>
								<xsl:with-param name="value" select="concat ('Language: ', $lang)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					<!-- Place of event -->
					<xsl:variable name="flag1" select="$gen='proceedings' or $gen='conference-report'"/>
					<xsl:variable name="flag2" select="$gen='conference-paper' or $gen='poster'"/>
					<xsl:variable name="ep" select="normalize-space(*:event/e:place)"/>
					<xsl:if test="$ep!=''">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="if ($flag1) then 'C' else 'Z'"/>
							<xsl:with-param name="value" select="if ($flag1) then $ep else if ($flag2) then concat('Place of Conference: ', $ep) else concat('Place of Event: ', $ep)"/>
						</xsl:call-template>
					</xsl:if>
					
					<!-- Physical Description -->
					<xsl:variable name="pd" select="normalize-space(*:total-number-of-pages)"/>
					<xsl:if test="$pd!=''">
						<xsl:variable name="flag" select="normalize-space(*:source/e:start-page)!='' and normalize-space(*:source/e:end-page)!=''"/>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="if ($flag) then 'P' else 'Z'"/>
							<xsl:with-param name="value" select="if ($flag) then $pd else concat ('Physical Description: ', $pd)"/>
						</xsl:call-template>
					</xsl:if>
		
					<!-- Proceedings' Editor -->
					<xsl:variable name="flag" select="$gen='conference-paper'"/>
					<xsl:for-each select="*:source[@type='proceedings']/*:creator[@role='editor']">
						<xsl:variable name="n" select="normalize-space(normalize-space(e:person/e:family-name | e:organization/e:organization-name))"/>
						<xsl:if test="$n!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="if ($flag) then 'E' else 'Z'"/>
								<xsl:with-param name="value" select="if ($flag) then $n else concat('Editor(s) of the Proceedings: ', $n)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>			
		
					<!-- Series' Editors -->
					<xsl:variable name="flag1" select="$gen='book-item' or $gen='conference-paper'"/>
					<xsl:variable name="flag2" select="$gen='book'"/>
					<xsl:for-each select="*:source[@type='series']/*:creator[@role='editor']">
						<xsl:variable name="n" select="normalize-space(e:person/e:family-name | e:organization/e:organization-name)"/>
						<xsl:if test="$n!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="if ($flag1) then 'Y' else if ($flag2) then 'E' else 'Z'"/>
								<xsl:with-param name="value" select="if ($flag1 or $flag2) then $n else concat('Series Editor(s): ', $n)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>			
					
					<!-- Publisher -->
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="'I'"/>
						<xsl:with-param name="value" select="*:publishing-info/dc:publisher"/>
					</xsl:call-template>
		
					<!-- Publisher Address -->
					<xsl:variable name="pa" select="normalize-space(*:publishing-info/e:place)"/>
					<xsl:if test="$pa!=''">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="if ($gen='proceedings') then 'Z' else 'C'"/>
							<xsl:with-param name="value" select="if ($gen='proceedings') then concat('Place of Publication: ', $pa) else $pa"/>
						</xsl:call-template>
					</xsl:if>
		
					<!-- Refereed (review-method) -->
					<xsl:variable name="rm" select="normalize-space(*:review-method)"/>
					<xsl:if test="$rm!=''">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'Z'"/>
							<xsl:with-param name="value" select="concat('Review Type: ', $rm)"/>
						</xsl:call-template>
					</xsl:if>
		
					<!-- Volume -->
					<xsl:for-each select="*:source/e:volume">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'V'"/>
							<xsl:with-param name="value" select="."/>
						</xsl:call-template>
					</xsl:for-each>			
		
				</xsl:for-each>
		</xsl:if>
		
		</xsl:for-each>
		
	</xsl:template>
	
	<!-- TEMPLATES -->
	<!-- Prints result line in EndNote format -->
	<xsl:template name="print-line">
		<xsl:param name="tag"/>
		<xsl:param name="value"/>
		<xsl:variable name="strn" select="normalize-space($value)"/>
		<xsl:if test="$tag!='' and $strn!=''">
			<xsl:value-of select="concat('%', $tag , ' ',  $strn, '&#10;')"/>
		</xsl:if>
	</xsl:template>
	
	<!-- Generates  Creator string -->
	<xsl:template name="get-creator-str">
		<xsl:param name="creator"/>
		<xsl:variable name="c" select="$creator/e:person | $creator/e:organization"/>
		<xsl:variable name="name" select="normalize-space($c/e:family-name | $c/e:organization-name)"/>
		<!-- TODO: organization handling -->
		<xsl:choose>
			<xsl:when test="$name!=''">
				<xsl:value-of select="$name"/>
				<xsl:if test="normalize-space($c/e:given-name)!=''">
					<xsl:value-of select="concat(', ', normalize-space($c/e:given-name))"/>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="normalize-space($c/e:complete-name)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- TEMPLATES END-->
	
</xsl:stylesheet>
