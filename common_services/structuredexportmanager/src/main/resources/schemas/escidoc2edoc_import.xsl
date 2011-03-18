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


 Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<!-- 
	Transformations from eSciDoc PubItem to eDoc Item in 
	eDoc import schema: http://edoc.mpg.de/doc/schema/zim_transfer.xsd
	Mapping: http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_eSciDoc_To_eDoc_Mapping  
	Author: Vlad Makarenko (initial creation) 
	$Author: mfranke $ (last changed)
	$Revision: 3183 $ 
	$LastChangedDate: 2010-05-27 16:10:51 +0200 (Thu, 27 May 2010) $
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
				<xsl:element name="zim_transfer">
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
		
			<!-- METAMETADATA -->
			<xsl:element name="metametadata">
			
				<!-- LOCALID -->
				<xsl:copy-of select="func:genElement(('localid', ../../../@objid))"/>
				
			</xsl:element>	


			<!-- RIGHTS -->
			<xsl:element name="rights">
				<xsl:copy-of select="func:genElement((
					'copyright',  
					string-join(dc:rights[.!=''], ' - ') 
					))"/>
			</xsl:element>
	
			
			<!-- GENRE-->
			<xsl:element name="genre">
				<xsl:variable name="esd" select="@type"/>
				<xsl:variable name="gid" select="$vm/publication-type/v2-to-edoc/map[@v2=$esd]"/>
				<xsl:value-of select="
				if (exists($gid))  
				then $gid
				else error(
					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), 
						concat ('No mapping escidoc to edoc for publication type: ', $esd )
				)
				" />				
			</xsl:element>
			
			
			<!-- CORPORATEBODY -->
			<!-- NOT in the mapping!!! -->
			
			<!-- xsl:if test="count(eterms:creator/person:person)=0">
				<xsl:value-of select="
					error(
						QName('http://www.escidoc.de/transformation', 'err:NoCreators' ), 
							concat ('No creators in item: ', ../../../@objid )
				)
				" />				
			</xsl:if-->
			
			
			<xsl:for-each select="eterms:creator">			
				<xsl:call-template name="creator">
					<xsl:with-param name="type" select="'import'"/>
				</xsl:call-template>
			</xsl:for-each>
			
			<xsl:call-template name="publication"/>
			
			<xsl:call-template name="content"/>
			
			<!-- DOCAFFs -->
			<xsl:call-template name="docaff">
				<xsl:with-param name="type" select="'import'"/>
			</xsl:call-template>						
			
			<!-- RELATIONs are not in the MAPPING -->			
			<!--
				not relevant for MPIPL 
				<xsl:call-template name="relation"/>
			 -->
			
		</xsl:element>
	</xsl:template>

	
	<!-- METAMETADATA -->
	<xsl:template name="metametadata">
	
				
	</xsl:template>
	
	
	<!-- PUBLICATION -->
	<xsl:template name="publication">
		
		<xsl:variable name="objid" select="../../../@objid"/>
		<xsl:variable name="s" select="source:source[1]"/>
		
		<xsl:element name="publication">
		
			<!-- PUBLISHER -->
			<xsl:copy-of select="func:genElement(('publisher', eterms:publishing-info/dc:publisher[1]))"/>


			<!-- PUBLISHERADD -->
			<xsl:copy-of select="func:genElement(('publisheradd', eterms:publishing-info/eterms:place[1]))"/>


			<!-- DATES -->
			<xsl:variable name="dp" select="
				func:coalesce((dcterms:issued, eterms:published-online))
			"/>
			
			<!-- DATEACCEPTED -->
			<xsl:copy-of select="func:genElementIf((
				@type='http://purl.org/eprint/type/Thesis',	
				'dateaccepted', 
				func:getDate(func:coalesce((dcterms:dateAccepted, $dp)))
			))"/>

			<!-- DATEPUBLISHED -->
			<!-- no datepublished for thesises-->
			<xsl:copy-of select="func:genElementIf((
				$dp!='' and @type!='http://purl.org/eprint/type/Thesis',	
				'datepublished', 
				func:getDate($dp)
			))"/>
			
			<!-- ENDDATEOFEVENT -->
			<xsl:copy-of select="func:genElement((
				'enddateofevent', 
				func:getDate(func:coalesce((event:event/eterms:end-date, event:event/eterms:start-date)))
			))"/>
			

			<!-- DATESUBMITTED -->
			<xsl:copy-of select="func:genElement(('datesubmitted', func:getDate(dcterms:dateSubmitted)))"/>

			<!-- END of DATES -->


			<!-- ARTNUM -->
			<xsl:copy-of select="func:genElement(('artnum', $s/eterms:sequence-number))"/>
			
			
			<!-- ISSUENR -->
			<xsl:copy-of select="func:genElement(('issuenr', $s/eterms:issue))"/>
			
			
			<!-- VOLUME -->
			<xsl:copy-of select="func:genElement(('volume', $s/eterms:volume))"/>
			
			
			<!-- SPAGE -->
			<xsl:copy-of select="func:genElement(('spage', $s/eterms:start-page))"/>
			
			
			<!-- EPAGE -->
			<xsl:copy-of select="func:genElement(('epage', $s/eterms:end-page))"/>			
			
		
			<xsl:for-each select="$s">
				<xsl:call-template name="source"/>
			</xsl:for-each>	
	
		</xsl:element>
	
	</xsl:template>
	
	
	
	<!-- SOURCE -->
	<xsl:template name="source">
	
		<xsl:element name="source">
		
			<!-- INBOOK -->
			<xsl:if test="@type='http://purl.org/eprint/type/Book'">
			
				<xsl:if test="dc:title">
					<xsl:element name="inbook">
					
						<!-- BOOKTITLE -->
						<xsl:copy-of select="func:genElement(('booktitle', dc:title))"/>
					
					
						<xsl:variable name="cre_con">
					
							<!-- BOOKCREATORFN -->
							<xsl:copy-of select="func:genElement((
								'bookcreatorfn',
								func:screators(eterms:creator[@role='http://www.loc.gov/loc.terms/relators/AUT']/person:person) 
							))"/>
						
							<!-- BOOKCONTRIBUTORFN -->
							<xsl:copy-of select="func:genElement((
								'bookcontributorfn',
								func:screators(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person) 
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
								(eterms:publishing-info/dc:publisher, eterms:publishing-info/eterms:place),
								'&#xA;'
							)
						))"/>
						
						<!-- EDITIONDESCRIPTION -->
						<xsl:copy-of select="func:genElement(('editiondescription', eterms:publishing-info/eterms:edition))"/>
						
					</xsl:element>
				</xsl:if>
			</xsl:if>

			<!-- INISSUE -->
			<xsl:if test="@type='http://purl.org/escidoc/metadata/ves/publication-types/issue'">
				<xsl:if test="dc:title">
					<xsl:element name="inissue">
					
						<!-- ISSUETITLE -->
						<xsl:copy-of select="func:genElement(('issuetitle', dc:title))"/>
					
						<!-- ISSUECONTRIBUTORFN -->
						<xsl:copy-of select="func:genElement((
							'issuecontributorfn', 
							func:screators(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)
						))"/>
	
						<!-- ISSUECORPORATEBODY -->
						<xsl:copy-of select="func:genElement((
							'issuecorporatebody',
							string-join(
								( eterms:publishing-info/dc:publisher, eterms:publishing-info/eterms:place ),
								'&#xA;'
							) 
						))"/>
						
					</xsl:element>
				</xsl:if>
			</xsl:if>
			
			
			<!-- INJOUIRNAL -->
			<xsl:if test="@type='http://purl.org/escidoc/metadata/ves/publication-types/journal'">
				<xsl:if test="dc:title">
					<xsl:element name="injournal">
					
						<!-- JOURNALTITLE -->
						<xsl:copy-of select="func:genElement(('journaltitle', dc:title))"/>
					
						<!-- JOURNALABBREVIATION -->
						<xsl:copy-of select="func:genElement(('journalabbreviation', dcterms:alternative[1]))"/>
						
					</xsl:element>
				</xsl:if>
			</xsl:if>
				
			<!-- INPROCEEDINGS -->
			<xsl:if test="@type='http://purl.org/escidoc/metadata/ves/publication-types/proceedings'">
				<xsl:if test="dc:title">
					<xsl:element name="inproceedings">
					
						<!-- TITLEOFPROCEEDINGS -->
						<xsl:copy-of select="func:genElement(('titleofproceedings', dc:title))"/>
					
						<!-- PROCEEDINGSCONTRIBUTORFN -->
						<xsl:copy-of select="func:genElement((
							'proceedingscontributorfn', 
							func:screators(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)
						))"/>
						
					</xsl:element>
				</xsl:if>
			</xsl:if>
				
			<!-- INSERIES -->
			<xsl:if test="@type='http://purl.org/escidoc/metadata/ves/publication-types/series'">
				<xsl:if test="dc:title">
					<xsl:element name="inseries">
					
						<!-- TITLEOFSERIES -->
						<xsl:copy-of select="func:genElement(('titleofseries', dc:title))"/>
					
						<xsl:variable name="scfn">
							<!-- SERIESCONTRIBUTORFN -->
							<xsl:copy-of select="func:genElement((
								'seriescontributorfn', 
								func:screators(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)
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
								(eterms:publishing-info/dc:publisher, eterms:publishing-info/eterms:place),
								'&#xA;'
							)
						))"/>		
						
					</xsl:element>
				</xsl:if>
			</xsl:if>
	
		</xsl:element>
	
	</xsl:template>
	
	
	<!-- CONTENT -->
	<xsl:template name="content">
		
		<xsl:element name="content">
		
			<!-- ABSTRACT -->
			<xsl:copy-of select="func:genElement(('abstract', dcterms:abstract[1]))"/>
	
			
			<!-- VERSIONCOMMENT -->
			<xsl:copy-of select="func:genElement((
				'versioncomment', 
				../../../ei:properties/prop:version[version:status='released']/version:comment
			))"/>
	
			<!-- DATEOFEVENT -->
			<xsl:copy-of select="func:genElement(('dateofevent', event:event/eterms:start-date))"/>

	
			<!-- DISCIPLINE -->
			<xsl:copy-of select="func:genElement((
				'discipline', 
				string-join (dc:subject[.!=''], '; ')
			))"/>
			
			
			<!-- EDUCATIONALPURPOSE  tbd -->
			<xsl:element name="educationalpurpose">no</xsl:element>
			
			
			<!-- ENDUSER  tbd -->
			<xsl:element name="enduser">notspecified</xsl:element>
			
			
			<!-- FTURLs -->
			<xsl:call-template name="fturl">
				<xsl:with-param name="size" select="false()"/>
			</xsl:call-template>

			
			<!-- IDENTIFIERS -->	
			<xsl:call-template name="identifiers"/>
			
			
			<!-- INSTREMARKS is not in the MAPPING -->
			
			
			<!-- KEYWORDS -->
			<xsl:copy-of select="func:genElement(('keywords', dcterms:subject))"/>
			
						
			<!-- LANGUAGE -->
			<xsl:variable name="esdl" select="normalize-space(dc:language[1])" />
			<xsl:copy-of select="func:genElement(('language', $vm/language/v2-to-edoc/map[@v2=$esdl]))"/>
			
			<!-- 
				MARKUPABSTRACT,
				MARKUPTITLE,
				MARKUPTYPE
				are not in the MAPPING -->
			

			<!-- INVITATIONSTATUS -->
			<xsl:copy-of select="func:genElementIf((
				event:event/eterms:invitation-status!='',
				'invitationstatus',
				 if (event:event/eterms:invitation-status='invited') then 'invited' else 'notspec'
			))"/>
	

			<!-- NAMEOFEVENT -->
			<xsl:copy-of select="func:genElement(('nameofevent', event:event/dc:title))"/>


			<!-- 
				NUMBEROFWORDS,
				OS,
				OSVERSION
				are not in the MAPPING -->
				

			<!-- PLACEOFEVENT -->
			<xsl:copy-of select="func:genElement(('placeofevent', event:event/eterms:place))"/>
	
			
			<!-- PLATFORM is not in the MAPPING -->
	
	
			<!-- PUBSTATUS -->
			<xsl:copy-of select="func:genElementIf((
				../../../ei:properties/prop:public-status='released',
				'pubstatus', 
				'published'
			))"/>
			
			
			<!-- PHYDESC -->
			<xsl:copy-of select="func:genElement(('phydesc', eterms:total-number-of-pages))"/>
			
			
			<!-- REFEREED -->
			<xsl:variable name="esdrm" select="normalize-space(eterms:review-method)"/>
			<xsl:if test="$esdrm">
				<xsl:element name="refereed">
					<xsl:value-of select="$vm/review-method/v2-to-edoc/map[@v2=$esdrm]"/>
				</xsl:element>
			</xsl:if>
			
	
			<!-- TITLE -->
			<xsl:copy-of select="func:genElement(('title', dc:title))"/>	
			
			
			<!-- TITLEALT -->
			<xsl:copy-of select="func:genElement(('titlealt', dcterms:alternative[1]))"/>


			<!-- TOC -->
			<xsl:copy-of select="func:genElementPlain(('toc', dcterms:tableOfContents))"/>
			

			<!-- DATEMODIFIED tbd propbaly lastdatemodified from props??? -->
			<xsl:copy-of select="func:genElement(('datemodified', func:getDate(dcterms:modified)))"/>

	
			<!-- THESISTYPE -->
			<xsl:if test="@type='http://purl.org/eprint/type/Thesis'"> 
				<xsl:variable name="esdd" select="eterms:degree"/>
				<xsl:variable name="esdd" select="$vm/academic-degree/v2-to-edoc/map[@v2=$esdd]"/>
				<xsl:copy-of select="func:genElement(('thesistype', $esdd ))"/>
			</xsl:if>
			
			
		</xsl:element>
			
	</xsl:template>
		
	
	
</xsl:stylesheet>
