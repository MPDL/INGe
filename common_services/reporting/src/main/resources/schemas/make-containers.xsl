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
	Back transformation from the eSciDoc metadata profile v2 to v1  
	Author: vmakarenko (initial creation) 
	$Author: vmakarenko $ (last changed)
	$Revision: $ 
	$LastChangedDate: $
-->

<xsl:stylesheet version="2.0"
 
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

		xmlns:escidocItemList="${xsd.soap.item.itemlist}"		
  		xmlns:escidocItem="${xsd.soap.item.item}"
  		
        xmlns:escidocMetadataRecords="${xsd.soap.common.mdrecords}"
		xmlns:escidocMetadataProfile="${xsd.metadata.escidocprofile}"
		xmlns:escidoc="${xsd.metadata.escidocprofile.types}"
		  		
		xmlns:publication="${xsd.metadata.publication}"        
		xmlns:eterms="${xsd.metadata.terms}"
		xmlns:organization="${xsd.metadata.organization}"
		xmlns:person="${xsd.metadata.person}"
		xmlns:source="${xsd.metadata.source}"
        xmlns:idtype="${xsd.metadata.escidocprofile.idtypes}"
		xmlns:event="${xsd.metadata.event}"		

        xmlns:file="${xsd.metadata.file}"
        
        xmlns:prop="${xsd.soap.common.prop}"
        
        xmlns="http://www.loc.gov/zing/srw/" 
        xmlns:ns2="http://www.loc.gov/zing/cql/xcql/" 
        xmlns:ns3="http://www.loc.gov/zing/srw/diagnostic/"
        
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:dcterms="http://purl.org/dc/terms/"
        
        xmlns:xlink="http://www.w3.org/1999/xlink"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       
  
	>
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>


<!--	<xsl:template match="/">-->
<!--		<xsl:apply-templates />-->
<!--	</xsl:template>-->

	<xsl:template match="node() | @*">
		<xsl:copy copy-namespaces="no">
			<xsl:apply-templates select="node() | @*"/>
		</xsl:copy>
	</xsl:template>

	
	<xsl:template match="//publication:publication">
		<!-- ONLY FHI SPECIFIC!!! -->
	    <!-- hack: negation is not implement for REST interface -->

			<xsl:element name="{name()}">
				<xsl:copy-of select="@*" />
					<xsl:element name="eterms:authors">
						<xsl:for-each select="eterms:creator">
							<xsl:variable name="au" select="
								string-join((
									string-join((person:person/eterms:family-name, person:person/eterms:given-name), ', '),
									organization:organization/dc:title
								), '; ')
							"/>
							<xsl:value-of select="
								if (not(empty($au))) 
								then concat($au, if (position()!=last()) then '; ' else '')  
								else ''
							"/>
						</xsl:for-each>
					</xsl:element>
					<xsl:element name="eterms:source-titles">
						<xsl:value-of select="string-join((source:source/dc:title[.!='']), '; ')"/>
					</xsl:element>
				<xsl:apply-templates />
			</xsl:element>

	</xsl:template>
	

	

</xsl:stylesheet>
