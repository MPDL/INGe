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

<xsl:stylesheet version="2.0"

	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 
	xmlns:dc="${xsd.metadata.dc}"
	xmlns:dcterms="${xsd.metadata.dcterms}" 
	xmlns:dcmitype="${xsd.metadata.dcmitype}"

	xmlns:pub="${xsd.metadata.publication}"
	xmlns:eterms="${xsd.metadata.terms}"
	xmlns:source="${xsd.metadata.source}"
	xmlns:event="${xsd.metadata.event}"
	xmlns:person="${xsd.metadata.person}"
	xmlns:organization="${xsd.metadata.organization}"
	>

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />

	<xsl:param name="source-format" select="''"/>
	<xsl:param name="target-format" select="''"/>

	<xsl:template match="/*[$source-format = 'eSciDoc-publication-item']">
		<result>From eSciDoc item</result>
	</xsl:template>

	<xsl:template match="/*[$source-format = 'eSciDoc-publication-item-list']">
		<result>From eSciDoc item list</result>
	</xsl:template>

	<xsl:template match="/*[$source-format = 'emarcxml']">
		<result>From MarcXML</result>
	</xsl:template>

</xsl:stylesheet>