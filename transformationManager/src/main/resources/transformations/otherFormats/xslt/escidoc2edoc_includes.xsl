<?xml version="1.0" encoding="UTF-8"?>
<!--  CDDL HEADER START  The contents of this file are subject to the terms of the  Common Development and Distribution License, Version 1.0 only  (the "License"). You may not use this file except in compliance  with the License.  You can obtain a copy of the license at license/ESCIDOC.LICENSE  or http://www.escidoc.org/license.  See the License for the specific language governing permissions  and limitations under the License.  When distributing Covered Code, include this CDDL HEADER in each  file and include the License file at license/ESCIDOC.LICENSE.  If applicable, add the following below this CDDL HEADER, with the  fields enclosed by brackets "[]" replaced with your own identifying  information: Portions Copyright [yyyy] [name of copyright owner]  CDDL HEADER END  Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft  für wissenschaftlich-technische Information mbH and Max-Planck-  Gesellschaft zur Förderung der Wissenschaft e.V.  All rights reserved. Use is subject to license terms. -->
<!--   Shared functions/tempates for escidoc2edoc_export.xsl and escidoc2edoc_import.xsl   Author: Vlad Makarenko (initial creation)   $Author: vmakarenko $ (last changed)  $Revision: 3737 $   $LastChangedDate: 2010-11-30 19:28:24 +0100 (Tue, 30 Nov 2010) $ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:func="http://www.escidoc.de/transformation/functions"
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:organizational-unit="${xsd.soap.ou.ou}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:mdr="${xsd.soap.common.mdrecords}"
	xmlns:file="${xsd.metadata.file}"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:person="${xsd.metadata.person}"  >
	<xsl:param name="itemLink"/>
	<xsl:variable name="vm" select="document('ves-mapping.xml')/mappings"/>
	<!-- see: http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_eSciDoc_To_eDoc_Mapping#OU-Mapping_Yearbook_2010 -->
	<xsl:variable name="escd2edoc_daffs">
		<map escd="ou_55201">11683</map>
		<map escd="ou_24007">1528</map>
		<map escd="ou_159875">2724</map>
		<map escd="ou_159888">2725</map>
		<!-- Kernphysik <map escd="">4851</map> -->
		<!-- FHI <map escd="">1515</map> -->
		<!-- Biophysikalische Chemie -->
		<map escd="ou_persistent28">3315</map>
		<!-- Kognitions- und Neurowissenschaften <map escd="">18314</map> -->
		<!-- Chemische Ökologie -->
		<map escd="ou_24027">12859</map>
		<!-- Human Cognitive and Brain Sciences -->
		<map escd="ou_634548">18314</map>
		<!-- Wissenschaftsgeschichte -->
		<map escd="ou_XXX">1526</map>
	</xsl:variable>
	<!-- CREATOR -->
	<xsl:template name="creator">
		<xsl:param name="type"/>
		<!-- template specific variables -->
		<xsl:variable name="objid" select="../../../../@objid"/>
		<xsl:variable name="esdr" select="normalize-space(@role)"/>
		<xsl:for-each select="*">
			<xsl:variable name="edr" select="$vm/creator-role/v2-to-edoc/map[@v2=$esdr]"/>
			<xsl:element name="creator">
				<xsl:attribute name="role" select="$edr"/>
				<xsl:attribute name="{if ($type='import') then 'creatortype' else 'creatorType'}" select="  if (local-name()='person') then 'individual' else 'group'   "/>
				<xsl:if test="local-name()='person'">
					<xsl:variable name="ouo" select="func:getOUTree(organization:organization/dc:identifier)"/>
					<xsl:attribute name="internextern">
						<xsl:choose>
							<xsl:when test="$ouo[@mpg='true'] or $ouo//aff[@mpg='true']">mpg</xsl:when>
							<xsl:otherwise>unknown</xsl:otherwise>
						</xsl:choose>
					</xsl:attribute>
					<xsl:if test="$type='import'">
						<xsl:element name="creatorini">
							<xsl:value-of select="func:get_initials(eterms:given-name)"/>
						</xsl:element>
						<xsl:element name="creatornfamily">
							<xsl:value-of select="eterms:family-name"/>
						</xsl:element>
					</xsl:if>
					<xsl:if test="$type='export'">
						<xsl:copy-of select="func:genElement(('creatorini', func:get_initials(eterms:given-name)))"/>
						<xsl:copy-of select="func:genElement(('creatornfamily', eterms:family-name))"/>
					</xsl:if>
					<xsl:copy-of select="func:genElement(('creatorngiven', eterms:given-name))"/>
				</xsl:if>
				<xsl:if test="local-name()='organization'">
					<xsl:variable name="ouo" select="func:getOUTree(dc:identifier)"/>
					<xsl:attribute name="internextern" select="  if ( $ouo[@mpg='false'] or $ouo//aff[@mpg='false'] ) then 'unknown' else 'mpg'  "/>
					<!-- to comply zim_transfer.xsd -->
					<xsl:if test="$type='import'">
						<xsl:element name="creatorini"/>
					</xsl:if>
					<xsl:copy-of select="func:genElement(('creatornfamily', dc:title))"/>
				</xsl:if>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	<!-- FTURL -->
	<xsl:template name="fturl">
		<xsl:param name="size"/>
		<xsl:variable name="imc" select="  ../../../escidocComponents:components/escidocComponents:component/escidocComponents:content[@storage='internal-managed' and @xlink:href]  "/>
		<xsl:for-each select="$imc">
			<xsl:element name="fturl">
				<xsl:variable name="vft" select="../escidocComponents:properties/prop:visibility"/>
				<xsl:variable name="vft" select="$vm/fulltext-visibility/v2-to-edoc/map[@v2=$vft]"/>
				<xsl:attribute name="viewftext" select="  func:coalesce(($vft, $vm/fulltext-visibility/v2-to-edoc/@default))  "/>
				<xsl:attribute name="filename" select="@xlink:title"/>
				<xsl:if test="$size and exists(../mdr:md-records/mdr:md-record[1]/file:file/dcterms:extent)">
					<xsl:attribute name="size" select="  ../mdr:md-records/mdr:md-record[1]/file:file/dcterms:extent  "/>
				</xsl:if>
				<xsl:value-of select="@xlink:href"/>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	<!-- IDENTIFIERS -->
	<xsl:template name="identifiers">
		<!-- ESCIDOC direct link as identifier -->
		<xsl:element name="identifier">
			<xsl:attribute name="type">url</xsl:attribute>
            <xsl:value-of select="replace($itemLink, '\$1', ../../../@objid)"/>
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
	</xsl:template>
	<!--== DOCAFF ==-->
	<xsl:template name="docaff">
		<xsl:param name="type"/>
		<xsl:if test="$OUs">
			<xsl:variable name="da">
				<!-- DOCAFF_EXTERNAL -->
				<xsl:variable name="ext_affs" select="  func:getNonMpgAffiliations(eterms:creator/person:person/organization:organization)  "/>
				<xsl:if test="$ext_affs!=''">
					<xsl:copy-of select="$ext_affs" copy-namespaces="no"/>
				</xsl:if>
				<!-- AFFs -->
				<xsl:variable name="int_affs" select="  func:getMpgAffiliations(eterms:creator/person:person/organization:organization, $type)  "/>
				<xsl:if test="$int_affs!=''">
					<xsl:copy-of select="$int_affs" copy-namespaces="no"/>
				</xsl:if>
			</xsl:variable>
			<xsl:if test="$da!=''">
				<xsl:element name="docaff">
					<xsl:copy-of select="$da"/>
				</xsl:element>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<!-- Organizational Units, flat structure -->
	<xsl:variable name="OUs" select="  document(concat ($pubmanUrl, '/oum/organizational-units' ) )  //organizational-unit:organizational-unit  "/>
	<!-- AFFILIATIONS -->
	<!--  Generate list of MPG affiliations (internal affiliations):  <affiliation><mpgunit id="mpgunit_id">name of mpgunit</mpgunit><mpgsunit id="mpgsunit_id">name of mpgsunit</mpgunit><mpgssunit id="mpgssunit_id">name of mpgssunit</mpgunit></affiliation>....    1) Only 3 levels will be taken, exclusive top MPG level  2) Only unique affiliations, no duplicates   -->
	<xsl:function name="func:getMpgAffiliations">
		<xsl:param name="ous"/>
		<xsl:param name="type"/>
		<xsl:variable name="daffs">
			<xsl:for-each select="func:getOUTree($ous/dc:identifier)//aff[@mpg='true']/..">
				<xsl:choose>
					<!-- case: import -->
					<xsl:when test="$type='import'">
						<xsl:variable name="da" select="normalize-space(dc:identifier)"/>
						<!-- <xsl:message select="$da"/>-->
						<xsl:variable name="da" select="$escd2edoc_daffs/map[@escd=$da]"/>
						<xsl:if test="$da!=''">
							<xsl:element name="aff">
								<xsl:element name="mpgunit">
									<xsl:value-of select="$da"/>
								</xsl:element>
							</xsl:element>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<!-- case: export -->
						<xsl:element name="affiliation">
							<xsl:element name="mpgunit">
								<xsl:attribute name="id" select="dc:identifier"/>
								<xsl:value-of select="dc:title"/>
							</xsl:element>
							<xsl:if test="exists(..)">
								<xsl:element name="mpgsunit">
									<xsl:attribute name="id" select="../dc:identifier"/>
									<xsl:value-of select="../dc:title"/>
								</xsl:element>
								<xsl:if test="exists(../../dc:identifier)">
									<xsl:element name="mpgssunit">
										<xsl:attribute name="id" select="../../dc:identifier"/>
										<xsl:value-of select="../../dc:title"/>
									</xsl:element>
								</xsl:if>
							</xsl:if>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:for-each>
		</xsl:variable>
		<!-- <xsl:message select="$daffs"/>-->
		<!-- exclude duplicates -->
		<xsl:for-each select="$daffs/*[not(.=following::node())]">
			<xsl:copy-of select="."/>
		</xsl:for-each>
	</xsl:function>
	<!--  Generate list of NON MPG affiliations (external affiliations):    1) All levels will be taken, inclusive top MPG level  2) Only unique affiliations, no duplicates   -->
	<xsl:function name="func:getNonMpgAffiliations">
		<xsl:param name="ous"/>
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
				<xsl:value-of select="string-join($affs/aff[.!=''], '; ')"/>
			</xsl:element>
		</xsl:if>
	</xsl:function>
	<xsl:function name="func:childOU">
		<xsl:param name="ou"/>
		<xsl:if test="$ou/dc:title">
			<xsl:value-of select="concat( ' - ', $ou/dc:title, func:childOU($ou/..) )"/>
		</xsl:if>
	</xsl:function>
	<xsl:function name="func:getOUTree" as="item()*">
		<xsl:param name="ids"/>
		<!-- root in needed for //aff[@mpg] matching -->
		<!-- doesn't work otherwise!! -->
		<xsl:element name="ou_tree_root">
			<xsl:for-each select="$ids[.!='']">
				<xsl:call-template name="getOU">
					<xsl:with-param name="ou" select="func:getOUXml(.)"/>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:element>
	</xsl:function>
	<xsl:template name="getOU" as="item()*">
		<xsl:param name="ou"/>
		<xsl:element name="aff">
			<xsl:choose>
				<!-- if no parents anymore, check MPG -->
				<xsl:when test="not($ou/organizational-unit:parents/srel:parent)">
					<xsl:attribute name="mpg" select="if ($ou/@xlink:href='/oum/organizational-unit/${inge.pubman.root.organization.id}') then 'true' else 'false'"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="getOU">
						<xsl:with-param name="ou" select="  func:getOUXml(  substring-after($ou/organizational-unit:parents/srel:parent/@xlink:href, '/oum/organizational-unit/')  )"/>
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
	<xsl:function name="func:getOUXml" as="item()*">
		<xsl:param name="id"/>
		<xsl:copy-of select="  $OUs[substring-after(@xlink:href, '/oum/organizational-unit/')=$id]  "/>
	</xsl:function>
	<!-- MISC -->
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
		<xsl:value-of select="  if (count($nodes)=0)  then ''  else if ($nodes[1]!='')   then $nodes[1]   else func:coalesce(($nodes[position()>1]))   "/>
	</xsl:function>
	<xsl:function name="func:screators">
		<xsl:param name="p"/>
		<xsl:for-each select="$p">
			<xsl:value-of select="  concat (  string-join( (eterms:family-name, eterms:given-name), ', ' ),  if (position()!=last()) then '; ' else ''  )   "/>
		</xsl:for-each>
	</xsl:function>
	<xsl:function name="func:get_initials">
		<xsl:param name="str" />
		<xsl:variable name="delim"  select="if (contains ($str, '-')) then '-' else ' '" />
		<xsl:for-each select="tokenize(normalize-space ($str), '\s+|\.\s+|\-\s*')">
			<xsl:value-of  select="concat(substring (., 1, 1), if (position()!=last())then concat ('.', $delim) else '.')" />
		</xsl:for-each>
	</xsl:function>
	<xsl:function name="func:getDate">
		<xsl:param name="d" />
		<xsl:value-of select="$d"/>
	</xsl:function>
</xsl:stylesheet>
