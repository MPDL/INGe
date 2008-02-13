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
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:il="http://www.escidoc.de/schemas/itemlist/0.2"	
	xmlns:i="http://www.escidoc.de/schemas/item/0.2"
	xmlns:mdrs="http://www.escidoc.de/schemas/metadatarecords/0.2"
	xmlns:mp="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
	xmlns:e="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"	
	>
	<xsl:output method="xml" indent="yes" />
	<xsl:template match="/">
		<item-list>
			<xsl:for-each select="il:item-list/i:item">
				<xsl:sort data-type="text" select="mdrs:md-records/mdrs:md-record/mp:publication/dc:title" order="descending" />
				<xsl:copy-of select="." />
			</xsl:for-each>
		</item-list>
	</xsl:template>
</xsl:stylesheet>
