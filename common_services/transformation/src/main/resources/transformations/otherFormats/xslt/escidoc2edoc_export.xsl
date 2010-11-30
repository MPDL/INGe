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
	Transformations from eSciDoc PubItem to eDoc Item in eDoc export schema
	Mapping: http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_eSciDoc_To_eDoc_Mapping  
	Author: Vlad Makarenko (initial creation) 
	$Author$ (last changed)
	$Revision$ 
	$LastChangedDate$
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
	
	<xsl:param name="pubman_instance">${escidoc.pubman.instance.url}</xsl:param>
<!--	<xsl:param name="pubman_instance">http://pubman.mpdl.mpg.de/pubman</xsl:param>-->
	<xsl:param name="coreservice_instance">${escidoc.common.framework.url}</xsl:param>
<!--	<xsl:param name="coreservice_instance">http://coreservice.mpdl.mpg.de</xsl:param>-->
	
	<xsl:variable name="vm" select="document('../../ves-mapping.xml')/mappings"/>
	
	<!-- Organizational Units, flat structure -->
	<xsl:variable name="OUs" select="
		document(concat ($coreservice_instance, '/oum/organizational-units' ) )
		//organizational-unit:organizational-unit
	"/>

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
			
			<xsl:call-template name="docaff"/>
			
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
		
			<xsl:call-template name="fturl"/>
			
			<xsl:call-template name="basic"/>
			
			<xsl:call-template name="creators"/>
			
			<xsl:call-template name="identifiers"/>
		
		</xsl:element>
		
	</xsl:template>
		
		
	<!--== DOCAFF ==-->	
	<xsl:template name="docaff">
	
			
		<xsl:if test="$OUs">
	
			
			
			<xsl:variable name="da">
			
				<!-- DOCAFF_EXTERNAL -->
				<xsl:variable name="ext_affs" select="
					func:getNonMpgAffiliations(eterms:creator/person:person/organization:organization)
				"/>
				<xsl:if test="$ext_affs!=''">
					<xsl:copy-of select="$ext_affs" copy-namespaces="no"/>
				</xsl:if>
				
				<!-- AFFs -->
				<xsl:variable name="int_affs" select="
					func:getMpgAffiliations(eterms:creator/person:person/organization:organization)
				"/> 
				<xsl:if test="$int_affs!=''">
					<xsl:copy-of select="$int_affs" copy-namespaces="no"/>
				</xsl:if>
				
			</xsl:variable>
			
			<xsl:copy-of select="func:genElement(('docaff', $da))"/>
		
		</xsl:if>
		 
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



	<!-- FTURL -->	
	<xsl:template name="fturl">	

		<xsl:variable name="imc" select="
			../../../escidocComponents:components/escidocComponents:component/escidocComponents:content[@storage='internal-managed' and @xlink:href]
		"/>
		
		<xsl:if test="count($imc)>1">
			
			<xsl:for-each select="$imc">
			
				<xsl:element name="fturl">
					
					<xsl:variable name="vft" select="../escidocComponents:properties/prop:visibility"/>
					<xsl:variable name="vft" select="$vm/fulltext-visibility/v2-to-edoc/map[@v2=$vft]"/>
					
					<xsl:attribute name="viewftext" select="
						func:coalesce(($vft, $vm/fulltext-visibility/v2-to-edoc/@default))
					"/>
					
					<xsl:attribute name="filename" select="@xlink:title"/>
					
					<xsl:attribute name="size" select="
						../mdr:md-records/mdr:md-record[1]/file:file/dcterms:extent
					"/>
					
					<xsl:value-of select="concat(
						$coreservice_instance,
						@xlink:href	
					)"/>
					
				</xsl:element>
				
			</xsl:for-each>
			
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
		
		</xsl:element>
		
		
	</xsl:template>
	
	
	<!-- CREATORS -->
	<xsl:template name="creators">
		<xsl:element name="creators">
			<xsl:for-each select="eterms:creator">			
				<xsl:call-template name="creator"/>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
	


	<!-- CREATOR -->
	<xsl:template name="creator">
	
		<!-- template specific variables -->
		<xsl:variable name="objid" select="../../../../@objid"/>
		<xsl:variable name="esdr" select="normalize-space(@role)"/>
		
		<xsl:for-each select="*">
			<xsl:variable name="edr" select="$vm/creator-role/v2-to-edoc/map[@v2=$esdr]"/>
			
			<xsl:element name="creator">
				<xsl:attribute name="role" select="$edr"/>
				<xsl:attribute name="creatorType" select="
					if (local-name()='person') then 'individual' else 'group' 
				"/>
				<xsl:if test="local-name()='person'">
					<xsl:variable name="ouo" select="func:getOUTree(organization:organization/dc:identifier)"/>
					<xsl:attribute name="internextern" select="
						if ( $ouo[@mpg='false'] or $ouo//aff[@mpg='false'] ) then 'unknown' else 'mpg'
					"/>
					
					<xsl:copy-of select="func:genElement(('creatorini', func:get_initials(eterms:given-name)))"/>
					
					<xsl:copy-of select="func:genElement(('creatornfamily', eterms:family-name))"/>
					
					<xsl:copy-of select="func:genElement(('creatorngiven', eterms:given-name))"/>
					
				</xsl:if>
				
				<xsl:if test="local-name()='organization'">
				
					<xsl:variable name="ouo" select="func:getOUTree(dc:identifier)"/>
					<xsl:attribute name="internextern" select="
						if ( $ouo[@mpg='false'] or $ouo//aff[@mpg='false'] ) then 'unknown' else 'mpg'
					"/>				
					
					<xsl:copy-of select="func:genElement(('creatornfamily', dc:title))"/>
										
				</xsl:if>
				
			</xsl:element>		
		</xsl:for-each>
		
	</xsl:template>
	
	
	<!-- IDENTIFIERS -->	
	<xsl:template name="identifiers">

		<xsl:element name="identifiers">
		
			<!-- ESCIDOC direct link as identifier -->
			<xsl:element name="identifier">
				<xsl:attribute name="type">url</xsl:attribute>
				<xsl:value-of select="concat($pubman_instance, '/item/', ../../../@objid)"/>
			</xsl:element>
		
			<!-- IDENTIFIERS from dc:identifier elements -->
			<xsl:for-each select="dc:identifier">
				<xsl:variable name="esdt" select="normalize-space(@xsi:type)" />
				<xsl:variable name="edt" select="$vm/identifier-type/v2-to-edoc/map[@v2=$esdt]"/>
				<!-- not strict for the moment -->
				<xsl:if test="$edt">
					<xsl:element name="identifier">
						<xsl:attribute name="type" select="$edt"/>
						<xsl:value-of select="."/>
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			
		<!-- IDENTIFIERS from components --> 
			<xsl:for-each select="../../../escidocComponents:components/escidocComponents:component/escidocComponents:content[@storage='external-url' and @xlink:href]">
				<xsl:element name="identifier">
					<xsl:attribute name="type">url</xsl:attribute>
					<xsl:value-of select="@xlink:href"/>
				</xsl:element> 				
			</xsl:for-each>
				
			
		</xsl:element>
				
	</xsl:template>	

		
	
	<!-- AFFILIATIONS -->
	<!--
		Generate list of MPG affiliations (internal affiliations):
		
		<affiliation>
			<mpgunit id="mpgunit_id">name of mpgunit</mpgunit>
			<mpgsunit id="mpgsunit_id">name of mpgsunit</mpgunit>
			<mpgssunit id="mpgssunit_id">name of mpgssunit</mpgunit>
		</affiliation>
		....
		
		1) Only 3 levels will be taken, exclusive top MPG level
		2) Only unique affiliations, no duplicates  			   
	 -->
	<xsl:function name="func:getMpgAffiliations">
		<xsl:param name="ous"/>
		<xsl:variable name="daffs">
			<xsl:for-each select="func:getOUTree($ous/dc:identifier)//aff[@mpg='true']/..">
				<xsl:element name="affiliation">
					<xsl:element name="mpgunit">
						<xsl:attribute name="id" select="dc:identifier"/>
						<xsl:value-of select="dc:title"/>
					</xsl:element>
					<xsl:if test="..">
						<xsl:element name="mpgsunit">
							<xsl:attribute name="id" select="../dc:identifier"/>
							<xsl:value-of select="../dc:title"/>
						</xsl:element>
						<xsl:if test="../..">
							<xsl:element name="mpgssunit">
								<xsl:attribute name="id" select="../../dc:identifier"/>
								<xsl:value-of select="../../dc:title"/>
							</xsl:element>
						</xsl:if>
					</xsl:if>
				</xsl:element>
			</xsl:for-each>
		</xsl:variable>
		<!-- exclude duplicates -->
		<xsl:for-each select="$daffs/*[not(.=following::node())]">
			<xsl:copy-of select="."/>
		</xsl:for-each>
	</xsl:function>
	
	<!--
		Generate list of NON MPG affiliations (external affiliations):
				
		1) All levels will be taken, inclusive top MPG level
		2) Only unique affiliations, no duplicates  			   
	 -->	
	<xsl:function name="func:getNonMpgAffiliations">
		<xsl:param name="ous"/>
