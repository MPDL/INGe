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


 Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="${xsd.metadata.dc}"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"   
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:file="${xsd.metadata.file}"
   xmlns:prop="${xsd.core.properties}"
   xmlns:bmc="http://www.biomedcentral.com/xml/schemas/oai/2.0/"
   >

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>	
	
	<xsl:template match="/">
		      <ec:component objid="escidoc:dummy">	      	
		        <ec:properties>
		        	<prop:visibility>public</prop:visibility>
		        </ec:properties>
		        <ec:content storage="internal-managed"/>
		        <mdr:md-records>
		          <mdr:md-record name="escidoc">
		            <file:file>
		              <dc:title/>
		              <dc:description/>
		              <dc:format/>
		              <dcterms:available/>
		              <dcterms:dateCopyrighted/>		              	
		              <dc:rights>
		             	<xsl:value-of select="oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/bmc:ArticleSet/bmc:Article/bmc:CopyrightInformation"/>
		              </dc:rights>
		              <dcterms:license/>
		            </file:file>
		          </mdr:md-record>
		        </mdr:md-records>
		      </ec:component>
	</xsl:template>	
	
</xsl:stylesheet>	
