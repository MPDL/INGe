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
<!-- 
	Transformations from TEI 2 to eSciDoc PubItem 
	See mapping: http://colab.mpdl.mpg.de/mediawiki/PubMan_Func_Spec_Submission/TEI_2_PubItem_Mapping 
	Author: Vlad Makarenko (initial creation) 
	$Author: vmakarenko $ (last changed)
	$Revision: $ 
	$LastChangedDate:  $
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
   xmlns:eidt="${xsd.metadata.escidocprofile}idtypes"
   xmlns:srel="${xsd.soap.common.srel}"
   xmlns:prop="${xsd.core.properties}"
   xmlns:oaipmh="http://www.openarchives.org/OAI/2.0/"
   xmlns:ec="${xsd.soap.item.components}"
   xmlns:file="${xsd.metadata.file}"
   xmlns:pub="${xsd.metadata.publication}"
   xmlns:escidoc="urn:escidoc:functions"
   xmlns:t="http://www.tei-c.org/ns/1.0" 
   xmlns:ce="http://www.elsevier.com"
   xmlns:mml="http://www.w3.org/1998/Math/MathML"   
   xmlns:AuthorDecoder="java:de.mpg.escidoc.services.common.util.creators.AuthorDecoder"
   >

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	
	<xsl:param name="is-item-list" select="true()"/>
	
	
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="$is-item-list">
				<item-list>
					<xsl:call-template name="createComponents"/>
				</item-list>
			</xsl:when>
			<xsl:when test="count(.) = 1">
				<xsl:call-template name="createComponents"/>
			</xsl:when>
			<xsl:when test="count(.) = 0">
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:NoSourceForSingleTarget' ), 'Single item was selected as target, but the source contained no items')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="error(QName('http://www.escidoc.de', 'err:MultipleSourceForSingleTarget' ), 'Single item was selected as target, but the source contained multiple items')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	
<!--	COMOPONENTS-->
	<xsl:template name="createComponents">
	
		<xsl:variable name="pubStmt" select="/t:TEI/t:teiHeader/t:fileDesc/t:publicationStmt"/>
		      <ec:component>
		        <!-- Default values we need to tansform item in itemVO -->
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
		              		<xsl:value-of select="
		              			if (exists($pubStmt/t:date/@when)) 
		              			then $pubStmt/t:date/@when
		              			else if (exists($pubStmt/t:date)) 
		              			then $pubStmt/t:date
		              			else ''
		              		"/>
		              </dcterms:dateCopyrighted>
		              <dc:rights>
		              	<xsl:if test="exists($pubStmt/t:availability)">
		              		<xsl:value-of select="$pubStmt/t:availability"/>
		              	</xsl:if>
		              	<xsl:if test="exists($pubStmt/t:authority)">
		              		<xsl:value-of select="
		              			if (exists($pubStmt/t:availability))
		              			then concat(' (', $pubStmt/t:authority, ')')
		              			else $pubStmt/t:authority
		              		"/>
		              	</xsl:if>
		              </dc:rights>
		              <dcterms:license/>
		            </file:file>
		          </mdr:md-record>
		        </mdr:md-records>
		      </ec:component>
	</xsl:template>				
	

</xsl:stylesheet>