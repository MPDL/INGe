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
	Transformations from eSciDoc PubItem to eDoc Item in 
	eDoc import schema: http://edoc.mpg.de/doc/schema/zim_transfer.xsd
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
		xmlns:organizational-unit="http://www.escidoc.de/schemas/organizationalunit/0.7"
		>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
<!--	<xsl:param name="pubman_instance">${escidoc.pubman.instance.url}</xsl:param>-->
	<xsl:param name="pubman_instance">http://qa-pubman.mpdl.mpg.de:8080/pubman</xsl:param>
<!--	<xsl:param name="fw_instance">${escidoc.common.framework.url}</xsl:param>-->
	<xsl:param name="coreservice_instance">http://coreservice.mpdl.mpg.de:8080</xsl:param>
	
	<xsl:variable name="vm" select="document('ves-mapping.xml')/mappings"/>
	
	<xsl:variable name="mpipl-daffs">
		<map escd="escidoc:55201">11683</map> 	 
		<map escd="escidoc:55209" mpgunit="escidoc:55201">19528</map>
		<map escd="escidoc:55208" mpgunit="escidoc:55201">19526</map>
		<map escd="escidoc:55204" mpgunit="escidoc:55201">19524</map>
	</xsl:variable>
	
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
		
			<xsl:call-template name="metametadata"/>
			
			<!--copyrights-->
			<xsl:element name="rights">
				<xsl:if test="count(dc:rights)>0">
					<xsl:element name="copyright">
						<xsl:value-of select="string-join (dc:rights[.!=''], ' - ')"/>
					</xsl:element>
				</xsl:if>
			</xsl:element>
			
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
			<!-- not relevant for MPIPL -->
			
			<xsl:if test="count(eterms:creator/person:person)=0">
				<xsl:value-of select="
					error(
						QName('http://www.escidoc.de/transformation', 'err:NoCreators' ), 
							concat ('No creators in item: ', ../../../@objid )
				)
				" />				
			</xsl:if>
			<xsl:for-each select="eterms:creator">			
				<xsl:call-template name="creator"/>
			</xsl:for-each>
			
			<xsl:call-template name="publication"/>
			
			<xsl:call-template name="content"/>
			
			<xsl:call-template name="docaff"/>
			
			<!--
				not relevant for MPIPL 
				<xsl:call-template name="relation"/>
			 -->
			
		</xsl:element>
	</xsl:template>
	
	<!-- METAMETADATA -->
	<xsl:template name="metametadata">
		<xsl:element name="metametadata">
			<xsl:element name="localid">
				<xsl:value-of select="../../../@objid"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- CREATOR -->
	<xsl:template name="creator">
	
		<xsl:variable name="objid" select="../../../../@objid"/>
		<xsl:variable name="esdr" select="@role"/>
		<xsl:for-each select="*">
			<xsl:variable name="edr" select="$vm/creator-role/v2-to-edoc/map[@v2=$esdr]"/>
			<!-- strict handling -->
