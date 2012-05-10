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
	Transformations from eSciDoc PubItem to eDoc Item in eDoc export schema
	Mapping: http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_eSciDoc_To_eDoc_Mapping  
	Author: Vlad Makarenko (initial creation) 
	$Author: vmakarenko $ (last changed)
	$Revision: 3743 $ 
	$LastChangedDate: 2010-12-03 10:42:56 +0100 (Fri, 03 Dec 2010) $
-->
<xsl:stylesheet version="2.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns:fn="http://www.w3.org/2005/xpath-functions"
		xmlns:func="http://www.escidoc.de/transformation/functions"
		xmlns:xlink="http://www.w3.org/1999/xlink"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xmlns:dc="http://purl.org/dc/elements/1.1/"
		xmlns:dcterms="http://purl.org/dc/terms/"
		xmlns:ei="${xsd.soap.item.item}"
		xmlns:escidocComponents="${xsd.soap.item.components}"
		xmlns:mdr="${xsd.soap.common.mdrecords}"
		xmlns:mdp="${xsd.metadata.escidocprofile}"
		xmlns:ec="${xsd.soap.item.components}"
		xmlns:prop="${xsd.soap.common.prop}"
		xmlns:srel="${xsd.soap.common.srel}"
		xmlns:version="${xsd.soap.common.version}"
		xmlns:release="${xsd.soap.common.release}"
		xmlns:file="${xsd.metadata.file}"
		xmlns:pub="${xsd.metadata.publication}"  
		xmlns:person="${xsd.metadata.person}"
		xmlns:source="${xsd.metadata.source}"
		xmlns:eterms="${xsd.metadata.terms}"
		xmlns:event="${xsd.metadata.event}"
		xmlns:organization="${xsd.metadata.organization}"
		xmlns:escidocFunctions="urn:escidoc:functions"
		xmlns:escidoc="http://escidoc.mpg.de/"
		xmlns:Util="java:de.mpg.escidoc.services.transformation.Util"
		xmlns:itemlist="${xsd.soap.item.itemlist}"
		xmlns:organizational-unit="${xsd.soap.ou.ou}"
		>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:include href="escidoc2edoc_includes.xsl"/>
		

	<xsl:template match="/*">
		<xsl:choose>
			<xsl:when test="count(//pub:publication)>0">
				<xsl:element name="edoc">
				
					<xsl:for-each select="//pub:publication">
						<xsl:call-template name="record"/>
					</xsl:for-each>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoItemsForTransforamtion' ), 'Empty item list')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="record">
	
	
		<xsl:element name="record">
			<xsl:attribute name="id" select="../../../@objid"/>
		
			<xsl:call-template name="metadata"/>
			
			<xsl:call-template name="docaff">
				<xsl:with-param name="type" select="'export'"/>
			</xsl:call-template>
			
			<!-- MPG yearbook status NOT in the MAPPING -->
			<!-- <xsl:call-template name="mpgyearbook"/> -->
			
			<xsl:call-template name="metametadata"/>
			
			<!-- NOT in the mapping
				  
				<xsl:call-template name="copyrights"/>
			-->
			
		</xsl:element>
			
			
	</xsl:template>


	<!--== METADATA ==-->	
	<xsl:template name="metadata">
	
		<xsl:element name="metadata">
					
			<xsl:call-template name="basic"/>
			
			<xsl:call-template name="creators"/>
			
			<xsl:element name="identifiers">
				<xsl:call-template name="identifiers"/>
			</xsl:element>
			
		
		</xsl:element>
		
	</xsl:template>
		
		
	<!--== MPGYEARBOOK ==-->	
	<xsl:template name="mpgyearbook">
		<xsl:element name="MPGyearbook">
			<xsl:attribute name="status" select="'dummyRecommended'"/>
			<xsl:text>2010</xsl:text>
		</xsl:element>
	</xsl:template>
	
	
	<!--== METAMETADATA ==-->	
	<xsl:template name="metametadata">
		
		<xsl:element name="metametadata">
			
			<!-- LASTMODIFIED -->
			<xsl:copy-of select="func:genElement(('lastmodified', substring(../../../@last-modification-date, 1, 10)))"/>
			
			<!-- NOT in the MAPPING!!!			
				owner container
			-->
			
		</xsl:element>
		
	</xsl:template>
	
	
	
	<!--== COPYRIGHT ==-->	
	<xsl:template name="copyrights">
		
		<!-- NOT in the mapping, but specified here! tbd -->
		<xsl:if test="count(dc:rights)>0">
			<xsl:element name="rights">
				<xsl:copy-of select="func:genElement(('copyright', string-join (dc:rights[.!=''], ' - ')))"/>
			</xsl:element>
		</xsl:if>
		
	</xsl:template>


	
	<!-- BASIC -->	
	<xsl:template name="basic">
	
		<!-- template specific variables -->
		<xsl:variable name="objid" select="../../../@objid"/>
		<xsl:variable name="s" select="source:source[1]"/>
			
						
		<xsl:element name="basic">

			<xsl:element name="genre">
				<xsl:variable name="esd" select="normalize-space(@type)"/>
				<xsl:variable name="gid" select="$vm/publication-type/v2-to-edoc/map[@v2=$esd]/@edoc"/>
				<xsl:value-of select="
				if (exists($gid))  
				then $gid
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping escidoc to edoc for publication type: ', $esd )
				)
				" />				
			</xsl:element>


			<!-- NOT IN THE MAPPING !!!
			<xsl:element name="corporatebody">
			</xsl:element>
			 -->

			<!-- TITLE -->
			<xsl:copy-of select="func:genElement(('title', dc:title))"/>
			
			
			<!-- TITLEALT -->
			<xsl:copy-of select="func:genElement(('titlealt', dcterms:alternative[1]))"/>


			<!-- LANGUAGE -->
			<xsl:variable name="esdl" select="normalize-space(dc:language[1])" />
			<xsl:copy-of select="func:genElement(('language', $vm/language/v2-to-edoc/map[@v2=$esdl]))"/>
			

			<!-- PUBLISHER -->
			<xsl:copy-of select="func:genElement(('publisher', eterms:publishing-info/dc:publisher[1]))"/>

			<!-- PUBLISHERADD -->
			<xsl:copy-of select="func:genElement(('publisheradd', eterms:publishing-info/eterms:place[1]))"/>
			

			<!-- DATES -->
			
			<!-- DATEPUBLISHED -->
			<xsl:variable name="dp" select="
				func:coalesce((dcterms:issued, eterms:published-online))
			"/>
			<!-- no datepublished for thesises-->
			<xsl:if test="
				$dp!='' 
				and @type!='http://purl.org/eprint/type/Thesis'
			">
				<xsl:copy-of select="func:genElement(('datepublished', func:getDate($dp)))"/>
			</xsl:if>
			
			
			<!-- DATEMODIFIED -->
			<xsl:copy-of select="func:genElement(('datemodified', func:getDate(dcterms:modified)))"/>
			
			
			<!-- DATEACCEPTED -->			
			<xsl:if test="@type='http://purl.org/eprint/type/Thesis'">
				<xsl:copy-of select="
					func:genElement((
						'dateaccepted', 
						func:getDate(func:coalesce(($dp, dcterms:dateAccepted)))
				))"/>
			</xsl:if>


			<!-- DATESUBMITTED -->
			<xsl:copy-of select="func:genElement(('datesubmitted', func:getDate(dcterms:dateSubmitted)))"/>
			
	
			<!-- SPAGE -->
			<xsl:copy-of select="func:genElement(('spage', $s/eterms:start-page))"/>
			
			
			<!-- EPAGE -->
			<xsl:copy-of select="func:genElement(('epage', $s/eterms:end-page))"/>
			
			
			<!-- ARTNUM -->
			<xsl:copy-of select="func:genElement(('artnum', $s/eterms:sequence-number))"/>
			

			<!-- JOURNAL stuff -->
			<xsl:if test="$s/@type='http://purl.org/escidoc/metadata/ves/publication-types/journal'">
				<xsl:if test="$s/dc:title">
				
					<!-- JOURNALTITLE -->
					<xsl:copy-of select="func:genElement(('journaltitle', $s/dc:title))"/>
				
					<!-- JOURNALABBREVIATION -->
					<xsl:copy-of select="func:genElement(('journalabbreviation', $s/dcterms:alternative[1]))"/>
					
				</xsl:if>
			</xsl:if>


			<!-- ISSUE stuff -->
			<!-- ISSUENR -->
			<xsl:copy-of select="func:genElement(('issuenr', $s/eterms:issue))"/>
			
			<xsl:if test="$s/@type='http://purl.org/escidoc/metadata/ves/publication-types/issue'">
			
				<xsl:if test="$s/dc:title">
					
					<!-- ISSUETITLE -->
					<xsl:copy-of select="func:genElement(('issuetitle', $s/dc:title))"/>
					
					<!-- ISSUECONTRIBUTORFN -->
					<xsl:copy-of select="func:genElement((
						'issuecontributorfn', 
						func:screators($s/eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)
					))"/>

					<!-- ISSUECORPORATEBODY -->
					<xsl:copy-of select="func:genElement((
						'issuecorporatebody',
						string-join(
							( $s/eterms:publishing-info/dc:publisher, $s/eterms:publishing-info/eterms:place ),
							'&#xA;'
						) 
					))"/>
				</xsl:if>
			</xsl:if>


			<!-- VOLUME -->
			<xsl:copy-of select="func:genElement(('volume', $s/eterms:volume))"/>


			<!-- INVITATIONSTATUS -->
			<xsl:copy-of select="func:genElementIf((
				event:event/eterms:invitation-status!='',
				'invitationstatus',
				 if (event:event/eterms:invitation-status='invited') then 'invited' else 'notspec'
			))"/>

			
			<!-- NAMEOFEVENT -->
			<xsl:copy-of select="func:genElement(('nameofevent', event:event/dc:title))"/>
					
			
			<!-- PLACEOFEVENT -->
			<xsl:copy-of select="func:genElement(('placeofevent', event:event/eterms:place))"/>
			

			<!-- DATEOFEVENT -->
			<xsl:copy-of select="func:genElement(('dateofevent', event:event/eterms:start-date))"/>


			<!-- ENDDATEOFEVENT  -->
			<xsl:copy-of select="func:genElementIf((
				event:event/eterms:start-date!='' and event:event/eterms:end-date!='',
				'dateofevent', 
				func:coalesce((event:event/eterms:end-date, event:event/eterms:start-date))				
			))"/>
			

			<!-- SOURCE stuff -->

			<!-- BOOK stuff -->
			<xsl:if test="$s/@type='http://purl.org/eprint/type/Book'">
			
				<xsl:if test="$s/dc:title">
				
					<!-- BOOKTITLE -->
					<xsl:copy-of select="func:genElement(('booktitle', $s/dc:title))"/>
				
				
					<xsl:variable name="cre_con">
					
						<!-- BOOKCREATORFN -->
						<xsl:copy-of select="func:genElement((
							'bookcreatorfn',
							func:screators($s/eterms:creator[@role='http://www.loc.gov/loc.terms/relators/AUT']/person:person) 
						))"/>
					
						<!-- BOOKCONTRIBUTORFN -->
						<xsl:copy-of select="func:genElement((
							'bookcontributorfn',
							func:screators($s/eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person) 
						))"/>
						
					</xsl:variable>

					<xsl:if test="$cre_con!=''">
						<xsl:copy-of select="$cre_con" />
					</xsl:if>
					
					<!-- BOOKCORPORATEBODY -->
					<xsl:copy-of select="func:genElementIf((
						$cre_con='',
						'bookcorporatebody',
						string-join(
							($s/eterms:publishing-info/dc:publisher, $s/eterms:publishing-info/eterms:place),
							'&#xA;'
						)
					))"/>
					
				</xsl:if>
			
			</xsl:if>


			<!-- PROCEEDINGS stuff -->
			<xsl:if test="$s/@type='http://purl.org/escidoc/metadata/ves/publication-types/proceedings'">
				<xsl:if test="$s/dc:title">
				
					<!-- TITLEOFPROCEEDINGS -->
					<xsl:copy-of select="func:genElement(('titleofproceedings', $s/dc:title))"/>
				
					<!-- PROCEEDINGSCONTRIBUTORFN -->
					<xsl:copy-of select="func:genElement((
						'proceedingscontributorfn', 
						func:screators($s/eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)
					))"/>
					
				</xsl:if>
				
			</xsl:if>


			<!-- SERIES stuff -->
			<xsl:if test="$s/@type='http://purl.org/escidoc/metadata/ves/publication-types/series'">
				<xsl:if test="$s/dc:title">
					
					<!-- TITLEOFSERIES -->
					<xsl:copy-of select="func:genElement(('titleofseries', $s/dc:title))"/>
				
					<xsl:variable name="scfn">
						<!-- SERIESCONTRIBUTORFN -->
						<xsl:copy-of select="func:genElement((
							'seriescontributorfn', 
							func:screators($s/eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)
						))"/>
					</xsl:variable>
					
					<xsl:if test="$scfn!=''">
						<xsl:copy-of select="$scfn" />
					</xsl:if>
					
					<!-- SERIESCORPORATEBODY -->
					<xsl:copy-of select="func:genElementIf((
						$scfn='',
						'seriescorporatebody',
						string-join(
							($s/eterms:publishing-info/dc:publisher, $s/eterms:publishing-info/eterms:place),
							'&#xA;'
						)
					))"/>
											
				</xsl:if>
			</xsl:if>


					
		<!-- EDITIONDESCRIPTION -->
