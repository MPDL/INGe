<?xml version="1.0" encoding="UTF-8"?>
<!--  CDDL HEADER START  The contents of this file are subject to the terms of the  Common Development and Distribution License, Version 1.0 only  (the "License"). You may not use this file except in compliance  with the License.  You can obtain a copy of the license at license/ESCIDOC.LICENSE  or http://www.escidoc.org/license.  See the License for the specific language governing permissions  and limitations under the License.  When distributing Covered Code, include this CDDL HEADER in each  file and include the License file at license/ESCIDOC.LICENSE.  If applicable, add the following below this CDDL HEADER, with the  fields enclosed by brackets "[]" replaced with your own identifying  information: Portions Copyright [yyyy] [name of copyright owner]  CDDL HEADER END  Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft  für wissenschaftlich-technische Information mbH and Max-Planck-  Gesellschaft zur Förderung der Wissenschaft e.V.  All rights reserved. Use is subject to license terms. -->
<!--   Transformations from eDoc Item to eSciDoc PubItem   Author: Julia Kurt (initial creation)   $Author: kurt $ (last changed)  $Revision: 747 $   $LastChangedDate: 2008-07-21 19:15:26 +0200 (Mo, 21 Jul 2008) $ -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:srw="http://www.loc.gov/zing/srw/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:eprints="http://purl.org/eprint/terms/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:escidoc="http://purl.org/escidoc/metadata/terms/0.1/">
	<xsl:output method="xml" encoding="UTF-8"/>
	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="*"/>
			<xsl:for-each select="child::text()">
				<xsl:value-of select="normalize-space(.)"/>
			</xsl:for-each>
		</xsl:copy>
		<xsl:text></xsl:text>
	</xsl:template>
</xsl:stylesheet>