<!--			<xsl:variable name="role" select="-->
<!--				if (exists($edr))  -->
<!--				then $edr-->
<!--				else error(-->
<!--					QName('http://www.escidoc.de/transformation', 'err:NoMappingForEnum' ), -->
<!--						concat ('No mapping escidoc to edoc for creator role: ', $esdr, ', item id: ', $objid)-->
<!--					)-->
<!--			"/>-->

			<!--   TODO: MPIPL
			bad item escidoc:64780, creator has no role -->
			<xsl:variable name="role" select="
				if (exists($edr))  
				then $edr
				else 'author'
			"/>
			
			
			<xsl:element name="creator">
				<xsl:attribute name="role" select="$role"/>
				<xsl:attribute name="creatortype" select="
					if (local-name()='person') then 'individual' else 'group' 
				"/>
				<xsl:if test="local-name()='person'">
					<xsl:attribute name="internextern" select="
						if ( func:isMpgMember(organization:organization/dc:identifier) ) then 'mpg' else 'unknown'
					"/>
					
					
					<xsl:element name="creatorini">
						<xsl:value-of select="func:get_initials(eterms:given-name)"/>
					</xsl:element>
					
					<xsl:element name="creatornfamily">
						<xsl:value-of select="eterms:family-name"/>
					</xsl:element>
					
					<xsl:element name="creatorngiven">
						<xsl:value-of select="eterms:given-name"/>
					</xsl:element>
					
				</xsl:if>
				
				<xsl:if test="local-name()='organization'">
					<xsl:attribute name="internextern" select="
						if ( func:isMpgMember(dc:identifier) ) then 'mpg' else 'unknown'
					"/>				
					<xsl:element name="creatornfamily">
						<xsl:value-of select="dc:title"/>
					</xsl:element>
				</xsl:if>
			</xsl:element>		
		</xsl:for-each>
		
	</xsl:template>
	
	<!-- PUBLICATION -->
	<xsl:template name="publication">
		
		
		<xsl:element name="publication">
		
			<!-- PUBLISHER -->
			<xsl:if test="
				not(../@type=(
					'http://purl.org/eprint/type/ConferencePaper', 
					'http://purl.org/escidoc/metadata/ves/publication-types/webpage', 
					'http://purl.org/escidoc/metadata/ves/publication-types/issue', 
					'http://purl.org/escidoc/metadata/ves/publication-types/paper', 
					'http://purl.org/eprint/type/Report'
					)
				) 
				and eterms:publishing-info">
				<xsl:element name="publisher">
					<xsl:value-of select="eterms:publishing-info/dc:publisher" />
				</xsl:element>
			</xsl:if>
			
			<!-- PUBLISHERADD -->
			<xsl:if test="
				../@type=(
					'http://purl.org/eprint/type/BookItem',
					'http://purl.org/escidoc/metadata/ves/publication-types/proceedings',
					'http://purl.org/eprint/type/Book',
					'http://purl.org/escidoc/metadata/ves/publication-types/series',
					'http://purl.org/escidoc/metadata/ves/publication-types/journal',
					'http://purl.org/eprint/type/Thesis'
				) 
				and eterms:publishing-info/eterms:place">
				<xsl:element name="publisheradd">
					<xsl:value-of select="eterms:publishing-info/eterms:place[1]" />
				</xsl:element>
			</xsl:if>
			
			<!-- DATEACCEPTED -->
			<xsl:if test="eterms:published-online!=''">
				<xsl:element name="dateaccepted">
					<xsl:value-of select="func:getDate(eterms:published-online)" />
				</xsl:element>
			</xsl:if>
			<xsl:if test="eterms:published-online='' and dcterms:dateAccepted!=''">
				<xsl:element name="dateaccepted">
					<xsl:value-of select="func:getDate(dcterms:dateAccepted)" />
				</xsl:element>
			</xsl:if>
			
							
			<!-- DATEPUBLISHED -->
			<xsl:if test="
					not(../@type=(
						'http://purl.org/escidoc/metadata/ves/publication-types/series',
						'http://purl.org/escidoc/metadata/ves/publication-types/journal',
						'http://purl.org/eprint/type/Thesis'
					))
					and dcterms:issued
				">
				<xsl:element name="datepublished">
					<xsl:value-of select="func:getDate(dcterms:issued)" />
				</xsl:element>
			</xsl:if>
			
			<!-- ENDDATEOFEVENT -->
			<xsl:if test="event:event/eterms:end-date!='' or event:event/eterms:start-date!=''">
				<xsl:element name="enddateofevent">
					<xsl:value-of select="
						func:getDate(
							if (event:event/eterms:end-date!='') 
							then event:event/eterms:end-date 
							else event:event/eterms:start-date
						)
					"/>
				</xsl:element>
			</xsl:if>
			
			<!-- DATESUBMITTED -->
			<xsl:if test="dcterms:dateSubmitted">
				<xsl:element name="datesubmitted">
					<xsl:value-of select="func:getDate(dcterms:dateSubmitted)" />
				</xsl:element>
			</xsl:if>
	
		
			<xsl:variable name="g1" select="
				not(../@type=(
					'http://purl.org/escidoc/metadata/ves/publication-types/series',
					'http://purl.org/escidoc/metadata/ves/publication-types/journal',
					'http://purl.org/escidoc/metadata/ves/publication-types/webpage', 
					'http://purl.org/eprint/type/Thesis',
					'http://purl.org/escidoc/metadata/ves/publication-types/issue',
					'http://purl.org/escidoc/metadata/ves/publication-types/paper',
					'http://purl.org/eprint/type/Report'
				))"/>
				
			<!-- ARTNUM, ISSUENR  -->
			<xsl:if test="$g1">
				<!-- ARTNUM -->
				<xsl:if test="source:source[1]/eterms:sequence-number">
					<xsl:element name="artnum">
						<xsl:value-of select="source:source[1]/eterms:sequence-number" />
					</xsl:element>
				</xsl:if>
				
				<!-- ISSUENR -->
				<xsl:if test="source:source[1]/eterms:issue">
					<xsl:element name="issuenr">
						<xsl:value-of select="source:source[1]/eterms:issue" />
					</xsl:element>
				</xsl:if>
				
			</xsl:if>
			
			<!-- VOLUME -->
			<xsl:if test="
				../@type=(
					'http://purl.org/escidoc/metadata/ves/publication-types/article',
					'http://purl.org/eprint/type/Report'
				)">
				<xsl:if test="source:source[1]/eterms:volume">
					<xsl:element name="volume">
						<xsl:value-of select="source:source[1]/eterms:volume" />
					</xsl:element>
				</xsl:if>
			</xsl:if>
	
			<!-- SPAGE, EPAGE -->
			<xsl:if test="$g1">
				<!-- SPAGE -->
				<xsl:if test="source:source[1]/eterms:start-page">
					<xsl:element name="spage">
						<xsl:value-of select="source:source[1]/eterms:start-page" />
					</xsl:element>
				</xsl:if>
				
				<!-- EPAGE -->
				<xsl:if test="source:source[1]/eterms:end-page">
					<xsl:element name="epage">
						<xsl:value-of select="source:source[1]/eterms:end-page" />
					</xsl:element>
				</xsl:if>
			</xsl:if>
		
			<xsl:for-each select="(source:source[1])">
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
						<xsl:element name="booktitle">
							<xsl:value-of select="dc:title"/>
						</xsl:element>
					
						<xsl:variable name="cre_con">
							
							<!-- BOOKCREATORFN -->
							<xsl:if test="count(eterms:creator[@role='http://www.loc.gov/loc.terms/relators/AUT']/person:person)>0">
								<xsl:element name="bookcreatorfn">
									<xsl:value-of select="func:screators(eterms:creator[@role='http://www.loc.gov/loc.terms/relators/AUT']/person:person)"/>
								</xsl:element>
							</xsl:if>
							
							<!-- BOOKCONTRIBUTORFN -->
							<xsl:if test="count(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)>0">
								<xsl:element name="bookcontributorfn">
									<xsl:value-of select="func:screators(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)"/>
								</xsl:element>
							</xsl:if>
						
						</xsl:variable>
						
						
						<xsl:if test="$cre_con">
							<xsl:copy-of select="$cre_con" />
						</xsl:if>
						<xsl:if test="not($cre_con)">
						
							<!-- BOOKCORPORATEBODY -->
							<xsl:if test="eterms:publishing-info/dc:publisher">
								<xsl:element name="bookcorporatebody">
									<xsl:value-of select="eterms:publishing-info/dc:publisher" />
								</xsl:element>
								<xsl:if test="eterms:publishing-info/eterms:place">
									<xsl:value-of select="concat ('&#xA;', eterms:publishing-info/eterms:place)" />
								</xsl:if>
							</xsl:if>
						</xsl:if>
						
						<!-- EDITIONDESCRIPTION -->
						<xsl:if test="eterms:publishing-info/eterms:edition">
							<xsl:element name="editiondescription">
								<xsl:value-of select="eterms:publishing-info/eterms:edition" />
							</xsl:element>
						</xsl:if>
						
						
					</xsl:element>
				</xsl:if>
			</xsl:if>

			<!-- INISSUE -->
			<xsl:if test="@type='http://purl.org/escidoc/metadata/ves/publication-types/issue'">
				<xsl:if test="dc:title">
					<xsl:element name="inissue">
					
						<!-- ISSUETITLE -->
						<xsl:element name="issuetitle">
							<xsl:value-of select="dc:title"/>
						</xsl:element>
					
						<!-- ISSUECONTRIBUTORFN -->
						<xsl:if test="count(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)>0">
							<xsl:element name="issuecontributorfn">
								<xsl:value-of select="func:screators(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)"/>
							</xsl:element>
						</xsl:if>
					
						<!-- ISSUECORPORATEBODY -->
						<xsl:if test="eterms:publishing-info/dc:publisher">
							<xsl:element name="issuecorporatebody">
								<xsl:value-of select="eterms:publishing-info/dc:publisher" />
								<xsl:if test="eterms:publishing-info/eterms:place">
									<xsl:value-of select="concat ('&#xA;', eterms:publishing-info/eterms:place)" />
								</xsl:if>
							</xsl:element>
						</xsl:if>
						
					</xsl:element>
				</xsl:if>
			</xsl:if>
			
		
			<!-- INJOUIRNAL -->
			<xsl:if test="@type='http://purl.org/escidoc/metadata/ves/publication-types/journal'">
				<xsl:if test="dc:title">
					<xsl:element name="injournal">
					
						<!-- JOURNALTITLE -->
						<xsl:element name="journaltitle">
							<xsl:value-of select="dc:title"/>
						</xsl:element>
					
						<!-- JOURNALABBREVIATION -->
						<xsl:if test="dcterms:alternative[1]">
							<xsl:element name="journalabbreviation">
								<xsl:value-of select="dcterms:alternative[1]"/>
							</xsl:element>
						</xsl:if>
						
					</xsl:element>
				</xsl:if>
			</xsl:if>
			
			<!-- INPROCEEDINGS -->
			<xsl:if test="@type='http://purl.org/escidoc/metadata/ves/publication-types/proceedings'">
				<xsl:if test="dc:title">
					<xsl:element name="inproceedings">
					
						<!-- TITLEOFPROCEEDINGS -->
						<xsl:element name="titleofproceedings">
							<xsl:value-of select="dc:title"/>
						</xsl:element>
					
						<!-- PROCEEDINGSCONTRIBUTORFN -->
						<xsl:if test="count(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)>0">
							<xsl:element name="proceedingscontributorfn">
								<xsl:value-of select="func:screators(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)"/>
							</xsl:element>
						</xsl:if>
						
					</xsl:element>
				</xsl:if>
			</xsl:if>
			
			<!-- INSERIES -->
			<xsl:if test="@type='http://purl.org/escidoc/metadata/ves/publication-types/series'">
				<xsl:if test="dc:title">
					<xsl:element name="inseries">
					
						<!-- TITLEOFSERIES -->
						<xsl:element name="titleofseries">
							<xsl:value-of select="dc:title"/>
						</xsl:element>
					
						<xsl:variable name="scfn">
							<!-- SERIESCONTRIBUTORFN -->
							<xsl:if test="count(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)>0">
								<xsl:element name="seriescontributorfn">
									<xsl:value-of select="func:screators(eterms:creator[@role!='http://www.loc.gov/loc.terms/relators/AUT']/person:person)"/>
								</xsl:element>
							</xsl:if>
						</xsl:variable>
						
						<xsl:if test="$scfn">
							<xsl:copy-of select="$scfn" />
						</xsl:if>
						<xsl:if test="not($scfn)">
						
							<!-- SERIESCORPORATEBODY -->
							<xsl:if test="eterms:publishing-info/dc:publisher">
								<xsl:element name="seriescorporatebody">
									<xsl:value-of select="eterms:publishing-info/dc:publisher" />
									<xsl:if test="eterms:publishing-info/eterms:place">
										<xsl:value-of select="concat ('&#xA;', eterms:publishing-info/eterms:place)" />
									</xsl:if>
								</xsl:element>
							</xsl:if>
						</xsl:if>						
						
						
					</xsl:element>
				</xsl:if>
			</xsl:if>
		
		</xsl:element>
	
	</xsl:template>
	
	<!-- CONTENT -->
	<xsl:template name="content">
		
		<xsl:element name="content">
		
			<!-- ABSTRACT -->
			<xsl:if test="dcterms:abstract[1]">
				<xsl:element name="abstract">
					<xsl:value-of select="dcterms:abstract[1]"/>
				</xsl:element>
			</xsl:if>
	
			
			<!-- TODO: versioncomment -->
	
			<!-- DATEOFEVENT -->
			<xsl:if test="event:event/eterms:start-date!=''">
				<xsl:element name="dateofevent">
					<xsl:value-of select="func:getDate(event:event/eterms:start-date)" />
				</xsl:element>
			</xsl:if>
	
			<!-- DISCIPLINE -->
			<xsl:variable name="ds" select="
				string-join (dc:subject[.!=''], '; ')
			"/>
			<xsl:if test="$ds">
				<xsl:element name="discipline">
					<xsl:value-of select="$ds" />
				</xsl:element>
			</xsl:if>
			
			<!-- EDUCATIONALPURPOSE  -->
			<!-- MPIPL specific -->
			<xsl:element name="educationalpurpose">no</xsl:element>

			
			<xsl:variable name="thesis" as="xs:boolean" select="
				@type=(
					'http://purl.org/eprint/type/Thesis',
					'http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation',
					'http://purl.org/escidoc/metadata/ves/academic-degrees/phd'
				)			
			"/>
			
			<!-- ENDUSER  -->
			<!-- MPIPL specific -->
			<xsl:element name="enduser">
				<xsl:value-of select="
					if ($thesis=false()) then 'expertsonly' else 'notspecified' 							
				"/>
			</xsl:element>
			
			<!-- IDENTIFIERS -->	
			<xsl:call-template name="identifiers"/>
			
			
			<!-- KEYWORDS -->
			<xsl:if test="dcterms:subject">
				<xsl:element name="keywords">
					<xsl:value-of select="normalize-space(dcterms:subject)" />
				</xsl:element>
			</xsl:if>
			
			<!-- LANGUAGE -->
			<xsl:variable name="esdl" select="dc:language" />
			<xsl:if test="$esdl">
				<xsl:variable name="edl" select="$vm/language/v2-to-edoc/map[@v2=$esdl]"/>
				<xsl:if test="$edl">
					<xsl:element name="language">
						<xsl:value-of select="$edl"/>
					</xsl:element>
				</xsl:if>		
			</xsl:if>
			
			
			<!-- INVITATIONSTATUS -->
			<xsl:variable name="esdis" select="event:event/eterms:invitation-status"/>
			<xsl:if test="$esdis">
				<xsl:element name="invitationstatus">
					<xsl:value-of select="if ($esdis='invited') then 'invited' else 'notspec'"/>
				</xsl:element>
			</xsl:if>		
			
			<!-- NAMEOFEVENT -->
			<xsl:if test="event:event/dc:title">
				<xsl:element name="nameofevent">
					<xsl:value-of select="event:event/dc:title"/>
				</xsl:element>
			</xsl:if>
					
			
			<!-- PLACEOFEVENT -->
			<xsl:if test="event:event/eterms:place">
				<xsl:element name="placeofevent">
					<xsl:value-of select="event:event/eterms:place"/>
				</xsl:element>
			</xsl:if>
	
	
			<!-- PUBSTATUS -->
			<!-- 
				MPIPL specific: should be all published to be ready for YB   
			-->
			<xsl:element name="pubstatus">published</xsl:element>
			
			<!-- PHYDESC -->
			<xsl:if test="eterms:total-number-of-pages">
				<xsl:element name="phydesc">
					<xsl:value-of select="eterms:total-number-of-pages" />
				</xsl:element>
			</xsl:if>
			
			<!-- REFEREED -->
			<xsl:variable name="esdrm" select="eterms:review-method"/>
			<xsl:if test="$esdrm">
				<xsl:element name="refereed">
					<xsl:value-of select="$vm/review-method/v2-to-edoc/map[@v2=$esdrm]"/>
				</xsl:element>
			</xsl:if>
	
			<!-- TITLE -->
			<xsl:element name="title">
				<xsl:value-of select="dc:title"/>
			</xsl:element>
			
			<!-- TITLEALT -->
			<xsl:if test="dcterms:alternative[1]">
				<xsl:element name="titlealt">
					<xsl:value-of select="dcterms:alternative[1]"/>
				</xsl:element>
			</xsl:if>
			
			<!-- TOC -->
			<xsl:if test="dcterms:tableOfContents">
				<xsl:element name="toc">
					<xsl:value-of select="dcterms:tableOfContents"/>
				</xsl:element>
			</xsl:if>
			
			<!-- DATEMODIFIED -->
			<xsl:if test="dcterms:modified">
				<xsl:element name="datemodified">
					<xsl:value-of select="dcterms:modified"/>
				</xsl:element>
			</xsl:if>
	
			<!-- THESISTYPE -->
			<xsl:if test="$thesis=false()"> 
				<xsl:variable name="esdd" select="eterms:degree"/>
				<xsl:variable name="edd" select="$vm/academic-degree/v2-to-edoc/map[@v2=$esdd]"/>
				<xsl:if test="$esdd">
					<xsl:element name="thesistype">
						<xsl:value-of select="$edd"/>
					</xsl:element>
				</xsl:if>
			</xsl:if>
			
		</xsl:element>
			
	</xsl:template>
		
		
	<!-- IDENTIFIERS -->	
	<xsl:template name="identifiers">
	
		<!-- IDENTIFIERS from dc:identifier elements -->
		<xsl:for-each select="dc:identifier">
			<xsl:variable name="esdt" select="@xsi:type" />
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
		<!-- 
		<xsl:for-each select="../../../escidocComponents:components/escidocComponents:component[escidocComponents:properties/prop:visibility='public']">
		
			<xsl:if test="escidocComponents:content/@storage='external-url' and escidocComponents:content/@xlink:href">
				<xsl:element name="identifier">
					<xsl:attribute name="type">url</xsl:attribute>
					<xsl:value-of select="escidocComponents:content/@xlink:href"/>
				</xsl:element> 
			</xsl:if>
			
			<xsl:if test="escidocComponents:content/@storage='internal-managed' and escidocComponents:content/@xlink:title"> 
				<xsl:element name="identifier">
					<xsl:attribute name="type">url</xsl:attribute>
					<xsl:value-of select="concat(
						$pubman_instance,
						'/item/',
						../../@objid, 
						'/component/',
						@objid,
						'/',
						escidocComponents:content/@xlink:title	
					)"/>
				</xsl:element>
			</xsl:if>
			
			
			 Not pubman relevant 
			<xsl:if test="escidocComponents:content/@storage='external-managed'">
			</xsl:if>
			
		</xsl:for-each>
		-->
		
	</xsl:template>
	
	
	<!-- DOCAFF -->
	<xsl:template name="docaff">
	
		<xsl:element name="docaff">
		
			<!-- DOCAFF_EXTERNAL -->
			<xsl:variable name="da_ext"  select="func:docaff_external(eterms:creator/person:person/organization:organization)"/>
			<xsl:if test="$da_ext!=''">
				<xsl:element name="docaff_external">	
					<xsl:value-of select="$da_ext"/>
				</xsl:element>
			</xsl:if>	
			
			<!-- DOCAFF_RESEARCHCONTEXT -->
			<!-- empty for MPIPL -->
			
			<!-- AFFs -->
			<!-- 
			<xsl:variable name="affs" select="func:docaff_persons(eterms:creator/person:person/organization:organization)" />
			<xsl:if test="$affs!=''">
				<xsl:copy-of select="$affs"/>
			</xsl:if>
			-->
			<!-- MPIPL has only one top AFF of institute-->
			<xsl:element name="aff">
				<xsl:element name="mpgunit">
					<xsl:value-of select="$mpipl-daffs/map[not(@mpgunit | @mpgsunit)]"/>
				</xsl:element>
			</xsl:element>
			
		</xsl:element>
		 
	</xsl:template>
	
	<xsl:template name="relation">
	</xsl:template>
	
	<!-- THE MPG MEMBERSHIP BLOCK -->
	<xsl:function name="func:isMpgMember" as="xs:boolean">
		<xsl:param name="ids"/>
		<xsl:variable name="result">
			<xsl:for-each select="$ids[.!='']">
				<xsl:if test=".='${escidoc.pubman.root.organisation.id}'">true</xsl:if>
				<xsl:if test=".!='${escidoc.pubman.root.organisation.id}'">
					<xsl:call-template name="checkOU">
						<xsl:with-param name="id" select="."/>
					</xsl:call-template>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:value-of select="if (contains ($result,'true') ) then true() else false ()"/>		
	</xsl:function>
	
	<xsl:template name="checkOU">
		<xsl:param name="id"/>
		
