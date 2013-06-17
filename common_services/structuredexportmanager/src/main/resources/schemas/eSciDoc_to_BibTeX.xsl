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
	Transformations from eSciDoc PubItem Schema to BibTeX
	Author: Julia Kurt (initial creation) 
	$Author$ (last changed)
	$Revision$ 
	$LastChangedDate$
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:escidoc="${xsd.metadata.escidocprofile.types}"
	xmlns:jfunc="java:de.mpg.escidoc.services.structuredexportmanager.functions.BibTex"
	xmlns:func="urn:my-functions" 
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	xmlns:ei="${xsd.soap.item.item}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:mdp="${xsd.metadata.escidocprofile}"
	xmlns:e="${xsd.metadata.escidocprofile.types}"
	xmlns:ec="${xsd.soap.item.components}"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:pub="${xsd.metadata.publication}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:eterms="${xsd.metadata.terms}">
	<!-- <xsl:import href="functions.xsl"/>-->
	
	<xsl:import href="vocabulary-mappings.xsl"/>
	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	
	
	<xsl:template match="/*">			
		<!-- create entry for each item -->
			<xsl:apply-templates select="//pub:publication"/>				
	</xsl:template>	
	
	<!-- create bibTeX entry -->
	<xsl:template match="pub:publication">
	
		<xsl:variable name="gen" select="@type"/>
		<xsl:variable name="genre" select="$genre-ves/enum[@uri=$gen]"/>
		
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
					<xsl:when test="eterms:degree=$degree-ves/enum[.='master']/@uri">
						<xsl:call-template name="createEntry">
							<xsl:with-param name="entryType" select="'masterthesis'"/>
						</xsl:call-template>						
					</xsl:when>
					<xsl:when test="eterms:degree=$degree-ves/enum[.='phd']/@uri">
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
		<xsl:variable name="cite-key">
		    <xsl:choose>
		        <xsl:when test="fn:exists(.//dc:identifier[@xsi:type='eterms:BIBTEX_CITEKEY'])">
		            <xsl:value-of select=".//dc:identifier[@xsi:type='eterms:BIBTEX_CITEKEY'][1]"/>
		        </xsl:when>
		        <xsl:otherwise>
		            <xsl:value-of select="fn:substring-after(fn:substring-after(fn:substring-after(parent::mdr:md-record/parent::mdr:md-records/parent::ei:item/@xlink:href, '/'), '/'), '/')"/>
		        </xsl:otherwise>
		    </xsl:choose>
		</xsl:variable>
		<xsl:value-of select="concat('@', $entryType, '{')"/>
		<xsl:value-of select="jfunc:texString($cite-key)"/>
		<xsl:value-of select="','"/>
		
		<xsl:text disable-output-escaping="yes">&#xD;&#xA;</xsl:text><!-- line break -->
		<!-- TITLE -->
		<xsl:apply-templates select="dc:title"/>		
		<!-- CREATOR --> 
		<xsl:apply-templates select="eterms:creator[@role=$creator-ves/enum[.='author']/@uri]"/>		
		<!-- EDITOR -->
		<xsl:apply-templates select="eterms:creator[@role=$creator-ves/enum[.='editor']/@uri]"/>		
		<!-- LANGUAGE -->
		<xsl:apply-templates select="dc:language"/>
		<!-- Identifiers -->
		<xsl:if test="exists(.//dc:identifier)">
		    <xsl:variable name="identifier-xml">
		        <xsl:for-each select=".//dc:identifier">
		            <xsl:copy-of select="."/>
		        </xsl:for-each>
		    </xsl:variable>
		    <xsl:call-template name="identifiers">
		        <xsl:with-param name="identifier-list" select="$identifier-xml" />
		    </xsl:call-template>
		</xsl:if>	
		<!-- PUBLISHER, ADDRESS -->
		<xsl:choose>
			<xsl:when test="source:source/eterms:publishing-info/dc:publisher=''">
				<xsl:apply-templates select="eterms:publishing-info/dc:publisher"/>		
				<xsl:apply-templates select="eterms:publishing-info/eterms:place"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="source:source/eterms:publishing-info/dc:publisher"/>		
				<xsl:apply-templates select="source:source/eterms:publishing-info/eterms:place"/>	
			</xsl:otherwise>
		</xsl:choose>			
		<!-- EDITION -->
		<xsl:choose>
			<xsl:when test="source:source/eterms:publishing-info/eterms:edition=''">
				<xsl:apply-templates select="eterms:publishing-info/eterms:edition"/>				
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="source:source/eterms:publishing-info/eterms:edition"/>	
			</xsl:otherwise>
		</xsl:choose>		
		<!-- YEAR -->
		<xsl:variable name="pubdate" select="if(dcterms:issued!='') then dcterms:issued else if  (eterms:published-online!='') then eterms:published-online else if (dcterms:dateAccepted!='') then dcterms:dateAccepted else if (dcterms:dateSubmitted!='') then dcterms:dateSubmitted else if (dcterms:modified!='') then dcterms:modified else if (dcterms:created!='') then dcterms:created else ''"/>	
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
		<!-- TYPE -->
		<xsl:variable name="degree" select="eterms:degree"/>
		<xsl:if test="$entryType='misc' 
						and ($degree=$degree-ves/enum[.='diploma']/@uri 
							or $degree=$degree-ves/enum[.='bachelor']/@uri
							or $degree=$degree-ves/enum[.='magister']/@uri 
							or $degree=$degree-ves/enum[.='habilitation']/@uri 
							or $degree=$degree-ves/enum[.='staatsexamen']/@uri)">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'type'"/>
				<xsl:with-param name="xpath" select="concat($degree-ves/enum[@uri=$degree], ' (thesis)')"/>
			</xsl:call-template>
		</xsl:if>
		
		<!-- PAGES -->
		<xsl:variable name="type-of-publication" select="./@type"/>
		<xsl:if test="exists(eterms:total-number-of-pages)
						and eterms:total-number-of-pages != ''
						and ($genre-ves/enum[@uri=$type-of-publication] = 'book'
						or $genre-ves/enum[@uri=$type-of-publication] = 'proceedings'
						or $genre-ves/enum[@uri=$type-of-publication] = 'issue')">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'pages'"/>
				<xsl:with-param name="xpath" select="eterms:total-number-of-pages"/>
			</xsl:call-template>
		</xsl:if>
		<!-- SOURCE -->
		<xsl:apply-templates select="source:source"/>			
		<!-- END OF ENTRY -->		
		<xsl:value-of select="concat('}','')"/>	
		<xsl:text disable-output-escaping="yes">&#xD;&#xA;&#xD;&#xA;</xsl:text>
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
	
	<xsl:template match="eterms:publishing-info/dc:publisher">
		<xsl:if test=".!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'publisher'"/>
			<xsl:with-param name="xpath" select="."/>
		</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="eterms:publishing-info/eterms:place">
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
		<xsl:if test=".!=''">
			<xsl:call-template name="createField">
				<xsl:with-param name="name" select="'keywords'"/>
				<xsl:with-param name="xpath" select="."/>
			</xsl:call-template>
		</xsl:if>
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
		
		<xsl:value-of select="jfunc:texString($name)"/>
		<xsl:text disable-output-escaping="yes"> = &#123;</xsl:text>
		<xsl:choose>
			<xsl:when test="$name = 'title' or $name = 'booktitle' or $name = 'series' or $name = 'booktitle' or $name = 'journal' or $name = 'abstract' or $name = 'keywords '">
				<xsl:value-of select="concat('&#123;', jfunc:texString(normalize-space($xpath)), '&#125;')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="jfunc:texString(normalize-space($xpath))"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:text disable-output-escaping="yes">&#125;,&#xD;&#xA;</xsl:text>
	</xsl:template>
	
	<!-- SOURCE -->
	<xsl:template match="source:source">
		<xsl:variable name="sourceType">
			<xsl:value-of select="@type"/>
		</xsl:variable>
		<xsl:variable name="publication-type" select="parent::pub:publication/@type"/>
		<!-- TITLE -->
		<xsl:variable name="sgenre" select="$genre-ves/enum[@uri=$sourceType]"/>
		<xsl:choose>
			<xsl:when test="$sgenre='series'">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'series'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$sgenre='journal' or $sgenre='newspaper' or $sgenre='issue' ">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'journal'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$sgenre='book' or $sgenre='proceedings' or $sgenre='collected-edition' or $sgenre='handbook' or $sgenre='festschrift'  or $sgenre='encyclopedia' ">
				<xsl:call-template name="createField">
					<xsl:with-param name="name" select="'booktitle'"/>
					<xsl:with-param name="xpath" select="dc:title"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<!-- SOURCE CREATOR -->
		<xsl:if test="exists(eterms:creator[@role = $creator-ves/enum[.='author']/@uri or @role = $creator-ves/enum[.='editor']/@uri]/*)">
			<xsl:text disable-output-escaping="yes">note = "</xsl:text>	
				<xsl:for-each select="eterms:creator[@role = $creator-ves/enum[.='author']/@uri or @role = $creator-ves/enum[.='editor']/@uri]">
					<xsl:if test="position() &gt; 1">; </xsl:if>
					<xsl:variable name="role" select="@role"/>
					<xsl:choose>
						<xsl:when test="exists(person:person)">
							<xsl:value-of select="$creator-ves/enum[@uri = $role]"/>: <xsl:value-of select="person:person/eterms:family-name"/>, <xsl:value-of select="person:person/eterms:given-name"/>
						</xsl:when>
						<xsl:when test="exists(organization:organization)">
							<xsl:value-of select="$creator-ves/enum[@uri = $role]"/>: <xsl:value-of select="organization:organization/dc:title"/>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
			<xsl:text disable-output-escaping="yes">",&#xD;&#xA;</xsl:text>
		</xsl:if>
		
		
		<!-- SOURCE VOLUME -->
		<xsl:if test="eterms:volume!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'volume'"/>
			<xsl:with-param name="xpath" select="eterms:volume"/>
		</xsl:call-template>
		</xsl:if>
		
		<!-- SOURCE ISSUE -->
		<xsl:if test="eterms:issue!=''">
		<xsl:call-template name="createField">
			<xsl:with-param name="name" select="'issue'"/>
			<xsl:with-param name="xpath" select="eterms:issue"/>
		</xsl:call-template>
		</xsl:if>
		
		<!-- SOURCE PAGES -->
		<xsl:choose>
			<xsl:when test="fn:exists(parent::pub:publication/eterms:total-number-of-pages) 
								and ($genre-ves/enum[@uri=$publication-type] = 'book'
								or $genre-ves/enum[@uri=$publication-type] = 'proceedings'
								or $genre-ves/enum[@uri=$publication-type] = 'issue')" />
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="normalize-space(eterms:start-page)!='' and normalize-space(eterms:end-page) != ''">
						<xsl:call-template name="createField">
							<xsl:with-param name="name" select="'pages'"/>
							<xsl:with-param name="xpath" select="concat(eterms:start-page, ' - ', eterms:end-page)"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="normalize-space(eterms:start-page)!='' and (not(eterms:end-page) or normalize-space(eterms:end-page) = '') ">
						<xsl:call-template name="createField">
							<xsl:with-param name="name" select="'pages'"/>
							<xsl:with-param name="xpath" select="eterms:start-page"/>
						</xsl:call-template>				
					</xsl:when>
				</xsl:choose>
				
			</xsl:otherwise>
		</xsl:choose>
			
		<!-- TODO SOURCE HOWPUBLISHED -->		
	</xsl:template>
	
	<xsl:template match="eterms:publishing-info/eterms:edition">
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
	<xsl:template match="eterms:creator">	
		<xsl:apply-templates select="person:person"/>			
		<xsl:apply-templates select="organization:organization"/>		
	</xsl:template>
	
	<xsl:template name="roleLabel">
		<xsl:variable name="role-string" select="../@role"/>	
		<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-string]"/>	
		<xsl:value-of select="$role"/>
		<!-- <xsl:text disable-output-escaping="yes"> = "</xsl:text>-->
	</xsl:template>
	
	<xsl:template match="person:person">			
		<xsl:variable name="role-string" select="../@role"/>	
		<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-string]"/>
		<xsl:choose>		
			<xsl:when test="count(../preceding-sibling::eterms:creator[@role=$role/@uri])=0">	
				<!-- <xsl:call-template name="roleLabel"/>-->
				<xsl:value-of select="concat($role, ' = &#123;')"/>						
			</xsl:when>
			<xsl:when test="count(../preceding-sibling::eterms:creator[@role=$role/@uri])=0 and count(../parent::source:source)=1">	
				<xsl:value-of select="concat($role, ' = &#123;')"/>				
			</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:variable name="familyname" select="jfunc:texString(normalize-space(eterms:family-name))"/>
		<xsl:variable name="givenname" select="jfunc:texString(normalize-space(eterms:given-name))"/>
		<xsl:value-of select="concat($familyname, ', ', $givenname, '')"/>		
		<!-- AND-connection of persons -->		
		<xsl:choose>		
			<xsl:when test="exists(../following-sibling::eterms:creator[@role=$role/@uri])">
				<xsl:value-of select="' and '"/>
			</xsl:when>
			<xsl:otherwise>				
				<xsl:text disable-output-escaping="yes">&#125;,&#xD;&#xA;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template match="organization:organization">		
		<xsl:variable name="role-string" select="../@role"/>	
		<xsl:variable name="role" select="$creator-ves/enum[@uri=$role-string]"/>	
		<xsl:choose>	
		<xsl:when test="count(../preceding-sibling::eterms:creator[@role=$role/@uri])=0 and count(../parent::source:source)=1">	
			<xsl:value-of select="concat($role, ' : ')"/>				
		</xsl:when>	
		<xsl:when test="count(../preceding-sibling::eterms:creator[@role=$role/@uri])=0">	
			<!-- <xsl:call-template name="roleLabel"/>-->	
			<xsl:value-of select="concat($role, ' : ')"/>					
		</xsl:when>		
		<xsl:otherwise></xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="jfunc:texString(dc:title)"/>
		<!-- AND-connection of orgas -->		
		<xsl:variable name="role" select="$creator-ves/enum[@uri=../@role]"/>
		<xsl:choose>		
			<xsl:when test="exists(../following-sibling::*:creator[@role=$role])">
				<xsl:value-of select="' and '"/>
			</xsl:when>
			<xsl:otherwise>		
				<xsl:choose>
					<xsl:when test="count(../parent::source:source)=0">
						<xsl:text disable-output-escaping="yes">",&#xD;&#xA;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text disable-output-escaping="yes">  </xsl:text>
					</xsl:otherwise>	
				</xsl:choose>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- IDENTIFIER TEMPLATE -->
	<xsl:template name="identifiers">
	    <xsl:param name="identifier-list"/>
	        <xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:ISSN'])">
	            <xsl:variable name="issn-concated">
	                <xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:ISSN']" separator="; " />
	            </xsl:variable>
	            <xsl:call-template name="createField">
                    <xsl:with-param name="name" select="'issn'"/>
                    <xsl:with-param name="xpath" select="jfunc:texString($issn-concated)"/>
                </xsl:call-template>
	        </xsl:if>
	        <xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:ISBN'])">
	            <xsl:variable name="isbn-concated">
	                <xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:ISBN']" separator="; " />
	            </xsl:variable>
	            <xsl:call-template name="createField">
                    <xsl:with-param name="name" select="'isbn'"/>
                    <xsl:with-param name="xpath" select="jfunc:texString($isbn-concated)"/>
                </xsl:call-template>
	        </xsl:if>
	        <xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:URI' or @xsi:type='eterms:URN'])">
	            <xsl:variable name="url-concated">
	                <xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:URI' or @xsi:type='eterms:URN']" separator="; " />
	            </xsl:variable>
	            <xsl:call-template name="createField">
                    <xsl:with-param name="name" select="'url'"/>
                    <xsl:with-param name="xpath" select="jfunc:texString($url-concated)"/>
                </xsl:call-template>
	        </xsl:if>
	        <xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:DOI'])">
	            <xsl:variable name="doi-concated">
	                <xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:DOI']" separator="; " />
	            </xsl:variable>
	            <xsl:call-template name="createField">
                    <xsl:with-param name="name" select="'doi'"/>
                    <xsl:with-param name="xpath" select="jfunc:texString($doi-concated)"/>
                </xsl:call-template>
	        </xsl:if>
	        <xsl:if test="exists($identifier-list/dc:identifier[@xsi:type='eterms:OTHER' and fn:starts-with(fn:lower-case(.), 'local-id:')]) ">
	            <xsl:variable name="local-id-concated">
	                <xsl:value-of select="$identifier-list/dc:identifier[@xsi:type='eterms:OTHER' and fn:starts-with(fn:lower-case(.), 'local-id:')]" separator="; " />
	            </xsl:variable>
	            <xsl:call-template name="createField">
                    <xsl:with-param name="name" select="'localid'"/>
                    <xsl:with-param name="xpath" select="jfunc:texString($local-id-concated)"/>
                </xsl:call-template>
	        </xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