<!--					TODO: -->
<!--
		specify uri for genres
-->
		
			<xsl:copy-of select="func:genElementIf((
				$s/@type=(
					'http://purl.org/eprint/type/Book',
					'handbook',
					'festschrift',
					'commentary',
					'commentary'
				),
				'editiondescription', 
				$s/eterms:publishing-info/eterms:edition
			))"/>

	
			<!-- END of SOURCE stuff -->
			

			<!-- ABSTRACT -->
			<xsl:copy-of select="func:genElement(('abstract', dcterms:abstract[1]))"/>

			<xsl:copy-of select="func:genElement((
				'versioncomment', 
				../../../ei:properties/prop:version[version:status='released']/version:comment
			))"/>

			<!-- DISCIPLINE -->
			<xsl:copy-of select="func:genElement((
				'discipline', 
				string-join (dc:subject[.!=''], '; ')
			))"/>

			<!-- KEYWORDS -->
			<xsl:copy-of select="func:genElement(('keywords', dcterms:subject))"/>
			

			<!-- PHYDESC -->
			<xsl:copy-of select="func:genElement(('phydesc', eterms:total-number-of-pages))"/>
			
			
			<!-- TOC -->
			<xsl:copy-of select="func:genElementPlain(('toc', dcterms:tableOfContents))"/>
			
			
			<!-- PUBSTATUS -->
			<xsl:copy-of select="func:genElementIf((
				../../../ei:properties/prop:public-status='released',
				'pubstatus', 
				'published'
			))"/>
			
			
			<!-- REFEREED -->
			<xsl:variable name="esdrm" select="normalize-space(eterms:review-method)"/>
			<xsl:if test="$esdrm">
				<xsl:element name="refereed">
					<xsl:value-of select="$vm/review-method/v2-to-edoc/map[@v2=$esdrm]"/>
				</xsl:element>
			</xsl:if>
			
			<xsl:call-template name="fturl">
				<xsl:with-param name="size" select="true()"/>
			</xsl:call-template>
		
		</xsl:element>
		
		
	</xsl:template>
	
	
	<!-- CREATORS -->
	<xsl:template name="creators">
		<xsl:element name="creators">
			<xsl:for-each select="eterms:creator">			
				<xsl:call-template name="creator">
					<xsl:with-param name="type" select="'export'"/>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
	


	


</xsl:stylesheet>