<!--		TODO: The exception will be thrown in case of the undefined id  -->
		<xsl:variable name="parents" select="
			document (
				concat (
					$coreservice_instance,
					'/oum/organizational-unit/',
					translate ($id, ' ', ''), 
					'/parents'
				)
			)
		"/>
<!--					'${escidoc.framework_access.framework.url}/oum/organizational-unit/',-->


		<xsl:for-each select="$parents/organizational-unit:parents/srel:parent">
			<xsl:if test="@xlink:href='/oum/organizational-unit/${escidoc.pubman.root.organisation.id}'">true</xsl:if>
			<xsl:if test="@xlink:href!='/oum/organizational-unit/${escidoc.pubman.root.organisation.id}'">
				<xsl:call-template name="checkOU">
					<xsl:with-param name="id" select="substring-after(@xlink:href, '/oum/organizational-unit/')"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<!-- END OF THE MPG MEMBERSHIP BLOCK -->	
	
	<xsl:function name="func:screators">
		<xsl:param name="p"/>
		<xsl:for-each select="$p">
			<xsl:if test="eterms:family-name">
				<xsl:value-of select="eterms:family-name"/>
			</xsl:if>
			<xsl:if test="eterms:given-name">
				<xsl:value-of select="concat(', ', eterms:given-name)"/>
			</xsl:if>
			<xsl:value-of select="if (position ()!=last()) then ' - ' else ''"/>	
		</xsl:for-each>
	</xsl:function>
	
	<xsl:function name="func:docaff_external">
		<xsl:param name="ous"/>
		<xsl:for-each select="$ous[not(func:isMpgMember(dc:identifier))]">
			<xsl:value-of select="dc:title"/>
			<xsl:value-of select="if (position ()!=last()) then ' - ' else ''"/>	
		</xsl:for-each>
	</xsl:function>
	
	<!-- INTERNAL AFFILIATIONS -->
	<xsl:function name="func:docaff_persons">
		<xsl:param name="ous"/>
		<xsl:variable name="daffs">
			<xsl:for-each select="$ous[func:isMpgMember(dc:identifier)]">
				<xsl:variable name="escd" select="dc:identifier"/>
				<xsl:variable name="ed" select="$mpipl-daffs/map[@escd=$escd]"/>
				<xsl:if test="$ed!=''">
					<xsl:element name="aff">
						<!-- MPIPL has always mpgunit at the top -->
						<xsl:element name="mpgunit">
							<xsl:value-of select="$mpipl-daffs/map[not(@mpgunit | @mpgsunit)]"/>
						</xsl:element>
						<xsl:if test="$ed/@mpgunit">
							<xsl:element name="mpgsunit">
								<xsl:value-of select="$ed"/>
							</xsl:element>
						</xsl:if>
						<xsl:if test="$ed/@mpgsunit">
							<xsl:element name="mpgsunit">
								<xsl:value-of select="$mpipl-daffs/map[@escd=$ed/@mpgsunit]"/>
							</xsl:element>
							<xsl:element name="mpgssunit">
								<xsl:value-of select="$ed"/>
							</xsl:element>
						</xsl:if>
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<!-- exclude duplicates -->
		<xsl:for-each select="$daffs/*[not(.=following::node())]">
			<xsl:copy-of select="."/>
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
			<xsl:value-of select="
				translate ($d, '.', '-')
			"/>
	</xsl:function>	
	
</xsl:stylesheet>
