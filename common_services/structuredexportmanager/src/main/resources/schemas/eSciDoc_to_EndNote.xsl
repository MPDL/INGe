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
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="${xsd.metadata.escidocprofile.types}"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:prop="${xsd.soap.common.prop}"
>

	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	<!--
  DC XML  Header
-->
	<xsl:template match="/">
	
		<xsl:for-each select="//mdr:md-record">
	
			<xsl:variable name="mdr_pos" select="position()"/>
	
			<!-- if md-record is not in publication profile pur the message -->
			<xsl:if test="name(mdp:publication)=''">
				<xsl:value-of select="concat(
					if ($mdr_pos!=1) then '&#10;' else ''
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
					<xsl:value-of select="if ($mdr_pos!=1) then '&#10;' else ''"/>
		
					<!-- GENRES -->
					<xsl:variable name="gen" select="@type"/>
					<xsl:choose>
						<!-- ### book ### -->
						<xsl:when test="$gen='book'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<!-- at least one editor! -->
								<xsl:with-param name="value">
									<xsl:choose>
										<xsl:when test="count(*:creator[@role='editor'])>0">Edited Book</xsl:when>
										<xsl:otherwise>Book</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<!-- ### book-item  ### -->
						<xsl:when test="$gen='book-item'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Book Section'"/>
							</xsl:call-template>
						</xsl:when>
						
						<!-- ### conference-paper ### -->
						<xsl:when test="$gen='conference-paper'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Conference Paper'"/>
							</xsl:call-template>
						</xsl:when>
						<!-- ### thesis ###  TODO: mapping of DegreeEnum  to EndNote  is needed  -->
						<xsl:when test="$gen='thesis'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Thesis'"/>
							</xsl:call-template>							
						</xsl:when>
						<!-- ### article ### -->
						<xsl:when test="$gen='article'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Journal Article'"/>
							</xsl:call-template>
						</xsl:when>
						<!-- ### proceedings  ### -->
						<xsl:when test="$gen='proceedings'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Conference Proceedings'"/>
							</xsl:call-template>
						</xsl:when>
						<!-- ### manuscript  ### -->
						<xsl:when test="$gen='manuscript'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Manuscript'"/>
							</xsl:call-template>
						</xsl:when>
						<!-- ### report  ### -->
						<xsl:when test="$gen='report'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Report'"/>
							</xsl:call-template>
						</xsl:when>
						<!-- ### conference-report, talk-at-event, poster, courseware-lecture, paper, journal, issue,  series, others, etc  ### -->						
						<xsl:otherwise>
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'0'"/>
								<xsl:with-param name="value" select="'Generic'"/>
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
							<xsl:otherwise>								
								<xsl:apply-templates select="e:person">
									
									<xsl:with-param name="gen" select="@gen"/>								
								</xsl:apply-templates>
								<xsl:apply-templates select="e:organization">
									
									<xsl:with-param name="gen" select="@gen"/>
								</xsl:apply-templates>
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
					<!-- <xsl:variable name="pubdate" select="if((dcterms:issued)!='') then dcterms:issued else if  (*:published-online!='') then *:published-online else if ((dcterms:dateAccepted)!='') then dcterms:dateAccepted else if ((dcterms:dateSubmitted)!='') then dcterms:dateSubmitted else if ((dcterms:modified)!='') then dcterms:modified else if ((dcterms:created)!='') then dcterms:created else ''"/>		-->	
					<xsl:variable name="pubdate" select="dcterms:issued"/>
					<xsl:variable name="year" select="substring($pubdate,1,4)"/>
					<xsl:variable name="month" select="substring($pubdate,6,2)"/>
					<xsl:variable name="day" select="substring($pubdate,9,2)"/>
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="'D'"/>
						<xsl:with-param name="value">
							<xsl:value-of select="$year"/>
						</xsl:with-param>
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
					<!-- <xsl:variable name="sequence-number" select="normalize-space(e:source/e:sequence-number)"/> -->
					<xsl:choose>
						<xsl:when test="($gen='article' or $gen='conference-paper') and normalize-space(*:source/e:start-page)=''">
						<!-- <xsl:when test="($gen='article' or $gen='conference-paper') and normalize-space(*:source/e:start-page)=''">-->
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
										<xsl:value-of select="concat('sequence number : ',$sequence-number)"/>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</xsl:otherwise>
					</xsl:choose>
					
					
					<!-- Titles -->
					<!-- 
						dc:title -> %T
						dcterms:alternative -> %Q
					-->
					<xsl:for-each select="dc:title">
						<xsl:variable name="t" select="normalize-space(.)"/>
						<xsl:if test="$t!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'T'"/>
								<xsl:with-param name="value" select="$t"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="dcterms:alternative">
						<xsl:variable name="t" select="normalize-space(.)"/>
						<xsl:if test="$t!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'Q'"/>
								<xsl:with-param name="value" select="$t"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					<!-- 
						*:source[@type='journal']/dc:title -> %J
						*:source[@type='journal']/dcterms:alternative -> %J
						*:source[@type!='journal']/dc:title -> %B
						*:source[@type!='journal']/dcterms:alternative -> %B
						*:source[@type='series' and ../*:item[@type='book-item']/dc:title -> %S
						*:source[@type='series' and ../*:item[@type='book-item']/dcterms:alternative -> %S
					-->
					<!-- <xsl:for-each select="*:source/(dc:title|dcterms:alternative)">-->
					<xsl:for-each select="*:source/dc:title">
						<xsl:variable name="jb" select="normalize-space(.)"/>
						<xsl:variable name="sgenre" select="../@type"/>
						<xsl:if test="$jb!=''">
							
							<xsl:choose>
								<xsl:when test="$sgenre='journal'">
									<xsl:call-template name="print-line">
										<xsl:with-param name="tag" select="'J'"/>
										<xsl:with-param name="value" select="$jb"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="($gen='book-item' or $gen='conference-paper') and $sgenre='series'">
									<xsl:call-template name="print-line">
										<xsl:with-param name="tag" select="'S'"/>
										<xsl:with-param name="value" select="$jb"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="print-line">
										<xsl:with-param name="tag" select="'B'"/>
										<xsl:with-param name="value" select="$jb"/>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:for-each>
		
					
					
					<!-- 
						[@type='proceedings' or @type='conference-paper']/e:event/dc:title -> %B
						[@type='proceedings' or @type='conference-paper']/e:event/dcterms:alternative -> %B 
					-->
					
					<xsl:for-each select="*:event/dc:title">
						<xsl:variable name="b" select="normalize-space(.)"/>
						<xsl:if test="$b!=''">
							<xsl:choose>
								<xsl:when test="$gen='proceedings' or $gen='conference-paper'">
									<xsl:call-template name="print-line">
										<xsl:with-param name="tag" select="'B'"/>
										<xsl:with-param name="value" select="$b"/>
									</xsl:call-template>							
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="print-line">
										<xsl:with-param name="tag" select="'Z'"/>
										<xsl:with-param name="value" select="concat('name of event:',$b)"/>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:for-each>
		
					<!-- Titles END -->
		
					
					
					<!-- Date of event -->
					<xsl:variable name="sd" select="substring(normalize-space(*:event/e:start-date),1,10)"/>
					<xsl:if test="$sd!=''">
						<xsl:variable name="ed" select="substring(normalize-space(*:event/e:end-date),1,10)"/>
						<xsl:variable name="dateString" select="if ($ed='') then '' else concat(' - ', $ed)"/>
						<xsl:variable name="dates" select="concat($sd, dateString)"/>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'Z'"/>
							<xsl:with-param name="value" select="concat('date of event: ', $dates)"/>
						</xsl:call-template>
					</xsl:if>
					
					<!-- AFFILIATIONS -->
					<xsl:for-each select="*:creator[@role='author']/e:person/e:organization/e:organization-name">
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
							<xsl:if test="$gen='thesis'">
								<xsl:call-template name="print-line">
									<xsl:with-param name="tag" select="'9'"/>
									<xsl:with-param name="value" select="$ed"/>
								</xsl:call-template>
							</xsl:if>
						</xsl:if>
					</xsl:for-each>
		
					<!-- Start Page - End Page -->
					<xsl:for-each select="*:source">
						<xsl:variable name="sp" select="normalize-space(e:start-page)"/>
						<xsl:if test="$sp!=''">
							<xsl:choose> 
								<xsl:when test="$gen!='book'">
									<xsl:variable name="ep" select="normalize-space(e:end-page)"/>
									<xsl:call-template name="print-line">
										<xsl:with-param name="tag" select="'P'"/>
										<xsl:with-param name="value" select="string-join(($sp, $ep), '-')"/>
									</xsl:call-template>
								</xsl:when>							
								<xsl:when test="$gen='article' or $gen='manuscript'">
									<xsl:call-template name="print-line">
										<xsl:with-param name="tag" select="'&amp;'"/>
										<xsl:with-param name="value" select="$sp"/>
									</xsl:call-template>
								</xsl:when>
							</xsl:choose>
						</xsl:if>
					</xsl:for-each>
					
									
					<!-- IDENTIFIERS -->
					<xsl:for-each select="dc:identifier">
						<xsl:variable name="ident" select="."/>
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="if (@xsi:type='eidt:ISSN' or @xsi:type='eidt:ISBN') then '@'
							else if (@xsi:type='dcterms:URI' or @xsi:type='eidt:URN') then 'U' 
							else if (@xsi:type='eidt:DOI') then 'R'
							else 'Z'"/>
							<xsl:with-param name="value" select="if(@xsi:type='eidt:ISSN' or @xsi:type='eidt:ISBN' or @xsi:type='dcterms:URI' or @xsi:type='eidt:URN' or @xsi:type='eidt:DOI') then $ident
							else concat(@xsi:type, ' : ', $ident)"/>
						</xsl:call-template>
						<!-- ESciDoc Identifier in %M -->
						<xsl:if test="@xsi:type='eidt:ESCIDOC'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'M'"/>
								<xsl:with-param name="value" select="."/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					<!-- Location -->
					<xsl:for-each select="*:location">
						<xsl:variable name="location" select="."/>
						<xsl:if test="$location!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'L'"/>
								<xsl:with-param name="value" select="$location"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
		
					<!-- 	Issue -->
					<xsl:for-each select="*:source">
						<xsl:if test="@type!='series'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'N'"/>
								<xsl:with-param name="value" select="e:issue"/>
							</xsl:call-template>
						</xsl:if>						
					</xsl:for-each>
		
										
					<!-- Language -->
					<xsl:for-each select="dc:language">	
						<xsl:variable name="lang" select="normalize-space(.)"/>
						<xsl:if test="$lang!=''">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'G'"/>
								<xsl:with-param name="value" select="concat ('Language: ', $lang)"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>
					
					<!-- Place of event -->
					<xsl:variable name="flag" select="$gen='proceedings' or $gen='conference-paper'"/>
					<xsl:variable name="ep" select="normalize-space(*:event/e:place)"/>
					<xsl:if test="$ep!=''">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="if($flag) then 'C' else 'Z'"/>
							<xsl:with-param name="value" select="if ($flag) then $ep else concat('place of event: ', $ep)"/>
						</xsl:call-template>
					</xsl:if>
					
					<!-- Physical Description -->
					<xsl:variable name="pd" select="normalize-space(*:total-number-of-pages)"/>
					<xsl:if test="$pd!=''">
						<xsl:variable name="flag" select="normalize-space(*:source/e:start-page)!='' and normalize-space(*:source/e:end-page)!=''"/>
						<xsl:if test="$gen='book'">						
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="'P'"/>
								<xsl:with-param name="value" select="$pd"/>
							</xsl:call-template>
						</xsl:if>
					</xsl:if>
		
								
					<!-- Publisher -->
					<xsl:call-template name="print-line">
						<xsl:with-param name="tag" select="if ($gen='report') then 'Y' else 'I'"/>
						<xsl:with-param name="value" select="*:publishing-info/dc:publisher"/>
					</xsl:call-template>
									
		
					<!-- Publisher Address -->
					<xsl:variable name="pa" select="normalize-space(*:publishing-info/e:place)"/>
					<xsl:if test="$pa!=''">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'C'"/>
							<xsl:with-param name="value" select="$pa"/>
						</xsl:call-template>
					</xsl:if>
		
					<!-- Refereed (review-method) -->
					<xsl:variable name="rm" select="normalize-space(*:review-method)"/>
					<xsl:if test="$rm!=''">
						<xsl:call-template name="print-line">
							<xsl:with-param name="tag" select="'Z'"/>
							<xsl:with-param name="value" select="concat('review method: ', $rm)"/>
						</xsl:call-template>
					</xsl:if>
		
					<!-- Volume -->
					<xsl:for-each select="*:source/e:volume">
					<xsl:variable name="sgenre" select="../@type"/>
						<xsl:if test="$sgenre='series'">
							<xsl:call-template name="print-line">
								<xsl:with-param name="tag" select="if ($sgenre='series') then 'N' else 'V'"/>
								<xsl:with-param name="value" select="."/>
							</xsl:call-template>
						</xsl:if>
					</xsl:for-each>			
		
				</xsl:for-each>
		</xsl:if>
		
		</xsl:for-each>
		
	</xsl:template>
	<!-- creator type organization -->
	<xsl:template match="e:organization">
		
		<xsl:param name="gen"/>
		<xsl:choose>
			<xsl:when test="../@role='author'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat(e:organization-name,e:address)"/>
				</xsl:call-template>	
			</xsl:when>
			<xsl:when test="../@role='editor'">				
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="if ($gen='book') then 'A' else 'E'"/>
					<xsl:with-param name="value" select="concat(e:organization-name,e:address)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="../@role='translator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'?'"/>
					<xsl:with-param name="value" select="concat(e:organization-name,e:address)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="../@role='artist' or ../@role='painter' or ../@role='photographer' or ../@role='illustrator' or ../@role='commentator' or ../@role='transcriber' or ../@role='advisor' or ../@role='contributor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'Z'"/>
					<xsl:with-param name="value" select="concat(e:organization-name,', ',e:address)"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<!-- creator type person -->
	<xsl:template match="e:person">		
		<xsl:param name="gen"/>
		<xsl:variable name="given-name" select="*:given-name"/>
		<xsl:variable name="family-name" select="*:family-name"/>	
			
		<xsl:choose>
			<xsl:when test="../@role='author'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'A'"/>
					<xsl:with-param name="value" select="concat($family-name,', ',$given-name)"/>
				</xsl:call-template>		
			</xsl:when>
			<xsl:when test="../@role='editor'">					
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="if ($gen='book')then 'A' else 'E'"/>
					<xsl:with-param name="value" select="concat($family-name,', ',$given-name)"/>
				</xsl:call-template>						
			</xsl:when>
			<xsl:when test="../@role='translator'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'?'"/>
					<xsl:with-param name="value" select="concat($family-name,', ',$given-name)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="../@role='artist' or ../@role='painter' or ../@role='photographer' or ../@role='illustrator' or ../@role='commentator' or ../@role='transcriber' or ../@role='advisor' or ../@role='contributor'">
				<xsl:call-template name="print-line">
					<xsl:with-param name="tag" select="'Z'"/>
					<xsl:with-param name="value" select="concat(@role, ':', $family-name,', ',$given-name)"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		
	</xsl:template>
	
	<!-- TEMPLATES -->
	<!-- Prints result line in EndNote format -->
	<xsl:template name="print-line">
		<xsl:param name="tag"/>
		<xsl:param name="value"/>
		<xsl:variable name="strn" select="normalize-space($value)"/>
		<xsl:if test="$tag!='' and $strn!=''">
			<xsl:value-of select="concat('%', $tag , ' ',  $strn, '&#10;' )"/>
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