<!--		<xsl:message select="func:getOUTree($ous/dc:identifier)//aff[@mpg='false']"/>-->
		<xsl:variable name="affs">
			<xsl:for-each select="func:getOUTree($ous/dc:identifier)//aff[@mpg='false']">
				<xsl:element name="aff">
					<xsl:value-of select="dc:title"/>
					<xsl:value-of select="func:childOU(..)"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:variable>
		<!-- exclude duplicates -->
		<xsl:variable name="affs">
			<xsl:for-each select="$affs/*[not(.=following::node())]">
				<xsl:copy-of select="."/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:if test="$affs">
			<xsl:element name="docaff_external">
				<xsl:value-of select="string-join($affs/aff, '; ')"/>
			</xsl:element>
		</xsl:if>
		
	</xsl:function>
	
	
	<xsl:function name="func:childOU">
		<xsl:param name="ou"/>
		<xsl:if test="$ou/dc:title">
			<xsl:value-of select="concat( ' - ', $ou/dc:title, func:childOU($ou/..) )"/>
		</xsl:if>
	</xsl:function>
	
	
	<xsl:function name="func:getOUTree">
		<xsl:param name="ids"/>
		<xsl:for-each select="$ids[.!='']">
			<xsl:call-template name="getOU">
				<xsl:with-param name="ou" select="func:getOUXml(.)"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:function>


	<xsl:function name="func:getOUXml">
		<xsl:param name="id"/>
		<xsl:copy-of select="
			$OUs[substring-after(@xlink:href, '/oum/organizational-unit/')=$id]
		"/>
	</xsl:function>
	

	<xsl:template name="getOU">
	
		<xsl:param name="ou"/>
		
		<xsl:element name="aff">
		
			<xsl:choose>
				<!-- if no parents anymore, check MPG  -->
				<xsl:when test="not($ou/organizational-unit:parents/srel:parent)">
					<xsl:attribute name="mpg" select="if ($ou/@xlink:href='/oum/organizational-unit/${escidoc.pubman.root.organisation.id}') then 'true' else 'false'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getOU">
						<xsl:with-param name="ou" select="
							func:getOUXml(
								substring-after($ou/organizational-unit:parents/srel:parent/@xlink:href, '/oum/organizational-unit/')
							)"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
			
			<xsl:element name="dc:title">
				<xsl:value-of select="$ou/@xlink:title"/>
			</xsl:element>
			
			<xsl:element name="dc:identifier">
				<xsl:value-of select="substring-after($ou/@xlink:href, '/oum/organizational-unit/') "/>
			</xsl:element>
			
		</xsl:element>
		
	</xsl:template>

	
	<xsl:function name="func:genElement">
		<xsl:param name="p"/>
		<xsl:if test="$p[1]!='' and $p[2]!=''">
			<xsl:element name="{$p[1]}">
				<xsl:value-of select="normalize-space($p[2])"/>
			</xsl:element>
		</xsl:if>
	</xsl:function>
	
	<xsl:function name="func:genElementIf">
		<xsl:param name="p"/>
		<xsl:if test="$p[1]=true() and $p[2]!='' and $p[3]!=''">
			<xsl:element name="{$p[2]}">
				<xsl:value-of select="normalize-space($p[3])"/>
			</xsl:element>
		</xsl:if>
	</xsl:function>
	
	<xsl:function name="func:genElementPlain">
		<xsl:param name="p"/>
			<xsl:if test="$p[1]!='' and $p[2]!=''">
				<xsl:element name="{$p[1]}">
					<xsl:value-of select="$p[2]"/>
				</xsl:element>
			</xsl:if>
	</xsl:function>
	
	<xsl:function name="func:coalesce">
		<xsl:param name="nodes"/>
		<xsl:value-of select="
			if (count($nodes)=0)
			then ''
			else if ($nodes[1]!='') 
			then $nodes[1] 
			else func:coalesce(($nodes[position()>1]))  
		"/>
	</xsl:function>

	
	
	<xsl:function name="func:screators">
		<xsl:param name="p"/>
		<xsl:for-each select="$p">
			<xsl:value-of select="
				concat (
					string-join( (eterms:family-name, eterms:given-name), ', ' ),
					if (position()!=last()) then '; ' else ''
				)	
			"/>
		</xsl:for-each>
	</xsl:function>
	
	
	<xsl:function name="func:get_initials">
		<xsl:param name="str" />
		<xsl:variable name="delim"
			select="if (contains ($str, '-')) then '-' else ' '" />
		<xsl:for-each select="tokenize(normalize-space ($str), '\s+|\.\s+|\-\s*')">
			<xsl:value-of
				select="concat(substring (., 1, 1), if (position()!=last())then concat ('.', $delim) else '.')" />
		</xsl:for-each>
	</xsl:function>
	
	
	<xsl:function name="func:getDate">
		<xsl:param name="d" />
		<xsl:value-of select="$d"/>
	</xsl:function>	
	

</xsl:stylesheet>
