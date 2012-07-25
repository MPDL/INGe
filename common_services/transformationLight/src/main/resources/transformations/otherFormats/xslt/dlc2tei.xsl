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
	Transformation from a dlc item xml to a zvdd conform mets xml
	Author: Friederike Kleinfercher (initial creation) 
-->

<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:mods="http://www.loc.gov/mods/v3" 
	xmlns:mets="http://www.loc.gov/METS" 
	xmlns:escidocMetadataRecords="http://www.escidoc.de/schemas/metadatarecords/0.5"
	xmlns:tei="http://www.tei-c.org/ns/1.0" 
	xmlns:dv="http://dfg-viewer.de/"
>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:variable name="teiUrl" select="escidocMetadataRecords:md-record/mods:mods/mods:relatedItem"/>

		<mets:mets>
			<xsl:value-of select="teiUrl"/>
		</mets:mets>	
	
</xsl:stylesheet>