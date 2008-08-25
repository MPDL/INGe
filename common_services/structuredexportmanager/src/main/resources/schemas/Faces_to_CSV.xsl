<?xml version="1.0" encoding="UTF-8"?>
	<!--
		CDDL HEADER START The contents of this file are subject to the terms
		of the Common Development and Distribution License, Version 1.0 only
		(the "License"). You may not use this file except in compliance with
		the License. You can obtain a copy of the license at
		license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the
		License for the specific language governing permissions and
		limitations under the License. When distributing Covered Code, include
		this CDDL HEADER in each file and include the License file at
		license/ESCIDOC.LICENSE. If applicable, add the following below this
		CDDL HEADER, with the fields enclosed by brackets "[]" replaced with
		your own identifying information: Portions Copyright [yyyy] [name of
		copyright owner] CDDL HEADER END Copyright 2006-2007
		Fachinformationszentrum Karlsruhe Gesellschaft für
		wissenschaftlich-technische Information mbH and Max-Planck-
		Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved.
		Use is subject to license terms.
	-->
	<!--
		Transformations from eSciDoc FacesItem to CSV Author: Julia Kurt
		(initial creation) $Revision: 1 $
	-->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/" 
	xmlns:escidocItem="${xsd.soap.item.item}"
	xmlns:escidocComponents="${xsd.soap.item.components}"
	xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}"
	xmlns:prop="${xsd.soap.common.prop}">
	<xsl:output method="text" encoding="UTF-8" indent="yes" />
	<xsl:template match="/">
		<xsl:value-of select="'filename,person ID,age,age group,gender,emotion,picture group'" />
		<xsl:for-each
			select="//escidocItem:item/escidocComponents:components/escidocComponents:component">
			<xsl:variable name="md"
				select="../../escidocMetadataRecords:md-records/escidocMetadataRecords:md-record/face-item" />
			<xsl:text>&#10;</xsl:text>
			<xsl:value-of select="
				fn:replace(
					concat(
						escidocComponents:properties/prop:file-name
						, ',', $md/dc:identifier
						, ',', $md/age
						, ',', $md/age-group
						, ',', $md/gender
						, ',', $md/emotion
						, ',', $md/picture-group
					),
					'[\t\r\n]', 
					'',
					's'
				)" />
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>