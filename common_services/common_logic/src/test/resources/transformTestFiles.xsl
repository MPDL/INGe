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
<xsl:stylesheet
	xml:base="stylesheet" version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:prop="${xsd.soap.common.prop}"
	xmlns:srel="${xsd.soap.common.srel}"
	xmlns:version="${xsd.soap.common.version}"
	xmlns:release="${xsd.soap.common.release}"
	xmlns:item="${xsd.soap.item.item}"
	xmlns:context="${xsd.soap.context.context}"
	xmlns:components="${xsd.soap.item.components}"
	xmlns:contextlist="${xsd.soap.context.contextlist}"
	xmlns:itemlist="${xsd.soap.item.itemlist}"
	xmlns:mdrecords="${xsd.soap.common.mdrecords}"
	xmlns:relations="${xsd.soap.common.relations}"
	xmlns:searchresult="${xsd.soap.searchresult.searchresult}"
	xmlns:useraccount="${xsd.soap.useraccount.useraccount}"
	xmlns:useraccountlist="${xsd.soap.useraccount.useraccountlist}"
	xmlns:usergroup="${xsd.soap.usergroup.usergroup}"
	xmlns:usergrouplist="${xsd.soap.usergroup.usergrouplist}"
	xmlns:oupathlist="${xsd.soap.ou.oupathlist}"
	xmlns:ou="${xsd.soap.ou.ou}"
	xmlns:oulist="${xsd.soap.ou.oulist}"
	xmlns:ouref="${xsd.soap.ou.ouref}"
	xmlns:stagingfile="${xsd.rest.stagingfile.stagingfile}"
	xmlns:commontypes="${xsd.soap.common.commontypes}"
	xmlns:grants="${xsd.soap.useraccount.grants}"
	xmlns:versionhistory="${xsd.soap.common.versionhistory}"
	xmlns:memberlist="${xsd.soap.common.memberlist}"
	xmlns:container="${xsd.soap.container.container}"
	xmlns:structmap="${xsd.soap.container.structmap}"
	xmlns:containerlist="${xsd.soap.container.containerlist}"
	xmlns:organization="${xsd.metadata.organization}"
	xmlns:file="${xsd.metadata.file}"
	xmlns:publication="${xsd.metadata.publication}"
	xmlns:escidocprofile="${xsd.metadata.escidocprofile}"
	xmlns:escidoc="${xsd.metadata.escidocprofile.types}"
	xmlns:idtypes="${xsd.metadata.escidocprofile.idtypes}"
	xmlns:properties="${xsd.core.properties}"
	xmlns:metadatarecords="${xsd.soap.common.metadatarecords}"
	xmlns:report="${xsd.soap.statistic.report}"
	xmlns:reportparameters="${xsd.soap.statistic.reportparameters}"
	xmlns:reportdefinitionlist="${xsd.soap.statistic.reportdefinitionlist}"
	xmlns:reportdefinition="${xsd.soap.statistic.reportdefinition}"
	xmlns:toc="${xsd.soap.toc.toc}"
	xmlns:table-of-content="${xsd.soap.toc.table-of-content}"
	xmlns:result="${xsd.soap.result.result}"
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}"
	>
	
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template> 
	
	<xsl:template match="*">
		<xsl:copy>
			<xsl:apply-templates select="@*" mode="attribute"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="*" mode="attribute">
		<xsl:copy/>
	</xsl:template>
	
	<xsl:template match="*[namespace-uri() = 'xmlns']" mode="attribute">
		<xsl:copy/>
		<xsl:attribute name="ns">123</xsl:attribute>
	</xsl:template>
	
	<xsl:template match="escidoc:organization">
		<organization:organization>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</organization:organization>
	</xsl:template>

	<xsl:template match="escidoc:organization/escidoc:organization-name">
		<dc:title>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates/>
		</dc:title>
	</xsl:template>

</xsl:stylesheet>
