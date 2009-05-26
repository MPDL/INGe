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
   xmlns:fn="http://www.w3.org/2005/xpath-functions"
   xmlns:xlink="http://www.w3.org/1999/xlink"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xmlns:dc="http://purl.org/dc/elements/1.1/"
   xmlns:dcterms="http://purl.org/dc/terms/"
   xmlns:mdr="${xsd.soap.common.mdrecords}"
   xmlns:mdp="${xsd.metadata.escidocprofile}"
   xmlns:e="${xsd.metadata.escidocprofile.types}"
   xmlns:ei="${xsd.soap.item.item}"
   xmlns:eidt="${xsd.metadata.escidocprofile.idtypes}"
   xmlns:prop="${xsd.core.properties}"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:file="${xsd.metadata.file}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:escidoc="urn:escidoc:functions"
   xmlns:pm="http://dtd.nlm.nih.gov/2.0/xsd/archivearticle" 
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:variable name="copyrightStatement" select="if (exists(oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:permissions/pm:copyright-statement))
													then oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:permissions/pm:copyright-statement
													else oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:copyright-statement"/>
	<xsl:variable name="copyrightHolder" select="if (exists(oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:permissions/pm:copyright-holder))
													then oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:permissions/pm:copyright-holder
													else oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:copyright-holder"/>
	<xsl:variable name="copyrightYear" select="if (exists(oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:permissions/pm:copyright-year))
													then oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:permissions/pm:copyright-year
													else oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:copyright-year"/>
	<xsl:variable name="license" select="if (exists(oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:permissions/pm:license))
													then oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:permissions/pm:license
													else oaipmh:OAI-PMH/oaipmh:GetRecord/oaipmh:record/oaipmh:metadata/pm:article/pm:front/pm:article-meta/pm:license"/>

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
		              <dcterms:dateCopyrighted>		              	
		              	<xsl:value-of select="$copyrightYear"/>
		              </dcterms:dateCopyrighted>
		              <dc:rights>
		             	<xsl:value-of select="
							if (exists($copyrightStatement) and exists ($copyrightHolder))
							then concat($copyrightStatement, concat(' (', $copyrightHolder, ') '))
							else copyrightStatement"/>
		              </dc:rights>
		              <dcterms:license>
		              	<xsl:value-of select="$license"/>
		              </dcterms:license>
		            </file:file>
		          </mdr:md-record>
		        </mdr:md-records>
		      </ec:component>
	</xsl:template>				
</xsl:stylesheet>