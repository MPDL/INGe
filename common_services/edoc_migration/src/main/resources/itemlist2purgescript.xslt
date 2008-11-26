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
	Transformations from eDoc Item to eSciDoc PubItem 
	Author: Julia Kurt (initial creation) 
	$Author: kurt $ (last changed)
	$Revision: 747 $ 
	$LastChangedDate: 2008-07-21 19:15:26 +0200 (Mo, 21 Jul 2008) $
-->
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:item-list="http://www.escidoc.de/schemas/itemlist/0.7"
	xmlns:item="http://www.escidoc.de/schemas/item/0.7"
	xmlns:eidt="http://escidoc.mpg.de/metadataprofile/schema/0.1/idtypes"
	xmlns:escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/types"
	xmlns:dcmitype="http://purl.org/dc/dcmitype/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:components="http://www.escidoc.de/schemas/components/0.7"
	xmlns:md-profile-escidoc="http://escidoc.mpg.de/metadataprofile/schema/0.1/"
	xmlns:md-records="http://www.escidoc.de/schemas/metadatarecords/0.4"
	xmlns:prop="http://escidoc.de/core/01/properties/"
	xmlns:srel="http://escidoc.de/core/01/structural-relations/"
	xmlns:version="http://escidoc.de/core/01/properties/version/"
	xmlns:release="http://escidoc.de/core/01/properties/release/"
	xmlns:relations="http://www.escidoc.de/schemas/relations/0.3"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:file="http://escidoc.mpg.de/metadataprofile/schema/0.1/file"
	xmlns:container="http://www.escidoc.de/schemas/container/0.7"
	xmlns:container-list="http://www.escidoc.de/schemas/containerlist/0.7"
	xmlns:struct-map="http://www.escidoc.de/schemas/structmap/0.4"
	xmlns:search="http://www.loc.gov/zing/srw/"
	xmlns:search-result="http://www.escidoc.de/schemas/searchresult/0.7">

	<xsl:output method="text" encoding="UTF-8" indent="no"/>

	<xsl:template match="/">
<xsl:for-each select="item-list:item-list/*"><xsl:sort select="@xlink:href"/><xsl:variable name="objid" select="substring-after(@xlink:href, '/ir/item/')"/>./fedora-purge.sh localhost:8082 fedoraAdmin fedoraAdmin <xsl:value-of select="$objid"/> http "Purged_object_<xsl:value-of select="$objid"/>"
</xsl:for-each>
<xsl:for-each select="search:searchRetrieveResponse/search:records/search:record/search:recordData/search-result:search-result-record/*">./fedora-purge.sh localhost:8082 fedoraAdmin fedoraAdmin <xsl:value-of select="@objid"/> http "Purged_object_<xsl:value-of select="@objid"/>"
</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>
